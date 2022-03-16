package co.finema.etdassi.common.data

import co.finema.etdassi.feature.myvc.myvctab.detail.VCDetailViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class VCSchemaModel(
    @SerializedName("jti") val id: String?,
    @SerializedName("iss") val issuer: String?,
    @SerializedName("nbf") val nbf: Long?,
    @SerializedName("sub") val holder: String?,
    @SerializedName("vc") val vc: VC?,
    @SerializedName("vp") val vp: VC?
) {
    data class VC(
        @SerializedName("type") val type: List<String>?,
        @SerializedName("credentialSubject") val credentialSubject: JsonObject?,
        @SerializedName("credentialSchema") val credentialSchema: CredentialSchema?,
        @SerializedName("verifiableCredential") val verifiableCredential: List<String>?
    )

    data class CredentialSchema(
        val id: String?,
        val type: String?
    )

    override fun toString(): String {
        return Gson().toJson(this)
    }
}
