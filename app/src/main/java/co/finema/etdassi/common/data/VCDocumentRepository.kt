package co.finema.etdassi.common.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import co.finema.etdassi.feature.myvc.myvctab.MyVCListAdapter
import co.finema.etdassi.feature.myvc.myvptab.model.VPListModel
import co.finema.etdassi.feature.notification.NotificationStatus
import co.finema.etdassi.feature.scan_qr.ScanStepSelectVCList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import javax.inject.Singleton

@Singleton
class VCDocumentRepository(
    private val vcDocumentDao: VCDocumentDao
) {


    fun getAllVCDocumentList(): LiveData<List<VCDocumentTable>> = vcDocumentDao.getVCDocumentList()
    fun getCountVC() = vcDocumentDao.getCountVC()

    suspend fun insertVCDocument(vcDocument: VCDocument): Long {
        val vcDocumentTable = VCDocumentTable(
            cid = vcDocument.cid,
            status = vcDocument.status,
            issuanceDate = vcDocument.issuanceDate,
            expirationDate = vcDocument.expirationDate,
            type = vcDocument.type,
            issuer = vcDocument.issuer,
            holder = vcDocument.holder,
            credentialSubject = Gson().toJson(vcDocument.credentialSubject),
            jwt = vcDocument.jwt,
            backupStatus = vcDocument.backupStatus,
            tags = vcDocument.tags
        )
        return vcDocumentDao.insertVCDocument(vcDocumentTable)
    }

    fun getVCDocument(cid: String): VCDocument? {
        val vcDocument = vcDocumentDao.getVCDocumentById(cid) ?: return null
        return vcDocument.let {
            val type = object : TypeToken<List<VCAttributeModel>>() {}.type
            VCDocument(
                cid = it.cid,
                status = it.status,
                issuanceDate = it.issuanceDate,
                expirationDate = it.expirationDate,
                type = it.type,
                issuer = it.issuer,
                holder = it.holder,
                credentialSubject = Gson().fromJson<List<VCAttributeModel>>(it.credentialSubject, type),
                jwt = it.jwt,
                backupStatus = it.backupStatus,
                tags = it.tags
            )
        }
    }

    fun getVCDocByType(type: String): List<ScanStepSelectVCList> {
        val vcDocument = vcDocumentDao.getVCDocByType(type)
        return vcDocument.map {
            ScanStepSelectVCList(
                cid = it.cid,
                vcTypeName = it.type,
                dateString = it.issuanceDate,
                status = it.status == NotificationStatus.ACTIVE.name || it.status == "active"
            )
        }
    }

    fun getVCDocByCidList(cidList: List<String>): List<String> {
        val vcDocList = vcDocumentDao.getVCDocumentByCidList(cidList)
        return vcDocList.map {
            it.jwt
        }
    }

    fun getVCDocListByCidList(cidList: List<String>): List<VPListModel> {
        val vcDocList = vcDocumentDao.getVCDocumentByCidList(cidList)
        return vcDocList.map {
            VPListModel(
                cid = it.cid,
                vcName = it.type,
                vcStatus = it.status
            )
        }
    }

    fun updateVCDocumentStatusByCid(cid: String, status: String) {
        vcDocumentDao.updateVCDocumentByCid(cid, status)
    }

    fun updateVCSDocumentStatus(
        cid: String,
        status: String,
        tags: String?
    ) {
        vcDocumentDao.updateVCSDocumentStatus(cid, status, tags)
    }

}