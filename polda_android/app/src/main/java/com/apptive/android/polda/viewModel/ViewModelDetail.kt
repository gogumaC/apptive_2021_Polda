package com.apptive.android.polda.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.apptive.android.polda.SingleLiveEvent

class ViewModelDetail: ViewModel() {
    private val _detailPage= SingleLiveEvent<Int>()
    private val _detailSeekbarProgress= SingleLiveEvent<Int>()

    val detailPage: LiveData<Int> get()=_detailPage
    val detailSeekbarProgress:LiveData<Int> get()=_detailSeekbarProgress


    fun setPageSeekBar(progress:Int,totalPage:Int){
        _detailPage.value=pageLocation(progress,totalPage)
    }

    fun setPageSwipe(position:Int,pageNum:Int){
        _detailSeekbarProgress.value=when(position){
                    0->0
                    pageNum-1->100
                    else->100/(pageNum-1)*(position)
                }
    }

    fun pageLocation(progress:Int,pageNum:Int):Int{
        val page=progress.toDouble()*(pageNum.toDouble()/100)
        return if(page.toInt()!=pageNum) page.toInt()+1
        else page.toInt()
    }
}