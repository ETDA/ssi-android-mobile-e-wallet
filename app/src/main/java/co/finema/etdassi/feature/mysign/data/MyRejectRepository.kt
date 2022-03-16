package co.finema.etdassi.feature.mysign.data

import co.finema.etdassi.feature.mysign.myreject.MyRejectModel
import javax.inject.Singleton

@Singleton
class MyRejectRepository(
    private val myRejectDao: MyRejectDao
) {

    suspend fun createMyReject(myReject: MyRejectModel) = myRejectDao.insertMyReject(myReject)

    fun getMyRejectList() = myRejectDao.getMyRejectList()

    fun getMyRejectById(id: String) = myRejectDao.getMyRejectById(id)
}