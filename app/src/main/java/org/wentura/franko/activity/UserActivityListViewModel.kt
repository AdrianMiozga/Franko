package org.wentura.franko.activity

import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.wentura.franko.data.*
import javax.inject.Inject

@HiltViewModel
class UserActivityListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    companion object {
        val TAG = UserActivityListViewModel::class.simpleName
    }

    private val _userActivity = MutableLiveData<UserActivity>()
    val userActivity: LiveData<UserActivity> = _userActivity

    private val activityId: String = savedStateHandle["id"]
        ?: throw IllegalArgumentException("Missing uid")

    init {
        viewModelScope.launch {
            val activitySnapshot = activityRepository
                .getActivity(activityId)
                .get()
                .await()

            val activity: Activity = activitySnapshot.toObject() ?: return@launch

            val uid = activity.uid ?: return@launch

            val userSnapshot = userRepository
                .getUser(uid)
                .get()
                .await()

            val user: User = userSnapshot.toObject() ?: return@launch

            _userActivity.value = UserActivity(user, activity)
        }
    }
}
