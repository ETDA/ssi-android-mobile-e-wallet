package co.finema.etdassi.feature.createreq.listofvc

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.finema.etdassi.common.base.BaseViewModel
import kotlinx.android.parcel.Parcelize

class ListOfVCViewModel:BaseViewModel() {

    //TODO MOCK DATA
    private val _vcList = MutableLiveData<List<ListOfVCModel>>()
    val vcList: LiveData<List<ListOfVCModel>> get() = _vcList

    fun getVCList() {
        val vcListBuilder = ArrayList<ListOfVCModel>()
        vcListBuilder.add(ListOfVCModel(count = 8))
        vcListBuilder.add(ListOfVCModel(id = "1", vcName = "บัตรประชาชน"))
        vcListBuilder.add(ListOfVCModel(id = "2", vcName = "ใบรับรองแพทย์"))
        vcListBuilder.add(ListOfVCModel(id = "3", vcName = "บัตรประกัน"))
        vcListBuilder.add(ListOfVCModel(id = "4", vcName = "บัตรสมาชิก"))
        vcListBuilder.add(ListOfVCModel(id = "5", vcName = "บัตรสมาชิก"))
        vcListBuilder.add(ListOfVCModel(id = "6", vcName = "บัตรสมาชิก"))
        vcListBuilder.add(ListOfVCModel(id = "7", vcName = "บัตรสมาชิก"))
        vcListBuilder.add(ListOfVCModel(id = "8", vcName = "บัตรสมาชิก"))
        _vcList.value = vcListBuilder
    }

}

@Parcelize
data class ListOfVCModel(
    val id: String? = null,
    val count: Int? = null,
    var isCheck: Boolean = false,
    val vcName: String? = null
): Parcelable

@Parcelize
data class ListOfVCModelList(
        val  vcList: List<ListOfVCModel>?
                            ):Parcelable