package com.apptive.android.polda

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, Loading::class.java)
        startActivity(intent)
    }

}