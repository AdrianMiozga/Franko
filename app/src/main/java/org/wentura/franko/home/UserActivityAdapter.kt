package org.wentura.franko.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.Utilities
import org.wentura.franko.data.UserActivity
import org.wentura.franko.databinding.ListItemActivityHomeBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class UserActivityAdapter(private val userActivities: List<UserActivity>) :
    RecyclerView.Adapter<UserActivityAdapter.ViewHolder>() {

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view),
        OnMapReadyCallback {

        private lateinit var map: GoogleMap
        private var latLng: ArrayList<LatLng> = arrayListOf()
        private val binding = ListItemActivityHomeBinding.bind(view)

        private val profileProfilePicture: ImageView = binding.itemActivityHomeProfilePicture
        private val name: TextView = binding.itemActivityHomeUsername
        private val dateTextView: TextView = binding.itemActivityHomeDate
        private val title: TextView = binding.itemActivityHomeTitle
        private val mapView: MapView = binding.itemActivityHomeMap
        private val timeSpan: TextView = binding.itemActivityHomeTimeSpan

        private val context = view.context

        init {
            with(mapView) {
                onCreate(null)
                getMapAsync(this@ViewHolder)
                isClickable = false
            }
        }

        fun bindView(position: Int, userActivities: List<UserActivity>) {
//            view.setOnClickListener {
//                Navigation.findNavController(view).navigate(
//                    ActivitiesFragmentDirections.toActivityFragment(activities[position].documentId)
//                )
//            }

            userActivities[position].activity.path?.forEach {
                latLng.add(LatLng(it[Constants.LATITUDE]!!, it[Constants.LONGITUDE]!!))
            }

            mapView.tag = this

            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            val startTime = userActivities[position].activity.startTime ?: 0L
            val endTime = userActivities[position].activity.endTime ?: 0L
            val date = dateFormatter.format(TimeUnit.SECONDS.toMillis(startTime))

            val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)

            timeSpan.text = context.getString(
                R.string.time_span,
                timeFormatter.format(TimeUnit.SECONDS.toMillis(startTime)),
                timeFormatter.format(TimeUnit.SECONDS.toMillis(endTime))
            )

            title.text = context.getString(
                R.string.user_activity_title,
                userActivities[position].activity.activityName,
                userActivities[position].activity.activity,
            )
           
            dateTextView.text = date

            name.text = context.getString(
                R.string.full_name,
                userActivities[position].user.firstName,
                userActivities[position].user.lastName
            )

            val photoUrl = userActivities[position].user.photoUrl

            Utilities.loadProfilePicture(photoUrl, profileProfilePicture)

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
                    addPolyline(
                        PolylineOptions()
                            .addAll(latLng)
                            .width(Constants.LINE_WIDTH)
                            .color(Constants.LINE_COLOR)
                    )
                }

                mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_activity_home, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindView(position, userActivities)
    }

    override fun getItemCount() = userActivities.size
}
