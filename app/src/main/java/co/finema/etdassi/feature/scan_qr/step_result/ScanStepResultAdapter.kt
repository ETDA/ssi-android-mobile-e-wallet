package co.finema.etdassi.feature.scan_qr.step_result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListItemScanStepResultBinding
import co.finema.etdassi.feature.scan_qr.ScanQRListVC

class ScanStepResultAdapter: BaseAdapter<ScanQRListVC>(scanStepResultListener) {

    companion object {
        val scanStepResultListener = object : DiffUtil.ItemCallback<ScanQRListVC>() {
            override fun areItemsTheSame(oldItem: ScanQRListVC, newItem: ScanQRListVC): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ScanQRListVC, newItem: ScanQRListVC): Boolean {
                return oldItem.vcTypeName == newItem.vcTypeName
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_scan_step_result, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        val data = getItem(position)
        binding as ListItemScanStepResultBinding
        binding.vcName = "${position + 1}. ${data.vcTypeName}"
    }

}