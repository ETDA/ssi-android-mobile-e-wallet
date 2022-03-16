package co.finema.etdassi.feature.bioauth

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseActivity
import co.finema.etdassi.common.utils.BackStack
import co.finema.etdassi.common.utils.FragmentOrigin
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.MainActivityContentBinding
import co.finema.etdassi.feature.mainpager.MainPagerFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class BioAuthActivity: BaseActivity<BioAuthViewModel, MainActivityContentBinding>(BioAuthViewModel::class.java) {

    override val viewModel: BioAuthViewModel by viewModel()

    override fun getLayoutRes() = R.layout.main_activity_content

    override fun onInject() {
        super.onInject()
        hideStatusBar()
        binding.mainContent.visibility = View.VISIBLE
        val fragment =  BioAuthFragment() //ProveIdentFragment()
//        args.putSerializable(MainPagerFragment.FRAGMENT_ORIGIN, FragmentOrigin.USER_PROFILE_MAIN_NEW_PINCODE)
        fragment.arguments = intent.extras
        supportFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment)
//            .addToBackStack(BackStack.SET_NEW_PINCODE.name)
            .commit()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }



}