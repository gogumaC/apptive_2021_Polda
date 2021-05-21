package com.apptive.android.polda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdapterSearchList():RecyclerView.Adapter<SearchHolder>() {

    var item = mutableListOf<SampleList>()

    interface ItemClick
    {
        fun onClick(view: View, position: Int)
    }
    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_polaloid_front,
            parent,
            false
        )

        val lp = inflatedView.getLayoutParams() as GridLayoutManager.LayoutParams
        lp.height = parent.measuredHeight / 2
        inflatedView.setLayoutParams(lp)

        return SearchHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        val data = item[position]
        holder.apply {
            holder.setData(data)
        }

        if(itemClick != null)
        {
            holder?.itemView?.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }

    }

    override fun getItemCount(): Int {
        return item.size
    }

}

class SearchHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    var image = itemView.findViewById<ImageView>(R.id.polaroidImage)
    fun setData(item: SampleList){
        image.setImageResource(R.drawable.sample_diary)
    }

}