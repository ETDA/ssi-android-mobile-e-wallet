package co.finema.etdassi.feature.notification.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "notification")
data class NotificationModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val vcType: String? = null,
    @ColumnInfo(name = "vc_status") val status: String? = null,
    @ColumnInfo(name = "date") val date: String? = null,
    @ColumnInfo(name = "dateKey") val dateKey: String? = null,
    @ColumnInfo(name = "read_status") var isRead: Boolean = false,
    @ColumnInfo(name = "source") val source: String? = null,
    @ColumnInfo(name = "issuer") val issuer: String,
    @ColumnInfo(name = "credentialSubject") val credentialSubject: String?,
    @ColumnInfo(name = "approve_endpoint") val approveEndpoint: String?,
    @ColumnInfo(name = "reject_endpoint") val rejectEndpoint: String?,
    @ColumnInfo(name = "creator") val creator: String?
)