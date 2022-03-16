package co.finema.etdassi.feature.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.enum.VCBaseViewType
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDate
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import co.finema.etdassi.databinding.ListNotificationTypeOneBinding
import co.finema.etdassi.databinding.ListNotificationTypeTwoBinding
import co.finema.etdassi.feature.notification.data.NotificationModel

class NotificationListAdapter: BaseAdapter<NotificationListAdapter.NotificationAdapterModel>(notificationListener) {

    data class NotificationAdapterModel(
        val notificationId: String? = null,
        val vcName: String? = null,
        val status: NotificationStatus? = null,
        val date: String? = null,
        var isRead: Boolean = false,
        val dateKey: String? = null,
        val source: String? = null,
        val issuer: String? = null,
        val credentialSubject: String?,
        val approveEndpoint: String?,
        val rejectEndpoint: String?,
        val creator: String?
    )

    interface NotificationListener {
        fun onItemClick(notificationModel: NotificationAdapterModel)
    }

    private lateinit var callback: NotificationListener

    companion object {
        val notificationListener = object :DiffUtil.ItemCallback<NotificationAdapterModel>() {
            override fun areItemsTheSame(oldItem: NotificationAdapterModel,
                                         newItem: NotificationAdapterModel
            ): Boolean {
                return oldItem.notificationId == newItem.notificationId
            }

            override fun areContentsTheSame(oldItem: NotificationAdapterModel,
                                            newItem: NotificationAdapterModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return when(viewType) {
            VCBaseViewType.TITLE.ordinal -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_notification_type_one, parent, false)
            }
            else -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_notification_type_two, parent, false)
            }
        }
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        val data = getItem(position)
        when (getItemViewType(position)) {
            VCBaseViewType.TITLE.ordinal -> {
                binding as ListNotificationTypeOneBinding
                binding.date = data.date?.toShortDate()
            }

            VCBaseViewType.CONTENT.ordinal -> {
                binding as ListNotificationTypeTwoBinding
                binding.vcName = data.vcName
                binding.signStatus = data.status?.name
                binding.date = data.date?.toShortDateTime()
                binding.readStatus = data.isRead
                binding.cardView.isClickable = true
                binding.setOnNotificationClickListener {
                    callback.onItemClick(data)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).vcName != null) {
            VCBaseViewType.CONTENT.ordinal
        } else {
            VCBaseViewType.TITLE.ordinal
        }
    }

    fun registerNotificationListener(callback: NotificationListener) {
        this.callback = callback
    }

}