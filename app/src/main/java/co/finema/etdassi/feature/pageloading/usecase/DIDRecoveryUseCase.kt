package co.finema.etdassi.feature.pageloading.usecase

import android.bluetooth.BluetoothAdapter
import android.os.Build
import android.util.Base64
import android.util.Log
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.helper.replaceDataNewLine
import co.finema.etdassi.common.helper.replaceDataNewLineJson
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.common.utils.SigningData
import co.finema.etdassi.feature.register.usecase.BackupWalletUseCase
import co.finema.etdassi.feature.register.usecase.RegisterUserModel
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import java.util.*

class DIDRecoveryUseCase(
    private val callApi: CallApi,
    private val keyHelper: KeyHelper,
    private val userHelper: UserHelper
):SimpleCoroutineUseCase<String, DIDRecoveryUseCase.Response>() {

//    data class Request(
//        @SerializedName("new_key") val newKey: String,
//        @SerializedName("signature") val signature: String,
//        @SerializedName("controller") val controller: String
//    )

    data class RequestRecoveryUser(
        @SerializedName("new_key") val newKey: RequestNewKey,
        @SerializedName("device") val device: RegisterUserModel.DeviceModel
    )

    data class RequestNewKey(
        @SerializedName("signature") val signature: String,
        @SerializedName("public_key") val public_key: String,
        @SerializedName("key_type") val key_type: String,
    )

    data class Response(
        @SerializedName("did_address") val didAddress: String,
        @SerializedName("user_id") val userId: String,
    )

    override suspend fun executes(param: String): Response {
        return withContext(Dispatchers.IO) {

            val requestNewKey = RequestNewKey(
                signature = replaceDataNewLineJson(keyHelper.sign(keyHelper.pem)),
                public_key = keyHelper.pem,
                key_type = "EcdsaSecp256r1VerificationKey2019"
            )

            val uuid = userHelper.getUUID() ?: kotlin.run {
                val uuidCreated = UUID.randomUUID().toString()
                userHelper.saveUUID(uuidCreated)
                uuidCreated
            }

            val deviceModel = RegisterUserModel.DeviceModel(
                name = BluetoothAdapter.getDefaultAdapter().name,
                osVersion = Build.VERSION.RELEASE,
                model = Build.MODEL,
                uuid = uuid
            )

            val requestBody = RequestRecoveryUser(
                newKey = requestNewKey,
                device = deviceModel
            )

            Log.d("DIDRecoveryUseCase", " $requestBody")

            val recovery = callApi.call().didRecovery(param, requestBody).blockingGet()
            val recoverer = callApi.call().registerDIDRecovery().blockingGet()
            val nonce = callApi.call().getNonce(userHelper.getDIDAddress()!!).blockingGet()

            val recovererRequest = BackupWalletUseCase.RecovererRequest(
                didAddress = userHelper.getDIDAddress()!!,
                recoverer = recoverer.rocoverer,
                nonce = nonce.nonce
            )

            Log.d("BackupWalletUseCase recovererRequest", recovererRequest.toString())

            val recovererSignRequest = SigningData.build(keyHelper, recovererRequest)

            val recover = callApi.call(signature = recovererSignRequest.signature).addRecovererId(userHelper.getDIDAddress()!!, recovererSignRequest.message).blockingGet()
            userHelper.setBackupStatus(true)
            Response(recover.did, recovery.userId)
        }

    }


}