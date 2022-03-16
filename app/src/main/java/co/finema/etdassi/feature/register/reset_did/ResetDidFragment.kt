package co.finema.etdassi.feature.register.reset_did

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils
import androidx.core.content.ContextCompat
import co.finema.etdassi.R
import co.finema.etdassi.common.Constants
import co.finema.etdassi.common.utils.*
import co.finema.etdassi.databinding.FragmentResetDidBinding
import co.finema.etdassi.feature.mainpager.MainPagerFragment
import co.finema.etdassi.feature.pageloading.LoadingFragment
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResetDidFragment : BaseFragment<ResetDidViewModel, FragmentResetDidBinding>(ResetDidViewModel::class.java) {
    override val mViewModel: ResetDidViewModel by viewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_reset_did
    }

    private lateinit var estimatedTime: CountDownTimer

    override fun init() {
        super.init()
        mBinding.email = mViewModel.email
        when(arguments?.getSerializable(MainPagerFragment.FRAGMENT_ORIGIN) as FragmentOrigin) {
            FragmentOrigin.RESET_DID -> {
                setRestView()
            }

            else -> {
                setVerifyEmailView()
            }
        }

        mViewModel.refCode.observe(viewLifecycleOwner, {
            mBinding.refCode = "Ref : $it"
        })

    }

    private fun resentVerifyEmail() {
        mViewModel.resentEmail(object : ApiListener {
            override fun onSuccess() {
                requireContext().toastNormal("ส่งอีเมลอีกครั้งสำเร็จ")
                timerResent()
            }

            override fun onFail(errorMessage: String) {
            }

        })
    }

    private fun timerResent() {
        mBinding.onResentClickListener = null
        val timer = object : CountDownTimer(Constants.RESEND_EMAIL_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mBinding.countdownResentText.text = DateUtils.formatElapsedTime(millisUntilFinished/1000)
            }

            override fun onFinish() {
                mBinding.countdownResentText.text = "ส่งอีกครั้ง"
                mBinding.setOnResentClickListener {
                    estimatedTime.cancel()
                    resentVerifyEmail()
                    estimatedTime()
                }
            }
        }
        timer.start()
    }

    private fun estimatedTime() {
        mBinding.countdownTimeText.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
        estimatedTime = object : CountDownTimer(Constants.RESET_DID_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mBinding.countdownTimeText.text = "(${DateUtils.formatElapsedTime(millisUntilFinished/1000)})"
                if (millisUntilFinished/1000L == 3L) {
                    mBinding.countdownTimeText.setTextColor(ContextCompat.getColor(requireContext(), R.color.danger))
                }
            }

            override fun onFinish() {
                mBinding.countdownTimeText.text = "หมดเวลา"
            }
        }
        estimatedTime.start()
    }

    private fun setRestView() {

        mViewModel.resentEmail(param = object :ApiListener {
            override fun onSuccess() {
                timerResent()
                estimatedTime()
            }

            override fun onFail(errorMessage: String) {

            }

        })

        mBinding.headerText = "ยืนยันการเริ่มต้นใหม่"
        mBinding.subTitle.text = "ระบบได้ส่งรหัสยืนยันการเริ่มต้นใหม่\n" + "ที่อีเมลของคุณ"

        setInputOtp()
    }

    private fun setVerifyEmailView() {
        timerResent()
        estimatedTime()

        mBinding.headerText = "ยืนยันการลงทะเบียน"
        mBinding.subTitle.text = "ระบบได้ส่งรหัสยืนยันการลงทะเบียน\n" + "ไปยังอีเมลของคุณ"

        setInputOtp()
    }

    private fun setInputOtp() {
        mBinding.resetCodeText1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank() && s.length == 1) {
                    mBinding.resetCodeBorder1.strokeColor =
                        ContextCompat.getColor(requireContext(), R.color.secondary)
                    mBinding.resetCodeText1.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
                    mBinding.resetCodeText2.requestFocus()
                } else {
                    mBinding.resetCodeBorder1.strokeColor =
                        ContextCompat.getColor(requireContext(), R.color.disable)
                }
            }
        })

        mBinding.resetCodeText2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank() && s.length == 1) {
                    mBinding.resetCodeBorder2.strokeColor =
                        ContextCompat.getColor(requireContext(), R.color.secondary)
                    mBinding.resetCodeText2.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
                    mBinding.resetCodeText3.requestFocus()
                } else {
                    mBinding.resetCodeBorder2.strokeColor =
                        ContextCompat.getColor(requireContext(), R.color.disable)
                    mBinding.resetCodeText1.requestFocus()
                }
            }

        })

        mBinding.resetCodeText3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank() && s.length == 1) {
                    mBinding.resetCodeBorder3.strokeColor = ContextCompat.getColor(requireContext(), R.color.secondary)
                    mBinding.resetCodeText3.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
                    mBinding.resetCodeText4.requestFocus()
                } else {
                    mBinding.resetCodeBorder3.strokeColor =
                        ContextCompat.getColor(requireContext(), R.color.disable)
                    mBinding.resetCodeText2.requestFocus()
                }
            }

        })

        mBinding.resetCodeText4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank() && s.length == 1) {
                    mBinding.resetCodeBorder4.strokeColor =
                        ContextCompat.getColor(requireContext(), R.color.secondary)
                    mBinding.resetCodeText4.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
                    mBinding.resetCodeText4.clearFocus()
                    sendResetCode()
                } else {
                    mBinding.resetCodeBorder4.strokeColor =
                        ContextCompat.getColor(requireContext(), R.color.disable)
                    mBinding.resetCodeText3.requestFocus()
                }
            }

        })
    }

    private fun sendResetCode() {
        val otpCode = "${mBinding.resetCodeText1.text}${mBinding.resetCodeText2.text}${mBinding.resetCodeText3.text}${mBinding.resetCodeText4.text}"
        println("otp -> ${otpCode.trim()}, otp -> ${otpCode.trim().length < 4}")
        if (otpCode.trim().length < 4) {
            setErrorView()
            return
        }
        mViewModel.sendOTP(otpCode, object :ApiListener {
            override fun onSuccess() {
                estimatedTime.cancel()
                if (mViewModel.isRecovery()) {
                    if (mViewModel.recoveryStateDone) {
                        navigateToRegisterDetailSuccess()
                    } else {
                        mViewModel.checkBackupEWallet(object :GenericListener<Boolean> {
                            override fun onSuccess(response: Boolean) {
                                val fragment = MainPagerFragment()
                                val args = Bundle()
                                args.putSerializable(LoadingFragment.FRAGMENT_ORIGIN, FragmentOrigin.REGISTER_RECOVERY)
                                fragment.arguments = args
                                parentFragmentManager.animateTransition()
                                    .replace(R.id.main_content, fragment, BackStack.REGISTER_APP.name)
                                    .addToBackStack(BackStack.REGISTER_APP.name)
                                    .commit()
                            }

                            override fun onFail(errorMessage: String) {
                                navigateToRegisterDetailSuccess()
                            }

                        })
                    }
                } else {
                    navigateToRegisterDetailSuccess()
                }
            }

            override fun onFail(errorMessage: String) {
                setErrorView()
            }

        })
//        parentFragmentManager.animateTransition()
//            .replace(R.id.main_content, ProveIdentFragment())
//            .addToBackStack(BackStack.REGISTER_APP.name)
//            .commit()
    }

    private fun navigateToRegisterDetailSuccess() {
        val fragment = MainPagerFragment()
        val args = Bundle()
        args.putSerializable(
            LoadingFragment.FRAGMENT_ORIGIN,
            FragmentOrigin.REGISTER_DETAIL_SUCCESS
        )
        fragment.arguments = args
        parentFragmentManager.animateTransition()
            .replace(R.id.main_content, fragment, BackStack.REGISTER_APP.name)
            .addToBackStack(BackStack.REGISTER_APP.name)
            .commit()
    }

    private fun setErrorView() {
        mBinding.resetCodeBorder1.strokeColor = ContextCompat.getColor(requireContext(), R.color.danger)
        mBinding.resetCodeBorder2.strokeColor = ContextCompat.getColor(requireContext(), R.color.danger)
        mBinding.resetCodeBorder3.strokeColor = ContextCompat.getColor(requireContext(), R.color.danger)
        mBinding.resetCodeBorder4.strokeColor = ContextCompat.getColor(requireContext(), R.color.danger)
        mBinding.resetCodeText1.setTextColor(ContextCompat.getColor(requireContext(), R.color.danger))
        mBinding.resetCodeText2.setTextColor(ContextCompat.getColor(requireContext(), R.color.danger))
        mBinding.resetCodeText3.setTextColor(ContextCompat.getColor(requireContext(), R.color.danger))
        mBinding.resetCodeText4.setTextColor(ContextCompat.getColor(requireContext(), R.color.danger))
    }


}