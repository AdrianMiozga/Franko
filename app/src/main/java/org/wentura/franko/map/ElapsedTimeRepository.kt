package org.wentura.franko.map

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ElapsedTimeRepository @Inject constructor() {

    var elapsedTime = MutableLiveData<Long>()

    /** Milliseconds since epoch **/
    var initialTime = 0L
}
