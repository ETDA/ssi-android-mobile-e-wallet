package co.finema.etdassi.feature.enablelockscreen

import android.os.Bundle
import android.view.View
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseActivity
import co.finema.etdassi.common.utils.BackStack
import co.finema.etdassi.common.utils.FragmentOrigin
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.MainActivityContentBinding
import co.finema.etdassi.feature.mainpager.MainPagerFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnableLockScreenActivity: BaseActivity<EnableLockScreenViewModel, MainActivityContentBinding>(EnableLockScreenViewModel::class.java) {
    override fun getLayoutRes() = R.layout.main_activity_content

    override val viewModel: EnableLockScreenViewModel by viewModel()

    override fun onInject() {
        super.onInject()
        binding.mainContent.visibility = View.VISIBLE
        val fragment = MainPagerFragment()
        val args = Bundle()
        args.putSerializable(MainPagerFragment.FRAGMENT_ORIGIN, FragmentOrigin.CHECK_SYSTEM_PIN_CODE)
        fragment.arguments = args
        supportFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {

    }
}