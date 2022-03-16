package co.finema.etdassi.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.data.VCDocumentRepository
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.feature.mysign.data.VCSignedRepository
import co.finema.etdassi.feature.notification.data.NotificationRepository

class HomeViewModel(
    private val userHelper: UserHelper,
    private val notificationRepository: NotificationRepository,
    private val vcDocumentRepository: VCDocumentRepository,
    private val vcSignedRepository: VCSignedRepository
) : BaseViewModel() {


    private val _didAddress = MutableLiveData<String>()
    val didAddress:LiveData<String> get() = _didAddress

    private val _myRequestVc = MutableLiveData<String>()
    val myRequestVc: LiveData<String> get() = _myRequestVc

    val notificationBadge: LiveData<Int> get() = notificationRepository.getUnreadCount()

    val vcCount = vcDocumentRepository.getCountVC()

    val signRequestCount: LiveData<Int> get() = notificationRepository.getRequestToSignCount()
    val signedCount: LiveData<Int> get() = vcSignedRepository.getVCSignedCount()

    init {
        _didAddress.value = userHelper.getDIDAddress()
    }

}