package co.finema.etdassi.feature.home

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.BottomSheetFragment
import co.finema.etdassi.common.utils.DialogType
import co.finema.etdassi.databinding.FragmentHomeBinding
import co.finema.etdassi.feature.notification.NotificationListActivity
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.collections.ArrayList

import com.google.android.material.bottomnavigation.BottomNavigationView




class HomeFragment : BaseFragment<HomeViewModel,FragmentHomeBinding>(HomeViewModel::class.java) {

    override val mViewModel: HomeViewModel by viewModel()
    override fun getLayoutRes() = R.layout.fragment_home

    override fun init() {
        super.init()

        mViewModel.didAddress.observe(viewLifecycleOwner, { didAddress->
            mBinding.didAddress = didAddress
        })

        mViewModel.vcCount.observe(viewLifecycleOwner, { myVc->
            mBinding.myVc = if (myVc>=1) myVc.toString() else "0"
        })

        mViewModel.myRequestVc.observe(viewLifecycleOwner, { myRequestVc->
            mBinding.myRequest = myRequestVc
        })

        mBinding.setOnNotificationClickListener {
            val intent = Intent(requireContext(), NotificationListActivity::class.java)
            startActivity(intent)
        }

        mViewModel.notificationBadge.observe(viewLifecycleOwner, { notificationBadge ->
            if (notificationBadge > 0) {
                mBinding.notificationBadge.visibility = View.VISIBLE
                mBinding.notificationCount.text = notificationBadge.toString()
            } else {
                mBinding.notificationBadge.visibility = View.GONE
            }
        })

        mBinding.setOnSeeMoreCliickListener {
            val myVC = (requireActivity().findViewById<View>(R.id.bottomAppBar) as BottomNavigationView).findViewById<View>(R.id.navigation_my_vc)
            myVC.performClick()
        }



        mBinding.notificationIcon.visibility = View.VISIBLE
        mBinding.etdaLogo.setOnClickListener {
            val bottomSheetDialog = BottomSheetFragment.newInstance(DialogType.DID_INFO)
            bottomSheetDialog.show(parentFragmentManager, "Bottom Sheet Dialog Fragment")
        }

        mBinding.setOnCreateRequestClickListener {

            //TODO TEST DATE PICKER DIALOG
            val dialogTheme = resolveOrThrow(requireContext(), R.attr.materialCalendarTheme)

//            val intent = Intent(requireContext(), CreateReqVCActivity::class.java)
//            startActivity(intent)
        }

        val requestToSignAdapter = HomeVcAdapter()
        val signAdapter = HomeVcAdapter()
        val concatAdapter = ConcatAdapter(requestToSignAdapter, signAdapter)
        mBinding.listDocItem.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }

        mViewModel.apply {
            signRequestCount.observe(viewLifecycleOwner, {
                requestToSignAdapter.submitList(listOf(
                    VCModel("1", "เอกสารที่รอฉันลงนาม", "เอกสารที่ผู้อื่นทำการร้องขอเข้ามาให้เราลงลายมือชื่อดิจิทัล" +
                            "เพื่อรับรองความถูกต้องของเอกสาร", it.toString())
                ))
            })
            signedCount.observe(viewLifecycleOwner, {
                signAdapter.submitList(listOf(
                    VCModel("2", "เอกสารที่ฉันลงนามแล้ว", "เอกสารที่ผู้อื่นทำการร้องขอเข้ามา โดยผ่านการลงลายมือชื่อดิจิทัล" +
                            "จากเราแล้ว", it.toString())
                ))
            })
        }

    }

    private fun resolveOrThrow(context: Context, @AttrRes attributeResId: Int): Int {
        val typedValue = TypedValue()
        if (context.theme.resolveAttribute(attributeResId, typedValue, true)) {
            return typedValue.data
        }
        throw IllegalArgumentException(context.resources.getResourceName(attributeResId))
    }

    override fun onResume() {
        super.onResume()
    }




}