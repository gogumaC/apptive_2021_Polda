package com.apptive_saenggamja.android.polda.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences.Editor
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils.isEmpty
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.marginRight
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.apptive_saenggamja.android.polda.*
import com.apptive_saenggamja.android.polda.customView.Diary
import com.apptive_saenggamja.android.polda.databinding.FragmentHomeBinding
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class FragmentHome: Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var callback:OnBackPressedCallback
    //TODO("태그리스트 불러오기->포커싱 들어오면")
    private var tagList=mutableListOf<String>()

    var diaryList : MutableList<Diary> = mutableListOf()
    var sortList : MutableList<Diary> = mutableListOf()
    var sort = false //false(0) 시간순, true(1) 사진많은순
    var imagePos = 0
    var sortPos = 0

    lateinit var dbHelperD : DBHelperDiary
    lateinit var databaseD : SQLiteDatabase
    lateinit var dbHelperP : DBHelperPolaroid
    lateinit var databaseP : SQLiteDatabase
    lateinit var cursor: Cursor
    lateinit var viewPager : ViewPager2
    lateinit var adapter : AdapterHomeList
    lateinit var cursorP: Cursor

    lateinit var sp : SharedPreferences

    object diaryNameS{
        var dbName = "test"
    }

    fun save(i: Boolean) {
        sp = requireContext().getSharedPreferences("sp", MODE_PRIVATE)
        val editor: Editor = sp.edit()
        editor.putBoolean("save", i)
        editor.apply()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivity()?.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var sf : SharedPreferences = requireContext().getSharedPreferences("sp", MODE_PRIVATE)
        sort = sf.getBoolean("save", false)

        //DB 우선 생성. 이미 생성되어있다면 오픈
        dbHelperD = DBHelperDiary(context, null, 1)
        databaseD = dbHelperD.readableDatabase
        dbHelperD.onCreate(databaseD)

        cursor = databaseD.rawQuery("SELECT * FROM diary", null)


        dbHelperP = DBHelperPolaroid(context, null, 1)
        databaseP = dbHelperP.readableDatabase

        cursorP = databaseP.rawQuery("SELECT * FROM polaroid", null)
        adapter = AdapterHomeList(requireContext())


        makeList()



        if(sort){
            adapter.item = sortList
        }else{ // 최신순
            adapter.item = diaryList
        }




        /**뷰페이저 관련부**/
        val inputMethodManager=requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val searchTextView=binding.searchTextView
        val mainBtn = binding.floatingBtnMain
        val delBtn = binding.floatingBtnDelete
        val sortBtn = binding.floatingBtnSort
        val addBtn = binding.floatingBtnAdd
        val modeBtn = binding.modeSwitch
        val container=binding.fragmentContainer
        viewPager = binding.homeList.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {})
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        var delmode = false

        viewPager.adapter = adapter

        if(container.visibility== VISIBLE){
            mainBtn.visibility=GONE
        }



        /**뷰페이저 preView구현**/
        viewPager.setClipToPadding(false)
        viewPager.setClipChildren(false)
        viewPager.setOffscreenPageLimit(200)
        val pageMarginPx = viewPager.marginRight
        val pagerWidth = viewPager.getWidth()
        val screenWidth = resources.displayMetrics.widthPixels
        val offsetPx = screenWidth - pageMarginPx - pagerWidth
        viewPager.setPadding(50,0,50,0)
        viewPager.setPageTransformer { page, position ->
            page.translationX = position * -offsetPx
        }
        viewPager.setPageTransformer(MarginPageTransformer(20))



        /**검색&자동완성 구현**/

        if(tagList.size != 0){
            tagList.clear()
        }

        if(cursorP.moveToFirst()){
            do{
                var i = 0
                if(!isEmpty(cursorP.getString(cursorP.getColumnIndex("tag")))){
                    var hashTag = convertStringToArray(cursorP.getString(cursorP.getColumnIndex("tag")))
                    while(i < hashTag!!.size) {
                        if (!tagList.contains(hashTag.get(i))){
                            tagList.add(hashTag.get(i)!!)
                        }
                        i++
                    }
                }
            }while(cursorP.moveToNext())
        }

        val adapterSearchBar=ArrayAdapter(this.requireContext(),android.R.layout.simple_dropdown_item_1line,tagList)
        searchTextView.setAdapter(adapterSearchBar)
        searchTextView.setOnKeyListener (object:View.OnKeyListener{
            override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
                if((p2?.getAction()==KeyEvent.ACTION_DOWN)&&(p1==KeyEvent.KEYCODE_ENTER)){
                    if(container.visibility!= VISIBLE){
                        container.visibility= VISIBLE
                        mainBtn.visibility= GONE
                    }
                    //키보드 비활성화
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0)
                    refreshFragment(searchTextView.text.toString())
                    return true
                }
                return false
            }
        })

        /**검색 후 분류 구현**/


        /**fab구현**/
        var visible = 0

        //모드 바꾸기
        modeBtn.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                modeBtn.setBackgroundResource(R.drawable.switch_on)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                modeBtn.setBackgroundResource(R.drawable.switch_off)
            }

        }

        //mainBtn.show()

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

        sortBtn.setOnClickListener(View.OnClickListener {
            sort = !sort
            save(sort)
            if(sort) { //사진많은 순 이라면
                adapter.item = sortList
                Toast.makeText(context, "사진개수 순으로 정렬합니다.", Toast.LENGTH_SHORT).show()
            }else{
                adapter.item = diaryList
                Toast.makeText(context, "생성날짜 순으로 정렬합니다.", Toast.LENGTH_SHORT).show()
            }
            viewPager.adapter = adapter

        })

        //다이어리 추가, 새로운 DB 구성
        addBtn.setOnClickListener(View.OnClickListener {
            val dateAndtime: LocalDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val formatted = dateAndtime.format(formatter)
            var diaryName = ContentValues()
            var dateCount = 0;
            var diaryT : EditText = EditText(requireContext())
            diaryT.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert))
            var builder = AlertDialog.Builder(context)
            builder.setTitle("다이어리 제목을 입력하세요.")
            builder.setView(diaryT)

            var listener = object :
                DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    //제목입력 완료, DB생성

                    var dateStr = ""

                    if(cursor.moveToFirst()){
                        do{
                            var str = cursor.getString(cursor.getColumnIndex("date"))
                            if (str.substring(0,8).equals(formatted)){
                                dateCount++
                                if(str.length > 8){
                                    dateStr = "_" + (str.substring(str.indexOf("_") + 1).toInt() + 1).toString()
                                }
                            }
                        }while(cursor.moveToNext())
                    }
                    if(dateCount == 1 && isEmpty(dateStr)){
                        diaryNameS.dbName = formatted + "_1"
                    }else {
                        diaryNameS.dbName = formatted + dateStr
                    }
                    diaryName.put("date", diaryNameS.dbName)
                    diaryName.put("title", diaryT.text.toString())
                    databaseD.insert("diary", null, diaryName);


                    //화면 전환

                    val action=FragmentHomeDirections.actionFragmentHomeToFragmentDetail()
                    view.findNavController().navigate(action)
                }
            }

            builder.setPositiveButton("확인", listener)
            builder.setNegativeButton("취소", null)
            builder.show()



        })

        //다이어리 삭제
        delBtn.setOnClickListener(View.OnClickListener {
            delmode = !delmode
            if(delmode) {
                Toast.makeText(context, "삭제모드: 돌아가려면 삭제버튼을 눌러주세요.", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "삭제모드: 해제", Toast.LENGTH_SHORT).show()
            }
        })


        /**화면 전환 이벤트 구현**/
        //HOME->DETAIL
        adapter.setOnItemClickListener(object:AdapterHomeList.OnItemClickListener{
            override fun onItemClick(v: View, pos: Int) {
                if(delmode){ //다이어리 삭제 구현
                    var builder = AlertDialog.Builder(context)
                    builder.setMessage("삭제하시겠습니까?")

                    var listener = object :
                        DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            cursor.moveToFirst()
                            var deleteList = diaryList
                            if(sort){
                                deleteList = sortList
                            }
                            do{
                                var delName = cursor.getString(cursor.getColumnIndex("date"))
                                if(delName.equals(deleteList.get(pos).date)){
                                    var arr : Array<String> = arrayOf(deleteList.get(pos).date)
                                    if(cursorP.moveToFirst()) {
                                        do {
                                            if (delName.equals(
                                                    cursorP.getString(
                                                        cursorP.getColumnIndex(
                                                            "diaryName"
                                                        )
                                                    )
                                                )
                                            ) {
                                                databaseP.delete("polaroid", "diaryName=?", arr)
                                            }
                                        } while (cursorP.moveToNext())
                                    }
                                    databaseD.delete("diary", "date=?", arr)
                                    deleteList.remove(deleteList.get(pos))
                                    viewPager.adapter = adapter
                                    break
                                }
                            }while(cursor.moveToNext())
                        }
                    }

                    builder.setPositiveButton("네", listener)
                    builder.setNegativeButton("아니오", null)
                    builder.show()
                }else {
                    cursor.moveToFirst()
                    var i = 0
                    if (sort) {
                        do {
                            if (cursor.getString(cursor.getColumnIndex("date"))
                                    .equals(sortList.get(pos).date)
                            ) {
                                diaryNameS.dbName = cursor.getString(cursor.getColumnIndex("date"))
                                break
                            }
                            i++
                        } while (cursor.moveToNext())
                    } else {
                        do {
                            if (cursor.getString(cursor.getColumnIndex("date"))
                                    .equals(diaryList.get(pos).date)
                            ) {
                                diaryNameS.dbName = cursor.getString(cursor.getColumnIndex("date"))
                                break
                            }
                            i++
                        } while (cursor.moveToNext())
                    }


                    val action = FragmentHomeDirections.actionFragmentHomeToFragmentDetail()
                    v.findNavController().navigate(action)
                    //키보드 비활성화
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)
                }
            }
        })

        adapter.setEditClickListener(object:AdapterHomeList.OnEditClickListener{
            override fun onEditClick(v: View, pos: Int) {
                imagePos = pos
                openGallery()
            }
        })



        adapter.setOnTitleClickListener(object:AdapterHomeList.OnTitleClickListener{
            override fun onTitleClick(v: View, pos: Int) {
                var diaryName = ContentValues()
                var dateCount = 0;
                var diaryT : EditText = EditText(requireContext())
                diaryT.setTextColor(ContextCompat.getColor(requireContext(), R.color.alert))
                var builder = AlertDialog.Builder(context)
                builder.setTitle("다이어리 제목을 입력하세요.")
                builder.setView(diaryT)

                var listener = object :
                    DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        //제목입력 완료, DB 업데이트!!!!!!!!!!

                        cursor.moveToFirst()

                        if(!sort){
                            do{
                                if (cursor.getString(cursor.getColumnIndex("date")).equals(diaryList.get(pos).date)){
                                    var arr : Array<String> = arrayOf(diaryList.get(pos).date)
                                    diaryName.put("title", diaryT.text.toString())
                                    databaseD.update("diary", diaryName, "date=?", arr)
                                    break
                                }
                            }while(cursor.moveToNext())
                        }else{
                            do{
                                if (cursor.getString(cursor.getColumnIndex("date")).equals(sortList.get(pos).date)){
                                    var arr : Array<String> = arrayOf(sortList.get(pos).date)
                                    diaryName.put("title", diaryT.text.toString())
                                    databaseD.update("diary", diaryName, "date=?", arr)
                                    break
                                }
                            }while(cursor.moveToNext())
                        }

                        cursor.close()
                        cursor = databaseD.rawQuery("SELECT * FROM diary", null)

                        makeList()
                        if(sort){
                            adapter.item = sortList
                        }else{
                            adapter.item = diaryList
                        }
                        viewPager.adapter = adapter


                    }
                }

                builder.setPositiveButton("확인", listener)
                builder.setNegativeButton("취소", null)
                builder.show()
            }
        })



        //SEARCH->SHOW_F
        //TODO("화면전환시 폴라로이드 이름&비트맵 등 전달")


    }

    fun makeList(){
        var sortCountList : ArrayList<SortList> = ArrayList()
        if(diaryList.size != 0){
            diaryList.clear()
        }
        if(sortList.size != 0){
            sortList.clear()
        }
        if(cursor.moveToFirst()) {
            do {
                if (!isEmpty(cursor.getString(cursor.getColumnIndex("image")))) {
                    var b = cursor.getString(cursor.getColumnIndex("image"))
                    var imageD = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(),
                        Uri.parse("file://" + b)
                    )
                    diaryList.add(
                        Diary(
                            cursor.getString(cursor.getColumnIndex("_id")),
                            cursor.getString(cursor.getColumnIndex("date")),
                            cursor.getString(cursor.getColumnIndex("title")),
                            imageD
                        )
                    )

                } else {
                    diaryList.add(
                        Diary(
                            cursor.getString(cursor.getColumnIndex("_id")),
                            cursor.getString(cursor.getColumnIndex("date")),
                            cursor.getString(cursor.getColumnIndex("title")),
                            requireContext().resources.getDrawable(R.drawable.diary_cover)
                                .toBitmap()
                        )
                    )
                }
            } while (cursor.moveToNext()) //최신순 정렬 완료
        }
        if(cursor.moveToFirst()){
            do {
                var dateForSort = cursor.getString(cursor.getColumnIndex("date"))
                var sortCount = 0
                if(cursorP.moveToFirst()){
                    do {
                        if (cursorP.getString(cursorP.getColumnIndex("diaryName"))
                                .equals(dateForSort)
                        ){
                            sortCount++
                        }
                    } while (cursorP.moveToNext())
                }
                sortCountList.add(
                    SortList(
                        cursor.getString(cursor.getColumnIndex("date")),
                        sortCount
                    )
                )
            } while (cursor.moveToNext())

            //다이어리 파일명+다이어리 카운트 별로 리스트 생성

            //그대로 정렬 필요

            val sortCountList2 = sortCountList.sortedByDescending { it.int }
            var i = 0
            cursor.moveToFirst()
            if(sortCountList2.size != 0) {
                do {
                    if (sortCountList2.get(i).string.equals(cursor.getString(cursor.getColumnIndex("date")))) {
                        if (!isEmpty(cursor.getString(cursor.getColumnIndex("image")))) {
                            var b = cursor.getString(cursor.getColumnIndex("image"))
                            var imageD = MediaStore.Images.Media.getBitmap(
                                requireActivity().getContentResolver(),
                                Uri.parse("file://" + b)
                            )
                            sortList.add(
                                Diary(
                                    cursor.getString(cursor.getColumnIndex("_id")),
                                    cursor.getString(cursor.getColumnIndex("date")),
                                    cursor.getString(cursor.getColumnIndex("title")),
                                    imageD
                                )
                            )}else{
                            sortList.add(
                                Diary(
                                    cursor.getString(cursor.getColumnIndex("_id")),
                                    cursor.getString(cursor.getColumnIndex("date")),
                                    cursor.getString(cursor.getColumnIndex("title")),
                                    requireContext().resources.getDrawable(R.drawable.diary_cover)
                                        .toBitmap())
                            )
                        }
                        i++
                    }
                    if (!cursor.moveToNext()) {
                        cursor.moveToFirst()
                    }
                } while (i < sortCountList2.size)
            }
            //정렬 끝. sortList 구성 완료
        }


    }

    fun openGallery() {
        CropImage.activity().setAspectRatio(332,500).start(requireContext(), this)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                //라이브러리사용 -> 크롭해서 넣기
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    val resultUri = MediaStore.Images.Media.getBitmap(
                        requireContext().getContentResolver(),
                        result.uri
                    )

                    val diaryDB = ContentValues()
                    val strFolderPath: String? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
                    val folder = File(strFolderPath)
                    if (!folder.exists()) {
                        folder.mkdirs()
                    }

                    var out: OutputStream? = null
                    var strFilePath = strFolderPath + diaryList[imagePos].id + "_cover"
                    if(sort){
                        strFilePath = strFolderPath + sortList[imagePos].id + "_cover"
                    }
                    val fileCacheItem = File(strFilePath)
                    fileCacheItem.createNewFile()
                    out = FileOutputStream(fileCacheItem)
                    resultUri.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out.close()

                    diaryDB.put(
                        "image",
                        strFilePath
                    )
                    cursor.moveToFirst()
                    do {
                        if (!sort && cursor.getString(cursor.getColumnIndex("date"))
                                .equals(diaryList.get(imagePos).date)
                        ){
                            var arr: Array<String> = arrayOf(diaryList.get(imagePos).date)
                            databaseD.update("diary", diaryDB, "date=?", arr)
                            break
                        }else if(sort && cursor.getString(cursor.getColumnIndex("date")).equals(sortList.get(imagePos).date)){
                            var arr: Array<String> = arrayOf(sortList.get(imagePos).date)
                            databaseD.update("diary", diaryDB, "date=?", arr)
                            break
                        }
                    } while (cursor.moveToNext())

                    cursor.close()
                    cursor = databaseD.rawQuery("SELECT * FROM diary", null)
                    makeList()
                    if(sort){
                        adapter.item = sortList
                    }else {
                        adapter.item = diaryList
                    }
                    viewPager.adapter = adapter

                }
            }
        }
    }

    fun refreshFragment(searchTag:String){
        val searchFragment=FragmentSearch(searchTag)
        val transaction=getChildFragmentManager().beginTransaction()
        transaction.replace(R.id.fragmentContainer ,searchFragment)
        transaction.commit()
    }

    //backpress구현
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback=object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(binding.fragmentContainer.visibility==View.VISIBLE){
                    binding.fragmentContainer.visibility=View.GONE
                    binding.floatingBtnMain.visibility= VISIBLE
                }
                else{
                    android.os.Process.killProcess(android.os.Process.myPid())
                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }

    override fun onDetach() {
        cursor.close()
        super.onDetach()
        callback.remove()
    }

    var strSeparator = "__,__"
    fun convertStringToArray(str: String): MutableList<String?>? {
        return str.split(strSeparator.toRegex()).toMutableList()
    }


}