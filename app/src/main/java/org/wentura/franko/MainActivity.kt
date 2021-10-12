package org.wentura.franko

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.data.UserRepository
import org.wentura.franko.databinding.ActivityMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        onSignInResult(result)
    }

    private val onPreDrawListener = ViewTreeObserver.OnPreDrawListener { false }

    private lateinit var content: View

    companion object {
        val TAG = MainActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        createNotificationChannel()

        content = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(onPreDrawListener)

        if (FirebaseAuth.getInstance().currentUser == null) {
            createSignInIntent()
        } else {
            setupUi()
        }
    }

    private fun setupUi() {
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        content.viewTreeObserver.removeOnPreDrawListener(onPreDrawListener)

        val navHostFragment =
            supportFragmentManager
                .findFragmentById(R.id.main_fragment_container_view) as NavHostFragment

        val navController = navHostFragment.navController

        val bottomNavigation = binding.bottomNavigation

        bottomNavigation.setupWithNavController(navController)

        val topLevelDestinations = setOf(
            R.id.home_fragment,
            R.id.map_fragment,
            R.id.people_fragment,
            R.id.view_pager_fragment
        )

        val appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    fun createSignInIntent() {
        val providers = listOf(
//            AuthUI.IdpConfig.EmailBuilder().build(),
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

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse

        if (result.resultCode != RESULT_OK) {
            // User pressed back button
            if (response == null) {
                finish()
            }

            return
        }

        setupUi()

        val user = FirebaseAuth.getInstance().currentUser ?: return
        val photoUrl = Utilities.extractPhotoUrl(user)

        val values: HashMap<String, Any> =
            hashMapOf(
                Constants.FIRST_NAME to (user.displayName ?: ""),
                Constants.PHOTO_URL to photoUrl
            )

        userRepository.addNewUser(values)
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
