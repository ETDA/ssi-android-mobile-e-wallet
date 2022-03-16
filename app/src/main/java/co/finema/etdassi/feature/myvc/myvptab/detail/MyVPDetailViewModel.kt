package co.finema.etdassi.feature.myvc.myvptab.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.data.VCDocumentRepository
import co.finema.etdassi.common.data.VPDocumentRepository
import co.finema.etdassi.feature.myvc.myvptab.MyVPAdapter
import co.finema.etdassi.feature.myvc.myvptab.model.VPListModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyVPDetailViewModel(
    private val vpDocumentRepository: VPDocumentRepository,
    private val vcDocumentRepository: VCDocumentRepository
): BaseViewModel() {


    private val _vpList = MutableLiveData<List<VPListModel>>()
    val vpList: LiveData<List<VPListModel>> get() = _vpList
    val didRequester = MutableLiveData<String>()

    init {
        //TODO MOCKUP
        val vcMock = ArrayList<VPListModel>()
        vcMock.add(VPListModel(cid = "1", vcName = "บัตรประชาชน", vcStatus = "active"))
        vcMock.add(VPListModel(cid = "1", vcName = "ใบรับรองแพทย์", vcStatus = "revoke"))
        _vpList.value = vcMock
    }

    fun getVPDocument(vpDocumentAdapterModel: MyVPAdapter.VPDocumentAdapterModel) {
        vpDocumentAdapterModel.id?.let {
            val vpDocumentTable = vpDocumentRepository.getVPBySubmitId(vpDocumentAdapterModel.id)
            didRequester.value = vpDocumentTable.verifierDid ?:""
            vpDocumentTable.vpIdList?.let {
                val type = object : TypeToken<List<String>>() {}.type
                val cidList = Gson().fromJson<List<String>>(it, type)
                _vpList.value  = vcDocumentRepository.getVCDocListByCidList(cidList)
            }
        }

    }

}