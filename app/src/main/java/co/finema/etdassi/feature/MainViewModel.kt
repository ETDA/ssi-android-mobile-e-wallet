package co.finema.etdassi.feature

import android.util.Log
import androidx.core.graphics.blue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.enum.DoActionEnum
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.feature.mysign.data.SignRequestRepository
import co.finema.etdassi.feature.mysign.data.VCSignedRepository
import co.finema.etdassi.feature.notification.data.NotificationRepository
import co.finema.etdassi.feature.pageloading.usecase.GetBackupVCUseCase
import com.google.gson.Gson

class MainViewModel(
    private val userHelper: UserHelper,
    private val notificationRepository: NotificationRepository,
    private val vcSignedRepository: VCSignedRepository
):BaseViewModel() {

    private val _bottomBadge = MutableLiveData<Int>()
    val notificationCount: LiveData<Int> get() = notificationRepository.getUnreadCount()
    val signedCount: LiveData<Int> get() = vcSignedRepository.getUnReadCount()
    val mainBadge: LiveData<Int> get() = Transformations.map(notificationRepository.countMainNavBadge()) {
        it.countNoti + it.countSigned
    }

    fun checkActionNext(): DoActionEnum? {
        return userHelper.getActionNext()
    }

    fun runTest() {
        println("UUID => ${userHelper.getUUID()}")
        println("ID => ${userHelper.getUserId()}")
        println("DID =>  ${userHelper.getDIDAddress()}")
        println("isbackup => ${userHelper.getBackupStatus()}")

    }
}