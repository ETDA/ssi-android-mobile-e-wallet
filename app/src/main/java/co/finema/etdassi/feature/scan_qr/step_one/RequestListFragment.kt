package co.finema.etdassi.feature.scan_qr.step_one

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.BiometricUtil
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import co.finema.etdassi.common.utils.GenericListener
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.common.utils.toastNormal
import co.finema.etdassi.databinding.FragmentScanRequestListBinding
import co.finema.etdassi.feature.qr_reader.usecase.QRReaderUseCase
import co.finema.etdassi.feature.scan_qr.RequestListViewModel
import co.finema.etdassi.feature.scan_qr.ScanQRListVC
import co.finema.etdassi.feature.scan_qr.step_result.ScanStepResultFragment
import co.finema.etdassi.feature.scan_qr.step_two.ScanStepSelectVCFragment
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RequestListFragment: BaseFragment<RequestListViewModel, FragmentScanRequestListBinding>(
    RequestListViewModel::class.java) {

    override val mViewModel: RequestListViewModel by sharedViewModel()

    override fun getLayoutRes() = R.layout.fragment_scan_request_list


    private lateinit var biometricResult: ActivityResultLauncher<Intent>
    private var repeatFail = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biometricResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                sendVP()

            } else {
                repeatFail += 1
                requireContext().toastNormal("ลองใหม่อีกครั้ง")
            }
        }
    }

    private fun sendVP() {

        arguments?.getParcelable<QRReaderUseCase.RequestingVPDocResponse>("result")?.let {

            createLoadingDialog()
            mViewModel.sendVP(it, object :GenericListener<Boolean> {
                override fun onSuccess(response: Boolean) {
                    dismissLoadingDialog()
                    if (response) {
                        val fragment = ScanStepResultFragment()
                        fragment.arguments = arguments
                        parentFragmentManager.animateTransition()
                            .replace(R.id.main_content, fragment)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        requireContext().toastNormal("ไม่สามารถส่งเอกสารสำแดงได้")
                    }
                }

                override fun onFail(errorMessage: String) {
                    dismissLoadingDialog()
                    requireContext().toastNormal(errorMessage)
                }

            })

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




        mBinding.btmBtn.apply {
            buttonText = "ยืนยันการเลือกเอกสาร"
            disable = true
            setOnButtonClickListener {
                val bioAuth = BiometricUtil()
                bioAuth.build(this@RequestListFragment, repeatFail, biometricResult, object : BiometricUtil.BiometricListener {
                    override fun onSuccess() {
                        sendVP()
                    }

                    override fun onFail() {
                        repeatFail += 1
                        //                            TODO("Not yet implemented")
                    }
                })

            }
        }

        val mAdapter = ScanRequestListAdapter()
        mAdapter.registerOnClick(object :ScanRequestListAdapter.ScanRequestListListener {
            override fun onVcClick(reqVcData: ScanQRListVC) {
                mViewModel.setDataOnClick(reqVcData)
                parentFragmentManager.animateTransition()
                    .add(R.id.main_content, ScanStepSelectVCFragment())
                    .addToBackStack(null)
                    .hide(this@RequestListFragment)
                    .commit()
            }

        })

        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        arguments?.getParcelable<QRReaderUseCase.RequestingVPDocResponse>("result")?.let {
            mBinding.didRequester = it.verifierDid
            mBinding.createDate = it.createAt?.toShortDateTime()
            mBinding.verifier = it.verifier
            mBinding.requestName = it.name
            val requestList = it.vpRequest?.map { vpRequest ->
                ScanQRListVC(
                    id = randomID(),
                    vcTypeName = vpRequest.type,
                    vcIdPick = null,
                    isRequest = vpRequest.isRequired
                )
            }
            if (requestList != null) {
                mViewModel.setVCRequestList(requestList)
            }
        }

        mViewModel.scanQrVcList.observe(viewLifecycleOwner, {
            mAdapter.submitList(it.toMutableList())
        })

        mViewModel.onVCSelectedPosition.observe(viewLifecycleOwner, {
            mAdapter.notifyItemChanged(it)
        })

        mViewModel.requestMainPageButton.observe(viewLifecycleOwner, {
            mBinding.btmBtn.disable = !it
            mBinding.titleBar.setBackgroundColor(ContextCompat.getColor(requireContext(), if (it)R.color.success else R.color.ice_blue))
            mBinding.titleBar.setTextColor(ContextCompat.getColor(requireContext(), if (it)R.color.white else R.color.primary))
        })

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mViewModel.checkResult()
        }
    }

    private fun randomID(): String = List(10) {
        (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
    }.joinToString("")

}