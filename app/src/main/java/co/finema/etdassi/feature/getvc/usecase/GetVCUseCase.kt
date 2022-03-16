package co.finema.etdassi.feature.getvc.usecase

import android.os.Parcelable
import android.util.Log
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.data.VCDocument
import co.finema.etdassi.common.data.VCDocumentRepository
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.helper.replaceDataNewLine
import co.finema.etdassi.common.helper.replaceDataNewLineJson
import co.finema.etdassi.common.repository.Api
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.common.utils.JWTUtils
import co.finema.etdassi.common.utils.SigningData.Companion.toBase64
import co.finema.etdassi.common.utils.buildJson
import co.finema.etdassi.feature.qr_reader.QRTextResultModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetVCUseCase(
    private val api: CallApi,
    private val userHelper: UserHelper,
    private val keyHelper: KeyHelper,
    private val vcDocumentRepository: VCDocumentRepository
): SimpleCoroutineUseCase<QRTextResultModel, GetVCUseCase.VCSavingCount?>() {

    data class JWTResponse(
        @SerializedName("vcs") val jwtList: List<String>?,
        @SerializedName("sender_did") val senderDid: String?,
        @SerializedName("created_at") val createdAt: String?
    )

    data class VCVerifyRequest(
        @SerializedName("message") val jwt: String?
    )

    data class VCVerifyResponse(
        @SerializedName("verification_result") val verificationResult: Boolean,
        @SerializedName("cid") val cid: String,
        @SerializedName("status") val sStatus: String?,
        @SerializedName("issuance_date") val issuanceDate: String?,
        @SerializedName("type") val type: List<String>?,
        @SerializedName("exp") val expirationDate: String?,
        @SerializedName("issuer") val issuer: String?,
        @SerializedName("holder") val holder: String?,
        )

    @Parcelize
    data class VCSavingCount(
        val errorCase: Int = 0,
        val successCase: Int = 0,
        val totalCase: Int = 0,
        val cidDocList: List<CIDDoc>?,
        val senderData: SenderData?
    ): Parcelable {
        @Parcelize
        data class CIDDoc(
            val cid: String?,
            val type: String?
        ): Parcelable

        @Parcelize
        data class SenderData(
            val senderDid: String?,
            val createdAt: String?,
            val successCase: Int = 0,
            val totalCase: Int = 0,
        ): Parcelable
    }


    private fun getHeaderMap(token: String, tokenSigned: String): Map<String, String> {
        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = token
        headerMap["x-signature"] = tokenSigned
        return headerMap
    }

    override suspend fun executes(param: QRTextResultModel): VCSavingCount? {
        var errorCount = 0
        var successCount = 0
        return withContext(Dispatchers.IO) {
            val tokenSigned = replaceDataNewLine(keyHelper.sign(param.token ?:""))
            val header = getHeaderMap(param.token ?:"", tokenSigned)
            val jwtResponse = api.call().getVC(header, param.endpoint).blockingGet()
            val cidDocList = ArrayList<VCSavingCount.CIDDoc>()
            jwtResponse.jwtList?.let { list ->

                list.forEach { jwtString ->
                    val verifyResponse = api.call().verifyVC(VCVerifyRequest(jwtString)).blockingGet()
                    if (verifyResponse.verificationResult) {
                        val jwtDecoded = JWTUtils.decodedJWT(jwtString)

                        val schemaModel = jwtDecoded?.let { it->
                            JWTUtils.jwtConvertToSchemaModel(it)
                        }

                        val credentialSubject = schemaModel?.vc?.credentialSubject?.let {
                            JWTUtils.credentialSubjectToAttributeModel(
                                it
                            )
                        }
                        if (verifyResponse.verificationResult && verifyResponse.sStatus == "active" && verifyResponse.holder == userHelper.getDIDAddress()) {

                            val backupStatus = if (userHelper.getBackupStatus()){

                                val backupIsExist = api.call(replaceDataNewLineJson(keyHelper.sign(userHelper.getDIDAddress()!!))).checkCidIsExist(userHelper.getDIDAddress()!!, verifyResponse.cid).blockingGet()
                                if (backupIsExist.isExists) {
                                    true
                                } else {
                                    val backupBody = Api.BackupRequestBody(didAddress = userHelper.getDIDAddress()!!, jwt = jwtString).buildJson().toBase64()
                                    val backupMessage = Api.RequestMessage(replaceDataNewLineJson(backupBody))
                                    val backupSignature = replaceDataNewLineJson(keyHelper.sign(backupBody))
                                    val backupResponse = api.call(backupSignature).backupVCToWallet(userHelper.getDIDAddress()!!, backupMessage).blockingGet()
                                    backupResponse.result == "success"
                                }
                                
                            } else {
                                false
                            }
                            val vcDocument = VCDocument(
                                cid = verifyResponse.cid,
                                status = verifyResponse.sStatus,
                                issuanceDate = verifyResponse.issuanceDate,
                                expirationDate = verifyResponse.expirationDate,
                                type = verifyResponse.type?.lastOrNull(),
                                issuer = verifyResponse.issuer,
                                holder = verifyResponse.holder,
                                credentialSubject = credentialSubject,
                                jwt = jwtString,
                                backupStatus = backupStatus,
                                tags = null
                            )
                            vcDocumentRepository.insertVCDocument(vcDocument)
                            successCount += 1
                            cidDocList.add(VCSavingCount.CIDDoc(cid = vcDocument.cid, vcDocument.type))

                        } else {
                            errorCount += 1
                        }
                    } else {
                        errorCount += 1
                    }
                }
                VCSavingCount(
                    totalCase = jwtResponse.jwtList.size,
                    successCase = successCount,
                    errorCase = errorCount,
                    cidDocList = cidDocList,
                    senderData = VCSavingCount.SenderData(
                        jwtResponse.senderDid,
                        jwtResponse.createdAt,
                        successCase = successCount,
                        totalCase = jwtResponse.jwtList.size)
                )
            } ?: kotlin.run {
                null
            }


        }

    }
}