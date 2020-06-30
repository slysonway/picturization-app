package com.esgi.picturization.ui.home.image.list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.esgi.picturization.R
import com.esgi.picturization.data.models.DbImage

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

        if (currentItem.treaty) {
            holder.status.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
        } else {
            holder.status.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
        }

        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide.with(holder.itemView)
            .load(if (currentItem.treaty) currentItem.urlTreated else currentItem.urlUntreated)
            .placeholder(circularProgressDrawable)
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
        val status: CardView = view.findViewById(R.id.status_badge)
        val image: ImageView = view.findViewById(R.id.image_view)
    }
}