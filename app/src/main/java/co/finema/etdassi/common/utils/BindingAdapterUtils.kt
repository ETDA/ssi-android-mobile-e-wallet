package co.finema.etdassi.common.utils

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import co.finema.etdassi.R
import co.finema.etdassi.feature.notification.NotificationStatus

import com.google.android.material.card.MaterialCardView

object BindingAdapterUtils  {

    /**
     * BUTTON
     *
     * Handle Button with status
     *
     */

    private fun <T> Boolean.checkCondition(t: T, f: T): T {
        return if (this) {
            t
        } else {
            f
        }
    }

    @BindingAdapter("app:primaryButtonDisable", "app:primaryButtonDanger")
    @JvmStatic fun setPrimaryButtonDisable(buttonView: MaterialCardView, disable: Boolean, danger: Boolean) {
        buttonView.apply {
            isClickable = !disable
            if (danger) {
                setCardBackgroundColor(ContextCompat.getColor(context, disable.checkCondition(R.color.disable, R.color.danger)))
            } else {
                setCardBackgroundColor(ContextCompat.getColor(context, disable.checkCondition(R.color.disable, R.color.primary)))
            }
        }

    }

    @BindingAdapter("app:fieldIsWrong", "app:fieldISWrongText")
    @JvmStatic fun fieldIsWrong(textField: MaterialCardView, isWrong: Boolean, text: AppCompatEditText) {
        if (isWrong) {
            textField.apply {
                strokeColor = ContextCompat.getColor(this.context, R.color.danger)
            }
            text.setTextColor(ContextCompat.getColor(text.context, R.color.danger))
        } else {
            textField.apply {
                strokeColor = ContextCompat.getColor(this.context, R.color.primary)
            }
            text.setTextColor(ContextCompat.getColor(text.context, R.color.primary))
        }
    }

    @BindingAdapter("app:passIsWrong", "app:passSize", "app:passIndexOne", "app:passIndexTwo",
        "app:passIndexThree", "app:passIndexFour", "app:passIndexFive", "app:passIndexSix")
    @JvmStatic fun setPassCodeView(linearLayout: LinearLayout, isWrong: Boolean, passCodeSize: Int, codeIndexOne: MaterialCardView, codeIndexTwo: MaterialCardView,
                                   codeIndexThree: MaterialCardView, codeIndexFour: MaterialCardView, codeIndexFive: MaterialCardView,
                                   codeIndexSix: MaterialCardView) {

        codeIndexOne.setCodeDefaultStyle()
        codeIndexTwo.setCodeDefaultStyle()
        codeIndexThree.setCodeDefaultStyle()
        codeIndexFour.setCodeDefaultStyle()
        codeIndexFive.setCodeDefaultStyle()
        codeIndexSix.setCodeDefaultStyle()

        if (isWrong) {
            codeIndexOne.setCodeWrongStyle()
            codeIndexTwo.setCodeWrongStyle()
            codeIndexThree.setCodeWrongStyle()
            codeIndexFour.setCodeWrongStyle()
            codeIndexFive.setCodeWrongStyle()
            codeIndexSix.setCodeWrongStyle()
        } else {
            if (passCodeSize > 0) codeIndexOne.setCodeDoneStyle()
            if (passCodeSize > 1) codeIndexTwo.setCodeDoneStyle()
            if (passCodeSize > 2) codeIndexThree.setCodeDoneStyle()
            if (passCodeSize > 3) codeIndexFour.setCodeDoneStyle()
            if (passCodeSize > 4) codeIndexFive.setCodeDoneStyle()
            if (passCodeSize > 5) codeIndexSix.setCodeDoneStyle()
        }

    }

    private fun MaterialCardView.setCodeWrongStyle() {
        this.apply {
            strokeColor = ContextCompat.getColor(context, R.color.temporary)
            strokeWidth = context.resources.getDimension(R.dimen.stroke2dp).toInt()
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.danger))
        }
    }

    private fun MaterialCardView.setCodeDefaultStyle() {
        this.apply {
            strokeColor = ContextCompat.getColor(context, R.color.brown_grey)
            strokeWidth = context.resources.getDimension(R.dimen.stroke1dp).toInt()
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    private fun MaterialCardView.setCodeDoneStyle() {
        this.apply {
            strokeColor = ContextCompat.getColor(context, R.color.secondary)
            strokeWidth = context.resources.getDimension(R.dimen.stroke1dp).toInt()
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.secondary))
        }
    }
    
//
//    @BindingAdapter("secondButtonDisable")
//    @JvmStatic fun setSecondButtonDisable(buttonView: MaterialCardView, disable: Boolean) {
//
//        buttonView.apply {
//            isCheckable = !disable
//            setCardBackgroundColor(ContextCompat.getColor(context, disable.checkCondition(R.color.white, R.color.colorPrimary)))
//        }
//
//    }

    @BindingAdapter("app:vcTextStatus")
    @JvmStatic fun setVcTextStatus(textView: TextView, status: String) {
        when(status) {
            "active" -> {
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.primary))
                textView.alpha = 1f
            }
            else -> {
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.primary))
                textView.alpha = 0.3f
            }
        }
    }

    @BindingAdapter("app:vcStatusCardView", "app:vcStatusCardStatus")
    @JvmStatic fun setVcStatusCard(materialCardView: MaterialCardView, textView: TextView, status: String?) {
        when(status) {
            NotificationStatus.ACTIVE.name, "active" -> {
                materialCardView.setCardBackgroundColor(ContextCompat.getColor(materialCardView.context, R.color.secondary))
                materialCardView.alpha = 1f
                textView.text = "ใช้งานได้"
            }
            NotificationStatus.REVOKE.name, "revoke" -> {
                materialCardView.setCardBackgroundColor(ContextCompat.getColor(materialCardView.context, R.color.brown_grey))
                materialCardView.alpha = 0.3f
                textView.text = "ถูกยกเลิก"
            }
            else -> {
                materialCardView.setCardBackgroundColor(ContextCompat.getColor(materialCardView.context, R.color.brown_grey))
                materialCardView.alpha = 0.3f
                textView.text = "หมดอายุ"
            }
        }
    }

    @BindingAdapter("app:vcItemCheckboxStatus", "app:vcItemCheckboxText")
    @JvmStatic fun setVcStatusCheckboxView(imageView: ImageView, status: Boolean, textView: TextView) {
        if (status) {
            imageView.setImageResource(R.drawable.ic_checkbox_anable)
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.secondary))
        } else {
            imageView.setImageResource(R.drawable.ic_checkbox_disable)
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.primary))
        }
    }

    @BindingAdapter("app:vcRadioBoxStatus", "app:vcRadioBoxText")
    @JvmStatic fun setVcRadioBox(imageView: ImageView, status: Boolean, textView: TextView) {
        if (status) {
            imageView.setImageResource(R.drawable.ic_radio_button_checked)
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.secondary))
        } else {
            imageView.setImageResource(R.drawable.ic_radio_button_unchecke)
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.primary))
        }
    }

    @BindingAdapter("app:vcNotificationCardView", "app:vcNotificationCardStatus")
    @JvmStatic fun setVcNotificationCard(materialCardView: MaterialCardView, textView: TextView, status: String) {
        when(status) {
            NotificationStatus.PENDING.name, "pending" -> {
                materialCardView.setCardBackgroundColor(ContextCompat.getColor(materialCardView.context, R.color.temporary))
                textView.text = "รอลงนาม"
            }
            NotificationStatus.REVOKE.name, "revoke" -> {
                materialCardView.setCardBackgroundColor(ContextCompat.getColor(materialCardView.context, R.color.disable))
                textView.text = "ถูกยกเลิก"
            }
            NotificationStatus.ACTIVE.name, "active" -> {
                materialCardView.setCardBackgroundColor(ContextCompat.getColor(materialCardView.context, R.color.secondary))
                textView.text = "ใช้งานได้"
            }
            NotificationStatus.REJECT.name, "reject" -> {
                materialCardView.setCardBackgroundColor(ContextCompat.getColor(materialCardView.context, R.color.danger))
                textView.text = "ปฏิเสธลงนาม"
            }
            else -> {
                materialCardView.setCardBackgroundColor(ContextCompat.getColor(materialCardView.context, R.color.disable))
                textView.text = "หมดอายุ"
            }
        }
    }

    @BindingAdapter("app:vcNotiDescriptionReadStatus", "app:vcNotiDescriptionSignStatus")
    @JvmStatic fun setVCNotificationDes(textView: TextView, readStatus: Boolean, signStatus: String) {
        when(signStatus) {
            NotificationStatus.PENDING.name, "pending" -> {
                textView.text = "คุณมีเอกสารที่กำลังรอลงนาม กรุณาลงนามเอกสาร"
            }
            NotificationStatus.REVOKE.name, "revoke" -> {
                textView.text = "เอกสารดังกล่าวของคุณถูกยกเลิกการใช้งาน"
            }
            else -> {
                textView.text = "คุณได้รับเอกสารใหม่"
            }
        }

        if (!readStatus) {
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.black))
        } else {
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.brown_grey))
        }
    }

    @BindingAdapter ("app:vcNotiDateTimeReadStatus")
    @JvmStatic fun setVCNotiDateTime(textView: TextView, readStatus: Boolean) {
        if (!readStatus) {
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.black))
        } else {
            textView.setTextColor(ContextCompat.getColor(textView.context, R.color.brown_grey))
        }
    }

    @BindingAdapter("app:vcNotiCardReadStatus")
    @JvmStatic fun setVCNotiCardReadStatus(cardView: MaterialCardView, readStatus: Boolean) {
        if (!readStatus) {
            cardView.strokeColor = ContextCompat.getColor(cardView.context, R.color.secondary)
            cardView.strokeWidth = cardView.resources.getDimension(R.dimen.stroke2dp).toInt()
        } else {
            cardView.strokeColor = ContextCompat.getColor(cardView.context, R.color.white)
            cardView.strokeWidth = cardView.resources.getDimension(R.dimen.stroke2dp).toInt()
        }
    }


    @BindingAdapter("app:vpStatusIcon")
    @JvmStatic fun setVPStatusIcon(imageView: AppCompatImageView, status: String) {
        when(status) {
            "active" -> {
//                imageView.setImageResource(R.drawable.ic_check_circle)
                imageView.visibility = View.VISIBLE
            }
            else -> {
                imageView.visibility = View.INVISIBLE
            }
        }
    }

    @BindingAdapter("app:secondaryBtnText", "app:secondaryBtnStatus")
    @JvmStatic fun setSecondaryBtnStatus(buttonView: MaterialCardView, textView: TextView, status: Boolean) {
        when(!status) {
            true -> {
                buttonView.strokeColor = ContextCompat.getColor(buttonView.context, R.color.primary)
                buttonView.strokeWidth = buttonView.resources.getDimension(R.dimen.stroke1dp).toInt()
                buttonView.setCardBackgroundColor(ContextCompat.getColor(buttonView.context, R.color.white))
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.primary))
            }
            else -> {
                buttonView.strokeColor = ContextCompat.getColor(buttonView.context, R.color.primary)
                buttonView.strokeWidth = buttonView.resources.getDimension(R.dimen.stroke1dp).toInt()
                buttonView.setCardBackgroundColor(ContextCompat.getColor(buttonView.context, R.color.primary))
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
            }
        }
    }


    /**
     *
     * Notification
     *
     */

    @BindingAdapter("app:vcListNotiVCTitle", "app:vcListNotiVCDate", "app:vcListNotiVCNotiView", "app:vcListNotiVCStatus")
    @JvmStatic fun setVCListNotificationStatus(constraintLayout: ConstraintLayout, vcTitle: TextView, vcDate: TextView,
                                               notificationDot: MaterialCardView, isRead: Boolean) {

        if (isRead) {
            constraintLayout.setBackgroundColor(ContextCompat.getColor(constraintLayout.context, R.color.white))
            vcTitle.typeface = ResourcesCompat.getFont(vcTitle.context, R.font.notosansthai_regular)
            vcDate.setTextColor(ContextCompat.getColor(vcDate.context, R.color.brown_grey))
            notificationDot.visibility = View.GONE
        } else {
            constraintLayout.setBackgroundColor(ContextCompat.getColor(constraintLayout.context, R.color.ice_blue))
            vcTitle.typeface = ResourcesCompat.getFont(vcTitle.context, R.font.notosansthai_semibold)
            vcDate.setTextColor(ContextCompat.getColor(vcDate.context, R.color.black))
            notificationDot.visibility = View.VISIBLE
        }

    }


}