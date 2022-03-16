package co.finema.etdassi.feature.scan_qr.step_three

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.enum.BioAuthType
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.common.utils.toastNormal
import co.finema.etdassi.databinding.FragmentScanRequestListBinding
import co.finema.etdassi.feature.bioauth.BioAuthActivity
import co.finema.etdassi.feature.scan_qr.RequestListViewModel
import co.finema.etdassi.feature.scan_qr.ScanQRListVC
import co.finema.etdassi.feature.scan_qr.step_one.ScanRequestListAdapter
import co.finema.etdassi.feature.scan_qr.step_result.ScanStepResultFragment
import co.finema.etdassi.feature.scan_qr.step_two.ScanStepSelectVCFragment
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ScanStepConfirmFragment: BaseFragment<RequestListViewModel, FragmentScanRequestListBinding>(
    RequestListViewModel::class.java) {
    override val mViewModel: RequestListViewModel by sharedViewModel()

    override fun getLayoutRes() = R.layout.fragment_scan_request_list

    private lateinit var biometricResult: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biometricResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                parentFragmentManager.animateTransition()
                    .replace(R.id.main_content, ScanStepResultFragment())
                    .addToBackStack(null)
                    .commit()

            } else {
                requireContext().toastNormal("ลองใหม่อีกครั้ง")
            }
        }
    }

    override fun init() {
        super.init()



        mBinding.toolbar.apply {
            toolbarTitle = "เอกสารที่ได้รับการร้องขอ"
            toolbarWindowFlag.setBackgroundResource(R.drawable.second_header_bg_fade)
            setOnBackClickListener {
                requireActivity().finish()
            }
        }

        mBinding.stepTitle = "ส่วนที่ 3/3 : ตรวจสอบเอกสารและลงลายมือชื่อดิจิทัล"
        mBinding.didRequester = "did:example:123xxxxxxxxxxxxx76"
        mBinding.createDate = "19 มี.ค. 64 เวลา 14:00 น."
        mBinding.btmBtn.apply {
            buttonText = "ยืนยันการเลือกเอกสาร"
            disable = true
            setOnButtonClickListener {
                val intent = Intent(requireContext(), BioAuthActivity::class.java)
                intent.putExtra("type", BioAuthType.SECRET_KEY)
                biometricResult.launch(intent)
            }
        }

        val mAdapter = ScanRequestListAdapter()

        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        mViewModel.scanQrVcList.observe(viewLifecycleOwner, {
            mAdapter.submitList(it.toMutableList())
        })

        mViewModel.requestMainPageButton.observe(viewLifecycleOwner, {
            mBinding.btmBtn.disable = !it
            mBinding.titleBar.setBackgroundColor(ContextCompat.getColor(requireContext(), if (it)R.color.success else R.color.ice_blue))
            mBinding.titleBar.setTextColor(ContextCompat.getColor(requireContext(), if (it)R.color.white else R.color.primary))
        })

    }
}