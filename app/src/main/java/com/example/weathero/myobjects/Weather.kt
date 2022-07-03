package com.example.weathero.myobjects


data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
){
    val description2: String
        get() = description.toLowerCase().capitalize()
}