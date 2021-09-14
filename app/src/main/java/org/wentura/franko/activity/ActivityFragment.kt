package org.wentura.franko.activity

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
import org.wentura.franko.databinding.FragmentActivityBinding

class ActivityFragment : Fragment(R.layout.fragment_activity) {

    private var activityFragmentBinding: FragmentActivityBinding? = null

    private val args: ActivityFragmentArgs by navArgs()
    private val db = Firebase.firestore

    companion object {
        val TAG = ActivityFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentActivityBinding.bind(view)
        activityFragmentBinding = binding

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        binding.activityDelete.setOnClickListener {
            AlertDialog
                .Builder(requireContext())
                .setMessage(getString(R.string.delete_activity_dialog_message))
                .setPositiveButton(R.string.OK) { _, _ ->
                    db.collection(Constants.USERS)
                        .document(uid)
                        .collection(Constants.PATHS)
                        .document(args.id)
                        .delete()
                        .addOnSuccessListener {
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
        activityFragmentBinding = null
    }
}
