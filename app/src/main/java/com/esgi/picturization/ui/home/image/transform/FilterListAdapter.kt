package com.esgi.picturization.ui.home.image.transform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.esgi.picturization.R
import com.esgi.picturization.data.models.FilterEnum

class FilterListAdapter: RecyclerView.Adapter<FilterListAdapter.FilterViewHolder>() {

    private var values: MutableList<FilterEnum> = ArrayList()
    var listener: OnRecyclerListInteractionListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        return FilterViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_filter_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val currentItem = values[position]
        holder.filter.text = currentItem.name
        holder.delete.setOnClickListener {
            listener?.onListFragmentInteraction(position)
        }
    }

    override fun getItemCount(): Int {
        return values.size
    }

    fun setData(data: List<FilterEnum>) {
        values.clear()
        values.addAll(data)
        notifyDataSetChanged()
    }

    fun addFilter(filter: FilterEnum) {
        values.add(filter)
        notifyDataSetChanged()
    }

    fun getAllFilter(): List<FilterEnum> {
        return values
    }
    private fun removeFilter(position: Int) {
        values.removeAt(position)
        notifyDataSetChanged()
    }

    class FilterViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val filter : TextView = view.findViewById(R.id.txt_filter)
        val delete: ImageView = view.findViewById(R.id.ic_remove_filter)
    }
}