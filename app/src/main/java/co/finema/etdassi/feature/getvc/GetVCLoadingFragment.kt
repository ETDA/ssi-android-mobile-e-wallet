package co.finema.etdassi.feature.getvc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.ApiListener
import co.finema.etdassi.common.utils.BiometricUtil
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.common.utils.toastNormal
import co.finema.etdassi.databinding.FragmentLoadingBinding
import co.finema.etdassi.feature.getvc.usecase.GetVCUseCase
import co.finema.etdassi.feature.qr_reader.QRReaderErrorActivity
import co.finema.etdassi.feature.qr_reader.QRTextResultModel
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GetVCLoadingFragment: BaseFragment<GetVCLoadingViewModel, FragmentLoadingBinding>(GetVCLoadingViewModel::class.java) {
    override val mViewModel: GetVCLoadingViewModel by sharedViewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_loading
    }

    private lateinit var biometricResult: ActivityResultLauncher<Intent>

    private var repeatFail = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biometricResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                doActionGetVC()

            } else {
                repeatFail += 1
                requireContext().toastNormal("ลองใหม่อีกครั้ง")
            }
        }
    }

    private fun doActionGetVC() {
        arguments?.getParcelable<QRTextResultModel>("data")?.let { qrTextResultModel ->
            mViewModel.getVC(qrTextResultModel, object :GetVCLoadingViewModel.GetVCListener {
                override fun onFail(errorMessage: String) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), QRReaderErrorActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                override fun onSuccess() {
                    val fragment =  GetVCResultFragment()
                    val args = Bundle()
                    args.putParcelable("data", mViewModel.vcGettingResult.value)
                    fragment.arguments = args
                    parentFragmentManager.animateTransition()
                        .replace(R.id.main_content, fragment)
                        .addToBackStack(null)
                        .commit()
                }

            })
        } ?: kotlin.run {
            requireActivity().finish()
        }

    }

    override fun init() {
        super.init()
        //ANIMATE LOADING
        val rotation = AnimationUtils.loadAnimation(requireContext(), R.anim.spin_view)
        mBinding.loadingImage.startAnimation(rotation)
        mBinding.mainDescription = "กรุณารอสักครู่"

        val bioAuth = BiometricUtil()
        bioAuth.build(this, repeatFail, biometricResult, object : BiometricUtil.BiometricListener {
            override fun onSuccess() {
                doActionGetVC()
            }

            override fun onFail() {
                repeatFail += 1
                requireContext().toastNormal("ลองใหม่อีกครั้ง")
                //                            TODO("Not yet implemented")
            }
        })
    }
}