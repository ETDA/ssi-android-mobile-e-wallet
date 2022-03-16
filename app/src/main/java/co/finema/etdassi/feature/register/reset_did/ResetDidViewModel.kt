package co.finema.etdassi.feature.register.reset_did

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.enum.RegisterState
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.utils.ApiListener
import co.finema.etdassi.common.utils.GenericListener
import co.finema.etdassi.common.utils.handleError
import co.finema.etdassi.feature.register.usecase.CheckIsBackupUseCase
import co.finema.etdassi.feature.register.usecase.RegisterCheckEmailStatusUseCase
import co.finema.etdassi.feature.register.usecase.ResendEmailUseCase

class ResetDidViewModel(
    private val userHelper: UserHelper,
    private val registerCheckEmailStatusUseCase: RegisterCheckEmailStatusUseCase,
    private val resendEmailUseCase: ResendEmailUseCase,
    private val checkIsBackupUseCase: CheckIsBackupUseCase
) : ViewModel() {

    val email = userHelper.getEmail()
    val recoveryStateDone = userHelper.getRegisterState() == RegisterState.RESET_DID_SUCCESS.name
    val refCode = MutableLiveData<String>()

    fun sendOTP(otpNumber: String, param: ApiListener) {
        registerCheckEmailStatusUseCase.launch(viewModelScope, RegisterCheckEmailStatusUseCase.Param(otpNumber)) {
            it.either({ throwable ->
                param.onFail(throwable.handleError())
            }, {
                if (it) {
                    if (isRecovery()) {
                        userHelper.setRegisterState(RegisterState.RESET_DID_SUCCESS.name)
                    } else {
                        userHelper.setRegisterState(RegisterState.VERIFY_EMAIL_SUCCESS.name)
                    }
                    param.onSuccess()
                }
            })
        }
    }
    fun isRecovery(): Boolean {
        return userHelper.getDIDAddress() != null && userHelper.getRegisterState() == RegisterState.VERIFY_EMAIL_SUCCESS.name
    }

    fun resentEmail(param: ApiListener) {

        val id = userHelper.getUserId()

        val useCaseParam = ResendEmailUseCase.Param(id ?:"")
        Log.d("resentEmail" ,"useCaseParam => $useCaseParam")
        resendEmailUseCase.launch(viewModelScope, param = useCaseParam) {
            it.either({ throwable ->
                param.onFail(throwable.handleError())
            }, { response ->
                if (response) {
                    param.onSuccess()
                } else {
                    param.onFail("Try a gain")
                }
            })
        }
    }

    fun checkBackupEWallet(listener: GenericListener<Boolean>) {
        userHelper.getDIDAddress()?.let {
            checkIsBackupUseCase.launch(viewModelScope, it) { either ->
                either.either({
                    it.printStackTrace()
                    listener.onFail("")
                },  {
                    if (it) {
                        userHelper.setBackupStatus(true)
                        listener.onSuccess(it)
                    } else {
                        listener.onFail("")
                    }
                })
            }
        } ?: kotlin.run {
            listener.onFail("")
        }
    }
}