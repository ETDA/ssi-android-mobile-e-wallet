package co.finema.etdassi.feature.notification.data

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import co.finema.etdassi.common.utils.JWTUtils
import co.finema.etdassi.feature.notification.NotificationListAdapter
import co.finema.etdassi.feature.notification.NotificationStatus
import co.finema.etdassi.feature.sign.SignViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat

class NotificationRepository(
    private val notificationDao: NotificationDao
) {

    fun insertNotificationList(listNotification: List<NotificationListAdapter.NotificationAdapterModel>) {

        val data: List<NotificationModel> = listNotification.map {
            NotificationModel(
                vcType = it.vcName,
                status = it.status?.name,
                date = it.date,
                dateKey = it.dateKey,
                source = it.source,
                issuer = it.issuer ?:"",
                credentialSubject = it.credentialSubject,
                approveEndpoint = it.approveEndpoint,
                rejectEndpoint = it.rejectEndpoint,
                creator = it.creator
            )
        }

        notificationDao.insertNotificationList(data)

    }

    fun getNotificationList() = notificationDao.getNotificationList()

    fun getRequestToSignList() = notificationDao.getRequestToSignList(status = NotificationStatus.PENDING.name)

    fun getRequestToSignCount() = notificationDao.getRequestToSignCount(status = NotificationStatus.PENDING.name)

    fun getSignedCount() = notificationDao.getSignedCount(status = NotificationStatus.PENDING.name)

    fun countMainNavBadge() = notificationDao.countMainNavBadge()

    fun updateNotiReadStatus(notiId: String) {
        notificationDao.updateNotiById(isRead = true, notiId = notiId)
    }

    fun updateNotificationVCStatus(status: String, notiId: String) {
        notificationDao.updateVCStatusById(status = status, notiId = notiId)
    }

    fun getUnreadCount(): LiveData<Int> {
        return notificationDao.getUnReadCount()
    }

    private fun String?.convertDate(): String {
        val dateFormat = object :SimpleDateFormat("yyyy-mm-dd"){}
        return dateFormat.format(dateFormat.parse(this!!)?.time)
    }

    data class NotificationOriginalSource(
        @SerializedName("iss") val issuer: String?,
        @SerializedName("nonce") val nonce: String?,
        @SerializedName("sub") val sub: String?,
        @SerializedName("vc") val vc: JsonObject?,
        var approveEndpoint: String?,
        var rejectEndpoint: String?
    )

    fun getNotificationDataById(notiId: String): SignViewModel.SignDescriptionModel {
        val notiData = notificationDao.getNotificationById(notiId)
        val source = Gson().fromJson(notiData.source, NotificationOriginalSource::class.java)
        val attribute = notiData.credentialSubject?.let { JWTUtils.credentialSubjectToAttributeModel(Gson().fromJson(it, JsonObject::class.java)) }
        source.approveEndpoint =  notiData.approveEndpoint
        source.rejectEndpoint =  notiData.rejectEndpoint
        return SignViewModel.SignDescriptionModel(
            notificationId = notiData.id.toString(),
            didRequester = notiData.issuer,
            status = notiData.status?.let { NotificationStatus.valueOf(it) },
            vcType = notiData.vcType,
            vcDetail = attribute,
            source = source,
            creator = notiData.creator
        )
    }

    data class MainBadgeModel(
        @ColumnInfo(name = "count_noti") val countNoti: Int,
        @ColumnInfo(name = "count_signed") val countSigned: Int
    )



}