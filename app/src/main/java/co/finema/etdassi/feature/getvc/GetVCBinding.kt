package co.finema.etdassi.feature.getvc

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import co.finema.etdassi.R
import com.google.android.material.card.MaterialCardView

object GetVCBinding {
    @BindingAdapter("app:setCountGetVCSuccess", "app:setCountGetVCTotal")
    @JvmStatic fun setGetVCCountTotal(textView: TextView, successCase: String?, totalCase: String) {
        textView.text = "ข้อมูลเอกสารรับรองที่ได้รับ($successCase/$totalCase):"

    }
}