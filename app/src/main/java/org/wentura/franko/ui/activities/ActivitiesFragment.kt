package org.wentura.franko.ui.activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.adapters.UserActivityAdapter
import org.wentura.franko.data.UserActivity
import org.wentura.franko.databinding.FragmentActivitiesBinding

@AndroidEntryPoint
class ActivitiesFragment : Fragment(R.layout.fragment_activities) {

    private val viewModel: UserActivityListViewModel by viewModels()

    private val activityTypes = Constants.ACTIVITY_TYPES.toMutableList()

    companion object {
        val TAG = ActivitiesFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentActivitiesBinding.bind(view)

        val recyclerView = binding.activitiesRecyclerView
        recyclerView.setHasFixedSize(true)

        val adapter = UserActivityAdapter()
        recyclerView.adapter = adapter

        val activitiesNothingToShow = binding.activitiesNothingToShow

        val observer = Observer<List<UserActivity>> { userActivities ->
            binding.progressBarOverlay.progressBarOverlay.visibility = View.GONE

            if (userActivities.isEmpty()) {
                recyclerView.visibility = View.INVISIBLE
                activitiesNothingToShow.visibility = View.VISIBLE
                return@Observer
            } else {
                recyclerView.visibility = View.VISIBLE
                activitiesNothingToShow.visibility = View.INVISIBLE
            }

            adapter.submitList(userActivities)
        }

        viewModel
            .userActivities
            .observe(viewLifecycleOwner, observer)

        val chipBike = binding.chipBike
        val chipWalking = binding.chipWalking
        val chipRunning = binding.chipRunning

        chipBike.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                activityTypes.add(Constants.BIKE)
            } else {
                activityTypes.remove(Constants.BIKE)
            }

            viewModel.getCurrentActivities(activityTypes)
        }

        chipWalking.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                activityTypes.add(Constants.WALKING)
            } else {
                activityTypes.remove(Constants.WALKING)
            }

            viewModel.getCurrentActivities(activityTypes)
        }

        chipRunning.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                activityTypes.add(Constants.RUNNING)
            } else {
                activityTypes.remove(Constants.RUNNING)
            }

            viewModel.getCurrentActivities(activityTypes)
        }
    }
}
