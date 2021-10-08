package org.wentura.franko.activitysave

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.R
import org.wentura.franko.Utilities
import org.wentura.franko.Utilities.createPolylineOptions
import org.wentura.franko.Utilities.setup
import org.wentura.franko.data.ActivityRepository
import org.wentura.franko.data.UserRepository
import org.wentura.franko.databinding.FragmentActivitySaveBinding
import org.wentura.franko.map.RecordingRepository
import org.wentura.franko.map.RecordingViewModel
import org.wentura.franko.viewmodels.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ActivitySaveFragment : Fragment(R.layout.fragment_activity_save),
    OnMapReadyCallback {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var activityRepository: ActivityRepository

    @Inject
    lateinit var recordingRepository: RecordingRepository

    private val userViewModel: UserViewModel by viewModels()
    private val recordingViewModel: RecordingViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var activitySaveObserver: ActivitySaveObserver

    companion object {
        val TAG = ActivitySaveFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        val binding = FragmentActivitySaveBinding.bind(view)

        val activityTypeSpinner = binding.activitySaveActivityTypeSpinner
        val activityVisibilitySpinner = binding.activitySaveActivityVisibilitySpinner

        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            val lastActivity = user.lastActivity

            val lastActivityId = resources
                .getStringArray(R.array.activities_array)
                .indexOf(lastActivity)

            activityTypeSpinner.setSelection(lastActivityId)

            val whoCanSeeActivityDefault = user.whoCanSeeActivityDefault

            val visibilityId = resources
                .getStringArray(R.array.who_can_see_activity)
                .indexOf(whoCanSeeActivityDefault)

            activityVisibilitySpinner.setSelection(visibilityId)
        }

        activitySaveObserver = ActivitySaveObserver(
            recordingRepository,
            activityRepository,
            requireContext(),
            view
        )

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment =
            childFragmentManager
                .findFragmentById(R.id.activity_save_map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        lifecycle.addObserver(activitySaveObserver)

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                ActivitySaveDialogFragment(activitySaveObserver, view).show(
                    parentFragmentManager,
                    ActivitySaveDialogFragment::class.simpleName
                )
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                findNavController().navigateUp()
                activitySaveObserver.save()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setup(requireContext())

        val polyline = googleMap.addPolyline(createPolylineOptions())

        recordingViewModel.points.observe(viewLifecycleOwner) { points ->
            val latLngBounds = Utilities.getBounds(points)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0))

            polyline.points = points
        }
    }
}
