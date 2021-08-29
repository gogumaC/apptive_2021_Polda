package com.apptive_saenggamja.android.polda

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class CheckPermissions() {

    private val REQUESTCODE=99
    private lateinit var context:Context
    private lateinit var fragment: Fragment
    private val savePermission=if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_MEDIA_LOCATION)
    }else {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    private val permissionList=mutableListOf<String>()

    fun permissionSupport(fragment:Fragment,context:Context){
        this.fragment=fragment
        this.context=context
    }

    fun checkPermission(context: Context):Boolean{

        savePermission.forEach{
            val storagePermissions= ContextCompat.checkSelfPermission(context,it)
            if(storagePermissions!=PackageManager.PERMISSION_GRANTED){
                permissionList.add(it)
            }
        }
        if(!permissionList.isEmpty()){
            return false
        }
        return true


    }


    fun requestPermission(){
//        ActivityCompat.requestPermissions(this,
//            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),REQUESTCODE)

        fragment.requestPermissions(
            permissionList.toTypedArray(),REQUESTCODE)
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ):Boolean {
        when(requestCode){
            REQUESTCODE->{
                for(i in permissionList.indices){
                    if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                        return false
                    }
                }

            }
        }
        return true
    }
}