package com.example.tabatatimer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.tabatatimer.Constants.ARROW
import com.example.tabatatimer.Constants.COUNTER
import com.example.tabatatimer.Constants.CYCLE
import com.example.tabatatimer.Constants.CYCLES
import com.example.tabatatimer.Constants.FINISHED
import com.example.tabatatimer.Constants.IMAGE
import com.example.tabatatimer.Constants.LEFT
import com.example.tabatatimer.Constants.MESSAGE
import com.example.tabatatimer.Constants.NEXT
import com.example.tabatatimer.Constants.PAUSE
import com.example.tabatatimer.Constants.PHASE
import com.example.tabatatimer.Constants.PLAY
import com.example.tabatatimer.Constants.PLAY_PAUSE
import com.example.tabatatimer.Constants.PREPARATION
import com.example.tabatatimer.Constants.PREV
import com.example.tabatatimer.Constants.REST
import com.example.tabatatimer.Constants.TICK
import com.example.tabatatimer.Constants.WORKOUT

class TimerService : Service() {

    private val TAG = "TimerService"
    private val bundle = Bundle()
    private lateinit var timer: CountDownTimer
    private var phase = 0
    private var cycle = 1
    private var counter = 1
    private var preparation = 1
    private var workout = 1
    private var rest = 1
    private var cycles = 1
    private var isRunning = false

    init {
        Log.d(TAG, "Service is running")
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val message = intent.getStringExtra(MESSAGE)
            if (message == null) {
                preparation = intent.getIntExtra(PREPARATION, 0)
                workout = intent.getIntExtra(WORKOUT, 1)
                rest = intent.getIntExtra(REST, 1)
                cycles = intent.getIntExtra(CYCLES, 1)
                counter = preparation
            }

            if (message == PLAY_PAUSE) {
                if (!isRunning) {
                    isRunning = true
                    startTimer(counter.toLong())
                    bundle.putString(IMAGE, PAUSE)
                }
                else {
                    isRunning = false
                    pauseTimer()
                    bundle.putString(IMAGE, PLAY)
                }
                bundle.putString(MESSAGE, PLAY_PAUSE)
                sendBroadcast(bundle)
                bundle.clear()
            }
            else if (message == NEXT) {
                if (phase < 2) {
                    if (isRunning) {
                        pauseTimer()
                        phase += 1
                        phaseTime(phase)
                        startTimer(counter.toLong())
                    }
                    else {
                        phase += 1
                        phaseTime(phase)
                        bundle.putString(MESSAGE, ARROW)
                        bundle.putInt(COUNTER, counter)
                        sendBroadcast(bundle)
                        bundle.clear()
                    }
                }
            }
            else if (message == PREV) {
                if (phase > 0) {
                    if (isRunning) {
                        pauseTimer()
                        phase -= 1
                        phaseTime(phase)
                        startTimer(counter.toLong())
                    }
                    else {
                        phase -= 1
                        phaseTime(phase)
                        bundle.putString(MESSAGE, ARROW)
                        bundle.putInt(COUNTER, counter)
                        sendBroadcast(bundle)
                        bundle.clear()
                    }
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun startTimer(time: Long) {
        timer = object : CountDownTimer(time * 1000, 1000) {
            override fun onFinish() {
                if (phase < 2) {
                    val mediaPlayer = MediaPlayer.create(this@TimerService, R.raw.ring)
                    mediaPlayer.start()

                    phase += 1
                    phaseTime(phase)
                    startTimer(counter.toLong())
                }
                else {
                    if (cycle < cycles) {
                        val mediaPlayer = MediaPlayer.create(this@TimerService, R.raw.ring)
                        mediaPlayer.start()

                        cycle += 1
                        bundle.putString(MESSAGE, CYCLES)
                        bundle.putInt(CYCLE, cycle)
                        sendBroadcast(bundle)
                        bundle.clear()
                        phase = 0
                        counter = preparation
                        startTimer(counter.toLong())
                    }
                    else {
                        val mediaPlayer = MediaPlayer.create(this@TimerService, R.raw.finish)
                        mediaPlayer.start()

                        isRunning = false
                        cycle = 1
                        phase = 0
                        counter = preparation
                        bundle.putString(MESSAGE, FINISHED)
                        sendBroadcast(bundle)
                        bundle.clear()
                    }
                }
            }

            override fun onTick(timeLeft: Long) {
                var left = (timeLeft / 1000) + 1
                if (left > counter) {
                    left -= 1
                }
                bundle.putString(MESSAGE, TICK)
                bundle.putLong(LEFT, left)
                sendBroadcast(bundle)
                bundle.clear()
                counter = left.toInt()
            }
        }
        timer.start()
    }

    private fun pauseTimer() {
        timer.cancel()
    }

    private fun phaseTime(phaseInt: Int) {
        when (phaseInt) {
            0 -> {
                bundle.putInt(PHASE, R.string.preparation)
                counter = preparation
            }
            1 -> {
                bundle.putInt(PHASE, R.string.workout)
                counter = workout
            }
            2 -> {
                bundle.putInt(PHASE, R.string.rest)
                counter = rest
            }
        }
        bundle.putString(MESSAGE, PHASE)
        sendBroadcast(bundle)
        bundle.clear()
    }

    private fun sendBroadcast(bundle: Bundle) {
        val intent = Intent()
        intent.action = MESSAGE
        intent.putExtras(bundle)
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "Service is stopped")

        super.onDestroy()
    }
}