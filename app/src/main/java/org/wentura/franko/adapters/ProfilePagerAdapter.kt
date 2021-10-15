package org.wentura.franko.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.wentura.franko.Constants.ACTIVITIES_PAGE_INDEX
import org.wentura.franko.Constants.PROFILE_PAGE_INDEX
import org.wentura.franko.activities.ActivitiesFragment
import org.wentura.franko.profile.ProfileMyFragment

class ProfilePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        PROFILE_PAGE_INDEX to { ProfileMyFragment() },
        ACTIVITIES_PAGE_INDEX to { ActivitiesFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke()
            ?: throw IndexOutOfBoundsException()
    }
}
