package co.finema.etdassi.feature.verifyvc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListVerifyResultStatusCardBinding

class VerifyResultStatusCardAdapter: BaseAdapter<VerifyResultStatusCardAdapter.Param>(listener) {
    data class Param(
        val vcType: String,
        val status: String
    )

    companion object {
        val listener = object :DiffUtil.ItemCallback<Param>() {
            override fun areItemsTheSame(oldItem: Param, newItem: Param): Boolean {
                return oldItem.vcType == newItem.vcType
            }

            override fun areContentsTheSame(oldItem: Param, newItem: Param): Boolean {
                return oldItem.vcType == newItem.vcType
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_verify_result_status_card, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListVerifyResultStatusCardBinding
        binding.vcType = getItem(position).vcType
        binding.vcStatus = getItem(position).status
    }

}