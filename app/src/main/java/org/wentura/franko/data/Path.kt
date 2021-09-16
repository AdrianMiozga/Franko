package org.wentura.franko.data

import com.google.firebase.firestore.DocumentId

data class Path(
    val startTime: Long? = null,
    val endTime: Long? = null,
    val path: List<HashMap<String, Double>>? = null,
    val activity: String? = null,
    val activityName: String? = "",
    val whoCanSeeThisActivity: String? = null
) {
    @DocumentId
    lateinit var documentId: String
}
