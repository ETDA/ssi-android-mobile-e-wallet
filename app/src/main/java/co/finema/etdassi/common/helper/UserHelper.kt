package co.finema.etdassi.common.helper

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import co.finema.etdassi.BuildConfig
import co.finema.etdassi.common.enum.DoActionEnum
import co.finema.etdassi.common.enum.RegisterState
import co.finema.etdassi.feature.userprofile.usecase.UserInformationUseCase
import com.google.gson.Gson
import kotlin.math.PI

class UserHelper(
    private val context: Context
) {
    companion object {
        const val KEY_TOKEN = "app_token"
        const val KEY_PROFILE = "app_profile"
        const val KEY_PASSCODE_SIGN = "app_passcode"
        const val KEY_USE_FINGER_PRINT = "app_finger_print"
        const val KEY_LAST_LOGIN = "app_last_login"
        const val KEY_FORCE_LOGIN = "app_force_login"
        const val KEY_UUID = "app_uuid"
        const val ID_CARD_NO = "id_card_no"
        const val DID_ADDRESS = "did_address"
        const val PIN_CODE = "pin_code"
        const val BIOMETRIC_ENABLE = "biometric_enable"
        const val BACKUP_STATUS = "backup_status"
        const val REGISTER_STATE = "register_state"
        const val USER_ID = "user_id"
        const val EMAIL = "email"
        const val USER_INFORMATION ="user_information"
        const val ADD_DEVICE_IN_UPDATE_DID = "add_device_in_update_did"



        const val DO_ACTION_NEXT = "do_action_next"
        const val TIME_DIFFERENT: Long = 10 * 60 * 1000 // 10 mins
    }

    private val sharePref =
        context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

    fun hasToken(): Boolean {
        return !getToken().isNullOrBlank()
    }

    fun saveToken(token: String) {
        sharePref.edit().putString(KEY_TOKEN, token).apply()
    }

    fun setPasscode(signedCode: String) {
        sharePref.edit().putString(KEY_PASSCODE_SIGN, signedCode).apply()
    }

    fun hasPasscode(): Boolean {
        return sharePref.contains(KEY_PASSCODE_SIGN)
    }

    fun checkPasscode(code: String, keyHelper: KeyHelper): Boolean {
        if (!hasPasscode()) {
            return false
        }

        return keyHelper.verifySignature(code, sharePref.getString(KEY_PASSCODE_SIGN, "")!!.hexToByteArray())
    }

    fun getToken(): String? {
        return sharePref.getString(KEY_TOKEN, null)
    }

    fun setCountKey(count_key_: Int) {
        sharePref.edit().putInt(KEY_PROFILE, count_key_)
    }

    fun getCountKey(): Int {
        return sharePref.getInt(KEY_PROFILE, 0)
    }

    fun saveUUID(uuid: String){
        sharePref.edit().putString(KEY_UUID, uuid).apply()
    }

    fun getUUID():String?{
        return sharePref.getString(KEY_UUID,null)
    }

    fun saveIdCardNo(idCardNo: String) {
        sharePref.edit().putString(ID_CARD_NO, idCardNo).apply()
    }

    fun getIdCardNo(): String? {
        return sharePref.getString(ID_CARD_NO, null)
    }

    fun saveDIDAddress(didAddress: String) {
        sharePref.edit().putString(DID_ADDRESS, didAddress).apply()
    }

    fun getDIDAddress(): String? {
        return sharePref.getString(DID_ADDRESS, null)
    }

    fun savePinCode(pinCode: String) {
        sharePref.edit().putString(PIN_CODE, pinCode).apply()
    }

    fun getPinCode(): String? {
        return sharePref.getString(PIN_CODE, null)?.trim()
    }

    fun hasPinCode(): Boolean {
        return sharePref.getString(PIN_CODE, null)?.let {
            true
        } ?: kotlin.run {
            false
        }
    }

    fun verifyPinCode(pinCode: String): Boolean {
        val oldCode = sharePref.getString(PIN_CODE, null)?.trim()
        return  oldCode == pinCode.trim()
    }

    fun clearPincode() {
        sharePref.edit().remove(PIN_CODE).apply()
    }

    fun saveBiometricEnable(status: Boolean) {
        sharePref.edit().putBoolean(BIOMETRIC_ENABLE, status).apply()
    }

    fun getBiometricEnable(): Boolean {
        return sharePref.getBoolean(BIOMETRIC_ENABLE, false)
    }

    fun setBackupStatus(status: Boolean) {
        sharePref.edit().putBoolean(BACKUP_STATUS, status).apply()
    }

    fun getBackupStatus(): Boolean {
        return sharePref.getBoolean(BACKUP_STATUS, false)
    }

    fun setRegisterState(state: String) {
        sharePref.edit().putString(REGISTER_STATE, state).apply()
    }

    fun getRegisterState(): String? {
        return sharePref.getString(REGISTER_STATE, null)
    }

    fun setUserId(userId: String?) {
        sharePref.edit().putString(USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return sharePref.getString(USER_ID, null)
    }

    fun setUserInformation(userInfo: UserInformationUseCase.Response) {
        val oldInfo = getUserInformation()
        val userInfoBundle = oldInfo?.copy(
            didAddress = userInfo.didAddress ?: oldInfo.didAddress,
            idCardNo = userInfo.idCardNo ?: oldInfo.idCardNo,
            firstName = userInfo.firstName ?: oldInfo.firstName,
            lastName = userInfo.lastName ?: oldInfo.lastName,
            device = userInfo.device,
            registerDate = userInfo.registerDate ?: oldInfo.registerDate,
            email = userInfo.email ?: oldInfo.email
        ) ?: userInfo
        val stringBundle = Gson().toJson(userInfoBundle)
        sharePref.edit().putString(USER_INFORMATION, stringBundle).apply()
    }
    fun saveEmail(email: String) {
        sharePref.edit().putString(EMAIL, email).apply()
    }

    fun getUserInformation(): UserInformationUseCase.Response? {
        val stringUserInfo = sharePref.getString(USER_INFORMATION, null)
        return if (stringUserInfo == null) {
            null
        } else {
            Gson().fromJson(stringUserInfo, UserInformationUseCase.Response::class.java)
        }
    }


    fun getEmail(): String? {
        return sharePref.getString(EMAIL, null)
    }

    fun setActionNext(action: DoActionEnum) {
        sharePref.edit().putString(DO_ACTION_NEXT, action.name).apply()
    }

    fun getActionNext(): DoActionEnum? {
        val actionNext = sharePref.getString(DO_ACTION_NEXT, null)
        return DoActionEnum.values().firstOrNull { it.name == actionNext }
    }

    fun clearActionNext() {
        sharePref.edit().remove(DO_ACTION_NEXT).apply()
    }

    fun setStatusMustAddDevice() {
        sharePref.edit().putBoolean(ADD_DEVICE_IN_UPDATE_DID, true).apply()
    }

    fun getStatusMustAddDevice(): Boolean {
        return sharePref.getBoolean(ADD_DEVICE_IN_UPDATE_DID, false)
    }

    fun clearStatusMustAddDevice() {
        sharePref.edit().remove(ADD_DEVICE_IN_UPDATE_DID).apply()
    }


}