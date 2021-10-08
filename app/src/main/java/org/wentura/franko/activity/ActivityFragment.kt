package org.wentura.franko.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.Utilities.createPolylineOptions
import org.wentura.franko.Utilities.setupMap
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
        return when (item.itemId) {
            R.id.edit -> {
                val toActivityEditFragment =
                    ActivityFragmentDirections.toActivityEditFragment(args.id)

                findNavController().navigate(toActivityEditFragment)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        setupMap(googleMap, requireContext())

        val polyline = googleMap.addPolyline(createPolylineOptions())

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

            val points: ArrayList<LatLng> = arrayListOf()

            activity.path?.forEach {
                points.add(LatLng(it[Constants.LATITUDE]!!, it[Constants.LONGITUDE]!!))
            }

            polyline.points = points

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.first(), 16f))
        }
    }
}
