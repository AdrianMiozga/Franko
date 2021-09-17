package org.wentura.franko.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.databinding.FragmentProfileBinding
import org.wentura.franko.viewmodels.UserViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val args: ProfileFragmentArgs by navArgs()
    private val userViewModel: UserViewModel by viewModels()

    private val db = Firebase.firestore

    companion object {
        val TAG = ProfileFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProfileBinding.bind(view)

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val profileFollow = binding.profileFollow
        val profileUnfollow = binding.profileUnfollow
        val profileProfilePicture = binding.profileProfilePicture
        val profileFullName = binding.profileFullName
        val profileBio = binding.profileBio
        val profileCity = binding.profileCity
        val profileFollowing = binding.profileFollowing
        val profileFollowers = binding.profileFollowers

        profileFollow.setOnClickListener {
            if (uid == null) return@setOnClickListener

            // TODO: 17.09.2021 Queries should act like transactions
            db.collection(Constants.USERS)
                .document(uid)
                .collection(Constants.FOLLOWING)
                .document(args.uid)
                .set(hashMapOf(Constants.UID to args.uid))
                .addOnSuccessListener {
                    profileFollow.visibility = View.INVISIBLE
                    profileUnfollow.visibility = View.VISIBLE
                }

            db.collection(Constants.USERS)
                .document(args.uid)
                .collection(Constants.FOLLOWERS)
                .document(uid)
                .set(hashMapOf(Constants.UID to uid))
        }

        profileUnfollow.setOnClickListener {
            if (uid == null) return@setOnClickListener

            db.collection(Constants.USERS)
                .document(uid)
                .collection(Constants.FOLLOWING)
                .document(args.uid)
                .delete()
                .addOnSuccessListener {
                    profileUnfollow.visibility = View.INVISIBLE
                    profileFollow.visibility = View.VISIBLE
                }

            db.collection(Constants.USERS)
                .document(args.uid)
                .collection(Constants.FOLLOWERS)
                .document(uid)
                .delete()
        }

        userViewModel.getUser(args.uid).observe(viewLifecycleOwner) { profile ->
            if (profile.photoUrl.isNullOrBlank()) {
                profileProfilePicture.load(R.drawable.ic_profile_picture_placeholder) {
                    transformations(CircleCropTransformation())
                }
            } else {
                profileProfilePicture.load(profile.photoUrl) {
                    transformations(CircleCropTransformation())
                }
            }

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

            if (uid == null) return@observe

            if (args.uid == uid) {
                profileFollow.visibility = View.GONE
            }
        }

        userViewModel.getFollowing(args.uid).observe(viewLifecycleOwner) { following ->
            val size = following.size

            profileFollowing.text =
                resources.getQuantityString(R.plurals.number_following, size, size)

            if (following.size <= 0) return@observe

            profileFollowing.setOnClickListener {
                Navigation.findNavController(view).navigate(
                    ProfileFragmentDirections.toFollowingFragment(args.uid)
                )
            }
        }

        userViewModel.getFollowers(args.uid).observe(viewLifecycleOwner) { followers ->
            val size = followers.size

            profileFollowers.text =
                resources.getQuantityString(R.plurals.number_followers, size, size)

            if (followers.contains(uid)) {
                profileFollow.visibility = View.INVISIBLE
                profileUnfollow.visibility = View.VISIBLE
            }
        }
    }
}
