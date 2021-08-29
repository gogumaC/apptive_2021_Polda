package com.apptive_saenggamja.android.polda.fragment

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apptive_saenggamja.android.polda.DBHelperPolaroid
import com.apptive_saenggamja.android.polda.R
import com.apptive_saenggamja.android.polda.adapter.AdapterFontView
import com.apptive_saenggamja.android.polda.customView.MemoState
import com.apptive_saenggamja.android.polda.customView.PolaroidBack
import com.apptive_saenggamja.android.polda.databinding.FragmentEditMemoBinding
import com.apptive_saenggamja.android.polda.viewModel.ViewModelEditTempState

@RequiresApi(Build.VERSION_CODES.O)
class FragmentEditMemo: Fragment() {

    private lateinit var binding: FragmentEditMemoBinding
    private val model:ViewModelEditTempState by activityViewModels()
    private lateinit var polaroidBack:PolaroidBack
    private lateinit var callback:OnBackPressedCallback

    private var state:MemoState?=null
    lateinit var dbHelperP : DBHelperPolaroid
    lateinit var databaseP : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        state=if(model.isInit){model.tempBackState.getValue()}
        else{ null }
        getActivity()?.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_edit_memo,container,false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnFlip=binding.memoEditBtnFlip
        polaroidBack=binding.polaroidBack
        lateinit var title: String
        lateinit var memo: String
        lateinit var hash: MutableList<String>
        val space=binding.fontViewContainer
        dbHelperP = DBHelperPolaroid(context, null, 1)
        databaseP = dbHelperP.readableDatabase
        dbHelperP.onCreate(databaseP)

        var cursor : Cursor = databaseP.rawQuery("SELECT _id, memo, tag, font FROM polaroid", null)



        polaroidBack.restoreMemoState(model.tempBackState.value!!)

        val fontList=List(8,{index ->index  })
        val fontAdapter= AdapterFontView().apply{
            this.fontList=fontList
            this.onCallback(object:AdapterFontView.OnCallbackListener{
                @RequiresApi(Build.VERSION_CODES.O)
                override fun setOnCallback(font: Int) {
                    super.setOnCallback(font)
                    setFont(font)
                }
            })
        }
        val fontView=binding.fontRecyclerView
        fontView.adapter=fontAdapter
        fontView.layoutManager=LinearLayoutManager(requireContext())
        val fontBtn=binding.btnFont

        space.setOnClickListener {
            if(space.visibility==View.VISIBLE){
                space.visibility=View.GONE
            }
        }
        fontBtn.setOnClickListener {
            space.setVisibility(View.VISIBLE)
        }



        Log.d("FragmentEditMemo",model.tempBackState.getValue().toString())


        btnFlip.setOnClickListener {
            model.setBackState(polaroidBack.getState())
            view.findNavController().popBackStack()
        }

        view.setOnClickListener {
            val inputManager=requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),0)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun setFont(fontNum:Int){

        val temp=polaroidBack.getState()
        temp.fontNum=fontNum
        polaroidBack.restoreMemoState(temp)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback=object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(binding.fontViewContainer.visibility==View.VISIBLE){
                    binding.fontViewContainer.visibility=View.GONE
                }
                else{findNavController().popBackStack()}

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }


    var strSeparator = "__,__"
    fun convertStringToArray(str: String): MutableList<String> {
        return str.split(strSeparator.toRegex()).toMutableList()
    }


}