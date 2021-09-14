package org.wentura.franko.activities

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import org.wentura.franko.data.ActivityRepository
import org.wentura.franko.data.Path
import javax.inject.Inject

@HiltViewModel
class ActivityListViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    companion object {
        val TAG = ActivityListViewModel::class.simpleName
    }

    private val paths = MutableLiveData<ArrayList<Path>>()

    fun getCurrentPaths(): LiveData<ArrayList<Path>> {
        activityRepository
            .getPaths()
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Log.w(TAG, "Listen failed.", exception)
                    return@addSnapshotListener
                }

                if (querySnapshot == null || querySnapshot.isEmpty) return@addSnapshotListener

                paths.value = ArrayList(querySnapshot.toObjects())
            }

        return paths
    }
}
