package com.apptive_saenggamja.android.polda.adapter

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.apptive_saenggamja.android.polda.R
import com.apptive_saenggamja.android.polda.customView.MemoState
import com.apptive_saenggamja.android.polda.customView.PolaroidBack


class AdapterShowViewPager(val context: Context): RecyclerView.Adapter<HolderShow>() {

    var polaroidData: ShowPolaroidData?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderShow {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_show_container,parent,false)
        return HolderShow(view,context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HolderShow, position: Int) {
        holder.bind(position,polaroidData)
    }

    override fun getItemCount(): Int {
        return 2
    }
}

class HolderShow(itemView: View,val context: Context) : RecyclerView.ViewHolder(itemView){

    val polaroidFront=ImageView(context)
    @RequiresApi(Build.VERSION_CODES.O)
    val polaroidBack=PolaroidBack(context)
    val container=itemView.findViewById<ConstraintLayout>(R.id.container)
    val lp= ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(pos:Int, data:ShowPolaroidData?=null){
        //TODO("데이터 처리 코드 추가 : data로 넘겨받음")
       when(pos){
            0->{
                var bitmap: Bitmap = data!!.image
                polaroidFront.setImageBitmap(bitmap)
                polaroidFront.layoutParams=lp

                container.addView(polaroidFront)
            }
            1->{
                val sampleMemoState=MemoState(data!!.title,data.memo,data.hash,data.font)
                polaroidBack.setEnable()
                polaroidBack.layoutParams=lp
                Log.d("checkfor","가져오는 메모"+data.hash.toString())
                polaroidBack.restoreShowMemoState(sampleMemoState)
                container.addView(polaroidBack)
            }
        }

    }
}

data class ShowPolaroidData(var image: Bitmap, var title: String, var memo: String, var font: Int, var hash: MutableList<String> = mutableListOf<String>()){
}