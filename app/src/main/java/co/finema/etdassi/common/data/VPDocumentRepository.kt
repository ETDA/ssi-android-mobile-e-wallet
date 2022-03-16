package co.finema.etdassi.common.data

import com.google.gson.Gson
import javax.inject.Singleton

@Singleton
class VPDocumentRepository(
    private val vpDocumentDao: VPDocumentDao
) {
    suspend fun insertVPDocument(vpDocument: VPDocument) {
        val vpDocumentTable = VPDocumentTable(
            id = vpDocument.id,
            name = vpDocument.name,
            verifierDid = vpDocument.verifierDid,
            createdAt = vpDocument.createdAt,
            sendAt = vpDocument.sendAt,
            vpIdList = Gson().toJson(vpDocument.vpIdList),
            jwt = vpDocument.jwt,
            source = Gson().toJson(vpDocument.source)
        )
        vpDocumentDao.insertVPDocument(vpDocumentTable)
    }

    fun getVPDocumentList() = vpDocumentDao.getVPDocumentList()

    fun getVPBySubmitId(submitId: String) = vpDocumentDao.getVPDocumentById(submitId)


}