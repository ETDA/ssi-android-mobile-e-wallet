package co.finema.etdassi.feature.getvc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListTitleDescriptionBinding

class TitleDescriptionGetVCAdapter: BaseAdapter<TitleDescriptionGetVCAdapter.TitleString>(
    titleStringListener
) {
    data class TitleString(val title: String)
    companion object {
        val titleStringListener = object :DiffUtil.ItemCallback<TitleString>() {
            override fun areItemsTheSame(oldItem: TitleString, newItem: TitleString): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: TitleString, newItem: TitleString): Boolean {
                return oldItem.title == newItem.title
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_title_description, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListTitleDescriptionBinding
        binding.title = getItem(position).title
    }
}