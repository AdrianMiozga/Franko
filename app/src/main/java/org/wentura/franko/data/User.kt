package org.wentura.franko.data

import com.google.firebase.firestore.DocumentId

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val bio: String = "",
    var following: ArrayList<String> = arrayListOf(),
    val city: String = "",
    val photoUrl: String? = null,
    val lastActivity: String? = null,
    val darkMode: Boolean? = null,
    val unitsOfMeasure: String? = null,
    val whoCanSeeMyProfile: String? = null,
    val whoCanSeeMyLocation: String? = null,
    val whoCanSeeMyFollowingCount: String? = null,
    val whoCanSeeActivityDefault: String? = null
) {
    @DocumentId
    lateinit var uid: String
}
