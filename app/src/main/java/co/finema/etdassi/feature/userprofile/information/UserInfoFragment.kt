package co.finema.etdassi.feature.userprofile.information

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.toastNormal
import co.finema.etdassi.databinding.FragmentUserInfoBinding
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserInfoFragment : BaseFragment<UserInfoViewModel, FragmentUserInfoBinding>(UserInfoViewModel::class.java) {

    companion object {
        fun newInstance() = UserInfoFragment()
    }

    override val mViewModel: UserInfoViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_user_info

    override fun init() {
        super.init()
        mBinding.userInfoViewModel = mViewModel
        mBinding.toolbarUserInfo.apply {
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
            toolbarTitle = getString(R.string.my_information)
        }
        mBinding.setOnCopyClick {
            val clipboard: ClipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("DID Address", mViewModel.didAddressFull)
            clipboard.setPrimaryClip(clip)
            requireContext().toastNormal("Copied")
        }
    }

}