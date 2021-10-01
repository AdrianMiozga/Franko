package org.wentura.franko.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    elapsedTimeRepository: ElapsedTimeRepository
) : ViewModel() {

    companion object {
        val TAG = TimerViewModel::class.simpleName
    }

    private val _elapsedTime = MediatorLiveData<String>()
    val elapsedTime: LiveData<String> = _elapsedTime

    init {
        _elapsedTime.addSource(elapsedTimeRepository.elapsedTime) {
            if (it == 0L) {
                _elapsedTime.value = ""
                return@addSource
            }

            _elapsedTime.value =
                SimpleDateFormat("mm:ss", Locale.US).format(it)
        }
    }
}
