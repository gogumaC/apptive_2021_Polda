package com.apptive.android.polda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class AdapterStickerList:RecyclerView.Adapter<StickerHolder>() {

    var stickerList=listOf<Int>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_stickers,parent,false)
        return StickerHolder(view)
    }

    override fun onBindViewHolder(holder: StickerHolder, position: Int) {
        //TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        //return stickerList.size
        return 30
    }
}

class StickerHolder(itemView:View): RecyclerView.ViewHolder(itemView){

}