package co.finema.etdassi.feature.sign.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListItemSignDidCardInfoBinding

class SignDidAdapter: BaseAdapter<SignDidAdapter.DidRequester>(signDidListener) {

    data class DidRequester(val didRequester: String?, val creator: String?)

    companion object {
        val signDidListener = object :DiffUtil.ItemCallback<DidRequester>() {
            override fun areItemsTheSame(oldItem: DidRequester, newItem: DidRequester): Boolean {
                return oldItem.didRequester == newItem.didRequester
            }

            override fun areContentsTheSame(oldItem: DidRequester, newItem: DidRequester): Boolean {
                return oldItem.didRequester == newItem.didRequester
            }
        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_sign_did_card_info, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListItemSignDidCardInfoBinding
        binding.didRequester = getItem(position).didRequester
        binding.creator = getItem(position).creator
    }


}