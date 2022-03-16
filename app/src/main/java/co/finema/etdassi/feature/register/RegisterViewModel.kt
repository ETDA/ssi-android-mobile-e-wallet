package co.finema.etdassi.feature.register

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.enum.RegisterState
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.utils.GenericListener
import co.finema.etdassi.feature.register.usecase.CheckIsBackupUseCase

class RegisterViewModel(
    private val userHelper: UserHelper
):BaseViewModel() {

    val hasDidAddress = (userHelper.getDIDAddress() != null) && (userHelper.getBackupStatus())

    fun registerState(): String? {
        println("registerState =>${userHelper.getRegisterState()}")
        return userHelper.getRegisterState()
    }


}