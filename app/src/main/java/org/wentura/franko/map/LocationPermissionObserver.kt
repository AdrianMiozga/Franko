package org.wentura.franko.map

import android.Manifest
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.wentura.franko.Constants

class LocationPermissionObserver(
    private val registry: ActivityResultRegistry
) : DefaultLifecycleObserver {

    private lateinit var requestPermission: ActivityResultLauncher<String>
    private lateinit var requestPermissions: ActivityResultLauncher<Array<String>>

    override fun onCreate(owner: LifecycleOwner) {
        requestPermission = registry.register(
            Constants.REQUEST_PERMISSION_KEY,
            owner,
            ActivityResultContracts.RequestPermission()
        ) {}

        requestPermissions = registry.register(
            Constants.REQUEST_PERMISSIONS_KEY,
            owner,
            ActivityResultContracts.RequestMultiplePermissions()
        ) {}
    }

    fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )

            requestPermissions.launch(permissions)
            return
        }

        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
