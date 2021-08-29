package com.apptive.android.polda

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatDrawableManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.apptive.android.polda.fragment.FragmentEdit
import kotlinx.coroutines.withContext
import java.net.URI

class AdapterStickerList():RecyclerView.Adapter<StickerHolder>() {

    var stickerList=listOf<Drawable>()


    interface OnItemClickListener{
        fun onItemClick(v:View, pos : Int)
    }
    var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_stickers,parent,false)
        return StickerHolder(view)
    }

    override fun onBindViewHolder(holder: StickerHolder, position: Int) {
        holder.bind(position,stickerList)
        holder.itemView.setOnClickListener {
            listener?.onItemClick(it,position)
        }
    }

    override fun getItemCount(): Int {
        return stickerList.size

    }
}

class StickerHolder(itemView:View): RecyclerView.ViewHolder(itemView){
    fun bind(pos:Int,stickerData:List<Drawable>){
        val item=itemView.findViewById<ImageView>(R.id.itemSticker)
        item.setImageDrawable(stickerData[pos])


    }



}