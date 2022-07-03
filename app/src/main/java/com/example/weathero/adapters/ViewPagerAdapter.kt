package com.example.weathero.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weathero.CurrentValidWeather
import com.example.weathero.database.Weather
import com.example.weathero.databinding.Item2ViewpagerBinding
import com.example.weathero.databinding.ItemViewPagerBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


private const val TAG = "my_tag"

class ViewPagerAdapter(val list: List<Weather>) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    private val dateToday: String
    init {
        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()
        Log.d(TAG, "Saat epoch -> $ts")

        dateToday = SimpleDateFormat("MM/dd/yyyy").format(Date(tsLong * 1000))
        Log.d(TAG, "Saat insan -> $dateToday")
    }

    private val dayOfWeek = time()

    inner class ViewPagerViewHolder(var binding: Item2ViewpagerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        return ViewPagerViewHolder(Item2ViewpagerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val item = list[position]
        val cvw = CurrentValidWeather(item)

        val nameOfDayList = cvw.completeList
        val listForHourly = cvw.listForHourly

        holder.binding.daysofweek = ViewPagerListDay(nameOfDayList)
        holder.binding.listForHourly = ListForHourly(listForHourly)
        holder.binding.weather = item

        //For wind degree and speed
        val weatherItem = item.weatherMain!!.current
        holder.binding.compassNeedle2.rotation = weatherItem.wind_deg.toFloat()
        val speed = weatherItem.wind_speed.roundToInt().toString()
        holder.binding.windSpeedTv.text = speed




    }

    override fun getItemCount(): Int {
        return list.size
    }
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