package co.finema.etdassi.feature.register.usecase

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Base64
import android.util.Log
import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.helper.replaceDataNewLine
import co.finema.etdassi.common.helper.replaceDataNewLineJson
import co.finema.etdassi.common.repository.Api
import co.finema.etdassi.common.repository.CallApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import java.util.*

class RegisterDeviceUseCase(
    val context: Context,
    val userHelper: UserHelper,
    val api : CallApi
) : SimpleCoroutineUseCase<RegisterDeviceUseCase.Param, Api.ResponseRegisterDevice>() {
    override suspend fun executes(param: Param): Api.ResponseRegisterDevice {
        return withContext(Dispatchers.IO) {
            val url = "/mobile"

            val myDevice = BluetoothAdapter.getDefaultAdapter()

            val uuid = getUUID(context)!!
            userHelper.saveUUID(uuid)

            val dataDetailDevice =DataDetailDevice(
                name = myDevice.name,
                os_version = Build.VERSION.RELEASE,
                model = Build.MODEL,
                uuid = uuid
            )

            val dataDevice = DataDevice(
                did_address = param.did,
                device = dataDetailDevice
            )

            val dataRegisterDevice = DataRegisterDevice.build(
                param.currentKey,
                dataDevice
            )

            val message = replaceDataNewLineJson(dataRegisterDevice.message)
            val signature = replaceDataNewLineJson(dataRegisterDevice.signature)

            api.call(signature).registerDevice(url, Api.RequestMessage(message)).blockingGet()
        }
    }

    data class DataDevice(
        val did_address : String,
        val device : DataDetailDevice
    ){
        fun toJson(): String = Gson().toJson(this)
        fun checkJson(): String{
            val gson = GsonBuilder().disableHtmlEscaping().create()
            val json = gson.toJson(this)
            var disJson = json.replace("\\n","")
            disJson= disJson.replace("-----BEGIN PUBLIC KEY-----","-----BEGIN PUBLIC KEY-----\\n")
            disJson = disJson.replace("-----END PUBLIC KEY-----","\\n-----END PUBLIC KEY-----")
            return disJson
        }
        override fun toString(): String = checkJson()
    }

    data class DataDetailDevice(
        val name : String,
        val os : String = "Android",
        val os_version : String,
        val model : String,
        val uuid : String
    )

    data class Param(
        val did : String,
        val currentKey: KeyHelper
    )

    data class DataRegisterDevice(
        val message: String? = null,
        val signature: String? =null
    ){
        companion object {
            fun build(
                keyManager: KeyHelper,
                dataDevice: DataDevice
            ): DataRegisterDevice {
                return build(
                    keyManager,
                    dataDevice.toString()
                )
            }

            private fun build(
                keyManager: KeyHelper,
                data: String
            ): DataRegisterDevice {
                val message = Base64.encodeToString(
                    data.toByteArray(StandardCharsets.UTF_8),
                    Base64.DEFAULT
                )
                val messages =
                    replaceDataNewLine(
                        message
                    )
                val dataSign = keyManager.sign(messages)


                return DataRegisterDevice(
                    signature = dataSign,
                    message = messages
                )
            }
        }
    }

    private fun getUUID(context: Context): String?{
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//        val uuid = UUID(androidId.hashCode().toLong(),)
//        val deviceId =  uuid.toString()
        return UUID.randomUUID().toString()
    }


}