package co.finema.etdassi.feature.register.term

import androidx.lifecycle.MutableLiveData
import co.finema.etdassi.R
import co.finema.etdassi.common.base.BaseViewModel
import co.finema.etdassi.common.utils.BackStack
import co.finema.etdassi.common.utils.animateTransition
import co.finema.etdassi.databinding.FragmentTermBinding
import co.finema.etdassi.feature.register.detail.RegisterDetailFragment
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class TermFragment:BaseFragment<TermViewModel, FragmentTermBinding>(TermViewModel::class.java) {

    override val mViewModel: TermViewModel by viewModel()
    private val acceptStatus = MutableLiveData<Boolean>()

    init {
        acceptStatus.value = false
    }

    override fun getLayoutRes() = R.layout.fragment_term

    override fun init() {
        super.init()
        acceptStatus.observe(viewLifecycleOwner, { accept ->
            if (accept) {
                mBinding.checkImage.setImageResource(R.drawable.ic_checkbox_anable)
                mBinding.termButton.disable = false
            } else {
                mBinding.checkImage.setImageResource(R.drawable.ic_check_box_outline_black)
                mBinding.termButton.disable = true
            }
        })

        mBinding.setOnAcceptClickListener {
            acceptStatus.value = !acceptStatus.value!!
        }

        mBinding.termDescription = getString(R.string.term_string)

        mBinding.termButton.apply {
            buttonText = getString(R.string.accept)
            setOnButtonClickListener {
                parentFragmentManager.animateTransition()
                    .add(R.id.main_content, RegisterDetailFragment(), "REGISTER_EKYC")
                    .hide(this@TermFragment)
                    .addToBackStack(BackStack.REGISTER_APP.name)
                    .commit()
            }
        }
    }
}