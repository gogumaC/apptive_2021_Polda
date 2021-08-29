package com.apptive.android.polda

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.sql.Blob
import java.util.function.BinaryOperator

//database에 넣어야하는 항목들
//date (생성날짜. 만약 같은 날 여러개 받았다면 날짜, 날짜_1, 날짜_2 형식으로 담는다.)
//title (제목. 중복 상관없으며 생성 시 먼저 설정하도록 한다.)
//image (설정한 이미지)

@Suppress("DEPRECATION")
class DBHelperDiary(context: Context?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, "POLDA_DIARY", factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS diary(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "date TEXT, title TEXT, image TEXT);"
            )

        }

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(newVersion == 1) {
            db?.execSQL("DROP TABLE IF EXISTS diary");
            onCreate(db);
        }
    }



}