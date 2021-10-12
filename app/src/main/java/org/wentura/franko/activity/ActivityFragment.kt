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
import org.wentura.franko.Utilities
import org.wentura.franko.Utilities.createPolylineOptions
import org.wentura.franko.Utilities.setup
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
        googleMap.setup(requireContext())

        val polyline = googleMap.addPolyline(createPolylineOptions())

        activityViewModel.getCurrentActivity().observe(viewLifecycleOwner) { activity ->
            if (activity.path == null) return@observe

            val startTime = activity?.startTime ?: 0L
            val endTime = activity?.endTime ?: 0L

            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = dateFormatter.format(TimeUnit.SECONDS.toMillis(startTime))

            val index =
                requireContext().resources.getStringArray(R.array.activities_array_values).indexOf(activity.activity)
            val activityType = requireContext().resources.getStringArray(R.array.activities_array)[index]

            activityTitle.text = requireContext().getString(
                R.string.activity_title,
                activity.activityName,
                activityType,
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

            for (point in activity.path) {
                val latitude = point[Constants.LATITUDE]!!
                val longitude = point[Constants.LONGITUDE]!!

                points.add(LatLng(latitude, longitude))
            }

            val bounds = Utilities.getBounds(points)

            polyline.points = points

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))
        }
    }
}
