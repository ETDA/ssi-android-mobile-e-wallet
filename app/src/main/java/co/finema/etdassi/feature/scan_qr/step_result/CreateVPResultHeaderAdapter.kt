package co.finema.etdassi.feature.scan_qr.step_result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListItemSignResultTitleBinding

class CreateVPResultHeaderAdapter: BaseAdapter<CreateVPResultHeaderAdapter.Param>(listener) {

    data class Param(
        val title: String?,
        val description: String?,
        val iconType: Boolean = true
    )

    companion object {
        val listener = object :DiffUtil.ItemCallback<Param>() {
            override fun areItemsTheSame(oldItem: Param, newItem: Param): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Param, newItem: Param): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_sign_result_title, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListItemSignResultTitleBinding
        binding.title = getItem(position).title
        binding.description = getItem(position).description
        binding.icon = getItem(position).iconType
    }

}