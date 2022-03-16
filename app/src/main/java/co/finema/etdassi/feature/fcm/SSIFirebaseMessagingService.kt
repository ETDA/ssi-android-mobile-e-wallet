package co.finema.etdassi.feature.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.PowerManager
import android.util.Base64
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import co.finema.etdassi.R
import co.finema.etdassi.common.helper.UserHelper
import co.finema.etdassi.common.utils.DateConvertUtil.toLocalFormat
import co.finema.etdassi.feature.MainActivity
import co.finema.etdassi.feature.fcm.usecase.UpdateFirebaseTokenFCMUseCase
import co.finema.etdassi.feature.notification.NotificationListActivity
import co.finema.etdassi.feature.notification.NotificationListAdapter
import co.finema.etdassi.feature.notification.NotificationStatus
import co.finema.etdassi.feature.notification.data.NotificationRepository
import co.finema.etdassi.feature.register.RegisterActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat


class SSIFirebaseMessagingService: FirebaseMessagingService() {
//    private val userHelper: UserHelper by inject()
//    private val deepLinkHelper: DeepLinkHelper by inject()
//    private val updateDeviceUseCase: UpdateDeviceUseCase by inject()

    private val notificationRepository: NotificationRepository by inject()
    private val userHelper: UserHelper by inject()
    private val updateFirebaseTokenUseCase: UpdateFirebaseTokenFCMUseCase by inject()
    companion object {
        private const val TAG = "FirebaseMessagingService"
    }

    private fun String?.convertDate(): String {
        val dateFormat = object : SimpleDateFormat("yyyy-mm-dd"){}
        return dateFormat.format(dateFormat.parse(this!!)?.time)
    }



    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("Check_data_FCM","From : ${Gson().toJson(remoteMessage.toIntent().extras)}")


        if(remoteMessage.data.isNotEmpty()){
            Log.e("Firebase.Fcm", remoteMessage.data.toString())

            remoteMessage.data["message"]?.let {
                val messageDecode = String(Base64.decode(it, Base64.NO_WRAP))
                Log.d("Message", messageDecode)
                val messageModel = Gson().fromJson(messageDecode, FirebaseNotificationModel::class.java)
                messageModel?.let { noti ->
                    val dateStamp = System.currentTimeMillis().toLocalFormat()
                    val preData = NotificationListAdapter.NotificationAdapterModel(
                        vcName = noti.vc?.type?.lastOrNull(),
                        status = NotificationStatus.PENDING,
                        date = dateStamp,
                        dateKey = dateStamp.convertDate(),
                        source = messageDecode,
                        issuer = noti.issuer,
                        credentialSubject = Gson().toJson(noti.vc?.credentialSubject),
                        approveEndpoint = remoteMessage.data["approve_endpoint"],
                        rejectEndpoint = remoteMessage.data["reject_endpoint"],
                        creator = remoteMessage.data["creator"]
                    )
                    notificationRepository.insertNotificationList(listOf(preData))

                }
            }




            if(/* Check if data needs to be processed by long running job */ true){
//                scheduleJob()
                val deeplink = remoteMessage.data["data"]
                val desc1 = remoteMessage.data["desc1"]
                val desc2 = remoteMessage.data["desc2"]

                if(deeplink !=null && desc1 != null && desc2 != null){
//                    deepLinkHelper.saveDeepLink(deeplink, desc1, desc2)
                }
                Log.e("Check_data_Firebase",deeplink+" \n $desc1 \n $desc2")

                wakeUpScreen()
                sendNotification(remoteMessage.data)
            }else{
                handleNow()
            }

        } else {
            remoteMessage.notification?.let { notificationWithoutPayload(it) }
        }

//        val uri = Uri.parse(deeplink)
//        val paths = uri.pathSegments

//        Log.e("deeplink", "$deeplink : (${paths.joinToString(separator = ",")})")

        remoteMessage.notification?.let {

            Log.e("Check_data_Firebase2", "Message Notification Body ${it.body} \n Action ${it.clickAction}")
        }





        super.onMessageReceived(remoteMessage)

    }

    private fun notificationWithoutPayload(messageBody: RemoteMessage.Notification) {
        val groupType = "DEFAULT"
        val intent = if (userHelper.getDIDAddress() != null) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, RegisterActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.ssi_logo)
//            .setContentTitle(getString(R.string.fcm_message))
//            .setPriority(2)
//            .setDefaults(Notification.DEFAULT_ALL)
//            .setAutoCancel(true)
//            .setSound(defaultSoundUri)
//            .setContentIntent(pendingIntent)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(messageBody?.title.toString())
            .setContentText(messageBody?.body.toString())
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .setGroup(groupType)
            .setGroupSummary(true)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle("My notification")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Much longer text that cannot fit one line..."))
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody?.body.toString()))
            .setContentIntent(pendingIntent)
            .setColor(getColor(R.color.primary))


//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
//        NotificationCompat.createNotificationChannel(channel)
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
        NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notificationBuilder.build())

//        notificationManager.notify(System.currentTimeMillis().toInt() /* ID of notification */, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.channel_name)
//            val descriptionText = getString(R.string.channel_description)
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//            // Register the channel with the system
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
    }

    override fun onDeletedMessages() {
        Log.e(TAG,"CHECK Delete Message")
        super.onDeletedMessages()
        Log.e(TAG,"CHECK Delete Message1")
    }

    @SuppressLint("InvalidWakeLockTag")
    private fun wakeUpScreen() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn = pm.isScreenOn
        Log.e("screen on......", "" + isScreenOn)
        if (isScreenOn == false) {
            val wl = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                "MyLock"
            )
            wl.acquire(10000)
            val wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock")
            wl_cpu.acquire(10000)
        }
    }

//    private fun scheduleJob(){
//        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))
//        val myJob = dispatcher.newJobBuilder()
//            .setService(SSIJobService::class.java)
//            .setTag(SSIJobService::class.java.name)
//            .setRecurring(true)
//            .setTrigger(Trigger.executionWindow(0, 60))
//            .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
//            .setReplaceCurrent(false)
//            .setConstraints(Constraint.ON_ANY_NETWORK)
//            .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
//            .build()
//        dispatcher.mustSchedule(myJob)
////        val work = OneTimeWorkRequest.Builder(MyJobService::class.java).build()
////        WorkManager.getInstance().beginWith(work).enqueue()
//    }

    private fun handleNow() {
        Log.e(TAG, "Short lived task is done.")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("SSI Token",token)
        val job = SupervisorJob()
        CoroutineScope(job).launch {
            updateFirebaseTokenUseCase.launch(this, token) { either ->
                either.either({
                    it.printStackTrace()
                },{

                })
            }
        }

    }

    private fun sendNotification(messageBody: MutableMap<String, String>) {
        val intent = if (userHelper.getDIDAddress() != null) {
            Intent(this, NotificationListActivity::class.java)
        } else {
            Intent(this, RegisterActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_noti)
            .setContentTitle(messageBody["title"])
            .setContentText(messageBody["body"])
            .setPriority(2)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        val channel = NotificationChannel(channelId,
            "Sign VC Channel",
            NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }



}