package co.finema.etdassi.feature.verifyvc

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import co.finema.etdassi.R
import co.finema.etdassi.common.base.NormalActivity
import co.finema.etdassi.common.utils.FragmentOrigin
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.MainActivityContentBinding
import co.finema.etdassi.feature.pageloading.LoadingFragment
import co.finema.etdassi.feature.scan_qr.step_one.RequestListFragment

class VerifyVCActivity: NormalActivity<MainActivityContentBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.main_activity_content
    }

    override fun onInject() {
        super.onInject()
        hideStatusBar()
        binding.mainContent.visibility = View.VISIBLE
        val fragment =  LoadingFragment()
        intent.putExtra(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.VERIFY_VC)
        fragment.arguments = intent.extras
        supportFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment)
//            .addToBackStack(null)
            .commit()
    }
}