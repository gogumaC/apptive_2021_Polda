package com.apptive.android.polda

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class AdapterHomeList():RecyclerView.Adapter<HomeHolder>(){

    var item = mutableListOf<SampleList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        val binding = LayoutInflater.from(parent.context).inflate(R.layout.item_diary, parent, false)

        return HomeHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {
        val data = item[position]
        holder.apply {
            holder.setData(data)
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

}

class HomeHolder(itemView:View): RecyclerView.ViewHolder(itemView){

    var image = itemView.findViewById<ImageView>(R.id.imageView)
    fun setData(item: SampleList){
        image.setImageResource(R.drawable.sample_diary)
    }
}