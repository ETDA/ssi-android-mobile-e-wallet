package co.finema.etdassi.common.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VCAttributeModel(
    val title: String? = null,
    val value: String? = null,
    val items: List<VCAttributeModel>? = null,
    val type: VCAttributeType,
    val level: Int
): Parcelable
