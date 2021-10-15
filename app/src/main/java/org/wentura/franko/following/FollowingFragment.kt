package org.wentura.franko.following

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.R
import org.wentura.franko.adapters.PeopleAdapter
import org.wentura.franko.databinding.FragmentSimplifiedPeopleBinding

@AndroidEntryPoint
class FollowingFragment : Fragment(R.layout.fragment_simplified_people) {
    private val viewModel: FollowingListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentSimplifiedPeopleBinding.bind(view)

        viewModel.following.observe(viewLifecycleOwner) { result ->
            binding.progressBarOverlay.progressBarOverlay.visibility = View.GONE

            binding.simplifiedPeopleRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = PeopleAdapter(result)
            }
        }
    }
}
