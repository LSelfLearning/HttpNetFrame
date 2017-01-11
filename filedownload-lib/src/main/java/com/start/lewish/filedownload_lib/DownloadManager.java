package com.start.lewish.filedownload_lib;

import android.content.Context;

import com.start.lewish.filedownload_lib.callback.FileDownLoadCallback;
import com.start.lewish.filedownload_lib.file.FileStorageManager;
import com.start.lewish.filedownload_lib.http.HttpManager;
import com.start.lewish.filedownload_lib.sqlite.DownloadEntity;
import com.start.lewish.filedownload_lib.sqlite.DownloadEntityDao;
import com.start.lewish.filedownload_lib.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author: sundong
 * created at 2017/1/11 14:14
 */
public class DownloadManager {
    private static final String TAG = "DownloadManager";
    public final static int MAX_THREAD = 2;
    public final static int LOCAL_PROGRESS_SIZE = 1;
    private HashSet<FileDownloadTask> mTaskSet = new HashSet<>();

    private List<DownloadEntity> mCache;

    private long mFileLength;

    private Context mContext;
    private static ExecutorService sLocalProgressPool = Executors.newFixedThreadPool(LOCAL_PROGRESS_SIZE);

    private static ThreadPoolExecutor sThreadPool = new ThreadPoolExecutor(MAX_THREAD, MAX_THREAD, 60, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(), new ThreadFactory() {

        private AtomicInteger mInteger = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "download thread #" + mInteger.getAndIncrement());
            return thread;
        }
    });

    private DownloadManager() {}

    public static class Holder {

        private static DownloadManager sManager = new DownloadManager();

        public static DownloadManager getInstance() {
            return sManager;
        }
    }

    public void init(DownloadConfig config, Context context) {
        this.mContext = context;
        FileStorageManager.Holder.getInstance().init(context);
        DownloadEntityDao.Holder.getInstance().init(context);

        sThreadPool = new ThreadPoolExecutor(config.getCoreThreadSize(), config.getMaxThreadSize(), 60, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(), new ThreadFactory() {
            private AtomicInteger mInteger = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, "download thread #" + mInteger.getAndIncrement());
                return thread;
            }
        });
        sLocalProgressPool = Executors.newFixedThreadPool(config.getLocalProgressThreadSize());
    }

    private void finish(FileDownloadTask task) {
        mTaskSet.remove(task);
    }

    public void download(final String url, final FileDownLoadCallback fileDownLoadCallback) {
        final FileDownloadTask task = new FileDownloadTask(url, fileDownLoadCallback);
        if (mTaskSet.contains(task)) {
            fileDownLoadCallback.onFailure(HttpManager.TASK_RUNNING_ERROR_CODE, "任务已经执行了");
            return;
        }
        mTaskSet.add(task);

        mCache = DownloadEntityDao.Holder.getInstance().getDownloadEntityByUrl(url);
        if (mCache == null || mCache.size() == 0) {
            HttpManager.Holder.getInstance().asyncRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    finish(task);
                    Logger.debug("nate", "onFailure ");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (!response.isSuccessful() && fileDownLoadCallback != null) {
                        fileDownLoadCallback.onFailure(HttpManager.NETWORK_ERROR_CODE, "网络出问题了");
                        return;
                    }

                    mFileLength = response.body().contentLength();
                    if (mFileLength == -1) {
                        fileDownLoadCallback.onFailure(HttpManager.CONTENT_LENGTH_ERROR_CODE, "content length -1");
                        return;
                    }
                    processDownload(url, mFileLength, fileDownLoadCallback, mCache);
                    finish(task);
                }
            });

        } else {
            // TODO: 16/9/3 处理已经下载过的数据
            for (int i = 0; i < mCache.size(); i++) {
                DownloadEntity entity = mCache.get(i);
                if (i == mCache.size() - 1) {
                    mFileLength = entity.getEndPos() + 1;
                }
                long startSize = entity.getStartPos() + entity.getProgressPos();
                long endSize = entity.getEndPos();
                sThreadPool.execute(new DownloadRunnable(startSize, endSize, url, fileDownLoadCallback, entity));
            }
        }

        sLocalProgressPool.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        int fileDownLoadProgress = getFileDownLoadProgress(url);
                        if(fileDownLoadProgress>=100) {
                            fileDownLoadCallback.onProgress(fileDownLoadProgress);
                            return;
                        }
                        Logger.error(TAG,"progress="+fileDownLoadProgress);
                        fileDownLoadCallback.onProgress(fileDownLoadProgress);
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public boolean isFileDownLoadFinish(String url){
        File file = FileStorageManager.Holder.getInstance().getFileByName(url);
        long fileSize = file.length();
        return fileSize >= mFileLength;
    }

    private int getFileDownLoadProgress(String url){
        File file = FileStorageManager.Holder.getInstance().getFileByName(url);
        long fileSize = file.length();
        return  (int) (fileSize * 100.0 / mFileLength);
    }

    private void processDownload(String url, long length, FileDownLoadCallback callback, List<DownloadEntity> cache) {
        // 100   2  50  0-49  50-99
        long threadDownloadSize = length / MAX_THREAD;
        if (cache == null || cache.size() == 0) {
            mCache = new ArrayList<>();
        }
        for (int i = 0; i < MAX_THREAD; i++) {
            DownloadEntity entity = new DownloadEntity();
            long startSize = i * threadDownloadSize;
            long endSize = (i + 1) * threadDownloadSize - 1;
            entity.setDownloadUrl(url);
            entity.setStartPos(startSize);
            entity.setEndPos(endSize);
            sThreadPool.execute(new DownloadRunnable(startSize, endSize, url, callback, entity));
        }

    }
}
