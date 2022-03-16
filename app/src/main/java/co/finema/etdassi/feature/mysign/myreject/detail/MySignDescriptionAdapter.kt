package co.finema.etdassi.feature.mysign.myreject.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListAttributeViewBinding


class MySignDescriptionAdapter: BaseAdapter<MySignDescriptionAdapter.DescriptionInfoModel>(rejectInfoDiffUtil) {

    data class DescriptionInfoModel(
        val id: String? = null,
        val attributeName: String? = null,
        val name: String?  = null,
        val description: String? = null
    )

    companion object {
        val rejectInfoDiffUtil = object :DiffUtil.ItemCallback<DescriptionInfoModel>() {
            override fun areItemsTheSame(
                oldItem: DescriptionInfoModel,
                newItem: DescriptionInfoModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: DescriptionInfoModel,
                newItem: DescriptionInfoModel
            ): Boolean {
                return oldItem.name == newItem.name
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