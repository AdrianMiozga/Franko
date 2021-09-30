package org.wentura.franko.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
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
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.*
import org.wentura.franko.R
import org.wentura.franko.data.*
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
    private lateinit var user: User

    private val userViewModel: UserViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()
    private val timerViewModel: TimerViewModel by viewModels()

    private lateinit var locationObserver: LocationPermissionObserver

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private val TAG = MapFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMapBinding.bind(view)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        askForLocationPermission()
        checkLocationServicesState()

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
            this.user = user

            val lastActivity = user.lastActivity
            val id = resources.getStringArray(R.array.activities_array).indexOf(lastActivity)

            spinner.setSelection(id)

            user.unitsOfMeasure?.let {
                speedometer.unitsOfMeasure = it
            }

            if (user.keepScreenOnInMap) {
                activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        if (!Utilities.isLocationPermissionGranted(context)) return

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
        if (Utilities.isLocationPermissionGranted(context)) return

        locationObserver = LocationPermissionObserver(requireActivity().activityResultRegistry)
        lifecycle.addObserver(locationObserver)

        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            GrantLocationPermissionDialogFragment(locationObserver).show(
                parentFragmentManager,
                GrantLocationPermissionDialogFragment::class.simpleName
            )
        } else {
            locationObserver.requestLocationPermission()
        }
    }

    private fun startTrackingLocation() {
        if (!Utilities.isLocationPermissionGranted(context)) return

        context?.startService(Intent(context, LocationUpdatesService::class.java))
        timerViewModel.startTimer()

        polylinePoints.clear()
        startTime = System.currentTimeMillis()

        speed.visibility = View.VISIBLE
    }

    private fun stopTrackingLocation() {
        if (!Utilities.isLocationPermissionGranted(context)) return

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

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val activity = Activity(
            uid,
            TimeUnit.MILLISECONDS.toSeconds(startTime),
            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()),
            array,
            spinner.selectedItem.toString(),
            "",
            user.whoCanSeeActivityDefault
        )

        activityRepository.addActivity(activity)
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View?, position: Int, id: Long) {
        if (initialOnItemSelected) {
            initialOnItemSelected = false
            return
        }

        val updates: HashMap<String, Any> =
            hashMapOf(Constants.LAST_ACTIVITY to adapterView.getItemAtPosition(position))

        userRepository.updateUser(updates)
    }

    override fun onNothingSelected(parent: AdapterView<*>) = Unit
}
