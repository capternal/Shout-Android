package com.shout.shoutin.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shout.shoutin.R;
import com.shout.shoutin.base.BaseActivity;


public class ShareActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        hideBottomTabs();
        hideDefaultTopHeader();
        initialize();
    }

    private void initialize() {
        BaseActivity.objTextViewDrawerCustomBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent objIntent = new Intent(ShareActivity.this, ShoutDetailActivity.class);
                startActivity(objIntent);
                finish();
                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
            }
        });


        BaseActivity.objTextViewDrawerCustomDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.objSlidingDrawer.animateOpen();
            }
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }
}
