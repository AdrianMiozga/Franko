package org.wentura.franko.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    recordingRepository: RecordingRepository
) : ViewModel() {

    companion object {
        val TAG = LocationViewModel::class.simpleName
    }

    private val _location = MediatorLiveData<Location>()
    val location: LiveData<Location> = _location

    private val _points = MediatorLiveData<ArrayList<LatLng>>()
    val points: LiveData<ArrayList<LatLng>> = _points

    init {
        _location.addSource(recordingRepository.currentLocation) { result ->
            _location.value = result
        }

        _points.addSource(recordingRepository.points) { result ->
            _points.value = result
        }
    }
}
