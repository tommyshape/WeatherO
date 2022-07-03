package com.example.weathero

import android.app.Application
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.example.weathero.database.Weather
import com.example.weathero.myobjects.WeatherMain
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application){

    companion object{
        @JvmStatic @BindingAdapter("android:yukle")
        fun setImageViewResourceQ(imageView: ImageView, resource: String) {
            //imageView.setImageResource(resource)
            Picasso.get().load("http://openweathermap.org/img/wn/${resource}@2x.png").into(imageView)
        }
    }

    val currentWeatherFirst: LiveData<WeatherMain>
        get() = Repository.currentWeather


    fun initialSearch(city: String){
        viewModelScope.launch {
            Repository.mergeCallsInitialSearch(city)
        }
    }

    fun updateWeather(list: List<Weather>){
        viewModelScope.launch {
            Repository.updateWeather(list)
        }
    }

    fun requestWeatherByLatLon(lat: String, lon: String){
        viewModelScope.launch {
            Repository.requestWeatherByLatLon(lat, lon)
        }
    }



}