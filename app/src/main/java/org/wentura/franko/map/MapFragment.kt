package org.wentura.franko.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.*
import org.wentura.franko.R
import org.wentura.franko.data.ActivityRepository
import org.wentura.franko.data.Path
import org.wentura.franko.data.UserRepository
import org.wentura.franko.databinding.FragmentMapBinding
import org.wentura.franko.viewmodels.UserViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map),
    OnMapReadyCallback,
    AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var activityRepository: ActivityRepository

    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var map: GoogleMap
    private lateinit var spinner: Spinner
    private lateinit var speed: TextView
    private lateinit var polyline: Polyline
    private val polylinePoints: MutableList<LatLng> = mutableListOf()
    private var trackPosition: Boolean = false
    private var startTime = 0L
    private var initialOnItemSelected = true
    private val speedometer: Speedometer = Speedometer()

    private val userViewModel: UserViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()
    private val timerViewModel: TimerViewModel by viewModels()

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    private var fragmentMapBinding: FragmentMapBinding? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private val TAG = MapFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMapBinding.bind(view)
        fragmentMapBinding = binding

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        askForLocationPermission()

        locationViewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            speedometer.speed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                location.speedAccuracyMetersPerSecond.toDouble()
            } else {
                location.speed.toDouble()
            }

            speed.text = getString(
                if (speedometer.unitsOfMeasure == Constants.IMPERIAL) {
                    R.string.mph
                } else {
                    R.string.kmh
                },
                speedometer.speed.toInt()
            )

            val latLng = LatLng(location.latitude, location.longitude)

            if (this::map.isInitialized) {
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                map.moveCamera(CameraUpdateFactory.zoomTo(Constants.DEFAULT_ZOOM))
            }

            if (trackPosition) {
                polylinePoints.add(latLng)
                polyline.points = polylinePoints
            } else {
                stopTrackingLocation()
            }
        }

        timerViewModel.secondsElapsed.observe(viewLifecycleOwner) { time ->
            binding.mapTimer.apply {
                visibility = if (time.isEmpty()) View.INVISIBLE else View.VISIBLE
                text = time
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkLocationServicesState()

        val startStopButton: Button = binding.mapStartStop

        startStopButton.setOnClickListener {
            trackPosition = !trackPosition

            if (trackPosition) {
                startStopButton.text = getString(R.string.stop)
                startTrackingLocation()
            } else {
                startStopButton.text = getString(R.string.start)
                stopTrackingLocation()
            }
        }

        val resetButton: Button = binding.mapReset

        resetButton.setOnClickListener {
            polylinePoints.clear()
            polyline.points = polylinePoints
        }

        speed = binding.mapSpeed

        spinner = binding.mapActivitySpinner
        spinner.onItemSelectedListener = this

        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            val lastActivity = user.lastActivity
            val id = resources.getStringArray(R.array.activities_array).indexOf(lastActivity)

            spinner.setSelection(id)

            user.unitsOfMeasure?.let {
                speedometer.unitsOfMeasure = it
            }
        }
    }

    private fun checkLocationServicesState() {
        LocationServices
            .getSettingsClient(requireContext())
            .checkLocationSettings(LocationSettingsRequest.Builder().build())
            .addOnSuccessListener { response ->
                val locationSettingsStates = response.locationSettingsStates ?: return@addOnSuccessListener

                if (locationSettingsStates.isLocationUsable) return@addOnSuccessListener

                EnableLocationDialogFragment().show(
                    parentFragmentManager,
                    EnableLocationDialogFragment::class.simpleName
                )
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentMapBinding = null
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        if (!Util.isLocationPermissionGranted(context)) return

        map = googleMap
        map.isMyLocationEnabled = true

        fusedLocationClient
            .lastLocation
            .addOnSuccessListener { location ->
                if (location == null) return@addOnSuccessListener

                val latLng = LatLng(location.latitude, location.longitude)

                map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                map.moveCamera(CameraUpdateFactory.zoomTo(Constants.DEFAULT_ZOOM))
            }

        val polylineOptions = PolylineOptions()
            .width(Constants.LINE_WIDTH)
            .color(Constants.LINE_COLOR)

        polyline = map.addPolyline(polylineOptions)
    }

    private fun askForLocationPermission() {
        if (Util.isLocationPermissionGranted(context)) return

        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog
                .Builder(requireContext())
                .setTitle(getString(R.string.location_permissions_needed_dialog_title))
                .setMessage(getString(R.string.location_permissions_needed_dialog_message))
                .setPositiveButton(R.string.OK) { _, _ ->
                    requestLocationPermission()
                }
                .create()
                .show()
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )

            requestMultiplePermissions.launch(permissions)
            return
        }

        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Constants.PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    startTrackingLocation()
                } else {
                    Toast.makeText(requireContext(), "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    private fun startTrackingLocation() {
        if (!Util.isLocationPermissionGranted(context)) return

        context?.startService(Intent(context, LocationUpdatesService::class.java))
        timerViewModel.startTimer()

        polylinePoints.clear()
        startTime = System.currentTimeMillis()

        speed.visibility = View.VISIBLE
    }

    private fun stopTrackingLocation() {
        if (!Util.isLocationPermissionGranted(context)) return

        context?.stopService(Intent(context, LocationUpdatesService::class.java))

        timerViewModel.stopTimer()

        val array: MutableList<HashMap<String, Double>> = ArrayList()

        for (point in polylinePoints) {
            val element = hashMapOf(
                Constants.LATITUDE to point.latitude,
                Constants.LONGITUDE to point.longitude
            )

            array.add(element)
        }

        speed.visibility = View.INVISIBLE

        if (startTime == 0L) return

        if (System.currentTimeMillis() - startTime < Constants.MIN_ACTIVITY_TIME) {
            Toast.makeText(
                requireContext(),
                "Activities shorter than one minute aren’t recorded",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val activity = Path(
            TimeUnit.MILLISECONDS.toSeconds(startTime),
            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()),
            array,
            spinner.selectedItem.toString()
        )

        activityRepository.addActivity(activity)
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View?, pos: Int, id: Long) {
        if (initialOnItemSelected) {
            initialOnItemSelected = false
            return
        }

        val updates: HashMap<String, Any> =
            hashMapOf(Constants.LAST_ACTIVITY to adapterView.getItemAtPosition(pos))

        userRepository.updateUser(updates)
    }

    override fun onNothingSelected(parent: AdapterView<*>) = Unit
}
