package co.finema.etdassi.feature.getvc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListItemGetVcItemViewBinding
import co.finema.etdassi.feature.getvc.usecase.GetVCUseCase

class VCItemListAdapter: BaseAdapter<GetVCUseCase.VCSavingCount.CIDDoc>(cidDocListener) {

    interface VCItemList {
        fun onClickListener(cidDoc: GetVCUseCase.VCSavingCount.CIDDoc)
    }

    private var callback: VCItemList? = null

    companion object {
        val cidDocListener = object :DiffUtil.ItemCallback<GetVCUseCase.VCSavingCount.CIDDoc>() {
            override fun areItemsTheSame(
                oldItem: GetVCUseCase.VCSavingCount.CIDDoc,
                newItem: GetVCUseCase.VCSavingCount.CIDDoc
            ): Boolean {
                return oldItem.cid == newItem.cid
            }

            override fun areContentsTheSame(
                oldItem: GetVCUseCase.VCSavingCount.CIDDoc,
                newItem: GetVCUseCase.VCSavingCount.CIDDoc
            ): Boolean {
                return oldItem.type == newItem.type
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_get_vc_item_view, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListItemGetVcItemViewBinding
        binding.vcType = "${position + 1}. ${getItem(position).type}"
        binding.setOnClickListener {
            callback?.onClickListener(getItem(position))
        }

    }

    fun registerOnVCItemDetailClickListener(callback: VCItemList) {
        this.callback = callback
    }
}