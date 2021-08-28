package org.wentura.physicalapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

class FirstFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_first, container, false)

        val button: Button = view.findViewById(R.id.show_map)

        button.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigate_to_map_fragment)
        }

        return view
    }
}