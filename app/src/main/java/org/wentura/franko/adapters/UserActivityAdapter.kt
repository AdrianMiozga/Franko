package org.wentura.franko.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.Utilities
import org.wentura.franko.Utilities.createPolylineOptions
import org.wentura.franko.Utilities.getCurrentUserUid
import org.wentura.franko.Utilities.setup
import org.wentura.franko.data.UserActivity
import org.wentura.franko.databinding.ListItemActivityBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class UserActivityAdapter :
    ListAdapter<UserActivity, UserActivityAdapter.ViewHolder>(UserActivityDiffCallback()) {

    class ViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view),
        OnMapReadyCallback {

        private lateinit var googleMap: GoogleMap
        private lateinit var bounds: LatLngBounds

        private val currentUser: String by lazy { getCurrentUserUid() }

        private var points: ArrayList<LatLng> = arrayListOf()
        private val binding = ListItemActivityBinding.bind(view)

        //        private val profileProfilePicture: ImageView = binding.activityView.activityProfilePicture
        private val name: TextView = binding.activityView.activityUsername
        private val dateTextView: TextView = binding.activityView.activityDate
        private val title: TextView = binding.activityView.activityTitle
        private val mapView: MapView = binding.itemActivityHomeMap
        private val durationTextView: TextView = binding.activityView.activityDuration

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

            view.setOnClickListener {
                val arguments = bundleOf(Pair("id", userActivity.activity.documentId))

                if (currentUser == userActivity.user.uid) {
                    arguments.putBoolean("currentUser", true)
                }

                Navigation.findNavController(view).navigate(
                    R.id.to_activity_fragment,
                    arguments
                )
            }

            for (point in userActivity.activity.path) {
                val latitude = point[Constants.LATITUDE]!!
                val longitude = point[Constants.LONGITUDE]!!

                points.add(LatLng(latitude, longitude))
            }

            bounds = Utilities.getBounds(points)

            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            val startTime = userActivity.activity.startTime ?: 0L
            val endTime = userActivity.activity.endTime ?: 0L
            val duration = endTime - startTime

            durationTextView.text = context.getString(
                R.string.time,
                Utilities.formatTime(TimeUnit.SECONDS.toMillis(duration))
            )

            val activityType = context.resources.getStringArray(R.array.activities_array)[context.resources
                .getStringArray(R.array.activities_array_values).indexOf(userActivity.activity.activity)]

            title.text = context.getString(
                R.string.user_activity_title,
                userActivity.activity.activityName,
                activityType
            )

            val date = dateFormatter.format(TimeUnit.SECONDS.toMillis(startTime))
            val timeFormatter = SimpleDateFormat("HH:mm", Locale.US)

            dateTextView.text = context.getString(
                R.string.date_and_time,
                date,
                timeFormatter.format(TimeUnit.SECONDS.toMillis(startTime))
            )

            name.text = context.getString(
                R.string.full_name,
                userActivity.user.firstName,
                userActivity.user.lastName
            )

            // TODO: 13.10.2021 This breaks MapView rendering
//            val photoUrl = userActivity.user.photoUrl
//            profileProfilePicture.loadProfilePicture(photoUrl)

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

            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            googleMap.setup(context)

            if (points.isNotEmpty()) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))
                googleMap.addPolyline(createPolylineOptions().addAll(points))
            }
        }
    }

    override fun onViewRecycled(viewHolder: ViewHolder) {
        super.onViewRecycled(viewHolder)

        viewHolder.clearView()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_activity, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindView(getItem(position))
    }
}

private class UserActivityDiffCallback : DiffUtil.ItemCallback<UserActivity>() {

    override fun areItemsTheSame(
        oldItem: UserActivity,
        newItem: UserActivity
    ): Boolean {
        return oldItem.activity.documentId == newItem.activity.documentId
    }

    override fun areContentsTheSame(
        oldItem: UserActivity,
        newItem: UserActivity
    ): Boolean {
        return oldItem == newItem
    }
}
