package co.finema.etdassi.feature.verifyvc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.data.VCAttributeModel
import co.finema.etdassi.common.data.VCDocument
import co.finema.etdassi.databinding.ListItemSignResultCardBinding
import co.finema.etdassi.feature.myvc.myvctab.detail.TitleDescriptionAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCAttributeAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCDetailAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCDetailViewModel

class VerifyResultCardAdapter: BaseAdapter<VerifyResultCardAdapter.Param>(listener) {

    data class Param(
        val itemList :List<VCAttributeModel>?
    )

    companion object {
        val listener = object :DiffUtil.ItemCallback<Param>() {
            override fun areItemsTheSame(oldItem: Param, newItem: Param): Boolean {
                return oldItem.itemList == newItem.itemList
            }

            override fun areContentsTheSame(oldItem: Param, newItem: Param): Boolean {
                return oldItem.itemList == newItem.itemList
            }


        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_sign_result_card, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListItemSignResultCardBinding
        val titleAdapter = TitleDescriptionAdapter()
        val vcDetailAdapter = VCAttributeAdapter()
        val conCardAdapter = ConcatAdapter(titleAdapter, vcDetailAdapter)
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = conCardAdapter
        }
        titleAdapter.submitList(listOf("รายละเอียดเอกสาร"))
        vcDetailAdapter.submitList(getItem(position).itemList)

    }


}