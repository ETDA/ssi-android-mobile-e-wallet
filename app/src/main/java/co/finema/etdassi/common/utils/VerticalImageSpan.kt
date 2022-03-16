package co.finema.etdassi.common.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder

import android.text.style.ImageSpan
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat


class VerticalImageSpan(drawable: Drawable?) :
    ImageSpan(drawable!!) {
    /**
     * update the text line height
     */
    override fun getSize(
        paint: Paint, text: CharSequence?, start: Int, end: Int,
        fontMetricsInt: Paint.FontMetricsInt?
    ): Int {
        val drawable = drawable
        val rect: Rect = drawable.bounds
        if (fontMetricsInt != null) {
            val fmPaint: Paint.FontMetricsInt = paint.getFontMetricsInt()
            val fontHeight: Int = fmPaint.descent - fmPaint.ascent
            val drHeight: Int = rect.bottom - rect.top
            val centerY: Int = fmPaint.ascent + fontHeight / 2
            fontMetricsInt.ascent = centerY - drHeight / 2
            fontMetricsInt.top = fontMetricsInt.ascent
            fontMetricsInt.bottom = centerY + drHeight / 2
            fontMetricsInt.descent = fontMetricsInt.bottom
        }
        return rect.right
    }

    /**
     * see detail message in android.text.TextLine
     *
     * @param canvas the canvas, can be null if not rendering
     * @param text the text to be draw
     * @param start the text start position
     * @param end the text end position
     * @param x the edge of the replacement closest to the leading margin
     * @param top the top of the line
     * @param y the baseline
     * @param bottom the bottom of the line
     * @param paint the work paint
     */
    override fun draw(
        canvas: Canvas, text: CharSequence?, start: Int, end: Int,
        x: Float, top: Int, y: Int, bottom: Int, paint: Paint
    ) {
        val drawable = drawable
        canvas.save()
        val fmPaint: Paint.FontMetricsInt = paint.getFontMetricsInt()
        val fontHeight: Int = fmPaint.descent - fmPaint.ascent
        val centerY: Int = y + fmPaint.descent - fontHeight / 2
        val transY = centerY - (drawable.bounds.bottom - drawable.bounds.top) / 2
        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
}

fun TextView.addImage(atText: String, @DrawableRes imgSrc: Int, imgWidth: Int, imgHeight: Int) {
    val ssb = SpannableStringBuilder(this.text)

    val drawable = ContextCompat.getDrawable(this.context, imgSrc) ?: return
    drawable.mutate()
    drawable.setBounds(0, 0,
        imgWidth,
        imgHeight)
    val start = text.indexOf(atText)
    ssb.setSpan(VerticalImageSpan(drawable), start, start + atText.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    this.setText(ssb, TextView.BufferType.SPANNABLE)
}
