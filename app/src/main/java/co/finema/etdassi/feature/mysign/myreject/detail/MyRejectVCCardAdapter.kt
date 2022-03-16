package co.finema.etdassi.feature.mysign.myreject.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListItemVcCardBinding


data class VCCardModel(
    val vcName: String?,
    val status: String?,
    val vcCardAttributeTitle1: String?,
    val vcCardAttributeDes1: String?,
    val vcCardAttributeTitle2: String?,
    val vcCardAttributeDes2: String?
)

class MyRejectVCCardAdapter: BaseAdapter<VCCardModel>(vcCardModelDiffUtil) {
    companion object {
        val vcCardModelDiffUtil = object :DiffUtil.ItemCallback<VCCardModel>() {
            override fun areItemsTheSame(oldItem: VCCardModel, newItem: VCCardModel): Boolean {
                return oldItem.vcName == newItem.vcName
            }

            override fun areContentsTheSame(oldItem: VCCardModel, newItem: VCCardModel): Boolean {
                return oldItem.vcName == newItem.vcName
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_vc_card, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListItemVcCardBinding
        val data = getItem(position)
        binding.vcName = data.vcName
        binding.status = data.status
        binding.vcCardAttributeTitle1 = data.vcCardAttributeTitle1
        binding.vcCardAttributeDes1 = data.vcCardAttributeDes1
        binding.vcCardAttributeTitle2 = data.vcCardAttributeTitle2
        binding.vcCardAttributeDes2 = data.vcCardAttributeDes2
    }
}