package co.finema.etdassi.common.utils

import android.util.Base64
import android.util.Log
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.replaceDataNewLine
import co.finema.etdassi.common.helper.replaceDataNewLineJson
import co.finema.etdassi.common.repository.Api
import co.finema.etdassi.feature.register.usecase.RegisterDIDUseCase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.nio.charset.StandardCharsets

interface BuildJson
fun BuildJson.buildJson(): String {
    val gson = GsonBuilder().disableHtmlEscaping().create()
    val json = gson.toJson(this)
    var disJson = json.replace("\\n","")
    disJson= disJson.replace("-----BEGIN PUBLIC KEY-----","-----BEGIN PUBLIC KEY-----\\n")
    disJson = disJson.replace("-----END PUBLIC KEY-----","\\n-----END PUBLIC KEY-----")
    Log.e("BuildJson",disJson)
    return disJson
}

data class SigningData(
    val message: Api.RequestMessage,
    val signature: String
){
    companion object{
        fun build(key: KeyHelper, didDataHeader: BuildJson): SigningData {
            return build(key, didDataHeader.buildJson())
        }

        fun build(key: KeyHelper, data: String): SigningData {
            val message = Base64.encodeToString(data.toByteArray(StandardCharsets.UTF_8),
                Base64.DEFAULT)
            val messages = replaceDataNewLine(message)

            val dataSign = key.sign(messages)

            return SigningData(
                signature = replaceDataNewLineJson(dataSign),
                message = Api.RequestMessage(replaceDataNewLineJson(messages))
            )
        }

        fun String.toBase64(): String {
            val message = Base64.encodeToString(this.toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT)
            return replaceDataNewLine(message)
        }
    }
}