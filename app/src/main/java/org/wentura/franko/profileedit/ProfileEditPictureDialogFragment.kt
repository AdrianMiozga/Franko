package org.wentura.franko.profileedit

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.R
import org.wentura.franko.data.UserRepository
import javax.inject.Inject

@AndroidEntryPoint
class ProfileEditPictureDialogFragment : DialogFragment() {

    @Inject
    lateinit var userRepository: UserRepository

    private lateinit var profilePictureObserver: ProfilePictureObserver

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        profilePictureObserver = ProfilePictureObserver(requireActivity().activityResultRegistry)
        lifecycle.addObserver(profilePictureObserver)

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.edit_profile_picture)
            .setItems(R.array.profile_edit_choice_array) { _, which ->
                when (which) {
                    0 -> {
                        profilePictureObserver.takePicture()
                    }
                    1 -> {
                        profilePictureObserver.selectImage()
                    }
                    2 -> {
                        userRepository.removeProfilePicture()
                    }
                }
            }
            .create()
    }
}
