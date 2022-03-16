package co.finema.etdassi.feature.mysign.myreject

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.feature.mysign.data.MyRejectRepository
import kotlinx.coroutines.launch

class MyRejectViewModel(
    private val myRejectRepository: MyRejectRepository
): BaseViewModel() {

    init {
//        getVcList()
    }


    val vcList: LiveData<List<MyRejectModel>> = Transformations.map(myRejectRepository.getMyRejectList()) { list ->
        list.sortedByDescending { it.rejectDate }
    }

}