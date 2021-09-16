package org.wentura.franko.profileedit

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.Util
import org.wentura.franko.data.User
import org.wentura.franko.databinding.FragmentProfileEditBinding

class ProfileEditFragment : Fragment(R.layout.fragment_profile_edit) {

    private val db = Firebase.firestore

    private var fragmentEditProfileBinding: FragmentProfileEditBinding? = null

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var editProfileProfilePicture: ImageView

    private lateinit var user: User

    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap == null) return@registerForActivityResult

        editProfileProfilePicture.load(bitmap)

        ProfilePictureUploader().updateProfilePicture(bitmap)
    }

    private val openGallery = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@registerForActivityResult

        editProfileProfilePicture.load(uri)

        ProfilePictureUploader().updateProfilePicture(uri)
    }

    companion object {
        val TAG = ProfileEditFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProfileEditBinding.bind(view)
        fragmentEditProfileBinding = binding

        firstNameEditText = binding.profileEditFirstName
        lastNameEditText = binding.profileEditLastName
        bioEditText = binding.profileEditBio
        cityEditText = binding.profileEditCity
        editProfileProfilePicture = binding.profileEditProfilePicture

        db.collection(Constants.USERS)
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                user = document.toObject() ?: return@addOnSuccessListener

                if (user.photoUrl.isNullOrBlank()) {
                    editProfileProfilePicture.load(R.drawable.ic_profile_picture_placeholder) {
                        transformations(CircleCropTransformation())
                    }
                } else {
                    editProfileProfilePicture.load(user.photoUrl) {
                        transformations(CircleCropTransformation())
                    }
                }

                binding.apply {
                    profileEditFirstName.setText(user.firstName)
                    profileEditLastName.setText(user.lastName)
                    profileEditCity.setText(user.city)
                    profileEditBio.setText(user.bio)
                }
            }

        editProfileProfilePicture.setOnClickListener {
            AlertDialog
                .Builder(requireContext())
                .setTitle(R.string.edit_profile_picture)
                .setItems(R.array.profile_edit_choice_array) { _, which ->
                    when (which) {
                        0 -> {
                            takePicture.launch(null)
                        }
                        1 -> {
                            openGallery.launch(arrayOf("image/*"))
                        }
                        2 -> {
                            val updates: Map<String, Any> =
                                hashMapOf(Constants.PHOTO_URL to FieldValue.delete())

                            db.collection(Constants.USERS)
                                .document(uid)
                                .update(updates)

                            val imagesRef = Firebase.storage.reference.child(Constants.IMAGES)
                            val thisImageRef = imagesRef.child("$uid.png")

                            thisImageRef.delete()
                        }
                    }
                }
                .create()
                .show()
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                if (user.firstName != firstNameEditText.text.toString().trim() ||
                    user.lastName != lastNameEditText.text.toString().trim() ||
                    user.bio != bioEditText.text.toString().trim() ||
                    user.city != cityEditText.text.toString().trim()
                ) {
                    AlertDialog
                        .Builder(requireContext())
                        .setTitle(getString(R.string.unsaved_changes))
                        .setMessage(getString(R.string.you_have_unsaved_changes))
                        .setPositiveButton(getString(R.string.save)) { _, _ ->
                            Util.closeKeyboard(view)
                            saveChanges()
                        }
                        .setNeutralButton(getString(R.string.discard)) { _, _ ->
                            Util.closeKeyboard(view)
                            Navigation.findNavController(view).navigateUp()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
                        .create()
                        .show()

                    return@addCallback
                }

                Navigation.findNavController(view).navigateUp()
            }

        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentEditProfileBinding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                saveChanges()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveChanges() {
        val updates = hashMapOf(
            Constants.FIRST_NAME to firstNameEditText.text.toString().trim(),
            Constants.LAST_NAME to lastNameEditText.text.toString().trim(),
            Constants.BIO to bioEditText.text.toString().trim(),
            Constants.CITY to cityEditText.text.toString().trim()
        )

        db.collection(Constants.USERS)
            .document(uid)
            .update(updates as Map<String, Any>)

        view?.let {
            Navigation.findNavController(it).navigateUp()
        }
    }
}
