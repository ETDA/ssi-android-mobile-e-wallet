package co.finema.etdassi.feature.myvc.myvptab.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListItemMyVpBinding
import co.finema.etdassi.feature.myvc.myvptab.model.VPListModel

class MyVPDetailAdapter: BaseAdapter<VPListModel>(myVPModelCallback) {

    interface MyVPDetailListener {
        fun onItemClick(data: VPListModel)
    }

    lateinit var callback: MyVPDetailListener

    companion object {
        val myVPModelCallback = object :DiffUtil.ItemCallback<VPListModel>() {
            override fun areItemsTheSame(oldItem: VPListModel, newItem: VPListModel): Boolean {
                return oldItem.cid == newItem.cid
            }

            override fun areContentsTheSame(oldItem: VPListModel, newItem: VPListModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_my_vp, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListItemMyVpBinding
        if (position == 0) binding.line.visibility = View.GONE
        val data = getItem(position)
        binding.docNumber = "เอกสารต้นฉบับที่ ${position + 1}:"
        binding.vcName  = data.vcName
        binding.status = data.vcStatus
        binding.setOnItemClickListener {
            callback.onItemClick(data)
        }
    }

    fun registerOnClickLister(callback: MyVPDetailListener) {
        this.callback = callback
    }
}