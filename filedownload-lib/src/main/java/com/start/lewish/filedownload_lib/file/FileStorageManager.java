package com.start.lewish.filedownload_lib.file;

import android.content.Context;
import android.os.Environment;

import com.start.lewish.filedownload_lib.utils.Md5Uills;

import java.io.File;
import java.io.IOException;

/**
 * author: sundong
 * created at 2017/1/11 14:44
 */
public class FileStorageManager {
    public static FileStorageManager getInstance(){
        return Holder.sFileStorageManager;
    }
    public static class Holder{
        private static FileStorageManager sFileStorageManager = new FileStorageManager();
    }

    private Context mContext;

    private FileStorageManager() {

    }

    public void init(Context context) {
        this.mContext = context;
    }

    public File getFileByName(String url) {
        File parent;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            parent = mContext.getExternalCacheDir();
        } else {
            parent = mContext.getCacheDir();
        }
        String fileName = Md5Uills.generateCode(url);
        File file = new File(parent, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
