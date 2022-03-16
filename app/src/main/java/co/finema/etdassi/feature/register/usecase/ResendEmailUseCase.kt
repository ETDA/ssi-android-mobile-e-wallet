package co.finema.etdassi.feature.register.usecase

import co.finema.etdassi.common.Constants
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.enum.Operations
import co.finema.etdassi.common.repository.CallApi
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResendEmailUseCase(
    private val callApi: CallApi
): SimpleCoroutineUseCase<ResendEmailUseCase.Param, Boolean>() {

    data class Param(
        val id: String
    )

    data class Response(
        @SerializedName("result") val result: String
    )

    override suspend fun executes(param: Param): Boolean {
        return withContext(Dispatchers.IO) {
            val response = callApi.call().resendEmailVerify(param.id).blockingGet()
            response.result == Constants.SUCCESS_VALUE
        }
    }
}