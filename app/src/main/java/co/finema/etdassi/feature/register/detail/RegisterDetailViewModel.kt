package co.finema.etdassi.feature.register.detail

import androidx.lifecycle.MutableLiveData
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.utils.UrlUtil
import co.finema.etdassi.feature.register.usecase.RegisterUserUseCase

class RegisterDetailViewModel(): BaseViewModel() {
    val firstName = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()
    val birthDate = MutableLiveData<String>()
    val cardIdNumber = MutableLiveData<String>()
    val backCardId = MutableLiveData<String>()
    val email = MutableLiveData<String>()

    fun buildPayload(): RegisterUserUseCase.Param {
        return RegisterUserUseCase.Param(
            firstName = firstName.value ?: "",
            lastName = lastName.value ?:"",
            dateOfBirth = birthDate.value ?:"",
            idCardNo = cardIdNumber.value ?:"",
            laserId = backCardId.value ?:"",
            url = UrlUtil.REGISTER_EWALLET.path,
            email = email.value ?:""
                                        )
    }

}