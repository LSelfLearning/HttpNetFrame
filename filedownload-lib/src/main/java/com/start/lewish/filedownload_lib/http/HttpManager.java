package com.start.lewish.filedownload_lib.http;


import com.start.lewish.filedownload_lib.callback.FileDownLoadCallback;
import com.start.lewish.filedownload_lib.file.FileStorageManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author: sundong
 * created at 2017/1/11 15:44
 * 下载任务对象
 */
public class HttpManager {

    public static class Holder{
        private static HttpManager sManager = new HttpManager();
        public static HttpManager getInstance(){
            return sManager;
        }
    }

    public static final int NETWORK_ERROR_CODE = 1;

    public static final int CONTENT_LENGTH_ERROR_CODE = 2;

    public static final int TASK_RUNNING_ERROR_CODE = 3;

    private OkHttpClient mClient;


    private HttpManager() {
        mClient = new OkHttpClient();
    }

    /**
     * 同步请求
     * @param url
     * @return
     */
    public Response syncRequest(String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            return mClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 同步请求分段下载
     * @param url
     * @return
     */
    public Response syncRequestByRange(String url, long start, long end) {
        Request request = new Request.Builder().url(url)
                .addHeader("Range", "bytes=" + start + "-" + end)
                .build();
        try {
            return mClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 异步调用
     * @param url
     * @param callback
     */
    public void asyncRequest(final String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(callback);
    }

    /**
     * 异步请求下载文件
     * @param url
     * @param fileDownLoadCallback
     */
    public void asyncRequest(final String url, final FileDownLoadCallback fileDownLoadCallback) {
        Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() && fileDownLoadCallback != null) {
                    fileDownLoadCallback.onFailure(NETWORK_ERROR_CODE, "请求失败");
                }
                File file = FileStorageManager.Holder.getInstance().getFileByName(url);
                byte[] buffer = new byte[1024 * 500];
                int len;
                FileOutputStream fileOut = new FileOutputStream(file);
                InputStream inStream = response.body().byteStream();
                while ((len = inStream.read(buffer, 0, buffer.length)) != -1) {
                    fileOut.write(buffer, 0, len);
                    fileOut.flush();
                }
                if(fileDownLoadCallback!=null) {
                    fileDownLoadCallback.onSuccess(file);
                }
            }
        });
    }
}
