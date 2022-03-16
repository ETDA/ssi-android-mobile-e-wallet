package co.finema.etdassi.feature.mysign.vcsign

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.databinding.FragmentVcsignedDetailBinding
import co.finema.etdassi.feature.mysign.data.VCSigned
import co.finema.etdassi.feature.myvc.myvctab.detail.TitleDescriptionAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCAttributeAdapter
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class VCSignedDetailFragment: BaseFragment<VCSignedDetailViewModel, FragmentVcsignedDetailBinding>(VCSignedDetailViewModel::class.java) {

    override val mViewModel: VCSignedDetailViewModel by viewModel()
    override fun getLayoutRes() = R.layout.fragment_vcsigned_detail

    override fun init() {
        super.init()

        mBinding.toolbar.apply {
            toolbarTitle = "เอกสารรับรองที่ฉันลงนาม"
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
            toolbarWindowFlag.setBackgroundResource(R.drawable.second_header_bg_fade)
        }

        val revokeCardAdapter = MySignedRevokeCardAdapter()
        val titleAdapter = TitleDescriptionAdapter()
        val mAdapter = VCAttributeAdapter()
        val concatAdapter = ConcatAdapter(revokeCardAdapter, titleAdapter, mAdapter)

        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = concatAdapter
        }

        mViewModel.description.observe(viewLifecycleOwner, {
            mAdapter.submitList(it)
        })
        titleAdapter.submitList(listOf("รายละเอียด"))
        arguments?.getParcelable<VCSigned>("data")?.let { vcSigned ->
            mViewModel.getVCDescription(vcSigned)
            revokeCardAdapter.submitList(listOf(MySignedRevokeCardAdapter.AdapterModel(cid = vcSigned.id, vcType = vcSigned.name, signedDate = vcSigned.signDate, status = vcSigned.status)))
        }



        revokeCardAdapter.registerOnRevokeClick(object :MySignedRevokeCardAdapter.RevokeListener {
            override fun onItemClickListener(revokeData: MySignedRevokeCardAdapter.AdapterModel) {
                mViewModel.revokeVC(revokeData, object: RevokeVCListener{
                    override fun onFail(errorMessage: String) {
                        Toast.makeText(activity?.applicationContext, "Error : $errorMessage", Toast.LENGTH_LONG).show()
                    }

                    override fun onSuccess(vcSigned: VCSigned) {

                        mViewModel.getVCDescription(vcSigned)
                        revokeCardAdapter.submitList(listOf(MySignedRevokeCardAdapter.AdapterModel(vcSigned.id,vcSigned.name,vcSigned.signDate,vcSigned.status)))

                        fragmentManager?.beginTransaction()?.detach(this@VCSignedDetailFragment)?.attach(this@VCSignedDetailFragment)?.commit()
                    }

                })
            }

        })




    }
}