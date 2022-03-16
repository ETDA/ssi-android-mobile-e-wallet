package co.finema.etdassi.feature.mysign.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SignRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignRequest(signRequestModel: List<SignRequestModel>)

    @Query("SELECT * FROM sign_request")
    fun getSignRequests(): LiveData<List<SignRequestModel>>

    @Query("DELETE FROM sign_request WHERE id IN (:signList)")
    fun deleteSignRequestFromList(signList: List<String>)
//
    @Query("UPDATE sign_request SET read_status = :isRead WHERE id=:signRequestId")
    fun updateSignRequestRead(isRead: Boolean, signRequestId: String)

    @Query("SELECT COUNT(id) FROM sign_request WHERE read_status = 0")
    fun getUnReadCount(): Int

}