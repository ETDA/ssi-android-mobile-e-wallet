package co.finema.etdassi.feature.splash

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseActivity
import co.finema.etdassi.databinding.SplashScreenBinding
import co.finema.etdassi.feature.enablelockscreen.EnableLockScreenActivity
import co.finema.etdassi.feature.passcode.PinCodeLoginActivity
import co.finema.etdassi.feature.register.RegisterActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity:BaseActivity<SplashViewModel, SplashScreenBinding>(SplashViewModel::class.java) {

    override val viewModel: SplashViewModel by viewModel()

    override fun getLayoutRes() = R.layout.splash_screen

    override fun onInject() {
        super.onInject()
        binding.appLogo.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        val km = applicationContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (km.isKeyguardSecure) {
            navigateToRegister()
        }
        else {
            val intent = Intent(this, EnableLockScreenActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToRegister() {
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.userRegister.observe(this, { hasRegister ->
                val intent = if (hasRegister) {
                    Intent(this, PinCodeLoginActivity::class.java)
                } else {
                    Intent(this, RegisterActivity::class.java)
                }
                startActivity(intent)
                this.finish()

            })
        }, 1500)
    }



}