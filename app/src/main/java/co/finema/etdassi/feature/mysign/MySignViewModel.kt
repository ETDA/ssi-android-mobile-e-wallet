package co.finema.etdassi.feature.mysign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.enum.DoActionEnum
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.feature.mysign.data.VCSignedRepository
import co.finema.etdassi.feature.mysign.data.SignRequestRepository

class MySignViewModel(
    private val userHelper: UserHelper,
    private val signRequestRepository: SignRequestRepository,
    private val vcSignedRepository: VCSignedRepository
): BaseViewModel() {

    val signRequestCountUnRead = MutableLiveData<Int>()
    val vcSignedUnRead :LiveData<Int> get() = vcSignedRepository.getUnReadCount()

    fun checkActionNext(): DoActionEnum? {
        return userHelper.getActionNext()
    }

    fun clearActionNext() {
        userHelper.clearActionNext()
    }

    fun getCountUnRead() {
        signRequestCountUnRead.value = signRequestRepository.getCountUnRead()
    }

}