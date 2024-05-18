package org.wentura.franko.ui.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordingRepository @Inject constructor() : LocationCallback() {

    companion object {
        val TAG = RecordingRepository::class.simpleName
    }

    /** Recording time in milliseconds * */
    var recordingTime = MutableLiveData<Long>()

    /** Milliseconds since epoch * */
    var startTime = 0L

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> = _currentLocation

    val points = MutableLiveData<List<LatLng>>()

    val locations = MutableLiveData<List<Location>>()

    override fun onLocationResult(locationResult: LocationResult) {
        val locationList = locationResult.locations

        if (locationList.isEmpty()) return

        val location = locationList.last()

        _currentLocation.value = location

        val tempPoints = points.value?.toMutableList() ?: ArrayList()
        tempPoints.add(LatLng(location.latitude, location.longitude))

        points.value = tempPoints

        val tempLocations = locations.value?.toMutableList() ?: ArrayList()
        tempLocations.add(location)

        locations.value = tempLocations
    }
}
