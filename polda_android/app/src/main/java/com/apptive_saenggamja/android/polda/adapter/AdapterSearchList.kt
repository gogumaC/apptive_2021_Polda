package com.apptive_saenggamja.android.polda

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdapterSearchList():RecyclerView.Adapter<SearchHolder>() {

    var item = mutableListOf<PolaroidData>()
    var tag:String=""
    interface ItemClick
    {
        fun onClick(view: View, position: Int)
    }
    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_search,
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
            holder.setData(data.date, data.imageC)
        }

        if(itemClick != null)
        {
            holder.itemView.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }

    }

    override fun getItemCount(): Int {
        return item.size
    }

}

class SearchHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    var image = itemView.findViewById<ImageView>(R.id.searchImage)
    fun setData(item: String, polarImage: Bitmap){
        image.setImageBitmap(polarImage)

    }

    fun getTagPolaroidList(tag:String){
        //TODO("해당 태그있는 폴라로이드 리스트 불러오기 후 반환")
    }

}