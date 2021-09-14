package org.wentura.franko.data

data class User(
    val uid: String? = null,
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
    val whoCanSeeMyFollowingCount: String? = null
)
