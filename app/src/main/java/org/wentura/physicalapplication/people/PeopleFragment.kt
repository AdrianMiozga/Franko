package org.wentura.physicalapplication.people

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import org.wentura.physicalapplication.Constants
import org.wentura.physicalapplication.R
import org.wentura.physicalapplication.User
import org.wentura.physicalapplication.databinding.FragmentPeopleBinding

class PeopleFragment : Fragment(),
    SearchView.OnQueryTextListener {

    private val db = Firebase.firestore
    private val people: ArrayList<User> = arrayListOf()
    private val filteredPeople: ArrayList<User> = arrayListOf()
    private lateinit var peopleAdapter: PeopleAdapter

    private val recyclerListener = RecyclerView.RecyclerListener { holder ->
        holder as PeopleAdapter.ViewHolder
    }

    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = _binding!!

    companion object {
        val TAG = PeopleFragment::class.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeopleBinding.inflate(inflater, container, false)
        val view = binding.root

        db.collection(Constants.USERS)
            .whereEqualTo(
                Constants.WHO_CAN_SEE_MY_PROFILE,
                resources.getStringArray(R.array.who_can_see_my_profile)[0]
            )
            .get()
            .addOnSuccessListener { users ->
                if (users == null) {
                    Log.d(TAG, "No such collection")
                    return@addOnSuccessListener
                }

                people.clear()
                filteredPeople.clear()

                people.addAll(users.toObjects())
                filteredPeople.addAll(users.toObjects())

                peopleAdapter = PeopleAdapter(filteredPeople)

                binding.peopleRecyclerView.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(context)
                    adapter = peopleAdapter
                    setRecyclerListener(recyclerListener)
                }

                binding.peopleSearchView.apply {
                    queryHint = getString(R.string.search_people)
                    setOnQueryTextListener(this@PeopleFragment)
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val newFilter = people.filter { user ->
            user.firstName.lowercase().contains(newText.toString())
        }

        filteredPeople.clear()
        filteredPeople.addAll(newFilter)

        peopleAdapter.notifyDataSetChanged()
        return true
    }
}
