package com.apptive_saenggamja.android.polda

import android.graphics.Bitmap

//image: 기본이미지
//imageC : 완성비트맵

data class PolaroidData(val image: Bitmap, val date: String, val imageC: Bitmap) {
}