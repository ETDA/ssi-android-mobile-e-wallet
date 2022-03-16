package co.finema.etdassi.feature.mysign.myreject.detail

import android.util.Log
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.databinding.FragmentMyRejectDetailBinding
import co.finema.etdassi.feature.mysign.myreject.MyRejectModel
import co.finema.etdassi.feature.myvc.myvctab.detail.TitleDescriptionAdapter
import co.finema.etdassi.feature.notification.NotificationStatus
import co.finema.etdassi.feature.sign.adapter.SignStatusCardAdapter
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyRejectDetailFragment: BaseFragment<MyRejectDetailViewModel, FragmentMyRejectDetailBinding>(
    MyRejectDetailViewModel::class.java) {

    override val mViewModel: MyRejectDetailViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_my_reject_detail

    override fun init() {
        super.init()

        arguments?.getParcelable<MyRejectModel>("data")?.let {
            mViewModel.getMyReject(it)
        }

        mBinding.myRejectToolbar.apply {
            toolbarWindowFlag.setBackgroundResource(R.drawable.second_header_bg_fade)
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
            toolbarTitle = "เอกสารที่ฉันปฏิเสธลงนาม"
        }

        val didCardAdapter = MyRejectDidCardAdapter()
        val vcCardAdapter = SignStatusCardAdapter()
        val descriptionAdapter = RejectCardMainAdapter()
        val conCardAdapter = ConcatAdapter(didCardAdapter, vcCardAdapter, descriptionAdapter)

        mBinding.myRejectRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = conCardAdapter
        }

        mViewModel.myRejectData.observe(viewLifecycleOwner, {
            didCardAdapter.submitList(listOf(MyRejectDidCardModel(it.issuer ?: "")))
            vcCardAdapter.submitList(listOf(SignStatusCardAdapter.SignStatusCard(it.vcType,
                it.status?.let { it1 -> NotificationStatus.valueOf(it1) })))
            descriptionAdapter.submitList(listOf(
                RejectCardMainAdapter.RejectCardMainModel(
                    type = RejectInfoType.INFORMATION,
                    it.vcDescription
                ),
                RejectCardMainAdapter.RejectCardMainModel(
                    type = RejectInfoType.REMARK,
                    rejectRemark = it.reason
                )
            ))
        })





    }
}