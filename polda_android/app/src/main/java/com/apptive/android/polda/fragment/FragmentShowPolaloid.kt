package com.apptive.android.polda.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.core.view.GestureDetectorCompat
import androidx.navigation.findNavController
import com.apptive.android.polda.R

import com.apptive.android.polda.customView.PolaroidFront


class FragmentShowPolaloid : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_polaloid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnEdit=view.findViewById<Button>(R.id.btnEdit)
        val polaroid=view.findViewById<PolaroidFront>(R.id.polaroidFront)


        btnEdit.setOnClickListener {
            val action=FragmentShowPolaloidDirections.actionGlobalEdit()
            view.findNavController().navigate(action)
        }

        //TODO("스와이프 구현해서 변경하기")
        polaroid.setOnClickListener {
            val action=FragmentShowPolaloidDirections.actionFragmentShowPolaloidToFragmentShowPolaroid22()
            view.findNavController().navigate(action)
        }





    }
}