package co.finema.etdassi.feature.mysign.vcsign

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.data.VCAttributeModel
import co.finema.etdassi.common.utils.JWTUtils
import co.finema.etdassi.common.utils.handleError
import co.finema.etdassi.feature.mysign.data.VCSigned
import co.finema.etdassi.feature.mysign.data.VCSignedRepository
import co.finema.etdassi.feature.notification.NotificationStatus
import co.finema.etdassi.feature.notification.data.NotificationRepository

class VCSignedDetailViewModel(
    private val vcSignedUseCase: VCSignedUseCase,
    private val notificationRepository: NotificationRepository
) : BaseViewModel() {

    private val _description = MutableLiveData<List<VCAttributeModel>>()
    val description: LiveData<List<VCAttributeModel>> get() = _description

    fun getVCDescription(vcSigned: VCSigned) {
        vcSigned.notificationId?.let {

            notificationRepository.getNotificationDataById(it).vcDetail?.let { list ->
                _description.value = list
            }

        } ?: kotlin.run {
            vcSigned.jwt?.let { jwt ->
                val jwtModel = JWTUtils.decodedJWT(jwt)
                val schemaModel = jwtModel?.let { it->
                    JWTUtils.jwtConvertToSchemaModel(it)
                }

                val credentialSubject = schemaModel?.vc?.credentialSubject?.let {
                    JWTUtils.credentialSubjectToAttributeModel(
                        it
                    )
                }
                credentialSubject?.let { subject ->
                    _description.value = subject
                }
            }
        }
    }

    fun revokeVC(
        revokeAdapter: MySignedRevokeCardAdapter.AdapterModel,
        revokeVCListener: RevokeVCListener
    ) {
        fun success(s: VCSigned) {
            revokeVCListener.onSuccess(s)
        }

        fun fail(throwable: Throwable) {
            revokeVCListener.onFail(throwable.handleError())
        }

        vcSignedUseCase.launch(viewModelScope, VCSignedUseCase.Param(revokeAdapter.cid)) {
            it.either(::fail, ::success)
        }
    }

}

interface RevokeVCListener {
    fun onFail(errorMessage: String)
    fun onSuccess(vcSigned: VCSigned)
}