package co.finema.etdassi.feature.mysign.myreject.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListItemDidCardBinding

data class MyRejectDidCardModel(
    val didAddress: String
)

class MyRejectDidCardAdapter: BaseAdapter<MyRejectDidCardModel>(didAddressDiffUtil) {

    companion object {
        val didAddressDiffUtil = object :DiffUtil.ItemCallback<MyRejectDidCardModel>() {
            override fun areItemsTheSame(
                oldItem: MyRejectDidCardModel,
                newItem: MyRejectDidCardModel
            ): Boolean {
                return oldItem.didAddress == newItem.didAddress
            }

            override fun areContentsTheSame(
                oldItem: MyRejectDidCardModel,
                newItem: MyRejectDidCardModel
            ): Boolean {
                return oldItem.didAddress == newItem.didAddress
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_did_card, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListItemDidCardBinding
        binding.didAddress = getItem(position).didAddress
        println("did ${getItem(position).didAddress}")
    }
}