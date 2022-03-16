package co.finema.etdassi.feature.myvc.myvctab.detail

import android.os.Bundle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.FragmentVcDetailBinding
import co.finema.etdassi.feature.myvc.myvctab.MyVCListAdapter
import co.finema.etdassi.feature.myvc.myvctab.qrcodedetail.VCDetailQRFragment
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class VCDetailFragment: BaseFragment<VCDetailViewModel, FragmentVcDetailBinding>(VCDetailViewModel::class.java) {

    companion object {
        fun setArguments(args: Bundle?): VCDetailFragment {
            val fragment = VCDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val mViewModel: VCDetailViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_vc_detail

    override fun init() {
        super.init()

        val cardAdapter = VCCardAdapter()
        val titleAdapter = TitleDescriptionAdapter()
        val mAdapter = VCAttributeAdapter()
        val concatAdapter = ConcatAdapter(cardAdapter, titleAdapter, mAdapter)
        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }


        mViewModel.vcDocumentCardStatus.observe(viewLifecycleOwner, {
            cardAdapter.submitList(listOf(it))
            mBinding.vcDetailToolbar.setOnCloseListener { view->
                val fragment = VCDetailQRFragment()
                arguments?.putString("vcType", it.vcType)
                arguments?.putString("vcStatus", it.vcStatus)
                fragment.arguments = arguments
                parentFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(null)
                    .commit()
            }

        })

        titleAdapter.submitList(listOf(resources.getString(R.string.title_vc_description)))

        mViewModel.vcDocumentDescription.observe(viewLifecycleOwner, {
            mAdapter.submitList(it)
        })

        arguments?.getString("cid")?.let {
            mViewModel.getVcDocumentByCid(it)
        }

        mBinding.vcDetailToolbar.apply {
            toolbarTitle = getString(R.string.title_my_doc)
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
            imageIc.setImageResource(R.drawable.ic_qr_code_toolbar)

        }
    }


}