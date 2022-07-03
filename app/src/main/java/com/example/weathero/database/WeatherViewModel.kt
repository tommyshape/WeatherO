package com.example.weathero.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Weather>>
    private val repository: WeatherRepository

    //for fragment_add.xml
    var editTextTitle = MutableLiveData<String>()
    var editTextBody = MutableLiveData<String>()

    init {
        val noteDao: WeatherDao = WeatherDatabase.getDatabase(application).weatherDao()
        repository = WeatherRepository(noteDao)
        readAllData = repository.readAllData


    }

    fun readData(): LiveData<List<Weather>>{
        return repository.readData()
    }



    fun addWeather(weather: Weather){
        viewModelScope.launch(Dispatchers.IO){
            repository.addWeather(weather)
        }
    }
    fun updateWeather(weather: Weather){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateWeather(weather)
        }
    }
    fun deleteWeather(weather: Weather){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteWeather(weather)
        }
    }
    fun deleteAllWeather(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteAllWeather()
        }
    }
    fun searchDatabase(searchQuery: String): LiveData<List<Weather>> {
        return repository.searchDatabase(searchQuery)
    }


}