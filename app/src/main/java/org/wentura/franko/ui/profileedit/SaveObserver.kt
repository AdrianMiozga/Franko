package org.wentura.franko.ui.profileedit

import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import org.wentura.franko.Constants
import org.wentura.franko.data.UserRepository
import org.wentura.franko.databinding.FragmentProfileEditBinding

class SaveObserver(
    private val userRepository: UserRepository,
    private val view: View,
) : DefaultLifecycleObserver {

    fun save() {
        val binding = FragmentProfileEditBinding.bind(view)

        val firstNameEditText = binding.profileEditFirstName.editText
        val lastNameEditText = binding.profileEditLastName.editText
        val bioEditText = binding.profileEditBio.editText
        val cityEditText = binding.profileEditCity.editText

        val updates: Map<String, Any> =
            hashMapOf(
                Constants.FIRST_NAME to firstNameEditText?.text.toString().trim(),
                Constants.LAST_NAME to lastNameEditText?.text.toString().trim(),
                Constants.BIO to bioEditText?.text.toString().trim(),
                Constants.CITY to cityEditText?.text.toString().trim()
            )

        userRepository.updateUser(updates)
    }
}
