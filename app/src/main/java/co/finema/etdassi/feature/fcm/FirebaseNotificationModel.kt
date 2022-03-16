package co.finema.etdassi.feature.fcm

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class FirebaseNotificationModel(
    @SerializedName("iss") val issuer: String?,
    @SerializedName("nonce") val nonce: String?,
    @SerializedName("sub") val sub: String?,
    @SerializedName("vc") val vc: VC?
) {

    data class VC(
        @SerializedName("type") val type: List<String>?,
        @SerializedName("credentialSubject") val credentialSubject: JsonObject?,
        @SerializedName("credentialSchema") val credentialSchema: JsonObject?
    )
}
