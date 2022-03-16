package co.finema.etdassi.feature.register.usecase

import co.finema.etdassi.common.Constants
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.enum.Operations
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.repository.CallApi
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RequestSendEmailUseCase(
    private val callApi: CallApi,
    private val userHelper: UserHelper
): SimpleCoroutineUseCase<RequestSendEmailUseCase.Param, Boolean>() {

    data class Param(
        val type: String
    )

    data class Request(
        @SerializedName("operation") val operation: String,
        @SerializedName("id") val id: String
    )

    data class Response(
        @SerializedName("result") val result: String
    )

    override suspend fun executes(param: Param): Boolean {
        val id = when(param.type) {
            Operations.RECOVERY.action -> {
                userHelper.getUserId()
//                userHelper.getDIDAddress()
            }

            Operations.REGISTER.action -> {
                userHelper.getUserId()
            }

            else -> {
                ""
            }
        }

        val requestBody = Request(param.type, id!!)

        val response = withContext(Dispatchers.IO) {
            callApi.call().requestToSendEmail(id).blockingGet()
        }

        return response.result == Constants.SUCCESS_VALUE
    }
}