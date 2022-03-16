package co.finema.etdassi.feature.mysign.vcsign

import android.os.Parcelable
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.data.VCDocumentRepository
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.helper.replaceDataNewLineJson
import co.finema.etdassi.common.repository.Api
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.common.utils.BuildJson
import co.finema.etdassi.common.utils.SigningData.Companion.toBase64
import co.finema.etdassi.common.utils.buildJson
import co.finema.etdassi.feature.mysign.data.VCSigned
import co.finema.etdassi.feature.mysign.data.VCSignedRepository
import co.finema.etdassi.feature.notification.NotificationStatus
import co.finema.etdassi.feature.notification.data.NotificationRepository
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VCSignedUseCase(
    private val callApi: CallApi,
    private val userHelper: UserHelper,
    private val keyHelper: KeyHelper,
    private val vcSignedRepository : VCSignedRepository,
    private val notificationRepository: NotificationRepository,
    private val vcDocumentRepository: VCDocumentRepository
):SimpleCoroutineUseCase<VCSignedUseCase.Param,VCSigned>(){

    @Parcelize
    data class Param(
        val vcSignedId : String
    ): Parcelable

    data class RevokeVCRequest(
        @SerializedName("operation") val operation: String = "VC_UPDATE_STATUS",
        @SerializedName("did_address") val didAddress: String?,
        @SerializedName("nonce") val nonce: String?,
        @SerializedName("status") val status: String = "revoke",
        @SerializedName("cid") val cid: String?
    ): BuildJson

    data class RevokeVCResponse(
        @SerializedName("cid") val cid: String,
        @SerializedName("did_address") val didAddress: String,
        @SerializedName("vc_hash") val vcHash: String,
        @SerializedName("status") val status: String,
        @SerializedName("tags") val tags: List<String>?,
        @SerializedName("activated_at") val activatedAt: String,
        @SerializedName("revoked_at") val revokedAt: String?
    ): BuildJson

    override suspend fun executes(param: Param): VCSigned {
        return withContext(Dispatchers.IO){
            val didAddress = userHelper.getDIDAddress() ?: ""
            val jwtNonce = callApi.call().getNonce(didAddress).blockingGet()

            val revokeVCRequest = RevokeVCRequest(
                didAddress = didAddress,
                nonce = jwtNonce.nonce,
                cid = param.vcSignedId
            )
            val revokeVCRequestJson = revokeVCRequest.buildJson().toBase64()
            val signMessage = Api.RequestMessage(replaceDataNewLineJson(revokeVCRequestJson))
            val signSignature = replaceDataNewLineJson(keyHelper.sign(revokeVCRequestJson))
            val signResponse = callApi.call(signSignature).revokeSigned(param.vcSignedId,signMessage).blockingGet()

            if(signResponse.status == "revoke"){
                val vcSignRepository = vcSignedRepository.getVCSignedById(param.vcSignedId)
//                Log.e("JWT","${vcSignRepository.jwt}")
//                val decodeJWT = JWTUtils.decodedJWT(vcSignRepository.jwt!!)
//
//                val schemaModel = decodeJWT.let{
//                    JWTUtils.jwtConvertToSchemaModel(it)
//                }
//                val currentTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis/1000
//                val header = KeyHelper.JWTAuthHeader(kid = param.vcSignedId)
//                val calm = KeyHelper.JWTAuthPayload(
//                    jti = schemaModel?.id,
//                    iss = schemaModel?.issuer,
//                    vc =
//                )
//                val jwt = keyHelper.jwtBuild(header, calm)

                vcSignedRepository.updateVCSignStatus(param.vcSignedId,signResponse.status)
                vcSignRepository.notificationId?.let {
                    notificationRepository.updateNotificationVCStatus(
                        NotificationStatus.REVOKE.name,
                        vcSignRepository.notificationId)
                }
                vcDocumentRepository.getVCDocument(vcSignRepository.id)?.let {
                    vcDocumentRepository.updateVCDocumentStatusByCid(it.cid, NotificationStatus.REVOKE.name)
                }

            }
            vcDocumentRepository

            vcSignedRepository.getVCSignedById(param.vcSignedId)
        }
    }
}
