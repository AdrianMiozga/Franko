package org.wentura.franko

data class Path(
    val startTime: Long? = null,
    val endTime: Long? = null,
    val path: List<HashMap<String, Double>>? = null,
    val activity: String? = null
)
