package com.apptive.android.polda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AdapterDetailViewPager:RecyclerView.Adapter<DetailHolder>() {
    var sampleTitleList=listOf<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_detail,parent,false)
        return DetailHolder(view)
    }

    override fun onBindViewHolder(holder: DetailHolder, position: Int) {
        //TODO("변경필요")
    }

    override fun getItemCount(): Int {
        //TODO("반환 개수 변경필요")
        return sampleTitleList.size
    }
}

class DetailHolder(itemView: View): RecyclerView.ViewHolder(itemView){

}