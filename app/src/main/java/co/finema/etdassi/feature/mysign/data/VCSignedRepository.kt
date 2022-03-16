package co.finema.etdassi.feature.mysign.data

import javax.inject.Singleton

@Singleton
class VCSignedRepository (
    private val vcSignedDao: VCSignedDao
) {

    suspend fun createVCSigned(vcSigned: VCSigned) {
        vcSignedDao.insertVCSigned(vcSigned)
    }

    fun getVCSignedList() = vcSignedDao.getVCSignedList()

    fun getVCSignedById(vcSignedId: String) = vcSignedDao.getVCSignedById(vcSignedId = vcSignedId)

    fun updateSReadStatus(vcSignedId: String) {
        vcSignedDao.updateVCSignedById(isRead = true, vcSignedId = vcSignedId)
    }

    fun getUnReadCount() = vcSignedDao.getUnReadCount()

    fun updateVCSignStatus(vcSignedId: String,vcSignedStatus: String){
        vcSignedDao.upDateVCSignedStatus(vcSignedId,vcSignedStatus)
    }

    fun updateVCJWT(vcSignedId: String,jwt : String){
        vcSignedDao.updateJwt(vcSignedId,jwt)
    }

    fun getVCSignedCount() = vcSignedDao.getVCSignedCount()

}