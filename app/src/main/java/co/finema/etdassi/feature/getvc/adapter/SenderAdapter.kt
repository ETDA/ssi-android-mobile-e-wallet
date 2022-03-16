package co.finema.etdassi.feature.getvc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import co.finema.etdassi.databinding.ListItemSenderViewBinding
import co.finema.etdassi.feature.getvc.usecase.GetVCUseCase

class SenderAdapter: BaseAdapter<GetVCUseCase.VCSavingCount.SenderData>(senderDataListener) {
    companion object {
        val senderDataListener = object :DiffUtil.ItemCallback<GetVCUseCase.VCSavingCount.SenderData>() {
            override fun areItemsTheSame(
                oldItem: GetVCUseCase.VCSavingCount.SenderData,
                newItem: GetVCUseCase.VCSavingCount.SenderData
            ): Boolean {
                return oldItem.senderDid == newItem.senderDid
            }

            override fun areContentsTheSame(
                oldItem: GetVCUseCase.VCSavingCount.SenderData,
                newItem: GetVCUseCase.VCSavingCount.SenderData
            ): Boolean {
                return oldItem.senderDid == newItem.senderDid
            }
        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_sender_view, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListItemSenderViewBinding
        val data = getItem(position)
        binding.didSender = data.senderDid
        binding.senderDate = data.createdAt?.toShortDateTime()
        binding.success = data.successCase.toString()
        binding.total = data.totalCase.toString()
    }
}