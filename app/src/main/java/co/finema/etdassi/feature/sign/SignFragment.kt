package co.finema.etdassi.feature.sign

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.*
import co.finema.etdassi.databinding.DialogTextOnlyBinding
import co.finema.etdassi.databinding.DialogWithEdittextBinding
import co.finema.etdassi.databinding.FragmentSignBinding
import co.finema.etdassi.feature.myvc.myvctab.detail.TitleDescriptionAdapter
import co.finema.etdassi.feature.myvc.myvctab.detail.VCAttributeAdapter
import co.finema.etdassi.feature.sign.adapter.SignDidAdapter
import co.finema.etdassi.feature.sign.adapter.SignStatusCardAdapter
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignFragment: BaseFragment<SignViewModel, FragmentSignBinding>(SignViewModel::class.java) {
    override val mViewModel: SignViewModel by sharedViewModel()

    override fun getLayoutRes(): Int {
        return R.layout.fragment_sign
    }

    private lateinit var biometricResult: ActivityResultLauncher<Intent>
    private lateinit var rejectResult: ActivityResultLauncher<Intent>
    private var repeatFail = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biometricResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                callToSignVC()

            } else {
                repeatFail += 1
                requireContext().toastNormal("ลองใหม่อีกครั้ง")
            }
        }

        rejectResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                dialogRejectSuccess()
            } else {
                requireContext().toastNormal("ลองใหม่อีกครั้ง")
            }
        }
    }

    private fun callToSignVC() {
        createLoadingDialog()
        mViewModel.signVC(object :GenericListener<String> {
            override fun onSuccess(response: String) {
                dismissLoadingDialog()
                if (response == "success") {
                    val data = SignViewModel.SignResultPageData(
                        notificationId = mViewModel.notificationData.value?.notificationId,
                        vcDetail = mViewModel.notificationData.value?.vcDetail
                    )
                    val fragment = SignResultFragment()
                    val bundle = Bundle()
                    bundle.putParcelable("data", data)
                    fragment.arguments = bundle
                    parentFragmentManager.animateTransition()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(null)
                    .commit()
                } else {
                    requireContext().toastNormal("Try again")
                }
            }

            override fun onFail(errorMessage: String) {
                dismissLoadingDialog()
                requireContext().toastNormal(errorMessage)
            }

        })

    }

    override fun init() {
        super.init()
        mBinding.toolbar.apply {
            toolbarWindowFlag.setBackgroundResource(R.drawable.second_header_bg_fade)
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
            toolbarTitle = "เอกสารรอลงนาม"
        }
        val didAdapter = SignDidAdapter()
        val statusAdapter = SignStatusCardAdapter()
        val titleDescriptionAdapter = TitleDescriptionAdapter()
        val vcAttributeAdapter = VCAttributeAdapter()
        val conCardAdapter = ConcatAdapter(didAdapter, statusAdapter, titleDescriptionAdapter, vcAttributeAdapter)
        mBinding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = conCardAdapter
        }
        mViewModel.notificationData.observe(viewLifecycleOwner, {
            didAdapter.submitList(listOf(
                SignDidAdapter.DidRequester(
                    didRequester = it.didRequester,
                    creator = it.creator
                )
            ))
            statusAdapter.submitList(listOf(
                SignStatusCardAdapter.SignStatusCard(
                    vcType = it.vcType,
                    status = it.status
                )
            ))
            titleDescriptionAdapter.submitList(listOf("รายละเอียดเอกสาร"))
            vcAttributeAdapter.submitList(it.vcDetail)
        })
        arguments?.getString("notiID")?.let {
            mViewModel.getDataFromNotiId(it)
        }




        mBinding.setOnCancelClickListener {
            buildDialog()
        }
        mBinding.setOnSignClickListener {
            val bioAuth = BiometricUtil()
            bioAuth.build(this, repeatFail, biometricResult, object : BiometricUtil.BiometricListener {
                override fun onSuccess() {
                    callToSignVC()
                }

                override fun onFail() {
                    repeatFail += 1
                    //                            TODO("Not yet implemented")
                }
            })
        }


    }

    private fun buildDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DataBindingUtil.inflate<DialogWithEdittextBinding>(
            LayoutInflater.from(requireContext()), R.layout.dialog_with_edittext, null, false
        )
        dialog.setContentView(dialogBinding.root)
        dialog.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.CENTER)
            val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            setBackgroundDrawable(InsetDrawable(ColorDrawable(resources.getColor(android.R.color.transparent, null)), margin, 0, margin, 0))
        }

        dialogBinding.editText.addTextChangedListener(object :TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    dialogBinding.editTextBorder.strokeColor = ContextCompat.getColor(requireContext(), R.color.secondary)
                }
            }

        })

        dialogBinding.apply {
            dialogTitle = "ปฏิเสธการลงนามเอกสารนี้?"
            dialogDescription = "กรุณาระบุเหตุผลสำหรับปฏิเสธการลงนาม"
            cancelText = "ยกเลิก"
            submitText = "ยืนยัน"
        }

        dialogBinding.setOnCancelClickListener {
            dialog.dismiss()
        }
        dialogBinding.setOnSubmitClickListener {
            if (!dialogBinding.editText.text.isNullOrBlank()) {
                mViewModel.rejectComment.value = dialogBinding.editText.text.toString()
                dialog.dismiss()


                val bioAuth = BiometricUtil()
                bioAuth.build(this, repeatFail, rejectResult, object : BiometricUtil.BiometricListener {
                    override fun onSuccess() {
                        rejectSignVC()
                    }

                    override fun onFail() {
                        repeatFail += 1
                        requireContext().toastNormal(resources.getString(R.string.btn_text_try_again_01))
                    }
                })
            } else {
                requireContext().toastNormal("กรุณาระบุเหตุผล")
            }

        }
        dialog.show()
    }

    private fun rejectSignVC() {
        createLoadingDialog()
        mViewModel.rejectVC(object :GenericListener<Boolean> {
            override fun onSuccess(response: Boolean) {
                dismissLoadingDialog()
                dialogRejectSuccess()
            }

            override fun onFail(errorMessage: String) {
                dismissLoadingDialog()
                requireContext().toastNormal(errorMessage)
            }
        })

    }

    private fun dialogRejectSuccess() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DataBindingUtil.inflate<DialogTextOnlyBinding>(
            LayoutInflater.from(requireContext()), R.layout.dialog_text_only, null, false
        )
        dialog.setContentView(dialogBinding.root)
        dialog.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.CENTER)
            val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            setBackgroundDrawable(InsetDrawable(ColorDrawable(resources.getColor(android.R.color.transparent, null)), margin, 0, margin, 0))
        }

        dialogBinding.apply {
            dialogTitle = "ปฏิเสธการลงนามเสร็จสิ้น"
            dialogDescription = "เอกสารดังกล่าวได้ถูกส่งกลับไปยังผู้ร้องขอ"
        }

        dialog.show()

        dialog.setOnDismissListener {
            requireActivity().finish()
        }
    }

}