package com.apptive_saenggamja.android.polda

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class Loading : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)
        startLoading()
    }

    private fun startLoading(){
        val handler = Handler()
        handler.postDelayed({ finish() }, 2000)
    }
}

