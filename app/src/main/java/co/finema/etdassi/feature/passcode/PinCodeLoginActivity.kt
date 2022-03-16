package co.finema.etdassi.feature.passcode

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseActivity
import co.finema.etdassi.common.utils.FragmentOrigin
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.MainActivityContentBinding
import co.finema.etdassi.feature.mainpager.MainPagerFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PinCodeLoginActivity:BaseActivity<PinCodeViewModel, MainActivityContentBinding>(
    PinCodeViewModel::class.java) {

    override val viewModel: PinCodeViewModel by viewModel()

    override fun getLayoutRes() = R.layout.main_activity_content

    override fun onInject() {
        super.onInject()
        hideStatusBar()
        binding.mainContent.visibility = View.VISIBLE
        val fragment =  PinCodeFragment() //ProveIdentFragment()
        val args = Bundle()
        args.putSerializable(MainPagerFragment.FRAGMENT_ORIGIN, FragmentOrigin.CHECK_PIN_CODE)
        fragment.arguments = args
        supportFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment)
            .commit()
    }

}