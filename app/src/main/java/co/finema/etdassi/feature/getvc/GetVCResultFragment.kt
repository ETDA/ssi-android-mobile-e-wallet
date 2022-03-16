package co.finema.etdassi.feature.getvc

import android.os.Bundle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.FragmentGetVcResultBinding
import co.finema.etdassi.feature.getvc.adapter.SenderAdapter
import co.finema.etdassi.feature.getvc.adapter.TitleDescriptionGetVCAdapter
import co.finema.etdassi.feature.getvc.adapter.VCItemListAdapter
import co.finema.etdassi.feature.getvc.usecase.GetVCUseCase
import co.finema.etdassi.feature.myvc.myvctab.detail.VCDetailFragment
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GetVCResultFragment: BaseFragment<GetVCLoadingViewModel, FragmentGetVcResultBinding>(GetVCLoadingViewModel::class.java) {
    override val mViewModel: GetVCLoadingViewModel by sharedViewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_get_vc_result
    }

    override fun init() {
        super.init()

        val titleAdapter = TitleDescriptionGetVCAdapter()
        val senderAdapter = SenderAdapter()
        val vcListAdapter = VCItemListAdapter()
        val concatAdapter = ConcatAdapter(titleAdapter, senderAdapter, vcListAdapter)
        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }

        titleAdapter.submitList(listOf(TitleDescriptionGetVCAdapter.TitleString("รายละเอียดการรับเอกสารรับรอง")))
        mViewModel.vcGettingResult.observe(viewLifecycleOwner, {
            senderAdapter.submitList(listOf(it.senderData))
            vcListAdapter.submitList(it.cidDocList)
        })

        vcListAdapter.registerOnVCItemDetailClickListener(object :VCItemListAdapter.VCItemList {
            override fun onClickListener(cidDoc: GetVCUseCase.VCSavingCount.CIDDoc) {
                val fragment = VCDetailFragment()
                val args = Bundle()
                args.putString("cid", cidDoc.cid)
                fragment.arguments = args
                parentFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(null)
                    .commit()
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