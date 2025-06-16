package com.samsa.weatherapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(date: Date): String {
    val format = SimpleDateFormat("EEEE, dd MMMM, HH:mm", Locale("ru"))
    return format.format(date)
}

fun formatForecastDate(date: Date): String {
    val format = SimpleDateFormat("EEEE, dd MMMM", Locale("ru"))
    return format.format(date)
}

fun parseDateToTimestamp(dateString: String): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ru"))
    val date = dateFormat.parse(dateString)
    if (date != null)
        return date.time
    return 0
}

fun dayDataFormatDate(dateString: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ru"))
    val date = dateFormat.parse(dateString)
    return formatForecastDate(date!!)
}

fun formatTime(dateString: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ru"))
    val date = dateFormat.parse(dateString)
    return formatForecastDateToTime(date!!)
}

fun formatForecastDateToTime(date: Date): String {
    val format = SimpleDateFormat("HH:mm", Locale("ru"))
    return format.format(date)
}