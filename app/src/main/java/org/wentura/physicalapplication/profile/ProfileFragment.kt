package org.wentura.physicalapplication.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import org.wentura.physicalapplication.Constants
import org.wentura.physicalapplication.R
import org.wentura.physicalapplication.User
import org.wentura.physicalapplication.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var fragmentProfileBinding: FragmentProfileBinding? = null

    private val args: ProfileFragmentArgs by navArgs()

    private val db = Firebase.firestore

    private lateinit var user: User

    companion object {
        val TAG = ProfileFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProfileBinding.bind(view)
        fragmentProfileBinding = binding

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        binding.profileFollow.setOnClickListener {
            if (uid == null) return@setOnClickListener

            db.collection(Constants.USERS)
                .document(uid)
                .collection(Constants.FOLLOWERS)
                .document(args.uid)
                .set(hashMapOf(Constants.UID to args.uid))
                .addOnSuccessListener {
                    binding.profileFollow.visibility = View.GONE
                    binding.profileUnfollow.visibility = View.VISIBLE
                }
        }

        binding.profileUnfollow.setOnClickListener {
            if (uid == null) return@setOnClickListener

            db.collection(Constants.USERS)
                .document(uid)
                .collection(Constants.FOLLOWERS)
                .document(args.uid)
                .delete()
                .addOnSuccessListener {
                    binding.profileUnfollow.visibility = View.GONE
                    binding.profileFollow.visibility = View.VISIBLE
                }
        }

        db.collection(Constants.USERS)
            .document(args.uid)
            .get()
            .addOnSuccessListener { document ->
                user = document.toObject() ?: return@addOnSuccessListener

                if (user.photoUrl == null) {
                    binding.profileProfilePicture.load(R.drawable.profile_picture_placeholder) {
                        transformations(CircleCropTransformation())
                    }
                } else {
                    binding.profileProfilePicture.load(user.photoUrl) {
                        transformations(CircleCropTransformation())
                    }
                }

                binding.profileFullName.text = getString(
                    R.string.full_name,
                    user.firstName,
                    user.lastName
                )

                val everyone = resources.getStringArray(R.array.who_can_see_my_location)[0]

                if (user.whoCanSeeMyLocation == everyone) {
                    binding.profileCity.text = user.city
                } else {
                    binding.profileCity.visibility = View.GONE
                }

                if (user.uid == FirebaseAuth.getInstance().currentUser?.uid) {
                    binding.profileFollow.visibility = View.GONE
                }
            }

        db.collection(Constants.USERS)
            .document(args.uid)
            .collection(Constants.FOLLOWERS)
            .get()
            .addOnSuccessListener { collection ->
                val everyone = resources.getStringArray(R.array.who_can_see_my_following_count)[0]

                if (user.whoCanSeeMyFollowingCount != everyone) {
                    binding.profileFollowing.visibility = View.GONE
                    return@addOnSuccessListener
                }

                val size = collection.size()

                binding.profileFollowing.text =
                    resources.getQuantityString(R.plurals.d_following, size, size)
            }

        if (uid == null) return

        db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.FOLLOWERS)
            .whereEqualTo(Constants.UID, args.uid)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) return@addOnSuccessListener

                binding.profileFollow.visibility = View.GONE
                binding.profileUnfollow.visibility = View.VISIBLE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentProfileBinding = null
    }
}
