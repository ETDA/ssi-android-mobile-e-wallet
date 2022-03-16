package co.finema.etdassi.feature.sign

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.data.VCAttributeModel
import co.finema.etdassi.common.enum.DoActionEnum
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.utils.GenericListener
import co.finema.etdassi.common.utils.handleError
import co.finema.etdassi.feature.notification.NotificationStatus
import co.finema.etdassi.feature.notification.data.NotificationRepository
import co.finema.etdassi.feature.sign.usecase.RejectVCUseCase
import co.finema.etdassi.feature.sign.usecase.SignVCUseCase
import kotlinx.android.parcel.Parcelize

class SignViewModel(
    private val userHelper: UserHelper,
    private val notificationRepository: NotificationRepository,
    private val signVCUseCase: SignVCUseCase,
    private val rejectVCUseCase: RejectVCUseCase
): BaseViewModel() {

    val rejectComment = MutableLiveData<String>()

    data class SignDescriptionModel(
        val notificationId: String?,
        val didRequester: String?,
        val vcType: String?,
        val status: NotificationStatus?,
        val vcDetail: List<VCAttributeModel>?,
        val source: NotificationRepository.NotificationOriginalSource?,
        val creator: String?
    )

    @Parcelize
    data class SignResultPageData(
        val notificationId: String?,
        val vcDetail: List<VCAttributeModel>?
            ) : Parcelable

    private val _notificationData = MutableLiveData<SignDescriptionModel>()
    val notificationData: LiveData<SignDescriptionModel> get() = _notificationData

    fun rejectVC(param: GenericListener<Boolean>) {
        val notiData = _notificationData.value
        if (notiData?.notificationId != null &&
            notiData.source?.rejectEndpoint != null &&
            notiData.source.issuer != null &&
            notiData.vcType != null) {
            rejectVCUseCase.launch(viewModelScope, RejectVCUseCase.Param(
                rejectEndpoint = notiData.source.rejectEndpoint!!,
                comment = rejectComment.value ?:" ",
                notificationId = notiData.notificationId,
                issuer = notiData.source.issuer,
                vcType = notiData.vcType
            )) { either ->
            either.either( {
                it.printStackTrace()
                param.onFail(it.handleError())
            }, {
                param.onSuccess(it)
            })
            }
        } else {
            param.onFail("ลองใหม่อีกครั้ง")
        }

    }

    fun signVC(param: GenericListener<String>) {
        if(_notificationData.value?.source != null && _notificationData.value?.vcType != null && _notificationData.value?.notificationId != null) {
            signVCUseCase.launch(viewModelScope, SignVCUseCase.Param(_notificationData.value?.source!!, _notificationData.value?.vcType!!, _notificationData.value?.notificationId!!)) {
                it.either({ error ->
                    error.printStackTrace()
                    param.onFail(error.handleError())
                }, { response ->
                    param.onSuccess(response)
                })
            }
        }
    }

    fun setActionNext() {
        userHelper.setActionNext(DoActionEnum.REJECT_DONE_OPEN_REJECT_PAGE)
    }

    fun getDataFromNotiId(notiId: String) {
        _notificationData.value = notificationRepository.getNotificationDataById(notiId)
    }
}