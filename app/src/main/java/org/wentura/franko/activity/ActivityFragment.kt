package org.wentura.franko.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.databinding.FragmentActivityBinding
import org.wentura.franko.viewmodels.ActivityViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ActivityFragment : Fragment(R.layout.fragment_activity),
    OnMapReadyCallback {

    private val activityViewModel: ActivityViewModel by viewModels()
    private val args: ActivityFragmentArgs by navArgs()

    private lateinit var activityTitle: TextView
    private lateinit var activityTimeSpan: TextView

    companion object {
        val TAG = ActivityFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        val binding = FragmentActivityBinding.bind(view)

        activityTitle = binding.activityTitle
        activityTimeSpan = binding.activityTimeSpan

        val mapFragment =
            childFragmentManager
                .findFragmentById(R.id.activity_map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // TODO: 07.10.2021 Simplify like in navigation component doc
        return when (item.itemId) {
            R.id.edit -> {
                Navigation.findNavController(requireView())
                    .navigate(ActivityFragmentDirections.toActivityEditFragment(args.id))

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.google_map_style
            )
        )

        val polylineOptions = PolylineOptions()
            .width(Constants.LINE_WIDTH)
            .color(Constants.LINE_COLOR)

        val polyline = googleMap.addPolyline(polylineOptions)

        activityViewModel.getCurrentActivity().observe(viewLifecycleOwner) { activity ->
            val startTime = activity?.startTime ?: 0L
            val endTime = activity?.endTime ?: 0L

            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = dateFormatter.format(TimeUnit.SECONDS.toMillis(startTime))

            activityTitle.text = requireContext()
                .getString(
                    R.string.activity_title,
                    activity.activityName,
                    activity.activity,
                    date
                )

            val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)

            activityTimeSpan.text = requireContext()
                .getString(
                    R.string.time_span,
                    timeFormatter.format(
                        TimeUnit.SECONDS.toMillis(startTime)
                    ),
                    timeFormatter.format(
                        TimeUnit.SECONDS.toMillis(endTime)
                    )
                )

            val latLng: ArrayList<LatLng> = arrayListOf()

            activity.path?.forEach {
                latLng.add(LatLng(it[Constants.LATITUDE]!!, it[Constants.LONGITUDE]!!))
            }

            polyline.points = latLng

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 16f))
        }
    }
}
