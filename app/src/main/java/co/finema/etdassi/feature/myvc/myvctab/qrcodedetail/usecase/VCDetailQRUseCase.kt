package co.finema.etdassi.feature.myvc.myvctab.qrcodedetail.usecase

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
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class VCDetailQRUseCase(
    private val vcDocumentRepository: VCDocumentRepository,
    private val keyHelper: KeyHelper,
    private val userHelper: UserHelper,
    private val callApi: CallApi
): SimpleCoroutineUseCase<String, String>() {


    data class BodyRequest(
        @SerializedName("did_address") val didAddress: String?,
        @SerializedName("jwt") val jwt: String?,
        @SerializedName("operation") val operation: String? = "REQUEST_VERIFY"
        ): BuildJson

    data class Response(
        @SerializedName("operation") val operation: String,
        @SerializedName("endpoint") val endpoint: String
    ): BuildJson



    override suspend fun executes(param: String): String {
        return withContext(Dispatchers.IO) {
            val vcDoc = vcDocumentRepository.getVCDocument(param)
            val didDoc = callApi.call().getDidDoc(userHelper.getDIDAddress() ?:"").blockingGet()
            didDoc.verificationMethod?.firstOrNull()?.let {
                val jwtAuthHeader = KeyHelper.JWTAuthHeader(
                    kid = it.id
                )
                val vp = KeyHelper.JWTVCDoc(verifiableCredential = listOf(vcDoc!!.jwt) , typeList = listOf("VerifiablePresentation"))
                val timeStamp = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis/1000
                val jwtAuthPayload = KeyHelper.JWTAuthPayload(
                    iss = userHelper.getDIDAddress(),
                    nbf = timeStamp,
                    aud = userHelper.getDIDAddress(),
                    jti = param,
                    vp = vp
                )
                val jwt = keyHelper.jwtBuild(jwtAuthHeader, jwtAuthPayload)
                val bodyRequest = BodyRequest(
                    didAddress = userHelper.getDIDAddress(),
                    jwt = jwt
                )
                val message = replaceDataNewLineJson(bodyRequest.buildJson().toBase64())
                val signature = replaceDataNewLineJson(keyHelper.sign(message))
                val response = callApi.call(signature).createQRForVerify(Api.RequestMessage(message)).blockingGet()
                response.buildJson()
            } ?: kotlin.run {
                ""
            }

        }
    }

}