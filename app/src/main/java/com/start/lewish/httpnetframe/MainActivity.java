package com.start.lewish.httpnetframe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.start.lewish.filedownload_lib.DownloadManager;
import com.start.lewish.filedownload_lib.callback.IFileDownLoadCallback;
import com.start.lewish.filedownload_lib.file.FileStorageManager;
import com.start.lewish.filedownload_lib.utils.Logger;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private ImageView mImageView;
    private ProgressBar mProgress;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mProgress = (ProgressBar) findViewById(R.id.progress);

        final String url = "http://shouji.360tpcdn.com/160901/84c090897cbf0158b498da0f42f73308/com.icoolme.android.weather_2016090200.apk";
        File file = FileStorageManager.getInstance().getFileByName(url);
        Logger.debug("nate", "file path = " + file.getAbsoluteFile());
//        final String url = "http://szimg.mukewang.com/5763765d0001352105400300-360-202.jpg";
        DownloadManager.getInstance().download(url, new IFileDownLoadCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int progress) {
                mProgress.setProgress(progress);
            }

            @Override
            public void onSuccess(File file) {
                Logger.debug("nate", "success " + file.getAbsoluteFile());
                Toast.makeText(MainActivity.this, "文件下载完成", Toast.LENGTH_SHORT).show();
                installApk(file);

            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                Logger.debug("nate", "fail " + errorCode + "  " + errorMessage);
            }
        });

    }

    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + file.getAbsoluteFile().toString()), "application/vnd.android.package-archive");
        MainActivity.this.startActivity(intent);
    }
}

