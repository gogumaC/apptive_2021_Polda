package com.apptive.android.polda.fragment

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.apptive.android.polda.CheckPermissions
import com.apptive.android.polda.DBHelperPolaroid
import com.apptive.android.polda.R
import com.apptive.android.polda.StickerData
import com.apptive.android.polda.adapter.AdapterShowViewPager
import com.apptive.android.polda.adapter.ShowPolaroidData

import com.apptive.android.polda.customView.PolaroidFront
import com.wajahatkarim3.easyflipviewpager.CardFlipPageTransformer2
import java.net.URLEncoder
import com.apptive.android.polda.fragment.FragmentDetail.polaroidEditINFO.polarID
import com.apptive.android.polda.fragment.FragmentDetail.polaroidINFO.isExist
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.security.Permission
import java.text.SimpleDateFormat
import java.util.*


class FragmentShowPolaloid : Fragment() {

    lateinit var dbHelperP : DBHelperPolaroid
    lateinit var databaseP : SQLiteDatabase
    var shareMode = false
    private val permission= CheckPermissions()
    private val args:FragmentShowPolaloidArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_polaloid, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnEdit=view.findViewById<ImageButton>(R.id.btnEdit)
        val btnShare=view.findViewById<ImageButton>(R.id.btnShare)
        val btnSave=view.findViewById<ImageButton>(R.id.btnSave)
        val viewPager=view.findViewById<ViewPager2>(R.id.polaroidShowViewPager).apply{
            registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback(){})
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
        val adapter= AdapterShowViewPager(requireContext())
        lateinit var image: Bitmap
        val polaroidInfo = args.finishedPolaroidInfo
        lateinit var title: String
        lateinit var memo: String
        var font: Int = 0
        lateinit var hash: MutableList<String>

        //db오픈
        dbHelperP = DBHelperPolaroid(context, null, 1)
        databaseP = dbHelperP.readableDatabase

        var cursor : Cursor = databaseP.rawQuery("SELECT * FROM polaroid", null)

        while(cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndex("_id")).equals(FragmentDetail.polaroidEditINFO.polarID)){
                var b = cursor.getString(cursor.getColumnIndex("completeImage"))
                image = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), Uri.parse("file://" + b))
                break
            }
        }


        //메모상태 저장하는 함수
        cursor = databaseP.rawQuery("SELECT * FROM polaroid", null)
        while(cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndex("_id")).equals(FragmentDetail.polaroidEditINFO.polarID)){
                if(cursor.getString(cursor.getColumnIndex("memo")) != null){
                    var memoDB = cursor.getString(cursor.getColumnIndex("memo"))
                    var memoArr = convertStringToArray(memoDB)
                    title = memoArr?.get(0).toString()
                    memo = memoArr?.get(1).toString()
                    font = cursor.getString(cursor.getColumnIndex("font")).toInt()
                    hash = convertStringToArray(cursor.getString(cursor.getColumnIndex("tag")))
                    // polaroidBack.setMemo(title, memo)
                }

                break
            }
        }


        var datalist : ShowPolaroidData = ShowPolaroidData(image, title, memo, font, hash)
        adapter.polaroidData= datalist
        viewPager.adapter=adapter
        val cardFlipPageTransformer= CardFlipPageTransformer2()
        cardFlipPageTransformer.setScalable(false)
        viewPager.setPageTransformer(cardFlipPageTransformer)


        //TODO("폴라로이드 정보로 뷰 갱신하는 코드 추가")

        btnEdit.setOnClickListener {
            FragmentDetail.polaroidINFO.polarDiaryN = cursor.getString(cursor.getColumnIndex("diaryName"))
            FragmentDetail.polaroidINFO.polarIndex = cursor.getInt(cursor.getColumnIndex("polaroidIndex"))
            isExist = true
            val action = FragmentShowPolaloidDirections.actionGlobalEdit()
            view.findNavController().navigate(action)
        }


        //트위터공유
        var strLink = ""

        btnShare.setOnClickListener {
            if (checkPermission()){
                shareMode = true
                var Sharing_intent: Intent = Intent(Intent.ACTION_SEND);
                Sharing_intent.setType("image/*");
                var Test_Message = "#POLDA";
                var uri: Uri? = saveImageFile(requireContext(), datalist.image, getTimeStamp())
                Sharing_intent.putExtra(Intent.EXTRA_STREAM, uri)
                Sharing_intent.putExtra(Intent.EXTRA_TEXT, Test_Message);
                var Sharing: Intent = Intent.createChooser(Sharing_intent, "공유하기");
                startActivity(Sharing);
                shareMode = false
            }
        }


        btnSave.setOnClickListener {
            if(checkPermission()){
                saveImageFile(requireContext(), datalist.image, getTimeStamp())
            }


        }
    }
    /**여기부터!!**/


    fun getTimeStamp():String{
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        return imageFileName
    }
    private fun saveImageFile(context: Context,bitmap: Bitmap,title:String):Uri?{
        val contentResolver=context.getContentResolver()
        val values= ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME,title)
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/*")
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.Q){
            values.put(MediaStore.Images.Media.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+"/POLDA")
            values.put(MediaStore.Images.Media.IS_PENDING,1)
        }
        val uri=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)


        if(uri!=null){
            val descriptor=contentResolver.openFileDescriptor(uri,"w",null)
            if(descriptor!=null){
                val fos= FileOutputStream(descriptor.fileDescriptor)
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos)
                fos.close()
            }
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                values.put(MediaStore.Images.Media.IS_PENDING,0)
                contentResolver.update(uri,values,null,null)
            }


            Log.d("FragmentEdit","저장완료")
            if(!shareMode) {
                Toast.makeText(context, "갤러리에 이미지가 저장됐어요(ง ᵕᴗᵕ)ว", Toast.LENGTH_SHORT).show()
            }
        }
        else{Log.d("FragmentEdit","저장실패")}

        return uri
    }



    /**여기까지지**/

    fun permissionCheck(){
        if(Build.VERSION.SDK_INT>=23){
            permission.permissionSupport(this,this.requireContext())
            if(!permission.checkPermission(requireContext())){
                permission.requestPermission()
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==999){
            if(grantResults.size>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){

            }else{
                Toast.makeText(requireContext(), "설정 > 애플리케이션 > POLDA에서 권한설정을 해주세요!", Toast.LENGTH_SHORT).show()
            }
        }

    }




    var strSeparator = "__,__"
    fun convertStringToArray(str: String): MutableList<String> {
        return str.split(strSeparator.toRegex()).toMutableList()
    }

    fun checkPermission():Boolean {
        val wPermissionCheck = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val rPermissionCheck= ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE)
        if (wPermissionCheck != PackageManager.PERMISSION_GRANTED||rPermissionCheck!=PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(requireContext(), "설정 > 애플리케이션 > POLDA에서 권한설정을 해주세요!", Toast.LENGTH_SHORT).show()
            }
            //Toast.makeText(requireContext(),"권한설정이 필요해요",Toast.LENGTH_SHORT).show()
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),999)
            return false

        }else return true
    }






}