package co.finema.etdassi.common.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.ref.WeakReference
import java.time.Instant
import java.time.format.DateTimeFormatter

object QRCodeUtil {
    fun getQrCodeBitmap(source: String): Bitmap {
        val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
        val size = 512 //pixels
        val bits = QRCodeWriter().encode(source, BarcodeFormat.QR_CODE, size, size, hints)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }
}

class DownloadAndSaveImageTask(private val activity: Activity, private val bitmap: Bitmap,
                               private val fileName: String
): AsyncTask<String, Unit, Unit>() {
    private var mContext: WeakReference<Context> = WeakReference(activity.applicationContext)
    override fun doInBackground(vararg params: String?) {
        //Toast.makeText(context, "บันทึกแล้ว", Toast.LENGTH_SHORT).show()

        mContext.get()?.let { it ->

            Log.i("Seiggailion", "")

            try {

                var fos: OutputStream? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    Log.i("Seiggailion", "new")
                    val resolver = (activity).contentResolver
                    val contentValues = ContentValues()
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.jpg")
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    println("imageUri => $imageUri")
                    imageUri?.let {
                        fos = resolver.openOutputStream(it)
                    }

                } else {
                    Log.i("Seiggailion", "old")
                    val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                    val image = File(imagesDir, "$fileName.jpg")
                    fos = FileOutputStream(image)
                }


                fos?.let {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    it.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("Seiggailion", "Failed to save image.")
            }

        }
    }

}

fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}