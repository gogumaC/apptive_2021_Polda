package com.apptive.android.polda

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apptive.android.polda.customView.Diary


class AdapterHomeList(context: Context):RecyclerView.Adapter<HomeHolder>(){

    var context = context
    var item = mutableListOf<Diary>()
    //클릭이벤트 위한 인터페이스 구현

    interface OnItemClickListener{
        fun onItemClick(v:View, pos : Int)
    }
    interface OnTitleClickListener{
        fun onTitleClick(v:View, pos: Int)
    }
    interface OnItemLongClickListener{
        fun onItemLongClick(v: View, pos: Int)
    }
    interface OnEditClickListener{
        fun onEditClick(v: View, pos: Int)
    }

    var listener3 : OnEditClickListener? = null
    fun setEditClickListener(listener3: OnEditClickListener){
        this.listener3 = listener3
    }

    var listener2 : OnTitleClickListener? = null
    fun setOnTitleClickListener(listener2: OnTitleClickListener){
        this.listener2 = listener2
    }

    var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    var longListener: OnItemLongClickListener? = null
    fun setOnItemLongClickListener(listener: OnItemLongClickListener): Boolean{
        this.longListener=listener
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        val binding = LayoutInflater.from(parent.context).inflate(R.layout.item_diary, parent, false)
        return HomeHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {
        val data = item[position]

        holder.itemView.setOnClickListener {
            listener?.onItemClick(it,position)
        }
        holder.title.setOnClickListener{
            listener2?.onTitleClick(it,position)
        }
        holder.itemView.setOnLongClickListener(OnLongClickListener { v ->
            val pos: Int = position
            if (pos != RecyclerView.NO_POSITION) {
                longListener?.onItemLongClick(v, pos)
            }
            true
        })
        holder.apply {
            holder.setData(data.title, data.image)
        }
        holder.editBtn.setOnClickListener{
            listener3?.onEditClick(it,position)
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

}

class HomeHolder(itemView:View): RecyclerView.ViewHolder(itemView){

    var title = itemView.findViewById<TextView>(R.id.diaryTitle)
    var image = itemView.findViewById<ImageView>(R.id.diary_image)
    var editBtn = itemView.findViewById<ImageButton>(R.id.diaryEditBtn)
    fun setData(t: String, i: Bitmap){
        title.setText(t)
        image.setImageBitmap(i)
        image.setBackgroundResource(R.drawable.diary_image_design)
        image.clipToOutline = true
    }

}