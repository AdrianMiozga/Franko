package org.wentura.franko.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.data.Path
import org.wentura.franko.databinding.ListItemActivityBinding
import java.text.SimpleDateFormat
import java.util.*

class ActivityAdapter(private val paths: List<Path>) :
    RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        OnMapReadyCallback {

        private lateinit var map: GoogleMap
        private var latLng: ArrayList<LatLng> = arrayListOf()
        private val binding = ListItemActivityBinding.bind(view)

        private val title: TextView = binding.activityTitle
        private val mapView: MapView = binding.activityMap
        private val timeSpan: TextView = binding.activityTimeSpan

        private val context = view.context

        init {
            with(mapView) {
                onCreate(null)
                getMapAsync(this@ViewHolder)
                isClickable = false
            }
        }

        fun bindView(position: Int, paths: List<Path>) {
            paths[position].path?.forEach {
                latLng.add(LatLng(it[Constants.LATITUDE]!!, it[Constants.LONGITUDE]!!))
            }

            mapView.tag = this

            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            val date = dateFormatter.format(paths[position].startTime?.times(1000))

            val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)

            timeSpan.text = context.getString(
                R.string.time_span,
                timeFormatter.format(
                    paths[position].startTime?.times(1000)
                ),
                timeFormatter.format(
                    paths[position].endTime?.times(1000)
                )
            )

            title.text = context.getString(
                R.string.activity_title,
                paths[position].activity,
                (position + 1),
                date
            )

            setupMap()
        }

        fun clearView() {
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
                    addPolyline(PolylineOptions().addAll(latLng))
                }

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
        viewHolder.bindView(position, paths)
    }

    override fun getItemCount() = paths.size
}
