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
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        val TAG = UserViewModel::class.simpleName
    }

    private val user = MutableLiveData<User>()
    private val following = MutableLiveData<List<String>>()
    private val followers = MutableLiveData<List<String>>()

    fun getUser(): LiveData<User> {
        userRepository
            .getUser()
            .addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    Log.w(TAG, "Listen failed.", exception)
                    return@addSnapshotListener
                }

                if (documentSnapshot == null) return@addSnapshotListener

                user.value = documentSnapshot.toObject()
            }

        return user
    }

    fun getUser(uid: String): LiveData<User> {
        userRepository
            .getUser(uid)
            .addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    Log.w(TAG, "Listen failed.", exception)
                    return@addSnapshotListener
                }

                val newUser: User = documentSnapshot?.toObject() ?: return@addSnapshotListener

                user.value = newUser
            }

        return user
    }

    fun getFollowing(uid: String): LiveData<List<String>> {
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

                following.value = newFollowing
            }

        return following
    }

    fun getFollowers(uid: String): LiveData<List<String>> {
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

                followers.value = newFollowers
            }

        return followers
    }
}
