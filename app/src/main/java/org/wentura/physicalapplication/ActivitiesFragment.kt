package org.wentura.physicalapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.wentura.physicalapplication.databinding.FragmentActivitiesBinding

class ActivitiesFragment : Fragment() {
    private val db = Firebase.firestore
    private val pointsArray: ArrayList<String> = arrayListOf()

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
                    for (path in paths) {
                        val entries = path.data.entries

                        entries.forEach {
                            val points = it.value as ArrayList<HashMap<String, Double>>

                            var pointsResult = ""

                            for (point in points) {
                                Log.d(TAG, "lat: ${point["latitude"]}, longitude: ${point["longitude"]}")
                                pointsResult += point["latitude"]
                                pointsResult += ", "
                                pointsResult += point["longitude"]
                                pointsResult += "; "
                            }

                            Log.d(TAG, "onCreateView: $pointsResult")

                            pointsArray.add(pointsResult)
                        }

                        val activitiesRecyclerView = binding.activitesRecyclerView
                        val adapter = ActivityAdapter(pointsArray)

                        activitiesRecyclerView.adapter = adapter
                        activitiesRecyclerView.layoutManager = LinearLayoutManager(context)

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