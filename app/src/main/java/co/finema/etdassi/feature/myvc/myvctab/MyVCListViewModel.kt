package co.finema.etdassi.feature.myvc.myvctab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.data.VCDocumentRepository
import co.finema.etdassi.feature.myvc.myvctab.usecase.MyVCCheckStatusUseCase

class MyVCListViewModel(
    private val vcDocumentRepository: VCDocumentRepository,
    private val myVCCheckStatusUseCase: MyVCCheckStatusUseCase
): BaseViewModel() {

    var filterAll: Boolean = false
    var filterActive: Boolean = false
    var filterDeActive: Boolean = false
    private var isFetch = false

    private val _vcList = MutableLiveData<List<MyVCListAdapter.VCViewAdapterItem>>()
    private val vcListFilter = MutableLiveData<List<MyVCListAdapter.VCViewAdapterItem>>()
    val vcList: LiveData<List<MyVCListAdapter.VCViewAdapterItem>> get() = Transformations.map(vcDocumentRepository.getAllVCDocumentList()) { list ->
        if (!isFetch) {
            updateVCStatus(list.map {
                it.cid
            })
        }
        list.sortedByDescending { it.issuanceDate }.map {
            MyVCListAdapter.VCViewAdapterItem(
                id = it.cid,
                vcTypeName = it.type,
                vcStatus = it.status
            )
        }
    }

    private fun updateVCStatus(cidList: List<String>) {
        myVCCheckStatusUseCase.launch(viewModelScope, cidList) {
            it.either({
                it.printStackTrace()
            }, {
                isFetch = it
                println()
            })
        }
    }

    fun doFilter() {
        val preFilter = if (filterAll|| (!filterAll && !filterActive && !filterDeActive)) {
            _vcList.value
        } else {
            _vcList.value?.filter {
                if (filterActive) {
                    it.vcStatus == "active"
                } else {
                    it.vcStatus == "revoke"
                }
            }
        }

//        preFilter?.first()?.count = preFilter?.size!! - 1
        vcListFilter.value = preFilter ?: emptyList()

    }
}