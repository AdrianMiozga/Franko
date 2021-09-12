package org.wentura.franko.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import org.wentura.franko.databinding.FragmentMapBinding
import kotlin.properties.Delegates

class MapFragment : Fragment(R.layout.fragment_map),
    OnMapReadyCallback,
    AdapterView.OnItemSelectedListener {

    private lateinit var myMap: GoogleMap
    private lateinit var currentLocation: LatLng
    private lateinit var spinner: Spinner
    private lateinit var speed: TextView
    private lateinit var polyline: Polyline
    private val polylinePoints: MutableList<LatLng> = mutableListOf()
    private var trackPosition: Boolean = false
    private var startTime by Delegates.notNull<Long>()
    private var initialOnItemSelected = true
    private val speedometer: Speedometer = Speedometer()

    private val model: LocationViewModel by viewModels()

    private val db = Firebase.firestore

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    private var fragmentMapBinding: FragmentMapBinding? = null

    companion object {
        private val TAG = MapFragment::class.simpleName

        private const val DEFAULT_ZOOM = 17F
        private const val LINE_WIDTH = 50F
        private const val LINE_COLOR = Color.BLUE
    }

//    private var locationCallback: LocationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            val locationList = locationResult.locations
//
//            if (locationList.isEmpty()) return
//
//            // The last location in the list is the newest
//            val location = locationList.last()
//
//            val latitude = location.latitude
//            val longitude = location.longitude
//
//            speedometer.speed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                location.speedAccuracyMetersPerSecond.toDouble()
//            } else {
//                location.speed.toDouble()
//            }
//
//            speed.text = getString(
//                if (speedometer.unitsOfMeasure == Constants.IMPERIAL) {
//                    R.string.mph
//                } else {
//                    R.string.kmh
//                },
//                speedometer.speed.toInt()
//            )
//
//            currentLocation = LatLng(latitude, longitude)
//
//            myMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
//            myMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM))
//
//            if (trackPosition) {
//                polylinePoints.add(currentLocation)
//                polyline.points = polylinePoints
//            } else {
//                stopTrackingLocation()
//            }
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMapBinding.bind(view)
        fragmentMapBinding = binding

        checkLocationPermission()

        model.currentLocation.observe(viewLifecycleOwner) { location ->
            Log.d(TAG, "onViewCreated: $location")
//            Log.d(TAG, "${location.latitude}, ${location.longitude}")
//            binding.mapSpeed.visibility = View.VISIBLE
//            binding.mapSpeed.text = location.latitude.toString()
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

//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location ->
//                if (location == null) return@addOnSuccessListener
//
//                val latLng = LatLng(location.latitude, location.longitude)
//
//                myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
//                myMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM))
//            }

        polyline = myMap.addPolyline(PolylineOptions().width(LINE_WIDTH).color(LINE_COLOR))
    }

    private fun checkLocationPermission() {
        val accessFineLocation = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (accessFineLocation != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
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

        if (accessFineLocation == PackageManager.PERMISSION_GRANTED) {
            context?.startService(Intent(context, LocationUpdatesService::class.java))

            polylinePoints.clear()
            startTime = System.currentTimeMillis() / 1000

            speed.visibility = View.VISIBLE
        }
    }

    private fun stopTrackingLocation() {
        val accessFineLocation = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (accessFineLocation == PackageManager.PERMISSION_GRANTED) {
            context?.stopService(Intent(context, LocationUpdatesService::class.java))

            val array: MutableList<HashMap<String, Double>> = ArrayList()

            for (point in polylinePoints) {
                array.add(
                    hashMapOf(
                        "latitude" to point.latitude,
                        "longitude" to point.longitude
                    )
                )
            }

            val path = Path(
                startTime,
                (System.currentTimeMillis() / 1000),
                array,
                spinner.selectedItem.toString()
            )

            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            db.collection(Constants.USERS)
                .document(uid)
                .collection(Constants.PATHS).add(path)
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

            speed.visibility = View.INVISIBLE
        }
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
