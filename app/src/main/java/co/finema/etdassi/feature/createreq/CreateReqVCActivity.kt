package co.finema.etdassi.feature.createreq

import android.view.View
import android.view.WindowManager
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseActivity
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.MainActivityContentBinding
import co.finema.etdassi.feature.createreq.listofvc.ListOfVCFragment
import co.finema.etdassi.feature.createreq.requestqr.ReqQrCodeSummaryFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateReqVCActivity: BaseActivity<CreateRqVCViewModel, MainActivityContentBinding>(CreateRqVCViewModel::class.java) {
    override fun getLayoutRes() = R.layout.main_activity_content

    override val viewModel: CreateRqVCViewModel by viewModel()

    override fun onInject() {
        super.onInject()
        hideStatusBar()
        binding.mainContent.visibility = View.VISIBLE
        val fragment = ListOfVCFragment()
//        val args = Bundle()
//        args.putSerializable(MainPagerFragment.FRAGMENT_ORIGIN, FragmentOrigin.CHECK_SYSTEM_PIN_CODE)
//        fragment.arguments = args
        supportFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment)
            .addToBackStack(null)
            .commit()
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount <= 1) {
            finish()
        } else {
            if (supportFragmentManager.findFragmentById(R.id.main_content) is ReqQrCodeSummaryFragment) {
                finish()
            } else {
                super.onBackPressed()
            }
        }
    }
}