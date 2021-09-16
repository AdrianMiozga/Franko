package org.wentura.franko.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.viewmodels.UserViewModel

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private val userViewModel: UserViewModel by viewModels()

    companion object {
        val TAG = SettingsFragment::class.simpleName
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_user, rootKey)

        preferenceManager.preferenceDataStore = DataStore()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val darkModeSwitch: SwitchPreferenceCompat? = preferenceManager.findPreference(Constants.DARK_MODE_KEY)
        val keepScreenOnInMapSwitch: SwitchPreferenceCompat? =
            preferenceManager.findPreference(Constants.KEEP_SCREEN_ON_IN_MAP)
        val unitsOfMeasure: ListPreference? = preferenceManager.findPreference(Constants.UNITS_OF_MEASURE_KEY)
        val whoCanSeeMyProfile: ListPreference? = preferenceManager.findPreference(Constants.WHO_CAN_SEE_MY_PROFILE)
        val whoCanSeeMyLocation: ListPreference? = preferenceManager.findPreference(Constants.WHO_CAN_SEE_MY_LOCATION)
        val whoCanSeeMyFollowingCount: ListPreference? =
            preferenceManager.findPreference(Constants.WHO_CAN_SEE_MY_FOLLOWING_COUNT)
        val whoCanSeeActivityDefault: ListPreference? =
            preferenceManager.findPreference(Constants.WHO_CAN_SEE_ACTIVITY_DEFAULT)

        userViewModel.getUser().observe(viewLifecycleOwner) { user ->
            darkModeSwitch?.let {
                it.isChecked = user.darkMode
            }

            keepScreenOnInMapSwitch?.let {
                it.isChecked = user.keepScreenOnInMap
            }

            unitsOfMeasure?.let {
                val array = resources.getStringArray(R.array.units_of_measure_array)
                val index = array.indexOf(user.unitsOfMeasure)

                unitsOfMeasure.setValueIndex(if (index == -1) 0 else index)
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

            whoCanSeeActivityDefault?.let {
                val array = resources.getStringArray(R.array.who_can_see_activity)
                val index = array.indexOf(user.whoCanSeeActivityDefault)

                whoCanSeeActivityDefault.setValueIndex(if (index == -1) 0 else index)
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
