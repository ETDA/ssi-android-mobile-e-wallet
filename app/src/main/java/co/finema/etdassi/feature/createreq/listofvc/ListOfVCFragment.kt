package co.finema.etdassi.feature.createreq.listofvc

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.common.utils.toastNormal
import co.finema.etdassi.databinding.FragmentStepListBinding
import co.finema.etdassi.feature.createreq.requestlist.ReqStepListFragment
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListOfVCFragment: BaseFragment<ListOfVCViewModel, FragmentStepListBinding>(ListOfVCViewModel::class.java) {
    override val mViewModel: ListOfVCViewModel by viewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_step_list
    }

    override fun init() {
        super.init()
        mBinding.stepListToolbar.apply {
            toolbarWindowFlag.setBackgroundResource(R.drawable.second_header_bg_fade)
            toolbarTitle = getString(R.string.toolbar_create_request_vc)
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
        }

        mBinding.stepTitle = getString(R.string.create_request_step_01_title)
        mBinding.filter.visibility = View.VISIBLE

        mBinding.stepListButton.apply {
            setOnButtonClickListener {
                val listCheck = ListOfVCModelList(mViewModel.vcList.value?.filter { it.isCheck })
                val fragment = ReqStepListFragment()
                val args = Bundle()
                args.putParcelable("data", listCheck)
                fragment.arguments = args
                parentFragmentManager.animateTransition()
                    .add(R.id.main_content, fragment)
                    .addToBackStack(null)
                    .hide(this@ListOfVCFragment)
                    .commit()
            }
            buttonText = getString(R.string.confirm)
            disable = true
        }

        val mAdapter = ListOfVCAdapter()

        mAdapter.listenerCallback(object :ListOfVCAdapter.Listener {
            override fun isOnClick() {
                mBinding.stepListButton.disable = mViewModel.vcList.value?.firstOrNull(){it.isCheck} == null
            }

        })

        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        mViewModel.vcList.observe(viewLifecycleOwner, {
            mAdapter.submitList(it)
        })

        mViewModel.getVCList()
    }
}