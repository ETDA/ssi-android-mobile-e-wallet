package co.finema.etdassi.feature.myvc

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import co.finema.etdassi.R
import co.finema.etdassi.databinding.FragmentMyVcBinding
import co.finema.etdassi.feature.myvc.myvptab.MyVPListFragment
import co.finema.etdassi.feature.myvc.myvctab.MyVCListFragment
import co.finema.sso.common.base.BaseFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyVCFragment: BaseFragment<MyVCViewModel, FragmentMyVcBinding>(MyVCViewModel::class.java) {
    override val mViewModel: MyVCViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_my_vc

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.mySignToolbar.apply {
            toolbarTitle = getString(R.string.title_my_doc)
        }

        val mAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when(position) {
                    0 -> MyVCListFragment()
                    else -> MyVPListFragment()
                }
//                return MyVCListFragment()
            }
        }

        mBinding.viewPager.apply {
            adapter = mAdapter
        }

        mBinding.tabLayout.tabMode = TabLayout.MODE_FIXED

        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager) {tab, position ->
            tab.text = when(position) {
                0 -> "เอกสารรับรองที่ฉันได้รับ"
                else -> "เอกสารสำแดงที่ฉันลงนาม"
            }
        }.attach()
    }




}