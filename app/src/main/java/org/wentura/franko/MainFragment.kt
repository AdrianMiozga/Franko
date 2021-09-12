package org.wentura.franko

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import org.wentura.franko.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    private var fragmentMainBinding: FragmentMainBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMainBinding.bind(view)
        fragmentMainBinding = binding

        binding.apply {
            mainShowMap.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(MainFragmentDirections.toMapFragment())
            }

            mainShowActivities.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(MainFragmentDirections.toActivitiesFragment())
            }

            mainShowStatistics.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(MainFragmentDirections.toStatisticsFragment())
            }

            mainShowAchievements.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(MainFragmentDirections.toAchievementsFragment())
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
                    .navigate(MainFragmentDirections.toEditProfileFragment())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentMainBinding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> {
                AuthUI.getInstance()
                    .signOut(requireContext())
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.logged_out),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                true
            }
            R.id.delete_account -> {
                AuthUI.getInstance()
                    .delete(requireContext())
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.account_deleted),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
