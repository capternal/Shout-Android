package com.shout.shoutin.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.shout.shoutin.CustomClasses.CustomSnackBarLayout;
import com.shout.shoutin.R;
import com.shout.shoutin.Utils.CallWebService;
import com.shout.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shout.shoutin.Utils.Constants;
import com.shout.shoutin.app.AppController;
import com.shout.shoutin.database.DatabaseHelper;
import com.shout.shoutin.login.ProfileScreenActivity;
import com.shout.shoutin.main.Adapter.NotificationListAdapter;
import com.shout.shoutin.main.Model.NotificationListModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationListActivity extends Activity implements CallWebService.WebserviceResponse, ConnectivityBroadcastReceiver.ConnectivityReceiverListener {

    ListView objNotificationList;
    TextView objTextViewBack;
    ArrayList<NotificationListModel> arrNotificationListModel = new ArrayList<NotificationListModel>();

    SharedPreferences objSharedPreferences;
    private NotificationListAdapter objNotificationAdapter;

    AppController objAppController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);

        // CALLES CONNETIVITY CHECK LISTENER
        objAppController = new AppController();
        objAppController.setConnectivityListener(this);
        initialize();


        try {
            JSONObject object = new JSONObject();
            object.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
            new CallWebService(Constants.NOTIFICATION_LIST_API, object, NotificationListActivity.this, this, true).execute();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // CALLES CONNETIVITY CHECK LISTENER
        objAppController.setConnectivityListener(this);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // CALLES CONNETIVITY CHECK LISTENER
        objAppController.setConnectivityListener(this);

    }

    private void initialize() {
        objNotificationList = (ListView) findViewById(R.id.listview_notification);
        objTextViewBack = (TextView) findViewById(R.id.btn_notification_back);
        objTextViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        objNotificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (arrNotificationListModel.get(i).getNotification_type().equals("FR")) {
                    if (ConnectivityBroadcastReceiver.isConnected()) {
                        SharedPreferences.Editor objProfileEditor = objSharedPreferences.edit();
                        objProfileEditor.putString(Constants.PROFILE_SCREEN_USER_ID, arrNotificationListModel.get(i).getUser_id());
                        objProfileEditor.putString(Constants.PROFILE_BACK_SCREEN_NAME, Constants.SHOUT_DETAIL_SCREEN);
                        objProfileEditor.putString(Constants.PROFILE_NOTIFICATION_BACK, "1");
                        objProfileEditor.commit();

                        Intent objIntent = new Intent(NotificationListActivity.this, ProfileScreenActivity.class);
                        startActivity(objIntent);
                        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                        finish();
                    } else {
                        Constants.showInternetToast(NotificationListActivity.this);
                    }
                } else if (arrNotificationListModel.get(i).getNotification_type().equals("CO") || arrNotificationListModel.get(i).getNotification_type().equals("LC") || arrNotificationListModel.get(i).getNotification_type().equals("CS")) {
                    // TODO: 10/11/16 STORING SHOUT ID FOR DETAIL SCREEN
                    SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, 0);
                    SharedPreferences.Editor objEditor = objSharedPreferences.edit();
                    objEditor.putString(Constants.SHOUT_ID_FOR_DETAIL_SCREEN, "");
                    objEditor.commit();

                    System.out.println("SHOUT ID PASSING FROM NOTIFICATION LIST : " + arrNotificationListModel.get(i).getShout_id());

                    new DatabaseHelper(NotificationListActivity.this).deleteShoutEntries();

                    Intent objIntent = new Intent(NotificationListActivity.this, ShoutDetailActivity.class);
                    Bundle objBundle = new Bundle();
                    objBundle.putString(Constants.SHOUT_ID_FOR_DETAIL_SCREEN, arrNotificationListModel.get(i).getShout_id());
                    objIntent.putExtra("NOTIFICATION_DATA", objBundle);
                    objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(objIntent);
                    finish();
                }
            }
        });

    }

    @Override
    public void onWebserviceResponce(String strUrl, String strResult) {
        if (Constants.NOTIFICATION_LIST_API.equals(strUrl)) {
            try {
                JSONObject objJsonObject = new JSONObject(strResult);
                if (objJsonObject.getBoolean("result")) {
                    JSONArray objJsonArray = new JSONArray(objJsonObject.getString("notifications"));
                    arrNotificationListModel.clear();
                    for (int index = 0; index < objJsonArray.length(); index++) {
                        NotificationListModel objNotificationListModel = new NotificationListModel(
                                objJsonArray.getJSONObject(index).getString("id"),
                                objJsonArray.getJSONObject(index).getString("message"),
                                objJsonArray.getJSONObject(index).getString("user_id"),
                                objJsonArray.getJSONObject(index).getString("username"),
                                objJsonArray.getJSONObject(index).getString("notification_type"),
                                objJsonArray.getJSONObject(index).getString("created"),
                                objJsonArray.getJSONObject(index).getString("user_photo"),
                                objJsonArray.getJSONObject(index).getString("is_read"),
                                objJsonArray.getJSONObject(index).getString("shout_id")
                        );
                        arrNotificationListModel.add(objNotificationListModel);
                    }
                    if (arrNotificationListModel.size() > 0) {
                        showDefaultMessgae(false);
                        objNotificationAdapter = new NotificationListAdapter(arrNotificationListModel, NotificationListActivity.this);
                        objNotificationList.setAdapter(objNotificationAdapter);
                        try {
                            JSONObject object = new JSONObject();
                            object.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
                            new CallWebService(Constants.NOTIFICATION_READ_API, object, NotificationListActivity.this, this, false).execute();
                        } catch (NullPointerException ne) {
                            ne.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        showDefaultMessgae(true);
                    }
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Constants.NOTIFICATION_READ_API.equals(strUrl)) {
            try {
                JSONObject objJsonObject = new JSONObject(strResult);
                if (objJsonObject.getBoolean("result")) {
                    for (int index = 0; index < arrNotificationListModel.size(); index++) {
                        NotificationListModel objNotificationListModel = arrNotificationListModel.get(index);
                        objNotificationListModel.setIs_read("Y");
                    }
                    /*Parcelable state = objNotificationList.onSaveInstanceState();
                    objNotificationAdapter = new NotificationListAdapter(arrNotificationListModel, NotificationListActivity.this);
                    objNotificationList.setAdapter(objNotificationAdapter);
                    objNotificationList.onRestoreInstanceState(state);*/
                    getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE).edit().putString(Constants.USER_NOTIFICATION_COUNT, "").commit();
                }
            } catch (ArrayIndexOutOfBoundsException ain) {
                ain.printStackTrace();
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    private void showDefaultMessgae(boolean b) {
        TextView objTextViewNotificationNotAvailable = (TextView) findViewById(R.id.txt_no_notification_found);
        if (b) {
            objTextViewNotificationNotAvailable.setVisibility(TextView.VISIBLE);
        } else {
            objTextViewNotificationNotAvailable.setVisibility(TextView.GONE);
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
