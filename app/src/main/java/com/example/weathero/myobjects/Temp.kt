package com.example.weathero.myobjects

import kotlin.math.roundToInt

data class Temp(
    val day: Double? = null,
    val eve: Double? = null,
    val max: Double? = null,
    val min: Double? = null,
    val morn: Double? = null,
    val night: Double? = null
){
    val day2: String
        get() = day!!.roundToInt().toString()
    val night2: String
        get() = night!!.roundToInt().toString()
    val max2: String
        get() = max!!.roundToInt().toString()
    val min2: String
        get() = min!!.roundToInt().toString()
}
