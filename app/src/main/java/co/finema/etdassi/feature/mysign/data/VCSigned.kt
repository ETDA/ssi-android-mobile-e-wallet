package co.finema.etdassi.feature.mysign.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull

@Entity(tableName = "vc_signed")
@Parcelize
data class VCSigned(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "sign_date") val signDate: String,
    @ColumnInfo(name = "read_status") var isRead: Boolean = false,
    @ColumnInfo(name = "notification_id") val notificationId: String?,
    @ColumnInfo(name = "issuer") val issuer: String?,
    @ColumnInfo(name = "status") val status: String?,
    @ColumnInfo(name = "vc_hash") val vcHash: String?,
    @ColumnInfo(name = "tags") val tags: String?,
    @ColumnInfo(name = "revoked_at") val revokedAt: String?,
    @ColumnInfo(name = "jwt") val jwt: String?,
    @ColumnInfo(name = "backup_status") val backupStatus: Boolean? = false
): Parcelable
