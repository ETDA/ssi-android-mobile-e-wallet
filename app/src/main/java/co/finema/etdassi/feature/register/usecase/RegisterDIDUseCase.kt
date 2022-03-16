package co.finema.etdassi.feature.register.usecase

import android.util.Base64
import android.util.Log
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.replaceDataNewLine
import co.finema.etdassi.common.helper.replaceDataNewLineJson
import co.finema.etdassi.common.repository.Api
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.common.utils.BuildJson
import co.finema.etdassi.common.utils.buildJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

class RegisterDIDUseCase(
    private val api: CallApi
) : SimpleCoroutineUseCase<KeyHelper,Api.ResponseRegisterDID>() {
    override suspend fun executes(param: KeyHelper): Api.ResponseRegisterDID {
        return withContext(Dispatchers.IO) {
            val requestBody = DidDataHeader(
                public_key = param.pem
            )

            Log.d("RegisterDIDUseCase ", requestBody.toString())

            val didData = DidData.build(
                param,
                requestBody
            )

            val message = replaceDataNewLineJson(didData.message)
            val signature = replaceDataNewLineJson(didData.signature)


            api.call(signature).registerDID(Api.RequestMessage(message)).blockingGet()
        }
    }

    data class DidDataHeader(
        @SerializedName("operation") val operation: String? = "DID_REGISTER",
        @SerializedName("public_key") val public_key: String? = null,
        @SerializedName("key_type") val key_type: String = "EcdsaSecp256r1VerificationKey2019"
    ): BuildJson

    data class DidData(
        val message: String? = null,
        val signature: String? = null
    ){
        companion object{
            fun build(key: KeyHelper, didDataHeader: DidDataHeader): DidData {
                Log.d("DidData", Gson().toJson(didDataHeader))
                return build(key, didDataHeader.buildJson())
            }

            fun build(key: KeyHelper, data: String): DidData {
                val message = Base64.encodeToString(data.toByteArray(StandardCharsets.UTF_8),
                    Base64.DEFAULT)
                val messages = replaceDataNewLine(message)

                val dataSign = key.sign(messages)

                return DidData(
                    signature = dataSign,
                    message = messages
                )
            }
        }
    }
}



