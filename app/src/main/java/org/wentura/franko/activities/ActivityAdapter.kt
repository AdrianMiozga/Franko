package org.wentura.franko.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import org.wentura.franko.Constants
import org.wentura.franko.ProfileViewPagerFragmentDirections
import org.wentura.franko.R
import org.wentura.franko.Utilities.createPolylineOptions
import org.wentura.franko.data.Activity
import org.wentura.franko.databinding.ListItemActivityBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ActivityAdapter(private val userActivities: List<Activity>) :
    RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view),
        OnMapReadyCallback {

        private lateinit var map: GoogleMap
        private var latLng: ArrayList<LatLng> = arrayListOf()
        private val binding = ListItemActivityBinding.bind(view)

        private val title: TextView = binding.itemActivityTitle
        private val mapView: MapView = binding.itemActivityMap
        private val timeSpan: TextView = binding.itemActivityTimeSpan

        private val context = view.context

        init {
            with(mapView) {
                onCreate(null)
                getMapAsync(this@ViewHolder)
                isClickable = false
            }
        }

        fun bindView(position: Int, userActivities: List<Activity>) {
            view.setOnClickListener {
                val toActivityFragment =
                    ProfileViewPagerFragmentDirections
                        .toActivityFragment(userActivities[position].documentId)

                Navigation.findNavController(view).navigate(toActivityFragment)
            }

            userActivities[position].path?.forEach {
                latLng.add(LatLng(it[Constants.LATITUDE]!!, it[Constants.LONGITUDE]!!))
            }

            mapView.tag = this

            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            val startTime = userActivities[position].startTime ?: 0L
            val endTime = userActivities[position].endTime ?: 0L
            val date = dateFormatter.format(TimeUnit.SECONDS.toMillis(startTime))

            val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)

            timeSpan.text = context.getString(
                R.string.time_span,
                timeFormatter.format(
                    TimeUnit.SECONDS.toMillis(startTime)
                ),
                timeFormatter.format(
                    TimeUnit.SECONDS.toMillis(endTime)
                )
            )

            title.text = context.getString(
                R.string.activity_title,
                userActivities[position].activityName,
                userActivities[position].activity,
                date
            )

            setupMap()
        }

        fun clearView() {
            if (!this::map.isInitialized) return

            with(map) {
                clear()
                mapType = GoogleMap.MAP_TYPE_NONE
            }
        }

        override fun onMapReady(googleMap: GoogleMap) {
            MapsInitializer.initialize(mapView.context)
            map = googleMap
            setupMap()
        }

        private fun setupMap() {
            if (!this::map.isInitialized) return

            with(map) {
                if (latLng.size > 0) {
                    moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 16f))
                    addPolyline(createPolylineOptions().addAll(latLng))
                }

                map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        context,
                        R.raw.google_map_style
                    )
                )

                mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_activity, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindView(position, userActivities)
    }

    override fun getItemCount() = userActivities.size
}
