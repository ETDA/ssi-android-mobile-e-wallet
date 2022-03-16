package co.finema.etdassi.feature.myvc.myvctab

import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListMyVcCountBinding
import co.finema.etdassi.databinding.ListMyVcItemBinding
import co.finema.etdassi.feature.myvc.myvctab.detail.VCDetailActivity
import kotlinx.android.parcel.Parcelize

class MyVCListAdapter: BaseAdapter<MyVCListAdapter.VCViewAdapterItem>(vcListMockupCallback) {

    private enum class ViewType {
        TITLE,
        CONTENT
    }

    @Parcelize
    data class VCViewAdapterItem(
        val id: String? = null,
        val vcTypeName: String? = null,
        val vcStatus: String? = null
    ): Parcelable

    companion object {
        val vcListMockupCallback = object :DiffUtil.ItemCallback<VCViewAdapterItem>() {
            override fun areItemsTheSame(oldItem: VCViewAdapterItem, newItem: VCViewAdapterItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: VCViewAdapterItem, newItem: VCViewAdapterItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_my_vc_item, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListMyVcItemBinding
        val vcData = getItem(position)
        binding.vcTypeName = vcData.vcTypeName
        binding.vcStatus = vcData.vcStatus
        binding.setOnVcClickListener {
            val intent = Intent(it.context, VCDetailActivity::class.java)
            intent.putExtra("data", vcData)
            intent.putExtra("cid", vcData.id)
            it.context.startActivity(intent)
        }
    }

}