package co.finema.etdassi.feature.sign.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListItemSignResultTitleBinding

class SignedResultAdapter: BaseAdapter<String>(titleListener) {

    companion object {
        val titleListener = object :DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_sign_result_title, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListItemSignResultTitleBinding
        binding.title = getItem(position)
        binding.description = binding.root.resources.getString(R.string.fragment_sign_result_02)
    }


}