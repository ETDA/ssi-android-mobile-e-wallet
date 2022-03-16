package co.finema.etdassi.feature.scan_qr.step_two

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.databinding.FragmentScanStepVcDetailBinding
import co.finema.etdassi.feature.myvc.myvctab.detail.TitleDescriptionAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCAttributeAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCCardAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCDetailAdapter
import co.finema.etdassi.feature.scan_qr.RequestListViewModel
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ScanStepVCDetailFragment: BaseFragment<RequestListViewModel, FragmentScanStepVcDetailBinding>(RequestListViewModel::class.java) {
    override val mViewModel: RequestListViewModel by sharedViewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_scan_step_vc_detail
    }

    override fun init() {
        super.init()

        mBinding.stepListToolbar.apply {
            toolbarWindowFlag.setBackgroundResource(R.drawable.second_header_bg_fade)
            toolbarTitle = "เอกสารที่ได้รับการร้องขอ"
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
        }

        val cardAdapter = VCCardAdapter()
        val titleAdapter = TitleDescriptionAdapter()
        val mAdapter = VCAttributeAdapter()
        val concatAdapter = ConcatAdapter(cardAdapter, titleAdapter, mAdapter)
        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }


        mViewModel.vcDocumentCardStatus.observe(viewLifecycleOwner, {
            cardAdapter.submitList(it)
        })

        titleAdapter.submitList(listOf(resources.getString(R.string.title_vc_description)))

        mViewModel.vcDocumentDescription.observe(viewLifecycleOwner, {
            mAdapter.submitList(it)
        })

        arguments?.getString("cid")?.let {
            mViewModel.getVcDocumentByCid(it)
        }

        mViewModel.vcDetailData.observe(viewLifecycleOwner, { vcDetailData ->
            mBinding.botton.apply {
                isNotActive = true
                buttonText = "เลือกเอกสาร"
                setOnButtonClickListener {
//                    mViewModel.isDetailSelect = true
                    mViewModel.onRadioBoxClick(vcDetailData!!)
                    mViewModel.setDataSelect()
                    requireActivity().onBackPressed()
                }
            }
        })

    }
}