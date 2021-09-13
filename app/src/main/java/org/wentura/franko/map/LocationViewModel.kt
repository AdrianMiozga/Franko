package org.wentura.franko.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {

    companion object {
        val TAG = LocationViewModel::class.simpleName
    }

    private val _currentLocation = MediatorLiveData<Location>()
    val currentLocation: LiveData<Location> = _currentLocation

    init {
        _currentLocation.addSource(LocationRepository.currentLocation) { result ->
            _currentLocation.value = result
        }
    }
}
