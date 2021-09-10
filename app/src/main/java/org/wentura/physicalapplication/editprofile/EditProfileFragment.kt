package org.wentura.physicalapplication.editprofile

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
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
import org.wentura.physicalapplication.databinding.FragmentEditProfileBinding


class EditProfileFragment : Fragment() {

    private val db = Firebase.firestore

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val view = binding.root

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

        setHasOptionsMenu(true)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                val updates = hashMapOf(
                    Constants.FIRST_NAME to firstNameEditText.text.toString(),
                    Constants.LAST_NAME to lastNameEditText.text.toString(),
                    Constants.CITY to cityEditText.text.toString()
                )

                db.collection(Constants.USERS)
                    .document(uid)
                    .update(updates as Map<String, Any>)

                view?.let {
                    Navigation.findNavController(it)
                        .navigate(EditProfileFragmentDirections.toMainFragment())
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
