package co.finema.etdassi.feature.getvc


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.utils.ApiListener
import co.finema.etdassi.common.utils.handleError
import co.finema.etdassi.feature.getvc.usecase.GetVCUseCase
import co.finema.etdassi.feature.qr_reader.QRTextResultModel
import kotlinx.coroutines.launch

class GetVCLoadingViewModel(
    private val getVCUseCase: GetVCUseCase
): BaseViewModel() {

    private val _vcGettingResult = MutableLiveData<GetVCUseCase.VCSavingCount>()
    val vcGettingResult: LiveData<GetVCUseCase.VCSavingCount> get() = _vcGettingResult

    fun getVC(qrTextResultModel: QRTextResultModel, getVCListener: GetVCListener) {
        getVCUseCase.launch(viewModelScope, qrTextResultModel) {
            it.either( { error ->
                error.printStackTrace()
                getVCListener.onFail(error.handleError())
            }, { vcSavingCount ->
                _vcGettingResult.value = vcSavingCount
                vcSavingCount?.successCase?.let { success ->
                    if (success > 0) {
                        getVCListener.onSuccess()
                    } else {
                        getVCListener.onFail("ไม่สามารถนำเข้าเอกสารได้")
                    }
                } ?: kotlin.run {
                    getVCListener.onFail("ไม่สามารถนำเข้าเอกสารได้")
                }


            })
        }
    }

    interface GetVCListener{
        fun onFail(errorMessage: String)
        fun onSuccess()
    }
}