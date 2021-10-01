package org.wentura.franko.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    recordingRepository: RecordingRepository
) : ViewModel() {

    companion object {
        val TAG = LocationViewModel::class.simpleName
    }

    private val _currentLocation = MediatorLiveData<Location>()
    val currentLocation: LiveData<Location> = _currentLocation

    init {
        _currentLocation.addSource(recordingRepository.currentLocation) { result ->
            _currentLocation.value = result
        }
    }
}
