package org.wentura.franko.profile

import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.wentura.franko.data.User
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val uid: String = savedStateHandle["uid"] ?: throw IllegalArgumentException("Missing uid")

    private val _profile = MutableLiveData<User>()
    val profile: LiveData<User> = _profile

    init {
        viewModelScope.launch {
            _profile.value = profileRepository.getProfile(uid).toObject<User>()
        }
    }
}
