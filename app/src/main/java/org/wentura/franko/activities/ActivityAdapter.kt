package org.wentura.franko.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import org.wentura.franko.Constants
import org.wentura.franko.ProfileViewPagerFragmentDirections
import org.wentura.franko.R
import org.wentura.franko.Utilities
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
        private var points: ArrayList<LatLng> = arrayListOf()
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

        fun bindView(activity: Activity) {
            view.setOnClickListener {
                val toActivityFragment =
                    ProfileViewPagerFragmentDirections
                        .toActivityFragment(activity.documentId)

                Navigation.findNavController(view).navigate(toActivityFragment)
            }

            activity.path?.forEach {
                points.add(LatLng(it[Constants.LATITUDE]!!, it[Constants.LONGITUDE]!!))
            }

            mapView.tag = this

            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            val startTime = activity.startTime ?: 0L
            val endTime = activity.endTime ?: 0L
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
                activity.activityName,
                activity.activity,
                date
            )

            setupMap()
        }

        fun clearView() {
            points.clear()

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

            if (points.isNotEmpty()) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(points.first(), 16f))
                map.addPolyline(createPolylineOptions().addAll(points))
            }

            Utilities.setupMap(map, context)

            map.mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_activity, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindView(userActivities[position])
    }

    override fun getItemCount() = userActivities.size
}
