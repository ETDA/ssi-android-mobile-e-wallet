package co.finema.etdassi.common.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VCDocument(
    val cid: String,
    val status: String,
    val issuanceDate: String?,
    val expirationDate: String?,
    val type: String?,
    val issuer: String?,
    val holder: String?,
    val credentialSubject: List<VCAttributeModel>?,
    val jwt: String,
    val backupStatus: Boolean,
    val tags: String?,
): Parcelable