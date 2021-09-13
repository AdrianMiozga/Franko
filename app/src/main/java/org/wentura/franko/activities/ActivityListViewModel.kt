package org.wentura.franko.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.wentura.franko.data.Path
import javax.inject.Inject

@HiltViewModel
class ActivityListViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _paths = MutableLiveData<ArrayList<Path>>()
    val paths: LiveData<ArrayList<Path>> = _paths

    init {
        viewModelScope.launch {
            _paths.value = ArrayList(activityRepository.getPaths().toObjects())
        }
    }
}
