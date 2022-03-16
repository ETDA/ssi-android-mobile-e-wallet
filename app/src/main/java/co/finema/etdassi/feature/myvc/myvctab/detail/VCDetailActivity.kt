package co.finema.etdassi.feature.myvc.myvctab.detail

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseActivity
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.MainActivityContentBinding
import co.finema.etdassi.feature.createreq.requestqr.ReqQrCodeSummaryFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class VCDetailActivity: BaseActivity<VCDetailViewModel, MainActivityContentBinding>(VCDetailViewModel::class.java) {
    override fun getLayoutRes() = R.layout.main_activity_content

    override val viewModel: VCDetailViewModel by viewModel()

    override fun onInject() {
        super.onInject()
        hideStatusBar()
        binding.mainContent.visibility = View.VISIBLE
        supportFragmentManager.animateTransition()
            .replace(R.id.main_content, VCDetailFragment.setArguments(intent.extras))
            .addToBackStack(null)
            .commit()
//        val fragment =  MainPagerFragment() //ProveIdentFragment()
//        val args = Bundle()
//        args.putSerializable(MainPagerFragment.FRAGMENT_ORIGIN, FragmentOrigin.USER_PROFILE_MAIN_NEW_PINCODE)
//        fragment.arguments = args
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount <= 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}