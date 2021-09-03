package org.wentura.physicalapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import org.wentura.physicalapplication.databinding.FragmentActivitiesBinding

class ActivitiesFragment : Fragment() {
    private val db = Firebase.firestore
    private val pathsArray: ArrayList<Path> = arrayListOf()

    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context)
    }

    private val recycleListener = RecyclerView.RecyclerListener { holder ->
        val mapHolder = holder as ActivityAdapter.ViewHolder
        mapHolder.clearView()
    }

    private var _binding: FragmentActivitiesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        const val TAG = "ActivitiesFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        val view = binding.root

        val collectionReference = db.collection("users")
            .document(Constants.USER)
            .collection("paths")

        collectionReference.get()
            .addOnSuccessListener { paths ->
                if (paths != null) {
                    pathsArray.addAll(paths.toObjects())

                    binding.activitesRecyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager = linearLayoutManager
                        adapter = ActivityAdapter(pathsArray)
                        setRecyclerListener(recycleListener)
                    }
                } else {
                    Log.d(TAG, "No such collection")
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