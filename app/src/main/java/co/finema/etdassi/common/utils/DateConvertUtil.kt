package co.finema.etdassi.common.utils

import co.finema.etdassi.common.utils.DateConvertUtil.toShortDateTime
import java.text.SimpleDateFormat
import java.util.*

object DateConvertUtil {
    fun String.toShortDateTime(): String {
        if (isDateFormat(this) == false) return this
        val dateFormat = object : SimpleDateFormat("yyyy-MM-dd'T'HH:mm"){}
        dateFormat.timeZone = TimeZone.getTimeZone("UTC+7")
        val time = dateFormat.parse(this)?.time ?: kotlin.run { return "" }
        val c = Calendar.getInstance()
        c.time = Date(time)
        val monthFormat = object : SimpleDateFormat("dd MMM ปป เวลา HH:mm", Locale("th", "TH")) {}
        return monthFormat.format(time).replace("ปป", (c.get(Calendar.YEAR) + 543).toString().substring(2))
    }

    fun Long.toShortDateTime(): String {
        val date = Date(this * 1000)
        val monthFormat = object : SimpleDateFormat("dd MMM ปป เวลา HH:mm", Locale("th", "TH")) {}
        val yearsFormat = object : SimpleDateFormat("yyyy") {}
        return monthFormat.format(date).replace("ปป", (yearsFormat.format(date).toInt() + 543).toString().substring(2))
    }

    fun String.toShortDate(): String {
        val dateFormat = object : SimpleDateFormat("yyyy-MM-dd"){}
        val time = dateFormat.parse(this)?.time ?: kotlin.run { return "" }
        val monthFormat = object : SimpleDateFormat("dd MMM ปป", Locale("th", "TH")) {}
        val c = Calendar.getInstance()
        c.time = Date(time)
        return monthFormat.format(time).replace("ปป", (c.get(Calendar.YEAR) + 543).toString().substring(2))
    }

    fun convertDateToTimeStamp(dateString: String?): Long? {
        if (dateString == null) return null
        val dateFormat = object : SimpleDateFormat("yyyy-MM-dd'T'HH:mm"){}
        return dateFormat.parse(dateString)?.time
    }

    fun Long.toISOFormat(): String {
        val date = if (this.toString().length >10) {
            Date(this )
        } else {
            Date(this * 1000)
        }
        val sdf = object : SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"){}
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(date)
    }

    fun Long.toLocalFormat(): String {
        val date = Date(this)
        val sdf = object : SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"){}
        sdf.timeZone = TimeZone.getTimeZone("UTC+7")
        return sdf.format(date)
    }

    private fun isDateFormat(value: String?): Boolean? {
        return value?.matches(Regex("([0-9]{4})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2}).*"))
    }

}