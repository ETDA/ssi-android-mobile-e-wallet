package co.finema.etdassi.feature.createreq.requestqr

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.QRCodeUtil
import co.finema.etdassi.databinding.FragmentReqQrSummaryBinding
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReqQrCodeSummaryFragment: BaseFragment<ReqQrCodeSummaryViewModel, FragmentReqQrSummaryBinding>(ReqQrCodeSummaryViewModel::class.java) {
    override val mViewModel: ReqQrCodeSummaryViewModel by viewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_req_qr_summary
    }

    override fun init() {
        super.init()

        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        mBinding.stepListButton.apply {
            buttonText = "กลับสู่หน้าหลัก"
            setOnButtonClickListener {
                requireActivity().finish()
            }
        }

        //TODO Waiting String to Build bitmap
        mBinding.imageQr.setImageBitmap(QRCodeUtil.getQrCodeBitmap("Waiting API"))

        val mAdapter = ReqQrCodeSummaryAdapter()

        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        mViewModel.vcList.observe(viewLifecycleOwner, {
            mAdapter.submitList(it.toMutableList())
        })

        mViewModel.setRequestList(arguments?.getParcelable("data"))
    }


}