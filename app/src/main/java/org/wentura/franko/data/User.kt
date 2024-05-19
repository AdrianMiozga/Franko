package org.wentura.franko.data

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentId

@Keep
data class User(
    val firstName: String = "",
    val lastName: String = "",
    val bio: String = "",
    val city: String = "",
    val photoUrl: String? = null,
    val lastActivity: String? = null,
    val darkMode: Boolean = false,
    val unitsOfMeasure: String? = null,
    val whoCanSeeMyProfile: String? = null,
    val whoCanSeeMyLocation: String? = null,
    val whoCanSeeMyFollowingCount: String? = null,
    val whoCanSeeActivityDefault: String? = null,
    val keepScreenOnInMap: Boolean = false,
) {
    @DocumentId lateinit var uid: String
}
