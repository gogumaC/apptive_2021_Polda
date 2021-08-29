package com.apptive.android.polda.customView

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.apptive.android.polda.R


class TagView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var textView:TextView
    private var btnDel: ImageView

    var text:String
    set(value){
        textView.text=value
    }
    get(){
        return textView.text.toString()
    }

    interface BackButtonClickListener{
        fun setOnDelButtonClick(v: View,text:String)
    }
    var listener:BackButtonClickListener?=null
    fun delButtonClicked(listener:BackButtonClickListener){
        this.listener=listener
    }


    init{
        LayoutInflater.from(context).inflate(R.layout.item_tag, this, true)
        textView=findViewById(R.id.tagText)
        btnDel=findViewById(R.id.btnDelete)

        btnDel.setOnClickListener {
            listener?.setOnDelButtonClick(this,text)
        }

    }
    fun setDelButton(){
        btnDel.visibility=GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setFont(tf: Typeface){
        textView.setTypeface(tf)
    }




    override fun setBackground(background: Drawable?) {
        super.setBackground(background)
    }

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        super.setLayoutParams(params)
    }
}