package org.wentura.franko.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import org.wentura.franko.*
import org.wentura.franko.R
import org.wentura.franko.data.Path
import org.wentura.franko.data.User
import org.wentura.franko.databinding.FragmentMapBinding

class MapFragment : Fragment(R.layout.fragment_map),
    OnMapReadyCallback,
    AdapterView.OnItemSelectedListener {

    private lateinit var myMap: GoogleMap
    private lateinit var spinner: Spinner
    private lateinit var speed: TextView
    private lateinit var polyline: Polyline
    private val polylinePoints: MutableList<LatLng> = mutableListOf()
    private var trackPosition: Boolean = false
    private var startTime = 0L
    private var initialOnItemSelected = true
    private val speedometer: Speedometer = Speedometer()

    private val locationViewModel: LocationViewModel by viewModels()
    private val timerViewModel: TimerViewModel by viewModels()

    private val db = Firebase.firestore

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

        checkLocationPermission()

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

            if (this::myMap.isInitialized) {
                myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                myMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.DEFAULT_ZOOM))
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

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.activities_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            db.collection(Constants.USERS)
                .document(uid)
                .get()
                .addOnSuccessListener { result ->
                    val lastActivity = result.toObject<User>()?.lastActivity
                    spinner.setSelection(adapter.getPosition(lastActivity))
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

    override fun onResume() {
        super.onResume()

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        db.collection(Constants.USERS)
            .document(uid)
            .get()
            .addOnSuccessListener { result ->
                val user: User? = result.toObject()

                user?.unitsOfMeasure?.let {
                    speedometer.unitsOfMeasure = it
                }
            }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        myMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location == null) return@addOnSuccessListener

                val latLng = LatLng(location.latitude, location.longitude)

                myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                myMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.DEFAULT_ZOOM))
            }

        val color = PolylineOptions()
            .width(Constants.LINE_WIDTH)
            .color(Constants.LINE_COLOR)

        polyline = myMap.addPolyline(color)
    }

    private fun checkLocationPermission() {
        val accessFineLocation = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (accessFineLocation == PackageManager.PERMISSION_GRANTED) return

        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            AlertDialog.Builder(requireContext())
                .setTitle("Location Permission Needed")
                .setMessage("This app needs the Location permission, please accept to use location functionality")
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

    @SuppressLint("MissingPermission")
    private fun startTrackingLocation() {
        val accessFineLocation = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (accessFineLocation != PackageManager.PERMISSION_GRANTED) return

        context?.startService(Intent(context, LocationUpdatesService::class.java))
        timerViewModel.startTimer()

        polylinePoints.clear()
        startTime = System.currentTimeMillis() / 1000

        speed.visibility = View.VISIBLE
    }

    private fun stopTrackingLocation() {
        val accessFineLocation = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (accessFineLocation != PackageManager.PERMISSION_GRANTED) return

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

        if (startTime == 0L) return

        val path = Path(
            startTime,
            (System.currentTimeMillis() / 1000),
            array,
            spinner.selectedItem.toString()
        )

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)
            .add(path)
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document", exception)
            }

        speed.visibility = View.INVISIBLE
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        if (initialOnItemSelected) {
            initialOnItemSelected = false
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        db.collection(Constants.USERS)
            .document(uid)
            .update(Constants.LAST_ACTIVITY, parent.getItemAtPosition(pos))
    }

    override fun onNothingSelected(parent: AdapterView<*>) = Unit
}
