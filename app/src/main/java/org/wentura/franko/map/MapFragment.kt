package org.wentura.franko.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.*
import org.wentura.franko.R
import org.wentura.franko.Utilities.createPolylineOptions
import org.wentura.franko.Utilities.setupMap
import org.wentura.franko.data.*
import org.wentura.franko.databinding.FragmentMapBinding
import org.wentura.franko.viewmodels.UserViewModel
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

    private var initialOnItemSelected = true
    private val speedometer: Speedometer = Speedometer()

    private val userViewModel: UserViewModel by viewModels()
    private val recordingViewModel: RecordingViewModel by viewModels()

    private lateinit var locationObserver: LocationPermissionObserver

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var recordingService: RecordingService
    private var isRecordingServiceBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as RecordingService.LocalBinder
            recordingService = binder.getService()
            isRecordingServiceBound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            isRecordingServiceBound = false
        }
    }

    companion object {
        val TAG = MapFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentMapBinding.bind(view)

        val speed = binding.mapSpeed

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        checkLocationPermission()

        lifecycleScope.launchWhenCreated {
            Utilities.checkLocationEnabled(requireContext(), parentFragmentManager)
        }

        recordingViewModel.location.observe(viewLifecycleOwner) { location ->
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
        }

        val startButton: Button = binding.mapStart
        val stopButton: Button = binding.mapStop

        startButton.setOnClickListener { startTrackingLocation() }
        stopButton.setOnClickListener { stopTrackingLocation() }

        recordingViewModel.recordingTime.observe(viewLifecycleOwner) { time ->
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

        val mapFragment =
            childFragmentManager
                .findFragmentById(R.id.map_map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        val spinner = binding.mapActivitySpinner
        spinner.onItemSelectedListener = this

        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
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

    override fun onStart() {
        super.onStart()

        Intent(requireContext(), RecordingService::class.java).also { intent ->
            requireContext().bindService(intent, connection, 0)
        }
    }

    override fun onStop() {
        super.onStop()

        if (isRecordingServiceBound) {
            requireContext().unbindService(connection)
            isRecordingServiceBound = false
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        if (!Utilities.isLocationPermissionGranted(requireContext())) return

        googleMap.clear()
        googleMap.isMyLocationEnabled = true

        setupMap(googleMap, requireContext())

        fusedLocationClient
            .lastLocation
            .addOnSuccessListener { location ->
                if (location == null) return@addOnSuccessListener

                val latLng = LatLng(location.latitude, location.longitude)

                val newLatLngZoom =
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        Constants.DEFAULT_ZOOM
                    )

                googleMap.moveCamera(newLatLngZoom)
            }

        val polyline = googleMap.addPolyline(createPolylineOptions())

        recordingViewModel.points.observe(viewLifecycleOwner) { points ->
            polyline.points = points
        }
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

        val intent = Intent(context, RecordingService::class.java)
        requireContext().startService(intent)

        Intent(requireContext(), RecordingService::class.java).also {
            requireContext().bindService(it, connection, 0)
        }
    }

    private fun stopTrackingLocation() {
        if (!Utilities.isLocationPermissionGranted(requireContext())) return

        recordingService.stopUpdates()

        val elapsedTime = recordingRepository.recordingTime.value

        if (elapsedTime == null) {
            stopRecordingService()
            return
        }

//        if (elapsedTime < Constants.MIN_ACTIVITY_TIME) {
//            Toast.makeText(
//                context,
//                getString(R.string.too_short_activity_to_save_toast),
//                Toast.LENGTH_LONG
//            ).show()
//
//            stopRecordingService()
//            return
//        }

        Navigation.findNavController(requireView())
            .navigate(MapFragmentDirections.toActivitySaveFragment())
    }

    private fun stopRecordingService() {
        if (isRecordingServiceBound) {
            requireContext().unbindService(connection)
            isRecordingServiceBound = false
        }

        val intent = Intent(context, RecordingService::class.java)
        requireContext().stopService(intent)
    }

    override fun onItemSelected(
        adapterView: AdapterView<*>,
        view: View?,
        position: Int,
        id: Long
    ) {
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
