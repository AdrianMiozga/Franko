package org.wentura.physicalapplication.people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.wentura.physicalapplication.R
import org.wentura.physicalapplication.User

class PeopleAdapter(private val peoples: List<User>) :
    RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.name)

        fun bindView(position: Int, dataSet: List<User>) {
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
