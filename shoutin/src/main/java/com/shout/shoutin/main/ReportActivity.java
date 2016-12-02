package com.shout.shoutin.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.shout.shoutin.CustomClasses.CustomSnackBarLayout;
import com.shout.shoutin.R;
import com.shout.shoutin.Utils.CallWebService;
import com.shout.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shout.shoutin.Utils.Constants;
import com.shout.shoutin.app.AppController;
import com.shout.shoutin.base.BaseActivity;
import com.shout.shoutin.database.DatabaseHelper;

import org.json.JSONObject;

public class ReportActivity extends BaseActivity implements CallWebService.WebserviceResponse, View.OnClickListener, ConnectivityBroadcastReceiver.ConnectivityReceiverListener {

    Button btnCloseReport;
    Button btnReport1;
    Button btnReport2;
    Button btnReport3;
    Button btnReport4;

    String strShoutId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        strShoutId = getIntent().getExtras().getString("REPORT_SHOUT_ID");

        setContentView(R.layout.activity_report);
        hideBottomTabs();
        hideBothTopHeader();
        // CALLES CONNETIVITY CHECK LISTENER
        new AppController().setConnectivityListener(this);
        initialize();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // CALLES CONNETIVITY CHECK LISTENER
        new AppController().setConnectivityListener(this);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // CALLES CONNETIVITY CHECK LISTENER
        new AppController().setConnectivityListener(this);

    }

    private void initialize() {
        btnCloseReport = (Button) findViewById(R.id.btn_report_close);
        btnReport1 = (Button) findViewById(R.id.report_1);
        btnReport2 = (Button) findViewById(R.id.report_2);
        btnReport3 = (Button) findViewById(R.id.report_3);
        btnReport4 = (Button) findViewById(R.id.report_4);

        btnCloseReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent objIntent = new Intent(ReportActivity.this, ShoutDetailActivity.class);
                startActivity(objIntent);
                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                finish();
            }
        });

        btnReport1.setOnClickListener(this);
        btnReport2.setOnClickListener(this);
        btnReport3.setOnClickListener(this);
        btnReport4.setOnClickListener(this);

    }

    @Override
    public void onWebserviceResponce(String strUrl, String strResult) {
        if (Constants.REPORT_API.equals(strUrl)) {
            try {
                JSONObject objJsonObject = new JSONObject(strResult);
                if (objJsonObject.getBoolean("result")) {
                    SharedPreferences objProfileSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                    JSONObject object = new JSONObject();
                    object.put("user_id", objProfileSharedPreferences.getString(Constants.USER_ID, ""));
                    object.put("shout_id", strShoutId);
                    new CallWebService(Constants.SHOUT_PASS_API, object, ReportActivity.this, this, true).execute();
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Constants.SHOUT_PASS_API.equals(strUrl)) {
            try {
                JSONObject objJsonObject = new JSONObject(strResult);
                if (objJsonObject.getBoolean("result")) {
                    DatabaseHelper objDatabaseHelper = new DatabaseHelper(ReportActivity.this);
                    objDatabaseHelper.deleteShoutById(strShoutId);
                    ReportActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(ReportActivity.this, "Thanks for your feedback, we will look into the matter.", Toast.LENGTH_SHORT).show();

                            Intent objIntent = new Intent(ReportActivity.this, ShoutDefaultActivity.class);
                            objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(objIntent);
                            overridePendingTransition(0, 0);
                            finish();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int intPosition = 0;
        switch (v.getId()) {
            case R.id.report_1:
                intPosition = 1;
                break;
            case R.id.report_2:
                intPosition = 2;
                break;
            case R.id.report_3:
                intPosition = 3;
                break;
            case R.id.report_4:
                intPosition = 4;
                break;
        }
        reportShout(intPosition);
    }

    private void reportShout(int intReportPosition) {
        try {
            SharedPreferences objProfileSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
            JSONObject object = new JSONObject();
            object.put("user_id", objProfileSharedPreferences.getString(Constants.USER_ID, ""));
            object.put("shout_id", strShoutId);
            object.put("position_id", intReportPosition);
            new CallWebService(Constants.REPORT_API, object, ReportActivity.this, this, true).execute();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showInternetView(isConnected);
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
    public void onPermissionsGranted(int requestCode) {

    }
}
