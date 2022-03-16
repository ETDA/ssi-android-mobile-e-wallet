package co.finema.etdassi.feature.createreq.requestlist

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.FragmentStepListBinding
import co.finema.etdassi.feature.createreq.listofvc.ListOfVCModelList
import co.finema.etdassi.feature.createreq.requestsum.ReqSummaryFragment
import co.finema.etdassi.feature.createreq.requestsum.ReqSummaryList
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReqStepListFragment: BaseFragment<ReqStepListViewModel, FragmentStepListBinding>(ReqStepListViewModel::class.java) {
    override val mViewModel: ReqStepListViewModel by viewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_step_list
    }

    private val isListIsEmpty = MutableLiveData<Boolean>()

    override fun init() {
        super.init()
        mBinding.stepListToolbar.apply {
            toolbarWindowFlag.setBackgroundResource(R.drawable.second_header_bg_fade)
            toolbarTitle = getString(R.string.toolbar_create_request_vc)
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
        }

        mBinding.stepListButton.apply {
            buttonText = "สร้างคำร้อง"
            setOnButtonClickListener {
                val list = ReqSummaryList(mViewModel.vcList.value?.toList())
                val fragment = ReqSummaryFragment()
                val args = Bundle()
                args.putParcelable("data", list)
                fragment.arguments = args
                parentFragmentManager.animateTransition()
                    .add(R.id.main_content, fragment)
                    .addToBackStack(null)
                    .hide(this@ReqStepListFragment)
                    .commit()
            }
        }

        mBinding.stepTitle = getString(R.string.create_request_step_02_title)

        isListIsEmpty.observe(viewLifecycleOwner, { isEmpty ->
            if (isEmpty) {
                mBinding.setOnAddListClickListener {
                    requireActivity().onBackPressed()
                }
                mBinding.stepListButton.disable = true
            }
        })

        val mAdapter = ReqStepListAdapter()
        mAdapter.registerOnDelete(object :ReqStepListAdapter.ReqStepListener {
            override fun onDelete(id: String) {
                mViewModel.deleteItemById(id)
                mAdapter.notifyItemChanged(0)
            }
        })

        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            setBackgroundResource(R.color.bg_color)
        }

        mViewModel.vcList.observe(viewLifecycleOwner, {
            mAdapter.submitList(it.toMutableList())
            isListIsEmpty.value = it.isNullOrEmpty()

        })

        mViewModel.setRequestList(arguments?.getParcelable("data"))


    }
}