package com.start.lewish.filedownload_lib;

/**
 * author: sundong
 * created at 2017/1/11 14:44
 * 下载配置：核心线程数，最大线程数，进度更新线程数
 */
public class DownloadConfig {
    private int coreThreadSize;
    private int maxThreadSize;
    private int localProgressThreadSize;

    private DownloadConfig(Builder builder) {
        coreThreadSize = builder.coreThreadSize == 0 ? DownloadManager.MAX_THREAD : builder.coreThreadSize;
        maxThreadSize = builder.maxThreadSize == 0 ? DownloadManager.MAX_THREAD : builder.coreThreadSize;
        localProgressThreadSize = builder.localProgressThreadSize == 0 ? DownloadManager.LOCAL_PROGRESS_SIZE : builder.localProgressThreadSize;
    }

    public int getCoreThreadSize() {
        return coreThreadSize;
    }

    public int getMaxThreadSize() {
        return maxThreadSize;
    }

    public int getLocalProgressThreadSize() {
        return localProgressThreadSize;
    }


    public static class Builder {
        private int coreThreadSize;
        private int maxThreadSize;
        private int localProgressThreadSize;

        public Builder coreThreadSize(int coreThreadSize) {
            this.coreThreadSize = coreThreadSize;
            return this;
        }

        public Builder maxThreadSize(int maxThreadSize) {
            this.maxThreadSize = maxThreadSize;
            return this;
        }

        public Builder localProgressThreadSize(int localProgressThreadSize) {
            this.localProgressThreadSize = localProgressThreadSize;
            return this;
        }


        public DownloadConfig build() {
            return new DownloadConfig(this);
        }
    }
}
