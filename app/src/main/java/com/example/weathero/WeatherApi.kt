package com.example.weathero

import com.example.weathero.myobjects.WeatherMain
import com.example.weathero.objects2.WeatherMainFirst
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApi {

    @GET("/data/2.5/weather?appid=${Anahtar.anahtar}")
    suspend fun getWeatherFirstType(@Query("q") city: String): Response<WeatherMainFirst>

    @GET("/data/2.5/onecall?units=metric&appid=${Anahtar.anahtar}")
    suspend fun getWeatherSecondType(
        @Query("lat") lat: String,
        @Query("lon") lon: String
    ): Response<WeatherMain>


}