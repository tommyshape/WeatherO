package com.example.weathero

import android.util.Log
import com.example.weathero.database.Weather
import com.example.weathero.myobjects.Daily
import com.example.weathero.myobjects.Temp
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "current_valid_weather"

class CurrentValidWeather(val weather: Weather) {

    //var day: String
    var completeList: List<String>
    var listForHourly: MutableList<String> = mutableListOf()

    //aldigin sehrin hangi gunde oldugunu bul. altindaki gun kismi ona gore olustur.
    init {
        //For daily
        /*val epoch = weather.weatherMain!!.current.dt
        val local_epoch = epoch *//*+ weather.weatherMain!!.timezone_offset*//*
        val format_template = SimpleDateFormat("MM/dd/yyyy")
        format_template.timeZone = TimeZone.getTimeZone(weather.weatherMain!!.timezone)
        val dateToday = format_template.format(Date(local_epoch.toLong() * 1000))
        val date = format_template.parse(dateToday)

        val calendar = Calendar.getInstance()
        calendar.time = format_template.parse(dateToday) as Date
        val days = arrayListOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        //day = days[calendar.get(Calendar.DAY_OF_WEEK) - 1]
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        val first = days.subList(currentDay - 1, days.size)
        val second = days.subList(0, currentDay - 1)
        completeList = merge(first, second)

        Log.d(TAG, "complete list: $completeList")
        Log.d(TAG, "day of week index: $currentDay")
        //Log.d(TAG, "day of week: $day")
        Log.d(TAG, "dateToday: $dateToday")
        Log.d(TAG, "date: $date")*/


        val localEpoch = weather.weatherMain!!.current.dt
        val formatTemplate = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
        formatTemplate.timeZone = TimeZone.getTimeZone(weather.weatherMain!!.timezone)
        val dateToday = formatTemplate.format(Date(localEpoch.toLong() * 1000))
        val date1 = SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dateToday)
        val calendar = Calendar.getInstance()
        calendar.time = date1
        val days = arrayListOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        val day = days[calendar.get(Calendar.DAY_OF_WEEK) - 1]
        val first = days.subList(currentDay - 1, days.size)
        val second = days.subList(0, currentDay - 1)
        completeList = merge(first, second)












        //For hourly
        val hourly = weather.weatherMain!!.hourly
        val timeFormat = SimpleDateFormat("HH:mm")
        timeFormat.timeZone = TimeZone.getTimeZone(weather.weatherMain!!.timezone)
        for (i in 0..20 step 4) {
            val timeInEpoch = hourly[i].dt/* + weather.weatherMain!!.timezone_offset*/
            val hourlyTime = timeFormat.format(Date(timeInEpoch.toLong() * 1000))
            listForHourly.add(hourlyTime)
        }



    }



//0 1 2 3 4 5 6 7
    private fun time(): List<String> {
        val calendar = Calendar.getInstance()
        val list = arrayListOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val day = list[calendar.get(Calendar.DAY_OF_WEEK) - 1]
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)
        val first = list.subList(currentDay - 1, list.size)
        val second = list.subList(0, currentDay - 1)
        val completeList = merge(first, second)

        Log.d(TAG, "complete list: $completeList")
        Log.d(TAG, "day of week index: $currentDay")
        Log.d(TAG, "day of week: $day")

        return completeList
    }
    private fun <T> merge(first: List<T>, second: List<T>): List<T> {
        return first + second
    }


}