package org.wentura.franko.ui.following

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.R
import org.wentura.franko.adapters.PeopleAdapter
import org.wentura.franko.databinding.FragmentSimplifiedPeopleBinding

@AndroidEntryPoint
class FollowingFragment : Fragment(R.layout.fragment_simplified_people) {
    private val viewModel: FollowingListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentSimplifiedPeopleBinding.bind(view)

        val recyclerView = binding.simplifiedPeopleRecyclerView
        recyclerView.setHasFixedSize(true)

        val adapter = PeopleAdapter()
        recyclerView.adapter = adapter

        viewModel.following.observe(viewLifecycleOwner) { result ->
            binding.progressBarOverlay.progressBarOverlay.visibility = View.GONE

            recyclerView.apply { adapter.submitList(result) }
        }
    }
}
