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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.samsa.weatherapp.forecastdialog.ForecastDialogFragment
import com.samsa.weatherapp.utils.formatDate
import com.samsa.weatherapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var mService: RetrofitServices
    private lateinit var locationHelper: LocationHelper
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var rcAdapter: ForecastAdapter

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 20051306
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        rcAdapter = ForecastAdapter(this, supportFragmentManager)

        setContentView(binding.root)
        initBinding()

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        locationHelper = LocationHelper(this)
        mService = Common.retrofitService

        weatherViewModel.dayDataListLiveData.observe(this, Observer { fData ->
            if(fData != null) {
                rcAdapter.submitList(fData)
            }
            else {
                rcAdapter.submitList(emptyList())
            }
        })

        weatherViewModel.currentWeatherData.observe(this, Observer { wData ->
            if (wData != null) {
                binding.tvDescription.text = wData.weather?.get(0)?.description.toString()
                binding.tvTemp.text = "+${wData.main?.temp}℃"
                binding.tvFeelsLike.text = "+${wData.main?.feels_like}℃"
                binding.tvCity.text = wData.name.toString()
                binding.imageView2.setImageResource(
                    resources.getIdentifier(
                        "ic_${
                            wData.weather?.get(
                                0
                            )?.icon
                        }", "drawable", packageName
                    )
                )

                binding.tvWind.text = "${wData.wind?.speed}м/с"
                binding.tvDate.text = formatDate(Date())
                binding.tvHumidity.text = "${wData.main?.humidity}%"

                if ((Date().time % 86400) / 60 > 10 && (Date().time % 86400) / 60 < 20) {
                    binding.main.setBackgroundResource(
                        resources.getIdentifier(
                            "ic_day",
                            "drawable",
                            packageName
                        )
                    )
                } else {
                    binding.main.setBackgroundResource(
                        resources.getIdentifier(
                            "ic_night",
                            "drawable",
                            packageName
                        )
                    )
                }
            }
            else
                defaultInit()
        })

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
            }
        }
    }

    private fun getLocation() {
        coroutineScope.launch {
            locationHelper.getSingleLocation(
                onSuccess = { latitude, longitude ->
                    weatherViewModel.loadWeatherData(mService, latitude, longitude)
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
        binding.RecycleForecast.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        binding.RecycleForecast.adapter = rcAdapter
        defaultInit()
    }
}