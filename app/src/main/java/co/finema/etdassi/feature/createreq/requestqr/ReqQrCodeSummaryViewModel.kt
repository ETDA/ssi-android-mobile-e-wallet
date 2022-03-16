package co.finema.etdassi.feature.createreq.requestqr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.feature.createreq.requestlist.ReqStepListModel
import co.finema.etdassi.feature.createreq.requestsum.ReqSummaryList

class ReqQrCodeSummaryViewModel: BaseViewModel() {

    private val _vcList = MutableLiveData<List<ReqStepListModel>>()
    val vcList: LiveData<List<ReqStepListModel>> get() = _vcList

    fun setRequestList(parcelable: ReqSummaryList?) {
        parcelable?.list?.let {
            _vcList.value = it
        }
    }

}