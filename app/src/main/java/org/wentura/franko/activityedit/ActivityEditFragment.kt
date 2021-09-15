package org.wentura.franko.activityedit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.wentura.franko.Constants
import org.wentura.franko.R
import org.wentura.franko.activity.ActivityFragmentArgs
import org.wentura.franko.databinding.FragmentActivityEditBinding

class ActivityEditFragment : Fragment(R.layout.fragment_activity_edit) {

    private var fragmentActivityEditBinding: FragmentActivityEditBinding? = null
    private val db = Firebase.firestore
    private val args: ActivityFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentActivityEditBinding.bind(view)
        fragmentActivityEditBinding = binding

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val activityDelete = binding.activityEditDelete

        activityDelete.setOnClickListener {
            AlertDialog
                .Builder(requireContext())
                .setMessage(getString(R.string.delete_activity_dialog_message))
                .setPositiveButton(R.string.delete) { _, _ ->
                    db.collection(Constants.USERS)
                        .document(uid)
                        .collection(Constants.PATHS)
                        .document(args.id)
                        .delete()
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
