package com.example.evendarker.services

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.LinearLayout
import com.example.evendarker.SharedMemory

class ScreenFilterService: Service (){

    private lateinit var sharedMemory: SharedMemory
    private lateinit var screenFilter: View

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        sharedMemory = SharedMemory(this)
        screenFilter = LinearLayout(this)
        screenFilter.setBackgroundColor(sharedMemory.getColor())


        val version =  if (Build.VERSION.SDK_INT >= 26)
            LayoutParams.TYPE_APPLICATION_OVERLAY
        else LayoutParams.TYPE_SYSTEM_ALERT

        val windowManager:WindowManager = (getSystemService(WINDOW_SERVICE) as WindowManager?)!!
        val dm = DisplayMetrics()

        windowManager.defaultDisplay.getRealMetrics(dm)
        val height = dm.heightPixels + getNavBarHeight()


        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            height, 0,0,
            version,
            LayoutParams.FLAG_NOT_TOUCHABLE
                    or LayoutParams.FLAG_NOT_FOCUSABLE
                    or LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    or LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        )

        windowManager.addView(screenFilter,layoutParams)

        STATE = STATE_ACTIVE
    }

    override fun onDestroy() {
        super.onDestroy()
        val windowManager:WindowManager = (getSystemService(WINDOW_SERVICE) as WindowManager?)!!
        windowManager.removeView(screenFilter)
        STATE = STATE_INACTIVE
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        screenFilter.setBackgroundColor(sharedMemory.getColor())
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        val STATE_ACTIVE: Int = 1
        val STATE_INACTIVE: Int = 0
        var STATE: Int = STATE_INACTIVE

    }

    private fun getNavBarHeight (): Int{
        var result: Int = 0
        val resourceId = resources.getIdentifier("navigation_bar_height","dimen","android")
        if(resourceId > 0)
            return resources.getDimensionPixelSize(resourceId)
        return result
    }
}