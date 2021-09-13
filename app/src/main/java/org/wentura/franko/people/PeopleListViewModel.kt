package org.wentura.franko.people

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.launch
import org.wentura.franko.data.User

class PeopleListViewModel : ViewModel() {

    private val peopleRepository = PeopleRepository()

    private val _users = MutableLiveData<ArrayList<User>>()
    val users: LiveData<ArrayList<User>> = _users

    init {
        viewModelScope.launch {
            _users.value = ArrayList(peopleRepository.getPeople().toObjects())
        }
    }
}
