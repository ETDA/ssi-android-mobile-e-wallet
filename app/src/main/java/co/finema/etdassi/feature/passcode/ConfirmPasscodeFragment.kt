package co.finema.etdassi.feature.passcode

import android.os.Bundle
import android.view.View
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.BackStack
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.FragmentPasscodeBinding
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConfirmPasscodeFragment:BaseFragment<PinCodeViewModel, FragmentPasscodeBinding>(
    PinCodeViewModel::class.java) {
    companion object{
        const val PINCODE = "pincode"
    }
    override val mViewModel: PinCodeViewModel by viewModel()
    override fun getLayoutRes() = R.layout.fragment_passcode
    override fun init() {
        mBinding.setPasswordToolbar.apply {
            toolbarTitle = mViewModel.testMultiViewModelValue
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
        }
        mBinding.passCodeTitle = getString(R.string.confirm_password)

        mViewModel.activePasscode.observe(viewLifecycleOwner, { passcodeSize ->
            println("passcodeSize => $passcodeSize")
            mBinding.passSize = passcodeSize
        })
        mViewModel.passcodeDone.observe(viewLifecycleOwner, { isDone ->
            if (isDone) {
                val fragment = ConfirmPasscodeFragment()
                val args = Bundle()
                args.putString(PINCODE, mViewModel.hashPass())
                fragment.arguments = args
                parentFragmentManager.animateTransition()
                    .add(R.id.main_content, fragment)
                    .hide(this)
                    .addToBackStack(BackStack.SET_PASSCODE.name)
                    .commit()
            }
        })
        mBinding.passCodeTitle = getString(R.string.set_password)
        mBinding.setPasswordToolbar.apply {
            toolbarTitle = mViewModel.testMultiViewModelValue
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
        }

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


            //TODO CONDITION FOR LOGIN
            btnBiometric.visibility = View.INVISIBLE
        }


        //TODO REMOVE IT --JAMESU--
        mBinding.llFlowPassCode.setOnClickListener {

        }
    }
}