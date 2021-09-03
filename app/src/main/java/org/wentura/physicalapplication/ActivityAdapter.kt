package org.wentura.physicalapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

class ActivityAdapter(private val dataSet: List<Path>) :
    RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view), OnMapReadyCallback {
        private lateinit var map: GoogleMap
        private var latLng: ArrayList<LatLng> = arrayListOf()

        private val title: TextView = view.findViewById(R.id.activity_title)
        private val mapView: MapView = view.findViewById(R.id.map)

        init {
            with(mapView) {
                onCreate(null)
                getMapAsync(this@ViewHolder)
                isClickable = false
            }
        }

        fun bindView(position: Int, dataSet: List<Path>) {
            dataSet[position].path?.forEach {
                latLng.add(LatLng(it["latitude"]!!, it["longitude"]!!))
            }

            mapView.tag = this
            title.text = (position + 1).toString()
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
            if (!::map.isInitialized) return

            with(map) {
                moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[0], 16f))
                addPolyline(PolylineOptions().addAll(latLng))
                mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.acitvity_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindView(position, dataSet)
    }

    override fun getItemCount() = dataSet.size
}
