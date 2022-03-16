package co.finema.etdassi.feature.notification

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.FragmentNotificationListBinding
import co.finema.etdassi.feature.sign.SignFragment
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import co.finema.etdassi.feature.notification.NotificationListAdapter.NotificationAdapterModel
import co.finema.etdassi.feature.notification.NotificationStatus.*

class NotificationListFragment: BaseFragment<NotificationListViewModel, FragmentNotificationListBinding>(NotificationListViewModel::class.java) {
    override val mViewModel: NotificationListViewModel by viewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_notification_list
    }

    override fun init() {
        super.init()

        mBinding.toolbar.apply {
            toolbarTitle = "แจ้งเตือนทั้งหมด"
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
        }

        val mAdapter = NotificationItemAdapter()
        mAdapter.registerNotificationClickListener(object :NotificationListAdapter.NotificationListener {
            override fun onItemClick(notificationModel: NotificationAdapterModel) {
                mViewModel.setReadStatus(notificationModel.notificationId!!)
                when(notificationModel.status) {
                    PENDING -> {
                        val fragment =  SignFragment()
                        val bundle = Bundle()
                        bundle.putString("notiID", notificationModel.notificationId)
                        fragment.arguments = bundle
                        parentFragmentManager.animateTransition()
                            .replace(R.id.main_content, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                    else -> {

                    }
                }

//                if(notificationModel.status != "pending") {
////                    val fragment = VCDetailFragment()
////                    val bundle = Bundle()
////                    bundle.putParcelable("data", MyVCListAdapter.VCViewAdapterItem(
////                        id = "mock",
////                        vcTypeName = notificationModel.vcName,
////                        vcStatus = notificationModel.status
////                    )
////                    )
////                    fragment.arguments = bundle
////                    parentFragmentManager.animateTransition()
////                        .replace(R.id.main_content, fragment)
////                        .addToBackStack(null)
////                        .commit()
//                } else {
//
//                }
            }

        })

        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        mViewModel.notificationList.observe(viewLifecycleOwner, {
            mAdapter.submitList(it)
        })



    }
}