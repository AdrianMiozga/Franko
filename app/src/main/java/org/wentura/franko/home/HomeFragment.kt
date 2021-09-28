package org.wentura.franko.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.R
import org.wentura.franko.databinding.FragmentHomeBinding

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: UserActivityListViewModel by viewModels()

    private val recyclerListener = RecyclerView.RecyclerListener { holder ->
        val mapHolder = holder as UserActivityAdapter.ViewHolder
        mapHolder.clearView()
    }

    companion object {
        val TAG = HomeFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentHomeBinding.bind(view)

        val recyclerView = binding.homeRecyclerView

        viewModel.getCurrentActivities().observe(viewLifecycleOwner) { activities ->
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = UserActivityAdapter(activities)
                setRecyclerListener(recyclerListener)
            }
        }
    }
}
