package com.samsa.weatherapp.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

// Одиночные данные (WeatherData)
@Serializable
data class WeatherData(
    val coord: Coord? = null,
    val weather: List<WeatherDescription>? = null,
    val base: String? = null,
    val main: MainWeather? = null,
    val visibility: Int? = null,
    val wind: Wind? = null,
    val clouds: Clouds? = null,
    val dt: Long? = null,
    val sys: Sys? = null,
    val timezone: Int? = null,
    val id: Int? = null,
    val name: String? = null,
    val cod: Int? = null
)

@Serializable
data class Coord(
    val lon: Double? = null,
    val lat: Double? = null
)

@Serializable
data class WeatherDescription(
    val id: Int? = null,
    val main: String? = null,
    val description: String? = null,
    val icon: String? = null
)

@Serializable
data class MainWeather(
    val temp: Double? = null,
    @SerialName("feels_like")
    val feelsLike: Double? = null,
    @SerialName("temp_min")
    val tempMin: Double? = null,
    @SerialName("temp_max")
    val tempMax: Double? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
    @SerialName("sea_level")
    val seaLevel: Int? = null,
    @SerialName("grnd_level")
    val grndLevel: Int? = null
)

@Serializable
data class Wind(
    val speed: Double? = null,
    val deg: Int? = null,
    val gust: Double? = null
)

@Serializable
data class Clouds(
    val all: Int? = null
)

@Serializable
data class Sys(
    val type: Int? = null,
    val id: Int? = null,
    val country: String? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null
)

// Данные списка (ForecastItem)
@Serializable
data class ForecastResponse(
    val cod: String? = null,
    val message: Int? = null,
    val cnt: Int? = null,
    val list: List<ForecastItem>? = null,
    val city: City? = null
)

@Serializable
data class ForecastItem(
    val dt: Long? = null,
    val main: MainForecast? = null,
    val weather: List<WeatherDescription>? = null, // Используем WeatherDescription
    val clouds: Clouds? = null,
    val wind: Wind? = null,
    val visibility: Int? = null,
    val pop: Double? = null,
    val rain: Rain? = null,
    val sys: SysForecast? = null,
    @SerialName("dt_txt")
    val dtTxt: String? = null
)

@Serializable
data class MainForecast(
    val temp: Double? = null,
    @SerialName("feels_like")
    val feelsLike: Double? = null,
    @SerialName("temp_min")
    val tempMin: Double? = null,
    @SerialName("temp_max")
    val tempMax: Double? = null,
    val pressure: Int? = null,
    @SerialName("sea_level")
    val seaLevel: Int? = null,
    @SerialName("grnd_level")
    val grndLevel: Int? = null,
    val humidity: Int? = null,
    @SerialName("temp_kf")
    val tempKf: Double? = null
)

@Serializable
data class Rain(
    @SerialName("3h")
    val threeH: Double? = null
)

@Serializable
data class SysForecast(
    val pod: String? = null
)

@Serializable
data class City(
    val id: Int? = null,
    val name: String? = null,
    val coord: Coord? = null,
    val country: String? = null,
    val population: Int? = null,
    val timezone: Int? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null
)