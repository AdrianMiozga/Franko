package org.wentura.franko.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.databinding.FragmentActivityBinding
import org.wentura.franko.viewmodels.ActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ActivityFragment : Fragment(R.layout.fragment_activity),
    OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var latLng: ArrayList<LatLng> = arrayListOf()
    private var activityFragmentBinding: FragmentActivityBinding? = null
    private val activityViewModel: ActivityViewModel by viewModels()
    private val args: ActivityFragmentArgs by navArgs()

    companion object {
        val TAG = ActivityFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentActivityBinding.bind(view)
        activityFragmentBinding = binding

        val activityTitle = binding.activityTitle
        val activityTimeSpan = binding.activityTimeSpan

        val mapFragment = childFragmentManager.findFragmentById(R.id.activity_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        activityViewModel.activity.observe(viewLifecycleOwner) { path ->
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = dateFormatter.format(path.startTime?.times(1000))

            activityTitle.text = requireContext()
                .getString(R.string.activity_title, path.activity, date)

            val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)

            activityTimeSpan.text = requireContext()
                .getString(
                    R.string.time_span,
                    timeFormatter.format(
                        path.startTime?.times(1000)
                    ),
                    timeFormatter.format(
                        path.endTime?.times(1000)
                    )
                )

            path.path?.forEach {
                latLng.add(LatLng(it[Constants.LATITUDE]!!, it[Constants.LONGITUDE]!!))
            }

            if (this::map.isInitialized) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 16f))
                map.addPolyline(
                    PolylineOptions()
                        .addAll(latLng)
                        .width(Constants.LINE_WIDTH)
                        .color(Constants.LINE_COLOR)
                )
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activityFragmentBinding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit -> {
                view?.let {
                    Navigation.findNavController(it)
                        .navigate(ActivityFragmentDirections.toActivityEditFragment(args.id))
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }
}
