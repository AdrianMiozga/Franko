package org.wentura.franko.activitysave

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.wentura.franko.R
import org.wentura.franko.databinding.FragmentActivitySaveBinding

class ActivitySaveFragment : Fragment(R.layout.fragment_activity_save) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentActivitySaveBinding.bind(view)

        binding.activitySaveActivityName.text = "Hello"
    }
}
