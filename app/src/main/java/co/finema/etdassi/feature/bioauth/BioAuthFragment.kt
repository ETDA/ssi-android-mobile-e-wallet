package co.finema.etdassi.feature.bioauth

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Context.KEYGUARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import co.finema.etdassi.R
import co.finema.etdassi.common.enum.BioAuthType
import co.finema.etdassi.common.utils.BiometricUtil
import co.finema.etdassi.common.utils.addImage
import co.finema.etdassi.common.utils.toastNormal
import co.finema.etdassi.databinding.FragmentBioauthBinding
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class BioAuthFragment :
    BaseFragment<BioAuthViewModel, FragmentBioauthBinding>(BioAuthViewModel::class.java) {

    companion object {

    }

    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override val mViewModel: BioAuthViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_bioauth

    private var repeatFail = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    requireActivity().setResult(Activity.RESULT_OK)
                    requireActivity().finish()
                } else {
                    repeatFail += 1
                    requireContext().toastNormal("กรุณายืนยันตัวตน")
                }
            }
    }

    override fun init() {
        super.init()

        when (arguments?.getSerializable("type")) {
            BioAuthType.PROFILE_BACKUP_WALLET -> {
                setViewProfileBackupWallrtType()
            }

            null -> {
                setViewNoType()
            }

            else -> {
                setViewGeneralType()
            }
        }

    }

    private fun setViewProfileBackupWallrtType() {
        mBinding.userprofileToolbar.toolbarBg.setBackgroundResource(R.color.white)
        mBinding.userprofileToolbar.toolbarTitle = "การสำรองข้อมูล"
        mBinding.userprofileToolbar.toolbarTitleView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary
            )
        )
        mBinding.userprofileToolbar.onBackClickListener = null
        mBinding.userprofileToolbar.onCloseListener = null
        mBinding.mainImage.setImageResource(R.drawable.access_to_pk)
        mBinding.descriptionMain = "ขอความยินยอมในการจัดเก็บ\n" + "การสำรองข้อมูลลงบนคลาวด์"
        mBinding.descriptionSecond = "เพื่อความปลอดภัยของข้อมูลสำคัญ\n" + "ภายในแอปพลิเคชัน"
        mBinding.bioAuthPrimaryButton.apply {
            buttonText = getString(R.string.confirm)
            setOnButtonClickListener {
                requireActivity().setResult(Activity.RESULT_OK)
                requireActivity().finish()
            }
        }
    }

    private fun setViewGeneralType() {
        mBinding.userprofileToolbar.toolbarBg.setBackgroundResource(R.color.white)
        mBinding.userprofileToolbar.toolbarTitle =
            getString(R.string.toolbar_request_authentication)
        mBinding.userprofileToolbar.toolbarTitleView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary
            )
        )
        mBinding.userprofileToolbar.setOnCloseListener {
            requireActivity().setResult(Activity.RESULT_CANCELED)
            requireActivity().finish()
        }
        mBinding.mainImage.setImageResource(R.drawable.access_to_pk)
        mBinding.descriptionMain = getString(R.string.please_verify_with_biometric)

        val string = SpannableString(getString(R.string.description_request_to_pk))
        string.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.temporary
                )
            ), 13, 24, 0
        )

        mBinding.descriptionThirdTextView.apply {
            text = string
            visibility = View.VISIBLE
        }

        mBinding.descriptionThirdTextView.addImage(
            "[info-image]",
            R.drawable.ic_info,
            resources.getDimensionPixelOffset(R.dimen.spacing_l),
            resources.getDimensionPixelOffset(R.dimen.spacing_l)
        )


        mBinding.descriptionBottom = getString(R.string.description_request_to_pk_bottom)

        mBinding.userprofileToolbar.toolbarWindowFlag.setBackgroundResource(R.drawable.second_header_bg_fade)

        mBinding.bioAuthPrimaryButton.apply {
            buttonText = getString(R.string.confirm)
            setOnButtonClickListener {
                usePassCodeHardWare()
                //                    val bioAuth = BiometricUtil()
                //                    bioAuth.build(this@BioAuthFragment, 202, startForResult, object : BiometricUtil.BiometricListener {
                //                        override fun onSuccess() {
                //                            requireActivity().setResult(Activity.RESULT_OK)
                //                            requireActivity().finish()
                //                        }
                //                        override fun onFail() {
                //                            //                            TODO("Not yet implemented")
                //                        }
                //                    })
            }
        }
    }

    private fun setViewNoType() {
        mBinding.userprofileToolbar.toolbarBg.setBackgroundResource(R.color.white)
        mBinding.userprofileToolbar.toolbarTitle =
            getString(R.string.toolbar_request_authentication)
        mBinding.userprofileToolbar.toolbarTitleView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary
            )
        )
        mBinding.userprofileToolbar.setOnCloseListener {
            requireActivity().setResult(Activity.RESULT_CANCELED)
            requireActivity().finish()
        }
        mBinding.descriptionMain = getString(R.string.please_verify_with_biometric)
        mBinding.descriptionSecond = getString(R.string.use_for_setting_new_pin_code)
        mBinding.mainImage.setImageResource(R.drawable.start_set_passcode)
        mBinding.bioAuthPrimaryButton.apply {
            buttonText = getString(R.string.confirm)
            setOnButtonClickListener {
                usePassCodeHardWare()
            }
        }
    }

    private fun usePassCodeHardWare() {
        val bioAuth = BiometricUtil()
        bioAuth.build(
            this@BioAuthFragment,
            repeatFail,
            startForResult,
            object : BiometricUtil.BiometricListener {
                override fun onSuccess() {
                    requireActivity().setResult(Activity.RESULT_OK)
                    requireActivity().finish()
                }

                override fun onFail() {
                    repeatFail += 1
                    //                            TODO("Not yet implemented")
                }
            })
    }


}