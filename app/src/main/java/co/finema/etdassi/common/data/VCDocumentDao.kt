package co.finema.etdassi.common.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VCDocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVCDocument(vcDocumentTable: VCDocumentTable): Long

    @Query("SELECT * FROM vc_document")
    fun getVCDocumentList(): LiveData<List<VCDocumentTable>>

    @Query("SELECT * FROM vc_document WHERE cid IN (:cidList)")
    fun getVCDocumentByCidList(cidList: List<String>): List<VCDocumentTable>

    @Query("SELECT * FROM vc_document WHERE cid = :cid LIMIT 1")
    fun getVCDocumentById(cid: String): VCDocumentTable?

    @Query("SELECT * FROM vc_document WHERE type = :type")
    fun getVCDocByType(type: String): List<VCDocumentTable>

//    @Query("SELECT COUNT(id) FROM notification WHERE read_status = 0")
    @Query("SELECT COUNT(cid) FROM vc_document")
    fun getCountVC(): LiveData<Int>

    @Query("UPDATE vc_document SET status =:status WHERE cid=:cid")
    fun updateVCDocumentByCid(cid: String, status: String)

    @Query("UPDATE vc_document SET status =:status, tags =:tags WHERE cid=:cid")
    fun updateVCSDocumentStatus(
        cid: String,
        status: String,
        tags: String?
    )
}