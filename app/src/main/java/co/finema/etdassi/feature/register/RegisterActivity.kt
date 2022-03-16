package co.finema.etdassi.feature.register

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseActivity
import co.finema.etdassi.common.enum.RegisterState
import co.finema.etdassi.common.utils.*
import co.finema.etdassi.databinding.MainActivityContentBinding
import co.finema.etdassi.feature.mainpager.MainPagerFragment
import co.finema.etdassi.feature.pageloading.LoadingFragment
import co.finema.etdassi.feature.register.reset_did.ResetDidFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity: BaseActivity<RegisterViewModel, MainActivityContentBinding>(RegisterViewModel::class.java) {

    override val viewModel: RegisterViewModel by viewModel()

    override fun getLayoutRes() = R.layout.main_activity_content

    override fun onInject() {
        super.onInject()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        binding.mainContent.visibility = View.VISIBLE

        when(viewModel.registerState()) {

            RegisterState.REGISTER_MOBILE_SUCCESS.name -> {
                navigateToVerifyEmail()
            }


            RegisterState.REGISTER_EWALLET_SUCCESS.name -> {
                val fragment = MainPagerFragment()
                val args = Bundle()
                args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_EWALLET_SUCCESS)
                fragment.arguments = args
                supportFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(BackStack.REGISTER_APP.name).commit()
            }

            RegisterState.VERIFY_EMAIL_SUCCESS.name -> {
                if (viewModel.hasDidAddress) {
                    val fragment = MainPagerFragment()
                    val args = Bundle()
                    args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_RECOVERY)
                    fragment.arguments = args
                    supportFragmentManager.animateTransition()
                        .replace(R.id.main_content, fragment, BackStack.REGISTER_APP.name)
                        .addToBackStack(BackStack.REGISTER_APP.name)
                        .commit()
                } else {
                    val fragment = MainPagerFragment()
                    val args = Bundle()
                    args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_DETAIL_SUCCESS)
                    fragment.arguments = args
                    supportFragmentManager.animateTransition()
                        .replace(R.id.main_content, fragment, BackStack.REGISTER_APP.name)
                        .addToBackStack(BackStack.REGISTER_APP.name)
                        .commit()
                }
            }

            RegisterState.BACKUP_WALLET_SUCCESS.name -> {
                val fragment = MainPagerFragment()
                val args = Bundle()
                args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.START_SETPASSCODE)
                fragment.arguments = args
                supportFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(BackStack.SET_PASSCODE.name)
                    .commit()
            }

            RegisterState.REGISTER_EWALLET_SUCCESS.name -> {
                val fragment = MainPagerFragment()
                val args = Bundle()
                args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_EWALLET_SUCCESS)
                fragment.arguments = args
                supportFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(BackStack.REGISTER_APP.name).commit()
            }

            RegisterState.REGISTER_DID_SUCCESS.name, RegisterState.UPDATE_USER_SUCCESS.name -> {
                val fragment = MainPagerFragment()
                val args = Bundle()
                args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_DETAIL_SUCCESS)
                fragment.arguments = args
                supportFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment, BackStack.REGISTER_APP.name)
                    .addToBackStack(BackStack.REGISTER_APP.name)
                    .commit()
            }

            else -> {
                val fragment =  MainPagerFragment() //ProveIdentFragment()
                val args = Bundle()
                args.putSerializable(MainPagerFragment.FRAGMENT_ORIGIN, FragmentOrigin.START_REGISTER)
                fragment.arguments = args
                supportFragmentManager.animateTransition()
                    .add(R.id.main_content, fragment, BackStack.REGISTER_APP.name)
                    .addToBackStack(BackStack.REGISTER_APP.name)
                    .commit()
            }
        }




    }

    private fun navigateToVerifyEmail() {
        val fragment = ResetDidFragment()
        val args = Bundle()
        args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_VERIFY_EMAIL)
        fragment.arguments = args
        supportFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment)
            .addToBackStack(BackStack.REGISTER_APP.name)
            .commit()
    }

    override fun onBackPressed() {
        val currFrag = supportFragmentManager.findFragmentById(R.id.main_content)
        when(currFrag) {
            is LoadingFragment -> {
                if (TransitionUtil.loadingBackPressedEnable) {
                    super.onBackPressed()
                } else {
                    Toast.makeText(this, "กรุณารอสักครู่", Toast.LENGTH_SHORT).show()
                }

            }
            is MainPagerFragment -> {
                if (TransitionUtil.onBackPressedEnable) {
                    super.onBackPressed()
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