package co.finema.etdassi.feature.myvc.myvctab.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListTitleDescriptionBinding

class TitleDescriptionAdapter: BaseAdapter<String>(titleListener) {
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
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_title_description, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListTitleDescriptionBinding
        binding.title = getItem(position)
    }
}