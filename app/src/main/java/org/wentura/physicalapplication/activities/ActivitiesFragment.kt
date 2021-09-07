package org.wentura.physicalapplication.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import org.wentura.physicalapplication.Constants
import org.wentura.physicalapplication.Path
import org.wentura.physicalapplication.databinding.FragmentActivitiesBinding

class ActivitiesFragment : Fragment() {
    private val db = Firebase.firestore
    private val pathsArray: ArrayList<Path> = arrayListOf()

    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context)
    }

    private val recyclerListener = RecyclerView.RecyclerListener { holder ->
        val mapHolder = holder as ActivityAdapter.ViewHolder
        mapHolder.clearView()
    }

    private var _binding: FragmentActivitiesBinding? = null
    private val binding get() = _binding!!

    companion object {
        val TAG = ActivitiesFragment::class.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        val view = binding.root

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val collectionReference = db.collection(Constants.USERS)
            .document(uid)
            .collection(Constants.PATHS)

        collectionReference.get()
            .addOnSuccessListener { paths ->
                if (paths == null) {
                    Log.d(TAG, "No such collection")
                    return@addOnSuccessListener
                }

                pathsArray.addAll(paths.toObjects())

                binding.activitesRecyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager = linearLayoutManager
                    adapter = ActivityAdapter(pathsArray)
                    setRecyclerListener(recyclerListener)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
