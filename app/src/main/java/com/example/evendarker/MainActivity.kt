package com.example.evendarker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.SeekBar
import com.example.evendarker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var sharedMemory: SharedMemory
    private lateinit var countDownTimer: CountDownTimer

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
    }

    private fun setListeners() {

        binding.apply {
            val seekbarList: List<SeekBar> =
                listOf(seekOpacity)

            for (item in seekbarList) {
                item.setOnSeekBarChangeListener(changeListener())
            }

            toggleButton.setOnClickListener {
                val i = Intent(this@MainActivity, ScreenFilterService::class.java)
                if (ScreenFilterService.STATE == ScreenFilterService.STATE_ACTIVE) {
                    stopService(i)
                }

                else {
                    startService(i)
                }
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
//        countDownTimer.cancel()
//        countDownTimer = object: CountDownTimer(100,100){
//            override fun onTick(millisUntilFinished: Long) {
//
//            }
//
//            override fun onFinish() {
//                binding.toggleButton.isChecked = (ScreenFilterService.STATE == ScreenFilterService.STATE_ACTIVE)
//            }
//
//        }
//
//        countDownTimer.start()
    }
}