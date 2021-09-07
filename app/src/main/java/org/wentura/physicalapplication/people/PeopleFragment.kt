package org.wentura.physicalapplication.people

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
import org.wentura.physicalapplication.Constants
import org.wentura.physicalapplication.User
import org.wentura.physicalapplication.activities.ActivityAdapter
import org.wentura.physicalapplication.databinding.FragmentPeopleBinding

class PeopleFragment : Fragment() {
    private val db = Firebase.firestore
    private val peoples: ArrayList<User> = arrayListOf()

    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context)
    }

    private val recycleListener = RecyclerView.RecyclerListener { holder ->
        holder as ActivityAdapter.ViewHolder
    }

    private var _binding: FragmentPeopleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        const val TAG = "PeopleFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPeopleBinding.inflate(inflater, container, false)
        val view = binding.root

        db.collection(Constants.USERS)
            .get()
            .addOnSuccessListener { users ->
                if (users != null) {
                    peoples.addAll(users.toObjects())

                    binding.peopleRecyclerView.apply {
                        setHasFixedSize(true)
                        layoutManager = linearLayoutManager
                        adapter = PeopleAdapter(peoples)
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