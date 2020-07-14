package com.esgi.picturization.ui.home.image.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.esgi.picturization.R

class SliderAdapter: RecyclerView.Adapter<SliderAdapter.SliderViewHolder>(){

    private var values: MutableList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
      return SliderViewHolder(
          LayoutInflater.from(parent.context).inflate(
              R.layout.slide_image_container,
              parent,
              false
          )
      )
    }

    override fun getItemCount(): Int {
        return values.size
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(values[position])
            .into(holder.image)
    }

    fun setData(imageUrl: List<String>) {
        values.clear()
        values.addAll(imageUrl)
        notifyDataSetChanged()
    }

    class SliderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image_preview)
    }
}