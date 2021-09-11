package org.wentura.physicalapplication.achievements

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.wentura.physicalapplication.R
import org.wentura.physicalapplication.databinding.FragmentAchievementsBinding

class AchievementsFragment : Fragment(R.layout.fragment_achievements) {

    private var fragmentAchievementsBinding: FragmentAchievementsBinding? = null

    companion object {
        val TAG = AchievementsFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAchievementsBinding.bind(view)
        fragmentAchievementsBinding = binding
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentAchievementsBinding = null
    }
}
