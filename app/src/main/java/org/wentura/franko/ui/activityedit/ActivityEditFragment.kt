package org.wentura.franko.ui.activityedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.ui.activity.ActivityFragmentArgs

@AndroidEntryPoint
class ActivityEditFragment : PreferenceFragmentCompat() {

    private val activityViewModel: ActivityViewModel by viewModels()
    private val args: ActivityFragmentArgs by navArgs()

    companion object {
        val TAG = ActivityEditFragment::class.simpleName
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_activity, rootKey)

        preferenceManager.preferenceDataStore = DataStoreActivity(args.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_delete, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.delete -> {
                            ActivityDeleteDialogFragment(args.id, requireView())
                                .show(
                                    parentFragmentManager,
                                    ActivityDeleteDialogFragment::class.simpleName
                                )

                            return true
                        }
                        else -> return false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        val activityType: ListPreference? =
            preferenceManager.findPreference(Constants.ACTIVITY_TYPE_KEY)

        val whoCanSeeThisActivity: ListPreference? =
            preferenceManager.findPreference(Constants.WHO_CAN_SEE_THIS_ACTIVITY)

        val activityName: EditTextPreference? =
            preferenceManager.findPreference(Constants.ACTIVITY_NAME)

        activityViewModel.activity.observe(viewLifecycleOwner) { activity ->
            if (activity == null) return@observe

            activityType?.let {
                val index =
                    resources
                        .getStringArray(R.array.activities_array_values)
                        .indexOf(activity.activity)

                activityType.setValueIndex(if (index == -1) 0 else index)
            }

            whoCanSeeThisActivity?.let {
                val index =
                    resources
                        .getStringArray(R.array.who_can_see_activity_values)
                        .indexOf(activity.whoCanSeeThisActivity)

                whoCanSeeThisActivity.setValueIndex(if (index == -1) 0 else index)
            }

            activityName?.let { activityName.text = activity.activityName }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
