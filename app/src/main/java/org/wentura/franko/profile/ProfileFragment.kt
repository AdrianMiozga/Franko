package org.wentura.franko.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var fragmentProfileBinding: FragmentProfileBinding? = null

    private val args: ProfileFragmentArgs by navArgs()
    private val profileViewModel: ProfileViewModel by viewModels()

    private val db = Firebase.firestore

    companion object {
        val TAG = ProfileFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProfileBinding.bind(view)
        fragmentProfileBinding = binding

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val profileFollow = binding.profileFollow
        val profileUnfollow = binding.profileUnfollow
        val profileProfilePicture = binding.profileProfilePicture
        val profileFullName = binding.profileFullName
        val profileBio = binding.profileBio
        val profileCity = binding.profileCity
        val profileFollowing = binding.profileFollowing

        profileFollow.setOnClickListener {
            if (uid == null) return@setOnClickListener

            db.collection(Constants.USERS)
                .document(uid)
                .collection(Constants.FOLLOWING)
                .document(args.uid)
                .set(hashMapOf(Constants.UID to args.uid))
                .addOnSuccessListener {
                    profileFollow.visibility = View.GONE
                    profileUnfollow.visibility = View.VISIBLE
                }
        }

        profileUnfollow.setOnClickListener {
            if (uid == null) return@setOnClickListener

            db.collection(Constants.USERS)
                .document(uid)
                .collection(Constants.FOLLOWING)
                .document(args.uid)
                .delete()
                .addOnSuccessListener {
                    profileUnfollow.visibility = View.GONE
                    profileFollow.visibility = View.VISIBLE
                }
        }

        profileViewModel.profile.observe(viewLifecycleOwner) { profile ->
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

            if (profile.uid == FirebaseAuth.getInstance().currentUser?.uid) {
                profileFollow.visibility = View.GONE
            }

            if (profile.following.contains(args.uid)) {
                profileFollow.visibility = View.GONE
                profileUnfollow.visibility = View.VISIBLE
            }

            val followingEveryone = resources.getStringArray(R.array.who_can_see_my_following_count)[0]

            if (profile.whoCanSeeMyFollowingCount != followingEveryone) {
                profileFollowing.visibility = View.GONE
            } else {
                val size = profile.following.size

                profileFollowing.text =
                    resources.getQuantityString(R.plurals.number_following, size, size)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentProfileBinding = null
    }
}
