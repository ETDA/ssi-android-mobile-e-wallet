package co.finema.etdassi.feature.mysign.vcsign

import android.app.Dialog
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import co.finema.etdassi.databinding.DialogDeleteBinding
import co.finema.etdassi.databinding.DialogRevokeBinding
import co.finema.etdassi.databinding.ListItemViewRevokeCardBinding
import co.finema.etdassi.feature.notification.NotificationStatus

class MySignedRevokeCardAdapter: BaseAdapter<MySignedRevokeCardAdapter.AdapterModel>(adapterLister) {

    data class AdapterModel(
        val cid: String,
        val vcType: String?,
        val signedDate: String?,
        val status: String?
    )

    interface RevokeListener {
        fun onItemClickListener(revokeData: AdapterModel)
    }

    private var callback: RevokeListener? = null

    companion object {
        val adapterLister = object :DiffUtil.ItemCallback<AdapterModel>() {
            override fun areItemsTheSame(oldItem: AdapterModel, newItem: AdapterModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: AdapterModel, newItem: AdapterModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_item_view_revoke_card, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListItemViewRevokeCardBinding
        val data = getItem(position)
        binding.apply {
            vcType = data.vcType
            signedDate = data.signedDate?.toShortDateTime()
            if(data.status == "revoke"){
                this.viewUnderline.visibility = View.INVISIBLE
                this.tvRevokeDocument.visibility = View.GONE
            }
            setOnRevokeClickListener {

                val dialog = Dialog(it.context)
                val dialogBinding = DataBindingUtil.inflate<DialogRevokeBinding>(LayoutInflater.from(it.context), R.layout.dialog_revoke, null, false)
                dialog.setContentView(dialogBinding.root)
                dialog.window?.apply {
                    setLayout(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT)
                    setGravity(Gravity.CENTER)
                    setBackgroundDrawableResource(android.R.color.transparent)
                }
                dialogBinding.cancelText = "ยกเลิก"
                dialogBinding.submitText = "เพิกถอน"
                dialogBinding.dialogTitle = "เพิกถอนเอกสารนี้?"
                dialogBinding.dialogDescription = "หากคุณทำการเพิกถอน เอกสารนี้จะไม่สามารถนำกลับมาใช้งานได้อีก"
                dialogBinding.setOnCancelClickListener {
                    dialog.dismiss()
                }
                dialogBinding.setOnSubmitClickListener {
                    callback?.onItemClickListener(data)
                    dialog.dismiss()
                }
                dialog.show()


            }
        }
    }

    fun registerOnRevokeClick(callback: RevokeListener) {
        this.callback = callback
    }
}