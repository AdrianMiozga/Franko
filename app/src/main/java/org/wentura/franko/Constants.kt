package org.wentura.franko

object Constants {

    // Firestore Paths
    const val USERS = "users"
    const val ACTIVITIES = "activities"
    const val FOLLOWING = "following"
    const val FOLLOWERS = "followers"

    // Firestore Fields
    const val UID = "uid"
    const val FIRST_NAME = "firstName"
    const val LAST_NAME = "lastName"
    const val BIO = "bio"
    const val LAST_ACTIVITY = "lastActivity"
    const val CITY = "city"
    const val PHOTO_URL = "photoUrl"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
    const val SPEED = "speed"
    const val ACTIVITY = "activity"
    const val END_TIME = "endTime"

    // Firestore Values
    const val EVERYONE = "Everyone"
    const val WALKING = "Walking"
    const val RUNNING = "Running"
    const val BIKE = "Bike"

    // Storage Paths
    const val IMAGES = "images"

    // Setting Keys
    const val DARK_MODE_KEY = "darkMode"
    const val WHO_CAN_SEE_MY_PROFILE = "whoCanSeeMyProfile"
    const val WHO_CAN_SEE_MY_LOCATION = "whoCanSeeMyLocation"
    const val ACTIVITY_TYPE_KEY = ACTIVITY
    const val WHO_CAN_SEE_THIS_ACTIVITY = "whoCanSeeThisActivity"
    const val ACTIVITY_NAME = "activityName"
    const val WHO_CAN_SEE_ACTIVITY_DEFAULT = "whoCanSeeActivityDefault"
    const val KEEP_SCREEN_ON_IN_MAP = "keepScreenOnInMap"
    const val VERSION_KEY = "versionKey"

    // Map Settings
    const val DEFAULT_ZOOM = 17F
    const val LINE_WIDTH = 12F

    //    const val LINE_COLOR = Color.BLUE
    const val LINE_COLOR = 0xFF7E57C2.toInt()

    // Activity Types
    val ACTIVITY_TYPES = listOf(BIKE, RUNNING, WALKING)

    // Notification
    const val ACTIVITY_TRACKING_NOTIFICATION_ID = 1
    const val ACTIVITY_RECORDING_NOTIFICATION_CHANNEL_ID = "activityRecordingNotificationChannel"

    // Providers
    const val PROVIDER_GOOGLE = "google.com"

    // Result Registry Keys
    const val SELECT_IMAGE_KEY = "selectImageKey"
    const val TAKE_PICTURE_KEY = "takePictureKey"
    const val REQUEST_PERMISSIONS_KEY = "requestPermissionsKey"

    // Pager Indexes
    const val PROFILE_PAGE_INDEX = 0
    const val ACTIVITIES_PAGE_INDEX = 1

    // Other
    const val PROFILE_PICTURE_FORMAT_EXTENSION = "webp"
    const val TMP_IMAGE_PREFIX = "tmp_image_file_"
    const val TMP_IMAGE_SUFFIX = ".png"

    /** Min activity time to save in milliseconds * */
    //    val MIN_ACTIVITY_TIME = TimeUnit.MINUTES.toMillis(1)

    /** Meters per second to kilometers per hour * */
    const val MS_TO_KMH = 3.6
}
