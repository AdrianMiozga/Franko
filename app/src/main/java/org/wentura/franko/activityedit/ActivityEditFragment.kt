package org.wentura.franko.activityedit

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.R
import org.wentura.franko.activity.ActivityFragmentArgs
import org.wentura.franko.data.ActivityRepository
import org.wentura.franko.databinding.FragmentActivityEditBinding
import org.wentura.franko.viewmodels.ActivityViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ActivityEditFragment : Fragment(R.layout.fragment_activity_edit),
    AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var activityRepository: ActivityRepository

    private var fragmentActivityEditBinding: FragmentActivityEditBinding? = null
    private val activityViewModel: ActivityViewModel by viewModels()
    private val args: ActivityFragmentArgs by navArgs()
    private var initialOnItemSelected = true

    companion object {
        val TAG = ActivityEditFragment::class.simpleName
    }

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

        val spinner = binding.activityEditActivitySpinner
        spinner.onItemSelectedListener = this

        activityViewModel.getCurrentActivity().observe(viewLifecycleOwner) { activity ->
            if (activity == null) return@observe
           
            val activityType = activity.activity
            val id = resources.getStringArray(R.array.activities_array).indexOf(activityType)

            spinner.setSelection(id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentActivityEditBinding = null
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View?, position: Int, id: Long) {
        if (initialOnItemSelected) {
            initialOnItemSelected = false
            return
        }

        val activityType = adapterView.getItemAtPosition(position).toString()
        activityRepository.updateActivityType(args.id, activityType)
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) = Unit
}
