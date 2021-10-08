package org.wentura.franko.activityedit

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.R
import org.wentura.franko.data.ActivityRepository
import javax.inject.Inject

@AndroidEntryPoint
class ActivityDeleteDialogFragment(
    private val activityId: String,
    private val activityView: View
) : DialogFragment() {

    @Inject
    lateinit var activityRepository: ActivityRepository

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog
            .Builder(requireContext())
            .setMessage(getString(R.string.delete_activity_dialog_message))
            .setPositiveButton(R.string.delete) { _, _ ->
                activityRepository
                    .deleteActivity(activityId)
                    .addOnSuccessListener {
                        val toProfileViewPagerFragment =
                            ActivityEditFragmentDirections.toProfileViewPagerFragment()

                        Navigation.findNavController(activityView)
                            .navigate(toProfileViewPagerFragment)
                    }
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .create()
    }
}
