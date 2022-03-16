package co.finema.etdassi.feature.mysign.myreject

import android.content.Intent
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.TitleAdapter
import co.finema.etdassi.common.TitleAdapterDataModel
import co.finema.etdassi.databinding.FragmentMyVcListBinding
import co.finema.etdassi.feature.mysign.myreject.detail.MyRejectDetailActivity
import co.finema.etdassi.feature.myvc.myvptab.MyVPAdapter
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyRejectFragment: BaseFragment<MyRejectViewModel, FragmentMyVcListBinding>(MyRejectViewModel::class.java) {
    override val mViewModel: MyRejectViewModel by viewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_vc_list
    }

    override fun init() {
        super.init()

        mBinding.editTextFiled.setText("Reject")

        val mAdapter = MyRejectAdapter()
        val titleAdapter = TitleAdapter()
        val concatAdapter = ConcatAdapter(titleAdapter, mAdapter)

        mAdapter.registerMyRejectListener(object :MyRejectAdapter.MyRejectAdapterListener {
            override fun onItemClick(myRejectModel: MyRejectModel) {
                val intent = Intent(requireContext(), MyRejectDetailActivity::class.java)
                intent.putExtra("data", myRejectModel)
                startActivity(intent)
            }

        })

        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }

        mViewModel.vcList.observe(viewLifecycleOwner, {
            mAdapter.submitList(it)
            titleAdapter.submitList(listOf(TitleAdapterDataModel(it.size)))
        })


    }


}