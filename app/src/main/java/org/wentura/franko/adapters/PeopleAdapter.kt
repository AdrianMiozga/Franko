package org.wentura.franko.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import org.wentura.franko.R
import org.wentura.franko.Utilities
import org.wentura.franko.data.User
import org.wentura.franko.databinding.ListItemUserBinding

class PeopleAdapter(private val people: List<User>) :
    RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    companion object {
        val TAG = PeopleAdapter::class.simpleName
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ListItemUserBinding.bind(view)

        private val fullName: TextView = binding.recyclerViewFullName
        private val profilePicture: ImageView = binding.recyclerViewProfilePicture

        private val context = view.context

        fun bindView(position: Int, people: List<User>) {
            view.setOnClickListener {
                Navigation.findNavController(view).navigate(
                    R.id.to_profile_fragment,
                    bundleOf("uid" to people[position].uid)
                )
            }

            val photoUrl = people[position].photoUrl

            Utilities.loadProfilePicture(photoUrl, profilePicture)

            fullName.text = context.getString(
                R.string.full_name,
                people[position].firstName,
                people[position].lastName
            )
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_user, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindView(position, people)
    }

    override fun getItemCount() = people.size
}
