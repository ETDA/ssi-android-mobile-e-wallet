package co.finema.etdassi.feature.register.usecase

import android.util.Base64
import android.util.Log
import co.finema.etdassi.common.Constants
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.enum.Operations
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.replaceDataNewLine
import co.finema.etdassi.common.helper.replaceDataNewLineJson
import co.finema.etdassi.common.repository.Api
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.common.utils.BuildJson
import co.finema.etdassi.common.utils.SigningData
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

class BackupWalletUseCase(
    val api : CallApi
                         ) : SimpleCoroutineUseCase<BackupWalletUseCase.Param, Boolean>(){
    override suspend fun executes(param: Param): Boolean {

        return withContext(Dispatchers.IO) {
            val recoverer = api.call().registerDIDRecovery().blockingGet()
            val nonce = api.call().getNonce(param.did).blockingGet()

            val recovererRequest = RecovererRequest(
                didAddress = param.did,
                recoverer = recoverer.rocoverer,
                nonce = nonce.nonce
            )

            Log.d("BackupWalletUseCase recovererRequest", recovererRequest.toString())

            val recovererSignRequest = SigningData.build(param.keyHelper, recovererRequest)

            val didDoc = api.call(signature = recovererSignRequest.signature).addRecovererId(param.did, recovererSignRequest.message).blockingGet()

            val dataVCWallet = DataVCWallet(
                operation = Operations.CREATE_WALLET.action,
                did_address = didDoc.did
            )

            Log.d("BackupWalletUseCase dataVCWallet", "$dataVCWallet")

            val dataRegisterVCWallet = SigningData.build(param.keyHelper, dataVCWallet)

            val response = api.call(dataRegisterVCWallet.signature).createBackupWallet(dataRegisterVCWallet.message).blockingGet()

            response != null
        }

    }

    data class RegisterDIDRecoveryRequest(
        @SerializedName("did_address") val didAddress: String
    )

    data class RegisterDIDRecoveryResponse(
        @SerializedName("did_address") val rocoverer: String
    )

    data class NonceResponse(
        @SerializedName("nonce") val nonce: String
    )

    data class RecovererRequest(
        @SerializedName("operation") val operation: String = Operations.DID_RECOVERER_ADD.action,
        @SerializedName("did_address") val didAddress: String,
        @SerializedName("recoverer") val recoverer: String,
        @SerializedName("nonce") val nonce: String
    ): BuildJson

    data class RecovererSignRequest(
        val message: String? = null,
        val signature: String? = null
    ) {
        companion object {
            fun build(
                keyManager: KeyHelper,
                data: RecovererRequest
            ): RecovererSignRequest {
                return build(keyManager, data.toString())
            }

            private fun build(keyManager: KeyHelper, data: String): RecovererSignRequest {
                val message = Base64.encodeToString(data.toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT)
                val messages = replaceDataNewLine(message)
                val dataSign = keyManager.sign(messages)
                return RecovererSignRequest(
                    message = messages,
                    signature = dataSign
                )
            }
        }
    }

    data class DataVCWallet(
        val did_address : String,
        val operation: String
    ): BuildJson

    data class DataRegisterVCWallet(
        val message: String? = null,
        val signature: String? =null
    ): BuildJson


    data class Param(
        val did : String,
        val keyHelper: KeyHelper
    )


}