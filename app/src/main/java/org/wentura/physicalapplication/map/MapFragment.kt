package org.wentura.physicalapplication.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import org.wentura.physicalapplication.Constants
import org.wentura.physicalapplication.Path
import org.wentura.physicalapplication.R
import org.wentura.physicalapplication.User
import org.wentura.physicalapplication.databinding.FragmentMapBinding
import kotlin.properties.Delegates

class MapFragment : Fragment(),
    OnMapReadyCallback,
    AdapterView.OnItemSelectedListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
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

    private val db = Firebase.firestore

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
    }

    companion object {
        private val TAG = MapFragment::class.simpleName

        private const val DEFAULT_ZOOM = 17F
        private const val LINE_WIDTH = 50F
        private const val LINE_COLOR = Color.BLUE
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations

            if (locationList.isEmpty()) return

            // The last location in the list is the newest
            val location = locationList.last()

            val latitude = location.latitude
            val longitude = location.longitude

            speedometer.speed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                location.speedAccuracyMetersPerSecond.toDouble()
            } else {
                location.speed.toDouble()
            }

            speed.text = getString(
                if (speedometer.miles) {
                    R.string.mph
                } else {
                    R.string.kmh
                },
                speedometer.speed.toInt()
            )

            currentLocation = LatLng(latitude, longitude)

            myMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
            myMap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM))

            if (trackPosition) {
                polylinePoints.add(currentLocation)
                polyline.points = polylinePoints
            } else {
                stopTrackingLocation()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        checkLocationPermission()

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

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        db.collection(Constants.USERS)
            .document(uid)
            .get()
            .addOnSuccessListener { result ->
                val user: User? = result.toObject()

                speedometer.miles = user?.miles ?: false
            }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        myMap.isMyLocationEnabled = true

        polyline = myMap.addPolyline(PolylineOptions().width(LINE_WIDTH).color(LINE_COLOR))
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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
        } else {
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Constants.MY_PERMISSIONS_REQUEST_LOCATION -> {
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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            polylinePoints.clear()
            startTime = System.currentTimeMillis() / 1000

            speed.visibility = View.VISIBLE

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun stopTrackingLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
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

            fusedLocationClient.removeLocationUpdates(locationCallback)

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
            .update("lastActivity", parent.getItemAtPosition(pos))
    }

    override fun onNothingSelected(parent: AdapterView<*>) = Unit
}
