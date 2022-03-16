package co.finema.etdassi.feature.scan_qr.step_two

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.enum.VCBaseViewType
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import co.finema.etdassi.databinding.ListItemVcWithRadioBoxBinding
import co.finema.etdassi.databinding.ListMyVcCountBinding
import co.finema.etdassi.feature.scan_qr.ScanStepSelectVCList

class ScanStepSelectVCAdapter: BaseAdapter<ScanStepSelectVCList>(scanStepSelectVCListener) {

    interface VCRadioBoxListener {
        fun onRadioBoxCheck(vcData: ScanStepSelectVCList, position: Int)
        fun onVCDetailClick(vcData: ScanStepSelectVCList, position: Int)
    }

    lateinit var callback: VCRadioBoxListener

    companion object {
        val scanStepSelectVCListener = object :DiffUtil.ItemCallback<ScanStepSelectVCList>() {
            override fun areItemsTheSame(oldItem: ScanStepSelectVCList,
                                         newItem: ScanStepSelectVCList): Boolean {
                return oldItem.cid == newItem.cid
            }

            override fun areContentsTheSame(oldItem: ScanStepSelectVCList,
                                            newItem: ScanStepSelectVCList): Boolean {
                return oldItem.isSelect == newItem.isSelect && oldItem == newItem
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return when(viewType) {
            VCBaseViewType.TITLE.ordinal -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_my_vc_count, parent, false)
            }
            else -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_vc_with_radio_box, parent, false)
            }
        }
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        val data = getItem(position)
        when(getItemViewType(position)) {
            VCBaseViewType.TITLE.ordinal -> {
                binding as ListMyVcCountBinding
                binding.count = data.count.toString()
            }
            else -> {
                binding as ListItemVcWithRadioBoxBinding
                if (position == 0) binding.line.visibility = View.GONE
                binding.isSelect = data.isSelect
                binding.vcName = data.vcTypeName
                binding.date = "วันที่ร้องขอ: ${data.dateString?.toShortDateTime()}"
                binding.setOnRadioClickListener {
                    callback.onRadioBoxCheck(data, position)
                }
                binding.setOnVCDetailClickListener {
                    callback.onVCDetailClick(data, position)
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

    fun registerRadioBoxOnClick(callback: VCRadioBoxListener) {
        this.callback = callback
    }

}