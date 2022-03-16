package co.finema.etdassi.feature.mysign.signrequest

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import co.finema.etdassi.databinding.ListVpMainBinding
import co.finema.etdassi.feature.mysign.data.SignRequestModel
import kotlinx.android.parcel.Parcelize

class SignRequestAdapter: BaseAdapter<SignRequestAdapter.SignRequestAdapterModel>(signRequestModelCallback) {

    interface SignDataListener {
        fun onItemClick(signRequestModel: SignRequestAdapterModel)
    }

    @Parcelize
    data class SignRequestAdapterModel(
        val id: Long,
        val typeName: String? = null,
        val dateRequest: String? = null,
        var isRead: Boolean = false
    ): Parcelable

    lateinit var callback: SignDataListener


    companion object {
        val signRequestModelCallback = object :DiffUtil.ItemCallback<SignRequestAdapterModel>() {
            override fun areItemsTheSame(
                oldItem: SignRequestAdapterModel,
                newItem: SignRequestAdapterModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: SignRequestAdapterModel,
                newItem: SignRequestAdapterModel
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
        binding.vcTitle = data.typeName
        binding.date = "วันที่ส่ง: ${data.dateRequest?.toShortDateTime()}"
        binding.isRead = data.isRead
        binding.setOnItemClickListener {
            callback.onItemClick(data)
        }
    }


    fun registerVCDataClick(callback: SignDataListener) {
        this.callback = callback
    }

}