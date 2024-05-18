package org.wentura.franko.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.wentura.franko.R
import org.wentura.franko.Utilities.loadProfilePicture
import org.wentura.franko.data.User
import org.wentura.franko.databinding.ListItemUserBinding

class PeopleAdapter : ListAdapter<User, PeopleAdapter.ViewHolder>(PeopleDiffCallback()) {

    companion object {
        val TAG = PeopleAdapter::class.simpleName
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ListItemUserBinding.bind(view)

        private val fullName: TextView = binding.recyclerViewFullName
        private val profilePicture: ImageView = binding.recyclerViewProfilePicture

        private val context = view.context

        fun bindView(user: User) {
            view.setOnClickListener {
                Navigation.findNavController(view)
                    .navigate(R.id.to_profile_fragment, bundleOf("uid" to user.uid))
            }

            profilePicture.loadProfilePicture(user.photoUrl)

            fullName.text = context.getString(R.string.full_name, user.firstName, user.lastName)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_item_user, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindView(getItem(position))
    }
}

private class PeopleDiffCallback : DiffUtil.ItemCallback<User>() {

    override fun areItemsTheSame(
        oldItem: User,
        newItem: User,
    ): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(
        oldItem: User,
        newItem: User,
    ): Boolean {
        return oldItem == newItem
    }
}
