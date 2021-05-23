package com.apptive.android.polda.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.transition.FragmentTransitionSupport
import com.apptive.android.polda.R

class FragmentShowPolaroid2: Fragment() {

    var edit = 0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_polaroid2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO("ERR:수정")
//        val editMemoBtn = view.findViewById<Button>(R.id.editMemoBtn)
//        val editMemo = view.findViewById<EditText>(R.id.memoView)

//        editMemoBtn.setOnClickListener{
//            if(edit == 0){
//                editMemo.setEnabled(true)
//                edit = 1
//            }else{
//                editMemo.setEnabled(false)
//                edit = 0
//            }
//        }
    }
}