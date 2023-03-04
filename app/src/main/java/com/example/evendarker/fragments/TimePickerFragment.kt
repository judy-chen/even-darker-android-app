package com.example.evendarker.fragments

import android.R
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.example.evendarker.SharedMemory
import com.example.evendarker.databinding.ActivityMainBinding
import java.util.Date


class TimePickerFragment(binding: ActivityMainBinding, layout: View, sharedMemory: SharedMemory) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    val binding: ActivityMainBinding = binding
    val layout: View = layout
    val sharedMemory: SharedMemory = sharedMemory

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)


        val df = SimpleDateFormat("h:mm aa")

        var date = Date()
        if(layout.id == binding.resumeLayout.id){
            date = df.parse(binding.resumeTime.text.toString())
        }
        if(layout.id == binding.pauseLayout.id){
            date = df.parse(binding.pauseTime.text.toString())
        }
        val minute = date.minutes
        val hour = date.hours

        var theme = if (isDarkTheme(activity)) R.style.Theme_Holo_Dialog_NoActionBar
        else R.style.Theme_Holo_Light_Dialog_NoActionBar

        val picker =
            TimePickerDialog(activity, theme,
                this, hour, minute, false
            )
        picker.window!!.setBackgroundDrawableResource(R.color.transparent)
        return picker
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val _12hour = SimpleDateFormat("h:mm aa")
        val _24hour = SimpleDateFormat("H:mm")
        val _24hourTime = _24hour.parse("$hourOfDay:"+ "$minute".padStart(2, '0'))
        val time = _12hour.format(_24hourTime).toString()
        if(layout.id == binding.resumeLayout.id){
            binding.resumeTime.text = time
            sharedMemory.setResumeTime(time)
        }
        if(layout.id == binding.pauseLayout.id){
            binding.pauseTime.text = time
            sharedMemory.setPauseTime(time)
        }
    }

    fun isDarkTheme(activity: FragmentActivity?): Boolean {
        return activity?.resources!!.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}