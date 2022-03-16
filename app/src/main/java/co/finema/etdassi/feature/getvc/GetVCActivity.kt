package co.finema.etdassi.feature.getvc

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import co.finema.etdassi.R
import co.finema.etdassi.common.base.NormalActivity
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.MainActivityContentBinding
import co.finema.etdassi.feature.notification.NotificationListFragment
import co.finema.etdassi.feature.qr_reader.QRTextResultModel

class GetVCActivity: NormalActivity<MainActivityContentBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.main_activity_content
    }

    override fun onInject() {
        super.onInject()
        binding.mainContent.visibility = View.VISIBLE
        hideStatusBar()


        val fragment =  GetVCLoadingFragment()
        val args = intent.extras
        //mock
//        args.putParcelable("data",
//            QRTextResultModel(
//                id = "e48a94f5-5ec2-49dc-aadd-1afcfe3368ce", token = "b2b8a8c81dbaa75c5eb845f8c3fa7bd9f32de1257a1129ef4860d45865bed33c", "GET_VC"))
        fragment.arguments = args
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