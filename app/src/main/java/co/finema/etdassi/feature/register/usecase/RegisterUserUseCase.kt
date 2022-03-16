package co.finema.etdassi.feature.register.usecase

import android.bluetooth.BluetoothAdapter
import android.os.Build
import android.os.Parcelable
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.repository.Api
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.common.utils.DateConvertUtil.toISOFormat
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class RegisterUserUseCase(
    private val api: CallApi,
    private val userHelper: UserHelper
                         ) : SimpleCoroutineUseCase<RegisterUserUseCase.Param, RegisterUserModel.ResponseRegisterUser>() {
    override suspend fun executes(param: Param): RegisterUserModel.ResponseRegisterUser {
        return withContext(Dispatchers.IO) {
            val uuid = userHelper.getUUID() ?: kotlin.run {
                val uuidCreated = UUID.randomUUID().toString()
                userHelper.saveUUID(uuidCreated)
                uuidCreated
            }

            val name = try {
                BluetoothAdapter.getDefaultAdapter().name
            } catch (e: Exception) {
                "Android"
            }

            val deviceModel = RegisterUserModel.DeviceModel(
                name = name,
                osVersion = Build.VERSION.RELEASE,
                model = Build.MODEL,
                uuid = uuid
                                                           )
            val requestModel = RegisterUserModel.RequestRegisterUser(
                idCard = param.idCardNo,
                firstName = param.firstName,
                lastName = param.lastName,
                dateOfBirth = param.dateOfBirth.toLong().toISOFormat(),
                laserId = param.laserId,
                email = param.email,
                device = deviceModel
                                                                    )

            api.call().registerUser(requestModel).blockingGet()
        }
    }

    @Parcelize
    data class Param(
        val url: String,
        val firstName: String,
        val lastName: String,
        val idCardNo: String,
        val laserId: String,
        val dateOfBirth: String,
        val email: String
    ): Parcelable
}