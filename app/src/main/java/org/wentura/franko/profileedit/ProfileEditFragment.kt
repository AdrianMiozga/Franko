package org.wentura.franko.profileedit

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.MainActivity
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

    private lateinit var profilePictureObserver: ProfilePictureObserver
    private lateinit var saveObserver: SaveObserver
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var user: User

    companion object {
        val TAG = ProfileEditFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        profilePictureObserver =
            ProfilePictureObserver(
                requireContext(),
                requireActivity().activityResultRegistry
            )

        lifecycle.addObserver(profilePictureObserver)

        saveObserver = SaveObserver(userRepository, view)
        lifecycle.addObserver(saveObserver)

        val binding = FragmentProfileEditBinding.bind(view)

        val firstNameEditText = binding.profileEditFirstName
        val lastNameEditText = binding.profileEditLastName
        val bioEditText = binding.profileEditBio
        val cityEditText = binding.profileEditCity
        val editProfileProfilePicture = binding.profileEditProfilePicture
        val profileEditSignOut = binding.profileEditSignOut
        val profileEditDeleteAccount = binding.profileEditDeleteAccount

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

        profileEditSignOut.setOnClickListener {
            AuthUI
                .getInstance()
                .signOut(requireContext())
                .addOnSuccessListener {
                    (activity as MainActivity).createSignInIntent()
                }
        }

        profileEditDeleteAccount.setOnClickListener {
            AuthUI
                .getInstance()
                .delete(requireContext())
                .addOnSuccessListener {
                    findNavController().navigateUp()

                    Toast.makeText(
                        requireContext(),
                        getString(R.string.account_deleted),
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        editProfileProfilePicture.setOnClickListener {
            ProfileEditPictureDialogFragment(profilePictureObserver).show(
                parentFragmentManager,
                ProfileEditPictureDialogFragment::class.simpleName
            )
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                // TODO: 01.10.2021 Compare changes in a cleaner way
                if (user.firstName != firstNameEditText.text.toString().trim() ||
                    user.lastName != lastNameEditText.text.toString().trim() ||
                    user.bio != bioEditText.text.toString().trim() ||
                    user.city != cityEditText.text.toString().trim()
                ) {
                    UnsavedChangesDialogFragment(view, saveObserver).show(
                        parentFragmentManager,
                        UnsavedChangesDialogFragment::class.simpleName
                    )

                    return@addCallback
                }

                findNavController().navigateUp()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                saveObserver.save()
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
