package co.finema.etdassi.feature.register.usecase

import android.bluetooth.BluetoothAdapter
import android.os.Build
import co.finema.etdassi.common.Constants
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.common.utils.BuildJson
import co.finema.etdassi.common.utils.SigningData
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterUpdateUserUseCase(
    private val callApi: CallApi,
    private val keyHelper: KeyHelper,
    private val userHelper: UserHelper
): SimpleCoroutineUseCase<RegisterUpdateUserUseCase.Param, Boolean>() {

    data class Param(
        val userId: String,
        val did: String
    )

    data class Response(
        @SerializedName("did_address") val didAddress: String,
        @SerializedName("user_id") val userId: String
    )

    data class Request(
        @SerializedName("id") val userId: String,
        @SerializedName("did_address") val did: String,
        @SerializedName("device") val device: Device?
    ): BuildJson

    data class Device(
        @SerializedName("name") val name: String,
        @SerializedName("model") val model: String,
        @SerializedName("os") val os: String,
        @SerializedName("os_version") val osVersion: String,
        @SerializedName("uuid") val uuid: String
    )

    override suspend fun executes(param: Param): Boolean {
        return withContext(Dispatchers.IO)  {
            val myDevice = BluetoothAdapter.getDefaultAdapter()
            val request = Request(
                userId = param.userId,
                did = param.did,
                device = if (userHelper.getStatusMustAddDevice()) {
                    Device(
                        name = myDevice.name,
                        model = Build.MODEL,
                        os = "Android",
                        osVersion = Build.VERSION.RELEASE,
                        uuid = userHelper.getUUID()!!
                    )
                } else {
                    null
                }
            )
            userHelper.clearStatusMustAddDevice()
            val signedRequest = SigningData.build(keyHelper, request)
            val response = withContext(Dispatchers.IO) {
                callApi.call(signedRequest.signature).updateUserDID(param.userId, signedRequest.message).blockingGet()
            }
            userHelper.setUserId(response.userId)
            userHelper.saveDIDAddress(response.didAddress)
            response != null
        }

    }
}