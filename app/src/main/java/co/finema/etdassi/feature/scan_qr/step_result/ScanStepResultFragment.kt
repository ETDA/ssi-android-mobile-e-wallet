package co.finema.etdassi.feature.scan_qr.step_result

import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import co.finema.etdassi.databinding.FragmentScanStepResultBinding
import co.finema.etdassi.feature.qr_reader.usecase.QRReaderUseCase
import co.finema.etdassi.feature.scan_qr.RequestListViewModel
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ScanStepResultFragment: BaseFragment<RequestListViewModel, FragmentScanStepResultBinding>(
    RequestListViewModel::class.java) {
    override val mViewModel: RequestListViewModel by sharedViewModel()

    override fun getLayoutRes() = R.layout.fragment_scan_step_result

    override fun init() {
        super.init()
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR


        val headerAdapter = CreateVPResultHeaderAdapter()
        val createVPResultCardAdapter = CreateVPResultCardAdapter()
        val concatAdapter = ConcatAdapter(headerAdapter, createVPResultCardAdapter)
        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }

        mViewModel.scanQrVcList.observe(viewLifecycleOwner, { list ->
            println("dddd => $list")
            headerAdapter.submitList(listOf(CreateVPResultHeaderAdapter.Param(
                title = resources.getString(R.string.scan_step_result_title_01),
                description = resources.getString(R.string.scan_step_result_title_02)
            )))
            arguments?.getParcelable<QRReaderUseCase.RequestingVPDocResponse>("result")?.let { vpDocResponse ->
                mBinding.didRequester = vpDocResponse.verifierDid
                mBinding.createDate = vpDocResponse.createAt?.toShortDateTime()
                createVPResultCardAdapter.submitList(listOf(
                    CreateVPResultCardAdapter.Param(
                        title = resources.getString(R.string.scan_step_result_title_03),
                        didCardModel = CreateVPResultDidAdapter.Param(
                            issuer = vpDocResponse.verifierDid,
                            requestDate = vpDocResponse.createAt?.toShortDateTime(),
                            scanQRListVC = list.filter { it.vcIdPick != null },
                            verifier = vpDocResponse.verifier,
                            requestName = vpDocResponse.name
                        )
                    )
                ))
            }

        })

        mBinding.button.apply {
            buttonText = "กลับสู่หน้าหลัก"
            setOnButtonClickListener {
                requireActivity().finish()
            }
        }

    }
}