package com.samsa.weatherapp.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
): java.io.Serializable

@Serializable
data class Coord(
    val lon: Double? = null,
    val lat: Double? = null
): java.io.Serializable

@Serializable
data class WeatherDescription(
    val id: Int? = null,
    val main: String? = null,
    val description: String? = null,
    val icon: String? = null
): java.io.Serializable

@Serializable
data class MainWeather(
    val temp: Double? = null,
    //@SerialName("feels_like")
    val feels_like: Double? = null,
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
): java.io.Serializable

@Serializable
data class Wind(
    val speed: Double? = null,
    val deg: Int? = null,
    val gust: Double? = null
): java.io.Serializable

@Serializable
data class Clouds(
    val all: Int? = null
): java.io.Serializable

@Serializable
data class Sys(
    val type: Int? = null,
    val id: Int? = null,
    val country: String? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null
): java.io.Serializable

// Данные списка (ForecastItem)
@Serializable
data class ForecastResponse(
    val cod: String? = null,
    val message: Int? = null,
    val cnt: Int? = null,
    val list: List<ForecastItem>? = null,
    val city: City? = null
): java.io.Serializable

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
    //@SerialName("dt_txt")
    val dt_txt: String? = null
): java.io.Serializable

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
): java.io.Serializable

@Serializable
data class Rain(
    @SerialName("3h")
    val threeH: Double? = null
): java.io.Serializable

@Serializable
data class SysForecast(
    val pod: String? = null
): java.io.Serializable

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
): java.io.Serializable

fun groupForecastItemsByDate(forecastItems: List<ForecastItem>?): Map<String, List<ForecastItem>> {
    if (forecastItems == null) {
        return emptyMap()
    }

    val groupedForecasts = mutableMapOf<String, MutableList<ForecastItem>>()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    for (item in forecastItems) {
        val dtTxt = item.dt_txt ?: continue // Skip if dtTxt is null
        try {
            val dateTime = LocalDateTime.parse(dtTxt, formatter)
            val date = dateTime.format(dateFormatter)

            if (groupedForecasts.containsKey(date)) {
                groupedForecasts[date]?.add(item)
            } else {
                groupedForecasts[date] = mutableListOf(item)
            }
        } catch (e: Exception) {
            // Handle parsing error (e.g., log the error)
            println("Error parsing date: ${item.dt_txt}. Skipping item.")
        }
    }

    return groupedForecasts
}

@Serializable
data class DayData(val date: String, val forecastItems: List<ForecastItem>)