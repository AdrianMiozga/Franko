package org.wentura.franko.activityedit

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.activity.ActivityFragmentArgs
import org.wentura.franko.viewmodels.ActivityViewModel

@AndroidEntryPoint
class ActivityEditFragment : PreferenceFragmentCompat() {

    private val activityViewModel: ActivityViewModel by viewModels()
    private val args: ActivityFragmentArgs by navArgs()

    companion object {
        val TAG = ActivityEditFragment::class.simpleName
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_delete, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                ActivityDeleteDialogFragment(args.id, requireView()).show(
                    parentFragmentManager,
                    ActivityDeleteDialogFragment::class.simpleName
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_activity, rootKey)

        preferenceManager.preferenceDataStore = DataStoreActivity(args.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        val activityType: ListPreference? =
            preferenceManager.findPreference(Constants.ACTIVITY_TYPE_KEY)

        val whoCanSeeThisActivity: ListPreference? =
            preferenceManager.findPreference(Constants.WHO_CAN_SEE_THIS_ACTIVITY)

        val activityName: EditTextPreference? =
            preferenceManager.findPreference(Constants.ACTIVITY_NAME)

        activityViewModel.getCurrentActivity().observe(viewLifecycleOwner) { activity ->
            if (activity == null) return@observe

            activityType?.let {
                val array = resources.getStringArray(R.array.activities_array)
                val index = array.indexOf(activity.activity)

                activityType.setValueIndex(if (index == -1) 0 else index)
            }

            whoCanSeeThisActivity?.let {
                val array = resources.getStringArray(R.array.who_can_see_activity)
                val index = array.indexOf(activity.whoCanSeeThisActivity)

                whoCanSeeThisActivity.setValueIndex(if (index == -1) 0 else index)
            }

            activityName?.let {
                activityName.text = activity.activityName
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
