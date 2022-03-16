package co.finema.etdassi.feature.mysign.vcsign

import android.content.Intent
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.TitleAdapter
import co.finema.etdassi.common.TitleAdapterDataModel
import co.finema.etdassi.databinding.FragmentMyVcListBinding
import co.finema.etdassi.feature.mysign.data.VCSigned
import co.finema.etdassi.feature.mysign.myreject.detail.MyRejectDetailActivity
import co.finema.etdassi.feature.mysign.signrequest.SignRequestAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCDetailActivity
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class VCSignedFragment: BaseFragment<VCSignedViewModel, FragmentMyVcListBinding>(VCSignedViewModel::class.java) {
    override val mViewModel: VCSignedViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_my_vc_list

    private val mTitleAdapter = TitleAdapter()
    private val mAdapter = VCSignedAdapter()
    private val concatAdapter = ConcatAdapter(mTitleAdapter, mAdapter)

    override fun init() {
        super.init()


        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }

        mAdapter.registerOnItemClick(object :VCSignedAdapter.VCSignedListener {
            override fun onVCSignedClick(vcSigned: VCSigned) {
                mViewModel.setVCReadStatus(vcSigned.id)
                val intent = Intent(requireContext(), VCSignedDetailActivity::class.java)
                intent.putExtra("data", vcSigned)
                startActivity(intent)
            }

        })

        mViewModel.list.observe(viewLifecycleOwner, {
            mAdapter.submitList(it.sortedByDescending { vcSigned -> vcSigned.signDate })
            mTitleAdapter.submitList(listOf(TitleAdapterDataModel(it.size)))
        })

    }

    override fun onResume() {
        super.onResume()
//        concatAdapter.notifyDataSetChanged()
    }
}