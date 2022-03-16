package co.finema.etdassi.feature.register.usecase

import co.finema.etdassi.common.Constants
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.repository.CallApi
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateFirebaseTokenUseCase(
    private val callApi: CallApi
): SimpleCoroutineUseCase<UpdateFirebaseTokenUseCase.Param, Boolean>() {

    data class Param(
        val firebaseToken: String,
        val didAddress: String,
        val uuid: String
    )

    data class Response(
        val result: String
    )

    data class Request(
        @SerializedName("uuid") val uuid: String,
        @SerializedName("token") val firebaseToken: String
        )

    override suspend fun executes(param: Param): Boolean {
        val request = Request(
            uuid = param.uuid,
            firebaseToken = param.firebaseToken
        )
        val response = withContext(Dispatchers.IO) {
            callApi.call().updateDeviceToken(param.uuid, request).blockingGet()
        }

        return response.result == Constants.SUCCESS_VALUE
    }
}