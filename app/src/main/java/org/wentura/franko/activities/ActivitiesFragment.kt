package org.wentura.franko.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import org.wentura.franko.Constants
import org.wentura.franko.Path
import org.wentura.franko.R
import org.wentura.franko.databinding.FragmentActivitiesBinding

class ActivitiesFragment : Fragment(R.layout.fragment_activities) {

    private val db = Firebase.firestore
    private val pathsArray: ArrayList<Path> = arrayListOf()

    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context)
    }

    private val recyclerListener = RecyclerView.RecyclerListener { holder ->
        val mapHolder = holder as ActivityAdapter.ViewHolder
        mapHolder.clearView()
    }

    private var fragmentActivitiesBinding: FragmentActivitiesBinding? = null

    companion object {
        val TAG = ActivitiesFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentActivitiesBinding.bind(view)
        fragmentActivitiesBinding = binding

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentActivitiesBinding = null
    }
}
