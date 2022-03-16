package co.finema.etdassi.feature.scan_qr.step_two

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.TitleAdapter
import co.finema.etdassi.common.TitleAdapterDataModel
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.FragmentStepListBinding
import co.finema.etdassi.feature.myvc.myvctab.detail.VCDetailFragment
import co.finema.etdassi.feature.scan_qr.RequestListViewModel
import co.finema.etdassi.feature.scan_qr.ScanStepSelectVCList
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ScanStepSelectVCFragment: BaseFragment<RequestListViewModel, FragmentStepListBinding>(RequestListViewModel::class.java) {
    override val mViewModel: RequestListViewModel by sharedViewModel()

    override fun getLayoutRes() = R.layout.fragment_step_list

    val titleVCCountAdapter = TitleAdapter()
    val mAdapter = ScanStepSelectVCAdapter()

    override fun init() {
        super.init()
//        mBinding.filter.visibility = View.VISIBLE
        mBinding.stepListToolbar.apply {
            toolbarWindowFlag.setBackgroundResource(R.drawable.second_header_bg_fade)
            toolbarTitle = "เอกสารที่ได้รับการร้องขอ"
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
        }


        mAdapter.registerRadioBoxOnClick(object :ScanStepSelectVCAdapter.VCRadioBoxListener {
            override fun onRadioBoxCheck(vcData: ScanStepSelectVCList, position: Int) {
                mViewModel.oldPosition()?.also { mAdapter.notifyItemChanged(it) }
                mViewModel.onRadioBoxClick(vcData)
                mAdapter.notifyItemChanged(position)
            }

            override fun onVCDetailClick(vcData: ScanStepSelectVCList, position: Int) {
                mViewModel.vcDetailData.value = vcData
                mViewModel.vcDetailPosition = position
                val fragment = ScanStepVCDetailFragment()
                val args = Bundle()
                args.putString("cid", vcData.cid)
                fragment.arguments = args
                parentFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        })

        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        mViewModel.apply {
            stepSelectVcList.observe(viewLifecycleOwner, {
                mAdapter.submitList(it)
                titleVCCountAdapter.submitList(listOf(TitleAdapterDataModel(itemCount = it.size)))
                if (it.isNullOrEmpty()) {
                    mViewModel.stepSelectButtonEnable.value = false
                } else if (it.first().isSelect) {
                    mViewModel.stepSelectButtonEnable.value = true
                }
            })
            reqVcData.observe(viewLifecycleOwner, {
                println("ScanStepSelectVCFragment => $it")
                mBinding.requestVcTypeName = it.vcTypeName
                it.vcTypeName?.let { vcType -> mViewModel.getStepSelectVcList(vcType) }
            })
        }


        mBinding.stepListButton.apply {
            buttonText = "เลือกเอกสาร"
            setOnButtonClickListener {
                mViewModel.setDataSelect()
                requireActivity().onBackPressed()
            }
            disable = true
        }

        mViewModel.stepSelectButtonEnable.observe(viewLifecycleOwner, {
            mBinding.stepListButton.disable = !it
        })

    }

    override fun onResume() {
        super.onResume()
//        if (mViewModel.isDetailSelect) {
//            mViewModel.oldPosition()?.also { mAdapter.notifyItemChanged(it) }
//            mViewModel.onRadioBoxClick(mViewModel.vcDetailData.value!!)
//            mAdapter.notifyItemChanged(mViewModel.vcDetailPosition)
//            mViewModel.isDetailSelect = false
//        }
    }
}