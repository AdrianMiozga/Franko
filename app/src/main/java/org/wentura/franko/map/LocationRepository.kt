package org.wentura.franko.map

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

object LocationRepository : LocationCallback() {

    val TAG = LocationRepository::class.simpleName

    val currentLocation = MutableLiveData<Location>()

    override fun onLocationResult(locationResult: LocationResult) {
        val locationList = locationResult.locations

        if (locationList.isEmpty()) return

        val location = locationList.last()

        currentLocation.value = location
    }
}
