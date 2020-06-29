package com.esgi.picturization.ui.home.image.transform.list.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.esgi.picturization.R
import com.esgi.picturization.data.models.Filter
import com.esgi.picturization.data.models.FilterEnum

class FilterListAdapter: RecyclerView.Adapter<FilterListAdapter.FilterViewHolder>() {

    private var values: MutableList<Filter> = ArrayList()
    var listener: OnFilterListInteractionListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        return FilterViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_filter_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val currentItem = values[position]
        val filterEnum = FilterEnum.valueOf(currentItem.name)
        holder.filter.setText(filterEnum.title)
        holder.icon.setImageResource(filterEnum.icon)
        holder.delete.setOnClickListener {
            listener?.onFilterListener(position)
        }
    }

    override fun getItemCount(): Int {
        return values.size
    }

    fun setData(data: List<Filter>) {
        values.clear()
        values.addAll(data)
        notifyDataSetChanged()
    }

    fun addFilter(filter: Filter) {
        values.add(filter)
        notifyDataSetChanged()
    }

    fun getAllFilter(): List<Filter> {
        return values
    }
    private fun removeFilter(position: Int) {
        values.removeAt(position)
        notifyDataSetChanged()
    }

    class FilterViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val filter : TextView = view.findViewById(R.id.txt_filter)
        val icon : ImageView = view.findViewById(R.id.filter_icon)
        val delete: ImageView = view.findViewById(R.id.ic_remove_filter)
    }
}