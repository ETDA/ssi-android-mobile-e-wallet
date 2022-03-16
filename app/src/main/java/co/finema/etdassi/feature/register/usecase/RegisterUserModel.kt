package co.finema.etdassi.feature.register.usecase

import com.google.gson.annotations.SerializedName

class RegisterUserModel {
    data class RequestRegisterUser(
        @SerializedName("id_card_no") val idCard: String,
        @SerializedName("first_name") val firstName: String,
        @SerializedName("last_name") val lastName: String,
        @SerializedName("date_of_birth") val dateOfBirth: String,
        @SerializedName("laser_id") val laserId: String,
        @SerializedName("email") val email: String,
        @SerializedName("device") val device: DeviceModel
                                  ) {
    }

    data class DeviceModel(
        @SerializedName("name") val name: String,
        @SerializedName("os") val os: String = "Android",
        @SerializedName("os_version") val osVersion: String,
        @SerializedName("model") val model: String,
        @SerializedName("uuid") val uuid: String
                          )

    data class ResponseRegisterUser(
        @SerializedName("result") val result: String?,
        @SerializedName("did_address") val didAddress: String?,
        @SerializedName("user_id") val userId: String?
                                   )
}

