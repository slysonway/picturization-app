package com.esgi.picturization.ui.home.image.transform.list.choice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.esgi.picturization.R
import com.esgi.picturization.data.models.FilterEnum

class FilterChoiceListAdapter : RecyclerView.Adapter<FilterChoiceListAdapter.FilterListStaticViewHolder>() {

    private var values: List<FilterEnum> = enumValues<FilterEnum>().toList()
    var listener: OnFilterChoiceListInteractionListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterListStaticViewHolder {
        return FilterListStaticViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.image_and_text_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilterListStaticViewHolder, position: Int) {
        val currentItem = values[position]
        holder.image.setImageResource(currentItem.icon)
        holder.title.setText(currentItem.title)
        holder.itemView.setOnClickListener {
            listener?.onFilterChoiceListener(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return values.size
    }

    class FilterListStaticViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val title: TextView = view.findViewById(R.id.txt_filter)
    }
}