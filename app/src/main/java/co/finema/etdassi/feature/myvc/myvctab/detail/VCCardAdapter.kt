package co.finema.etdassi.feature.myvc.myvctab.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListItemVcCardWithTagBinding

class VCCardAdapter: BaseAdapter<VCCardAdapter.VCCardModel>(vcCardListener) {

    data class VCCardModel(
        val vcType: String,
        val vcStatus: String,
        val vcTag: String? = null
    )

    companion object {
        val vcCardListener = object :DiffUtil.ItemCallback<VCCardModel>() {
            override fun areItemsTheSame(oldItem: VCCardModel, newItem: VCCardModel): Boolean {
                return oldItem.vcType == newItem.vcType
            }

            override fun areContentsTheSame(oldItem: VCCardModel, newItem: VCCardModel): Boolean {
                return oldItem.vcType == newItem.vcType
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_vc_card_with_tag, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        val data = getItem(position)
        binding as ListItemVcCardWithTagBinding
        binding.vcType = data.vcType
        binding.vcStatus = data.vcStatus
        binding.tagString = data.vcTag
    }
}