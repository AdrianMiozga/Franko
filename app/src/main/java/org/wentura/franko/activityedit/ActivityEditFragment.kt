package org.wentura.franko.activityedit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.R
import org.wentura.franko.activity.ActivityFragmentArgs
import org.wentura.franko.data.ActivityRepository
import org.wentura.franko.databinding.FragmentActivityEditBinding
import javax.inject.Inject

@AndroidEntryPoint
class ActivityEditFragment : Fragment(R.layout.fragment_activity_edit) {

    @Inject
    lateinit var activityRepository: ActivityRepository

    private var fragmentActivityEditBinding: FragmentActivityEditBinding? = null
    private val args: ActivityFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentActivityEditBinding.bind(view)
        fragmentActivityEditBinding = binding

        val activityDelete = binding.activityEditDelete

        activityDelete.setOnClickListener {
            AlertDialog
                .Builder(requireContext())
                .setMessage(getString(R.string.delete_activity_dialog_message))
                .setPositiveButton(R.string.delete) { _, _ ->
                    activityRepository
                        .deleteActivity(args.id)
                        .addOnSuccessListener {
                            Navigation.findNavController(view).navigateUp()
                            Navigation.findNavController(view).navigateUp()
                        }
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .create()
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentActivityEditBinding = null
    }
}
