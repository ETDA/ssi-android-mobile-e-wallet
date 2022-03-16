package co.finema.sso.common.base

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.BackStack
import co.finema.etdassi.common.utils.FragmentOrigin
import co.finema.etdassi.common.utils.TransitionUtil
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.DialogLoadingBinding
import co.finema.etdassi.databinding.DialogNormalBinding
import co.finema.etdassi.feature.mainpager.MainPagerFragment
import co.finema.etdassi.feature.pageloading.LoadingFragment

abstract class BaseFragment<VM : ViewModel, DB : ViewDataBinding>(private val mViewModelClass: Class<VM>) : Fragment() {
    protected abstract val mViewModel: VM
    open lateinit var mBinding: DB
    fun init(inflater: LayoutInflater, container: ViewGroup?) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
    }

    open fun init() {}
    @LayoutRes
    abstract fun getLayoutRes(): Int

//    private fun getViewM(): VM = ViewModelProvider(this).get(mViewModelClass)
    open fun onInject() {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel = getViewM()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        init(inflater, container)
        mBinding.setLifecycleOwner { lifecycle }
        init()
        super.onCreateView(inflater, container, savedInstanceState)
        return mBinding.root
    }

    open fun refresh() {}

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (TransitionUtil.sDisableFragmentAnimations) {
            val a: Animation = object : Animation() {}
            a.duration = 0
            return a
        }
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    fun clearBackStack(name: String? = null) {
        TransitionUtil.sDisableFragmentAnimations = true
        requireActivity().supportFragmentManager.popBackStack(
            name,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        TransitionUtil.sDisableFragmentAnimations = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }

    private var dialog: Dialog? = null

    open fun createLoadingDialog() {
        dialog = Dialog(requireContext())
        dialog?.let { mDialog ->
            val dialogBinding = DataBindingUtil.inflate<DialogLoadingBinding>(LayoutInflater.from(requireContext()), R.layout.dialog_loading, null, false)
            mDialog.setContentView(dialogBinding.root)
            mDialog.setCanceledOnTouchOutside(false)
            mDialog.window?.apply {
                setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT)
                setGravity(Gravity.CENTER)
                setBackgroundDrawableResource(android.R.color.transparent)
            }//ANIMATE LOADING
            val rotation = AnimationUtils.loadAnimation(requireContext(), R.anim.spin_view)
            dialogBinding.loadingImage.startAnimation(rotation)
            mDialog.show()
        }
    }

    open fun dismissLoadingDialog() {
        dialog?.dismiss()
    }

    open fun setStatusFlagToBlack() {
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}