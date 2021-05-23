package com.apptive.android.polda.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apptive.android.polda.AdapterHomeList
import com.apptive.android.polda.R
import com.apptive.android.polda.SampleList
import com.apptive.android.polda.databinding.FragmentHomeBinding

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

        //화면전환 클릭이벤트 구현
        adapter.setOnItemClickListener(object:AdapterHomeList.OnItemClickListener{
            override fun onItemClick(v: View, pos: Int) {
                //TODO("화면전환")
                val action=FragmentHomeDirections.actionFragmentHomeToFragmentDetail()
                v.findNavController().navigate(action)
            }
        })

        return binding.root
    }



}