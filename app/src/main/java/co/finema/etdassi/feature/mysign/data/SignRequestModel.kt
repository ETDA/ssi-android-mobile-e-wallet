package co.finema.etdassi.feature.mysign.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "sign_request")
@Parcelize
data class SignRequestModel(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "vc_name") val typeName: String? = null,
    @ColumnInfo(name = "date_request")val dateRequest: String? = null,
    @ColumnInfo(name = "read_status") var isRead: Boolean = false
): Parcelable