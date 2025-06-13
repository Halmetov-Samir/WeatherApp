package com.samsa.weatherapp.interfaces

import com.samsa.weatherapp.model.WeatherData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RetrofitServices {
    @GET("weather")
    fun getWeather(@Query("lat") latitude: Double,
                   @Query("lon") longitude: Double,
                   @Query("appid") apiKey: String,
                   @Query("units") unit: String,
                   @Query("lang") language: String) : Call<WeatherData>
}