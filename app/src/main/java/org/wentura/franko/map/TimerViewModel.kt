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
    recordingRepository: RecordingRepository
) : ViewModel() {

    companion object {
        val TAG = TimerViewModel::class.simpleName
    }

    private val _recordingTime = MediatorLiveData<String>()
    val recordingTime: LiveData<String> = _recordingTime

    init {
        _recordingTime.addSource(recordingRepository.recordingTime) {
            if (it == 0L) {
                _recordingTime.value = ""
                return@addSource
            }

            _recordingTime.value =
                SimpleDateFormat("mm:ss", Locale.US).format(it)
        }
    }
}
