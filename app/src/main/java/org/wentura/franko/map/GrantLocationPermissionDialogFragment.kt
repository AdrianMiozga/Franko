package org.wentura.franko.map

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import org.wentura.franko.R

class GrantLocationPermissionDialogFragment(
    private val locationObserver: LocationPermissionObserver
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog
            .Builder(requireContext())
            .setTitle(getString(R.string.location_permissions_needed_dialog_title))
            .setMessage(getString(R.string.location_permissions_needed_dialog_message))
            .setPositiveButton(R.string.OK) { _, _ ->
                locationObserver.requestLocationPermission()
            }
            .create()
    }
}
