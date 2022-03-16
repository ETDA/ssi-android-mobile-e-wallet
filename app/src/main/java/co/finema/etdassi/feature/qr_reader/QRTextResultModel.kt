package co.finema.etdassi.feature.qr_reader

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QRTextResultModel(
    val id: String?,
    val token: String?,
    val operation: String,
    val endpoint: String,
    @SerializedName("access_key") val accessKey: String?
): Parcelable
