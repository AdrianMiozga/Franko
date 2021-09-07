package org.wentura.physicalapplication.people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import org.wentura.physicalapplication.R
import org.wentura.physicalapplication.User

class PeopleAdapter(private val people: List<User>) :
    RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    companion object {
        val TAG = PeopleAdapter::class.simpleName
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.name)
        private val profilePicture: ImageView = view.findViewById(R.id.profile_picture)

        fun bindView(position: Int, people: List<User>) {
            view.setOnClickListener {
                Navigation.findNavController(view).navigate(
                    PeopleFragmentDirections.toProfileFragment
                        (people[position].uid ?: "")
                )
            }

            val uri = people[position].photoUrl

            if (uri == null) {
                profilePicture.load(R.drawable.profile_picture_placeholder) {
                    transformations(CircleCropTransformation())
                }
            } else {
                profilePicture.load(uri) {
                    transformations(CircleCropTransformation())
                }
            }

            name.text = people[position].name
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.user_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindView(position, people)
    }

    override fun getItemCount() = people.size
}
