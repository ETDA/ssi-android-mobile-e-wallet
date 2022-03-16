package co.finema.etdassi.common.repository

import android.util.Log
import co.finema.etdassi.common.helper.UserHelper
import okhttp3.Interceptor
import okhttp3.Response

class ApiClientInterceptor(
    private var signature : String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

//        if (userHelper.hasToken()) {
//            builder.addHeader("Authorization", "Token ${userHelper.getToken()}")
//        }

        if(signature.isNotBlank()){
            builder.addHeader("x-signature", signature)
        }
        builder.addHeader("Content-Type", "application/json")

        val request = builder.build()
        Log.d("OkHttp.Api", request.headers.toString())
        Log.d("OkHttp.Api", request.toString())

        val response = chain.proceed(request)

        return response
    }

}