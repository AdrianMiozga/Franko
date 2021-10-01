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
import androidx.lifecycle.lifecycleScope
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

    @Inject
    lateinit var recordingRepository: RecordingRepository

    private lateinit var map: GoogleMap
    private lateinit var user: User
    private lateinit var spinner: Spinner
    private lateinit var polyline: Polyline
    private val polylinePoints: MutableList<LatLng> = mutableListOf()
    private var initialOnItemSelected = true
    private val speedometer: Speedometer = Speedometer()

    private val userViewModel: UserViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()
    private val timerViewModel: TimerViewModel by viewModels()

    private lateinit var locationObserver: LocationPermissionObserver

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        val TAG = MapFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMapBinding.bind(view)

        val speed = binding.mapSpeed

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        checkLocationPermission()

        lifecycleScope.launchWhenCreated {
            Utilities.checkLocationEnabled(requireContext(), parentFragmentManager)
        }

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

            if (this::polyline.isInitialized) {
                polylinePoints.add(latLng)
                polyline.points = polylinePoints
            }
        }

        val startButton: Button = binding.mapStart
        val stopButton: Button = binding.mapStop

        startButton.setOnClickListener { startTrackingLocation() }
        stopButton.setOnClickListener { stopTrackingLocation() }

        timerViewModel.recordingTime.observe(viewLifecycleOwner) { time ->
            if (time.isNotEmpty()) {
                startButton.visibility = View.INVISIBLE
                stopButton.visibility = View.VISIBLE

                speed.visibility = View.VISIBLE
            } else {
                startButton.visibility = View.VISIBLE
                stopButton.visibility = View.INVISIBLE

                speed.visibility = View.INVISIBLE
            }

            binding.mapTimer.apply {
                visibility = if (time.isEmpty()) View.INVISIBLE else View.VISIBLE
                text = time
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        if (!Utilities.isLocationPermissionGranted(requireContext())) return

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

    private fun checkLocationPermission() {
        if (Utilities.isLocationPermissionGranted(requireContext())) return

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
        if (!Utilities.isLocationPermissionGranted(requireContext())) return

        requireContext().startService(Intent(context, LocationUpdatesService::class.java))
    }

    private fun stopTrackingLocation() {
        if (!Utilities.isLocationPermissionGranted(requireContext())) return

        requireContext().stopService(Intent(context, LocationUpdatesService::class.java))

//        Navigation.findNavController(requireView())
//            .navigate(MapFragmentDirections.toActivitySaveFragment())

        val array: MutableList<HashMap<String, Double>> = ArrayList()

        for (point in polylinePoints) {
            val element = hashMapOf(
                Constants.LATITUDE to point.latitude,
                Constants.LONGITUDE to point.longitude
            )

            array.add(element)
        }

        polylinePoints.clear()
        polyline.points = polylinePoints

        val startTime = recordingRepository.startTime

        if (startTime == 0L) return

        val elapsedTime = recordingRepository.recordingTime.value ?: return

        if (elapsedTime < Constants.MIN_ACTIVITY_TIME) {
            Toast.makeText(
                requireContext(),
                getString(R.string.too_short_activity_to_save_toast),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val activity = Activity(
            uid,
            // TODO: 01.10.2021 Store in milliseconds?
            TimeUnit.MILLISECONDS.toSeconds(startTime),
            TimeUnit.MILLISECONDS.toSeconds(startTime + elapsedTime),
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
