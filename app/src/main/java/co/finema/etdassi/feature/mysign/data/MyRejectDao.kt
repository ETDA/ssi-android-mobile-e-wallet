package co.finema.etdassi.feature.mysign.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.finema.etdassi.feature.mysign.myreject.MyRejectModel

@Dao
interface MyRejectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMyReject(insertMyReject: MyRejectModel): Long

    @Query("SELECT * FROM my_reject_table")
    fun getMyRejectList(): LiveData<List<MyRejectModel>>

    @Query("SELECT * FROM my_reject_table WHERE id =:id")
    fun getMyRejectById(id: String): LiveData<List<MyRejectModel>>

}