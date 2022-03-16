package co.finema.etdassi.feature.userprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.helper.UserHelper

class UserProfileViewModel(private val userHelper: UserHelper) : BaseViewModel() {

    private val _backupStatus = MutableLiveData<Boolean>()
    val backupStatus: LiveData<Boolean> get() = _backupStatus

    init {
        _backupStatus.value = userHelper.getBackupStatus()
    }

    fun refreshBackupStatus() {
        _backupStatus.value = userHelper.getBackupStatus()
    }

}