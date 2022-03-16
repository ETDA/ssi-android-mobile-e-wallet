package co.finema.etdassi.feature.scan_qr.usecase

import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.data.VCDocumentRepository
import co.finema.etdassi.common.data.VPDocument
import co.finema.etdassi.common.data.VPDocumentRepository
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.common.utils.DateConvertUtil.toISOFormat
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.coroutineContext

class CreateVPUseCase(
    private val callApi: CallApi,
    private val keyHelper: KeyHelper,
    private val userHelper: UserHelper,
    private val vpDocumentRepository: VPDocumentRepository,
    private val vcDocumentRepository: VCDocumentRepository
): SimpleCoroutineUseCase<CreateVPUseCase.Param, Boolean>() {

    data class DidDocResponse(
        @SerializedName(value = "VerificationMethod", alternate = ["verificationMethod"]) val verificationMethod: List<VerificationMethod>?,
        val controller: String?,
        val id: String?,
        val recoverer: String?
        )
    data class VerificationMethod(
        val controller: String?,
        val id: String,
        val publicKeyPem: String?,
        val type: String?
        )

    data class Param(
        val vpRequestId: String,
        val cidList: List<String>,
        val verifierDid: String,
        val endpoint: String,
        val groupName: String?,
        val createAt: String?
    )

    data class Request(
        val message: String
    )

    data class Response(
        @SerializedName("id") val submitId: String,
        @SerializedName("requested_vp_id") val requestedVPId: String?,
        @SerializedName("requested_vp") val requestVP: RequestVP?,
        @SerializedName("holder") val holder: String?,
        @SerializedName("jwt") val jwt: String?,
        @SerializedName("tags") val tags: String?,
        @SerializedName("document_count") val documentCount: Int?,
        @SerializedName("verify") val verify: Boolean = false,
        @SerializedName("created_at") val createdAt: String?,
        @SerializedName("updated_at") val updatedAt: String?
    )

    data class RequestVP(
        @SerializedName("id") val id: String?,
        @SerializedName("name") val name: String?,
        @SerializedName("status") val status: String?,
        @SerializedName("schema_count") val schemaCount: Int?,
        @SerializedName("created_at") val createdAt: String?,
        @SerializedName("updated_at") val updatedAt: String?
    )

    override suspend fun executes(param: Param): Boolean {
        return withContext(Dispatchers.IO) {
            userHelper.getDIDAddress()?.let { did ->
                val didDoc = callApi.call().getDidDoc(did = did).blockingGet()
                didDoc?.verificationMethod?.firstOrNull()?.let {

                    val timeStamp = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis/1000
                    val vcInJwtList = vcDocumentRepository.getVCDocByCidList(param.cidList)

                    val jwtAuthHeader = KeyHelper.JWTAuthHeader(
                        kid = it.id,
                        typ = "JWT"
                    )
                    val vp = KeyHelper.JWTVCDoc(
                        verifiableCredential = vcInJwtList
                    )
                    val jwtAuthPayload = KeyHelper.JWTAuthPayload(
                        jti = param.cidList.firstOrNull(),
                        aud = param.verifierDid,
                        iss = did,
                        nbf = timeStamp,
                        vp = vp
                    )
                    val jwt = keyHelper.jwtBuild(jwtAuthHeader, jwtAuthPayload)
                    val response = callApi.call().sendVP(param.endpoint, Request(jwt)).blockingGet()
                    if (response.verify) {
                        val vpDocument = VPDocument(
                            id = response.submitId,
                            name = param.groupName,
                            verifierDid = param.verifierDid,
                            createdAt = param.createAt,
                            sendAt = timeStamp.toISOFormat(),
                            vpIdList = param.cidList,
                            jwt = jwt,
                            source = response
                        )
                        vpDocumentRepository.insertVPDocument(vpDocument)
                        true
                    } else {
                        false
                    }
                } ?: kotlin.run {
                    false
                }
            } ?: kotlin.run {
                false
            }


        }
    }


}