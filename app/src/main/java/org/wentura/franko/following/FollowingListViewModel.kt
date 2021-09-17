package org.wentura.franko.following

import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.wentura.franko.Constants
import org.wentura.franko.data.User
import org.wentura.franko.data.UserRepository
import javax.inject.Inject

@HiltViewModel
class FollowingListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository
) : ViewModel() {

    private val uid: String = savedStateHandle["uid"] ?: throw IllegalArgumentException("Missing uid")

    private val _users = MutableLiveData<ArrayList<User>>()
    val users: LiveData<ArrayList<User>> = _users

    init {
        viewModelScope.launch {
            val following = userRepository
                .getFollowing(uid)
                .get()
                .await()

            val followingIds = ArrayList<String>()

            following?.forEach { user ->
                followingIds.add(user[Constants.UID].toString())
            }

            val users = userRepository
                .getUsers(followingIds)
                .await()

            _users.value = ArrayList(users.toObjects())
        }
    }
}
