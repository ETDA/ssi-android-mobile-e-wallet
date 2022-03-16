package co.finema.etdassi.feature.mysign.data

import javax.inject.Singleton

@Singleton
class SignRequestRepository(private val signRequestDao: SignRequestDao) {

    suspend fun createSignRequestList(signRequestList: List<SignRequestModel>) {
        signRequestDao.insertSignRequest(signRequestList)
    }

    fun getSignRequest() = signRequestDao.getSignRequests()

    fun setReadStatus(signRequestId: String) = signRequestDao.updateSignRequestRead(true, signRequestId)

    fun getCountUnRead() = signRequestDao.getUnReadCount()

}