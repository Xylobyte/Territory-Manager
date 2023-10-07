package fr.swiftapp.territorymanager.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

fun convertDate(jjmmaa: String): String {
    return try {
        val inputFormat = SimpleDateFormat("ddMMyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val date = inputFormat.parse(jjmmaa)
        outputFormat.format(date!!)
    } catch (e: ParseException) {
        e.printStackTrace()
        jjmmaa
    }
}

fun reverseDate(yyyyMmDd: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("ddMMyy", Locale.getDefault())

        val date = inputFormat.parse(yyyyMmDd)
        outputFormat.format(date!!)
    } catch (e: ParseException) {
        e.printStackTrace()
        yyyyMmDd
    }
}
