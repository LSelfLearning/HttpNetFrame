package com.start.lewish.filedownload_lib.callback;

import java.io.File;

/**
 * author: sundong
 * created at 2017/1/11 14:55
 */
public interface IFileDownLoadCallback {
    void onStart();
    void onProgress(int progress);
    void onSuccess(File file);
    void onFailure(int errorCode, String errorMessage);
}
