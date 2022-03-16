package co.finema.etdassi.feature.mysign.myreject.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.data.VCAttributeModel
import co.finema.etdassi.common.utils.collapse
import co.finema.etdassi.common.utils.expand
import co.finema.etdassi.databinding.ListItemRejectCardInfoType1Binding
import co.finema.etdassi.databinding.ListItemRejectCardInfoType2Binding
import co.finema.etdassi.feature.myvc.myvctab.detail.VCAttributeAdapter

class RejectCardMainAdapter: BaseAdapter<RejectCardMainAdapter.RejectCardMainModel>(
    rejectCardMainDiffUtil) {

    data class RejectCardMainModel(
        val type: RejectInfoType,
        val rejectInfoList: List<VCAttributeModel>? = null,
        val rejectRemark: String? = null
    )

    companion object {
        val rejectCardMainDiffUtil = object :DiffUtil.ItemCallback<RejectCardMainModel>() {
            override fun areItemsTheSame(
                oldItem: RejectCardMainModel,
                newItem: RejectCardMainModel
            ): Boolean {
                return oldItem.type == newItem.type
            }

            override fun areContentsTheSame(
                oldItem: RejectCardMainModel,
                newItem: RejectCardMainModel
            ): Boolean {
                return oldItem.type == newItem.type
            }
        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return when(viewType) {
            RejectInfoType.INFORMATION.ordinal -> DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_reject_card_info_type_1, parent, false)
            else -> DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_reject_card_info_type_2, parent, false)
        }
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        when(getItemViewType(position)) {
            RejectInfoType.INFORMATION.ordinal -> {
                binding as ListItemRejectCardInfoType1Binding
                binding.title = "รายละเอียดเอกสาร"
                binding.setOnExpandOnClickListener {
                    if (binding.imageArrow.rotation == 180F) {
                        binding.imageArrow.animate().rotation(0F).duration = 100
                        binding.recyclerview.expand()
                    } else {
                        binding.imageArrow.animate().rotation(180F).duration = 100
                        binding.recyclerview.collapse()
                    }
                }
                val mAdapter = VCAttributeAdapter()
                binding.recyclerview.apply {
                    layoutManager = LinearLayoutManager(this.context)
                    adapter = mAdapter
                }
                mAdapter.submitList(getItem(position).rejectInfoList)
            }

            else -> {
                binding as ListItemRejectCardInfoType2Binding
                binding.title = "เหตุผลในการปฏิเสธ"
                binding.description = getItem(position).rejectRemark
                binding.setOnExpandOnClickListener {
                    if (binding.imageArrow.rotation == 180F) {
                        binding.imageArrow.animate().rotation(0F).duration = 100
                        binding.contentView.expand()
                    } else {
                        binding.imageArrow.animate().rotation(180F).duration = 100
                        binding.contentView.collapse()
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position).type) {
            RejectInfoType.INFORMATION -> RejectInfoType.INFORMATION.ordinal
            else -> RejectInfoType.REMARK.ordinal
        }
    }


}