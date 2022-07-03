package com.example.weathero.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addWeather(weather: Weather)

    @Update
    suspend fun updateWeather(weather: Weather)

    @Delete
    suspend fun deleteWeather(weather: Weather)

    @Query("DELETE FROM cities_table")
    suspend fun deleteAllWeather()

    @Query("SELECT * FROM cities_table ORDER BY time DESC")
    fun readAllData(): LiveData<List<Weather>>

    @Query("SELECT * FROM cities_table WHERE city_name LIKE :searchQuery ORDER BY time DESC")
    fun searchDatabase(searchQuery: String): LiveData<List<Weather>>



}