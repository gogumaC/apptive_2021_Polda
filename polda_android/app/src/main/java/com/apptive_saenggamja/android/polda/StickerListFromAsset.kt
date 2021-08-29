package com.apptive_saenggamja.android.polda

import android.content.Context
import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import java.io.InputStream

class StickerListFromAsset(val context: Context) {
    /** 주의 : null반환되면 아마 오류;;ㅜ -> 스티커 불러오기 관련문제는 웬만하면 이 클래스안에서 해결바람**/
    // /assets/stickers/groupN/스티커이름
    val assetManager: AssetManager=context.getResources().getAssets()
    val stickerGroup=assetManager.list("stickers")


    fun getGroupStickerList(groupName:String):List<Drawable>?{

        val stickerNameList=assetManager.list("stickers/${groupName}")?.toList()
        val stickerList=mutableListOf<Drawable>()
        if(stickerNameList==null){return null}
        else {
            for (name in stickerNameList) {
                val inputStream: InputStream = assetManager.open("stickers/${groupName}/${name}")
                val sticker = Drawable.createFromStream(inputStream, null)
                stickerList.add(sticker)
                inputStream.close()
            }

            return stickerList
        }


    }


}