package org.wentura.franko

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import org.wentura.franko.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMainBinding.bind(view)

        binding.apply {
            mainShowHome.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(MainFragmentDirections.toHomeFragment())
            }

            mainShowMap.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(MainFragmentDirections.toMapFragment())
            }

            mainShowActivities.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(MainFragmentDirections.toActivitiesFragment())
            }

            mainShowOptions.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(MainFragmentDirections.toSettingsFragment())
            }

            mainShowPeople.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(MainFragmentDirections.toPeopleFragment())
            }

            mainEditProfile.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(MainFragmentDirections.toProfileEditFragment())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> {
                AuthUI
                    .getInstance()
                    .signOut(requireContext())
                    .addOnSuccessListener {
                        (activity as MainActivity).createSignInIntent()
                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
