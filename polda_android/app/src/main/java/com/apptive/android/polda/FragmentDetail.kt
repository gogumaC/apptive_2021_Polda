package com.apptive.android.polda

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.apptive.android.polda.databinding.FragmentDetailBinding

class FragmentDetail : Fragment() {
    private lateinit var binding:FragmentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_detail,container,false)
        val sampleList=listOf("1","2","3","4","5")
        val adapter=CustomPagerAdapter()
        adapter.sampleTitleList=sampleList
        binding.viewPager.adapter=adapter


        return binding.root
    }


}