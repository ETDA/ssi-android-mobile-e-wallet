package co.finema.etdassi.feature.createreq.requestsum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.enum.VCBaseViewType
import co.finema.etdassi.databinding.ListMyVcCountBinding
import co.finema.etdassi.databinding.ListVcItemWithSummaryBinding
import co.finema.etdassi.feature.createreq.requestlist.ReqStepListModel

class ReqSummaryAdapter: BaseAdapter<ReqStepListModel>(reqSummaryCallback) {

    companion object {
        val reqSummaryCallback = object : DiffUtil.ItemCallback<ReqStepListModel>() {
            override fun areItemsTheSame(oldItem: ReqStepListModel,
                                         newItem: ReqStepListModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ReqStepListModel,
                                            newItem: ReqStepListModel): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return when(viewType) {
            VCBaseViewType.TITLE.ordinal -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_my_vc_count, parent, false)
            }
            else -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_vc_item_with_summary, parent, false)
            }
        }
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        when(getItemViewType(position)) {
            VCBaseViewType.TITLE.ordinal -> {
                binding as ListMyVcCountBinding
                binding.count = getItem(position).count.toString()
                binding.title.text = "รายการเอกสารที่ร้องขอ"
                binding.unit.text = "รายการ"
            }
            else -> {
                binding as ListVcItemWithSummaryBinding
                val data = getItem(position)
                binding.vcIndexTitle = "รายละเอียดเอกสารที่ $position"
                binding.vcName = data.vcName
                binding.description = data.description ?: "-"
                if (!data.isRequest) {
                    binding.requestText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.default_text))
                    binding.requestText.text = "-"
                } else {
                    binding.requestText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.secondary))
                    binding.requestText.text = binding.root.context.getString(R.string.step_request_need_request)
                }
                when(position) {
                    currentList.size - 1 -> {
                        binding.linear.visibility = View.INVISIBLE
                        if (currentList.size > 2){
                            binding.background.setBackgroundResource(R.drawable.bg_bottom_radius)
                        } else {
                            binding.background.setBackgroundResource(R.drawable.bg_top_bottom_radius)
                        }
                    }

                    1 -> {
                        binding.background.setBackgroundResource(R.drawable.bg_top_radius)
                    }

                    else -> {
                        binding.background.setBackgroundResource(R.color.white)
                    }

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


}