package org.wentura.franko.followers

import androidx.lifecycle.*
import com.google.firebase.firestore.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.wentura.franko.data.User
import org.wentura.franko.data.UserRepository
import javax.inject.Inject

@HiltViewModel
class FollowersListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository
) : ViewModel() {

    private val uid: String = savedStateHandle["uid"]
        ?: throw IllegalArgumentException("Missing uid")

    private val _followers = MutableLiveData<List<User>>()
    val followers: LiveData<List<User>> = _followers

    init {
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
    }
}
