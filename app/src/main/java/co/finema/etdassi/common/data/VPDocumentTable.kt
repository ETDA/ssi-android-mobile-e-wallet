package co.finema.etdassi.common.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "vp_document")
@Parcelize
data class VPDocumentTable(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "group_name") val name: String?,
    @ColumnInfo(name = "verifier_did") val verifierDid: String?,
    @ColumnInfo(name = "created_at") val createdAt: String?,
    @ColumnInfo(name = "send_at") val sendAt: String?,
    @ColumnInfo(name = "vp_id_list") val vpIdList: String?,
    @ColumnInfo(name = "jwt") val jwt: String,
    @ColumnInfo(name = "source") val source: String?
): Parcelable
