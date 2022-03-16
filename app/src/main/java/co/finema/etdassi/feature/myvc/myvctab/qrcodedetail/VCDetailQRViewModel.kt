package co.finema.etdassi.feature.myvc.myvctab.qrcodedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.data.VCDocumentRepository
import co.finema.etdassi.feature.myvc.myvctab.MyVCListAdapter
import co.finema.etdassi.feature.myvc.myvctab.qrcodedetail.usecase.VCDetailQRUseCase

class VCDetailQRViewModel(
    private val vcDetailQRUseCase: VCDetailQRUseCase
): BaseViewModel() {

    private val _vcData = MutableLiveData<MyVCListAdapter.VCViewAdapterItem>()
    val vcData: LiveData<MyVCListAdapter.VCViewAdapterItem> get() = _vcData

    private val _qrString = MutableLiveData<String>()
    val qrString: LiveData<String> get() = _qrString

    fun readData(data: MyVCListAdapter.VCViewAdapterItem) {
        _vcData.value = data
    }

    fun generateQR(cid: String) {
        vcDetailQRUseCase.launch(viewModelScope, cid) {
            it.either({
                it.printStackTrace()
            }, { s ->
                _qrString.value = s
                println("")
            })
        }
    }


}