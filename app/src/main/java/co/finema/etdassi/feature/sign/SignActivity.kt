package co.finema.etdassi.feature.sign

import android.view.View
import android.view.WindowManager
import co.finema.etdassi.R
import co.finema.etdassi.common.base.NormalActivity
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.MainActivityContentBinding

class SignActivity: NormalActivity<MainActivityContentBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.main_activity_content
    }

    override fun onInject() {
        super.onInject()
        hideStatusBar()
        binding.mainContent.visibility = View.VISIBLE
        val fragment =  SignFragment()
        fragment.arguments = intent.extras
        supportFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount <= 1) {
            finish()
        } else {
            if (supportFragmentManager.findFragmentById(R.id.main_content) is SignResultFragment) {
                finish()
            } else {
                super.onBackPressed()
            }
        }

    }
}