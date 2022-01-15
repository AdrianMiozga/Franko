package org.wentura.franko.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.BuildConfig
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.viewmodels.CurrentUserViewModel

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val currentUserViewModel: CurrentUserViewModel by viewModels()

    companion object {
        val TAG = SettingsFragment::class.simpleName
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_user, rootKey)

        preferenceManager.preferenceDataStore = DataStore()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val darkModeSwitch: SwitchPreferenceCompat? =
            preferenceManager.findPreference(Constants.DARK_MODE_KEY)

        val keepScreenOnInMapSwitch: SwitchPreferenceCompat? =
            preferenceManager.findPreference(Constants.KEEP_SCREEN_ON_IN_MAP)

        val whoCanSeeMyProfile: ListPreference? =
            preferenceManager.findPreference(Constants.WHO_CAN_SEE_MY_PROFILE)

        val whoCanSeeMyLocation: ListPreference? =
            preferenceManager.findPreference(Constants.WHO_CAN_SEE_MY_LOCATION)

        val whoCanSeeActivityDefault: ListPreference? =
            preferenceManager.findPreference(Constants.WHO_CAN_SEE_ACTIVITY_DEFAULT)

        val version: Preference? =
            preferenceManager.findPreference(Constants.VERSION_KEY)

        version?.summary = BuildConfig.VERSION_NAME

        currentUserViewModel.user.observe(viewLifecycleOwner) { user ->
            darkModeSwitch?.let {
                it.isChecked = user.darkMode
            }

            keepScreenOnInMapSwitch?.let {
                it.isChecked = user.keepScreenOnInMap
            }

            whoCanSeeMyProfile?.let {
                val index = resources
                    .getStringArray(R.array.who_can_see_my_profile_values)
                    .indexOf(user.whoCanSeeMyProfile)

                whoCanSeeMyProfile.setValueIndex(if (index == -1) 0 else index)
            }

            whoCanSeeMyLocation?.let {
                val index = resources
                    .getStringArray(R.array.who_can_see_my_location_values)
                    .indexOf(user.whoCanSeeMyLocation)

                whoCanSeeMyLocation.setValueIndex(if (index == -1) 0 else index)
            }

            whoCanSeeActivityDefault?.let {
                val index = resources
                    .getStringArray(R.array.who_can_see_activity_values)
                    .indexOf(user.whoCanSeeActivityDefault)

                whoCanSeeActivityDefault.setValueIndex(if (index == -1) 0 else index)
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
