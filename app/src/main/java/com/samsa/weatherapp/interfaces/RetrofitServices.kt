package com.samsa.weatherapp.interfaces

import com.samsa.weatherapp.model.ForecastResponse
import com.samsa.weatherapp.model.WeatherData
import retrofit2.Call
import retrofit2.http.*

interface RetrofitServices {
    @GET("weather")
    fun getWeather(@Query("lat") latitude: Double,
                   @Query("lon") longitude: Double,
                   @Query("appid") apiKey: String,
                   @Query("units") unit: String,
                   @Query("lang") language: String) : Call<WeatherData>

    @GET("forecast")
    fun getForecast(@Query("lat") latitude: Double,
                   @Query("lon") longitude: Double,
                   @Query("appid") apiKey: String,
                   @Query("units") unit: String,
                   @Query("lang") language: String) : Call<ForecastResponse>
}