package com.apptive.android.polda.fragment

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.transition.FragmentTransitionSupport
import com.apptive.android.polda.DBHelperPolaroid
import com.apptive.android.polda.R
import com.apptive.android.polda.customView.PolaroidBack

class FragmentShowPolaroid2: Fragment() {
    lateinit var dbHelperP : DBHelperPolaroid
    lateinit var databaseP : SQLiteDatabase
    var edit = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_polaroid2, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backBtn=view.findViewById<Button>(R.id.backBtn)
        val polaroidBack=view.findViewById<PolaroidBack>(R.id.polaroidBack)
        polaroidBack.setEnable()

        backBtn.setOnClickListener {
            val action=FragmentShowPolaroid2Directions.actionFragmentShowPolaroid22ToFragmentShowPolaloid()
            view.findNavController().navigate(action)
        }



    }



}