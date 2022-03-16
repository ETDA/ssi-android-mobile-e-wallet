package co.finema.etdassi.feature.mainpager

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.finema.etdassi.common.enum.RegisterState
import co.finema.etdassi.common.helper.UserHelper

class MainPagerViewModel(
    private val userHelper: UserHelper
):ViewModel() {

    val isResetDID = userHelper.getRegisterState() == RegisterState.RESET_DID_SUCCESS.name

    fun setBiometricEnable() {
        userHelper.saveBiometricEnable(true)
    }

    fun getDIDAddress(): String? = userHelper.getDIDAddress()

    fun clearPincode() {
        userHelper.clearPincode()
    }

    fun setRegisterStateBackup() {
        userHelper.setRegisterState(RegisterState.BACKUP_WALLET_SUCCESS.name)
    }

    fun setBackupStatus(): String {
        val currentStatus = userHelper.getBackupStatus()
        userHelper.setBackupStatus(!currentStatus)
        return if (!currentStatus) {
            "เปิดใช้งานการสำรองข้อมูลสำเร็จ"
        } else {
            "ปิดใช้งานการสำรองข้อมูลสำเร็จ"
        }
    }

    fun getBackupStatus(): Boolean {
        return userHelper.getBackupStatus()
    }

    fun setRegisterFinishState() {
        userHelper.setRegisterState(RegisterState.REGISTER_SUCCESS.name)
    }

}