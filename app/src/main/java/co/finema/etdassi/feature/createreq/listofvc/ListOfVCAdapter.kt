package co.finema.etdassi.feature.createreq.listofvc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.enum.VCBaseViewType
import co.finema.etdassi.databinding.ListMyVcCountBinding
import co.finema.etdassi.databinding.ListVcItemWithCheckboxBinding

class ListOfVCAdapter: BaseAdapter<ListOfVCModel>(listOfVCCallbacks) {

    interface Listener {
        fun isOnClick()
    }

    var callback :Listener? = null

    companion object {
        val listOfVCCallbacks = object :DiffUtil.ItemCallback<ListOfVCModel>() {
            override fun areItemsTheSame(oldItem: ListOfVCModel, newItem: ListOfVCModel): Boolean {
                return oldItem.vcName == newItem.vcName
            }

            override fun areContentsTheSame(
                oldItem: ListOfVCModel,
                newItem: ListOfVCModel
            ): Boolean {
                return (oldItem.isCheck == newItem.isCheck) && (oldItem.vcName == newItem.vcName)
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return when(viewType) {
            VCBaseViewType.TITLE.ordinal -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_my_vc_count, parent, false)
            }
            else -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_vc_item_with_checkbox, parent, false)
            }
        }
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        when(getItemViewType(position)) {
            VCBaseViewType.TITLE.ordinal -> {
                binding as ListMyVcCountBinding
                binding.count = getItem(position).count.toString()
            }
            else -> {
                binding as ListVcItemWithCheckboxBinding
                val data = getItem(position)
                binding.isCheck = data.isCheck
                binding.vcName = data.vcName
                binding.setOnItemClickListener {
                    getItem(position).isCheck = !getItem(position).isCheck
                    notifyItemChanged(position)
                    callback?.isOnClick()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).count != null) {
            VCBaseViewType.TITLE.ordinal
        } else {
            VCBaseViewType.CONTENT.ordinal
        }
    }

    fun listenerCallback(callback: Listener) {
        this.callback = callback
    }

}