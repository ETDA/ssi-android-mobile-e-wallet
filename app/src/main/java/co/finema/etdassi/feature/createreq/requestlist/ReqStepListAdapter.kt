package co.finema.etdassi.feature.createreq.requestlist

import android.app.Dialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.base.BaseViewHolder
import co.finema.etdassi.common.enum.VCBaseViewType
import co.finema.etdassi.databinding.DialogDeleteBinding
import co.finema.etdassi.databinding.ListMyVcCountBinding
import co.finema.etdassi.databinding.ListVcItemWithDetailBinding

class ReqStepListAdapter: BaseAdapter<ReqStepListModel>(reqStepListCallback) {
    interface ReqStepListener {
        fun onDelete(id: String)
    }

    lateinit var callback: ReqStepListener

    companion object {
        val reqStepListCallback = object : DiffUtil.ItemCallback<ReqStepListModel>() {
            override fun areItemsTheSame(oldItem: ReqStepListModel,
                                         newItem: ReqStepListModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ReqStepListModel,
                                            newItem: ReqStepListModel): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return when(viewType) {
            VCBaseViewType.TITLE.ordinal -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_my_vc_count, parent, false)
            }
            else -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_vc_item_with_detail, parent, false)
            }
        }
    }

    override fun setViewHolder(holder: BaseViewHolder<ViewDataBinding>,
                               position: Int) {
        super.setViewHolder(holder, position)
        if (getItemViewType(position) == VCBaseViewType.CONTENT.ordinal) {
            holder.binding as ListVcItemWithDetailBinding
            holder.binding.editTextDescription.removeTextChangedListener(holder.watcher)
            holder.binding.editTextDescription.setText(getItem(position).description)
            holder.watcher = holder.binding.editTextDescription.doAfterTextChanged { text ->
                if (!text.isNullOrBlank()) {
                    getItem(position).description = text.toString()
                }
            }
        }
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        when(getItemViewType(position)) {
            VCBaseViewType.TITLE.ordinal -> {
                binding as ListMyVcCountBinding
                binding.count = getItem(position).count.toString()
                binding.title.text = "รายการเอกสารที่ร้องขอ"
                binding.unit.text = "รายการ"
            }
            else -> {
                binding as ListVcItemWithDetailBinding
                val data = getItem(position)
                binding.isRequest = data.isRequest
                binding.vcName = data.vcName
                binding.vcIndexTitle = "รายละเอียดเอกสารที่ $position"
                data.id?.let { id ->
                    binding.setOnCloseClickListener {

                        val dialog = Dialog(it.context)
                        val dialogBinding = DataBindingUtil.inflate<DialogDeleteBinding>(LayoutInflater.from(it.context), R.layout.dialog_delete, null, false)
                        dialog.setContentView(dialogBinding.root)
                        dialog.window?.apply {
                            setLayout(
                                WindowManager.LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.WRAP_CONTENT)
                            setGravity(Gravity.CENTER)
                            setBackgroundDrawableResource(android.R.color.transparent)
                        }
                        dialogBinding.setOnCancelListener {
                            dialog.dismiss()
                        }
                        dialogBinding.setOnDeleteListener {
                            callback.onDelete(id)
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                }
                binding.setOnItemClickListener {
                    getItem(position).isRequest = !getItem(position).isRequest
                    notifyItemChanged(position)
                }
            }
        }
    }



    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).count != null) {
            VCBaseViewType.TITLE.ordinal
        } else {
            VCBaseViewType.CONTENT.ordinal
        }
    }

    fun registerOnDelete(callback: ReqStepListener) {
        this.callback = callback
    }
}