package com.example.evendarker

import ShakeEventListener
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.evendarker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var sharedMemory: SharedMemory
    private lateinit var countDownTimer: CountDownTimer

    private val actionManageOverlayRequestCode = 1234;


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
            val seekbarList: List<SeekBar> =
                listOf(seekOpacity)

            for (item in seekbarList) {
                item.setOnSeekBarChangeListener(changeListener())
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

            shakeSwitch.setOnCheckedChangeListener { it, isChecked ->
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

    private fun changeListener(): SeekBar.OnSeekBarChangeListener{
        return object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                sharedMemory.setAlpha(binding.seekOpacity.progress)

                if(ScreenFilterService.STATE == ScreenFilterService.STATE_ACTIVE){
                    val i = Intent(this@MainActivity,ScreenFilterService:: class.java )
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
        startActivityForResult(intent, actionManageOverlayRequestCode);
    }

    fun createAlert(): AlertDialog? {

        val builder: AlertDialog.Builder? = this.let {
            AlertDialog.Builder(it)
        }

        builder?.setMessage(R.string.dialog_message)?.setTitle(R.string.dialog_title)

        builder?.apply {
            setPositiveButton(R.string.ok,
                DialogInterface.OnClickListener { dialog, id ->
                    // User clicked OK button
                    askDrawPermission()
                })
            setNegativeButton(R.string.cancel,
                DialogInterface.OnClickListener { dialog, id ->
                    // User cancelled the dialog
                })
        }

        val dialog: AlertDialog? = builder?.create()
        return dialog
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