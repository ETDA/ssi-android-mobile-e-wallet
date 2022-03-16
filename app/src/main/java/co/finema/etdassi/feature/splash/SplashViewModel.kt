package co.finema.etdassi.feature.splash

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.enum.RegisterState
import co.finema.etdassi.common.helper.UserHelper

class SplashViewModel(
    private val userHelper: UserHelper
):BaseViewModel() {

    private val _userRegister = MutableLiveData<Boolean>()
    val userRegister: LiveData<Boolean> get() = _userRegister

    init {
        getRegisterStatus()
    }

    private fun getRegisterStatus() {
        println("getRegisterStatus => ${userHelper.getRegisterState()}")
        _userRegister.value = userHelper.getRegisterState() == RegisterState.REGISTER_SUCCESS.name
    }

}