package fr.devid.plantR.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class DateFormatUtils {
    @SuppressLint("SimpleDateFormat")
    fun setToDateFormat(timestamp: String, text : String?, text2 : String?): String {
        val date = Date(timestamp.toLong() *1000)
        val format = SimpleDateFormat("dd/MM/yyyy")
        val min = SimpleDateFormat("dd/MM/yyyy HH:mm")
        return "${text} ${format.format(date)} ${text2} ${min.format(date).substring(11)}"
    }

}