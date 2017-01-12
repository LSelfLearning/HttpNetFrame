package com.start.lewish.filedownload_lib;

import android.os.Handler;
import android.os.Message;
import android.os.Process;

import com.start.lewish.filedownload_lib.file.FileStorageManager;
import com.start.lewish.filedownload_lib.http.HttpManager;
import com.start.lewish.filedownload_lib.sqlite.DownloadEntity;
import com.start.lewish.filedownload_lib.sqlite.DownloadEntityDao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;

/**
 * author: sundong
 * created at 2017/1/11 14:44
 * 分段下载Runnable
 */
public class DownloadRunnable implements Runnable {
    private static final String TAG = "DownloadRunnable";

    private long mStart;

    private long mEnd;

    private String mUrl;

    private Handler mHandler;

    private DownloadEntity mEntity;

    public DownloadRunnable(long mStart, long mEnd, String mUrl, Handler hanler, DownloadEntity mEntity) {
        this.mStart = mStart;
        this.mEnd = mEnd;
        this.mUrl = mUrl;
        this.mHandler = hanler;
        this.mEntity = mEntity;
    }

    public DownloadRunnable(long mStart, long mEnd, String mUrl, Handler hanler) {
        this.mStart = mStart;
        this.mEnd = mEnd;
        this.mUrl = mUrl;
        this.mHandler = hanler;
    }
    private void sendDownLoadSuccess(File file) {
        Message msg = new Message();
        msg.what = DownloadManager.DOWNLOAD_SUCCESS;
        msg.obj = file;
        mHandler.sendMessage(msg);
    }

    private void sendDownLoadFailureMsg(int errorCode, String errorMsg) {
        Message msg = new Message();
        msg.what = DownloadManager.DOWNLOAD_FAILURE;
        msg.arg1 = errorCode;
        msg.obj = errorMsg;
        mHandler.sendMessage(msg);
    }
    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Response response = HttpManager.Holder.getInstance().syncRequestByRange(mUrl, mStart, mEnd);
        if (response == null) {
            sendDownLoadFailureMsg(HttpManager.NETWORK_ERROR_CODE, "网络出问题了");
            return;
        }
        File file = FileStorageManager.Holder.getInstance().getFileByName(mUrl);
        long progress = mEntity.getProgressPos() == 0 ? 0 : mEntity.getProgressPos();
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(mStart);
            byte[] buffer = new byte[1024 * 500];
            int len;
            InputStream inStream = response.body().byteStream();
            while ((len = inStream.read(buffer, 0, buffer.length)) != -1) {
                randomAccessFile.write(buffer, 0, len);
                progress += len;
                mEntity.setProgressPos(progress);
//                Logger.debug(TAG, "progress  ----->" + progress);
            }
            randomAccessFile.close();
            if(DownloadManager.Holder.getInstance().isFileDownLoadFinish(mEntity.getDownloadUrl())) {
                //文件下载完成
                sendDownLoadSuccess(file);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            DownloadEntityDao.Holder.getInstance().insertOrReplace(mEntity);
        }

    }
}
