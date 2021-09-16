package org.wentura.franko.activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.R
import org.wentura.franko.data.Path
import org.wentura.franko.databinding.FragmentActivitiesBinding

@AndroidEntryPoint
class ActivitiesFragment : Fragment(R.layout.fragment_activities) {

    private var pathsArray: ArrayList<Path> = arrayListOf()

    private val viewModel: ActivityListViewModel by viewModels()

    private val recyclerListener = RecyclerView.RecyclerListener { holder ->
        val mapHolder = holder as ActivityAdapter.ViewHolder
        mapHolder.clearView()
    }

    private var fragmentActivitiesBinding: FragmentActivitiesBinding? = null

    companion object {
        val TAG = ActivitiesFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentActivitiesBinding.bind(view)
        fragmentActivitiesBinding = binding

        viewModel.getCurrentActivities().observe(viewLifecycleOwner) { paths ->
            pathsArray = paths

            binding.activitesRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = ActivityAdapter(pathsArray)
                setRecyclerListener(recyclerListener)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentActivitiesBinding = null
    }
}
