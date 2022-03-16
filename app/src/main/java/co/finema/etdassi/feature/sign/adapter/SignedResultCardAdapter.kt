package co.finema.etdassi.feature.sign.adapter

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
import co.finema.etdassi.feature.myvc.myvctab.detail.TitleDescriptionAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCAttributeAdapter
import co.finema.etdassi.feature.sign.SignViewModel

class SignedResultCardAdapter:BaseAdapter<SignViewModel.SignResultPageData>(vcAttributeModel) {
    companion object {
        val vcAttributeModel = object :DiffUtil.ItemCallback<SignViewModel.SignResultPageData>() {
            override fun areItemsTheSame(
                oldItem: SignViewModel.SignResultPageData,
                newItem: SignViewModel.SignResultPageData
            ): Boolean {
                return oldItem.notificationId == newItem.notificationId
            }

            override fun areContentsTheSame(
                oldItem: SignViewModel.SignResultPageData,
                newItem: SignViewModel.SignResultPageData
            ): Boolean {
                return oldItem.notificationId == newItem.notificationId
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_sign_result_card, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListItemSignResultCardBinding
        val titleDescriptionAdapter = TitleDescriptionAdapter()
        val mAdapter = VCAttributeAdapter()
        val concatAdapter = ConcatAdapter(titleDescriptionAdapter, mAdapter)
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = concatAdapter
        }
        titleDescriptionAdapter.submitList(listOf(binding.root.resources.getString(R.string.title_vc_description)))
        mAdapter.submitList(getItem(position).vcDetail)
    }
}