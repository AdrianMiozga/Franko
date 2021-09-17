package org.wentura.franko.following

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.R
import org.wentura.franko.databinding.FragmentFollowingBinding

@AndroidEntryPoint
class FollowingFragment : Fragment(R.layout.fragment_following) {

    private lateinit var followingAdapter: FollowingAdapter
    private val viewModel: FollowingListViewModel by viewModels()

    private val recyclerListener = RecyclerView.RecyclerListener { holder ->
        holder as FollowingAdapter.ViewHolder
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentFollowingBinding.bind(view)

        viewModel.users.observe(viewLifecycleOwner) { result ->
            followingAdapter = FollowingAdapter(result)

            binding.followingRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = followingAdapter
                setRecyclerListener(recyclerListener)
            }
        }
    }
}
