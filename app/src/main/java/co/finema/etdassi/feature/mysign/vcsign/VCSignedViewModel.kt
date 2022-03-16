package co.finema.etdassi.feature.mysign.vcsign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.feature.mysign.data.VCSigned
import co.finema.etdassi.feature.mysign.data.VCSignedRepository
import kotlinx.coroutines.launch

class VCSignedViewModel(
    private val vcSignedRepository: VCSignedRepository
): BaseViewModel() {

    val list: LiveData<List<VCSigned>> get() = vcSignedRepository.getVCSignedList()


    init {
//        getVcList()
    }


    fun setVCReadStatus(vcSignedId: String) {
        viewModelScope.launch {
            vcSignedRepository.updateSReadStatus(vcSignedId)
        }
    }

}