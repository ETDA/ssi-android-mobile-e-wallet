package co.finema.etdassi.feature.fcm.usecase

import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.feature.register.usecase.UpdateFirebaseTokenUseCase
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateFirebaseTokenFCMUseCase(
    private val callApi: CallApi,
    private val userHelper: UserHelper
): SimpleCoroutineUseCase<String, Boolean>() {

    override suspend fun executes(param: String): Boolean {
        return withContext(Dispatchers.IO) {
            userHelper.getUUID()?.let { uuid ->
                val request = UpdateFirebaseTokenUseCase.Request(
                    uuid = uuid,
                    firebaseToken = param
                )
                val response = callApi.call().updateDeviceToken(userId = uuid, request).blockingGet()
                response.result == "success"
            } ?: kotlin.run {
                false
            }
        }
    }

}