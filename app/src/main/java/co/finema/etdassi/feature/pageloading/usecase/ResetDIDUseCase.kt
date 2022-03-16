package co.finema.etdassi.feature.pageloading.usecase

import android.util.Base64
import co.finema.etdassi.common.Constants
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.enum.Operations
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.replaceDataNewLine
import co.finema.etdassi.common.helper.replaceDataNewLineJson
import co.finema.etdassi.common.repository.Api
import co.finema.etdassi.common.repository.CallApi
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import java.security.PublicKey
import java.util.*

class ResetDIDUseCase(
    private val callApi: CallApi,
    private val keyHelper: KeyHelper
): SimpleCoroutineUseCase<String, Boolean>() {

    data class Request(
        @SerializedName("operation") val operation: String,
        @SerializedName("did_address") val didAddress: String,
        @SerializedName("request_did") val requestDid: String,
        @SerializedName("new_key") val newKey: KeySchema,
        @SerializedName("nonce") val nonce: String
    )

    data class KeySchema(
        @SerializedName("public_key") val publicKey: String,
        @SerializedName("signature") val signature: String,
        @SerializedName("controller") val controller: String
    )

    data class Response(
        @SerializedName("result") val result: String
    )


    override suspend fun executes(param: String): Boolean {
        return withContext(Dispatchers.IO) {
            val nonce = callApi.call().getNonce(param).blockingGet().nonce
            val keySchema = KeySchema("","","") //TODO WAITING FOR SPEC UPDATE 23/7/21
            val requestBody = Request(
                operation = Operations.DID_KEY_RESET.action,
                didAddress = param,
                requestDid = param,
                newKey = keySchema,
                nonce = nonce
            )

            val base64 = Base64.encodeToString(requestBody.toString().toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT)
            val message = replaceDataNewLineJson(replaceDataNewLine(base64))
            val xSignature = keyHelper.sign(replaceDataNewLine(base64))
            val response = callApi.call(xSignature).resetDIDRecovery(Api.RequestMessage(message)).blockingGet()
            response.result == Constants.SUCCESS_VALUE
        }

    }


}