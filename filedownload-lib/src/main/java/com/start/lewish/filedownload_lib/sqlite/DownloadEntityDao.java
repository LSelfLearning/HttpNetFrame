package com.start.lewish.filedownload_lib.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * author: sundong
 * created at 2017/1/11 14:44
 */
public class DownloadEntityDao {
    private static final String TAG = "DownloadEntityDao";
    private DownloadDBHelper mDownloadDBHelper;
    private DownloadEntityDao() {
    }
    public static DownloadEntityDao getInstance(){
        return Holder.instance;
    }
    public static class Holder {
        private static final DownloadEntityDao instance = new DownloadEntityDao();
    }

    public void init(Context context) {
        mDownloadDBHelper = new DownloadDBHelper(context, 1);
    }

    public List<DownloadEntity> getDownloadEntityByUrl(String downUrl) {
        SQLiteDatabase database = mDownloadDBHelper.getReadableDatabase();
        List<DownloadEntity> list = new ArrayList<DownloadEntity>();
        Cursor cursor = database.query(DownloadDBHelper.TABLE_NAME, null, "downloadUrl=?", new String[]{downUrl}, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            int startPos = cursor.getInt(cursor.getColumnIndex("startPos"));
            int endPos = cursor.getInt(cursor.getColumnIndex("endPos"));
            int progressPos = cursor.getInt(cursor.getColumnIndex("progressPos"));
            list.add(new DownloadEntity(id, startPos, endPos, progressPos, downUrl));
        }
        cursor.close();
        database.close();
        return list;
    }

    public void insertOrReplace(DownloadEntity downloadEntity) {
        SQLiteDatabase database = mDownloadDBHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("startPos", downloadEntity.getStartPos());
        values.put("endPos", downloadEntity.getEndPos());
        values.put("progressPos", downloadEntity.getProgressPos());
        values.put("downloadUrl", downloadEntity.getDownloadUrl());
        if (downloadEntity.getId() == -1) {//插入
            long rowID = database.insert(DownloadDBHelper.TABLE_NAME, null, values);
        } else {//更新
            int updateNum = database.update(DownloadDBHelper.TABLE_NAME, values, "_id = ?", new String[]{downloadEntity.getId() + ""});
            Log.e(TAG, "修改了" + updateNum + "条记录");
        }
        database.close();
    }

    public void deleteDataByUrl(String downLoadUrl) {
        SQLiteDatabase database = mDownloadDBHelper.getReadableDatabase();
        int delete = database.delete(DownloadDBHelper.TABLE_NAME, "downloadUrl=?", new String[]{downLoadUrl});
        Log.e(TAG, "共删除" + delete + "条记录");
        database.close();
    }
}
