package org.wentura.physicalapplication

data class User(
    val uid: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val city: String = "",
    val photoUrl: String? = null,
    val lastActivity: String? = null,
    val darkMode: Boolean? = null,
    val miles: Boolean? = null,
    val whoCanSeeMyProfile: String? = null,
    val whoCanSeeMyLocation: String? = null
)
