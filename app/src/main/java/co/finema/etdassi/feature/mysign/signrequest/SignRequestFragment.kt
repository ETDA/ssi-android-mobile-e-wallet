package co.finema.etdassi.feature.mysign.signrequest

import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.TitleAdapter
import co.finema.etdassi.common.TitleAdapterDataModel
import co.finema.etdassi.databinding.FragmentMyVcListBinding
import co.finema.etdassi.feature.mysign.data.SignRequestModel
import co.finema.etdassi.feature.sign.SignActivity
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignRequestFragment: BaseFragment<SignRequestViewModel, FragmentMyVcListBinding>(SignRequestViewModel::class.java) {
    override val mViewModel: SignRequestViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_my_vc_list

    private val mAdapter = SignRequestAdapter()
    private val titleAdapter = TitleAdapter()
    private val conCatAdapter = ConcatAdapter(titleAdapter,mAdapter)

    override fun init() {
        super.init()

        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = conCatAdapter
        }

        mAdapter.registerVCDataClick(object :SignRequestAdapter.SignDataListener {
            override fun onItemClick(signRequestModel: SignRequestAdapter.SignRequestAdapterModel) {
                mViewModel.setReadStatus(signRequestModel.id.toString())
                val intent = Intent(requireContext(), SignActivity::class.java)
                intent.putExtra("notiID", signRequestModel.id.toString())
                startActivity(intent)
            }

        })

        mViewModel.list.observe(viewLifecycleOwner, {
            Log.d("SignRequestFragment", "$it")
            mAdapter.submitList(it)
            titleAdapter.submitList(listOf(TitleAdapterDataModel(it.size)))
        })

    }

}