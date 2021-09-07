package org.wentura.physicalapplication

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import org.wentura.physicalapplication.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.apply {
            showMap.setOnClickListener {
                Navigation.findNavController(view).navigate(MainFragmentDirections.toMapFragment())
            }

            showActivities.setOnClickListener {
                Navigation.findNavController(view).navigate(MainFragmentDirections.toActivitiesFragment())
            }

            showStatistics.setOnClickListener {
                Navigation.findNavController(view).navigate(MainFragmentDirections.toStatisticsFragment())
            }

            showAchievements.setOnClickListener {
                Navigation.findNavController(view).navigate(MainFragmentDirections.toAchievementsFragment())
            }

            showOptions.setOnClickListener {
                Navigation.findNavController(view).navigate(MainFragmentDirections.toSettingsFragment())
            }

            showPeople.setOnClickListener {
                Navigation.findNavController(view).navigate(MainFragmentDirections.toPeopleFragment())
            }
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
