package co.finema.etdassi.feature.scan_qr.step_result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseAdapter
import co.finema.etdassi.databinding.ListCreateVpDidBinding
import co.finema.etdassi.feature.scan_qr.ScanQRListVC

class CreateVPResultDidAdapter: BaseAdapter<CreateVPResultDidAdapter.Param>(listener) {
    data class Param(
        val issuer: String?,
        val requestDate: String?,
        val scanQRListVC: List<ScanQRListVC>?,
        val requestName: String? = null,
        val verifier: String? = null
    )
    companion object {
        val listener = object :DiffUtil.ItemCallback<Param>() {
            override fun areItemsTheSame(oldItem: Param, newItem: Param): Boolean {
                return oldItem.issuer == newItem.issuer
            }

            override fun areContentsTheSame(oldItem: Param, newItem: Param): Boolean {
                return oldItem.issuer == newItem.issuer
            }
        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.list_create_vp_did, parent, false)
    }

    override fun bind(binding: ViewDataBinding, position: Int) {
        binding as ListCreateVpDidBinding
        binding.didRequester = getItem(position).issuer
        binding.createDate = getItem(position).requestDate
        binding.verifier = getItem(position).verifier
        binding.requestName = getItem(position).requestName
        val scanStepResultAdapter = ScanStepResultAdapter()
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = scanStepResultAdapter
        }
        scanStepResultAdapter.submitList(getItem(position).scanQRListVC)
    }


}