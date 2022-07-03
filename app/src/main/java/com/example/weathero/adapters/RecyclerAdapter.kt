package com.example.weathero.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weathero.database.Weather
import com.example.weathero.databinding.ListItemBinding

private const val TAG = "my_tag"

class RecyclerAdapter(val listX: List<Weather>) : RecyclerView.Adapter<RecyclerAdapter.WeatherViewHolder>(){

    lateinit var listener: MyInterfaceForList

    class WeatherViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    private val differCallBack  = object : DiffUtil.ItemCallback<Weather>() {

        override fun areItemsTheSame(oldItem: Weather, newItem: Weather): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Weather, newItem: Weather): Boolean {
            return oldItem == newItem
        }


    }

    val differ = AsyncListDiffer(this, differCallBack)
    var check = false
    val list2 = ArrayList<Weather>()
    fun moveItem(from: Int, to: Int) {

        val list = differ.currentList.toMutableList()

        Log.d(TAG, "onMove check: $check")
        if (!check){
            list.forEach {
                list2.add(it)
            }
            check = true
        }

        Log.d(TAG, "onMove size list2: ${list2.size}")

        val fromLocation = list2[from]
        list2.removeAt(from)

        list2.add(to , fromLocation)

        list2.forEachIndexed { index, string ->
            Log.d(TAG, "onMove: $index -> $string")
        }


        listener.clickToSave2(list2)


    }


    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        Log.d("my_adaptor", "onBindViewHolder: called")
        /*val item = listX[position]
        holder.binding.weather = item*/

        val item = differ.currentList[position]
        holder.binding.weather = item

    }

    override fun getItemCount(): Int {
        //return listX.size
        return differ.currentList.size
    }
    //---

    interface MyInterfaceForList{
        fun clickToSave2(list: MutableList<Weather>)
    }

    fun setMyInterfaceForList(listener: MyInterfaceForList){
        this.listener = listener
    }


}