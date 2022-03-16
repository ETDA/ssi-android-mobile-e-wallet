package co.finema.etdassi.feature.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.feature.notification.NotificationListAdapter.NotificationAdapterModel
import co.finema.etdassi.feature.notification.NotificationItemAdapter.NotificationItemModel
import co.finema.etdassi.feature.notification.data.NotificationRepository
import kotlinx.coroutines.launch

class NotificationListViewModel(
    private val notificationRepository: NotificationRepository
): BaseViewModel() {


    private val _notificationList = MutableLiveData<List<NotificationAdapterModel>>()

    private val _notification = notificationRepository.getNotificationList()
    val notificationList: LiveData<List<NotificationItemModel>> get() = Transformations.map(_notification) { list ->
        val source = list.map { model ->
            NotificationAdapterModel(
                notificationId = model.id.toString(),
                vcName = model.vcType,
                status = model.status?.let { NotificationStatus.valueOf(it) },
                date = model.date,
                isRead = model.isRead,
                dateKey = model.dateKey,
                credentialSubject = "",
                approveEndpoint = model.approveEndpoint,
                rejectEndpoint = model.rejectEndpoint,
                creator = model.creator
            )
        }
        val dataGroup = source.groupBy { _source -> _source.dateKey }
        val data = ArrayList<NotificationItemModel>()
        for (key in dataGroup.keys){
            data.add(
                NotificationItemModel(
                    title = key,
                    dataGroup.getValue(key).sortedByDescending { it.date }
                )
            )
        }
        data.sortedByDescending { it.title }
    }

    fun setReadStatus(notificationId: String) {
        notificationRepository.updateNotiReadStatus(notificationId)
    }






}
