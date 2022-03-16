package co.finema.etdassi.feature.createreq.requestsum

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.feature.createreq.listofvc.ListOfVCModelList
import co.finema.etdassi.feature.createreq.requestlist.ReqStepListModel
import kotlinx.android.parcel.Parcelize

class ReqSummaryViewModel:BaseViewModel() {
    private val _vcList = MutableLiveData<List<ReqStepListModel>>()
    val vcList: LiveData<List<ReqStepListModel>> get() = _vcList

    fun setRequestList(parcelable: ReqSummaryList?) {
        parcelable?.list?.let {
            _vcList.value = it
        }
    }
}

@Parcelize
data class ReqSummaryList(
        val list: List<ReqStepListModel>?
                         ): Parcelable