package com.apptive.android.polda

import android.graphics.Point
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat.getDisplay
import androidx.databinding.DataBindingUtil
import com.apptive.android.polda.databinding.FragmentStickerBinding
import com.apptive.android.polda.databinding.ItemStickerTabsBinding
import com.google.android.material.tabs.TabLayoutMediator


class FragmentSticker : Fragment() {
    private lateinit var binding:FragmentStickerBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_sticker,container,false)

        val viewPager=binding.stickerViewPager
        val tabLayout=binding.stickerTabLayout
        val adapter=AdapterStickerViewPager(requireContext())
        adapter.optionList=listOf()
        viewPager.adapter=adapter

        val tabTitles=listOf("옵션1","옵션2","옵션3","옵션4","옵션5")
        TabLayoutMediator(tabLayout,viewPager){tab,position->
            tab.text=tabTitles[position]
        }.attach()



        return binding.root
    }


}