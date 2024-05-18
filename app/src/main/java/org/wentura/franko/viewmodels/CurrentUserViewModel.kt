package org.wentura.franko.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import org.wentura.franko.data.User
import org.wentura.franko.data.UserRepository
import javax.inject.Inject

@HiltViewModel
class CurrentUserViewModel
@Inject
constructor(
    userRepository: UserRepository,
) : ViewModel() {

    companion object {
        val TAG = CurrentUserViewModel::class.simpleName
    }

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    init {
        userRepository.getUser().addSnapshotListener { documentSnapshot, exception ->
            if (exception != null) {
                Log.w(TAG, "Listen failed.", exception)
                return@addSnapshotListener
            }

            if (documentSnapshot == null) return@addSnapshotListener

            _user.value = documentSnapshot.toObject()!!
        }
    }
}
