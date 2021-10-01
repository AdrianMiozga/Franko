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
    // TODO: 01.10.2021 Can you do this without passing View?
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
                        // TODO: 01.10.2021 Write this in one action
                        Navigation.findNavController(activityView).navigateUp()
                        Navigation.findNavController(activityView).navigateUp()
                    }
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .create()
    }
}
