package com.example.evendarker

import ShakeEventListener
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.evendarker.databinding.ActivityMainBinding
import com.example.evendarker.fragments.TimePickerFragment
import com.example.evendarker.services.ScreenFilterService

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var sharedMemory: SharedMemory
    private lateinit var countDownTimer: CountDownTimer

    private val actionManageOverlayRequestCode = 1234


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val view = binding.root

        sharedMemory = SharedMemory(this)
        countDownTimer = object: CountDownTimer(100,100){
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
            }
        }
        setContentView(view)
        setListeners()
        if(!isPermissionGranted()) createAlert()?.show()

    }

    private fun setListeners() {
        val i = Intent(this@MainActivity, ScreenFilterService::class.java)
        binding.apply {
            val viewList: List<View> =
                listOf(opacitySeek,temperatureSeek,resumeLayout,pauseLayout)


            for (item in viewList) {
                when(item){
                    is SeekBar -> item.setOnSeekBarChangeListener(seekBarListener())
                    is ConstraintLayout -> item.setOnClickListener(timeListener())
                }
            }

            toggleButton.setOnClickListener {

                if (ScreenFilterService.STATE == ScreenFilterService.STATE_ACTIVE) {
                    stopService(i)
                }

                else if(isPermissionGranted()){
                    startService(i)
                }
                else createAlert()?.show()
                refresh() // corrects toggle state
            }

            shakeSwitch.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked)
                    sharedMemory.setShake(1)
                else
                    sharedMemory.setShake(0)
            }


        }

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        ShakeEventListener(this,sensorManager){
            println("shake!")
            if(sharedMemory.getShake() == 1){
                stopService(i)
                refresh()
            }

        }
    }

    private fun timeListener(): View.OnClickListener? {
        return View.OnClickListener {
            TimePickerFragment(binding,it,sharedMemory).show(supportFragmentManager, "timePicker")
        }
    }

    private fun seekBarListener(): SeekBar.OnSeekBarChangeListener{
        return object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if(seekBar?.id == binding.opacitySeek.id)
                    sharedMemory.setAlpha(progress)

                if(seekBar?.id == binding.temperatureSeek.id)
                    sharedMemory.setTemperature(progress)

                if(ScreenFilterService.STATE == ScreenFilterService.STATE_ACTIVE){
                    val i = Intent(this@MainActivity, ScreenFilterService:: class.java )
                    startService(i)

                    binding.toggleButton.isChecked = (ScreenFilterService.STATE == ScreenFilterService.STATE_ACTIVE)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        }
    }

    private fun refresh(){
        countDownTimer.cancel()
        countDownTimer = object: CountDownTimer(100,100){
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                binding.toggleButton.isChecked = (ScreenFilterService.STATE == ScreenFilterService.STATE_ACTIVE)
            }

        }

        countDownTimer.start()
    }

    private fun isPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this))
            return true
        return false
    }

    private fun askDrawPermission(){
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, actionManageOverlayRequestCode)
    }

    private fun createAlert(): AlertDialog? {

        val builder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(it)
        }

        builder.setMessage(R.string.dialog_message)?.setTitle(R.string.dialog_title)

        builder.apply {
            setPositiveButton(
                R.string.ok
            ) { _, _ ->
                // User clicked OK button
                askDrawPermission()
            }
            setNegativeButton(
                R.string.cancel
            ) { _, _ ->
                // User cancelled the dialog
            }
        }

        return builder.create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == actionManageOverlayRequestCode) {
            // Check if the app get the permission
            if (Settings.canDrawOverlays(this)) {
                // Run your logic with newly-granted permission.
            } else {
                // Permission not granted. Change your logic accordingly.
                // App can re-request permission anytime.
            }
        }
    }
}