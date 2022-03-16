package co.finema.etdassi.feature.pageloading

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.finema.etdassi.common.Constants
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.enum.Operations
import co.finema.etdassi.common.enum.RegisterState
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.KeyManager
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.utils.ApiListener
import co.finema.etdassi.common.utils.GenericListener
import co.finema.etdassi.common.utils.handleError
import co.finema.etdassi.feature.pageloading.usecase.DIDRecoveryUseCase
import co.finema.etdassi.feature.pageloading.usecase.GetBackupVCUseCase
import co.finema.etdassi.feature.pageloading.usecase.VerifyVPUseCase
import co.finema.etdassi.feature.qr_reader.QRTextResultModel
import co.finema.etdassi.feature.register.usecase.*
import co.finema.etdassi.feature.userprofile.usecase.UserInformationUseCase
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject

class LoadingViewModel(
    private val userHelper: UserHelper,
    private val keyManager: KeyManager,
    private val registerUserUseCase: RegisterUserUseCase,
    private val registerDIDUseCase: RegisterDIDUseCase,
    private val keyHelper: KeyHelper,
    private val registerUpdateUserUseCase: RegisterUpdateUserUseCase,
    private val updateFirebaseTokenUseCase: UpdateFirebaseTokenUseCase,
    private val requestSendEmailUseCase: RequestSendEmailUseCase,
    private val didRecoveryUseCase: DIDRecoveryUseCase,
    private val getBackupVCUseCase: GetBackupVCUseCase,
    private val verifyVPUseCase: VerifyVPUseCase
    ):BaseViewModel() {

    private val _didAddress = MutableLiveData<String>()
    fun registerState(): String? = userHelper.getRegisterState()

    fun getUserId() = userHelper.getUserId()

    fun createDid(registerUserPayload: RegisterUserUseCase.Param, param: ApiListener) {
//        setPayload(registerUserPayload, param)
        param.onSuccess()
//        registerDIDUseCase.launch(viewModelScope, keyHelper) {
//            it.either({ error ->
//                param.onFail(error.handleError())
//                error.printStackTrace()
//            }, { response ->
//                userHelper.saveDIDAddress(response.did)
//                _didAddress.value = response.did
//                param.onSuccess()
//            })
//        }
    }


    fun registerUser(registerUserPayload: RegisterUserUseCase.Param, param: ApiListener) {
            fun success(s: RegisterUserModel.ResponseRegisterUser) {
                userHelper.saveIdCardNo(registerUserPayload.idCardNo)
                userHelper.saveEmail(registerUserPayload.email)
                val userInfo = UserInformationUseCase.Response(
                    firstName = registerUserPayload.firstName,
                    lastName = registerUserPayload.lastName
                )
                userHelper.setUserInformation(userInfo)
                if(!s.userId.isNullOrBlank()) userHelper.setUserId(s.userId)
                if (!s.didAddress.isNullOrBlank()) {
                    userHelper.saveDIDAddress(s.didAddress)
                    userHelper.setStatusMustAddDevice()
                    userHelper.setRegisterState(RegisterState.REGISTER_MOBILE_SUCCESS.name)
                    param.onSuccess()
//                    sendOTPRecovery(param)
                } else {
                    userHelper.setRegisterState(RegisterState.REGISTER_MOBILE_SUCCESS.name)
                    param.onSuccess()
                }

            }

            fun fail(throwable: Throwable) {
                throwable.printStackTrace()
                param.onFail(throwable.handleError())
            }

            registerUserUseCase.launch(viewModelScope,registerUserPayload) {
                it.either(::fail, ::success)
            }

        }

    fun registerRecovery(param: ApiListener){
        fun success(s: DIDRecoveryUseCase.Response){
            if(!s.userId.isNullOrBlank()) userHelper.setUserId(s.userId)
            if(!s.didAddress.isNullOrBlank()) userHelper.saveDIDAddress(s.didAddress)
            userHelper.setRegisterState(RegisterState.RECOVERY_DID.name)
            updateFirebaseToken(param)
        }

        fun fail(throwable: Throwable) {
            param.onFail(throwable.handleError())
        }

        didRecoveryUseCase.launch(viewModelScope,userHelper.getUserId() ?:""){
            it.either(::fail,::success)
        }
    }

    fun getBackupWallet(param: GenericListener<GetBackupVCUseCase.ItemRestore>){
        userHelper.getDIDAddress()?.let {
            getBackupVCUseCase.launch(viewModelScope, it) { either ->
                either.either({
                    it.printStackTrace()
                    param.onFail(it.handleError())
                }, {
                    param.onSuccess(it)
                    Log.d("SDASDASDSA", Gson().toJson(it))
                })
            }
        }

    }

    private fun sendOTPRecovery(param: ApiListener) {
        requestSendEmailUseCase.launch(viewModelScope, RequestSendEmailUseCase.Param(Operations.RECOVERY.action)) {
            it.either({ throwable ->
                param.onFail(throwable.handleError())
            }, {
                userHelper.setRegisterState(RegisterState.REGISTER_MOBILE_SUCCESS.name)
                param.onSuccess()
            })
        }
    }

    fun createDID(param: ApiListener) {
        registerDIDUseCase.launch(viewModelScope, keyHelper) {
            it.either({ throwable ->
                param.onFail(throwable.handleError())
            }, { response ->
                userHelper.saveDIDAddress(response.did)
                userHelper.setRegisterState(RegisterState.REGISTER_DID_SUCCESS.name)
                updateUser(param)
            })
        }
    }

    fun updateUser(param: ApiListener) {
        val requestParam = RegisterUpdateUserUseCase.Param(userHelper.getUserId() ?:"",userHelper.getDIDAddress() ?:"")
        registerUpdateUserUseCase.launch(viewModelScope, requestParam) {
            it.either({ throwable ->
                param.onFail(throwable.handleError())
            }, { response ->
                if (response) {
                    userHelper.setRegisterState(RegisterState.UPDATE_USER_SUCCESS.name)
                    updateFirebaseToken(param)
                } else {
                    param.onFail(Constants.TRY_AGAIN_TEXT)
//                    updateUser(param)
                }
            })
        }
    }

    fun updateFirebaseToken(param: ApiListener) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                param.onFail(task.exception?.cause?.message?.substringAfter("\"")?.substringBeforeLast("\"") ?:Constants.TRY_AGAIN_TEXT)
                return@OnCompleteListener
            }

            task.result?.let { token ->
                val requestParam = UpdateFirebaseTokenUseCase.Param(
                    didAddress = userHelper.getDIDAddress() ?:"",
                    uuid = userHelper.getUUID() ?:"",
                    firebaseToken = token
                )
                updateFirebaseTokenUseCase.launch(viewModelScope, requestParam) {
                    it.either({ throwable ->
                        throwable.printStackTrace()
                        param.onFail(throwable.handleError())
                    }, {
                        userHelper.setRegisterState(RegisterState.REGISTER_EWALLET_SUCCESS.name)
                        param.onSuccess()
                    })
                }
            }

        })

    }

    fun qrVerify(qrTextResultModel: QRTextResultModel, genericListener: GenericListener<VerifyVPUseCase.QRVerifyResult>) {
        verifyVPUseCase.launch(viewModelScope, qrTextResultModel.endpoint) { either ->
            either.either({
                it.printStackTrace()
                genericListener.onFail(it.handleError())
            }, {
                genericListener.onSuccess(it)
            })
        }
    }

}