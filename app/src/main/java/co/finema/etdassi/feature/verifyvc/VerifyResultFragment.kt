package co.finema.etdassi.feature.verifyvc

import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import co.finema.etdassi.databinding.FragmentSignResultBinding
import co.finema.etdassi.feature.myvc.myvctab.detail.VCDetailAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCDetailViewModel
import co.finema.etdassi.feature.pageloading.usecase.VerifyVPUseCase
import co.finema.etdassi.feature.scan_qr.step_result.CreateVPResultHeaderAdapter
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class VerifyResultFragment: BaseFragment<VerifyResultViewModel, FragmentSignResultBinding>(VerifyResultViewModel::class.java) {
    override val mViewModel: VerifyResultViewModel by viewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_sign_result
    }

    override fun init() {
        super.init()

        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val titleAdapter = CreateVPResultHeaderAdapter()
        val vcCard = VerifyResultStatusCardAdapter()
        val mAdapter = VerifyResultCardAdapter()
        val concatAdapter = ConcatAdapter(titleAdapter, vcCard, mAdapter)
        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }

        arguments?.getParcelable<VerifyVPUseCase.QRVerifyResult>("verifyResult")?.let {
            if (it.verifyStatus) {
                titleAdapter.submitList(listOf(
                    CreateVPResultHeaderAdapter.Param(
                        title = "การตรวจสอบเอกสารเสร็จสมบูรณ์",
                        description = "เอกสารได้รับการตรวจสอบความถูกต้อง",
                        iconType = true
                    )
                ))
                vcCard.submitList(listOf(VerifyResultStatusCardAdapter.Param(
                    vcType = it.vcType ?: "",
                    status = it.vcStatus
                )))
                mAdapter.submitList(listOf(VerifyResultCardAdapter.Param(it.description)))
            } else {
                titleAdapter.submitList(listOf(
                    CreateVPResultHeaderAdapter.Param(
                        title = "การตรวจสอบเอกสารเสร็จสมบูรณ์",
                        description = "เอกสารไม่ถูกต้องกับข้อมูลจากผู้ออกเอกสาร",
                        iconType = false
                    )
                ))
            }
        }




        mBinding.button.apply {
            buttonText = "กลับสู่หน้าหลัก"
            setOnButtonClickListener {
                requireActivity().finish()
            }
        }
    }
}