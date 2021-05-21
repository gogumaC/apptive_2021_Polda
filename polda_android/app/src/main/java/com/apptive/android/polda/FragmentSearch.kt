package com.apptive.android.polda

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.apptive.android.polda.databinding.FragmentSearchBinding


class FragmentSearch : Fragment() {

    private lateinit var binding : FragmentSearchBinding


    var sample : MutableList<SampleList> = mutableListOf(
        SampleList("1"),
        SampleList("2"),
        SampleList("3"),
    )

    val adapter = AdapterSearchList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        val recyclerView = binding.searchList
        adapter.item = sample
        adapter.itemClick = object: AdapterSearchList.ItemClick {
            override fun onClick(view: View, position: Int) {
                Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        return binding.root
    }
}