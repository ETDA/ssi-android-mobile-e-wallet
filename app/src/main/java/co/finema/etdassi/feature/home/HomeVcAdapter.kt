package co.finema.etdassi.feature.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListDocViewBinding


class HomeVcAdapter:BaseAdapter<VCModel>(VCModelCallback) {

    companion object {
        val VCModelCallback = object :DiffUtil.ItemCallback<VCModel>() {
            override fun areItemsTheSame(oldItem: VCModel, newItem: VCModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: VCModel, newItem: VCModel): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_doc_view, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListDocViewBinding
        val vcModel = getItem(position)
        binding.listDocImage.setImageResource(if (vcModel.id == "1") R.drawable.icon_waiting_sign_01 else R.drawable.icon_reject_sign_01)
        binding.title = vcModel.title
        binding.description = vcModel.description
        binding.count = vcModel.total

    }
}

//TODO REMOVE IT WHEN MODEL IS SUCCESS
data class VCModel(
    val id: String,
    val title: String,
    val description: String,
    val total: String
)