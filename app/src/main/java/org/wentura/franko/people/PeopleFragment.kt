package org.wentura.franko.people

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.wentura.franko.R
import org.wentura.franko.adapters.PeopleAdapter
import org.wentura.franko.data.User
import org.wentura.franko.databinding.FragmentPeopleBinding

@AndroidEntryPoint
class PeopleFragment : Fragment(R.layout.fragment_people),
    SearchView.OnQueryTextListener {

    private var people: ArrayList<User> = arrayListOf()
    private var filteredPeople: ArrayList<User> = arrayListOf()
    private lateinit var peopleAdapter: PeopleAdapter

    private val viewModel: PeopleListViewModel by viewModels()

    companion object {
        val TAG = PeopleFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        val binding = FragmentPeopleBinding.bind(view)

        viewModel.users.observe(viewLifecycleOwner) { result ->
            binding.progressBarOverlay.progressBarOverlay.visibility = View.GONE

            people.clear()
            filteredPeople.clear()

            people = ArrayList(result)
            filteredPeople = ArrayList(result)

            if (result.isEmpty()) {
                binding.apply {
                    peopleNothingToShow.visibility = View.VISIBLE
                    peopleRecyclerView.visibility = View.GONE
                }
            }

            peopleAdapter = PeopleAdapter(filteredPeople)

            binding.peopleRecyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = peopleAdapter
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        val search = menu.findItem(R.id.search)
        val searchView = search.actionView as SearchView

        searchView.queryHint = getString(R.string.search_people)
        searchView.setOnQueryTextListener(this@PeopleFragment)
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
