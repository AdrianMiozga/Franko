package org.wentura.franko.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.wentura.franko.Constants.ACTIVITIES_PAGE_INDEX
import org.wentura.franko.Constants.PROFILE_PAGE_INDEX
import org.wentura.franko.Constants.SETTINGS_PAGE_INDEX
import org.wentura.franko.activities.ActivitiesFragment
import org.wentura.franko.profile.ProfileFragment
import org.wentura.franko.settings.SettingsFragment

class ProfilePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        ACTIVITIES_PAGE_INDEX to { ActivitiesFragment() },
        PROFILE_PAGE_INDEX to { ProfileFragment() },
        SETTINGS_PAGE_INDEX to { SettingsFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}
