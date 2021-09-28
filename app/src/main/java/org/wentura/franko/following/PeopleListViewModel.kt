package org.wentura.franko.following

import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.wentura.franko.data.User
import org.wentura.franko.data.UserRepository
import javax.inject.Inject

@HiltViewModel
class PeopleListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository
) : ViewModel() {

    private val uid: String = savedStateHandle["uid"] ?: throw IllegalArgumentException("Missing uid")

    private val _following = MutableLiveData<ArrayList<User>>()
    private val _followers = MutableLiveData<ArrayList<User>>()

    fun getFollowing(): LiveData<ArrayList<User>> {
        viewModelScope.launch {
            val following = userRepository
                .getFollowing(uid)
                .get()
                .await()

            val followingIds = ArrayList<String>()

            following?.forEach { user ->
                followingIds.add(user.id)
            }

            if (followingIds.isEmpty()) {
                _following.value = ArrayList()
                return@launch
            }

            val users = userRepository
                .getUsers(followingIds)
                .await()

            _following.value = ArrayList(users.toObjects())
        }

        return _following
    }

    fun getFollowers(): LiveData<ArrayList<User>> {
        viewModelScope.launch {
            val following = userRepository
                .getFollowers(uid)
                .get()
                .await()

            val followersIds = ArrayList<String>()

            following?.forEach { user ->
                followersIds.add(user.id)
            }

            if (followersIds.isEmpty()) {
                _followers.value = ArrayList()
                return@launch
            }

            val users = userRepository
                .getUsers(followersIds)
                .await()

            _followers.value = ArrayList(users.toObjects())
        }

        return _followers
    }
}
