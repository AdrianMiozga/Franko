package org.wentura.franko.profileedit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import org.wentura.franko.activities.ActivityListViewModel
import org.wentura.franko.data.User
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val user = MutableLiveData<User>()

    fun getUser(): LiveData<User> {
        userRepository
            .getUser()
            .addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    Log.w(ActivityListViewModel.TAG, "Listen failed.", exception)
                    return@addSnapshotListener
                }

                if (documentSnapshot == null) return@addSnapshotListener

                user.value = documentSnapshot.toObject()
            }

        return user
    }
}
