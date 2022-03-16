package co.finema.etdassi.feature.mainpager

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.KeyguardManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import co.finema.etdassi.feature.MainActivity
import co.finema.etdassi.R
import co.finema.etdassi.common.Constants
import co.finema.etdassi.common.enum.BioAuthType
import co.finema.etdassi.common.enum.IdentType
import co.finema.etdassi.common.utils.*
import co.finema.etdassi.databinding.DialogCancelBaseBinding
import co.finema.etdassi.databinding.DialogNormalBinding
import co.finema.etdassi.databinding.PageImagePrimaryBinding
import co.finema.etdassi.feature.bioauth.BioAuthActivity
import co.finema.etdassi.feature.pageloading.LoadingFragment
import co.finema.etdassi.feature.passcode.PinCodeFragment
import co.finema.etdassi.feature.passcode.PinCodeLoginActivity
import co.finema.etdassi.feature.register.ewallet.ProveIdentFragment
import co.finema.etdassi.feature.register.reset_did.ResetDidFragment
import co.finema.etdassi.feature.register.term.TermFragment
import co.finema.etdassi.feature.userprofile.settingpasscode.SettingPasscodeActivity
import co.finema.sso.common.base.BaseFragment
import kotlinx.android.synthetic.main.main_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
class MainPagerFragment: BaseFragment<MainPagerViewModel, PageImagePrimaryBinding>(MainPagerViewModel::class.java) {

    companion object {
        const val FRAGMENT_ORIGIN = "fragment_origin"
    }
    override val mViewModel: MainPagerViewModel by viewModel()

    override fun getLayoutRes() = R.layout.page_image_primary

    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo
    private val RequestCameraPermission = 1001

    private lateinit var backupBiometricResult: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backupBiometricResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                requireContext().toastNormal(mViewModel.setBackupStatus())
                requireActivity().finish()

            } else {
                requireContext().toastNormal("ลองใหม่อีกครั้ง")
            }
        }
    }



    override fun init() {
        super.init()
        val fragmentOrigin = arguments?.getSerializable(FRAGMENT_ORIGIN) as FragmentOrigin
//        println("MainPagerFragment => ${fragmentOrigin.name}")
//        println("supportFragmentManager.backStackEntryCount => ${requireActivity().supportFragmentManager.backStackEntryCount}")
//        for (entry in 0 until requireActivity().supportFragmentManager.backStackEntryCount) {
//            Log.i(ContentValues.TAG, "Found fragment: " + requireActivity().supportFragmentManager.getBackStackEntryAt(entry).name)
//        }
        when(fragmentOrigin) {

            FragmentOrigin.START_REGISTER -> {
                initStartRegisterView()
            }

            FragmentOrigin.REGISTER_DETAIL_SUCCESS -> {
                initRegisterDetailSuccessView()
            }

            FragmentOrigin.REGISTER_EWALLET_SUCCESS -> {
                initRegisterEwalletView()
            }

            FragmentOrigin.RECOVERY_SUCCESS -> {
                initRecoverySuccess()
            }

            FragmentOrigin.START_SETPASSCODE -> {
                initSetPasscodeStart()
            }

            FragmentOrigin.ENABLE_BIOMETRIC_LOGIN -> {
                initEnableBiometricLogin()
            }

            FragmentOrigin.REGISTER_FINISH -> {
                initRegisterFinish()
            }

            FragmentOrigin.USER_PROFILE_MAIN_NEW_PINCODE -> {
                initUserprofileNewPinCode()
            }

            FragmentOrigin.USER_PROFILE_MAIN_CONFIRM_PINCODE -> {
                initUserprofileConfirmPinCode()
            }

            FragmentOrigin.CHECK_SYSTEM_PIN_CODE -> {
                initCheckSystemPinCodeView()
            }

            FragmentOrigin.BACKUP_STEP -> {
                initBackupView()
            }

            FragmentOrigin.USER_PROFILE_BACKUP -> {
                initBackupFromUserprofileView()
            }

            FragmentOrigin.REGISTER_RECOVERY -> {
                initRegisterRecoveryView()
            }

            else -> notFoundType()
        }
    }



    private fun initRecoverySuccess() {
        mBinding.mainImage.setImageResource(R.drawable.register_ewallet_success)

        val string = SpannableString(getString(R.string.your_did_number))
        string.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.temporary)), 12, 15, 0)

        mBinding.descriptionWithImage.apply {
            text = string
            visibility = View.VISIBLE
        }

        mBinding.setOnDidDialogClickListener {
            val bottomSheetDialog = BottomSheetFragment.newInstance(DialogType.DID_INFO)
            bottomSheetDialog.show(parentFragmentManager, "Bottom Sheet Dialog Fragment")
        }

        mBinding.descriptionWithImage.addImage("[info-image]", R.drawable.ic_info,
            resources.getDimensionPixelOffset(R.dimen.spacing_l),
            resources.getDimensionPixelOffset(R.dimen.spacing_l))

        mBinding.textWithBorder = mViewModel.getDIDAddress()

        mBinding.pageImagePrimaryToolbarView.visibility = View.GONE
        mBinding.headerText = "กู้คืนข้อมูล\n" + "ETDA e-Wallet สำเร็จ"
        mBinding.descriptionSecond = "ข้อมูลและเอกสารของคุณได้รับการกู้คืนกลับมา"
        mBinding.pageImagePrimaryButton.apply {
            buttonText = getString(R.string.next_step)
            setOnButtonClickListener {
                val fragment = MainPagerFragment()
                val args = Bundle()
                args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.START_SETPASSCODE)
                fragment.arguments = args
//                clearBackStack(BackStack.REGISTER_EWALLET.name)
                parentFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(BackStack.REGISTER_APP.name)
                    .commit()
            }
        }
    }

    private fun initRegisterRecoveryView() {
        mBinding.mainImage.setImageResource(R.drawable.capture_welcome_back_01)
        mBinding.descriptionMain = "บัญชีของคุณเคยมีการใช้งานก่อนหน้านี้\n" + "และข้อมูลของคุณได้ถูกสำรองไว้"
        mBinding.pageImagePrimaryToolbar.toolbarTitle = "ยินดีต้อนรับกลับ"
        mBinding.descriptionSecond = "ระบบจะทำการกู้คืนข้อมูลของคุณ"

        mBinding.pageImagePrimaryButton.apply {
            buttonText = "กู้คืน"
            setOnButtonClickListener {
                val fragment = ProveIdentFragment()
                val args = Bundle()
                args.putSerializable(FRAGMENT_ORIGIN, IdentType.RESTORE_DID)
                fragment.arguments = args
                parentFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(BackStack.REGISTER_APP.name).commit()
            }
        }

        mBinding.pageImageSecondaryButton.apply {
            buttonText = "ข้าม"
            setOnButtonClickListener {
                val dialog = Dialog(it.context)
                val dialogBinding = DataBindingUtil.inflate<DialogNormalBinding>(LayoutInflater.from(it.context), R.layout.dialog_normal, null, false)
                dialog.setContentView(dialogBinding.root)
                dialog.window?.apply {
                    setLayout(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT)
                    setGravity(Gravity.CENTER)
                    setBackgroundDrawableResource(android.R.color.transparent)
                }
                dialogBinding.apply {
                    dialogNormalTitle.text = "ไม่กู้คืนข้อมูล ?"
                    dialogNormalDescription.text = "หากคุณยืนยันที่จะไม่กู้คืน ข้อมูลที่สำรองไว้จะถูก " +
                            "ลบทิ้งและระบบจะทำการสร้าง DID ใหม่ให้คุณ " +
                            "คุณต้องการกู้คืนข้อมูลหรือไม่\u200B ?"
                    dialogNormalCancel.text = "ไม่กู้คืนข้อมูล"
                    dialogNormalAccept.text = "กู้คืนข้อมูล"
                }
                dialogBinding.setOnCancelClickListener {
                    dialog.dismiss()
                    val fragment = ResetDidFragment()
                    val args = Bundle()
                    args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.RESET_DID)
                    fragment.arguments = args
                    parentFragmentManager.animateTransition()
                        .replace(R.id.main_content, fragment)
                        .addToBackStack(BackStack.REGISTER_APP.name)
                        .commit()
                }
                dialogBinding.setOnBackupClickListener {
                    dialog.dismiss()
                    val fragment = ProveIdentFragment()
                    val args = Bundle()
                    args.putSerializable(FRAGMENT_ORIGIN, IdentType.RESTORE_DID)
                    fragment.arguments = args
                    parentFragmentManager.animateTransition()
                        .replace(R.id.main_content, fragment)
                        .addToBackStack(BackStack.REGISTER_APP.name).commit()
                }
                dialog.show()
            }
        }

    }

    private fun initBackupFromUserprofileView() {

        mBinding.apply {
            pageImagePrimaryToolbar.toolbarTitle = "การสำรองข้อมูล"
            pageImagePrimaryToolbar.toolbarTitleView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            pageImagePrimaryToolbar.backImage.setImageResource(R.drawable.ic_baseline_arrow_white_ios_24)
            pageImagePrimaryToolbarView.setBackgroundResource(R.drawable.home_header_bg_fade)
            pageImagePrimaryToolbar.setOnBackClickListener {
                requireActivity().onBackPressed()
            }
            pageImagePrimaryButton.setOnButtonClickListener {
                val intent = Intent(requireContext(), BioAuthActivity::class.java)
                intent.putExtra("type", BioAuthType.PROFILE_BACKUP_WALLET)
                backupBiometricResult.launch(intent)
            }

            if (mViewModel.getBackupStatus()) {
                mainImage.setImageResource(R.drawable.capture_not_backup_vc)
                val string = SpannableString(getString(R.string.text_description_cancel_backup))
                string.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.danger)), 10, 16, 0)

                descriptionWithImage.apply {
                    text = string
                    visibility = View.VISIBLE
                }

                pageImagePrimaryButton.apply {
                    buttonText = "ยกเลิกสำรองข้อมูล"
                    danger = true
                }
            } else {
                mainImage.setImageResource(R.drawable.capture_backup_vc)
                descriptionMain = "คุณต้องการสำรองข้อมูล\n" + "เอกสารรับรองภายใน ETDA e-Wallet \n" + "ไว้กับผู้ให้บริการหรือไม่"
                pageImagePrimaryButton.buttonText = "สำรองข้อมูล"
            }

        }
    }

    private fun initBackupView() {
        mBinding.apply {
            pageImagePrimaryToolbar.toolbarTitle = "การสำรองข้อมูล"
            pageImagePrimaryToolbar.setOnBackClickListener {
                TransitionUtil.onBackPressedEnable = true
                requireActivity().onBackPressed()
                TransitionUtil.onBackPressedEnable = false
            }
            descriptionMain = "คุณต้องการสำรองข้อมูล\n" + "เอกสารรับรองภายใน ETDA e-Wallet \n" + "ไว้กับผู้ให้บริการหรือไม่"
            mainImage.setImageResource(R.drawable.capture_backup_vc)
            pageImagePrimaryButton.apply {
                buttonText = "สำรองข้อมูล"
                setOnButtonClickListener {
                    backupRequestBiometric()
                }
            }
            pageImageSecondaryButton.apply {
                buttonText = "ข้าม"
                setOnButtonClickListener {
                    val dialog = Dialog(it.context)
                    val dialogBinding = DataBindingUtil.inflate<DialogNormalBinding>(LayoutInflater.from(it.context), R.layout.dialog_normal, null, false)
                    dialog.setContentView(dialogBinding.root)
                    dialog.window?.apply {
                        setLayout(
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT)
                        setGravity(Gravity.CENTER)
                        setBackgroundDrawableResource(android.R.color.transparent)
                    }
                    dialogBinding.setOnCancelClickListener {
                        mViewModel.setRegisterStateBackup()
                        dialog.dismiss()
                        val fragment = MainPagerFragment()
                        val args = Bundle()
                        args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.START_SETPASSCODE)
                        fragment.arguments = args
                        parentFragmentManager.animateTransition()
                            .replace(R.id.main_content, fragment)
                            .addToBackStack(BackStack.SET_PASSCODE.name)
                            .commit()
                    }
                    dialogBinding.setOnBackupClickListener {
                        dialog.dismiss()
                        backupRequestBiometric()
                    }
                    dialog.show()
                }
            }
        }
    }

    private fun backupRequestBiometric() {
        val fragment = ProveIdentFragment()
        val args = Bundle()
        args.putSerializable(FRAGMENT_ORIGIN, IdentType.BACKUP_DID)
        fragment.arguments = args
        parentFragmentManager.animateTransition().replace(R.id.main_content, fragment).addToBackStack(BackStack.REGISTER_EWALLET.name).commit()
    }

    private fun initCheckSystemPinCodeView() {

        mBinding.mainImage.setImageResource(R.drawable.capture_set_device)
        mBinding.pageImagePrimaryButton.apply {
            buttonText = getString(R.string.title_system_setting_security)
            setOnButtonClickListener {
                val intent = Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD) //ACTION_SET_NEW_PASSWORD, ACTION_SECURITY_SETTINGS
                startActivity(intent)
            }
        }
        mBinding.descriptionTop = getString(R.string.description_system_setting_security_01)
        mBinding.headerText = getString(R.string.description_system_setting_security_02)

    }

    override fun onResume() {
        super.onResume()
        when(arguments?.getSerializable(FRAGMENT_ORIGIN) as FragmentOrigin) {
            FragmentOrigin.CHECK_SYSTEM_PIN_CODE -> {
                val km = requireActivity().applicationContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                if (km.isKeyguardSecure) {
                    requireActivity().finish()
                }
            }

            else -> {

            }
        }
    }

    private fun initUserprofileConfirmPinCode() {
        mBinding.pageImagePrimaryToolbarView.setBackgroundResource(R.drawable.home_header_bg_fade)
        mBinding.mainImage.setImageResource(R.drawable.finished_register)
        mBinding.descriptionMain = getString(R.string.setting_security_success)
        mBinding.descriptionSecond = getString(R.string.application_protected_by_new_pin_code)
        mBinding.pageImagePrimaryToolbar.apply {
            toolbarTitle = getString(R.string.setting_security)
            toolbarTitleView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        mBinding.pageImagePrimaryButton.apply {
            buttonText = getString(R.string.login_again)
            SettingPasscodeActivity.requestLogin = true
            setOnButtonClickListener {
                val intent = Intent(requireContext(), PinCodeLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                SettingPasscodeActivity.requestLogin = false
            }
        }
    }

    private fun initUserprofileNewPinCode() {
        mBinding.pageImagePrimaryToolbarView.setBackgroundResource(R.drawable.home_header_bg_fade)
        mBinding.mainImage.setImageResource(R.drawable.start_set_passcode)
        mBinding.descriptionMain = getString(R.string.setting_new_pin_code_description)
        mBinding.pageImagePrimaryToolbar.apply {
            toolbarTitle = getString(R.string.setting_security)
            setOnBackClickListener {
                requireActivity().finish()
            }
            toolbarTitleView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            backImage.setImageResource(R.drawable.ic_baseline_arrow_white_ios_24)
        }
        mBinding.pageImagePrimaryButton.apply {
            buttonText = getString(R.string.setting_new_pincode)
            setOnButtonClickListener {
                val fragment = PinCodeFragment()
                val args = Bundle()
                args.putSerializable(FRAGMENT_ORIGIN, FragmentOrigin.USER_PROFILE_MAIN_NEW_PINCODE)
                fragment.arguments = args
                parentFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(BackStack.SET_NEW_PINCODE.name)
                    .commit()
            }
        }
    }

    private fun initStartRegisterView() {
        mBinding.mainImage.setImageResource(R.drawable.ic_start_registere_wallet)
        mBinding.descriptionMain = getString(R.string.start_to_use_ewallet)
        mBinding.pageImagePrimaryButton.apply {
            buttonText = getString(R.string.active)
            setOnButtonClickListener {
                parentFragmentManager.animateTransition()
                    .add(R.id.main_content, TermFragment(), BackStack.REGISTER_APP.name)
                    .addToBackStack(BackStack.REGISTER_APP.name)
                    .hide(this@MainPagerFragment)
                    .commit()
            }
        }
    }

    private fun initRegisterDetailSuccessView() {
        mBinding.mainImage.setImageResource(R.drawable.verify_register_success)
        if (mViewModel.isResetDID) {
            mBinding.descriptionMain = getString(R.string.your_info_verify_success)
            mBinding.pageImagePrimaryToolbar.apply {
                buttonBack.visibility = View.GONE
                toolbarTitle = "เริ่มต้นใหม่สำเร็จ"
            }
        } else {
            mBinding.descriptionMain = getString(R.string.your_info_verify_success)
            mBinding.pageImagePrimaryToolbar.apply {
                buttonBack.visibility = View.GONE
                toolbarTitle = getString(R.string.register_success)
            }
        }


        mBinding.pageImagePrimaryButton.apply {
            buttonText = getString(R.string.next)
            setOnButtonClickListener {
                val fragment = ProveIdentFragment()
                val args = Bundle()
                args.putSerializable(FRAGMENT_ORIGIN, IdentType.REGISTER_DID)
                fragment.arguments = args
                parentFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(BackStack.REGISTER_EWALLET.name)
                    .commit()
            }
        }
    }

    private fun initRegisterFinish() {
        mBinding.apply {
            headerText = getString(R.string.setting_security)
            descriptionMain = getString(R.string.setting_security_success)
            descriptionSecond = getString(R.string.protect_your_application_success)
            mainImage.setImageResource(R.drawable.finished_register)

            pageImagePrimaryButton.apply {
                mViewModel.setRegisterFinishState()
                buttonText = getString(R.string.start_etda_ewallet)
                setOnButtonClickListener {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    private fun initEnableBiometricLogin() {
        mBinding.apply {
            pageImagePrimaryToolbar.toolbarTitle = getString(R.string.setting_security)
            pageImagePrimaryToolbar.setOnBackClickListener {
                enableBiometricLoginCancelDialog(it)
            }
            descriptionMain = getString(R.string.enable_biometric_instead_of)
            descriptionSecond = getString(R.string.enable_biometric_instead_of_2)
            mainImage.setImageResource(R.drawable.biometric_login)
            pageImagePrimaryButton.apply {
                buttonText = getString(R.string.active)
                setOnButtonClickListener {
                    isFlowPermission()
//                    Toast.makeText(requireContext(), "Active", Toast.LENGTH_SHORT).show()
                }
            }
            pageImageSecondaryButton.apply {
                buttonText = getString(R.string.skip)
                setOnButtonClickListener {
                    onNavigateToRegisterSuccess()
                }
            }
        }

    }

    private fun enableBiometricLoginCancelDialog(it: View) {
        val dialog = Dialog(it.context)
        val dialogBinding = DataBindingUtil.inflate<DialogCancelBaseBinding>(
            LayoutInflater.from(it.context), R.layout.dialog_cancel_base, null, false
                                                                            )
        dialog.setContentView(dialogBinding.root)
        dialog.window?.apply {
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
                     )
            setGravity(Gravity.CENTER)
            setBackgroundDrawableResource(android.R.color.transparent)
        }

        dialogBinding.apply {
            dialogTitle = getString(R.string.dialog_cancel_pincode_text_01)
            dialogDescription = getString(R.string.dialog_cancel_pincode_text_02)
            cancelText = getString(R.string.dialog_cancel_pincode_text_03)
            submitText = getString(R.string.dialog_cancel_pincode_text_04)
        }

        dialogBinding.setOnCancelClickListener {
            dialog.dismiss()
        }
        dialogBinding.setOnSubmitClickListener {
            dialog.dismiss()
            mViewModel.clearPincode()
            val fragment = MainPagerFragment()
            val args = Bundle()
            args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.START_SETPASSCODE)
            fragment.arguments = args
            parentFragmentManager.animateOnBack()
                .replace(R.id.main_content, fragment)
                .addToBackStack(BackStack.SET_PASSCODE.name)
                .commit()
        }
        dialog.show()
    }

    private fun initSetPasscodeStart() {
        mBinding.headerText = getString(R.string.set_passcode)
        mBinding.mainImage.setImageResource(R.drawable.start_set_passcode)
        mBinding.descriptionMain = getString(R.string.create_pincode_for_access_app)
        mBinding.pageImagePrimaryToolbarView.visibility = View.GONE
        mBinding.pageImagePrimaryButton.apply {
            buttonText = getString(R.string.start_crate_passcode)
            setOnButtonClickListener {
                val fragment = PinCodeFragment()
                val args = Bundle()
                args.putSerializable(FRAGMENT_ORIGIN, FragmentOrigin.START_SETPASSCODE)
                fragment.arguments = args
                parentFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(BackStack.SET_PASSCODE.name)
                    .commit()
            }
        }
    }

    private fun initRegisterEwalletView() {

        mBinding.mainImage.setImageResource(R.drawable.register_ewallet_success)

        val string = SpannableString(getString(R.string.your_did_number))
        string.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.temporary)), 12, 15, 0)

        mBinding.descriptionWithImage.apply {
            text = string
            visibility = View.VISIBLE
        }

        mBinding.setOnDidDialogClickListener {
            val bottomSheetDialog = BottomSheetFragment.newInstance(DialogType.DID_INFO)
            bottomSheetDialog.show(parentFragmentManager, "Bottom Sheet Dialog Fragment")
        }

        mBinding.descriptionWithImage.addImage("[info-image]", R.drawable.ic_info,
            resources.getDimensionPixelOffset(R.dimen.spacing_l),
            resources.getDimensionPixelOffset(R.dimen.spacing_l))

        mBinding.textWithBorder = mViewModel.getDIDAddress()
        mBinding.pageImagePrimaryToolbarView.visibility = View.GONE
        mBinding.headerText = getString(R.string.register_ewallet_success)
        mBinding.pageImagePrimaryButton.apply {
            buttonText = getString(R.string.next_step)
            setOnButtonClickListener {
                val fragment = MainPagerFragment()
                val args = Bundle()
                args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.BACKUP_STEP)
                fragment.arguments = args
//                clearBackStack(BackStack.REGISTER_EWALLET.name)
                parentFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(BackStack.REGISTER_APP.name)
                    .commit()
            }
        }
    }

    private fun notFoundType() {

    }

    private fun isFlowPermission() {
        if (isFeatureAvailableScan()) {
            createFingerPrintDialog()
        } else {
            openPermissionCheck()
        }
    }

    private fun openPermissionCheck() {
        Log.e("CheckPermission", "openPermission")
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.USE_FINGERPRINT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.USE_BIOMETRIC,
                    Manifest.permission.USE_FINGERPRINT
                ), RequestCameraPermission
            )
            return
        }
    }

    private fun isFeatureAvailableScan(): Boolean {
        Log.e("Permission ScanFace", "${isCheckBiometric()}")
        return isCheckBiometric()
    }

    private fun isCheckBiometric(): Boolean {
        val biometricManager = BiometricManager.from(requireContext())//BiometricManager.from(context)
        var checkBiometric = false
        when (biometricManager.canAuthenticate()) {

            BiometricManager.BIOMETRIC_SUCCESS ->
                checkBiometric = true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Log.e(
                    "MY_APP_TAG", "The user hasn't associated " +
                            "any biometric credentials with their account."
                )
        }
        return checkBiometric
    }

    private fun createFingerPrintDialog() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> initScannerQ() //initScannerQ()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> initScannerP() //initScannerP()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initScannerP() {
        Log.e("Permission_Version 28+", "initScannerP")

        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                super.onAuthenticationHelp(helpCode, helpString)
                Log.e("FingerPrint", "onAuthenticationHelp ($helpCode) : $helpString")
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                Log.e("FingerPrint", "onAuthenticationError ($errorCode) : $errString")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.d("FingerPrint", "onAuthenticationFailed")
//                Snackbar.make(
//                    view!!,
//                    "Authentication fail, please try again",
//                    Snackbar.LENGTH_SHORT
//                ).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                onNavigateToRegisterSuccess()
                mViewModel.setBiometricEnable()
            }

        }
        val cancellationSignal = CancellationSignal()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.e("Permission_Version 29+", "initScannerP")
            BiometricPrompt.Builder(this.context)
                .setDeviceCredentialAllowed(true)
                .setTitle("For using finger print")
                .setDescription("Place your finger on finger print scanner")
                .setNegativeButton("Cancel", {
                    it.run()
                }, { dialog, _ ->
                    dialog.dismiss()
                })
                .build()
                .authenticate(cancellationSignal, {
                    it.run()
                }, callback)
        } else {
            BiometricPrompt.Builder(this.context)
                .setTitle("For using finger print")
                .setDescription("Place your finger on finger print scanner")
                .setNegativeButton("Cancel", {
                    it.run()
                }, { dialog, _ ->
                    dialog.dismiss()
                })
                .build()
                .authenticate(cancellationSignal, {
                    it.run()
                }, callback)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun initScannerQ() {
        Log.e("Permission_Version 29+", "initScannerQ")

        val executor = ContextCompat.getMainExecutor(this.context)

        biometricPrompt = androidx.biometric.BiometricPrompt(this, executor,
            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: androidx.biometric.BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    onNavigateToRegisterSuccess()
                    mViewModel.setBiometricEnable()
                }

                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.e("Face_Finger", "onAuthenticationError ($errorCode) : $errString")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.e("Face_Finger", "onAuthenticationFailed")
//                    Snackbar.make(
//                        view!!,
//                        "Authentication fail, please try again",
//                        Snackbar.LENGTH_SHORT
//                    ).show()
                }
            })

        promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("Face ID & Finger Print")
            .setNegativeButtonText(getString(R.string.cancel))
            .setDeviceCredentialAllowed(false)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun onNavigateToRegisterSuccess() {
        val fragment = MainPagerFragment()
        val args = Bundle()
        args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_FINISH)
        fragment.arguments = args
        parentFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment)
            .addToBackStack(BackStack.SET_BIOMETRIC.name)
            .commit()
    }


}