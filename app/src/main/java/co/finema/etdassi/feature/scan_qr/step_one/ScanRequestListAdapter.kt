package co.finema.etdassi.feature.scan_qr.step_one

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListItemVcRequestTypeOneBinding
import co.finema.etdassi.feature.scan_qr.ScanQRListVC

class ScanRequestListAdapter: BaseAdapter<ScanQRListVC>(scanRequestListCallback) {

    interface ScanRequestListListener {
        fun onVcClick(reqVcData: ScanQRListVC)
    }

    lateinit var callback: ScanRequestListListener

    companion object {
        val scanRequestListCallback = object : DiffUtil.ItemCallback<ScanQRListVC>() {
            override fun areItemsTheSame(oldItem: ScanQRListVC, newItem: ScanQRListVC): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ScanQRListVC, newItem: ScanQRListVC): Boolean {
                return oldItem.vcIdPick == newItem.vcIdPick
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_vc_request_type_one, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        val data = getItem(position)
        binding as ListItemVcRequestTypeOneBinding
        binding.descriptionNo = "รายละเอียดเอกสารที่ ${position +1}"
        binding.vcTypeName = data.vcTypeName
        binding.isCheck = data.vcIdPick != null
        if (position == 0) binding.line.visibility = View.GONE else binding.line.visibility = View.VISIBLE

        binding.setOnCardClickListener {
            callback.onVcClick(data)
        }
    }

    fun registerOnClick(callback: ScanRequestListListener) {
        this.callback = callback
    }

}