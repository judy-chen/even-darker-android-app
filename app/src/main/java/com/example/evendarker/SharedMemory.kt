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

    private fun getString(key:String, value:String): String?{
        return sharedPref.getString(key, value)
    }
    private fun setString(key:String, value:String){
        return sharedPref.edit().putString(key, value).apply()
    }

    fun getAlpha(): Int{
        return getValue("alpha",0x33)
    }

    fun getRed(): Int{
        return getValue("red",0)
    }
    fun getGreen(): Int{
        return getValue("green",0)
    }

    fun getBlue(): Int{
        return getValue("blue",0)
    }

    fun getShake(): Int{
        return getValue("shake",0)
    }

    fun setRed(value: Int){
        setValue("red",value)
    }
    fun setGreen(value: Int){
        setValue("green",value)
    }

    fun setBlue(value: Int){
        setValue("blue",value)
    }

    fun setAlpha(value: Int){
        setValue("alpha",value)
    }

    fun setShake(value: Int){
        setValue("shake",value)
    }

    fun getColor(): Int {
        return Color.argb(getAlpha(),getRed(),getGreen(),getBlue())
    }

    fun setTemperature(value: Int) {
        val green =  0.42 * value
        setRed(value)
        setGreen(green.toInt())
    }

    fun getResumeTime(value: String): String? {
        return getString("resume_time",value)
    }

    fun getPauseTime(value: String): String? {
        return getString("pause_time",value)
    }
    fun setResumeTime(value: String) {
        setString("resume_time",value)
    }

    fun setPauseTime(value: String) {
        setString("pause_time",value)
    }
}