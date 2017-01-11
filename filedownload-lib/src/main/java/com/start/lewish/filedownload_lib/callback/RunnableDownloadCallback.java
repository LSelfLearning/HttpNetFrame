package com.start.lewish.filedownload_lib.callback;

import java.io.File;

/**
 * author: sundong
 * created at 2017/1/11 14:44
 * 下载任务对象
 */
public interface RunnableDownloadCallback {

    void success(File file);

    void fail(int errorCode, String errorMessage);

    void progress(int progress);
}
