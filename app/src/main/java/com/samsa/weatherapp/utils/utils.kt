package com.samsa.weatherapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(date: Date): String {
    val format = SimpleDateFormat("EEEE, dd MMMM", Locale("ru"))
    return format.format(date)
}