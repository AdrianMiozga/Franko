package org.wentura.physicalapplication.statistics

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.wentura.physicalapplication.R
import org.wentura.physicalapplication.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private var fragmentStatisticsBinding: FragmentStatisticsBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentStatisticsBinding.bind(view)
        fragmentStatisticsBinding = binding
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentStatisticsBinding = null
    }
}
