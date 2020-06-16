package com.esgi.picturization.ui.home.start

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.esgi.picturization.R
import com.esgi.picturization.data.models.DbImage
import com.esgi.picturization.data.models.Image
import java.text.SimpleDateFormat

class ImageAdapter: RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    private var values: MutableList<DbImage> = ArrayList()
    var listener: OnRecycleListInteractionListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_image_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentItem = values[position]
        holder.name.text = SimpleDateFormat(holder.itemView.resources.getString(R.string.date_format)).format(currentItem.createdAt)
        Glide.with(holder.itemView)
            .load(currentItem.url)
            .placeholder(R.drawable.ic_loop_blue_24dp)
            .centerCrop()
            .into(holder.image)
        holder.itemView.setOnClickListener {
            listener?.onListFragmentInteraction(position)
        }
    }

    override fun getItemCount(): Int {
        return values.size
    }

    fun setData(data: List<DbImage>) {
        values.clear()
        values.addAll(data)
        notifyDataSetChanged()
    }

    class ImageViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txt_name)
        val image: ImageView = view.findViewById(R.id.image_view)
    }
}