package co.finema.etdassi.feature.userprofile

import android.app.ActivityOptions
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.findNavController
import co.finema.etdassi.R
import co.finema.etdassi.databinding.FragmentUserProfileBinding
import co.finema.etdassi.feature.userprofile.backup.BackupEwalletActivity
import co.finema.etdassi.feature.userprofile.settingpasscode.SettingPasscodeActivity
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserProfileFragment : BaseFragment<UserProfileViewModel, FragmentUserProfileBinding>(UserProfileViewModel::class.java) {

    companion object {
        fun newInstance() = UserProfileFragment()
    }

    override val mViewModel: UserProfileViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_user_profile

    override fun init() {
        super.init()
        mBinding.userprofileToolbar.toolbarTitle = getString(R.string.manage_profile)
        mViewModel.backupStatus.observe(viewLifecycleOwner, { hasBackup ->
            if (hasBackup) {
                mBinding.backupImage.setImageResource(R.drawable.ic_switch_on)
            } else {
                mBinding.backupImage.setImageResource(R.drawable.ic_switch_off)
            }
        })

        mBinding.setOnInformationClick {
            val action = UserProfileFragmentDirections.navigateToUserInfo()
            findNavController().navigate(action)
        }

        mBinding.setOnSecurityClick {
            val intent = Intent(requireContext(), SettingPasscodeActivity::class.java)
//            val transitionName = ViewCompat.getTransitionName(mBinding.tabSecurity)
//            val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), mBinding.tabSecurity, transitionName)
            startActivity(intent)
        }

        mBinding.setOnBackupClickListener {
            val intent = Intent(requireContext(), BackupEwalletActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        mViewModel.refreshBackupStatus()
        super.onResume()
    }


}