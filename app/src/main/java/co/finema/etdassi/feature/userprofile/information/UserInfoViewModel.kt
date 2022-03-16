package co.finema.etdassi.feature.userprofile.information

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.utils.subAsDIDAddress
import co.finema.etdassi.feature.userprofile.usecase.UserInformationUseCase

class UserInfoViewModel(
    private val userHelper: UserHelper,
    private val userInformationUseCase: UserInformationUseCase,
    private val keyHelper: KeyHelper
) : BaseViewModel() {


    private val _didAddress = MutableLiveData<String>()
    val didAddress: LiveData<String> get() = _didAddress

    private val _fullName = MutableLiveData<String>()
    val fullName: LiveData<String> get() = _fullName

    private val _deviceName = MutableLiveData<String>()
    val deviceName: LiveData<String> get() = _deviceName

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> get() = _location

    private val _timeLog = MutableLiveData<String>()
    val timeLog: LiveData<String> get() = _timeLog

    val didAddressFull = userHelper.getDIDAddress()

    init {
        setData()
        getUserInformation()
    }

    private fun getUserInformation() {
        val param = UserInformationUseCase.Param(
                did = userHelper.getDIDAddress() ?: "",
                keyHelper = keyHelper
        )
        userInformationUseCase.launch(viewModelScope, param) {
            it.either({ error ->
                error.printStackTrace()
            }, { response ->
                if (response) {
                    setData()
                } else {
                    Log.e("getUserInformation ", "response Fail")
                }
            })
        }
    }

    private fun setData() {
        val infoData = userHelper.getUserInformation()
        _didAddress.value = userHelper.getDIDAddress()
        _fullName.value = infoData?.fullName
        _deviceName.value = infoData?.device?.name
//        _location.value = "กรุงเทพมหานคร, ประเทศไทย"
//        _timeLog.value = "21 เม.ษ. 64 เวลา 15:22 น."
    }


}