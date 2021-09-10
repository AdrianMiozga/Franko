package org.wentura.physicalapplication.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import org.wentura.physicalapplication.Constants
import org.wentura.physicalapplication.R
import org.wentura.physicalapplication.User
import org.wentura.physicalapplication.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val args: ProfileFragmentArgs by navArgs()

    private val db = Firebase.firestore

    companion object {
        val TAG = ProfileFragment::class.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        db.collection(Constants.USERS)
            .document(args.uid)
            .get()
            .addOnSuccessListener { document ->
                val user: User = document.toObject() ?: return@addOnSuccessListener

                binding.profileProfilePicture.load(user.photoUrl) {
                    transformations(CircleCropTransformation())
                }

                binding.profileFullName.text = getString(
                    R.string.full_name,
                    user.firstName,
                    user.lastName
                )
            }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
