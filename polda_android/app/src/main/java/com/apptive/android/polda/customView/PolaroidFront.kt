package com.apptive.android.polda.customView

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.apptive.android.polda.R

class PolaroidFront @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var polaroidImg: ImageView


    init {
        LayoutInflater.from(context).inflate(R.layout.item_polaloid_front, this, true)
        polaroidImg=findViewById(R.id.polaroidImage)

    }
    fun setImage(image: Bitmap){
            polaroidImg=findViewById(R.id.polaroidImage)
            polaroidImg.setImageBitmap(image)
        }
}