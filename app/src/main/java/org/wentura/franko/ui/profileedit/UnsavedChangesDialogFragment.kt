package org.wentura.franko.ui.profileedit

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import org.wentura.franko.R
import org.wentura.franko.Utilities

class UnsavedChangesDialogFragment(
    private val activityView: View,
    private val saveObserver: SaveObserver,
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.unsaved_changes))
            .setMessage(getString(R.string.you_have_unsaved_changes))
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                saveObserver.save()
                Utilities.closeKeyboard(activityView)
                findNavController().navigateUp()
            }
            .setNeutralButton(getString(R.string.discard)) { _, _ ->
                Utilities.closeKeyboard(activityView)
                findNavController().navigateUp()
            }
            .setNegativeButton(getString(R.string.back)) { _, _ -> }
            .create()
    }
}
