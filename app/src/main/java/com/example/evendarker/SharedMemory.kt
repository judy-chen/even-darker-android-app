package com.example.evendarker

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color

class SharedMemory(context: Context) {
    private var sharedPref: SharedPreferences = context.getSharedPreferences("SCREEN_FILTER", Context.MODE_PRIVATE)

    private fun getValue(key:String, value:Int): Int{
        return sharedPref.getInt(key, value)
    }

    private fun setValue(key:String, value:Int){
        return sharedPref.edit().putInt(key,value).apply()
    }

    fun getAlpha(): Int{
        return getValue("alpha",0x33)
    }

    fun getShake(): Int{
        return getValue("shake",0)
    }

    fun setAlpha(value: Int){
        return setValue("alpha",value)
    }

    fun setShake(value: Int){
        return setValue("shake",value)
    }

    fun getColor(): Int {
        return Color.argb(getAlpha(),0,0,0)
    }
}