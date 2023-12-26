package org.wentura.franko

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import coil.load
import coil.transform.CircleCropTransformation
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.wentura.franko.map.EnableLocationDialogFragment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

object Utilities {

    val TAG = Utilities::class.simpleName

    /**
     * Extract Google profile picture in original quality.
     *
     * @param [url] to extract
     * @return extracted url
     */
    fun extractGoogleProfilePicture(url: String): String {
        return url.removeSuffix("=s96-c")
    }

    fun extractPhotoUrl(user: FirebaseUser): String {
        var photoUrl = user.photoUrl?.toString() ?: ""

        for (profile in user.providerData) {
            if (profile.providerId == Constants.PROVIDER_GOOGLE) {
                photoUrl = extractGoogleProfilePicture(photoUrl)
            }
        }

        return photoUrl
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun InputStream.copyToFile(outputFile: File) {
        withContext(Dispatchers.IO) {
            use { input ->
                val outputStream = FileOutputStream(outputFile)

                outputStream.use { output ->
                    val bufferSize = ByteArray(4 * 1024)

                    while (true) {
                        val byteCount = input.read(bufferSize)

                        if (byteCount < 0) break

                        output.write(bufferSize, 0, byteCount)
                    }

                    output.flush()
                }
            }
        }
    }

    fun File.getUri(context: Context): Uri {
        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            this
        )
    }

    suspend fun createTmpFile(prefix: String, suffix: String, directory: File): File {
        val file: File

        withContext(Dispatchers.IO) {
            @Suppress("BlockingMethodInNonBlockingContext")
            file = File.createTempFile(
                prefix,
                suffix,
                directory
            ).apply {
                createNewFile()
                deleteOnExit()
            }
        }

        return file
    }

    fun closeKeyboard(view: View) {
        val inputMethodManager = view.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun isLocationPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    suspend fun checkLocationEnabled(context: Context, fragmentManager: FragmentManager) {
        val response = LocationServices
            .getSettingsClient(context)
            .checkLocationSettings(LocationSettingsRequest.Builder().build())
            .await()

        val locationSettingsStates = response.locationSettingsStates ?: return

        if (locationSettingsStates.isLocationUsable) return

        EnableLocationDialogFragment().show(
            fragmentManager,
            EnableLocationDialogFragment::class.simpleName
        )
    }

    fun ImageView.loadProfilePicture(photoUrl: String?) {
        if (photoUrl.isNullOrBlank()) {
            load(R.drawable.ic_profile_picture_placeholder) {
                transformations(CircleCropTransformation())
            }
        } else {
            load(photoUrl) {
                transformations(CircleCropTransformation())
            }
        }
    }

    fun getCurrentUserUid(): String {
        return FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("Current user UID does not exist")
    }

    fun createPolylineOptions(): PolylineOptions {
        return PolylineOptions()
            .width(Constants.LINE_WIDTH)
            .color(Constants.LINE_COLOR)
    }

    fun GoogleMap.setup(context: Context) {
        setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                context,
                R.raw.google_map_style
            )
        )

        setMaxZoomPreference(18f)
    }

    // TODO: 08.10.2021 Firebase stores points as List of HashMaps,
    //  I store as List of LatLng. This requires looping two times
    fun getBounds(points: List<LatLng>): LatLngBounds {
        if (points.isEmpty()) {
            val zero = LatLng(0.0, 0.0)
            return LatLngBounds(zero, zero)
        }

        val smallestLatitude = points.minByOrNull { it.latitude }!!.latitude
        val biggestLatitude = points.maxByOrNull { it.latitude }!!.latitude

        val smallestLongitude = points.minByOrNull { it.longitude }!!.longitude
        val biggestLongitude = points.maxByOrNull { it.longitude }!!.longitude

        val southWest = LatLng(smallestLatitude, smallestLongitude)
        val northEast = LatLng(biggestLatitude, biggestLongitude)

        return LatLngBounds(southWest, northEast)
    }

    fun formatTime(time: Long): String {
        var remaining = time
        var result = ""

        val hours = TimeUnit.MILLISECONDS.toHours(remaining)

        if (hours != 0L) {
            result += "${hours}h "
        }

        remaining = time - TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(remaining)

        result += if (minutes == 0L) {
            "1m"
        } else {
            "${minutes}m "
        }

        remaining = time - TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(remaining)

        result += "${seconds}s"

        return result.trim()
    }

    fun createSignInIntent(signInLauncher: ActivityResultLauncher<Intent>) {
        val providers = listOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI
            .getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.mipmap.ic_launcher_round)
//            .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
            .build()

        signInLauncher.launch(signInIntent)
    }
}
