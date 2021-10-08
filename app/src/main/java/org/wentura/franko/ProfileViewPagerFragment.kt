package org.wentura.franko

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import org.wentura.franko.Constants.ACTIVITIES_PAGE_INDEX
import org.wentura.franko.Constants.PROFILE_PAGE_INDEX
import org.wentura.franko.Constants.SETTINGS_PAGE_INDEX
import org.wentura.franko.adapters.ProfilePagerAdapter
import org.wentura.franko.databinding.FragmentProfileViewPagerBinding

class ProfileViewPagerFragment : Fragment(R.layout.fragment_profile_view_pager) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentProfileViewPagerBinding.bind(view)

        val adapter = ProfilePagerAdapter(this)

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            ACTIVITIES_PAGE_INDEX -> getString(R.string.activities)
            PROFILE_PAGE_INDEX -> getString(R.string.profile)
            SETTINGS_PAGE_INDEX -> getString(R.string.settings)
            else -> null
        }
    }
}
