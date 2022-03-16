package co.finema.etdassi.feature.pageloading

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import co.finema.etdassi.R
import co.finema.etdassi.common.enum.RegisterState
import co.finema.etdassi.common.utils.*
import co.finema.etdassi.databinding.FragmentLoadingBinding
import co.finema.etdassi.feature.mainpager.MainPagerFragment
import co.finema.etdassi.feature.pageloading.usecase.GetBackupVCUseCase
import co.finema.etdassi.feature.pageloading.usecase.VerifyVPUseCase
import co.finema.etdassi.feature.qr_reader.QRTextResultModel
import co.finema.etdassi.feature.register.reset_did.ResetDidFragment
import co.finema.etdassi.feature.register.usecase.RegisterUserUseCase
import co.finema.etdassi.feature.verifyvc.VerifyResultFragment
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoadingFragment:BaseFragment<LoadingViewModel, FragmentLoadingBinding>(LoadingViewModel::class.java) {

    companion object {
        const val FRAGMENT_ORIGIN = "fragment_origin"
        const val PAYLOAD = "PAYLOAD"
    }

    override val mViewModel: LoadingViewModel by viewModel<LoadingViewModel>()
    override fun getLayoutRes() = R.layout.fragment_loading

    override fun init() {
        super.init()

        val fragmentOrigin = arguments?.getSerializable(FRAGMENT_ORIGIN) as FragmentOrigin

        //INIT TOOLBAR

        mBinding.toolbarLoading.apply {
            buttonBack.visibility = View.GONE
        }


        //ANIMATE LOADING
        val rotation = AnimationUtils.loadAnimation(requireContext(), R.anim.spin_view)
        mBinding.loadingImage.startAnimation(rotation)

        when(fragmentOrigin) {

            FragmentOrigin.REGISTER_DETAIL -> {
                mBinding.headerText = getString(R.string.register_title)
                mBinding.subDescription = getString(R.string.please_waiting_your_info_inprogress)
                arguments?.getParcelable<RegisterUserUseCase.Param>(PAYLOAD)?.let { param ->
                    mViewModel.registerUser(param, object :ApiListener {
                        override fun onSuccess() {
                            val clipboard: ClipboardManager = requireActivity().getSystemService(
                                Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("DID Address", mViewModel.getUserId())
                            clipboard.setPrimaryClip(clip)
                            requireContext().toastNormal("Copied")
                            navigateToVerifyEmail()
                        }
                        override fun onFail(errorMessage: String) {
                            setRegisterUserErrorView()
                        }

                    })
                }
            }

            FragmentOrigin.REGISTER_EWALLET -> {
                registerEWallet()
            }

            FragmentOrigin.REGISTER_RECOVERY -> {
//                mBinding.headerText = "กู้คืนข้อมูล\nETDA e-Wallet"
//                mBinding.subDescription = "กรุณารอสักครู่ ระบบกำลังดำเนินการ\n" + "กู้คืนข้อมูล ETDA e-Wallet"
                registerRecovery()
            }

            FragmentOrigin.VERIFY_VC -> {
                mBinding.headerText = "ตรวจสอบเอกสาร"
                mBinding.subDescription = "กรุณารอสักครู่ \n" + "ระบบกำลังดำเนินการตรวจสอบเอกสาร"
                arguments?.getParcelable<QRTextResultModel>("data")?.let {
                    mViewModel.qrVerify(it, object :GenericListener<VerifyVPUseCase.QRVerifyResult> {
                        override fun onSuccess(response: VerifyVPUseCase.QRVerifyResult) {
                            val fragment = VerifyResultFragment()
                            arguments?.putParcelable("verifyResult", response)
                            fragment.arguments = arguments
                            parentFragmentManager.animateTransition()
                                .replace(R.id.main_content, fragment)
                                .commit()
                        }

                        override fun onFail(errorMessage: String) {
                            requireContext().toastNormal(errorMessage)
                            requireActivity().finish()
                        }

                    })
                }



            }

            else -> {

            }
        }

    }

    private fun registerEWallet() {

        mBinding.apply {
            loadingImage.setImageResource(R.drawable.ic_loading_png)
            headerText = getString(R.string.register_ewallet)
            subDescription = getString(R.string.please_waiting_register_e_wallet_inprogress)
        }

        //ANIMATE LOADING
        val rotation = AnimationUtils.loadAnimation(requireContext(), R.anim.spin_view)
        mBinding.loadingImage.startAnimation(rotation)

        when(mViewModel.registerState()) {

            RegisterState.REGISTER_DID_SUCCESS.name -> {
                mViewModel.updateUser(object : ApiListener {
                    override fun onSuccess() {
                        navigateToRegisterEwalletSuccess()
                    }

                    override fun onFail(errorMessage: String) {
                        setRegisterEwalletErrorView()
                    }
                })
            }

            RegisterState.UPDATE_USER_SUCCESS.name -> {
                mViewModel.updateFirebaseToken(object : ApiListener {
                    override fun onSuccess() {
                        navigateToRegisterEwalletSuccess()
                    }

                    override fun onFail(errorMessage: String) {
                        setRegisterEwalletErrorView()
                    }
                })
            }

            else -> {
                mViewModel.createDID(object : ApiListener {
                    override fun onSuccess() {
                        navigateToRegisterEwalletSuccess()
                    }

                    override fun onFail(errorMessage: String) {
                        setRegisterEwalletErrorView()
                    }
                })
            }
        }

    }

    private fun registerRecovery(){
//        mViewModel
        mBinding.apply {
            loadingImage.setImageResource(R.drawable.ic_loading_png)
            headerText = getString(R.string.recovery_ewallet)
            subDescription = getString(R.string.please_waiting_recovery_e_wallet_inprogress)
        }
        val rotation = AnimationUtils.loadAnimation(requireContext(), R.anim.spin_view)
        mBinding.loadingImage.startAnimation(rotation)

        when(mViewModel.registerState()) {

            RegisterState.UPDATE_USER_SUCCESS.name -> {
                mViewModel.updateFirebaseToken(object : ApiListener {
                    override fun onSuccess() {
                        getBackup()
                    }

                    override fun onFail(errorMessage: String) {
                        setRecoveryUserErrorView()
                    }
                })
            }

            else -> {
                mViewModel.registerRecovery(object : ApiListener {
                    override fun onSuccess() {
                        getBackup()
                    }

                    override fun onFail(errorMessage: String) {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        setRecoveryUserErrorView()
                    }
                })
            }
        }
    }

    private fun getBackup() {
        mViewModel.getBackupWallet(object : GenericListener<GetBackupVCUseCase.ItemRestore> {
            override fun onSuccess(response: GetBackupVCUseCase.ItemRestore) {
                mBinding.apply {
                    loadingImage.clearAnimation()
                    loadingImage.visibility = View.GONE
                    subDescription =
                        "เอกสารรับรองของคุณ สามารถกู้คืนได้ทั้งหมด ${response.vc.success}/${response.vc.error + response.vc.success}\n" +
                                "เอกสารรับรองที่คุณลงนามรับรอง สามารถกู้คืนได้ทั้งหมด ${response.vcSigned.success}/${response.vcSigned.error + response.vcSigned.success}"
                    buttonLoading.buttonText = "ถัดไป"
                    buttonLoading.setOnButtonClickListener {
                        navigateToRecoveryEwalletSuccess()
                    }
                }


            }

            override fun onFail(errorMessage: String) {
                requireContext().toastNormal(errorMessage)
                setRecoveryUserErrorView()
            }

        })
    }

    private fun navigateToRecoveryEwalletSuccess(){
        val fragment = MainPagerFragment()
        val args = Bundle()
        args.putSerializable(FRAGMENT_ORIGIN, FragmentOrigin.RECOVERY_SUCCESS)
        fragment.arguments = args
        parentFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment)
            .addToBackStack(BackStack.REGISTER_APP.name).commit()
    }

    private fun navigateToRegisterEwalletSuccess() {
        val fragment = MainPagerFragment()
        val args = Bundle()
        args.putSerializable(FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_EWALLET_SUCCESS)
        fragment.arguments = args
        parentFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment)
            .addToBackStack(BackStack.REGISTER_APP.name).commit()
    }

    private fun setRegisterEwalletErrorView() {
        mBinding.apply {
            loadingImage.clearAnimation()
            loadingImage.setImageResource(R.drawable.ic_phone_error)
            val density = Resources.getSystem().displayMetrics.density
            loadingImage.layoutParams.width = 105 * density.toInt()
            headerText = ""
            mainDescription = "พบข้อผิดพลาดบนอุปกรณ์"
            subDescription = "กรุณาตรวจสอบอุปกรณ์\n" + "ที่ใช้ลงทะเบียนอีกครั้ง"
            buttonLoading.buttonText = getString(R.string.btn_text_try_again_01)
            buttonLoading.setOnButtonClickListener {
                registerEWallet()
            }
        }
    }

    private fun setRegisterUserErrorView() {
        mBinding.apply {
            loadingImage.clearAnimation()
            loadingImage.setImageResource(R.drawable.ic_info)
            val density = Resources.getSystem().displayMetrics.density
            loadingImage.layoutParams.height = 105 * density.toInt()
            headerText = ""
            mainDescription = getString(R.string.text_loading_error_01)
            subDescription = getString(R.string.text_loading_error_02)
            buttonLoading.buttonText = getString(R.string.btn_text_try_again_01)
            buttonLoading.setOnButtonClickListener {
                TransitionUtil.loadingBackPressedEnable = true
                requireActivity().onBackPressed()
                TransitionUtil.loadingBackPressedEnable = false
            }
        }
    }

    private fun setRecoveryUserErrorView() {
        mBinding.apply {
            loadingImage.clearAnimation()
            loadingImage.setImageResource(R.drawable.ic_info)
            val density = Resources.getSystem().displayMetrics.density
            loadingImage.layoutParams.height = 105 * density.toInt()
            headerText = ""
            mainDescription = "พบข้อผิดพลาดในการกู้คืนข้อมูล"//getString(R.string.text_loading_error_01)
            subDescription = getString(R.string.text_loading_error_02)
            buttonLoading.buttonText = getString(R.string.btn_text_try_again_01)
            buttonLoading.setOnButtonClickListener {
                TransitionUtil.loadingBackPressedEnable = true
                requireActivity().onBackPressed()
                TransitionUtil.loadingBackPressedEnable = false
            }
        }
    }

    private fun navigateToVerifyEmail() {

        val fragment = ResetDidFragment()
        val args = Bundle()
        args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_VERIFY_EMAIL)
        fragment.arguments = args
        parentFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment)
            .addToBackStack(BackStack.REGISTER_APP.name)
            .commit()
    }

    private fun navigateToRegisterDID() {
        val fragment = MainPagerFragment()
        val args = Bundle()
        args.putSerializable(FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_DETAIL_SUCCESS)
        fragment.arguments = args
        parentFragmentManager.animateOnlyInTransition()
            .add(R.id.main_content, fragment, BackStack.REGISTER_APP.name)
            .hide(this)
            .addToBackStack(BackStack.REGISTER_APP.name)
            .commit()
    }

//    private fun clearBackStack() {
//        TransitionUtil.sDisableFragmentAnimations = true
//        requireActivity().supportFragmentManager.popBackStack(
//            null,
//            FragmentManager.POP_BACK_STACK_INCLUSIVE
//        )
//        TransitionUtil.sDisableFragmentAnimations = false
//    }
}