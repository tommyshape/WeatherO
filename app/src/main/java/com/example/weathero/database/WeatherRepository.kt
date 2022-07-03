package com.example.weathero.database

import androidx.lifecycle.LiveData

class WeatherRepository(val weatherDao: WeatherDao) {

    val readAllData: LiveData<List<Weather>> = weatherDao.readAllData()

    fun readData(): LiveData<List<Weather>>{
        return weatherDao.readAllData()
    }

    fun searchDatabase(searchQuery: String): LiveData<List<Weather>> {
        return weatherDao.searchDatabase(searchQuery)
    }

    suspend fun addWeather(weather: Weather){
        weatherDao.addWeather(weather)
    }
    suspend fun updateWeather(weather: Weather){
        weatherDao.updateWeather(weather)
    }
    suspend fun deleteWeather(weather: Weather){
        weatherDao.deleteWeather(weather)
    }
    suspend fun deleteAllWeather(){
        weatherDao.deleteAllWeather()
    }

    //---------


}