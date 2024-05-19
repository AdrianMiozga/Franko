package org.wentura.franko.data

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentId

@Keep
data class Activity(
    val uid: String? = null,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val path: List<HashMap<String, Double>>? = null,
    val activity: String? = null,
    val activityName: String? = "",
    val whoCanSeeThisActivity: String? = null,
    val length: Float = 0f,
    val maxSpeed: Double = 0.0,
) {
    @DocumentId lateinit var documentId: String
}
