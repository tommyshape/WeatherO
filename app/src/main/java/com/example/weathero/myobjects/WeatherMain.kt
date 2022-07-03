package com.example.weathero.myobjects

data class WeatherMain(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Float,
    val lon: Float,
    val minutely: List<Minutely>,
    val timezone: String,
    val timezone_offset: Int
)