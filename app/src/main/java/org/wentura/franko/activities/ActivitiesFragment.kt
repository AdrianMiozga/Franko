package org.wentura.franko.activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.data.Activity
import org.wentura.franko.databinding.FragmentActivitiesBinding

@AndroidEntryPoint
class ActivitiesFragment : Fragment(R.layout.fragment_activities) {

    private val viewModel: ActivityListViewModel by viewModels()

    private val activityTypes = arrayListOf(
        Constants.BIKE,
        Constants.RUNNING,
        Constants.WALKING
    )

    private val recyclerListener = RecyclerView.RecyclerListener { holder ->
        val mapHolder = holder as ActivityAdapter.ViewHolder
        mapHolder.clearView()
    }

    companion object {
        val TAG = ActivitiesFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentActivitiesBinding.bind(view)

        val recyclerView = binding.activitesRecyclerView
        val activitiesNothingToShow = binding.activitiesNothingToShow

        val observer = Observer<ArrayList<Activity>> { activities ->
            binding.progressBarOverlay.progressBarOverlay.visibility = View.GONE
          
            if (activities.isEmpty()) {
                recyclerView.visibility = View.INVISIBLE
                activitiesNothingToShow.visibility = View.VISIBLE
                return@Observer
            } else {
                recyclerView.visibility = View.VISIBLE
                activitiesNothingToShow.visibility = View.INVISIBLE
            }

            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = ActivityAdapter(activities)
                setRecyclerListener(recyclerListener)
            }
        }

        viewModel.getCurrentActivities(activityTypes).observe(viewLifecycleOwner, observer)

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
