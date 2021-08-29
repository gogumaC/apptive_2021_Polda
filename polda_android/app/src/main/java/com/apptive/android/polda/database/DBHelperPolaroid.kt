package com.apptive.android.polda

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.sql.Blob
import java.util.function.BinaryOperator

//database에 넣어야하는 항목들
//연결되어있는 다이어리(Text) string :: diaryName
//인덱스(integer) int :: polaroidIndex (위치이동 기능을 위한 것)
//이미지(BLOB) bmp :: image
//메모리스트 {제목, 내용} list -> string:: memo (JSONObject를 이용하여 문자열로 저장)
//해시태그리스트 {} :: tag
//스티커 좌표값 {} :: stickerGrid
//스티커 자료값 {} :: stickerKind
//스티커 편집(회전 뒤집기? 등) {} :: stickerFlip
//완성된 이미지(BLOB) bmp :: completeImage
//폰트상태 Integer :: font


@Suppress("DEPRECATION")
class DBHelperPolaroid(context: Context?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, "POLDA_POLAROID", factory, version) {



    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS polaroid(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "diaryName TEXT, polaroidIndex INTEGER, image TEXT, memo TEXT, tag TEXT, completeImage TEXT, font INTEGER, stickerGrid TEXT, stickerKind TEXT, stickerFlip TEXT);"
            )
            //stickerGrid TEXT, stickerKind TEXT, stickerFlip TEXT

        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(newVersion == 1) {
            db?.execSQL("DROP TABLE IF EXISTS polaroid");
            onCreate(db);
        }
    }




}