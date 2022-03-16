package co.finema.etdassi.common.utils

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import co.finema.etdassi.R
import co.finema.etdassi.databinding.DialogInformationBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment :BottomSheetDialogFragment() {

    companion object {
        const val DIALOG_TYPE = "DIALOG_TYPE"
        fun newInstance(dialogType: DialogType): BottomSheetFragment {
            val fragment = BottomSheetFragment()
            val bundle = Bundle()
            bundle.putSerializable(DIALOG_TYPE, dialogType)
            fragment.arguments = bundle
            return fragment
        }
    }

    lateinit var binding: DialogInformationBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_information, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getSerializable(DIALOG_TYPE)?.let { bundle ->
            when(bundle as DialogType) {
                DialogType.PRIVATE_KEY_INFO -> {
                    binding.dialogInfoTitle = getString(R.string.dialog_title_private_key_info)
                    binding.dialogInfoDescription = getString(R.string.dialog_description_private_key)
                }

                else -> {
                    binding.dialogInfoTitle = getString(R.string.dialog_title_did_info)
                    binding.dialogInfoDescription = getString(R.string.dialog_description_did_info)
                }
            }
        }



    }


}