package com.example.cj.videoeditor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.cj.videoeditor.R;
import com.example.cj.videoeditor.permission.Perssion;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button recordBtn = (Button) findViewById(R.id.record_activity);
        Button selectBtn = (Button) findViewById(R.id.select_activity);
        Button videoBtn = (Button) findViewById(R.id.video_connect);

        recordBtn.setOnClickListener(this);
        selectBtn.setOnClickListener(this);
        videoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Perssion per = new Perssion();
        switch (v.getId()){
            case R.id.record_activity:
                if(per.check(1,this) && per.check(2,this) && per.check(3,this)){
                    startActivity(new Intent(this, RecordedActivity.class));
                }
                else{
                    per.forceRequest(1,this,this);
                    per.forceRequest(2,this,this);
                    per.forceRequest(3,this,this);
                }

                break;
            case R.id.select_activity:
                if(per.check(3,this)) {
                    VideoSelectActivity.openActivity(this);
                }
                else{
                    per.forceRequest(3,this,this);
                }

                break;
            case R.id.video_connect:
                if(per.check(3,this)) {
                    startActivity(new Intent(MainActivity.this , VideoConnectActivity.class));
                }
                else{
                    per.forceRequest(3,this,this);
                }
                break;
        }
    }
}
