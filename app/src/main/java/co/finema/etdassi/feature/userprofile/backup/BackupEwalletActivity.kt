package co.finema.etdassi.feature.userprofile.backup

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import co.finema.etdassi.R
import co.finema.etdassi.common.base.NormalActivity
import co.finema.etdassi.common.utils.BackStack
import co.finema.etdassi.common.utils.FragmentOrigin
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.MainActivityContentBinding
import co.finema.etdassi.feature.mainpager.MainPagerFragment

class BackupEwalletActivity: NormalActivity<MainActivityContentBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.main_activity_content
    }

    override fun onInject() {
        super.onInject()
        hideStatusBar()
        binding.mainContent.visibility = View.VISIBLE
        val fragment =  MainPagerFragment()
        val args = Bundle()
        args.putSerializable(MainPagerFragment.FRAGMENT_ORIGIN, FragmentOrigin.USER_PROFILE_BACKUP)
        fragment.arguments = args
        supportFragmentManager.animateTransition()
                .replace(R.id.main_content, fragment, BackStack.REGISTER_APP.name)
                .addToBackStack(BackStack.SET_NEW_PINCODE.name)
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