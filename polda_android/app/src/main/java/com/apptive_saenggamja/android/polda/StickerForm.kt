package com.apptive_saenggamja.android.polda
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.apptive_saenggamja.android.polda.sticker.DrawableSticker
import com.apptive_saenggamja.android.polda.sticker.Sticker

class StickerFormTrans(val context: Context){

    fun stickerTodata(sticker:Sticker):StickerData{
        var stickerData=StickerData(matrixValues = arrayOf())
        val photo=sticker.drawable as BitmapDrawable
        stickerData.photo=photo.bitmap
        stickerData.matrixValues= floatArrayToStringArray(sticker.matrixValues())
//        stickerData.unrotatedWrapperCorner= floatArrayToStringArray(sticker.unrotatedWrapperCorner())
//        stickerData.unrotatedPoint=floatArrayToStringArray(sticker.unrotatedPoint())
//        stickerData.boundPoints= floatArrayToStringArray(sticker.boundPoints)
        stickerData.isFlipHorizontally= sticker.isFlippedHorizontally

        return stickerData
    }


//
//    Sticker sticker=stickerList.get(i);
//    sticker.setMatrix(stickerList.get(i).getMatrix());
//    sticker.setFlippedVertically(sticker.isFlippedVertically());
//    sticker.setFlippedHorizontally(sticker.isFlippedHorizontally());

    fun dataToSticker(stickerData:StickerData?):Sticker{

        val matrix=Matrix()
        if(stickerData!=null){
            val drawable= BitmapDrawable(context.resources,stickerData.photo )
            matrix.setValues(stringArrayToFloatArray(stickerData.matrixValues))
            val sticker = DrawableSticker(drawable)
            sticker.setMatrix(matrix)
            sticker.setFlippedHorizontally(stickerData.isFlipHorizontally)
            return sticker
        }
        else{
            val drawable= ContextCompat.getDrawable(context,R.drawable.blue)
            matrix.setValues( floatArrayOf(0.63687557f,0.5870497f, -326.0395f,-0.5870497f, 0.63687557f, 980.38245f,0.0f, 0.0f, 1.0f))
            val sticker=DrawableSticker(drawable)
            sticker.setMatrix(matrix)
            sticker.setFlippedHorizontally(true)
            return sticker
        }


    }



    fun floatArrayToStringArray(floatArr: FloatArray):Array<String>{
        val stringArr=Array<String>(floatArr.size,{""})
        floatArr.forEachIndexed { index, fl ->
            stringArr[index]=fl.toString()
        }
        return stringArr
    }

    fun stringArrayToFloatArray(strArr:Array<String>):FloatArray{
        val floatArr=FloatArray(strArr.size,{0f})
        strArr.forEachIndexed { index, s ->
            floatArr[index]=s.toFloat()
        }
        return floatArr
    }





}


data class StickerData(var photo: Bitmap?=null, var matrixValues:Array<String>,var isFlipHorizontally:Boolean=false)