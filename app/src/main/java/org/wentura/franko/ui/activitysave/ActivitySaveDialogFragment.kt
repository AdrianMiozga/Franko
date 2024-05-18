package org.wentura.franko.ui.activitysave

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import org.wentura.franko.R
import org.wentura.franko.Utilities
import org.wentura.franko.ui.map.RecordingService

class ActivitySaveDialogFragment(
    private val activitySaveObserver: ActivitySaveObserver,
    private val activityView: View
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog
            .Builder(requireContext())
            .setTitle(getString(R.string.unsaved_changes))
            .setMessage(getString(R.string.new_activity_unsaved_changes_message))
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                Utilities.closeKeyboard(activityView)
                findNavController().navigateUp()
                activitySaveObserver.save()
            }
            .setNeutralButton(getString(R.string.no)) { _, _ ->
                Utilities.closeKeyboard(activityView)
                findNavController().navigateUp()

                val intent = Intent(context, RecordingService::class.java)
                requireContext().stopService(intent)
            }
            .setNegativeButton(getString(R.string.back)) { _, _ -> }
            .create()
    }
}
