package co.finema.etdassi.feature.mysign

import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import co.finema.etdassi.R
import co.finema.etdassi.common.enum.DoActionEnum
import co.finema.etdassi.databinding.FragmentMyVcBinding
import co.finema.etdassi.feature.mysign.signrequest.SignRequestFragment
import co.finema.etdassi.feature.mysign.vcsign.VCSignedFragment
import co.finema.etdassi.feature.mysign.myreject.MyRejectFragment
import co.finema.sso.common.base.BaseFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MySignFragment: BaseFragment<MySignViewModel, FragmentMyVcBinding>(MySignViewModel::class.java) {
    override val mViewModel: MySignViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_my_vc

    override fun init() {
        super.init()
        mBinding.mySignToolbar.toolbarWindowFlag.setBackgroundResource(R.drawable.second_header_bg_fade)

        mBinding.mySignToolbar.toolbarTitle = getString(R.string.toolbar_vc_signed)

        val mAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 3
            }

            override fun createFragment(position: Int): Fragment {
                return when(position) {
                    0 -> SignRequestFragment()
                    1 -> MyRejectFragment()
                    else -> VCSignedFragment()
                }
            }
        }

        mBinding.viewPager.apply {
            adapter = mAdapter
        }

        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) {tab, position ->
            tab.text = when(position) {
                0 -> getString(R.string.tab_layout_vc_request_to_sign)
                1 -> "เอกสารที่ฉันปฏิเสธลงนาม"
                else -> getString(R.string.tab_layout_vc_signed)
            }
        }.attach()

        /**
         *  setup badge all in tab layout
         */

        mViewModel.signRequestCountUnRead.observe(viewLifecycleOwner, {
            mBinding.tabLayout.getTabAt(0)?.orCreateBadge?.apply {
                isVisible = it != 0
                number = it
                maxCharacterCount = 3
            }
        })

        mViewModel.vcSignedUnRead.observe(viewLifecycleOwner , {
            mBinding.tabLayout.getTabAt(2)?.orCreateBadge?.apply {
                isVisible = it != 0
                number = it
                maxCharacterCount = 3
            }
        })


    }

    private fun getCustomViewFromTab(tab: TabLayout.Tab?) = tab?.customView as? AppCompatTextView

    override fun onResume() {
        super.onResume()
        mViewModel.checkActionNext()?.let { nextAction ->
            when(nextAction) {
                DoActionEnum.REJECT_DONE_OPEN_REJECT_PAGE -> {
                    mBinding.viewPager.setCurrentItem(1, true)
                    mViewModel.clearActionNext()
                }

                else -> {

                }
            }
        }
        mViewModel.getCountUnRead()
    }

}