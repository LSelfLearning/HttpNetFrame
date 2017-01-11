package com.start.lewish.filedownload_lib;


import com.start.lewish.filedownload_lib.callback.FileDownLoadCallback;

/**
 * author: sundong
 * created at 2017/1/11 14:44
 * 下载任务对象
 */
public class FileDownloadTask {
    private String mUrl;

    private FileDownLoadCallback mCallback;

    public FileDownloadTask(String mUrl, FileDownLoadCallback mCallback) {
        this.mUrl = mUrl;
        this.mCallback = mCallback;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setCallback(FileDownLoadCallback mCallback) {
        this.mCallback = mCallback;
    }

    public FileDownLoadCallback getCallback() {
        return mCallback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileDownloadTask that = (FileDownloadTask) o;

        if (mUrl != null ? !mUrl.equals(that.mUrl) : that.mUrl != null) return false;
        return mCallback != null ? mCallback.equals(that.mCallback) : that.mCallback == null;

    }

    @Override
    public int hashCode() {
        int result = mUrl != null ? mUrl.hashCode() : 0;
        result = 31 * result + (mCallback != null ? mCallback.hashCode() : 0);
        return result;
    }
}
