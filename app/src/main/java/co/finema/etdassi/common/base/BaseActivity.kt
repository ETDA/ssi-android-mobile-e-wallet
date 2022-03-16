package co.finema.etdassi.common.base

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import co.finema.etdassi.feature.enablelockscreen.EnableLockScreenActivity


//@SuppressLint("Registered")
//abstract class BaseActivity<B : ViewDataBinding>  : AppCompatActivity(){
//
//    private lateinit var mViewDataBinding: B
//
//    val binding: B get() = mViewDataBinding
//
//    @LayoutRes
//    protected abstract fun getLayoutId(): Int
//
//    protected abstract fun onViewReady(savedInstance: Bundle?)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
//        super.onCreate(savedInstanceState)
//        if (getLayoutId() != 0) {
//            mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
//            onViewReady(savedInstanceState)
//        }
//    }
//}

abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding>(private val mViewModelClass: Class<VM>) : AppCompatActivity() {

    @LayoutRes
    abstract fun getLayoutRes(): Int

    val binding by lazy {
        DataBindingUtil.setContentView(this, getLayoutRes()) as DB
    }

    protected abstract val viewModel: VM

    /**
     * If you want to inject Dependency Injection
     * on your activity, you can override this.
     */
    open fun onInject() {}

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        onInject()
    }

//    override fun onBackPressed() {
//        if (supportFragmentManager.backStackEntryCount <= 1) {
//            finish()
//        } else {
//            super.onBackPressed()
//        }
//    }

    open fun hideStatusBar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
    }
}