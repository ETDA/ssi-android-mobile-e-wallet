package co.finema.etdassi.feature.userprofile.usecase

import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.helper.replaceDataNewLineJson
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.feature.register.usecase.RegisterUserModel
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserInformationUseCase(
    private val callApi: CallApi,
    private val userHelper: UserHelper,
): SimpleCoroutineUseCase<UserInformationUseCase.Param, Boolean>() {

    data class Param(
            val did: String,
            val keyHelper: KeyHelper
                    )

    data class Response(
        @SerializedName("did_address") val didAddress: String? = null,
        @SerializedName("id_card_no") var idCardNo: String? = null,
        @SerializedName("first_name") var firstName: String? = null,
        @SerializedName("last_name") var lastName: String? = null,
        @SerializedName("device") var device: RegisterUserModel.DeviceModel? = null,
        @SerializedName("register_date") var registerDate: String? = null,
        @SerializedName("email") var email: String? = null
    ) {
        val fullName = "${firstName ?:""} ${lastName ?:""}"
    }

    override suspend fun executes(param: Param): Boolean {
        return withContext(Dispatchers.IO) {
            val didSign = param.keyHelper.sign(param.did)
            val response = callApi.call(replaceDataNewLineJson(didSign)).getUserInformation(param.did).blockingGet()
            val oldInfo = userHelper.getUserInformation()
            val userInfoBundle = oldInfo?.copy(
                didAddress = response.didAddress ?: oldInfo.didAddress,
                idCardNo = response.idCardNo ?: oldInfo.idCardNo,
                firstName = response.firstName ?: oldInfo.firstName,
                lastName = response.lastName ?: oldInfo.lastName,
                device = response.device,
                registerDate = response.registerDate ?: oldInfo.registerDate,
                email = response.email ?: oldInfo.email
            ) ?: response
            userHelper.setUserInformation(userInfoBundle)
            true
        }
    }

}