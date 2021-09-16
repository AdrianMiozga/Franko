package org.wentura.franko.viewmodels

import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.wentura.franko.data.ActivityRepository
import org.wentura.franko.data.Path
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    companion object {
        val TAG = ActivityViewModel::class.simpleName
    }

    private val activityId: String = savedStateHandle["id"] ?: throw IllegalArgumentException("Missing uid")

    private val _activity = MutableLiveData<Path>()
    val activity: LiveData<Path> = _activity

    init {
        viewModelScope.launch {
            _activity.value = activityRepository.getActivity(activityId).toObject()
        }
    }
}
