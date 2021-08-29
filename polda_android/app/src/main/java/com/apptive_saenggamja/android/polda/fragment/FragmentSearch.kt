package com.apptive_saenggamja.android.polda.fragment

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.apptive_saenggamja.android.polda.*
import com.apptive_saenggamja.android.polda.databinding.FragmentSearchBinding
import com.apptive_saenggamja.android.polda.fragment.FragmentDetail.polaroidEditINFO.polarID


class FragmentSearch(val searchTag:String) : Fragment() {

    private lateinit var binding : FragmentSearchBinding

    var polarListS = mutableListOf<PolaroidData>()
    lateinit var dbHelperP : DBHelperPolaroid
    lateinit var databaseP : SQLiteDatabase

    val adapter = AdapterSearchList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelperP = DBHelperPolaroid(context, null, 1)
        databaseP = dbHelperP.readableDatabase
        dbHelperP.onCreate(databaseP)

        var cursor : Cursor = databaseP.rawQuery("SELECT * FROM polaroid", null)


        if(!cursor.moveToFirst() || polarListS.size != 0){
            polarListS.clear()
        }else if(cursor.moveToFirst()){
            do{
                var i = 0
                if(cursor.getString(cursor.getColumnIndex("tag")) != null){
                    var hashTag = convertStringToArray(cursor.getString(cursor.getColumnIndex("tag")))
                    while(i < hashTag!!.size) {
                        if (hashTag!!.get(i).equals(searchTag)){
                            val b1 = cursor.getString(cursor.getColumnIndex("image"))
                            lateinit var image1: Bitmap
                            lateinit var image2: Bitmap
                            image1 = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), Uri.parse("file://" + b1));
                            val b2 = cursor.getString(cursor.getColumnIndex("completeImage"))
                            image2 = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), Uri.parse("file://" + b2))
                            val id = cursor.getString(cursor.getColumnIndex("_id"))
                            polarListS.add(PolaroidData(image1, id, image2))
                            break
                        }
                        i++

                    }
                }
            }while(cursor.moveToNext())
        }

        val recyclerView = binding.searchList
        adapter.item = polarListS
        adapter.itemClick = object: AdapterSearchList.ItemClick {
            override fun onClick(view: View, position: Int) {
                polarID = polarListS.get(position).date
                val action=FragmentShowPolaloidDirections.actionGlobalShow2()
                findNavController().navigate(action)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.clipToPadding=false
    }

    //DB에 담겨있는 해시태그를 다시 list로 만드는 함수

    var strSeparator = "__,__"
    fun convertStringToArray(str: String): MutableList<String?>? {
        return str.split(strSeparator.toRegex()).toMutableList()
    }


}