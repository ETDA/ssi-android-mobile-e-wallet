package co.finema.etdassi.feature.myvc.myvptab.detail

import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.FragmentMyVpDetailBinding
import co.finema.etdassi.feature.myvc.myvctab.MyVCListAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCDetailFragment
import co.finema.etdassi.feature.myvc.myvptab.MyVPAdapter
import co.finema.etdassi.feature.myvc.myvptab.model.VPListModel
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import co.finema.etdassi.feature.myvc.myvptab.MyVPAdapter.*

class MyVPDetailFragment: BaseFragment<MyVPDetailViewModel, FragmentMyVpDetailBinding>(MyVPDetailViewModel::class.java) {

    companion object {
        fun setArguments(args: Bundle?): MyVPDetailFragment {
            val fragment = MyVPDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val mViewModel: MyVPDetailViewModel by viewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_vp_detail
    }

    override fun init() {
        super.init()
            arguments?.getParcelable<VPDocumentAdapterModel>("data")?.let {
                mBinding.vpGroupName = it.groupName
                mBinding.requestDate = it.date?.toShortDateTime()
                mViewModel.getVPDocument(it)
            }

        mViewModel.didRequester.observe(viewLifecycleOwner, {
            mBinding.requesterDidAddress = it
        })
        mBinding.vpDetailToolbar.apply {
            toolbarTitle = "เอกสารสำแดงที่ฉันลงนาม"
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
        }

        val mAdapter = MyVPDetailAdapter()
        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        mAdapter.registerOnClickLister(object :MyVPDetailAdapter.MyVPDetailListener {
            override fun onItemClick(data: VPListModel) {
                val args = Bundle()
                args.putString("cid", data.cid)
                parentFragmentManager.animateTransition()
                    .replace(R.id.main_content, VCDetailFragment.setArguments(args))
                    .addToBackStack(null)
                    .commit()
            }

        })

        mViewModel.vpList.observe(viewLifecycleOwner, {
            mAdapter.submitList(it)
        })


    }
}