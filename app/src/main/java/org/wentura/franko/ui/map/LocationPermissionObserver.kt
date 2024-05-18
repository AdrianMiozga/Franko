package org.wentura.franko.ui.map

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.wentura.franko.Constants

class LocationPermissionObserver(
    private val registry: ActivityResultRegistry
) : DefaultLifecycleObserver {

    private lateinit var requestPermissions: ActivityResultLauncher<Array<String>>

    override fun onCreate(owner: LifecycleOwner) {
        requestPermissions = registry.register(
            Constants.REQUEST_PERMISSIONS_KEY,
            owner,
            ActivityResultContracts.RequestMultiplePermissions()
        ) {}
    }

    fun requestLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        requestPermissions.launch(permissions)
    }
}
