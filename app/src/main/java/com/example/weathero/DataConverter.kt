package com.example.weathero

import androidx.room.TypeConverter
import com.example.weathero.myobjects.WeatherMain
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type



class DataConverter {

    @TypeConverter
    fun fromWeatherMain(weatherMain: WeatherMain?): String? {
        if (weatherMain == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<WeatherMain?>() {}.type
        return gson.toJson(weatherMain, type)
    }

    @TypeConverter
    fun toWeatherMain(weatherMainString: String?): WeatherMain? {
        if (weatherMainString == null) {
            return null
        }
        val gson = Gson()
        val type =
            object : TypeToken<WeatherMain?>() {}.type
        return gson.fromJson<WeatherMain>(weatherMainString, type)
    }


}
