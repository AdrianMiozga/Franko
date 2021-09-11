package org.wentura.physicalapplication.settings

import android.os.Bundle
import androidx.preference.ListPreference
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
        val TAG = SettingsFragment::class.simpleName
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        preferenceManager.preferenceDataStore = DataStore()

        val darkModeSwitch: SwitchPreferenceCompat? = preferenceManager.findPreference(Constants.DARK_MODE_KEY)
        val milesSwitch: SwitchPreferenceCompat? = preferenceManager.findPreference(Constants.MILES_KEY)
        val whoCanSeeMyProfile: ListPreference? = preferenceManager.findPreference(Constants.WHO_CAN_SEE_MY_PROFILE)
        val whoCanSeeMyLocation: ListPreference? = preferenceManager.findPreference(Constants.WHO_CAN_SEE_MY_LOCATION)
        val whoCanSeeMyFollowingCount: ListPreference? =
            preferenceManager.findPreference(Constants.WHO_CAN_SEE_MY_FOLLOWING_COUNT)

        db.collection(Constants.USERS)
            .document(uid)
            .get()
            .addOnSuccessListener { result ->
                val user = result.toObject<User>() ?: return@addOnSuccessListener

                darkModeSwitch?.let {
                    it.isChecked = user.darkMode ?: false
                }

                milesSwitch?.let {
                    it.isChecked = user.miles ?: false
                }

                whoCanSeeMyProfile?.let {
                    val array = resources.getStringArray(R.array.who_can_see_my_profile)
                    val index = array.indexOf(user.whoCanSeeMyProfile)

                    whoCanSeeMyProfile.setValueIndex(if (index == -1) 0 else index)
                }

                whoCanSeeMyLocation?.let {
                    val array = resources.getStringArray(R.array.who_can_see_my_location)
                    val index = array.indexOf(user.whoCanSeeMyLocation)

                    whoCanSeeMyLocation.setValueIndex(if (index == -1) 0 else index)
                }

                whoCanSeeMyFollowingCount?.let {
                    val array = resources.getStringArray(R.array.who_can_see_my_following_count)
                    val index = array.indexOf(user.whoCanSeeMyFollowingCount)

                    whoCanSeeMyFollowingCount.setValueIndex(if (index == -1) 0 else index)
                }
            }
    }
}
