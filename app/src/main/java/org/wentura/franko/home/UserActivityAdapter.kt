package org.wentura.franko.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.Utilities
import org.wentura.franko.Utilities.createPolylineOptions
import org.wentura.franko.Utilities.setup
import org.wentura.franko.data.UserActivity
import org.wentura.franko.databinding.ListItemActivityHomeBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class UserActivityAdapter(private val userActivities: List<UserActivity>) :
    RecyclerView.Adapter<UserActivityAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        OnMapReadyCallback {

        private lateinit var googleMap: GoogleMap
        private lateinit var bounds: LatLngBounds

        private var points: ArrayList<LatLng> = arrayListOf()
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

        fun bindView(userActivity: UserActivity) {
            if (userActivity.activity.path == null) return

            mapView.tag = this

            for (point in userActivity.activity.path) {
                val latitude = point[Constants.LATITUDE]!!
                val longitude = point[Constants.LONGITUDE]!!

                points.add(LatLng(latitude, longitude))
            }

            bounds = Utilities.getBounds(points)

            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            val startTime = userActivity.activity.startTime ?: 0L
            val endTime = userActivity.activity.endTime ?: 0L
            val date = dateFormatter.format(TimeUnit.SECONDS.toMillis(startTime))

            val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)

            timeSpan.text = context.getString(
                R.string.time_span,
                timeFormatter.format(TimeUnit.SECONDS.toMillis(startTime)),
                timeFormatter.format(TimeUnit.SECONDS.toMillis(endTime))
            )

            title.text = context.getString(
                R.string.user_activity_title,
                userActivity.activity.activityName,
                userActivity.activity.activity,
            )

            dateTextView.text = date

            name.text = context.getString(
                R.string.full_name,
                userActivity.user.firstName,
                userActivity.user.lastName
            )

            val photoUrl = userActivity.user.photoUrl

            Utilities.loadProfilePicture(photoUrl, profileProfilePicture)

            setupMap()
        }

        fun clearView() {
            points.clear()

            if (!this::googleMap.isInitialized) return

            with(googleMap) {
                clear()
                mapType = GoogleMap.MAP_TYPE_NONE
            }
        }

        override fun onMapReady(googleMap: GoogleMap) {
            MapsInitializer.initialize(mapView.context)
            this.googleMap = googleMap

            setupMap()
        }

        private fun setupMap() {
            if (!this::googleMap.isInitialized) return

            googleMap.setup(context)

            if (points.isNotEmpty()) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))
                googleMap.addPolyline(createPolylineOptions().addAll(points))
            }

            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_activity_home, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindView(userActivities[position])
    }

    override fun getItemCount() = userActivities.size
}
