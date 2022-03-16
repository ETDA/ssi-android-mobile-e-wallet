package co.finema.etdassi.feature.userprofile.settingpasscode

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseActivity
import co.finema.etdassi.common.utils.*
import co.finema.etdassi.databinding.MainActivityContentBinding
import co.finema.etdassi.feature.mainpager.MainPagerFragment
import co.finema.etdassi.feature.pageloading.LoadingFragment
import co.finema.etdassi.feature.passcode.PinCodeLoginActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingPasscodeActivity:BaseActivity<SettingPasscodeViewModel, MainActivityContentBinding>(SettingPasscodeViewModel::class.java) {


    override val viewModel: SettingPasscodeViewModel by viewModel()

    override fun getLayoutRes() = R.layout.main_activity_content

    companion object {
        var requestLogin: Boolean = false
    }

    override fun onInject() {
        super.onInject()
        hideStatusBar()
        binding.mainContent.visibility = View.VISIBLE
        val fragment =  MainPagerFragment() //ProveIdentFragment()
        val args = Bundle()
        args.putSerializable(MainPagerFragment.FRAGMENT_ORIGIN, FragmentOrigin.USER_PROFILE_MAIN_NEW_PINCODE)
        fragment.arguments = args
        supportFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment, BackStack.REGISTER_APP.name)
            .addToBackStack(BackStack.SET_NEW_PINCODE.name)
            .commit()
    }

    override fun onBackPressed() {
        when(supportFragmentManager.findFragmentById(R.id.main_content)) {
            is MainPagerFragment -> {
                if (requestLogin) {
                    val intent = Intent(this, PinCodeLoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    finish()
                }
            }
            else -> {
                if (supportFragmentManager.backStackEntryCount <= 1) {
                    finish()
                } else {
                    super.onBackPressed()
                }
            }
        }
    }

}