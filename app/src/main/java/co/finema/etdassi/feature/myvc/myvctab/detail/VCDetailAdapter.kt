package co.finema.etdassi.feature.myvc.myvctab.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListAttributeViewBinding

class VCDetailAdapter:BaseAdapter<VCDetailViewModel.VCDetailAdapterModel>(VCAttributeCallback) {

    companion object {
        val VCAttributeCallback = object : DiffUtil.ItemCallback<VCDetailViewModel.VCDetailAdapterModel>() {
            override fun areItemsTheSame(
                oldItem: VCDetailViewModel.VCDetailAdapterModel,
                newItem: VCDetailViewModel.VCDetailAdapterModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: VCDetailViewModel.VCDetailAdapterModel,
                newItem: VCDetailViewModel.VCDetailAdapterModel
            ): Boolean {
                return oldItem.attributeName == newItem.attributeName
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_attribute_view, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        val attribute = getItem(position)
        binding as ListAttributeViewBinding
        binding.title = attribute.attributeName
        binding.name = "${attribute.name}:"
        binding.description = attribute.description
    }


}