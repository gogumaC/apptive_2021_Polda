package com.apptive.android.polda.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils.isEmpty
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.apptive.android.polda.*
import com.apptive.android.polda.databinding.FragmentDetailBinding
import com.apptive.android.polda.fragment.FragmentDetail.polar.polarListD
import com.apptive.android.polda.fragment.FragmentDetail.polaroidEditINFO.cutoffmode
import com.apptive.android.polda.fragment.FragmentDetail.polaroidEditINFO.deletemode
import com.apptive.android.polda.fragment.FragmentDetail.polaroidINFO.isExist
import com.apptive.android.polda.fragment.FragmentDetail.polaroidINFO.polarCount
import com.apptive.android.polda.fragment.FragmentDetail.polaroidINFO.polaroidDB
import com.apptive.android.polda.fragment.FragmentHome.diaryNameS.dbName
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer2

class FragmentDetail : Fragment() {

    private lateinit var callback: OnBackPressedCallback
    lateinit var dbHelperP : DBHelperPolaroid
    lateinit var databaseP : SQLiteDatabase
    lateinit var cursor : Cursor

    private lateinit var binding:FragmentDetailBinding

    //private val detailViewModel:ViewModelDetail by activityViewModels()
    object polaroidINFO { //폴라로이드의 정보를 넘겨 Edit에서 저장 시 값을 찾을 수 있도록 한다.
        var polarDiaryN = ""
        var polarIndex = 0
        var polarCount = 1
        var polaroidDB = ContentValues()
        var isExist = false
    }
    object polar {
        var polarListD = mutableListOf<PolaroidData>()
    }
    object polaroidEditINFO{
        var polarID = ""
        var deletemode = false //삭제 모드라면 1, 아니면 0. 기본값은 0
        var cutoffmode = false // true:잘라내기 모드
        var cutinmode = false // cutoff가 true고 폴라로이드가 선택될시 true로 전환
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_detail,container,false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deletemode = false
        var diaryN = ""
        polarCount = 1
        var visible = 0


        //DB 우선 생성. 이미 생성되어있다면 오픈
        dbHelperP = DBHelperPolaroid(context, null, 1)
        databaseP = dbHelperP.readableDatabase
        dbHelperP.onCreate(databaseP)

        cursor = databaseP.rawQuery("SELECT * FROM polaroid order by polaroidIndex asc", null)

        if(polarListD.size != 0){
            polarListD.clear()
        }
        while(cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndex("diaryName")).equals(dbName)) {
                var b1 = ""
                lateinit var image1: Bitmap
                lateinit var image2: Bitmap

                b1 = cursor.getString(cursor.getColumnIndex("image"))
                image1 = MediaStore.Images.Media.getBitmap(
                    requireActivity().getContentResolver(),
                    Uri.parse("file://" + b1)
                )

                val b2 = cursor.getString(cursor.getColumnIndex("completeImage"))
                image2 = MediaStore.Images.Media.getBitmap(
                    requireActivity().getContentResolver(),
                    Uri.parse("file://" + b2)
                )
                val id = cursor.getString(cursor.getColumnIndex("_id"))
                polarListD.add(PolaroidData(image1, id, image2))
                polarCount++
            }
        }

        val pageNum=if(polarListD.size%4==0)polarListD.size/4 else polarListD.size / 4 + 1
        Log.d("detail","pageNum : $pageNum")
        val adapter= AdapterDetailViewPager(requireContext())
        val seekBar = binding.seekBar.apply{
            setMax(pageNum-1)
            setProgress(pageNum-1)
        }
        val detailViewPager = binding.viewPager.apply {
            adapter.dataList=polarListD
            this.adapter=adapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {})
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            setCurrentItem(pageNum-1).apply{
                Log.d("detail","setting currentpage")
            }
        }


        val bookFlipPageTransformer = BookFlipPageTransformer2()
        bookFlipPageTransformer.setEnableScale(true)
        bookFlipPageTransformer.setScaleAmountPercent(10f)
        detailViewPager.setPageTransformer(bookFlipPageTransformer)


        val fabMain=binding.detailFabMain
        val fabCutoff=binding.detailFabCutoff
        val fabInsert=binding.detailFabInsert
        val fabDelete=binding.detailFabDelete
        val fabLayout=binding.fabLayout



        adapter.setOnItemStateListener(object :AdapterDetailViewPager.OnItemStateListener{
            override fun onItemState(pos:Int) {
                when(pos){
                    0->{
                        adapter.notifyDataSetChanged().apply{
                            Log.d("detail","notifyData called")
                        }
                        var tempPage=detailViewPager.currentItem
                        if(adapter.dataList.size%4==0) tempPage-=1
                        Log.d("detail","$tempPage")
                        detailViewPager.adapter=adapter//->바로되긴하는데 첫페이지로 돌아가벌임+에러 짱많,,
                        detailViewPager.setCurrentItem(tempPage).apply {
                            Log.d("detail","currentItem: $detailViewPager temp: $tempPage")
                        }
                    }
                    1->{}
                    2->{
                        findNavController().popBackStack()
                    }
                    else->{
                        when{
                            (fabDelete.getVisibility()==View.VISIBLE)->{
                                fabCutoff.setVisibility(View.GONE)
                                fabDelete.setVisibility(View.GONE)
                            }
                            (fabLayout.getVisibility()!=View.VISIBLE)->{
                                fabLayout.setVisibility(View.VISIBLE)
                            }
                            else-> {
                                val action = FragmentDetailDirections.actionGlobalShow2()
                                findNavController().navigate(action)
                            }

                        }

                    }

                }
            }
        })



        //아이템 롱클릭 이벤트
        //TODO 롱클릭 대신 홈버튼 클릭으로
        fabMain.setOnClickListener{
            Log.d("click","메뉴클릭")
            if(visible == 0) {
                fabDelete.visibility = View.VISIBLE
                fabCutoff.visibility = View.VISIBLE
                fabInsert.visibility = View.VISIBLE
                visible = 1
            }else{
                fabDelete.visibility = View.INVISIBLE
                fabCutoff.visibility = View.INVISIBLE
                fabInsert.visibility = View.INVISIBLE
                visible = 0
            }

        }


        //아이템 터치이벤트->페이지 터치이벤트
        adapter.setOnItemTouchListener(object:AdapterDetailViewPager.OnItemTouchListener {
            override fun onItemTouch(v: View, pos: Int) {
                detailViewPager.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        seekBar.progress = position
                    }
                })

            }
        })

        //아이템 터치이벤트
        adapter.setOnItemTouchListener(object:AdapterDetailViewPager.OnItemTouchListener {
            override fun onItemTouch(v: View, pos: Int) {
                detailViewPager.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        seekBar.progress = position
                    }
                })

            }
        })

        view.setOnClickListener {
            when{
                (fabDelete.getVisibility()==View.VISIBLE)->{
                    fabCutoff.setVisibility(View.GONE)
                    fabDelete.setVisibility(View.GONE)
                }
                (fabLayout.getVisibility()!=View.VISIBLE)->{
                    fabLayout.setVisibility(View.VISIBLE)
                }
            }
        }


        //fabCutoff클릭이벤트
        fabCutoff.setOnClickListener {
            //fabPaste.visibility=View.VISIBLE
            //fabCutoff.visibility=View.GONE
            cutoffmode = true
            Toast.makeText(context, "잘라낼 폴라로이드를 선택하세요.", Toast.LENGTH_SHORT).show()
        }
        //fabDelete클릭이벤트
        fabDelete.setOnClickListener {
            //fabDelete.visibility=View.GONE
            //fabCutoff.visibility=View.GONE
            deletemode = !deletemode
            if(deletemode){
                Toast.makeText(context, "삭제모드: 돌아가려면 삭제버튼을 다시 터치해주세요.", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(context, "삭제모드 해제", Toast.LENGTH_SHORT).show()
            }
        }



        //FABinsert 누르면 편집부로 전환. Diary 정보 전달
        fabInsert.setOnClickListener{

            polaroidDB.put("diaryName", FragmentHome.diaryNameS.dbName)
            polaroidDB.put("polaroidIndex", polarCount)

            polaroidINFO.polarDiaryN = FragmentHome.diaryNameS.dbName
            polaroidINFO.polarIndex = polarCount

            isExist = false
            val action=FragmentDetailDirections.actionGlobalEdit()
            it.findNavController().navigate(action)
        }
        //Fab main 길게 누르면 화면에서 사라짐
        fabMain.setOnLongClickListener(object:View.OnLongClickListener{
            override fun onLongClick(p0: View?): Boolean {
                fabLayout.setVisibility(View.GONE)
                return true
            }
        })


        seekBar.setOnTouchListener(object:View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                when (p1?.action) {
                    MotionEvent.ACTION_DOWN -> {

                    }
                    MotionEvent.ACTION_MOVE -> {

                        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                                Log.d("checkfor","touch")
                                detailViewPager.setCurrentItem(p1)
                            }

                            override fun onStartTrackingTouch(p0: SeekBar?) {

                            }

                            override fun onStopTrackingTouch(p0: SeekBar?) {

                            }
                        })
                    }
                    MotionEvent.ACTION_UP -> {
                    }
                    else -> return true
                }
                return false
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("checkfor","backccc")
                findNavController().navigate(R.id.action_fragmentDetail_to_fragmentHome)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }

    override fun onDetach() {
        cursor.close()
        callback.remove()
        super.onDetach()
    }



}