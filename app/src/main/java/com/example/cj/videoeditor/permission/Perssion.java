package com.example.cj.videoeditor.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Perssion {

    public boolean check(int i, Context con) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (i) {
                case 1:
                    if (ActivityCompat.checkSelfPermission(con, Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED)
                        return true;
                    else
                        return false;
                case 2:
                    if (ActivityCompat.checkSelfPermission(con, Manifest.permission.RECORD_AUDIO) ==
                            PackageManager.PERMISSION_GRANTED)
                        return true;
                    else
                        return false;
                case 3:
                    if (ActivityCompat.checkSelfPermission(con, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED)
                        return true;
                    else
                        return false;
            }
        }
        return false;
    }
    //强制设置权限
    public void forceRequest(int i, Activity act,Context con){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (i) {
                case 1:
                    if (ContextCompat.checkSelfPermission(con, Manifest.permission.CAMERA) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(act, new String[]{
                                Manifest.permission.CAMERA}, 2);
                    }
                    break;
                case 2:
                    if (ContextCompat.checkSelfPermission(con, Manifest.permission.RECORD_AUDIO) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.RECORD_AUDIO}
                                , 2);
                    }
                    break;
                case 3:
                    if (ContextCompat.checkSelfPermission(con, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                , 2);
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
