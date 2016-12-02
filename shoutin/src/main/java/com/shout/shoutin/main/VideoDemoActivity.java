package com.shout.shoutin.main;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.rtoshiro.view.video.FullscreenVideoLayout;
import com.shout.shoutin.CustomClasses.CustomSnackBarLayout;
import com.shout.shoutin.R;
import com.shout.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shout.shoutin.Utils.Constants;
import com.shout.shoutin.app.AppController;

import java.io.IOException;

public class VideoDemoActivity extends AppCompatActivity implements ConnectivityBroadcastReceiver.ConnectivityReceiverListener {

    FullscreenVideoLayout objFullscreenVideoLayout;
    String VideoURL = "";
    String VideoLocalPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_demo);

        // CALLES CONNETIVITY CHECK LISTENER
        new AppController().setConnectivityListener(this);


        objFullscreenVideoLayout = (FullscreenVideoLayout) findViewById(R.id.videoview);

        VideoURL = getIntent().getExtras().getString("VIDEO_URL");
        VideoLocalPath = getIntent().getExtras().getString("VIDEO_LOCAL_PATH");

        System.out.println("VIDEO URL HERE : " + VideoURL);
        System.out.println("VIDEO LOCAL PATH HERE : " + VideoLocalPath);

        objFullscreenVideoLayout.setVisibility(FullscreenVideoLayout.VISIBLE);
        objFullscreenVideoLayout.setActivity(this);

        Uri videoUri = null;

        if (VideoLocalPath.isEmpty()) {
            videoUri = Uri.parse(VideoURL);
            try {
                objFullscreenVideoLayout.setVideoURI(videoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            videoUri = Uri.parse(VideoLocalPath);
            try {
                objFullscreenVideoLayout.setVideoURI(videoUri);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    objFullscreenVideoLayout.setVideoURI(Uri.parse(VideoURL));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AppController().setConnectivityListener(this);
        
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new AppController().setConnectivityListener(this);

    }

    private void showInternetView(boolean isConnected) {
        System.out.println("CONNECTIVITY LOGIN CHECK STATUS : " + isConnected);
        if (isConnected) {
            CustomSnackBarLayout obj = (CustomSnackBarLayout) findViewById(R.id.relative_internet_check);
            Constants.hideToBottom(obj);
        } else {
            CustomSnackBarLayout obj = (CustomSnackBarLayout) findViewById(R.id.relative_internet_check);
            obj.setVisibility(CustomSnackBarLayout.VISIBLE);
            Constants.show(obj);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showInternetView(isConnected);
    }
}
