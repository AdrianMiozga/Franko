package org.wentura.franko.people

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.R
import org.wentura.franko.data.User
import org.wentura.franko.databinding.FragmentPeopleBinding

@AndroidEntryPoint
class PeopleFragment : Fragment(R.layout.fragment_people),
    SearchView.OnQueryTextListener {

    private var people: ArrayList<User> = arrayListOf()
    private var filteredPeople: ArrayList<User> = arrayListOf()
    private lateinit var peopleAdapter: PeopleAdapter

    private val viewModel: PeopleListViewModel by viewModels()

    private val recyclerListener = RecyclerView.RecyclerListener { holder ->
        holder as PeopleAdapter.ViewHolder
    }

    private var fragmentPeopleBinding: FragmentPeopleBinding? = null

    companion object {
        val TAG = PeopleFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPeopleBinding.bind(view)
        fragmentPeopleBinding = binding

        viewModel.users.observe(viewLifecycleOwner) { result ->
            people.clear()
            filteredPeople.clear()

            people = result
            filteredPeople = result

            if (result.size == 0) {
                binding.apply {
                    peopleNothingToShow.visibility = View.VISIBLE
                    peopleRecyclerView.visibility = View.GONE
                    peopleSearchView.visibility = View.GONE
                }
            }

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentPeopleBinding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

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
