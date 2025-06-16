package com.samsa.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.samsa.weatherapp.interfaces.RetrofitServices
import com.samsa.weatherapp.model.ForecastItem
import com.samsa.weatherapp.model.ForecastResponse
import com.samsa.weatherapp.model.WeatherData
import com.samsa.weatherapp.utils.formatDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class WeatherViewModel : ViewModel() {

    val currentWeatherData = MutableLiveData<WeatherData?>() // Для одиночных данных
    val forecastListLiveData = MutableLiveData<List<ForecastItem>?>() // Для списка прогнозов

    fun loadWeatherData(mService: RetrofitServices, lat: Double, lon: Double) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                getWeatherA(mService, lat, lon)
                getForecastA(mService, lat, lon)
            }
        }
    }

    private fun getForecastA(mService: RetrofitServices, lat: Double, lon: Double) {
        mService.getForecast(lat, lon, "bea73937fb77f56dfd8f3f1717ccb31d", "metric", "ru").enqueue(object:
            Callback<ForecastResponse> {
            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                forecastListLiveData.value = emptyList()
                Log.d("ReqFail", "${t.message} || ${t.cause}")
            }

            override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                if (response.body() != null) {
                    Log.d("Response", "${response.body()}")

                    forecastListLiveData.value = response.body()?.copy()?.list
                }
                else
                    forecastListLiveData.value = emptyList()
            }
        })
    }

    private fun getWeatherA(mService: RetrofitServices, lat: Double, lon: Double) {
        mService.getWeather(lat, lon, "bea73937fb77f56dfd8f3f1717ccb31d", "metric", "ru").enqueue(object:
            Callback<WeatherData> {
            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                currentWeatherData.value = null
                Log.d("ReqFail", "${t.message} || ${t.cause}")
            }

            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.body() != null) {
                    Log.d("Response", "${response.body()}")

                    currentWeatherData.value = response.body()?.copy()
                }
                else
                    currentWeatherData.value = null
                //Log.d("ReqFailNot", "msg: ${response.body()?.toString()} || ${response.message()}")
            }
        })
    }
}