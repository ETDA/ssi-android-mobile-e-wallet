package co.finema.etdassi.feature.mysign.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VCSignedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVCSigned(vcSigned: VCSigned)

    @Query("SELECT * FROM vc_signed")
    fun getVCSignedList(): LiveData<List<VCSigned>>

    @Query("SELECT * FROM vc_signed WHERE id=:vcSignedId LIMIT 1")
    fun getVCSignedById(vcSignedId: String): VCSigned

    @Query("UPDATE vc_signed SET read_status = :isRead WHERE id=:vcSignedId")
    fun updateVCSignedById(isRead: Boolean, vcSignedId: String)

    @Query("SELECT COUNT(id) FROM vc_signed WHERE read_status = 0")
    fun getUnReadCount(): LiveData<Int>

    @Query("UPDATE vc_signed SET status =:status WHERE id=:vcSignedId")
    fun upDateVCSignedStatus(vcSignedId: String, status: String)

    @Query("UPDATE vc_signed SET jwt =:jwt WHERE id=:cid")
    fun updateJwt(cid: String, jwt: String)

    @Query("SELECT COUNT(id) FROM vc_signed")
    fun getVCSignedCount(): LiveData<Int>
}