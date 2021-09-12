package org.wentura.franko.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

class LocationViewModel : ViewModel() {

    companion object {
        val TAG = LocationViewModel::class.simpleName
    }

    private var _currentLocation = MutableLiveData<String>()
    val currentLocation: LiveData<String> = _currentLocation

    init {
        _currentLocation.value = "Hell yeah"
    }

    var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations

            if (locationList.isEmpty()) return

            // The last location in the list is the newest
            val location = locationList.last()

            val latitude = location.latitude
            val longitude = location.longitude

            val result = "$latitude, $longitude"

            _currentLocation.value = result

            Log.d(TAG, "onLocationResult: ${currentLocation.value}")
        }
    }
}
