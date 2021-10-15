package org.wentura.franko.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import org.wentura.franko.Constants
import org.wentura.franko.Utilities.getCurrentUserUid
import org.wentura.franko.data.User
import org.wentura.franko.data.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    userRepository: UserRepository
) : ViewModel() {

    companion object {
        val TAG = UserViewModel::class.simpleName
    }

    private val uid: String = savedStateHandle["uid"]
        ?: getCurrentUserUid()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _following = MutableLiveData<List<String>>()
    val following: LiveData<List<String>> = _following

    private val _followers = MutableLiveData<List<String>>()
    val followers: LiveData<List<String>> = _followers

    init {
        userRepository
            .getUser(uid)
            .addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    Log.w(TAG, "Listen failed.", exception)
                    return@addSnapshotListener
                }

                val newUser: User = documentSnapshot?.toObject() ?: return@addSnapshotListener

                _user.value = newUser
            }

        userRepository
            .getFollowing(uid)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Log.w(TAG, "Listen failed.", exception)
                    return@addSnapshotListener
                }

                val newFollowing = ArrayList<String>()

                querySnapshot?.forEach { document ->
                    newFollowing.add(document[Constants.UID].toString())
                }

                _following.value = newFollowing
            }

        userRepository
            .getFollowers(uid)
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Log.w(TAG, "Listen failed.", exception)
                    return@addSnapshotListener
                }

                val newFollowers = ArrayList<String>()

                querySnapshot?.forEach { document ->
                    newFollowers.add(document[Constants.UID].toString())
                }

                _followers.value = newFollowers
            }
    }
}
