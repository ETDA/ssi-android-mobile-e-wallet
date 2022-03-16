package co.finema.etdassi.feature.register.ewallet

import android.Manifest
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import co.finema.etdassi.R
import co.finema.etdassi.databinding.PageImagePrimaryBinding
import co.finema.etdassi.feature.pageloading.LoadingFragment
import co.finema.sso.common.base.BaseFragment
import android.text.style.ForegroundColorSpan

import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal

import android.text.SpannableString
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import co.finema.etdassi.common.enum.IdentType
import co.finema.etdassi.common.utils.*
import co.finema.etdassi.feature.mainpager.MainPagerFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProveIdentFragment: BaseFragment<ProveIdentViewModel, PageImagePrimaryBinding>(ProveIdentViewModel::class.java) {

    override val mViewModel: ProveIdentViewModel by viewModel()

    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo
    private val RequestCameraPermission = 1001

    private lateinit var startForResult: ActivityResultLauncher<Intent>

    lateinit var identType: IdentType
    private var repeatCountFail = 0

    override fun getLayoutRes() = R.layout.page_image_primary

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleNavigation()
            } else {
                requireContext().toastNormal("กรุณายืนยันตัวตน")
                requireContext().toastNormal("กรุณายืนยันตัวตน")
            }
        }
    }

    private fun handleNavigation() {
        when(identType) {

            IdentType.BACKUP_DID -> {
                mViewModel.backupEwallet(object :ApiListener {
                    override fun onSuccess() {
                        val fragment = MainPagerFragment()
                        val args = Bundle()
                        args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.START_SETPASSCODE)
                        fragment.arguments = args
//                clearBackStack(BackStack.REGISTER_EWALLET.name)
                        parentFragmentManager.animateTransition()
                            .replace(R.id.main_content, fragment)
                            .addToBackStack(BackStack.SET_PASSCODE.name)
                            .commit()
                    }

                    override fun onFail(errorMessage: String) {
                        requireContext().toastNormal(errorMessage)
                    }

                })
            }

            IdentType.REGISTER_DID -> {
                onNavigateToRegisterDID()
            }

            IdentType.RESTORE_DID -> {
                val fragment = LoadingFragment()
                val args = Bundle()
                args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_RECOVERY)
                fragment.arguments = args
                parentFragmentManager.animateTransition()
                    .add(R.id.main_content, fragment)
                    .hide(this@ProveIdentFragment)
                    .addToBackStack(BackStack.REGISTER_EWALLET.name)
                    .commit()
            }

            else -> {

            }

        }
    }

    override fun init() {
        super.init()

        arguments?.getSerializable(MainPagerFragment.FRAGMENT_ORIGIN)?.let {
            identType = it as IdentType
        } ?: kotlin.run {
            identType = IdentType.REGISTER_DID
        }

        mBinding.mainImage.setImageResource(R.drawable.access_to_pk)

        when(identType) {
            IdentType.BACKUP_DID -> {
                mBinding.pageImagePrimaryToolbar.apply {
                    toolbarTitle = "การเข้าถึงข้อมูลส่วนบุคคล"
                }
                mBinding.pageImagePrimaryButton.apply {
                    buttonText = getString(R.string.confirm)
                    setOnButtonClickListener {
                        handleNavigation()
                    }
                }
                mBinding.descriptionMain = "ขอความยินยอมในการจัดเก็บ\n" + "การสำรองข้อมูลลงบนคลาวด์"
                mBinding.descriptionSecond = "เพื่อความปลอดภัยของข้อมูลสำคัญ\n" + "ภายในแอปพลิเคชั่น"
            }
            else -> {
                mBinding.descriptionMain = getString(R.string.please_verify_with_biometric)
                val string = when(identType) {
                    IdentType.RESTORE_DID -> {
                        SpannableString(getString(R.string.description_request_to_pk_restore))
                    }

                    else -> SpannableString(getString(R.string.description_request_to_pk))
                }
                string.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.temporary)), 13, 24, 0)

                mBinding.descriptionThirdTextView.apply {
                    text = string
                    visibility = View.VISIBLE
                }
                mBinding.setOnInfoDialogClickListener {
                    val bottomSheetDialog = BottomSheetFragment.newInstance(DialogType.PRIVATE_KEY_INFO)
                    bottomSheetDialog.show(parentFragmentManager, "Bottom Sheet Dialog Fragment")
                }

                mBinding.descriptionThirdTextView.addImage("[info-image]", R.drawable.ic_info,
                    resources.getDimensionPixelOffset(R.dimen.spacing_l),
                    resources.getDimensionPixelOffset(R.dimen.spacing_l))

//        mBinding.descriptionThirdTextView.setOnClickListener {
//            val bottomSheetDialog = BottomSheetFragment()
//            bottomSheetDialog.show(parentFragmentManager, "Bottom Sheet Dialog Fragment")
//        }

                mBinding.descriptionBottom = getString(R.string.description_request_to_pk_bottom)

                mBinding.pageImagePrimaryToolbar.apply {
                    toolbarTitle = getString(R.string.request_to_access)
                    setOnBackClickListener {
                        TransitionUtil.onBackPressedEnable = true
                        requireActivity().onBackPressed()
                        TransitionUtil.onBackPressedEnable = false
                    }
                }
                mBinding.pageImagePrimaryButton.apply {
                    buttonText = getString(R.string.confirm)
                    setOnButtonClickListener {
                        isFlowPermission()
                    }
                }
            }
        }


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

            BiometricManager.BIOMETRIC_SUCCESS -> checkBiometric = true
            else -> usePassCodeHardWare()
        }
        return checkBiometric
    }

    private fun usePassCodeHardWare(){
        val km: KeyguardManager = activity?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (km.isKeyguardSecure) {
            val i = km.createConfirmDeviceCredentialIntent("Authentication required", "password")
            startForResult.launch(i)
        } else {
            Toast.makeText(
                requireContext(),
                "No any security setup done by user(pattern or password or pin or fingerprint",
                Toast.LENGTH_SHORT
                          ).show()
        }
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
                repeatCountFail += 1
                if (repeatCountFail >2) usePassCodeHardWare()
                Toast.makeText(context, errString, Toast.LENGTH_SHORT).show()
                Log.e("FingerPrint", "onAuthenticationError ($errorCode) : $errString")
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                repeatCountFail += 1
                Log.d("FingerPrint", "onAuthenticationFailed")
//                Snackbar.make(
//                    view!!,
//                    "Authentication fail, please try again",
//                    Snackbar.LENGTH_SHORT
//                ).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                handleNavigation()

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
                    handleNavigation()
                }

                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    repeatCountFail += 1
                    if (repeatCountFail >2) usePassCodeHardWare()
                    Toast.makeText(context, errString, Toast.LENGTH_SHORT).show()
                    Log.e("Face_Finger", "onAuthenticationError ($errorCode) : $errString")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    repeatCountFail += 1
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

    private fun onNavigateToRegisterDID() {
        val fragment = LoadingFragment()
        val args = Bundle()
        args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_EWALLET)
        fragment.arguments = args
        parentFragmentManager.animateTransition()
            .add(R.id.main_content, fragment)
            .hide(this@ProveIdentFragment)
            .addToBackStack(BackStack.REGISTER_EWALLET.name)
            .commit()
    }
}