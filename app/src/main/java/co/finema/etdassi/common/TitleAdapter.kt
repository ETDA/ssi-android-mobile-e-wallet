package co.finema.etdassi.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListMyVcCountBinding

data class TitleAdapterDataModel(
    val itemCount: Int
        )
class TitleAdapter: BaseAdapter<TitleAdapterDataModel>(titleAdapterListener) {
    companion object {
        val titleAdapterListener = object :DiffUtil.ItemCallback<TitleAdapterDataModel>() {
            override fun areItemsTheSame(
                oldItem: TitleAdapterDataModel,
                newItem: TitleAdapterDataModel
            ): Boolean {
                return oldItem.itemCount == newItem.itemCount
            }

            override fun areContentsTheSame(
                oldItem: TitleAdapterDataModel,
                newItem: TitleAdapterDataModel
            ): Boolean {
                return oldItem.itemCount == newItem.itemCount
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_my_vc_count, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListMyVcCountBinding
        binding.count = "${getItem(position).itemCount}"
    }
}