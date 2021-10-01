package org.wentura.franko.activitysave

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.R
import org.wentura.franko.data.ActivityRepository
import org.wentura.franko.data.UserRepository
import org.wentura.franko.map.RecordingRepository
import javax.inject.Inject

@AndroidEntryPoint
class ActivitySaveFragment : Fragment(R.layout.fragment_activity_save) {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var activityRepository: ActivityRepository

    @Inject
    lateinit var recordingRepository: RecordingRepository

    private lateinit var activitySaveObserver: ActivitySaveObserver

    companion object {
        val TAG = ActivitySaveFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activitySaveObserver = ActivitySaveObserver(
            recordingRepository,
            activityRepository,
            userRepository,
            requireContext()
        )

        lifecycle.addObserver(activitySaveObserver)

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                ActivitySaveDialogFragment(activitySaveObserver, view).show(
                    parentFragmentManager,
                    ActivitySaveDialogFragment::class.simpleName
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
                activitySaveObserver.save()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
