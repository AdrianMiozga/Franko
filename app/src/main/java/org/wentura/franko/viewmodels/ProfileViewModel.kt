package org.wentura.franko.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import org.wentura.franko.Constants
import org.wentura.franko.data.User
import org.wentura.franko.data.UserRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        val TAG = ProfileViewModel::class.simpleName
    }

    private val user = MutableLiveData<User>()

    fun getUserAndFollowing(uid: String): LiveData<User> {
        userRepository
            .getUser(uid)
            .addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    Log.w(TAG, "Listen failed.", exception)
                    return@addSnapshotListener
                }

                val newUser: User = documentSnapshot?.toObject() ?: return@addSnapshotListener

                userRepository
                    .getFollowing(uid)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        querySnapshot.forEach { document ->
                            newUser.following.add(document[Constants.UID].toString())
                        }

                        user.value = newUser
                    }
            }

        userRepository
            .getFollowing(uid)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Log.w(TAG, "Listen failed.", exception)
                    return@addSnapshotListener
                }

                if (querySnapshot == null || querySnapshot.isEmpty) return@addSnapshotListener

                val newUser = user.value ?: return@addSnapshotListener

                newUser.following.clear()

                querySnapshot.forEach { document ->
                    newUser.following.add(document[Constants.UID].toString())
                }

                user.value = newUser
            }

        return user
    }
}
