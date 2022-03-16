package co.finema.etdassi.feature.passcode

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import co.finema.etdassi.R
import co.finema.etdassi.common.enum.BioAuthType
import co.finema.etdassi.common.utils.*
import co.finema.etdassi.databinding.FragmentPasscodeBinding
import co.finema.etdassi.feature.MainActivity
import co.finema.etdassi.feature.bioauth.BioAuthActivity
import co.finema.etdassi.feature.mainpager.MainPagerFragment
import co.finema.etdassi.feature.pageloading.LoadingFragment
import co.finema.etdassi.feature.userprofile.settingpasscode.SettingPasscodeActivity
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PinCodeFragment: BaseFragment<PinCodeViewModel, FragmentPasscodeBinding>(
    PinCodeViewModel::class.java) {
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var biometricResult: ActivityResultLauncher<Intent>
    override val mViewModel: PinCodeViewModel by viewModel()
    override fun getLayoutRes() = R.layout.fragment_passcode
    private var repeatFail = 112
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if (result.resultCode == Activity.RESULT_OK) {
                mViewModel.changePinCode()
                val fragment = MainPagerFragment()
                val args = Bundle()
                args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.USER_PROFILE_MAIN_CONFIRM_PINCODE)
                fragment.arguments = args
                parentFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(BackStack.SET_BIOMETRIC.name)
                    .commit()
            } else {
                val fragment =  MainPagerFragment() //ProveIdentFragment()
                val args = Bundle()
                args.putSerializable(MainPagerFragment.FRAGMENT_ORIGIN, FragmentOrigin.USER_PROFILE_MAIN_NEW_PINCODE)
                fragment.arguments = args
                parentFragmentManager.animateOutIn()
                    .replace(R.id.main_content, fragment, BackStack.REGISTER_APP.name)
                    .addToBackStack(BackStack.SET_NEW_PINCODE.name)
                    .commit()
            }
        }


        biometricResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if (result.resultCode == Activity.RESULT_OK) {
                navigateToMain()
            } else {
                requireContext().toastNormal("ลองใหม่อีกครั้ง")
            }
        }
    }

    override fun init() {
        super.init()
//        mViewModel = ViewModelProvider(requireActivity()).get(SetPasscodeViewModel::class.java)

        val origin = arguments?.getSerializable(MainPagerFragment.FRAGMENT_ORIGIN) as FragmentOrigin


        mViewModel.testMultiViewModelValue = getString(R.string.setting_security)

        when(origin) {
            FragmentOrigin.USER_PROFILE_MAIN_NEW_PINCODE -> {
                mBinding.passCodeTitle = "กรอกรหัสผ่านเดิม"
                mBinding.setPasswordToolbar.apply {
                    toolbarTitle = mViewModel.testMultiViewModelValue
                    setOnBackClickListener {
                        startActivity(Intent(requireContext(), SettingPasscodeActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }

            FragmentOrigin.START_SETPASSCODE -> {
                mBinding.setPasswordToolbar.toolbarWindowFlag.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                mBinding.setPasswordToolbar.icImageBack.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
                mBinding.setPasswordToolbar.toolbarTitleView.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
                mBinding.passCodeTitle = getString(R.string.set_password)
                mBinding.setPasswordToolbar.apply {

                    toolbarTitle = mViewModel.testMultiViewModelValue
                    setOnBackClickListener {
                        TransitionUtil.onBackPressedEnable = true
                        requireActivity().onBackPressed()
                        TransitionUtil.onBackPressedEnable = false

                    }
                }
            }

            FragmentOrigin.CHECK_PIN_CODE -> {
                mBinding.setPasswordToolbar.toolbarWindowFlag.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                mBinding.passCodeTitle = getString(R.string.please_enter_pin_code)
                mViewModel.biometricEnableStatus.observe(viewLifecycleOwner, { biometricStatus ->
                    if (biometricStatus) {
                        mBinding.setOnBiometricClick {
                            loginBiometricDialog()
                        }
                        loginBiometricDialog()
                    }
                })
                mViewModel.isBiometricEnable()
            }

            FragmentOrigin.USER_PROFILE_MAIN_CONFIRM_PINCODE -> {
                mBinding.passCodeTitle = "กรอกรหัสผ่านใหม่"
                mBinding.setPasswordToolbar.apply {
                    toolbarWindowFlag.setBackgroundResource(R.drawable.home_header_bg_fade)
                    toolbarTitle = mViewModel.testMultiViewModelValue
                    setOnBackClickListener {
                        startActivity(Intent(requireContext(), SettingPasscodeActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }

            else -> {
                mBinding.setPasswordToolbar.toolbarWindowFlag.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                mBinding.setPasswordToolbar.icImageBack.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
                mBinding.setPasswordToolbar.toolbarTitleView.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
                mBinding.passCodeTitle = getString(R.string.confirm_password)
                mBinding.setPasswordToolbar.apply {
                    toolbarWindowFlag.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                    toolbarTitle = mViewModel.testMultiViewModelValue
                    setOnBackClickListener {
                        val fragment = MainPagerFragment()
                        val args = Bundle()
                        args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.START_SETPASSCODE)
                        fragment.arguments = args
                        parentFragmentManager.animateOnBack()
                            .replace(R.id.main_content, fragment)
                            .addToBackStack(BackStack.SET_PASSCODE.name)
                            .commit()
                    }
                }
            }
        }

        mViewModel.firstPinCode = arguments?.getString(ConfirmPasscodeFragment.PINCODE)
        mViewModel.activePasscode.observe(viewLifecycleOwner, { passcodeSize ->
            mBinding.passSize = passcodeSize
        })
        mViewModel.passcodeDone.observe(viewLifecycleOwner, { isDone ->
            if (isDone) {
                when(origin) {
                    FragmentOrigin.START_SETPASSCODE -> {
                        navigateToConfirmPass()
                    }
                    FragmentOrigin.USER_PROFILE_MAIN_NEW_PINCODE -> {
                        if (mViewModel.verifyPinCode()) {
                            navigateToConfirmPass()
                        }
                    }
                    FragmentOrigin.USER_PROFILE_MAIN_CONFIRM_PINCODE -> {
                        val intent = Intent(requireContext(), BioAuthActivity::class.java)
                        startForResult.launch(intent)

                    }
                    FragmentOrigin.CHECK_PIN_CODE -> {
                        if (mViewModel.verifyPinCode()) {
                            navigateToMain()
                        }
                    }
                    else -> {
                        if (BiometricManager.from(requireContext()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                            navigateToSetBiometric()
                        } else {
                            navigateToRegisterSuccess()
                        }

                    }
                }
            }
        })

        mViewModel.passcodeWrong.observe(viewLifecycleOwner, { isWrong ->
            mBinding.passWrong = isWrong
            if (isWrong) mViewModel.clear()
        })


        mBinding.apply {
            num1.setOnClickListener { mViewModel.appendCode("1") }
            num2.setOnClickListener { mViewModel.appendCode("2") }
            num3.setOnClickListener { mViewModel.appendCode("3") }
            num4.setOnClickListener { mViewModel.appendCode("4") }
            num5.setOnClickListener { mViewModel.appendCode("5") }
            num6.setOnClickListener { mViewModel.appendCode("6") }
            num7.setOnClickListener { mViewModel.appendCode("7") }
            num8.setOnClickListener { mViewModel.appendCode("8") }
            num9.setOnClickListener { mViewModel.appendCode("9") }
            num0.setOnClickListener { mViewModel.appendCode("0") }
            btnDel.setOnClickListener { mViewModel.backSpace() }

            btnBiometric.visibility = View.INVISIBLE
        }

    }

    private fun loginBiometricDialog() {
        val bioAuth = BiometricUtil()
        bioAuth.build(this, repeatFail, biometricResult, object : BiometricUtil.BiometricListener {
            override fun onSuccess() {
                navigateToMain()
            }

            override fun onFail() {
                //                            TODO("Not yet implemented")
            }
        })
    }

    private fun navigateToMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToRequestBiometric() {
        val intent = Intent(requireContext(), BioAuthActivity::class.java)
        intent.putExtra("type", BioAuthType.REQUEST)
        biometricResult.launch(intent)

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mViewModel.clear()
        }
    }

    private fun navigateToConfirmPass() {
        val fragment = PinCodeFragment()
        val args = Bundle()
        val originType = arguments?.getSerializable(MainPagerFragment.FRAGMENT_ORIGIN) as FragmentOrigin
        args.putString(ConfirmPasscodeFragment.PINCODE, mViewModel.hashPass())
        args.putSerializable(MainPagerFragment.FRAGMENT_ORIGIN, when(originType) {
            FragmentOrigin.START_SETPASSCODE -> FragmentOrigin.CONFIRM_PASSCODE
            else -> FragmentOrigin.USER_PROFILE_MAIN_CONFIRM_PINCODE
        })
        fragment.arguments = args
        parentFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment)
            .addToBackStack(BackStack.SET_PASSCODE.name)
            .commit()
    }

    private fun navigateToSetBiometric() {
        if (mViewModel.savePincode()) {
            val fragment = MainPagerFragment()
            val args = Bundle()
            val originType = arguments?.getSerializable(MainPagerFragment.FRAGMENT_ORIGIN) as FragmentOrigin
            args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.ENABLE_BIOMETRIC_LOGIN)
            fragment.arguments = args
            parentFragmentManager.animateTransition()
                .replace(R.id.main_content, fragment)
                .addToBackStack(BackStack.SET_PASSCODE.name)
                .commit()
        } else {
            mViewModel.clear()
        }
    }

    private fun navigateToRegisterSuccess() {
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