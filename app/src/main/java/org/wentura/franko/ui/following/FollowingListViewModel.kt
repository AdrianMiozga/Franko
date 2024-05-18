package org.wentura.franko.ui.following

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.wentura.franko.data.User
import org.wentura.franko.data.UserRepository
import javax.inject.Inject

@HiltViewModel
class FollowingListViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val uid: String =
        savedStateHandle["uid"] ?: throw IllegalArgumentException("Missing uid")

    private val _following = MutableLiveData<List<User>>()
    val following: LiveData<List<User>> = _following

    init {
        viewModelScope.launch {
            val following = userRepository.getFollowing(uid).get().await()

            val followingIds = ArrayList<String>()

            following?.forEach { user -> followingIds.add(user.id) }

            if (followingIds.isEmpty()) {
                _following.value = listOf()
                return@launch
            }

            val users = userRepository.getUsers(followingIds).await()

            _following.value = ArrayList(users.toObjects())
        }
    }
}
