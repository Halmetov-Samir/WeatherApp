package com.samsa.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.samsa.weatherapp.interfaces.RetrofitServices
import com.samsa.weatherapp.model.DayData
import com.samsa.weatherapp.model.ForecastItem
import com.samsa.weatherapp.model.ForecastResponse
import com.samsa.weatherapp.model.WeatherData
import com.samsa.weatherapp.model.groupForecastItemsByDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel : ViewModel() {

    val dayDataListLiveData = MutableLiveData<List<DayData>>()
    val currentWeatherData = MutableLiveData<WeatherData?>()

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
                dayDataListLiveData.value = emptyList()
                Log.d("ReqFail", "${t.message} || ${t.cause}")
            }

            override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                if (response.body() != null) {
                    Log.d("Response", "${response.body()}")

                    response.body()?.copy()?.let {
                        dayDataListLiveData.value = convertMapToList(groupForecastItemsByDate(it.list))
                    }
                }
                else
                    dayDataListLiveData.value = emptyList()
            }
        })
    }

    private fun convertMapToList(groupedForecasts: Map<String, List<ForecastItem>>): List<DayData> {
        return groupedForecasts.map { (date, forecastItems) ->
            DayData(date, forecastItems)
        }
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
            }
        })
    }
}