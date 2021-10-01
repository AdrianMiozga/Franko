package org.wentura.franko.profileedit

import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import org.wentura.franko.Constants
import org.wentura.franko.data.UserRepository
import org.wentura.franko.databinding.FragmentProfileEditBinding

class SaveObserver(
    private val userRepository: UserRepository,
    private val view: View
) : DefaultLifecycleObserver {

    fun save() {
        val binding = FragmentProfileEditBinding.bind(view)

        val firstNameEditText = binding.profileEditFirstName
        val lastNameEditText = binding.profileEditLastName
        val bioEditText = binding.profileEditBio
        val cityEditText = binding.profileEditCity

        val updates: HashMap<String, Any> =
            hashMapOf(
                Constants.FIRST_NAME to firstNameEditText.text.toString().trim(),
                Constants.LAST_NAME to lastNameEditText.text.toString().trim(),
                Constants.BIO to bioEditText.text.toString().trim(),
                Constants.CITY to cityEditText.text.toString().trim()
            )

        userRepository.updateUser(updates)
    }
}
