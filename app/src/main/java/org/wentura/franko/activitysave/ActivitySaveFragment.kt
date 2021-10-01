package org.wentura.franko.activitysave

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import org.wentura.franko.R
import org.wentura.franko.databinding.FragmentActivitySaveBinding

class ActivitySaveFragment : Fragment(R.layout.fragment_activity_save) {

    companion object {
        val TAG = ActivitySaveFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentActivitySaveBinding.bind(view)

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                SaveDialogFragment(view).show(
                    parentFragmentManager,
                    SaveDialogFragment::class.simpleName
                )
            }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                Navigation.findNavController(requireView()).navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
