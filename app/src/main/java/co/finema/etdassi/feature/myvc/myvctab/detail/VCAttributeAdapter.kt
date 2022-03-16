package co.finema.etdassi.feature.myvc.myvctab.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.common.data.VCAttributeModel
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDate
import co.finema.etdassi.common.utils.collapse
import co.finema.etdassi.common.utils.expand
import co.finema.etdassi.databinding.*
import co.finema.etdassi.common.data.VCAttributeType.*

class VCAttributeAdapter: BaseAdapter<VCAttributeModel>(VCAttributeCallback) {

    private val DDD = 45
    private val EEE = 46
    companion object {
        val VCAttributeCallback = object :DiffUtil.ItemCallback<VCAttributeModel>() {
            override fun areItemsTheSame(
                oldItem: VCAttributeModel,
                newItem: VCAttributeModel
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: VCAttributeModel,
                newItem: VCAttributeModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        val viewItem = getItem(position)
        return when(viewItem.type) {
            OBJECT_WITH_ARRAY -> {
                OBJECT.ordinal
            }
            VALUE -> VALUE.ordinal
            OBJECT -> {
                if (viewItem.title != null) {
                    OBJECT.ordinal
                } else {
                    DDD
                }
            }
            ARRAY-> ARRAY.ordinal
            else -> 99
        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return when(viewType) {
            VALUE.ordinal -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_vc_attribute_type_1, parent, false)
            }
            OBJECT.ordinal -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_vc_attribute_type_2, parent, false)
            }
            OBJECT_WITH_ARRAY.ordinal -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_vc_attribute_type_3, parent, false)
            }
            ARRAY.ordinal -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_vc_attribute_type_4, parent, false)
            }
            DDD -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_vc_attribute_type_5, parent, false)
            }
            EEE -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_vc_attribute_type_6, parent, false)
            }
            else -> {
                DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_vc_attribute_type_4, parent, false)
            }
        }
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        val data = getItem(position)
        when(getItemViewType(position)) {
            VALUE.ordinal -> {
                binding as ListVcAttributeType1Binding
                binding.title = data.title //+ " ${data.level} ${data.type}"
                binding.value = if (isDateFormat(data.value) == true) {
                    data.value?.toShortDate()
                } else {
                    data.value
                }
            }
            OBJECT.ordinal -> {
                binding as ListVcAttributeType2Binding
                binding.upperLine.visibility = if (data.level > 1) View.GONE else View.VISIBLE
                binding.upperLineNoMargin.visibility = if (data.level >= 4) View.VISIBLE else View.GONE
                binding.underLine.visibility = if (data.type == OBJECT_WITH_ARRAY && data.level == 0) View.VISIBLE else View.GONE
                binding.title = data.title //+ " ${data.level} ${data.type}"
                data.items?.let {
                    val mAdapter = VCAttributeAdapter()
                    binding.recyclerview.apply {
                        layoutManager = LinearLayoutManager(binding.root.context)
                        adapter = mAdapter
                    }
                    mAdapter.submitList(it)
                }

            }
            OBJECT_WITH_ARRAY.ordinal -> {
                binding as ListVcAttributeType3Binding
                binding.title = data.title //+ " ${data.level} ${data.type}"
                Log.e("OBJECT_WITH_ARRAY", "${data.items}")
                data.items?.let {
                    val mAdapter = VCAttributeAdapter()
                    binding.recyclerview.apply {
                        layoutManager = LinearLayoutManager(binding.root.context)
                        adapter = mAdapter
                    }
                    mAdapter.submitList(it)
                }

            }
            ARRAY.ordinal -> {
                binding as ListVcAttributeType4Binding
                Log.e("ARRAY", "${data.items}")
                binding.title = data.title //+ " ${data.level} ${data.type}"
                binding.setOnExpandClickListener {
                    if (binding.imageArrow.rotation == 180F) {
                        binding.imageArrow.animate().rotation(360F).duration = 100
                        binding.recyclerview.expand()
                    } else {
                        binding.imageArrow.animate().rotation(180F).duration = 100
                        binding.recyclerview.collapse()
                    }
                }
                data.items?.let {
                    val mAdapter = VCAttributeAdapter()
                    binding.recyclerview.apply {
                        layoutManager = LinearLayoutManager(binding.root.context)
                        adapter = mAdapter
                    }
                    mAdapter.submitList(it)
                }
            }
            DDD -> {
                binding as ListVcAttributeType5Binding
                data.items?.let {
                    val mAdapter = VCAttributeAdapter()
                    binding.recyclerview.apply {
                        layoutManager = LinearLayoutManager(binding.root.context)
                        adapter = mAdapter
                    }
                    mAdapter.submitList(it)
                }
            }
            EEE -> {
                binding as ListVcAttributeType6Binding
                binding.title = data.title
            }
            else -> {

            }
        }
    }

    private fun isDateFormat(value: String?): Boolean? {
        return value?.matches(Regex("([0-9]{4})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2}).*"))
    }
}