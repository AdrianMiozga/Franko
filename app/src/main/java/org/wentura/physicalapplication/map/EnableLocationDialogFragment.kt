package org.wentura.physicalapplication.map

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import org.wentura.physicalapplication.R

class EnableLocationDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setMessage(getString(R.string.enable_location_dialog_message))
            .setPositiveButton(R.string.OK) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }

        return builder.create()
    }
}
