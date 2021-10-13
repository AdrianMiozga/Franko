package org.wentura.franko.profileedit

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.MainActivity
import org.wentura.franko.R
import org.wentura.franko.Utilities.loadProfilePicture
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
    private lateinit var firstNameInput: TextInputLayout
    private lateinit var bioInput: TextInputLayout

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

        firstNameInput = binding.profileEditFirstName
        val lastNameInput = binding.profileEditLastName
        bioInput = binding.profileEditBio
        val cityInput = binding.profileEditCity
        val editProfileProfilePicture = binding.profileEditProfilePicture
        val profileEditSignOut = binding.profileEditSignOut
        val profileEditDeleteAccount = binding.profileEditDeleteAccount

        firstNameInput.editText?.addTextChangedListener { editable ->
            if (editable.isNullOrBlank()) {
                firstNameInput.error = getString(R.string.field_can_not_be_empty)
            } else {
                firstNameInput.error = null
            }
        }

        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            this.user = user

            binding.apply {
                progressBarOverlay.progressBarOverlay.visibility = View.GONE

                profileEditFirstName.apply {
                    editText?.setText(user.firstName)
                    isHintAnimationEnabled = true
                }

                profileEditLastName.apply {
                    editText?.setText(user.lastName)
                    isHintAnimationEnabled = true
                }

                profileEditBio.apply {
                    editText?.setText(user.bio)
                    isHintAnimationEnabled = true
                }

                profileEditCity.apply {
                    editText?.setText(user.city)
                    isHintAnimationEnabled = true
                }
            }

            editProfileProfilePicture.loadProfilePicture(user.photoUrl)
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
                if (areInputsInvalid()) {
                    InvalidChangesDialogFragment(view).show(
                        parentFragmentManager,
                        InvalidChangesDialogFragment::class.simpleName
                    )

                    return@addCallback
                }

                // TODO: 01.10.2021 Compare changes in a cleaner way
                val firstName = firstNameInput.editText?.text.toString().trim()
                val lastName = lastNameInput.editText?.text.toString().trim()
                val bio = bioInput.editText?.text.toString().trim()
                val city = cityInput.editText?.text.toString().trim()

                if (user.firstName != firstName ||
                    user.lastName != lastName ||
                    user.bio != bio ||
                    user.city != city
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
                if (areInputsInvalid()) {
                    return true
                }

                saveObserver.save()
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun areInputsInvalid(): Boolean {
        return (firstNameInput.error != null ||
                (bioInput.editText?.text?.length ?: 0 > bioInput.counterMaxLength))
    }
}
