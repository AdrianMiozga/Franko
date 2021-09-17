package org.wentura.franko.following

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import org.wentura.franko.R
import org.wentura.franko.data.User
import org.wentura.franko.databinding.ListItemUserBinding

// TODO: 17.09.2021 Use PeopleAdapter?
class FollowingAdapter(private val people: List<User>) :
    RecyclerView.Adapter<FollowingAdapter.ViewHolder>() {

    companion object {
        val TAG = FollowingAdapter::class.simpleName
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ListItemUserBinding.bind(view)

        private val fullName: TextView = binding.recyclerViewFullName
        private val profilePicture: ImageView = binding.recyclerViewProfilePicture

        private val context = view.context

        fun bindView(position: Int, people: List<User>) {
            view.setOnClickListener {
                Navigation.findNavController(view).navigate(
                    FollowingFragmentDirections.toProfileFragment(people[position].uid)
                )
            }

            val uri = people[position].photoUrl

            if (uri.isNullOrBlank()) {
                profilePicture.load(R.drawable.ic_profile_picture_placeholder) {
                    transformations(CircleCropTransformation())
                }
            } else {
                profilePicture.load(uri) {
                    transformations(CircleCropTransformation())
                }
            }

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
