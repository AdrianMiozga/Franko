package org.wentura.franko.profileedit

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.Utilities
import org.wentura.franko.data.User
import org.wentura.franko.data.UserRepository
import org.wentura.franko.databinding.FragmentProfileEditBinding
import org.wentura.franko.viewmodels.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ProfileEditFragment : Fragment(R.layout.fragment_profile_edit) {

    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var editProfileProfilePicture: ImageView
    private lateinit var profilePictureObserver: ProfilePictureObserver
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var user: User

    companion object {
        val TAG = ProfileEditFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProfileEditBinding.bind(view)

        firstNameEditText = binding.profileEditFirstName
        lastNameEditText = binding.profileEditLastName
        bioEditText = binding.profileEditBio
        cityEditText = binding.profileEditCity
        editProfileProfilePicture = binding.profileEditProfilePicture
        val profileEditDeleteAccount = binding.profileEditDeleteAccount

        profilePictureObserver = ProfilePictureObserver(requireContext(), requireActivity().activityResultRegistry)
        lifecycle.addObserver(profilePictureObserver)

        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            this.user = user

            binding.apply {
                progressBarOverlay.progressBarOverlay.visibility = View.GONE
                profileEditFirstName.setText(user.firstName)
                profileEditLastName.setText(user.lastName)
                profileEditCity.setText(user.city)
                profileEditBio.setText(user.bio)
            }

            Utilities.loadProfilePicture(user.photoUrl, editProfileProfilePicture)
        }

        profileEditDeleteAccount.setOnClickListener {
            AuthUI
                .getInstance()
                .delete(requireContext())
                .addOnSuccessListener {
                    Navigation.findNavController(view).navigateUp()

                    Toast.makeText(
                        requireContext(),
                        getString(R.string.account_deleted),
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        editProfileProfilePicture.setOnClickListener {
            ProfileEditPictureDialogFragment(profilePictureObserver)
                .show(
                    parentFragmentManager,
                    ProfileEditPictureDialogFragment::class.simpleName
                )
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
                            Utilities.closeKeyboard(view)
                            saveChanges()
                        }
                        .setNeutralButton(getString(R.string.discard)) { _, _ ->
                            Utilities.closeKeyboard(view)
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
        val updates: HashMap<String, Any> =
            hashMapOf(
                Constants.FIRST_NAME to firstNameEditText.text.toString().trim(),
                Constants.LAST_NAME to lastNameEditText.text.toString().trim(),
                Constants.BIO to bioEditText.text.toString().trim(),
                Constants.CITY to cityEditText.text.toString().trim()
            )

        userRepository.updateUser(updates)

        view?.let {
            Navigation.findNavController(it).navigateUp()
        }
    }
}
