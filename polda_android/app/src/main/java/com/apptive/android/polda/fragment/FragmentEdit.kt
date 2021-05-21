package com.apptive.android.polda.fragment

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.apptive.android.polda.R
import com.apptive.android.polda.databinding.FragmentEditBinding


class FragmentEdit : Fragment() {
    private lateinit var binding:FragmentEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_edit,container,false)



        return binding.root
    }


}