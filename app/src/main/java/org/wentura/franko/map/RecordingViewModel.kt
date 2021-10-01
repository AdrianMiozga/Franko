package org.wentura.franko.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RecordingViewModel @Inject constructor(
    recordingRepository: RecordingRepository
) : ViewModel() {

    companion object {
        val TAG = RecordingViewModel::class.simpleName
    }

    private val _location = MediatorLiveData<Location>()
    val location: LiveData<Location> = _location

    private val _points = MediatorLiveData<ArrayList<LatLng>>()
    val points: LiveData<ArrayList<LatLng>> = _points

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

        _location.addSource(recordingRepository.currentLocation) { result ->
            _location.value = result
        }

        _points.addSource(recordingRepository.points) { result ->
            _points.value = result
        }
    }
}
