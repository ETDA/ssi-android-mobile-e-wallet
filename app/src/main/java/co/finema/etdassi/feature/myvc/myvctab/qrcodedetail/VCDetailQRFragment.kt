package co.finema.etdassi.feature.myvc.myvctab.qrcodedetail

import android.graphics.Bitmap
import android.os.Environment
import android.view.View
import android.view.animation.AnimationUtils
import co.finema.etdassi.R
import co.finema.etdassi.common.utils.DownloadAndSaveImageTask
import co.finema.etdassi.common.utils.QRCodeUtil
import co.finema.etdassi.common.utils.toastNormal
import co.finema.etdassi.common.utils.writeBitmap
import co.finema.etdassi.databinding.FragmentVcQrBinding
import co.finema.etdassi.feature.myvc.myvctab.MyVCListAdapter
import co.finema.sso.common.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class VCDetailQRFragment: BaseFragment<VCDetailQRViewModel, FragmentVcQrBinding>(VCDetailQRViewModel::class.java) {



    override val mViewModel: VCDetailQRViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_vc_qr

    override fun init() {
        super.init()

        mBinding.vcDetailQrToolbar.apply {
            toolbarTitle = getString(R.string.title_my_doc)
            setOnBackClickListener {
                requireActivity().onBackPressed()
            }
        }

        val rotation = AnimationUtils.loadAnimation(requireContext(), R.anim.spin_view)
        mBinding.imageLoading.startAnimation(rotation)

        arguments?.getString("cid")?.let {
            mViewModel.generateQR(it)
        }
        arguments?.getString("vcType")?.let {
            mBinding.vcName = it
        }
        arguments?.getString("vcStatus")?.let {
            mBinding.status = it
        }

        mViewModel.qrString.observe(viewLifecycleOwner, {
            mBinding.qrView.visibility = View.VISIBLE
            mBinding.imageLoading.apply {
                clearAnimation()
                visibility = View.GONE
            }
            val bitmap = QRCodeUtil.getQrCodeBitmap(it)
            mBinding.qrImage.setImageBitmap(bitmap)

            val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.ENGLISH)
            val stamp = Timestamp(System.currentTimeMillis())
            val date = simpleDateFormat.format(Date(stamp.time))

            mBinding.setSaveQrListener {
                DownloadAndSaveImageTask(requireActivity(), bitmap, mViewModel.vcData.value?.id ?:date).execute("")
                requireContext().toastNormal("Saved")
            }
        })
    }
}