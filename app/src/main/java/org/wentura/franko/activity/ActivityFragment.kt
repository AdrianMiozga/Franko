package org.wentura.franko.activity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.wentura.franko.R
import org.wentura.franko.databinding.FragmentActivityBinding
import org.wentura.franko.profile.ProfileFragmentArgs

class ActivityFragment : Fragment(R.layout.fragment_activity) {

    private var activityFragmentBinding: FragmentActivityBinding? = null

    private val args: ProfileFragmentArgs by navArgs()

    companion object {
        val TAG = ActivityFragment::class.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentActivityBinding.bind(view)
        activityFragmentBinding = binding
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activityFragmentBinding = null
    }
}
