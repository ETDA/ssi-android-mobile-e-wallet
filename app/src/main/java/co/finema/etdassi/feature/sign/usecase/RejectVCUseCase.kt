package co.finema.etdassi.feature.sign.usecase

import co.finema.etdassi.common.base.SimpleCoroutineUseCase
import co.finema.etdassi.common.helper.KeyHelper
import co.finema.etdassi.common.helper.replaceDataNewLineJson
import co.finema.etdassi.common.repository.Api
import co.finema.etdassi.common.repository.CallApi
import co.finema.etdassi.common.utils.BuildJson
import co.finema.etdassi.common.utils.DateConvertUtil.toISOFormat
import co.finema.etdassi.common.utils.SigningData.Companion.toBase64
import co.finema.etdassi.common.utils.buildJson
import co.finema.etdassi.feature.mysign.data.MyRejectRepository
import co.finema.etdassi.feature.mysign.myreject.MyRejectModel
import co.finema.etdassi.feature.notification.NotificationStatus
import co.finema.etdassi.feature.notification.data.NotificationRepository
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.*

class RejectVCUseCase(
    private val callApi: CallApi,
    private val keyHelper: KeyHelper,
    private val notificationRepository: NotificationRepository,
    private val rejectRepository: MyRejectRepository
): SimpleCoroutineUseCase<RejectVCUseCase.Param, Boolean>() {

    data class Param(
        val rejectEndpoint: String,
        val comment: String,
        val notificationId: String,
        val issuer: String,
        val vcType: String
    )

    data class RejectRequest(
        @SerializedName("reason") val reason: String
    ): BuildJson

    override suspend fun executes(param: Param): Boolean {
        return withContext(Dispatchers.IO) {
            val request = RejectRequest(param.comment).buildJson().toBase64()
            val response = callApi.call(replaceDataNewLineJson(keyHelper.sign(request))).rejectVC(
                url = param.rejectEndpoint,
                body = Api.RequestMessage(request)
            ).blockingGet()
            if (response.status == "success") {
                val currentMoment: Instant = Clock.System.now()
                notificationRepository.updateNotificationVCStatus(NotificationStatus.REJECT.name, param.notificationId)
                val createReject = rejectRepository.createMyReject(
                    MyRejectModel(
                        name = param.vcType,
                        rejectDate = currentMoment.toString(),
                        notificationId = param.notificationId,
                        issuer = param.issuer,
                        status = NotificationStatus.REJECT.name,
                        reason = param.comment
                    )
                )
                createReject > 0
            } else {
                false
            }
        }
    }


}