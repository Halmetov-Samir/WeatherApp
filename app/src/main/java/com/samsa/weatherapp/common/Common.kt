package com.samsa.weatherapp.common

import com.samsa.weatherapp.interfaces.RetrofitServices
import com.samsa.weatherapp.retrofit.RetClient

object Common {
    private const val BASE_URL = "http://api.openweathermap.org/data/2.5/"
    val retrofitService: RetrofitServices
        get() = RetClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}