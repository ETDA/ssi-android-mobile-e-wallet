package co.finema.etdassi.feature.scan_qr.step_result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListItemSignResultCardBinding
import co.finema.etdassi.feature.mysign.myreject.detail.MyRejectDidCardModel
import co.finema.etdassi.feature.myvc.myvctab.detail.TitleDescriptionAdapter
import co.finema.etdassi.feature.scan_qr.ScanQRListVC

class CreateVPResultCardAdapter: BaseAdapter<CreateVPResultCardAdapter.Param>(listener) {
    data class Param(
        val title: String?,
        val didCardModel: CreateVPResultDidAdapter.Param?
    )
    companion object {
        val listener  = object :DiffUtil.ItemCallback<Param>() {
            override fun areItemsTheSame(oldItem: Param, newItem: Param): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Param, newItem: Param): Boolean {
                return oldItem.title == newItem.title
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_sign_result_card, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListItemSignResultCardBinding
        val titleDescriptionAdapter = TitleDescriptionAdapter()
        val didInfoAdapter = CreateVPResultDidAdapter()
        val concatAdapter = ConcatAdapter(titleDescriptionAdapter, didInfoAdapter)
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = concatAdapter
        }
        titleDescriptionAdapter.submitList(listOf("รายละเอียดการส่งเอกสารสำแดง"))
        didInfoAdapter.submitList(listOf(getItem(position).didCardModel))

    }
}