package co.finema.etdassi.feature.mysign.myreject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDate
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import co.finema.etdassi.databinding.ListVpMainBinding

class MyRejectAdapter: BaseAdapter<MyRejectModel>(myRejectListener) {

    interface MyRejectAdapterListener {
        fun onItemClick(myRejectModel: MyRejectModel)
    }

    private var callback: MyRejectAdapterListener? = null

    companion object {
        val myRejectListener = object :DiffUtil.ItemCallback<MyRejectModel>() {
            override fun areItemsTheSame(oldItem: MyRejectModel, newItem: MyRejectModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MyRejectModel,
                newItem: MyRejectModel
            ): Boolean {
                return oldItem.name == newItem.name
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
        binding.date = "วันที่ปฏิเสธลงนาม: ${data.rejectDate.toShortDateTime()}"
        binding.isRead = true
        binding.setOnItemClickListener {
            callback?.onItemClick(data)
        }
    }

    fun registerMyRejectListener(callback: MyRejectAdapterListener) {
        this.callback = callback
    }

}