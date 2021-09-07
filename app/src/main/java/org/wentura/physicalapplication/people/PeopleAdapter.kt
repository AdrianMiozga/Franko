package org.wentura.physicalapplication.people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import org.wentura.physicalapplication.R
import org.wentura.physicalapplication.User

class PeopleAdapter(private val peoples: List<User>) :
    RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.name)
        private val profilePicture: ImageView = view.findViewById(R.id.profile_picture)

        fun bindView(position: Int, dataSet: List<User>) {
            val uri = dataSet[position].photoUrl

            if (uri == null) {
                profilePicture.load(R.drawable.profile_picture_placeholder) {
                    transformations(CircleCropTransformation())
                }
            } else {
                profilePicture.load(uri) {
                    transformations(CircleCropTransformation())
                }
            }

            name.text = dataSet[position].name
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.user_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindView(position, peoples)
    }

    override fun getItemCount() = peoples.size
}
