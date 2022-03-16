package co.finema.etdassi.feature.mysign.myreject

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "my_reject_table")
@Parcelize
data class MyRejectModel(
    @PrimaryKey (autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "reject_date") val rejectDate: String,
    @ColumnInfo(name = "notification_id") val notificationId: String?,
    @ColumnInfo(name = "issuer") val issuer: String?,
    @ColumnInfo(name = "status") val status: String?,
    @ColumnInfo(name = "reason") val reason: String?
): Parcelable
