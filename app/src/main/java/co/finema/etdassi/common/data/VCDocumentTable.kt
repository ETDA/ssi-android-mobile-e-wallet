package co.finema.etdassi.common.data


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Entity(tableName = "vc_document")
@Parcelize
data class VCDocumentTable(
    @PrimaryKey @ColumnInfo(name = "cid") val cid: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "issuance_date") val issuanceDate: String?,
    @ColumnInfo(name = "expiration_date") val expirationDate: String?,
    @ColumnInfo(name = "type") val type: String?,
    @ColumnInfo(name = "issuer") val issuer: String?,
    @ColumnInfo(name = "holder") val holder: String?,
    @ColumnInfo(name = "credentialSubject") val credentialSubject: String,
    @ColumnInfo(name = "jwt") val jwt: String,
    @ColumnInfo(name = "backup_status") val backupStatus: Boolean,
    @ColumnInfo(name = "tags") val tags: String?
): Parcelable
