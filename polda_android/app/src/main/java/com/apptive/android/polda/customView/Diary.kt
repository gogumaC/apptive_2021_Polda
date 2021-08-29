package com.apptive.android.polda.customView

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext

data class Diary(var id: String, var date: String, var title: String, var image: Bitmap){
}