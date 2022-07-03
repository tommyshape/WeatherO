package com.example.weathero

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weathero.database.Weather
import com.example.weathero.myobjects.WeatherMain
import com.example.weathero.objects2.WeatherMainFirst
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "my_tag"

object Repository {

    lateinit var listener: MyInterface

    private val _currentWeather = MutableLiveData<WeatherMain>()

    private val _lastUpdateDate = MutableLiveData<String>()

    val currentWeather: LiveData<WeatherMain>
        get() = _currentWeather




    suspend fun mergeCallsInitialSearch(city: String){

        try {
            val result1 = callWeatherFirstType(city)
            val result2 = callWeatherSecondType(result1)
            //save result2 to database
            listener.clickToSave(result2)
        }catch (e: Exception){
            Log.d(TAG, "mergeCallsInitialSearch: $e")
        }

    }

    suspend fun requestWeatherByLatLon(lat: String, lon: String){
        val result1 = makeCallByLatLon(lat, lon)
        listener.clickToSave(result1)
    }

    suspend fun makeCallByLatLon(lat: String, lon: String): Response<WeatherMain>?{
        val response = try{
            RetrofitInstance.api.getWeatherSecondType(lat = lat, lon = lon)
        }catch (e: IOException){
            Log.d(TAG, "IOException, you might not have internet connection")
            return null
        }catch (e: HttpException){
            Log.d(TAG, "HttpException, unexpected result")
            return null
        }
        return if (response.isSuccessful && response.body() != null){
            response
        }else{
            Log.d(TAG, "Response not successful")
            null
        }
    }

    suspend fun updateWeather(list: List<Weather>) {

        val listOfResponses: MutableList<Response<WeatherMain>> = mutableListOf()
        list.forEach {
            val response = try{
                RetrofitInstance.api.getWeatherSecondType(lat = it.weatherMain!!.lat.toString(), lon = it.weatherMain!!.lon.toString())
            }catch (e: IOException){
                Log.d(TAG, "IOException, you might not have internet connection")
                return
            }catch (e: HttpException){
                Log.d(TAG, "HttpException, unexpected result")
                return
            }
            if (response.isSuccessful && response.body() != null){
                Log.d(TAG, "updateWeather: Response successful")
                listOfResponses.add(response)
            }else{
                Log.d(TAG, "Response not successful")
            }
        }
        listener.updateWeather(listOfResponses)
        //return listOfResponses
    }

    private suspend fun callWeatherSecondType(result1: Response<WeatherMainFirst>?): Response<WeatherMain>?{
        val response = try{
            RetrofitInstance.api.getWeatherSecondType(lat = result1?.body()?.coord?.lat.toString(), lon = result1?.body()?.coord?.lon.toString())
        }catch (e: IOException){
            Log.d(TAG, "IOException, you might not have internet connection")
            return null
        }catch (e: HttpException){
            Log.d(TAG, "HttpException, unexpected result")
            return null
        }
        return if (response.isSuccessful && response.body() != null){
            //toDoAdapter.todos = response.body()!!
            /*withContext(Dispatchers.Main){
                _currentWeather.value = response.body()!!
            }*/
            _currentWeather.value = response.body()!!
            response
        }else{
            Log.d(TAG, "Response not successful")
            null
        }

    }

    private suspend fun callWeatherFirstType(city: String): Response<WeatherMainFirst>?{
        val response = try{
            RetrofitInstance.api.getWeatherFirstType(city)
        }catch (e: IOException){
            Log.d(TAG, "IOException, you might not have internet connection")
            return null
        }catch (e: HttpException){
            Log.d(TAG, "HttpException, unexpected result")
            return null
        }
        return if (response.isSuccessful && response.body() != null){
            //toDoAdapter.todos = response.body()!!
            withContext(Dispatchers.Main){
                    //_currentWeatherFirst.value = response.body()!!
                Log.d(TAG, "callWeatherFirstType: ${response.body()?.sys?.country}")
                }
            response
        }else{
            Log.d(TAG, "Response not successful")
            null
        }
    }

    interface MyInterface{
        fun clickToSave(response: Response<WeatherMain>?)
        fun updateWeather(list: MutableList<Response<WeatherMain>>)
    }

    fun setMyInterface(listener: MyInterface){
        this.listener = listener
    }



}