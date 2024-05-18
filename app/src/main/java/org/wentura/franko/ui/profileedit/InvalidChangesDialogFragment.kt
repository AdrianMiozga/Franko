package org.wentura.franko.ui.profileedit

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import org.wentura.franko.R
import org.wentura.franko.Utilities

class InvalidChangesDialogFragment(
    private val activityView: View,
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog
            .Builder(requireContext())
            .setTitle(getString(R.string.unsaved_changes))
            .setMessage(getString(R.string.you_have_unsaved_changes))
            .setPositiveButton(getString(R.string.back)) { _, _ -> }
            .setNeutralButton(getString(R.string.discard)) { _, _ ->
                Utilities.closeKeyboard(activityView)
                findNavController().navigateUp()
            }
            .create()
    }
}
