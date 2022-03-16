package co.finema.etdassi.feature.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class SSIFirebaseInstanceIDService : FirebaseMessagingService() {
//    private val userHelper: UserHelper by inject()
//    private val deepLinkHelper: DeepLinkHelper by inject()
//    private val updateDeviceUseCase: UpdateDeviceUseCase by inject()

    companion object{
        private const val TAG = "FirebaseInstanceIDService"
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG,"Token : $p0")
        sendToServer(p0)
    }

    private fun sendToServer(token:String){
//        if(!userHelper.getDidAddress().isNullOrBlank() && !userHelper.getUUID().isNullOrBlank()) {
//            updateDeviceUseCase.launch(
//                GlobalScope,
//                UpdateDeviceUseCase.Params(
//                    userHelper.getDidAddress()!!,
//                    userHelper.getUUID()!!,
//                    token
//                )
//            ) {
//                it.either({ throwable ->
//                    throwable.printStackTrace()
//                }, { data ->
//                    Log.d("updaateFcm", data.toString())
//                })
//            }
//        }
    }
}