package co.finema.etdassi.feature.createreq.requestlist

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.feature.createreq.listofvc.ListOfVCModelList
import kotlinx.android.parcel.Parcelize

class ReqStepListViewModel : BaseViewModel() {

    private val _vcList = MutableLiveData<ArrayList<ReqStepListModel>>()
    val vcList: LiveData<ArrayList<ReqStepListModel>> get() = _vcList

    fun setRequestList(parcelable: ListOfVCModelList?) {
        parcelable?.vcList?.let {
            val requestList = ArrayList<ReqStepListModel>()
            requestList.add(ReqStepListModel(count = it.size))
            it.forEach { listOfVCModel ->
                requestList.add(
                    ReqStepListModel(
                        id = listOfVCModel.id, isRequest = true, vcName = listOfVCModel.vcName
                                    )
                               )
            }
            _vcList.value = requestList
        }
    }

    fun deleteItemById(id: String) {
        _vcList.value?.apply {
            this.removeIf { it.id == id }
            if (this.size == 1) {
                _vcList.value = ArrayList()
            } else {
                this.firstOrNull { it.count != null }?.count = this.size - 1
                _vcList.value = this
            }
        }
    }


}

@Parcelize
data class ReqStepListModel(val id: String? = null,
                            var count: Int? = null,
                            var isRequest: Boolean = true,
                            val vcName: String? = null,
                            var description: String? = null) : Parcelable
