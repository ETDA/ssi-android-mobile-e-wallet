package co.finema.etdassi.feature.mysign.myreject.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.data.VCAttributeModel
import co.finema.etdassi.feature.mysign.myreject.MyRejectModel
import co.finema.etdassi.feature.mysign.data.MyRejectRepository
import co.finema.etdassi.feature.notification.data.NotificationRepository

class MyRejectDetailViewModel(
    private val notificationRepository: NotificationRepository
): BaseViewModel() {


    private val _myRejectData = MutableLiveData<MyRejectDetailPageModel>()
    val myRejectData: LiveData<MyRejectDetailPageModel> get() = _myRejectData

    data class MyRejectDetailPageModel(
        val issuer: String?,
        val vcType: String?,
        val status: String?,
        val vcDescription: List<VCAttributeModel>?,
        val reason: String?
    )

    fun getMyReject(myRejectModel: MyRejectModel) {
        myRejectModel.notificationId?.let {
            val descriptionModel = notificationRepository.getNotificationDataById(it)
            _myRejectData.value = MyRejectDetailPageModel(
                issuer = myRejectModel.issuer,
                vcType = myRejectModel.name,
                status = myRejectModel.status,
                vcDescription = descriptionModel.vcDetail,
                reason = myRejectModel.reason
            )

        }

    }

}