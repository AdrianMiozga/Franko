package org.wentura.franko.map

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask

class TimerViewModel : ViewModel() {

    companion object {
        val TAG = TimerViewModel::class.simpleName
    }

    private lateinit var timer: Timer
    private var initialTime = 0L

    private val _secondsElapsed = MutableLiveData<String>()
    val secondsElapsed: LiveData<String> = _secondsElapsed

    fun startTimer() {
        initialTime = SystemClock.elapsedRealtime()

        timer = Timer()
        timer.scheduleAtFixedRate(timerTask {
            val newValue = SystemClock.elapsedRealtime() - initialTime

            _secondsElapsed.postValue(
                SimpleDateFormat("mm:ss", Locale.US)
                    .format(newValue)
            )
        }, 0, 1000)
    }

    fun stopTimer() {
        _secondsElapsed.postValue("")
        timer.cancel()
    }

    override fun onCleared() {
        super.onCleared()

        if (this::timer.isInitialized) {
            timer.cancel()
        }
    }
}
