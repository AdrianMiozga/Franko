package org.wentura.franko.ui.activitymap

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.Utilities
import org.wentura.franko.Utilities.createPolylineOptions
import org.wentura.franko.Utilities.setup
import org.wentura.franko.ui.activity.UserActivityViewModel

@AndroidEntryPoint
class ActivityMapFragment :
    Fragment(R.layout.fragment_activity_map),
    OnMapReadyCallback {

    private val viewModel: UserActivityViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mapFragment =
            childFragmentManager
                .findFragmentById(R.id.activity_map_map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setup(requireContext())

        val polyline = googleMap.addPolyline(createPolylineOptions())

        viewModel.userActivity.observe(viewLifecycleOwner) { userActivity ->
            val activity = userActivity.activity

            if (activity.path == null) return@observe

            val points: ArrayList<LatLng> = arrayListOf()

            for (point in activity.path) {
                val latitude = point[Constants.LATITUDE]!!
                val longitude = point[Constants.LONGITUDE]!!

                points.add(LatLng(latitude, longitude))
            }

            val bounds = Utilities.getBounds(points)

            polyline.points = points

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }
}
