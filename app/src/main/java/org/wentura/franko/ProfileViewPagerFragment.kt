package org.wentura.franko

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import org.wentura.franko.Constants.ACTIVITIES_PAGE_INDEX
import org.wentura.franko.Constants.PROFILE_PAGE_INDEX
import org.wentura.franko.adapters.ProfilePagerAdapter
import org.wentura.franko.databinding.FragmentProfileViewPagerBinding

class ProfileViewPagerFragment : Fragment(R.layout.fragment_profile_view_pager) {

    private val args: ProfileViewPagerFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        val binding = FragmentProfileViewPagerBinding.bind(view)

        val tabLayout = binding.tabLayout

        val viewPager = binding.viewPager
        viewPager.adapter = ProfilePagerAdapter(this)

        viewPager.doOnPreDraw {
            if (args.item != 0) {
                viewPager.currentItem = args.item
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            PROFILE_PAGE_INDEX -> getString(R.string.profile)
            ACTIVITIES_PAGE_INDEX -> getString(R.string.activities)
            else -> null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                val toSettingsFragment =
                    ProfileViewPagerFragmentDirections.toSettingsFragment()

                findNavController().navigate(toSettingsFragment)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
