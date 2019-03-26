package com.example.cj.videoeditor.activity;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cj.videoeditor.Constants;
import com.example.cj.videoeditor.R;
import com.example.cj.videoeditor.media.VideoInfo;
import com.example.cj.videoeditor.mediacodec.MediaMuxerRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频拼接的类
 */

public class VideoConnectActivity extends BaseActivity implements View.OnClickListener {
    private TextView mPathOne;
    private TextView mPathTwo;
    private String path1;
    private String path2;

    private ArrayList<VideoInfo> mInfoList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_connect);
        findViewById(R.id.select_one).setOnClickListener(this);
        findViewById(R.id.select_two).setOnClickListener(this);
        findViewById(R.id.video_connect).setOnClickListener(this);

        mPathOne = (TextView) findViewById(R.id.path_one);
        mPathTwo = (TextView) findViewById(R.id.path_two);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_one:
                VideoSelectActivity.openActivityForResult(this, 100);
                break;
            case R.id.select_two:
                VideoSelectActivity.openActivityForResult(this, 101);
                break;
            case R.id.video_connect:
                if (TextUtils.isEmpty(path1) || TextUtils.isEmpty(path2)) {
                    Toast.makeText(this, "Please select video", Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] data = {path1, path2};
                setDataSource(data);


                break;
        }
    }
    private String outputPath,outputPath2;
    public void setDataSource(String[] dataSource) {

        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        mInfoList.clear();
        for (int i = 0; i < dataSource.length; i++) {
            VideoInfo info = new VideoInfo();
            String path = dataSource[i];
            retr.setDataSource(path);
            String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String duration = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            info.path = path;
            info.rotation = Integer.parseInt(rotation);
            info.width = Integer.parseInt(width);
            info.height = Integer.parseInt(height);
            info.duration = Integer.parseInt(duration);

            mInfoList.add(info);
        }
        outputPath = Constants.getPath("video/connect/", System.currentTimeMillis() + ".mp4");
        outputPath2 = Constants.getPath("", System.currentTimeMillis() + ".mp4");
        showLoading("processing...");
        MediaMuxerRunnable instance = new MediaMuxerRunnable();
        MediaMuxerRunnable instance2  = new MediaMuxerRunnable();
        instance.setVideoInfo(mInfoList, outputPath);
        instance2.setVideoInfo(mInfoList,outputPath2);
        instance.addMuxerListener(new MediaMuxerRunnable.MuxerListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        endLoading();
                        Toast.makeText(VideoConnectActivity.this,"succeed Video path: "+outputPath,Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(new File(outputPath+File.separator));
                        intent.setData(uri);
                        sendBroadcast(intent);
                    }
                });
            }
        });
        instance.start();
        instance2.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 100) {
                path1 = data.getStringExtra("path");
                mPathOne.setText(path1);
            } else if (requestCode == 101) {
                path2 = data.getStringExtra("path");
                mPathTwo.setText(path2);
            }
        }
    }
}
