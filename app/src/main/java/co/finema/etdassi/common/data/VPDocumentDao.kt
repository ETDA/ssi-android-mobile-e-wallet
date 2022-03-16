package co.finema.etdassi.common.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VPDocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVPDocument(vpDocumentTable: VPDocumentTable): Long

    @Query("SELECT * FROM vp_document")
    fun getVPDocumentList(): LiveData<List<VPDocumentTable>>

    @Query("SELECT * FROM vp_document WHERE id =:submitId")
    fun getVPDocumentById(submitId: String): VPDocumentTable
}