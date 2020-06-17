package com.esgi.picturization.ui.home.image.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.MediaStoreSignature
import com.esgi.picturization.R
import com.esgi.picturization.data.models.DbImage
import java.text.SimpleDateFormat

class ImageAdapter(
    val isTreated: Boolean
): RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
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
        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide.with(holder.itemView)
            .load(if (isTreated) currentItem.urlTreated else currentItem.urlUntreated)
            .placeholder(circularProgressDrawable)
            .centerCrop()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
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