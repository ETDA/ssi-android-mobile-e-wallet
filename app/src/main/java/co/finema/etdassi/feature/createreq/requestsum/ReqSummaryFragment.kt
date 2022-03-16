package co.finema.etdassi.feature.createreq.requestsum

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.FragmentStepListBinding
import co.finema.etdassi.feature.createreq.requestqr.ReqQrCodeSummaryFragment
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReqSummaryFragment:BaseFragment<ReqSummaryViewModel, FragmentStepListBinding>(ReqSummaryViewModel::class.java) {
    override val mViewModel: ReqSummaryViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_step_list

    override fun init() {
        super.init()

        mBinding.stepListToolbar.apply {
            toolbarWindowFlag.setBackgroundResource(R.drawable.second_header_bg_fade)
            toolbarTitle = getString(R.string.toolbar_create_request_vc)
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
        }

        mBinding.stepTitle = getString(R.string.create_request_step_03_title)

        mBinding.stepListButton.apply {
            buttonText = "ขอเอกสาร"
            setOnButtonClickListener {
                val list = ReqSummaryList(mViewModel.vcList.value?.filter { it.count == null })
                val fragment = ReqQrCodeSummaryFragment()
                val args = Bundle()
                args.putParcelable("data", list)
                fragment.arguments = args
                parentFragmentManager.animateTransition()
                    .add(R.id.main_content, fragment)
                    .addToBackStack(null)
                    .hide(this@ReqSummaryFragment)
                    .commit()
            }
        }

        val mAdapter = ReqSummaryAdapter()

        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            setBackgroundResource(R.color.bg_color)
        }

        mViewModel.vcList.observe(viewLifecycleOwner, {
            mAdapter.submitList(it.toMutableList())
        })

        mViewModel.setRequestList(arguments?.getParcelable("data"))

    }

}