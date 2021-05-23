package com.apptive.android.polda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AdapterDetailViewPager:RecyclerView.Adapter<DetailHolder>() {
    var sampleTitleList=listOf<String>()


    interface OnItemClickListener{
        fun onItemClick(v:View, pos : Int)
    }
    var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_detail,parent,false)
        return DetailHolder(view)
    }

    override fun onBindViewHolder(holder: DetailHolder, position: Int) {
        //TODO("변경필요")
        val item=sampleTitleList[position]
        val pola1=holder.itemView.findViewById<View>(R.id.polaloid1)
        val pola2=holder.itemView.findViewById<View>(R.id.polaloid2)
        val pola3=holder.itemView.findViewById<View>(R.id.polaloid3)
        val pola4=holder.itemView.findViewById<View>(R.id.polaloid4)
        holder.itemView.setOnClickListener {
            listener?.onItemClick(it,position)
        }
        //holder.bind()
//        holder.apply{
//            bind(item)
//        }

    }

    override fun getItemCount(): Int {
        //TODO("확인요")
        return sampleTitleList.size
    }


}

class DetailHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    fun bind(){
//        val pola1=itemView.findViewById<View>(R.id.polaloid1)
//        val pola2=itemView.findViewById<View>(R.id.polaloid2)
//        val pola3=itemView.findViewById<View>(R.id.polaloid3)
//        val pola4=itemView.findViewById<View>(R.id.polaloid4)
    }

}