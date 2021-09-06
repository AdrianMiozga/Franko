package org.wentura.physicalapplication.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import org.wentura.physicalapplication.Constants
import org.wentura.physicalapplication.R
import org.wentura.physicalapplication.User

class SettingsFragment : PreferenceFragmentCompat() {
    private val db = Firebase.firestore

    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    companion object {
        const val TAG = "SettingsFragment"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        preferenceManager.preferenceDataStore = DataStore()

        val darkModeSwitch: SwitchPreferenceCompat? = preferenceManager.findPreference(Constants.DARK_MODE_KEY)
        val milesSwitch: SwitchPreferenceCompat? = preferenceManager.findPreference(Constants.MILES_KEY)

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { result ->
                val user = result.toObject<User>()

                darkModeSwitch?.let {
                    it.isChecked = user?.darkMode ?: false
                }

                milesSwitch?.let {
                    it.isChecked = user?.miles ?: false
                }
            }
    }
}
