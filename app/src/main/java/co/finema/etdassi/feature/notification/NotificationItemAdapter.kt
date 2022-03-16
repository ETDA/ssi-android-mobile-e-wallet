package co.finema.etdassi.feature.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDate
import co.finema.etdassi.databinding.ListNotificationTypeItemBinding

class NotificationItemAdapter: BaseAdapter<NotificationItemAdapter.NotificationItemModel>(
    notificationItemListener) {

    data class NotificationItemModel(
        val title: String?,
        val notificationList: List<NotificationListAdapter.NotificationAdapterModel>?
    )

    private var callback: NotificationListAdapter.NotificationListener? = null

    companion object {
        val notificationItemListener = object :DiffUtil.ItemCallback<NotificationItemModel>() {
            override fun areItemsTheSame(
                oldItem: NotificationItemModel,
                newItem: NotificationItemModel
            ): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: NotificationItemModel,
                newItem: NotificationItemModel
            ): Boolean {
                return oldItem.title == newItem.title
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_notification_type_item, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListNotificationTypeItemBinding
        val data = getItem(position)
        binding.date = data.title?.toShortDate()
        val mAdapter = NotificationListAdapter()
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = mAdapter
        }
        data.notificationList?.let { mAdapter.submitList(it) }
        callback?.let { mAdapter.registerNotificationListener(it) }
    }

    fun registerNotificationClickListener(callback: NotificationListAdapter.NotificationListener) {
        this.callback = callback
    }

}