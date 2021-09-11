package org.wentura.physicalapplication.editprofile

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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import org.wentura.physicalapplication.Constants
import org.wentura.physicalapplication.R
import org.wentura.physicalapplication.User
import org.wentura.physicalapplication.Util
import org.wentura.physicalapplication.databinding.FragmentEditProfileBinding


class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private val db = Firebase.firestore

    private var fragmentEditProfileBinding: FragmentEditProfileBinding? = null

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
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
        val TAG = EditProfileFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentEditProfileBinding.bind(view)
        fragmentEditProfileBinding = binding

        firstNameEditText = binding.editProfileFirstName
        lastNameEditText = binding.editProfileLastName
        cityEditText = binding.editProfileCity
        editProfileProfilePicture = binding.editProfileProfilePicture

        db.collection(Constants.USERS)
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                user = document.toObject() ?: return@addOnSuccessListener

                binding.apply {
                    editProfileProfilePicture.load(user.photoUrl) {
                        transformations(CircleCropTransformation())
                    }

                    editProfileFirstName.setText(user.firstName)
                    editProfileLastName.setText(user.lastName)
                    editProfileCity.setText(user.city)
                }
            }

        editProfileProfilePicture.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle(R.string.edit_profile_picture)
                .setItems(R.array.edit_profile_choice_array) { _, which ->
                    if (which == 0) {
                        takePicture.launch(null)
                    } else if (which == 1) {
                        openGallery.launch(arrayOf("image/*"))
                    }
                }

            builder.create()
            builder.show()
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                if (user.firstName != firstNameEditText.text.toString() ||
                    user.lastName != lastNameEditText.text.toString() ||
                    user.city != cityEditText.text.toString()
                ) {
                    val builder = AlertDialog.Builder(requireContext())

                    builder.setTitle(getString(R.string.unsaved_changes))
                        .setMessage(getString(R.string.you_have_unsaved_changes))
                        .setPositiveButton(getString(R.string.save)) { _, _ ->
                            Util.closeKeyboard(view)

                            saveChanges()
                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ -> }

                    builder.create()
                    builder.show()

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
        inflater.inflate(R.menu.save_menu, menu)
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
            Constants.FIRST_NAME to firstNameEditText.text.toString(),
            Constants.LAST_NAME to lastNameEditText.text.toString(),
            Constants.CITY to cityEditText.text.toString()
        )

        db.collection(Constants.USERS)
            .document(uid)
            .update(updates as Map<String, Any>)

        view?.let {
            Navigation.findNavController(it).navigateUp()
        }
    }
}
