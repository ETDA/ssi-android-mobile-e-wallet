package co.finema.etdassi.feature.mysign.vcsign

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import co.finema.etdassi.databinding.ListVpMainBinding
import co.finema.etdassi.feature.mysign.data.VCSigned

class VCSignedAdapter: BaseAdapter<VCSigned>(vcSignedModelCallback) {

    interface VCSignedListener {
        fun onVCSignedClick(vcSigned: VCSigned)
    }

    private var callback: VCSignedListener? = null

    companion object {
        val vcSignedModelCallback = object : DiffUtil.ItemCallback<VCSigned>() {
            override fun areItemsTheSame(
                oldItem: VCSigned,
                newItem: VCSigned
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: VCSigned,
                newItem: VCSigned
            ): Boolean {
                return oldItem.isRead == newItem.isRead
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_vp_main, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        val data = getItem(position)
        binding as ListVpMainBinding
        binding.vcTitle = data.name
        binding.date = "วันที่ลงนาม: ${data.signDate.toShortDateTime()}"
        binding.isRead = data.isRead
        binding.setOnItemClickListener {
            callback?.onVCSignedClick(data)
        }
    }

    fun registerOnItemClick(vcSignedListener: VCSignedListener) {
        this.callback = vcSignedListener
    }


}