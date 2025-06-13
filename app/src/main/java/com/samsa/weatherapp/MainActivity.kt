package com.samsa.weatherapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.samsa.weatherapp.common.Common
import com.samsa.weatherapp.databinding.ActivityMainBinding
import com.samsa.weatherapp.interfaces.RetrofitServices
import com.samsa.weatherapp.model.Weather
import com.samsa.weatherapp.model.WeatherData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.samsa.weatherapp.locationutils.LocationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import android.Manifest
import com.samsa.weatherapp.utils.formatDate
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var mService: RetrofitServices
    private lateinit var locationHelper: LocationHelper
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 20051306
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        initBinding()

        locationHelper = LocationHelper(this)
        mService = Common.retrofitService

        requestLocationAndGetLocation()
    }

    private fun requestLocationAndGetLocation() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            } else {
                Toast.makeText(this, "Разрешение на получение местоположения отклонено", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getLocation() {
        coroutineScope.launch {
            locationHelper.getSingleLocation(
                onSuccess = { latitude, longitude ->
                    val locationString = "Широта: $latitude, Долгота: $longitude"
                    Toast.makeText(this@MainActivity, "Местоположение получено! $locationString", Toast.LENGTH_LONG).show()

                    getWeatherA(latitude, longitude)
                },
                onFailure = { e ->
                    Toast.makeText(this@MainActivity, "Ошибка получения местоположения: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun getWeatherA(lat: Double, lon: Double) {
        mService.getWeather(lat, lon, "bea73937fb77f56dfd8f3f1717ccb31d", "metric", "ru").enqueue(object: Callback<WeatherData> {
            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                defaultInit()
                Log.d("ReqFail", "${t.message} || ${t.cause}")
            }

            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.body() != null) {
                    Log.d("Response", "${response.body()}")

                    binding.tvDescription.text = response.body()!!.weather?.get(0)?.description.toString()
                    binding.tvTemp.text = "+${response.body()?.main?.temp}℃"
                    binding.tvFeelsLike.text = "+${response.body()?.main?.temp}℃"
                    binding.tvCity.text = response.body()?.name.toString()
                    binding.imageView2.setImageResource(resources.getIdentifier("ic_${response.body()?.weather?.get(0)?.icon}", "drawable", packageName))

                    binding.tvWind.text = "${response.body()?.wind?.speed}м/с"
                    binding.tvDate.text = formatDate(Date())
                    binding.tvHumidity.text = "${response.body()?.main?.humidity}%"

                    if ((Date().time % 86400) / 60 > 10 && (Date().time % 86400) < 20) {
                        binding.main.setBackgroundResource(resources.getIdentifier("ic_day", "drawable", packageName))
                    }
                    else {
                        binding.main.setBackgroundResource(resources.getIdentifier("ic_night", "drawable", packageName))
                    }
                }
                else
                    defaultInit()
                //Log.d("ReqFailNot", "msg: ${response.body()?.toString()} || ${response.message()}")
            }
        })
    }

    private fun defaultInit() {
        binding.apply {
            tvDescription.text = "Данных нет"
            tvTemp.text = "N|A"
            tvFeelsLike.text = "N|A"
            tvCity.text = "N|A"

            tvWind.text = "N|A"
            tvDate.text = formatDate(Date())
            tvHumidity.text = "N|A"

            if ((Date().time % 86400) / 60 > 10 && (Date().time % 86400) < 20) {
                main.setBackgroundResource(resources.getIdentifier("ic_day", "drawable", packageName))
            }
            else {
                main.setBackgroundResource(resources.getIdentifier("ic_night", "drawable", packageName))
            }

            imageView2.setImageResource(resources.getIdentifier("ic_unknown", "drawable", packageName))
        }
    }

    private fun initBinding() {
        defaultInit()
    }
}