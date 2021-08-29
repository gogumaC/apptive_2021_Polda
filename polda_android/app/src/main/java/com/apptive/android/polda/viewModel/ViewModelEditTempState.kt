package com.apptive.android.polda.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.apptive.android.polda.R
import com.apptive.android.polda.SingleLiveEvent
import com.apptive.android.polda.customView.MemoState
import com.apptive.android.polda.fragment.TempEdit

class ViewModelEditTempState(): ViewModel() {

//database에 넣어야하는 항목들
//연결되어있는 다이어리(Text) string :: diaryName
//인덱스(integer) int :: polaroidIndex (위치이동 기능을 위한 것)
//이미지(BLOB) bmp :: image
//메모리스트 {제목, 내용} list -> string:: memo (JSONObject를 이용하여 문자열로 저장)
//해시태그리스트 {} :: tag
//스티커 좌표값 {} :: stickerGrid
//스티커 자료값 {} :: stickerKind


    val polaroidName:String=""
    var isInit=false
    private val _tempFrontState= SingleLiveEvent<TempEdit?>()
    private val _tempBackState= SingleLiveEvent<MemoState?>()
    private val _polaroidBitmap=SingleLiveEvent<Bitmap?>()

    val tempFrontState: LiveData<TempEdit?> get()=_tempFrontState
    val tempBackState:LiveData<MemoState?> get()=_tempBackState
    val polaroidBitmap:LiveData<Bitmap?> get()=_polaroidBitmap



    fun setPolaroidBitmap(btm:Bitmap){
        _polaroidBitmap.value=btm
    }


    fun setBackState(state:MemoState?){
        _tempBackState.value=state
    }

    fun setFrontState(state:TempEdit?){
        _tempFrontState.value=state
    }



    fun refreshBackState(){
        val temp=tempBackState.value
        _tempBackState.value=temp
    }



    fun reset(context: Context){
        //초기화로직
        isInit=false
        //setInitState(null)
        val bitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_polaroid)
        val drawable=ContextCompat.getDrawable(context,R.drawable.polaroid_empty_front)
        setBackState(MemoState("","",mutableListOf(),0))
        if(drawable!=null) {
            setFrontState(TempEdit(drawable, listOf()))
        }else{
            setFrontState(null)
        }
        setPolaroidBitmap(bitmap)

    }


}