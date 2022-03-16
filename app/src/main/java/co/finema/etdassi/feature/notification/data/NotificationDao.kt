package co.finema.etdassi.feature.notification.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotificationList(notificationList: List<NotificationModel>)

    @Query("SELECT * FROM notification")
    fun getNotificationList(): LiveData<List<NotificationModel>>

    @Query("UPDATE notification SET read_status = :isRead WHERE id=:notiId")
    fun updateNotiById(isRead: Boolean, notiId: String)

    @Query("SELECT COUNT(id) FROM notification WHERE read_status = 0")
    fun getUnReadCount(): LiveData<Int>

    @Query("SELECT * FROM notification WHERE id=:notiId LIMIT 1")
    fun getNotificationById(notiId: String): NotificationModel

    @Query("UPDATE notification SET vc_status =:status WHERE id=:notiId")
    fun updateVCStatusById(status: String, notiId: String)

    @Query("SELECT * FROM notification WHERE vc_status=:status")
    fun getRequestToSignList(status: String): LiveData<List<NotificationModel>>

    @Query("SELECT COUNT(id) FROM notification WHERE vc_status=:status")
    fun getRequestToSignCount(status: String): LiveData<Int>

    @Query("SELECT COUNT(id) FROM notification WHERE vc_status!=:status")
    fun getSignedCount(status: String): LiveData<Int>

    @Query("SELECT (SELECT COUNT(id) FROM notification WHERE read_status = 0) as count_noti, (SELECT COUNT(id) FROM vc_signed WHERE read_status = 0) as count_signed")
    fun countMainNavBadge(): LiveData<NotificationRepository.MainBadgeModel>

}