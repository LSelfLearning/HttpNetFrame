package com.start.lewish.httpnetframe;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.start.lewish.filedownload_lib.DownloadConfig;
import com.start.lewish.filedownload_lib.DownloadManager;

/**
 * @author nate
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        DownloadConfig config = new DownloadConfig.Builder()
                .coreThreadSize(2)
                .maxThreadSize(4)
                .localProgressThreadSize(1)
                .build();
        DownloadManager.Holder.getInstance().init(config,getApplicationContext());

        LeakCanary.install(this);
    }
}
