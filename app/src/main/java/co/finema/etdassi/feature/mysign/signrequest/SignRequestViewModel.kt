package co.finema.etdassi.feature.mysign.signrequest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.TitleAdapterDataModel
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.feature.mysign.data.SignRequestModel
import co.finema.etdassi.feature.mysign.data.SignRequestRepository
import co.finema.etdassi.feature.notification.data.NotificationRepository
import kotlinx.coroutines.launch
import co.finema.etdassi.feature.mysign.signrequest.SignRequestAdapter.*

class SignRequestViewModel(
    private val notificationRepository: NotificationRepository
): BaseViewModel() {

    val list: LiveData<List<SignRequestAdapterModel>> get() = Transformations.map(notificationRepository.getRequestToSignList()) { notiList ->
        notiList.map { notiModel ->
            SignRequestAdapterModel(
                id = notiModel.id,
                typeName = notiModel.vcType,
                dateRequest = notiModel.date,
                isRead = notiModel.isRead
            )
        }.sortedByDescending { it.dateRequest }
    }

    fun setReadStatus(notificationId: String) {
        notificationRepository.updateNotiReadStatus(notificationId)
    }



}