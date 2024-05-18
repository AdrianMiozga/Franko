package org.wentura.franko.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.ProfileViewPagerFragmentDirections
import org.wentura.franko.R
import org.wentura.franko.Utilities.getCurrentUserUid
import org.wentura.franko.Utilities.loadProfilePicture
import org.wentura.franko.databinding.FragmentProfileMyBinding
import org.wentura.franko.viewmodels.UserViewModel

@AndroidEntryPoint
class ProfileMyFragment : Fragment(R.layout.fragment_profile_my) {

    private val userViewModel: UserViewModel by viewModels()

    private var followersLoaded = false
    private var followingsLoaded = false
    private var profileLoaded = false

    companion object {
        val TAG = ProfileMyFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentProfileMyBinding.bind(view)

        val profileProfilePicture = binding.profilePart1.profileProfilePicture
        val profileFullName = binding.profilePart1.profileFullName
        val profileBio = binding.profilePart1.profileBio
        val profileCity = binding.profilePart1.profileCity

        val profileFollowing = binding.profilePart2.profileFollowing
        val profileFollowers = binding.profilePart2.profileFollowers

        val editProfile = binding.profileMyEditProfile
        val progressBarOverlay = binding.progressBarOverlay.progressBarOverlay

        editProfile.setOnClickListener {
            val toProfileEditFragment = ProfileViewPagerFragmentDirections.toProfileEditFragment()

            findNavController().navigate(toProfileEditFragment)
        }

        userViewModel.user.observe(viewLifecycleOwner) { profile ->
            profileLoaded = true
            show(progressBarOverlay)

            profileProfilePicture.loadProfilePicture(profile.photoUrl)

            profileFullName.text =
                getString(R.string.full_name, profile.firstName, profile.lastName)

            if (profile.bio.isNotBlank()) {
                profileBio.visibility = View.VISIBLE
                profileBio.text = profile.bio
            }

            val locationEveryone = resources.getStringArray(R.array.who_can_see_my_location).first()

            if (profile.whoCanSeeMyLocation == locationEveryone) {
                profileCity.text = profile.city
            } else {
                profileCity.visibility = View.GONE
            }
        }

        userViewModel.following.observe(viewLifecycleOwner) { following ->
            followingsLoaded = true
            show(progressBarOverlay)

            val size = following.size

            profileFollowing.text =
                resources.getQuantityString(R.plurals.number_following, size, size)

            if (following.isEmpty()) return@observe

            profileFollowing.setOnClickListener {
                val toFollowingFragment =
                    ProfileFragmentDirections.toFollowingFragment(getCurrentUserUid())

                findNavController().navigate(toFollowingFragment)
            }
        }

        userViewModel.followers.observe(viewLifecycleOwner) { followers ->
            followersLoaded = true
            show(progressBarOverlay)

            val size = followers.size

            profileFollowers.text =
                resources.getQuantityString(R.plurals.number_followers, size, size)

            if (followers.isEmpty()) return@observe

            profileFollowers.setOnClickListener {
                val toFollowersFragment =
                    ProfileFragmentDirections.toFollowersFragment(getCurrentUserUid())

                findNavController().navigate(toFollowersFragment)
            }
        }
    }

    private fun show(view: View) {
        // TODO: 30.09.2021 Write this cleaner, without booleans
        if (profileLoaded && followersLoaded && followingsLoaded) {
            view.visibility = View.GONE
        }
    }
}
