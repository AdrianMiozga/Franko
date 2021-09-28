package org.wentura.franko

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val db = Firebase.firestore

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        this.onSignInResult(result)
    }

    companion object {
        private val TAG = MainActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment_container_view) as NavHostFragment

        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)

        if (FirebaseAuth.getInstance().currentUser == null) {
            createSignInIntent()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
//            .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser ?: return
            val photoUrl = Utilities.extractPhotoUrl(user)

            db.collection(Constants.USERS)
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) return@addOnSuccessListener

                    val set = hashMapOf(
                        Constants.FIRST_NAME to user.displayName,
                        Constants.PHOTO_URL to photoUrl
                    )

                    db.collection(Constants.USERS)
                        .document(user.uid)
                        .set(set)
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val name = getString(R.string.activity_recording_channel_name)
        val descriptionText = getString(R.string.recording_channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel =
            NotificationChannel(
                Constants.ACTIVITY_RECORDING_NOTIFICATION_CHANNEL_ID,
                name,
                importance
            ).apply {
                description = descriptionText
            }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
