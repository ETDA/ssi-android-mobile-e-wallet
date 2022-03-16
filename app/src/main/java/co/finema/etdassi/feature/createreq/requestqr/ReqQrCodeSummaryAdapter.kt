package co.finema.etdassi.feature.createreq.requestqr

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListVcItemSummaryBinding
import co.finema.etdassi.feature.createreq.requestlist.ReqStepListModel

class ReqQrCodeSummaryAdapter: BaseAdapter<ReqStepListModel>(reqSummaryCallback) {

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
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_vc_item_summary, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListVcItemSummaryBinding
        binding.vcTypeName = getItem(position).vcName
    }

}