package com.apptive.android.polda

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apptive.android.polda.fragment.FragmentEdit
import com.apptive.android.polda.sticker.DrawableSticker
import com.apptive.android.polda.sticker.Sticker
import kotlinx.coroutines.flow.callbackFlow
import java.net.URI
import java.util.zip.Inflater


class AdapterStickerViewPager(private val context: Context):RecyclerView.Adapter<Holder>() {

    var optionList=listOf<String>()




    interface CallBackListener{
        fun onCallback(stickerName:Drawable)
    }
    var listener:CallBackListener?=null
    fun onCallback(listener:CallBackListener){
        this.listener=listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_sticker_tabs,parent,false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        //holder.bind(context,listener)
        holder.bind(context, position, listener)

    }

    override fun getItemCount(): Int {
        return optionList.size

    }



}

class Holder(itemView: View):RecyclerView.ViewHolder(itemView){
    private val stickerRecycler=itemView.findViewById<RecyclerView>(R.id.recyclerView)
    private lateinit var stickerList:List<Drawable>

    fun bind(context: Context,position:Int,listener:AdapterStickerViewPager.CallBackListener?){
        val stickerAdapter=AdapterStickerList()
        val stickerListFromAsset=StickerListFromAsset(context)
        val groupName=stickerListFromAsset.stickerGroup!![position]
        stickerList=stickerListFromAsset.getGroupStickerList(groupName)!!
        stickerRecycler.adapter=stickerAdapter
        stickerAdapter.stickerList=stickerList
        stickerRecycler.layoutManager=GridLayoutManager(context,4)
        stickerRecycler.setHasFixedSize(true)




        stickerAdapter.setOnItemClickListener(object :AdapterStickerList.OnItemClickListener{

            override fun onItemClick(v: View, pos: Int) {
                //스티커 출력
                listener?.onCallback(stickerList[pos])
            }
        })
    }


}