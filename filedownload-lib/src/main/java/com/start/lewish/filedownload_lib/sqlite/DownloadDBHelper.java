package com.start.lewish.filedownload_lib.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * author: sundong
 * created at 2017/1/11 14:44
 */
public class DownloadDBHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "downloadRecord";
    public DownloadDBHelper(Context context, int version) {
        super(context, TABLE_NAME+".db", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table downloadRecord(_id integer primary key autoincrement,startPos integer,endPos integer,progressPos integer,downloadUrl varchar)");
//        db.execSQL("insert into downloadRecord(startPos,endPos,progressPos,downloadUrl)values(0,0,0,'hahah')");
//        db.execSQL("insert into downloadRecord(startPos,endPos,progressPos,downloadUrl)values(0,0,1,'hahah')");
//        db.execSQL("insert into downloadRecord(startPos,endPos,progressPos,downloadUrl)values(0,0,2,'hahah')");
//        db.execSQL("insert into downloadRecord(startPos,endPos,progressPos,downloadUrl)values(1,1,1,'heiheiheihei')");
        Log.e("TAG", "onCreate()");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
