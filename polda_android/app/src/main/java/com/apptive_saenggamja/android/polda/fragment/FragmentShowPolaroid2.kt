package com.apptive_saenggamja.android.polda.fragment

import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.apptive_saenggamja.android.polda.DBHelperPolaroid
import com.apptive_saenggamja.android.polda.R
import com.apptive_saenggamja.android.polda.customView.PolaroidBack

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