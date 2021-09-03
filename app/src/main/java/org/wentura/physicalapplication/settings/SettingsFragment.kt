package org.wentura.physicalapplication.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import org.wentura.physicalapplication.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
