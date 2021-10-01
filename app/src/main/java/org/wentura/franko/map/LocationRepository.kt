package org.wentura.franko.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor() : LocationCallback() {

    companion object {
        val TAG = LocationRepository::class.simpleName
    }

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> = _currentLocation

    override fun onLocationResult(locationResult: LocationResult) {
        val locationList = locationResult.locations

        if (locationList.isEmpty()) return

        val location = locationList.last()

        _currentLocation.value = location
    }
}
