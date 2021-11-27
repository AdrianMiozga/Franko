package org.wentura.franko.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
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
import org.wentura.franko.Utilities.loadProfilePicture
import org.wentura.franko.Utilities.setup
import org.wentura.franko.databinding.FragmentActivityBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ActivityFragment :
    Fragment(R.layout.fragment_activity),
    OnMapReadyCallback {

    private val viewModel: UserActivityViewModel by viewModels()
    private val args: ActivityFragmentArgs by navArgs()

    private lateinit var profilePicture: ImageView
    private lateinit var userName: TextView
    private lateinit var activityTitle: TextView
    private lateinit var activityDate: TextView
    private lateinit var activityDuration: TextView
    private lateinit var activityLength: TextView
    private lateinit var activityAverageSpeed: TextView
    private lateinit var activityMaxSpeed: TextView

    companion object {
        val TAG = ActivityFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (args.currentUser) {
            setHasOptionsMenu(true)
        }

        val binding = FragmentActivityBinding.bind(view)

        binding.activityView.let {
            profilePicture = it.activityProfilePicture
            userName = it.activityUsername
            activityTitle = it.activityTitle
            activityDate = it.activityDate
            activityDuration = it.activityDuration
            activityLength = it.activityLength
        }

        activityAverageSpeed = binding.activityAverageSpeed
        activityMaxSpeed = binding.activityMaxSpeed

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

        viewModel.userActivity.observe(viewLifecycleOwner) { userActivity ->
            val activity = userActivity.activity
            val user = userActivity.user

            if (activity.path == null) return@observe

            userName.text = getString(
                R.string.full_name,
                user.firstName,
                user.lastName
            )

            profilePicture.loadProfilePicture(user.photoUrl)

            val startTime = activity.startTime ?: 0L
            val endTime = activity.endTime ?: 0L
            val duration = endTime - startTime

            activityDuration.text = getString(
                R.string.time,
                Utilities.formatTime(TimeUnit.SECONDS.toMillis(duration))
            )

            val index = resources
                .getStringArray(R.array.activities_array_values)
                .indexOf(activity.activity)

            val activityType = resources
                .getStringArray(R.array.activities_array)[index]

            activityTitle.text = getString(
                R.string.activity_title,
                activity.activityName,
                activityType
            )

            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = dateFormatter.format(TimeUnit.SECONDS.toMillis(startTime))
            val timeFormatter = SimpleDateFormat("HH:mm", Locale.US)

            activityDate.text = getString(
                R.string.date_and_time,
                date,
                timeFormatter.format(TimeUnit.SECONDS.toMillis(startTime))
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

            activityLength.text = getString(
                R.string.activity_length,
                activity.length / 1000
            )

            val averageSpeed = (activity.length / duration).times(Constants.MS_TO_KMH)

            activityAverageSpeed.text = getString(
                R.string.activity_average_speed,
                averageSpeed
            )

            activityMaxSpeed.text = getString(
                R.string.activity_max_speed,
                activity.maxSpeed.times(Constants.MS_TO_KMH)
            )
        }
    }
}
