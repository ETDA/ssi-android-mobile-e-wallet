package co.finema.etdassi.feature.passcode

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.helper.UserHelper
import com.google.common.hash.Hashing
import java.nio.charset.StandardCharsets

class PinCodeViewModel(private val userHelper: UserHelper):BaseViewModel() {
    var testMultiViewModelValue = ""

    var passCode: String = ""
    var firstPinCode: String? = ""

    private val _activePasscode: MutableLiveData<Int> = MutableLiveData()
    val activePasscode: LiveData<Int> get() = _activePasscode

    private val _passcodeDone = MutableLiveData<Boolean>()
    val passcodeDone: LiveData<Boolean> get() = _passcodeDone

    private val _passcodeWrong = MutableLiveData<Boolean>()
    val passcodeWrong: LiveData<Boolean> get() = _passcodeWrong

    private val _biometricEnableStatus = MutableLiveData<Boolean>()
    val biometricEnableStatus: LiveData<Boolean> get() = _biometricEnableStatus

    fun appendCode(s: String) {
        _passcodeWrong.value = false
        passCode += s
        _activePasscode.value = passCode.length
        _passcodeDone.value = passCode.length == 6
    }

    fun backSpace() {
        if (passCode.length < 6 && passCode.isNotEmpty()) {
            passCode = passCode.drop(1)
            _activePasscode.value = passCode.length
        }
    }

    fun hashPass(): String {
        val sha256hex = Hashing.sha256()
            .hashString(passCode, StandardCharsets.UTF_8)
            .asBytes()

        return Base64.encodeToString(sha256hex, Base64.DEFAULT)
    }

    private fun String.hashThis(): String {
        val sha256hex = Hashing.sha256()
            .hashString(this, StandardCharsets.UTF_8)
            .asBytes()

        return Base64.encodeToString(sha256hex, Base64.DEFAULT)
    }

    fun clear() {
        if (passCode.length == 6) {
            passCode = ""
            _activePasscode.value = passCode.length
        }
    }

    private fun String.verifySetPasscode(): Boolean {
        return (this == firstPinCode) && firstPinCode != null
    }

    fun savePincode(): Boolean {
        val confirmPinCode = hashPass()
        return if (confirmPinCode.verifySetPasscode()) {
            _passcodeWrong.value = false
            userHelper.savePinCode(confirmPinCode)
            true
        } else {
            _passcodeWrong.value = true
            false
        }
    }

    fun changePinCode(){
        val confirmPinCode = passCode.hashThis()
        userHelper.savePinCode(confirmPinCode)
        _passcodeWrong.value = false
    }

    private fun String.verifyPinCodeValid(): Boolean {
        return userHelper.verifyPinCode(this)
    }

    fun verifyPinCode(): Boolean {
        return if (hashPass().verifyPinCodeValid()) {
            _passcodeWrong.value = false
            true
        } else {
            _passcodeWrong.value = true
            false
        }
    }

    fun isBiometricEnable() {
        println("isBiometricEnable => ${userHelper.getBiometricEnable()}")
        _biometricEnableStatus.value = userHelper.getBiometricEnable()
    }

}