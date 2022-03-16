package co.finema.etdassi.feature.sign

import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.databinding.FragmentSignResultBinding
import co.finema.etdassi.feature.myvc.myvctab.detail.TitleDescriptionAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCAttributeAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCDetailAdapter
import co.finema.etdassi.feature.sign.adapter.SignedResultAdapter
import co.finema.etdassi.feature.sign.adapter.SignedResultCardAdapter
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignResultFragment: BaseFragment<SignViewModel, FragmentSignResultBinding>(SignViewModel::class.java) {
    override val mViewModel: SignViewModel by sharedViewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_sign_result
    }

    override fun init() {
        super.init()
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val titleAdapter = SignedResultAdapter()
        val mAdapter = SignedResultCardAdapter()
        val concatAdapter = ConcatAdapter(titleAdapter, mAdapter)
        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }


        titleAdapter.submitList(listOf(resources.getString(R.string.fragment_sign_result_01)))

        arguments?.getParcelable<SignViewModel.SignResultPageData>("data")?.let {
            mAdapter.submitList(listOf(it))
        }



        mBinding.button.apply {
            buttonText = "กลับสู่หน้าหลัก"
            setOnButtonClickListener {
                requireActivity().finish()
            }
        }

    }
}