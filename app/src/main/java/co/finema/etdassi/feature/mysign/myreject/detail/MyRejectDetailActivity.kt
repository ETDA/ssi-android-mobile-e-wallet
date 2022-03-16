package co.finema.etdassi.feature.mysign.myreject.detail

import android.view.View
import co.finema.etdassi.R
import co.finema.etdassi.common.base.NormalActivity
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.MainActivityContentBinding

class MyRejectDetailActivity: NormalActivity<MainActivityContentBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.main_activity_content
    }

    override fun onInject() {
        super.onInject()
        binding.mainContent.visibility = View.VISIBLE
        hideStatusBar()
        val fragment =  MyRejectDetailFragment()
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
            super.onBackPressed()
        }

    }
}