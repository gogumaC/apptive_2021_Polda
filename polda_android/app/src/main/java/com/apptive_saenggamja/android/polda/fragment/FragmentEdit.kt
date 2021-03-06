package com.apptive_saenggamja.android.polda.fragment

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
import com.apptive_saenggamja.android.polda.DBHelperPolaroid
import com.apptive_saenggamja.android.polda.R
import com.apptive_saenggamja.android.polda.StickerFormTrans
import com.apptive_saenggamja.android.polda.customView.MemoState
import com.apptive_saenggamja.android.polda.customView.StickerListView
import com.apptive_saenggamja.android.polda.databinding.FragmentEditBinding
import com.apptive_saenggamja.android.polda.fragment.FragmentDetail.polaroidEditINFO.polarID
import com.apptive_saenggamja.android.polda.fragment.FragmentDetail.polaroidINFO.isExist
import com.apptive_saenggamja.android.polda.fragment.FragmentDetail.polaroidINFO.polaroidDB
import com.apptive_saenggamja.android.polda.sticker.*
import com.apptive_saenggamja.android.polda.viewModel.ViewModelEditTempState
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
        /**????????? ?????????**/
        model.reset(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit, container, false)
        Log.d("create", "FragmentEdit ?????????????????? ; onCreateView")
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val polaroidEditView = binding.editPolaroidView
        stickerView = binding.StickerView

        //DB ??????
        dbHelperP = DBHelperPolaroid(context, null, 1)
        databaseP = dbHelperP.readableDatabase
        dbHelperP.onCreate(databaseP)

        cursor = databaseP.rawQuery("SELECT * FROM polaroid", null)

        if(isExist) {
            while (cursor.moveToNext()) { //?????? ????????? ?????????????????? ????????? ??????
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

        //TODO ??????????????? ?????? ??? ?????? ??????
//        if (!isEmpty(cursor.getString(cursor.getColumnIndex("stickerKind")))) { //?????????????????????. ??????????????? ?????????????????????
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
//            //???????????? ?????? - Bitmap ????????? ???????????????. ????????????
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
//                //????????? ?????? ?????????!!!!!!!!!!!!????????? ??????????????????
//                stickerGrid.add(gridSet)
//                i += 9
//            }
//
//            //db?????? ???????????? ???
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
//            //????????? ???????????? ???????????????
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


        Log.d("create", "FragmentEdit view????????? : onViewCreated")

        //????????????

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
        //TODO("???????????? ???????????? ?????? ??????????????????")

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


        //??????????????? ?????????
        view.setOnClickListener {
            //?????? ?????? ????????? ??????????????? ??????->????????? ?????? ????????? ???????????????
            //TODO("????????? ???????????????????????? ???????????????????????? ????????????")
            if (guideVisible) {
                guideVisible = false
                /** drawable/sticker_sample_2 ???????????? ??????!! **/
                val drawable =
                    ContextCompat.getDrawable(this.requireContext(), R.drawable.sticker_sample_2)
                loadSticker(drawable!!)
                stickerView.removeCurrentSticker()
            }
        }

        //?????? ?????? ?????????
        val stickerList = binding.stickerListView
        val cover = binding.nonStickerCover
        val btnFlip = binding.btnFlip
        val btnEditSave = binding.btnEditSave
        val btnShowStickers = binding.btnShowStickers
        val btnPhoto=binding.btnChangePhoto

        polaroidImage = binding.editPolaroidView.findViewById(R.id.polaroidImage)


        //????????? ??? ???????????????
        if (tempStickerState != null) {
            if (tempStickerState?.photo != null) {
                polaroidImage.setImageDrawable(tempStickerState!!.photo)
            }
            if (tempStickerState?.stickerList != null) {
                val stickers = tempStickerState!!.stickerList
                stickerView.restoreStickers(stickers)
            }
        }



        //???????????????
        btnFlip.setOnClickListener {

            tempStickerState = TempEdit(polaroidImage.drawable, stickerView.getStickers())
            val action = FragmentEditDirections.actionFragmentEditToFragmentEditMemo2()
            view.findNavController().navigate(action)

        }


        //???????????? ???????????? ??????
        btnShowStickers.setOnClickListener {
            cover.setVisibility(View.VISIBLE)
        }


        //????????? ??????????????? ?????????????????? ????????????????????? ????????? ??????
        cover.setOnClickListener {
            if (cover.visibility == View.VISIBLE) {
                cover.setVisibility(View.GONE)
            }
        }

        //????????????
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


        //?????????????????? ?????? ????????? ?????? ?????? ??????????????? (??????????????????>????????????>????????????>FragmentEdit)->????????? ?????????????????? ??????
        stickerList.setCallbackListener(object : StickerListView.CallbackListener {
            override fun callBack(sticker: Drawable) {
                loadSticker(sticker)
            }
        })


    }


    //??????????????? ??????????????? ???????????? ?????????????????? ??????
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                //????????????????????? -> ???????????? ??????
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    resultUri = result.uri
                    polaroidImage.setImageURI(resultUri)
                }
            }
        }
    }


    //backpress??????
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.nonStickerCover.visibility == View.VISIBLE) {
                    binding.nonStickerCover.visibility = View.GONE
                } else {

                    var builder = AlertDialog.Builder(context)
                    builder.setMessage("????????? ?????????????????????????")

                    var listener = object :
                        DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            model.reset(requireContext())
                            findNavController().popBackStack()
                        }
                    }
                    builder.setPositiveButton("???", listener)
                    builder.setNegativeButton("?????????", null)
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
        //?????? ????????? ?????? ??????
        if (isExist) {
            model.reset(requireContext())
        } else {

        }
        super.onDestroy()
    }

    override fun onPause() {
        //?????? ????????? ?????? ??????
        super.onPause()
    }


    //????????? ?????? ??????
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

    //???????????? DB??? ?????? ?????? ??????????????????

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

        /**????????? ???????????????
         * model.???????????????????????????.value
         * ex) model.tempBackState.value
         *
         * [????????? ?????????]
         * ???????????? TempEdit(val photo: Drawable, val stickerList:List<Sticker>)
         * ???????????? MemoState(val title:String="",val memo:String="",val tagList:MutableList<String>?=null)
         * ??????????????? polaroidBitmap:Bitmap
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
                    "?????? > ?????????????????? > POLDA?????? ??????????????? ????????????!",
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
                    "?????? > ?????????????????? > POLDA?????? ??????????????? ????????????!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //Toast.makeText(requireContext(),"??????????????? ????????????",Toast.LENGTH_SHORT).show()
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