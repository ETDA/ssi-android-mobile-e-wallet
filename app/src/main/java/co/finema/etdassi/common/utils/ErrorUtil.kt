package co.finema.etdassi.common.utils

import android.util.Log
import co.finema.etdassi.common.repository.CallApi
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.HttpException
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

fun Throwable.handleError(): String {
    return when (this) {
        is HttpException -> {
            this.response()?.errorBody()?.source().let {
                val data = responseError(it!!.inputStream())
                val i = object : IErrorResponse {
                    override var errorCode: String
                        get() = data.code
                        set(value) {}
                    override var message: String
                        get() = data.message
                        set(value) {}
                    }
                    Log.e("ChangeDevice Error", "${i.errorCode} ${i.message}")
                    i.message
                }
        }

        else -> {
            "Unknown error"
        }
    }
}

interface IErrorResponse {
    var errorCode: String
    var message: String
}

fun responseError(inputStreams: InputStream): ApiResponseError {
    val inputStream: InputStream = inputStreams
    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
    val tokener = object : JSONTokener(bufferedReader.readText()) {}
    return try {
        val json = JSONObject(tokener)
        val gson = Gson()
        gson.fromJson(json.toString(), ApiResponseError::class.java)
    }catch (e:Exception){
        ApiResponseError("Invalid Url","Not Found", JsonObject())
    }
}

data class ApiResponseError(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("fields") val fields: JsonObject
                           )