package com.apptive.android.polda.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils.isEmpty
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.apptive.android.polda.DBHelperPolaroid
import com.apptive.android.polda.R
import com.apptive.android.polda.StickerFormTrans
import com.apptive.android.polda.customView.MemoState
import com.apptive.android.polda.customView.StickerListView
import com.apptive.android.polda.databinding.FragmentEditBinding
import com.apptive.android.polda.fragment.FragmentDetail.polaroidEditINFO.polarID
import com.apptive.android.polda.fragment.FragmentDetail.polaroidINFO.isExist
import com.apptive.android.polda.fragment.FragmentDetail.polaroidINFO.polaroidDB
import com.apptive.android.polda.sticker.*
import com.apptive.android.polda.viewModel.ViewModelEditTempState
import com.theartofdev.edmodo.cropper.CropImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class FragmentEdit : Fragment() {
    private lateinit var binding: FragmentEditBinding
    private lateinit var polaroidImage: ImageView
    private lateinit var callback: OnBackPressedCallback
    private lateinit var stickerView: StickerView
    private val model: ViewModelEditTempState by activityViewModels()

    //private val model:ViewModelEditSaveState by viewModels()
    private var tempStickerState: TempEdit? = null

    lateinit var dbHelperP: DBHelperPolaroid
    lateinit var databaseP: SQLiteDatabase
    lateinit var cursor: Cursor
    var resultUri : Uri? = null

//    var stickerKind: MutableList<Bitmap> = mutableListOf<Bitmap>()
//    var stickerGrid: MutableList<Array<String>> = mutableListOf<Array<String>>()
//    var stickerFlip: MutableList<Boolean> = mutableListOf<Boolean>()
//    var stickerData: MutableList<StickerData> = mutableListOf<StickerData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**뷰모델 초기화**/
        model.reset(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit, container, false)
        Log.d("create", "FragmentEdit 크리에이트뷰 ; onCreateView")
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val polaroidEditView = binding.editPolaroidView
        stickerView = binding.StickerView

        //DB 오픈
        dbHelperP = DBHelperPolaroid(context, null, 1)
        databaseP = dbHelperP.readableDatabase
        dbHelperP.onCreate(databaseP)

        cursor = databaseP.rawQuery("SELECT * FROM polaroid", null)

        if(isExist) {
            while (cursor.moveToNext()) { //편집 선택한 폴라로이드에 커서를 둔다
                if (cursor.getString(cursor.getColumnIndex("diaryName"))
                        .equals(FragmentDetail.polaroidINFO.polarDiaryN)
                    && cursor.getInt(cursor.getColumnIndex("polaroidIndex")) == FragmentDetail.polaroidINFO.polarIndex
                ) {
                    if (!isEmpty(cursor.getString(cursor.getColumnIndex("memo")))) {
                        var memoDB = cursor.getString(cursor.getColumnIndex("memo"))
                        var memoArr = convertStringToArray(memoDB)
                        var title = memoArr.get(0)
                        var memo = memoArr.get(1)
                        var hash =
                            convertStringToArray(cursor.getString(cursor.getColumnIndex("tag")))
                        var font = cursor.getInt(cursor.getColumnIndex("font"))

                        if (!model.isInit) {
                            model.isInit = true
                            model.setBackState(MemoState(title, memo, hash, font))
                        }
                        polarID = cursor.getString(cursor.getColumnIndex("_id"))
                        val editblob = cursor.getString(cursor.getColumnIndex("image"))
                        val editimage = MediaStore.Images.Media.getBitmap(
                            requireActivity().getContentResolver(),
                            Uri.parse("file://" + editblob)
                        )
                        polaroidEditView.setImage(editimage)
                        break
                    }
                }

            }
        }

        //TODO 스티커복구 해결 시 다시 확인
//        if (!isEmpty(cursor.getString(cursor.getColumnIndex("stickerKind")))) { //스티커설정함수. 설정자체는 구현안되어있음
//            var stickerKindTemp =
//                convertStringToArray(cursor.getString(cursor.getColumnIndex("stickerKind")))
//            var stickerFlipTemp =
//                convertStringToArray(cursor.getString(cursor.getColumnIndex("stickerFlip")))
//
//            var i = 0
//            while (i < stickerKindTemp.size) {
//                val encodeByte: ByteArray =
//                    Base64.decode(stickerKindTemp.get(i), Base64.DEFAULT)
//                stickerKind.add(BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size))
//                stickerFlip.add(stickerFlipTemp.get(i).toBoolean())
//                i++
//            }
//
//            //스티커의 종류 - Bitmap 배열로 들어가있음. 확인필요
//
//            var stickerGridTemp =
//                convertStringToArray(cursor.getString(cursor.getColumnIndex("stickerGrid")))
//
//            i = 0
//            var size = stickerGridTemp.size
//            while (i < size) {
//                var gridSet = arrayOf<String>(
//                    stickerGridTemp.get(i),
//                    stickerGridTemp.get(i + 1),
//                    stickerGridTemp.get(i + 2),
//                    stickerGridTemp.get(i + 3),
//                    stickerGridTemp.get(i + 4),
//                    stickerGridTemp.get(i + 5),
//                    stickerGridTemp.get(i + 6),
//                    stickerGridTemp.get(i + 7),
//                    stickerGridTemp.get(i + 8)
//                )
//                //코드가 너무 드럽다!!!!!!!!!!!!나중에 시간되면수정
//                stickerGrid.add(gridSet)
//                i += 9
//            }
//
//            //db에서 뽑아오기 끝
//            i = 0
//            while (i < stickerKindTemp.size) {
//                stickerData.add(
//                    StickerData(
//                        stickerKind.get(i),
//                        stickerGrid.get(i),
//                        stickerFlip.get(i)
//                    )
//                )
//                i++
//            }
//            //가져온 데이터로 스티커복구
//            val stickerList = mutableListOf<Sticker>()
//            val trans = StickerFormTrans(requireContext())
//            stickerData.forEach {
//                stickerList.add(trans.dataToSticker(it))
//            }
//            if (stickerList.size != 0) {
//                Log.d("stickerList", stickerList.toString())
//                stickerView.restoreStickers(stickerList)
//            }
//
//        }


        Log.d("create", "FragmentEdit view생성됨 : onViewCreated")

        //스티커뷰

//        val sampleIcon= BitmapStickerIcon(ContextCompat.getDrawable(this.requireContext(),
//            R.drawable.sticker_sample_1),BitmapStickerIcon.LEFT_BOTTOM)
        val deleteIcon = BitmapStickerIcon(
            ContextCompat.getDrawable(
                this.requireContext(),
                R.drawable.sticker_ic_close_white_18dp
            ), BitmapStickerIcon.LEFT_TOP
        )
        val zoomIcon = BitmapStickerIcon(
            ContextCompat.getDrawable(
                this.requireContext(),
                R.drawable.sticker_ic_scale_white_18dp
            ), BitmapStickerIcon.RIGHT_BOTTOM
        )
        val flipIcon = BitmapStickerIcon(
            ContextCompat.getDrawable(
                this.requireContext(),
                R.drawable.sticker_ic_flip_white_18dp
            ), BitmapStickerIcon.RIGHT_TOP
        )

        deleteIcon.setIconEvent(DeleteIconEvent())
        zoomIcon.setIconEvent(ZoomIconEvent())
        flipIcon.setIconEvent(FlipHorizontallyEvent())
        //TODO("맨앞으로 가져오기 기능 구현필요할듯")

        stickerView.setIcons(listOf(deleteIcon, zoomIcon, flipIcon))
        stickerView.setLocked(false)
        stickerView.setConstrained(true)

        var guideVisible = false
        stickerView.setOnStickerOperationListener(object : StickerView.OnStickerOperationListener {

            override fun onStickerAdded(sticker: Sticker) {
                Log.d("stickerView", "add")
            }

            override fun onStickerClicked(sticker: Sticker) {
                Log.d("stickerView", "click")

            }

            override fun onStickerDeleted(sticker: Sticker) {
                Log.d("stickerView", "deleted")
            }

            override fun onStickerDragFinished(sticker: Sticker) {
                Log.d("stickerView", "drag finish")
            }

            override fun onStickerTouchedDown(sticker: Sticker) {
                Log.d("stickerView", "touch down")
                guideVisible = true
                stickerView.setEditGuide(true)

            }

            override fun onStickerZoomFinished(sticker: Sticker) {
                Log.d("stickerView", "zoomfinished")

            }

            override fun onStickerFlipped(sticker: Sticker) {
                Log.d("stickerView", "fliped")
            }

            override fun onStickerDoubleTapped(sticker: Sticker) {
                Log.d("stickerView", "doubletap")
                

            }


        })


        //가이드라인 없애기
        view.setOnClickListener {
            //내부 소스 바꾸기 까다로워서 생성->삭제를 통해 가이드 라인없애기
            //TODO("삭제할 스티커변경하거나 스티커목록나오고 수정필요")
            if (guideVisible) {
                guideVisible = false
                /** drawable/sticker_sample_2 삭제하면 안됌!! **/
                val drawable =
                    ContextCompat.getDrawable(this.requireContext(), R.drawable.sticker_sample_2)
                loadSticker(drawable!!)
                stickerView.removeCurrentSticker()
            }
        }

        //기타 터치 이벤트
        val stickerList = binding.stickerListView
        val cover = binding.nonStickerCover
        val btnFlip = binding.btnFlip
        val btnEditSave = binding.btnEditSave
        val btnShowStickers = binding.btnShowStickers
        val btnPhoto=binding.btnChangePhoto

        polaroidImage = binding.editPolaroidView.findViewById(R.id.polaroidImage)


        //뒤집기 후 돌아왔을때
        if (tempStickerState != null) {
            if (tempStickerState?.photo != null) {
                polaroidImage.setImageDrawable(tempStickerState!!.photo)
            }
            if (tempStickerState?.stickerList != null) {
                val stickers = tempStickerState!!.stickerList
                stickerView.restoreStickers(stickers)
            }
        }



        //뒤집기버튼
        btnFlip.setOnClickListener {

            tempStickerState = TempEdit(polaroidImage.drawable, stickerView.getStickers())
            val action = FragmentEditDirections.actionFragmentEditToFragmentEditMemo2()
            view.findNavController().navigate(action)

        }


        //스티커창 보여주는 버튼
        btnShowStickers.setOnClickListener {
            cover.setVisibility(View.VISIBLE)
        }


        //스티커 나와있을때 스티커제외한 다른부분누르면 스티커 닫기
        cover.setOnClickListener {
            if (cover.visibility == View.VISIBLE) {
                cover.setVisibility(View.GONE)
            }
        }

        //저장버튼
        btnEditSave.setOnClickListener {
            tempStickerState = TempEdit(polaroidImage.drawable, stickerView.getStickers())
            model.setFrontState(tempStickerState)
            model.setPolaroidBitmap(stickerView.createBitmap())
            val action = FragmentEditDirections.actionGlobalFragmentDetailDeleteBackStack()
            if(isExist) {
                insertDB(arrayOf(cursor.getString(cursor.getColumnIndex("_id"))), cursor.getString(cursor.getColumnIndex("_id")))
                cursor.close()
            }else{
                databaseP.insert("polaroid", null, polaroidDB)
                var cursor2 = databaseP.rawQuery("SELECT * FROM polaroid", null)
                cursor2.moveToLast()
                insertDB(arrayOf(cursor2.getString(cursor2.getColumnIndex("_id"))),cursor2.getString(cursor2.getColumnIndex("_id")))
            }
            view.findNavController().navigate(action)
        }

        btnPhoto.setOnClickListener{
            if (checkPermission()){
                openGallery()
            }
        }


        //콜백이용해서 누른 스티커 정보 받아 스티커추가 (리사이클러뷰>뷰페이저>커스텀뷰>FragmentEdit)->나중에 수정가능하면 수정
        stickerList.setCallbackListener(object : StickerListView.CallbackListener {
            override fun callBack(sticker: Drawable) {
                loadSticker(sticker)
            }
        })


    }


    //갤러리에서 사진받아서 이미지를 폴라로이드에 넣기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                //라이브러리사용 -> 크롭해서 넣기
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    resultUri = result.uri
                    polaroidImage.setImageURI(resultUri)
                }
            }
        }
    }


    //backpress구현
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.nonStickerCover.visibility == View.VISIBLE) {
                    binding.nonStickerCover.visibility = View.GONE
                } else {

                    var builder = AlertDialog.Builder(context)
                    builder.setMessage("작성을 취소하시겠습니까?")

                    var listener = object :
                        DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            model.reset(requireContext())
                            findNavController().popBackStack()
                        }
                    }
                    builder.setPositiveButton("네", listener)
                    builder.setNegativeButton("아니오", null)
                    builder.show()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onDestroy() {
        //작성 초기화 로직 필요
        if (isExist) {
            model.reset(requireContext())
        } else {

        }
        super.onDestroy()
    }

    override fun onPause() {
        //작성 초기화 로직 필요
        super.onPause()
    }


    //스티커 로드 함수
    private fun loadSticker(sticker: Drawable) {

        stickerView.addSticker(
            DrawableSticker(sticker),
            Sticker.Position.CENTER
        )
        Log.d("sticker", "${stickerView.stickerCount}")

    }

    fun openGallery() {
        CropImage.activity().setFixAspectRatio(true).start(requireContext(), this)
    }

    //리스트를 DB에 넣기 위한 전환작업함수

    var strSeparator = "__,__"
    fun convertArrayToString(array: MutableList<String>): String? {
        var str = ""
        for (i in array.indices) {
            str = str + array[i]
            // Do not append comma at the end of last element
            if (i < array.size - 1) {
                str = str + strSeparator
            }
        }
        return str
    }

    fun convertStringToArray(str: String): MutableList<String> {
        return str.split(strSeparator.toRegex()).toMutableList()
    }

    fun printStickerInf(sticker: Sticker) {
        Log.d("stickerInf", sticker.getMatrix().toString())
        Log.d("stickerInf", " ${sticker.matrixValues()}")
//            Log.d("stickerInf",sticker.mappedBoundPoints.toString())
//            Log.d("stickerInf",sticker.toString())
//            Log.d("stickerInf",sticker.getMatrix().toString())
//            Log.d("stickerInf",sticker.getMatrix().toString())

    }

    fun insertDB(arr: Array<String>, id: String) {

        var polaroidDB = ContentValues()
        val memostate = model.tempBackState.value
        var polarstate = model.tempFrontState.value
        var polarimage = model.polaroidBitmap.value

        /**데이터 가져가는법
         * model.라이브데이터변수명.value
         * ex) model.tempBackState.value
         *
         * [뷰모델 데이터]
         * 앞면정보 TempEdit(val photo: Drawable, val stickerList:List<Sticker>)
         * 뒷면정보 MemoState(val title:String="",val memo:String="",val tagList:MutableList<String>?=null)
         * 완성비트맵 polaroidBitmap:Bitmap
         *
         * **/

        val memo = mutableListOf<String>()

        if (memostate?.title != null) {
            memo.add(memostate.title)
        }
        if (memostate?.memo != null) {
            memo.add(memostate.memo)
        }
        if (memostate?.title != null || memostate?.memo != null) {
            polaroidDB.put("memo", convertArrayToString(memo))
        }
        if (memostate?.tagList !== null) {
            val hash = memostate.tagList
            polaroidDB.put("tag", hash?.let { it1 -> convertArrayToString(it1) })
        } else {
            polaroidDB.put("tag", "")
        }
        if (polarimage != null) {
            var bitmap: Bitmap = Bitmap.createBitmap(polarimage)
            val strFolderPath: String? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
            val folder = File(strFolderPath)
            if (!folder.exists()) {
                folder.mkdirs()
            }

            var out: OutputStream? = null
            val strFilePath = strFolderPath + id + "_completeImage"
            val fileCacheItem = File(strFilePath)
            fileCacheItem.createNewFile()
            out = FileOutputStream(fileCacheItem)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()

            polaroidDB.put("completeImage", strFilePath)
        }
        if (polarstate?.photo != null) {
            var bitmap: Bitmap = polarstate.photo.toBitmap()
            if(resultUri != null){
                bitmap = MediaStore.Images.Media.getBitmap(
                    requireContext().getContentResolver(),
                    resultUri
                )
            }
            val strFolderPath: String? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
            val folder = File(strFolderPath)
            if (!folder.exists()) {
                folder.mkdirs()
            }

            var out: OutputStream? = null
            val strFilePath = strFolderPath + id + "_image"
            val fileCacheItem = File(strFilePath)
            fileCacheItem.createNewFile()
            out = FileOutputStream(fileCacheItem)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()

            polaroidDB.put("image", strFilePath)
        }
        if (polarstate?.stickerList != null) {
            val stickerFun = StickerFormTrans(requireContext())
            var stickerKind = mutableListOf<String>()
            var stickerGrid = mutableListOf<String>()
            var stickerFlip = mutableListOf<String>()
            var i = 0
            while (i < polarstate.stickerList.size) {
                var sticker = stickerFun.stickerTodata(polarstate.stickerList.get(i))
                var bitmap: Bitmap? = sticker.photo?.let { it1 -> Bitmap.createBitmap(it1) }
                val stream = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.PNG, 40, stream)
                val byteArray = stream.toByteArray()
                stickerKind.add(Base64.encodeToString(byteArray, Base64.DEFAULT))
                convertArrayToString(sticker.matrixValues.toMutableList())?.let { it1 ->
                    stickerGrid.add(
                        it1
                    )
                }
                stickerFlip.add(sticker.isFlipHorizontally.toString())


                i++
            }
            polaroidDB.put("stickerKind", convertArrayToString(stickerKind))
            polaroidDB.put("stickerGrid", convertArrayToString(stickerGrid))
            polaroidDB.put("stickerFlip", convertArrayToString(stickerFlip))
        }
        polaroidDB.put("font", memostate!!.fontNum)
        databaseP.update("polaroid", polaroidDB, "_id=?", arr)
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 999) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(
                    requireContext(),
                    "설정 > 애플리케이션 > POLDA에서 권한설정을 해주세요!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    fun checkPermission(): Boolean {
        val wPermissionCheck = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val rPermissionCheck = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (wPermissionCheck != PackageManager.PERMISSION_GRANTED || rPermissionCheck != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(
                    requireContext(),
                    "설정 > 애플리케이션 > POLDA에서 권한설정을 해주세요!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //Toast.makeText(requireContext(),"권한설정이 필요해요",Toast.LENGTH_SHORT).show()
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 999
            )
            return false

        } else return true
    }

}

data class TempEdit(val photo: Drawable, val stickerList:List<Sticker>)