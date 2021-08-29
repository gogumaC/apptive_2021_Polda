package com.apptive.android.polda

import android.util.Log
import androidx.recyclerview.widget.DiffUtil

class PolaroidDiffUtil (private val oldList: List<PolaroidData>, private val currentList: List<PolaroidData>):
    DiffUtil.Callback(){
    override fun getOldListSize(): Int =oldList.size

    override fun getNewListSize(): Int =currentList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        Log.d("detail",(oldList[oldItemPosition].date==currentList[newItemPosition].date).toString())
        return oldList[oldItemPosition].date==currentList[newItemPosition].date
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]==currentList[newItemPosition]
    }

}