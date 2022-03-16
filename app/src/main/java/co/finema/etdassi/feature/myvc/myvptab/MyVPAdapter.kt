package co.finema.etdassi.feature.myvc.myvptab

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import co.finema.etdassi.databinding.ListMyVcCountBinding
import co.finema.etdassi.databinding.ListVpMainBinding
import kotlinx.android.parcel.Parcelize

class MyVPAdapter: BaseAdapter<MyVPAdapter.VPDocumentAdapterModel>(vcListRequestMockupCallback) {

    private enum class ViewType {
        TITLE,
        CONTENT
    }

    interface MyVPAdapterLister {
        fun onItemClick(data: VPDocumentAdapterModel)
    }

    @Parcelize
    data class VPDocumentAdapterModel(
        val id: String? = null,
        val groupName: String? = null,
        val date: String? = null,
        val vpCount: String? = null
    ): Parcelable

    lateinit var callback: MyVPAdapterLister

    companion object {
        val vcListRequestMockupCallback = object :DiffUtil.ItemCallback<VPDocumentAdapterModel>() {
            override fun areItemsTheSame(
                oldItem: VPDocumentAdapterModel,
                newItem: VPDocumentAdapterModel
            ): Boolean {
                return oldItem.groupName == newItem.groupName
            }

            override fun areContentsTheSame(
                oldItem: VPDocumentAdapterModel,
                newItem: VPDocumentAdapterModel
            ): Boolean {
                return oldItem.groupName == newItem.groupName
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_vp_main, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        val data = getItem(position)
        binding as ListVpMainBinding
        binding.vcTitle = data.groupName + "(${data.vpCount})"
        binding.date = "วันที่ลงนาม: ${data.date?.toShortDateTime()}"
        binding.isRead = true
        binding.setOnItemClickListener {
            callback.onItemClick(data)
        }
    }

    fun registerOnClickLister(callback: MyVPAdapterLister) {
        this.callback = callback
    }

}