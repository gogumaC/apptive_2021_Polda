package com.apptive_saenggamja.android.polda.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.apptive_saenggamja.android.polda.R

class AdapterFontView():RecyclerView.Adapter<ViewHolderFont>() {
    interface OnCallbackListener{
        fun setOnCallback(font:Int){
        }
    }
    var listener:OnCallbackListener?=null
    fun onCallback(listener:OnCallbackListener?){
        this.listener=listener
    }

    var fontList= listOf<Int>()
    private lateinit var context:Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFont {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_font,parent,false)
        context=parent.context
        return ViewHolderFont(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolderFont, position: Int) {
        holder.bind(position,context)
        holder.itemView.setOnClickListener { listener?.setOnCallback(position)  }
    }

    override fun getItemCount(): Int {
        return fontList.size
    }
}
class ViewHolderFont(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(pos:Int, context: Context){
        val textView=itemView.findViewById<TextView>(R.id.fontTextView)

        val fontId=when(pos){
            0->R.font.binggrae_samanco
            1->R.font.cafe24oneprettynight
            2->R.font.goodtoday_medium
            3->R.font.kyobo_handwriting
            4->R.font.maruburi_regular
            5->R.font.nanum_squarer
            6->R.font.scdream6
            7->R.font.uhbee_ann
            else->R.font.font
        }
        val tf= context.resources.getFont(fontId)
        textView.setTypeface(tf)
    }
}