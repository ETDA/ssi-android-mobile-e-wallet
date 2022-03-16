package co.finema.etdassi.feature.qr_reader

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import co.finema.etdassi.R
import co.finema.etdassi.common.Constants
import co.finema.etdassi.common.enum.BioAuthType
import co.finema.etdassi.common.utils.toastNormal
import co.finema.etdassi.databinding.ActivityQrReaderBinding
import co.finema.etdassi.feature.sign.SignActivity
import co.finema.etdassi.feature.scan_qr.ScanQRCodeActivity
import co.finema.etdassi.feature.verifyvc.VerifyVCActivity
import me.dm7.barcodescanner.core.IViewFinder
import me.dm7.barcodescanner.core.ViewFinderView
import me.dm7.barcodescanner.zxing.ZXingScannerView

import com.google.zxing.common.HybridBinarizer

import android.graphics.Bitmap
import android.util.Log
import co.finema.etdassi.common.utils.GenericListener
import co.finema.etdassi.feature.getvc.GetVCActivity
import co.finema.etdassi.feature.qr_reader.usecase.QRReaderUseCase
import com.google.gson.Gson
import com.google.zxing.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class QRReaderActivity: AppCompatActivity(), ZXingScannerView.ResultHandler {
    companion object {
        const val REQUEST_RUNTIME_PERMISSION_CAMERA = 400
    }
    private var mScannerView: ZXingScannerView? = null
    lateinit var binding: ActivityQrReaderBinding
    private lateinit var imageResult: ActivityResultLauncher<Intent>
    private val viewModel: QRReaderViewModel by viewModel()

    /**
     * Kotlin method for Bitmap scaling
     * @param bitmap the bitmap to be scaled
     * @param pixel  the target pixel size
     * @param width  the width
     * @param height the height
     * @param max    the max(height, width)
     * @return the scaled bitmap
     */
    fun scaleBitmap(bitmap:Bitmap, pixel:Float, width:Int, height:Int, max:Int):Bitmap {
        val scale = pixel / max
        val h = Math.round(scale * height)
        val w = Math.round(scale * width)
        return Bitmap.createScaledBitmap(bitmap, w, h, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRunTimePermissionForAccessCamera()

        imageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data?.data != null) {
                /**
                 *      stop camera to use zxing feature
                 */
//                mScannerView?.stopCamera()
                println("url => ${result.data}")
                val inputStream = contentResolver.openInputStream(result.data?.data!!)
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream) ?: return@registerForActivityResult
//                val dstBmp = if (bitmap.width >= bitmap.height){
//                    val mBitmap = Bitmap.createBitmap(
//                        bitmap,
//                        bitmap.width /2 - bitmap.height /2,
//                        0,
//                        bitmap.height,
//                        bitmap.height
//                    )
//                    scaleBitmap(mBitmap, 512F, 512, 512, 512)
//
//
//                }else{
//                    val mBitmap = Bitmap.createBitmap(
//                        bitmap,
//                        0,
//                        bitmap.height /2 - bitmap.width /2,
//                        bitmap.width,
//                        bitmap.width
//                    )
//                    scaleBitmap(mBitmap, 512F, 512, 512, 512)
//                }
                val width: Int = bitmap.width
                val height: Int = bitmap.height
                val pixels = IntArray(width * height)
                println("pixels => ${pixels.size}, height => $height, width => $width")
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                bitmap.recycle()
                val source = RGBLuminanceSource(width, height, pixels)
                val bBitmap = BinaryBitmap(HybridBinarizer(source))
                val reader = MultiFormatReader()
                try {
                    val qrResult = reader.decodeWithState(bBitmap)
                    navigateResult(qrResult.text)
                } catch (e: NotFoundException) {
                    Log.e("TAG", "decode exception", e)
//                    Toast.makeText(
//                        this,
//                        "can't read QR image",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    e.printStackTrace()

                    navigateToErrorView()


                } catch (e: Exception) {
                    e.printStackTrace()
                    finish()
//                    mScannerView?.startCamera()
                }

            } else {
                this.toastNormal("ลองใหม่อีกครั้ง")
            }
        }


        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT

        binding = DataBindingUtil.setContentView(this, R.layout.activity_qr_reader)
        setContentView(binding.root)

        binding.toolbar.toolbarWindowFlag.setBackgroundResource(R.drawable.second_header_bg_fade)
        binding.toolbar.setOnBackClickListener {
            finish()
        }
        binding.toolbar.toolbarTitle = "สแกน QR code"

        val contentFrame = findViewById<View>(R.id.scan_qr_view) as ViewGroup
        mScannerView = object :ZXingScannerView(this) {
            override fun createViewFinderView(context: Context?): IViewFinder {
                return CustomViewFinderView(context)
            }
        }
        mScannerView?.setBorderCornerRadius(100)
        mScannerView?.setBorderStrokeWidth(20)
        contentFrame.addView(mScannerView)

        binding.openGallery.setOnClickListener {
//            Toast.makeText(this, "open", Toast.LENGTH_SHORT).show()
            val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imageResult.launch(pickImageIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        mScannerView?.setResultHandler(this)
        mScannerView?.startCamera()
    }

    override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()
    }

    override fun handleResult(rawResult: Result?) {
        navigateResult(rawResult?.text)
    }

    private fun navigateResult(qrString: String?) {
        try {
            qrString?.let {
                val textResultModel = Gson().fromJson(qrString, QRTextResultModel::class.java)
                textResultModel?.let {
                    when(textResultModel.operation) {
                        "GET_VC" -> {
                            val intent = Intent(this, GetVCActivity::class.java)
                            intent.putExtra("data", textResultModel)
                            startActivity(intent)
                            this.finish()
                        }
                        "VERIFY_VC" -> {
                            val intent = Intent(this, VerifyVCActivity::class.java)
                            intent.putExtra("data", textResultModel)
                            startActivity(intent)
                            this.finish()
                        }

                        "vc" -> {
                            val intent = Intent(this, SignActivity::class.java)
                            intent.putExtra("result", qrString)
                            intent.putExtra("feature", Constants.GET_NEW_VC_FROM_QR_CODE)
                            startActivity(intent)
                            finish()
                        }

                        "GET_VP_REQUEST" -> {
                            viewModel.getGetVPDoc(textResultModel, object :GenericListener<QRReaderUseCase.RequestingVPDocResponse> {
                                override fun onSuccess(response: QRReaderUseCase.RequestingVPDocResponse) {
                                    val intent = Intent(this@QRReaderActivity, ScanQRCodeActivity::class.java)
                                    intent.putExtra("result", response)
                                    startActivity(intent)
                                    finish()
                                }

                                override fun onFail(errorMessage: String) {
                                    navigateToErrorView()
                                }

                            })

                        }

                        else -> {
                            if (intent.getSerializableExtra("type") == BioAuthType.SCAN_MAIN) {
                                val intent = Intent(this, ScanQRCodeActivity::class.java)
                                intent.putExtra("result", qrString)
                                startActivity(intent)
                                this.finish()
                            } else {
                                val intent = Intent()
                                intent.putExtra("qr_data", qrString)
                                setResult(Activity.RESULT_OK, intent)
                                println("text => $qrString")
                                finish()
                            }
                        }
                    }
                } ?: kotlin.run {
                    navigateToErrorView()
                }
            }?: kotlin.run {
                navigateToErrorView()
            }

        } catch (e: Exception) {
            navigateToErrorView()
        }
    }

    private fun navigateToErrorView() {
        val intent = Intent(this, QRReaderErrorActivity::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
                                           ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_RUNTIME_PERMISSION_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //
                } else {
                    val showRational = ActivityCompat.shouldShowRequestPermissionRationale(
                        this, android.Manifest.permission.CAMERA)
                    if (showRational) {
                        requestRunTimePermissionForAccessCamera()
                    } else {
                        this.finish()
                    }
                }
            }
        }
    }






    private fun requestRunTimePermissionForAccessCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf<String>(android.Manifest.permission.CAMERA), REQUEST_RUNTIME_PERMISSION_CAMERA)
        } else {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_qr_reader)
            setContentView(binding.root)
//            setResult(Activity.RESULT_CANCELED)
//            finish()
        }
    }


    private class CustomViewFinderView : ViewFinderView {
        val PAINT = Paint()

        constructor(context: Context?) : super(context) {
            init()
        }

        constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
            init()
        }

        private fun init() {
            PAINT.color = Color.WHITE
            PAINT.isAntiAlias = true
            setSquareViewFinder(true)
        }
    }
}