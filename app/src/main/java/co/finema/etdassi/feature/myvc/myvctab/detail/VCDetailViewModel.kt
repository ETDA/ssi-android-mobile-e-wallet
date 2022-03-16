package co.finema.etdassi.feature.myvc.myvctab.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.data.VCAttributeModel
import co.finema.etdassi.common.data.VCDocument
import co.finema.etdassi.common.data.VCDocumentRepository
import co.finema.etdassi.common.utils.JWTUtils
import com.google.gson.JsonObject

class VCDetailViewModel(
    private val vcDocumentRepository: VCDocumentRepository
): BaseViewModel() {

    private val _vcDocumentCardStatus = MutableLiveData<VCCardAdapter.VCCardModel>()
    private val _vcDocumentDescription = MutableLiveData<List<VCAttributeModel>>()
    val vcDocumentCardStatus: LiveData<VCCardAdapter.VCCardModel> get() = _vcDocumentCardStatus
    val vcDocumentDescription: LiveData<List<VCAttributeModel>> get() = _vcDocumentDescription


    data class VCDetailAdapterModel(
        val id: String? = null,
        val attributeName: String? = null,
        val name: String?  = null,
        val description: String? = null
    )

    fun getVcDocumentByCid(cid: String) {
        val vcDoc = vcDocumentRepository.getVCDocument(cid)
        vcDoc.let {
            _vcDocumentCardStatus.value = VCCardAdapter.VCCardModel(
                vcType = it?.type?:"",
                vcStatus = it?.status ?:"",
                vcTag = it?.tags
            )
            it?.credentialSubject?.let { itemList -> _vcDocumentDescription.value = itemList}
        }
    }

}