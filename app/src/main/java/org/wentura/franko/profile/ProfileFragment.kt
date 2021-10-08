package org.wentura.franko.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.Constants
import org.wentura.franko.ProfileViewPagerFragmentDirections
import org.wentura.franko.R
import org.wentura.franko.Utilities
import org.wentura.franko.databinding.FragmentProfileBinding
import org.wentura.franko.viewmodels.UserViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val userViewModel: UserViewModel by viewModels()

    private var followersLoaded = false
    private var followingsLoaded = false
    private var profileLoaded = false

    private val db = Firebase.firestore

    companion object {
        val TAG = ProfileFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentProfileBinding.bind(view)

        // TODO: 08.10.2021 User helper method and always throw exception if not found?
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("Current user UID does not exist")

        val arguments = arguments

        val argUid = if (arguments == null) {
            uid
        } else {
            ProfileFragmentArgs.fromBundle(arguments).uid
        }

        val profileFollow = binding.profileFollow
        val profileUnfollow = binding.profileUnfollow
        val profileProfilePicture = binding.profileProfilePicture
        val profileFullName = binding.profileFullName
        val profileBio = binding.profileBio
        val profileCity = binding.profileCity
        val profileFollowing = binding.profileFollowing
        val profileFollowers = binding.profileFollowers
        val editProfile = binding.profileEditProfile
        val progressBarOverlay = binding.progressBarOverlay.progressBarOverlay

        profileFollow.setOnClickListener {
            // TODO: 17.09.2021 Queries should act like transactions
            db.collection(Constants.USERS)
                .document(uid)
                .collection(Constants.FOLLOWING)
                .document(argUid)
                .set(hashMapOf(Constants.UID to argUid))
                .addOnSuccessListener {
                    profileFollow.visibility = View.INVISIBLE
                    profileUnfollow.visibility = View.VISIBLE
                }

            db.collection(Constants.USERS)
                .document(argUid)
                .collection(Constants.FOLLOWERS)
                .document(uid)
                .set(hashMapOf(Constants.UID to uid))
        }

        profileUnfollow.setOnClickListener {
            db.collection(Constants.USERS)
                .document(uid)
                .collection(Constants.FOLLOWING)
                .document(argUid)
                .delete()
                .addOnSuccessListener {
                    profileUnfollow.visibility = View.INVISIBLE
                    profileFollow.visibility = View.VISIBLE
                }

            db.collection(Constants.USERS)
                .document(argUid)
                .collection(Constants.FOLLOWERS)
                .document(uid)
                .delete()
        }

        editProfile.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(ProfileViewPagerFragmentDirections.toProfileEditFragment())
        }

        userViewModel.getUser(argUid).observe(viewLifecycleOwner) { profile ->
            profileLoaded = true
            show(progressBarOverlay)

            Utilities.loadProfilePicture(profile.photoUrl, profileProfilePicture)

            profileFullName.text = getString(
                R.string.full_name,
                profile.firstName,
                profile.lastName
            )

            if (profile.bio.isNotBlank()) {
                profileBio.visibility = View.VISIBLE
                profileBio.text = profile.bio
            }

            val locationEveryone = resources.getStringArray(R.array.who_can_see_my_location)[0]

            if (profile.whoCanSeeMyLocation == locationEveryone) {
                profileCity.text = profile.city
            } else {
                profileCity.visibility = View.GONE
            }

            if (argUid == uid) {
                profileFollow.visibility = View.GONE
            } else {
                editProfile.visibility = View.GONE
            }
        }

        userViewModel.getFollowing(argUid).observe(viewLifecycleOwner) { following ->
            followingsLoaded = true
            show(progressBarOverlay)

            val size = following.size

            profileFollowing.text =
                resources.getQuantityString(R.plurals.number_following, size, size)

            if (following.size <= 0) return@observe

            profileFollowing.setOnClickListener {
                Navigation.findNavController(view).navigate(
                    ProfileFragmentDirections.toFollowingFragment(argUid)
                )
            }
        }

        userViewModel.getFollowers(argUid).observe(viewLifecycleOwner) { followers ->
            followersLoaded = true
            show(progressBarOverlay)

            val size = followers.size

            profileFollowers.text =
                resources.getQuantityString(R.plurals.number_followers, size, size)

            if (followers.contains(uid)) {
                profileFollow.visibility = View.INVISIBLE
                profileUnfollow.visibility = View.VISIBLE
            }

            if (followers.size <= 0) return@observe

            profileFollowers.setOnClickListener {
                Navigation.findNavController(view).navigate(
                    ProfileFragmentDirections.toFollowersFragment(argUid)
                )
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
