package co.finema.etdassi.common.repository

import co.finema.etdassi.BuildConfig
import co.finema.etdassi.common.base.TLSSocketFactory
import co.finema.etdassi.common.helper.UserHelper
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class CallApi {
    fun call(signature : String): Api {
        val apiClientInterceptor = ApiClientInterceptor(signature)
        val tlsFactory = TLSSocketFactory()

        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        val httpClient: OkHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(tlsFactory, tlsFactory.trustManager!!)
            .callTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .addInterceptor(apiClientInterceptor)
            .addInterceptor(logger)
            .build()


//        val url = ShareMethod.getInstance().apiEndPoint
//        val version = "/api/"

//        val urls = "https://etax-api.finema.co/user/token-auth/"
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        return retrofit.create(Api::class.java)
    }

    fun call(): Api {
        println("aaaaa")
        val apiClientInterceptor = ApiClientInterceptor("")
        val tlsFactory = TLSSocketFactory()

        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY


        val httpClient: OkHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(tlsFactory, tlsFactory.trustManager!!)
            .callTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .addInterceptor(apiClientInterceptor)
            .addInterceptor(logger)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        return retrofit.create(Api::class.java)
    }

//    fun responseError(inputStreams: InputStream): ApiResponseError {
//        val inputStream: InputStream = inputStreams
//        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
//        val tokener = object : JSONTokener(bufferedReader.readText()) {}
//        return try {
//            val json = JSONObject(tokener)
//            val gson = Gson()
//            gson.fromJson(json.toString(), ApiResponseError::class.java)
//        }catch (e:Exception){
//            ApiResponseError("Invalid Url","Not Found", JsonObject())
//        }
//    }
}