package org.wentura.franko.activity

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

    private val pathId: String = savedStateHandle["id"] ?: throw IllegalArgumentException("Missing uid")

    private val _path = MutableLiveData<Path>()
    val path: LiveData<Path> = _path

    init {
        viewModelScope.launch {
            _path.value = activityRepository.getActivity(pathId).toObject()
        }
    }
}
