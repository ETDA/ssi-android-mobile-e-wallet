package co.finema.etdassi.feature.notification

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import co.finema.etdassi.R
import co.finema.etdassi.common.base.NormalActivity
import co.finema.etdassi.common.utils.FragmentOrigin
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.MainActivityContentBinding
import co.finema.etdassi.feature.pageloading.LoadingFragment

class NotificationListActivity: NormalActivity<MainActivityContentBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.main_activity_content
    }

    override fun onInject() {
        super.onInject()
        hideStatusBar()
        binding.mainContent.visibility = View.VISIBLE
        val fragment =  NotificationListFragment()
        val args = Bundle()
//        args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.VERIFY_VC)
        fragment.arguments = args
        supportFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount <= 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}