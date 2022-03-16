package co.finema.etdassi.feature.register.ewallet

import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.Constants
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.enum.RegisterState
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.utils.ApiListener
import co.finema.etdassi.common.utils.handleError
import co.finema.etdassi.feature.register.usecase.BackupWalletUseCase
import co.finema.etdassi.feature.register.usecase.RegisterDIDUseCase
import co.finema.etdassi.feature.register.usecase.RegisterUpdateUserUseCase

class ProveIdentViewModel(
    private val userHelper: UserHelper,
    private val keyHelper: KeyHelper,
    private val backupWalletUseCase: BackupWalletUseCase
                         ): BaseViewModel() {

    fun backupEwallet(param: ApiListener) {
        val registerEwalletParam = BackupWalletUseCase.Param(
            did = userHelper.getDIDAddress() ?:"",
            keyHelper = keyHelper)

        backupWalletUseCase.launch(viewModelScope, registerEwalletParam) {
            it.either({ error ->
                param.onFail(error.handleError())
                error.printStackTrace()
            }, { responseStatus ->
                if (responseStatus) {
                    userHelper.setRegisterState(RegisterState.BACKUP_WALLET_SUCCESS.name)
                    userHelper.setBackupStatus(true)
                    param.onSuccess()
                } else {
                    param.onFail(Constants.TRY_AGAIN_TEXT)
                }
            })
        }
    }
}