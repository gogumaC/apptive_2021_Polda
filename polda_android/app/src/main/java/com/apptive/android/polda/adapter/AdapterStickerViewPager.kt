package com.apptive.android.polda

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class AdapterStickerViewPager(private val context: Context):RecyclerView.Adapter<Holder>() {

    var optionList=listOf<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_sticker_tabs,parent,false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        //TODO("Not yet implemented")\
        holder.bind(context)

    }

    override fun getItemCount(): Int {
        //TODO("Not yet implemented")
        return 5
    }
}

class Holder(itemView: View):RecyclerView.ViewHolder(itemView){
    private val stickerRecycler=itemView.findViewById<RecyclerView>(R.id.recyclerView)

    fun bind(context: Context){
        val stickerAdapter=AdapterStickerList()
        stickerRecycler.adapter=stickerAdapter
        stickerRecycler.layoutManager=GridLayoutManager(context,4)
        stickerRecycler.setHasFixedSize(true)
        stickerAdapter.setOnItemClickListener(object :AdapterStickerList.OnItemClickListener{
            override fun onItemClick(v: View, pos: Int) {
                //TODO("스티커 출력")
                Log.d("rvclick","스티커 $pos")
            }
        })

    }

}