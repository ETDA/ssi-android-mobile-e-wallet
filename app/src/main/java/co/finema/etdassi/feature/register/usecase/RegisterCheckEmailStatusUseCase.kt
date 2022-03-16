package co.finema.etdassi.feature.register.usecase

import co.finema.etdassi.common.Constants
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.repository.CallApi
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterCheckEmailStatusUseCase(
    private val callApi: CallApi,
    private val userHelper: UserHelper
): SimpleCoroutineUseCase<RegisterCheckEmailStatusUseCase.Param, Boolean>() {

    data class Param(
        val otpNumber: String
    )

    data class Response(
        @SerializedName("id") val id: String?,
        @SerializedName("register_email") val registerEmail: String?,
    )

    data class Request(
        @SerializedName("otp_number") val otpNumber: String
    )

    data class ConfirmOTPResponse(
        @SerializedName("id") val id: String,
        @SerializedName("result") val result: String
    )

    override suspend fun executes(param: Param): Boolean {

         return withContext(Dispatchers.IO) {
             val response = callApi.call().confirmOTP(userHelper.getUserId() ?:"", Request(param.otpNumber)).blockingGet()
             response.result == Constants.SUCCESS_VALUE
        }

    }
}