package com.example.weathero.database

import android.os.Parcelable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weathero.myobjects.WeatherMain
import com.squareup.picasso.Picasso
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "cities_table")
data class Weather(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var weatherMain: @RawValue WeatherMain?,
    val city_name: String,
    var time: String
): Parcelable{
    /*companion object{
        @JvmStatic @BindingAdapter("android:yukle")
        fun setImageViewResourceQ(imageView: ImageView, resource: String) {
            //imageView.setImageResource(resource)
            Picasso.get().load(resource).into(imageView)
        }
    }*/
}