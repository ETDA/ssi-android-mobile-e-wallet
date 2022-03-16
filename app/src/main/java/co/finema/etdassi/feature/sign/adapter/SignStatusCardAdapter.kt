package co.finema.etdassi.feature.sign.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListItemSignStatusBinding
import co.finema.etdassi.feature.notification.NotificationStatus

class SignStatusCardAdapter: BaseAdapter<SignStatusCardAdapter.SignStatusCard>(signStatusCard) {

    data class SignStatusCard(
        val vcType: String?,
        val status: NotificationStatus?
    )

    companion object {
        val signStatusCard = object :DiffUtil.ItemCallback<SignStatusCard>() {
            override fun areItemsTheSame(
                oldItem: SignStatusCard,
                newItem: SignStatusCard
            ): Boolean {
                return oldItem.vcType == newItem.vcType
            }

            override fun areContentsTheSame(
                oldItem: SignStatusCard,
                newItem: SignStatusCard
            ): Boolean {
                return oldItem.vcType == newItem.vcType
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_sign_status, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListItemSignStatusBinding
        val data = getItem(position)
        binding.vcStatus = data.status?.name
        binding.vcType = data.vcType
    }

}