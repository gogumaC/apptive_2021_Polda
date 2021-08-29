package com.apptive_saenggamja.android.polda.customView

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.apptive_saenggamja.android.polda.AdapterStickerViewPager
import com.apptive_saenggamja.android.polda.R
import com.apptive_saenggamja.android.polda.StickerListFromAsset
import com.apptive_saenggamja.android.polda.databinding.FragmentStickerBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.lang.Exception

class StickerListView @kotlin.jvm.JvmOverloads constructor(context: Context, attrs: AttributeSet?=null,defStyleAttr:Int=0)
    : LinearLayout(context,attrs,defStyleAttr)
{
    private val binding:FragmentStickerBinding

    interface CallbackListener{
        fun callBack(stickerName:Drawable)
    }
    var listener:CallbackListener?=null
    fun setCallbackListener(listener:CallbackListener){
        this.listener=listener
    }
    init{
        binding=FragmentStickerBinding.inflate(LayoutInflater.from(context),this,true)

        val stickerListFromAsset=StickerListFromAsset(context)
        val viewPager=binding.stickerViewPager
        val tabLayout=binding.stickerTabLayout
        val adapter= AdapterStickerViewPager(context)
        val tabTitles=stickerListFromAsset.stickerGroup!!.toList()

        try{
            adapter.optionList=tabTitles


        }catch(e:Exception){
            Log.d("err","StickerListView : tabTitles가 null임")
        }

        viewPager.adapter=adapter

        val icons=arrayOf<Int>(
            R.drawable.diamond,
            R.drawable.polygon,
            R.drawable.star,
            R.drawable.union,
            R.drawable.sticker_tab_ellipse,
            R.drawable.diamond
        )

        TabLayoutMediator(tabLayout,viewPager){tab,position->
                tab.setIcon(ContextCompat.getDrawable(context, icons[position]))
        }.attach()
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL)
        adapter.onCallback(object:AdapterStickerViewPager.CallBackListener{
            override fun onCallback(sticker: Drawable) {
                listener?.callBack(sticker)
            }
        })
    }

//    fun getIconDrawable(pos:Int):Drawable{
//        val icons=arrayOf<Int>(R.drawable.union,R.drawable.sticker_tab_ellipse,R.drawable.star,R.drawable.polygon,R.drawable.diamond)
//        if()
//        val drawable=ContextCompat.getDrawable(context,icons[pos])
//    }



}