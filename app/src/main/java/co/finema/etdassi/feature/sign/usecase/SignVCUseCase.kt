package co.finema.etdassi.feature.sign.usecase

import android.util.Base64
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.enum.VCStatus
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.helper.replaceDataNewLine
import co.finema.etdassi.common.helper.replaceDataNewLineJson
import co.finema.etdassi.common.repository.Api
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.common.utils.BuildJson
import co.finema.etdassi.common.utils.buildJson
import co.finema.etdassi.feature.mysign.data.VCSigned
import co.finema.etdassi.feature.mysign.data.VCSignedRepository
import co.finema.etdassi.feature.notification.NotificationStatus
import co.finema.etdassi.feature.notification.data.NotificationRepository
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import java.util.TimeZone

class SignVCUseCase(
    private val callApi: CallApi,
    private val userHelper: UserHelper,
    private val keyHelper: KeyHelper,
    private val vcSignedRepository: VCSignedRepository,
    private val notificationRepository: NotificationRepository
): SimpleCoroutineUseCase<SignVCUseCase.Param, String>() {

    data class Param(
        val source: NotificationRepository.NotificationOriginalSource,
        val vcType: String,
        val notificationId: String
        )

    data class RegisterVCRequest(
        @SerializedName("operation") val operation: String = "VC_REGISTER",
        @SerializedName("did_address") val didAddress: String?,
        @SerializedName("nonce") val nonce: String?
    ): BuildJson

    data class RegisterVCResponse(
        @SerializedName("cid") val cid: String?,
        @SerializedName("did_address") val didAddress: String?,
    )

    data class SignVCRequest(
        @SerializedName("operation") val operation: String = "VC_ADD_STATUS",
        @SerializedName("did_address") val didAddress: String?,
        @SerializedName("nonce") val nonce: String?,
        @SerializedName("status") val status: String = "active",
        @SerializedName("cid") val cid: String?,
        @SerializedName("vc_hash") val vcHash: String?
    ): BuildJson

    data class SignVCResponse(
        @SerializedName("cid") val cid: String,
        @SerializedName("did_address") val didAddress: String,
        @SerializedName("vc_hash") val vcHash: String,
        @SerializedName("status") val status: String,
        @SerializedName("tags") val tags: List<String>?,
        @SerializedName("activated_at") val activatedAt: String,
        @SerializedName("revoked_at") val revokedAt: String?
    ): BuildJson

    data class ApproveRequest(
        @SerializedName("jwt") val jwt: String?
    ): BuildJson

    data class ApproveResponse(
        @SerializedName("status") val status: String
    )


    private fun String.toBase64(): String {
        val message = Base64.encodeToString(this.toByteArray(StandardCharsets.UTF_8),
            Base64.DEFAULT)
        return replaceDataNewLine(message)
    }

    override suspend fun executes(param: Param): String {
        return withContext(Dispatchers.IO) {
            val kidResponse = callApi.call().getDidDoc(userHelper.getDIDAddress() ?:"").blockingGet()  /** ในที่นี้หมายถึง did doc */
            val didAddress = userHelper.getDIDAddress() ?: ""
            kidResponse.verificationMethod?.firstOrNull()?.let { kid->
                val nonce = callApi.call().getNonce(didAddress).blockingGet()
                val registerVCRequest = RegisterVCRequest(
                    operation = "VC_REGISTER",
                    didAddress = didAddress,
                    nonce = nonce.nonce
                )
                val registerVCRequestMessage = registerVCRequest.buildJson().toBase64()
                val rgMessage = Api.RequestMessage(replaceDataNewLineJson(registerVCRequestMessage))
                val rgSignature = replaceDataNewLineJson(keyHelper.sign(registerVCRequestMessage))
                val registerVCRepo = callApi.call(rgSignature).registerVC(rgMessage).blockingGet()

                val header = KeyHelper.JWTAuthHeader(kid = kid.id)
                val currentTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis/1000
                val clam = KeyHelper.JWTAuthPayload(
                    sub = param.source.sub,
                    jti = registerVCRepo.cid,
                    iss = param.source.issuer,
                    nbf = currentTime,
                    vc = param.source.vc
                )
                val jwt = keyHelper.jwtBuild(header, clam)
                val jwtNonce = callApi.call().getNonce(didAddress).blockingGet()
                val signVCRequest = SignVCRequest(
                    didAddress = didAddress,
                    nonce = jwtNonce.nonce,
                    cid = registerVCRepo.cid,
                    vcHash = jwt.hashThis()
                )

                val signVCRequestJson = signVCRequest.buildJson().toBase64()
                val signMessage = Api.RequestMessage(replaceDataNewLineJson(signVCRequestJson))
                val signSignature = replaceDataNewLineJson(keyHelper.sign(signVCRequestJson))
                val signResponse = callApi.call(signSignature).signVCActive(signMessage).blockingGet()
                val approveBase64 = ApproveRequest(jwt = jwt).buildJson().toBase64()
                val approveResponse = callApi.call(replaceDataNewLineJson(keyHelper.sign(approveBase64)))
                    .vcApprove(url = param.source.approveEndpoint, body = Api.RequestMessage(
                        replaceDataNewLineJson(approveBase64))).blockingGet()
                if (approveResponse.status == "Success" || approveResponse.status == "success") {
                    val backupStatus = if (userHelper.getBackupStatus()) {
                        val backupIsExist = callApi.call(replaceDataNewLineJson(keyHelper.sign(didAddress))).checkCidIsExist(didAddress, signResponse.cid).blockingGet()
                        if (backupIsExist.isExists) {
                            true
                        } else {
                            val backupBody = Api.BackupRequestBody(didAddress = didAddress, jwt = jwt).buildJson().toBase64()
                            val backupMessage = Api.RequestMessage(replaceDataNewLineJson(backupBody))
                            val backupSignature = replaceDataNewLineJson(keyHelper.sign(backupBody))
                            val backupResponse = callApi.call(backupSignature).backupVCToWallet(didAddress, backupMessage).blockingGet()
                            backupResponse.result == "success"
                        }
                    } else{
                        false
                    }
                    val vcSigned = VCSigned(
                        id = signResponse.cid,
                        name = param.vcType,
                        signDate = signResponse.activatedAt,
                        notificationId = param.notificationId,
                        issuer = param.source.issuer,
                        status = VCStatus.ACTIVE.name,
                        vcHash = signResponse.vcHash,
                        tags = Gson().toJson(signResponse.tags),
                        revokedAt = signResponse.revokedAt,
                        jwt = jwt,
                        backupStatus = backupStatus
                    )
                    vcSignedRepository.createVCSigned(vcSigned)
                    notificationRepository.updateNotificationVCStatus(NotificationStatus.ACTIVE.name, param.notificationId)
                    "success"

                } else {
                    "ไม่สามารถลงนามเอกสารได้"
                }
            } ?: kotlin.run {
                "หา verificationMethod ไม่พบ"
            }




        }



    }

    private fun String.hashThis() : String{
        val md = MessageDigest.getInstance("SHA-256")
        md.update(this.toByteArray(Charsets.UTF_8))
        val digest = md.digest()
        return String.format("%02x", BigInteger(1, digest))
    }

}