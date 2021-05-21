package com.apptive.android.polda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.SCROLL_AXIS_HORIZONTAL
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apptive.android.polda.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentHome: Fragment() {

    private lateinit var binding : FragmentHomeBinding


    var sample : MutableList<SampleList> = mutableListOf(
        SampleList("1"),
        SampleList("2"),
        SampleList("3")
    )

    val adapter = AdapterHomeList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        val recyclerView = binding.homeList
        adapter.item = sample
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext()).also { it.orientation = LinearLayoutManager.HORIZONTAL }
        var visible = 0
        val mainBtn = binding.floatingBtnMain
        val delBtn = binding.floatingBtnDelete
        val sortBtn = binding.floatingBtnSort
        val addBtn = binding.floatingBtnAdd
        mainBtn.setOnClickListener(View.OnClickListener{
            if(visible == 0) {
                delBtn.visibility = VISIBLE
                sortBtn.visibility = VISIBLE
                addBtn.visibility = VISIBLE
                visible = 1
            }else{
                delBtn.visibility = View.INVISIBLE
                sortBtn.visibility = View.INVISIBLE
                addBtn.visibility = View.INVISIBLE
                visible = 0
            }
        })

        return binding.root
    }



}