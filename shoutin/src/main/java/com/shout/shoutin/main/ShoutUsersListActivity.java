package com.shout.shoutin.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.shout.shoutin.CustomClasses.CustomFontTextView;
import com.shout.shoutin.CustomClasses.CustomSnackBarLayout;
import com.shout.shoutin.R;
import com.shout.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shout.shoutin.Utils.Constants;
import com.shout.shoutin.Utils.NetworkUtils;
import com.shout.shoutin.Utils.NotificationBroadcastReceiver;
import com.shout.shoutin.Utils.Utils;
import com.shout.shoutin.app.AppController;
import com.shout.shoutin.base.BaseActivity;
import com.shout.shoutin.database.DatabaseHelper;
import com.shout.shoutin.main.Adapter.UserMessageListAdapter;
import com.shout.shoutin.main.Model.UserMessageListModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShoutUsersListActivity extends BaseActivity implements BaseActivity.DrawerOpenCloseListener, View.OnClickListener, AdapterView.OnItemClickListener, ConnectivityBroadcastReceiver.ConnectivityReceiverListener, NotificationBroadcastReceiver.NotificationCountListener {

    ListView objListViewShoutUsers;
    ImageButton objImageButtonBack;
    CustomFontTextView objTextViewShoutTitle;

    ArrayList<UserMessageListModel> arrUserMessageListModels;
    ArrayList<String> arrMessageTitle;
    ArrayList<String> arrMessageLastMessageDate;
    ArrayList<String> arrMessageMessageCount;
    ArrayList<String> arrMessageImageUrl;


    private String strShoutId = "";
    private String strShoutTitle = "";
    private String strLoggedInUserId = "";

    DatabaseHelper objDatabaseHelper;
    Parcelable state;
    SharedPreferences objSharedPreferences;
    SharedPreferences objChatSharedPreferences;

    AppController objAppController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shout_users_list);

        objAppController = new AppController();


        objDatabaseHelper = new DatabaseHelper(this);
        objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
        objChatSharedPreferences = getSharedPreferences(Constants.CHAT_PREFERENCES, MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!ShoutUsersListActivity.super.mayRequestLocationAccess()) {
                return;
            }
        } else {
            startWorkingNormal();
        }
    }

    public void startWorkingNormal() {
        hideBottomTabs();
        showDefaultTopHeader();

        updateNotificationCount(getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_NOTIFICATION_COUNT, ""));
        updateMessageCount(getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_MESSAGE_COUNT, ""));

        BaseActivity.objShoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent objIntent = new Intent(ShoutUsersListActivity.this, ShoutDefaultActivity.class);
                startActivity(objIntent);
                finish();
                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
            }
        });


        strLoggedInUserId = objSharedPreferences.getString(Constants.USER_ID, "");
        strShoutId = objChatSharedPreferences.getString(Constants.CHAT_SHOUT_ID, "");
        strShoutTitle = objChatSharedPreferences.getString(Constants.CHAT_SHOUT_TITLE, "");

        // CALLES CONNETIVITY CHECK LISTENER
        objAppController.setConnectivityListener(this);
        objAppController.setNotificationListener(this);
        initialize();

        state = objListViewShoutUsers.onSaveInstanceState();

        new ShoutMessageUsersAPI().execute();

        // SET OFFLINE DATA FOR MESSAGE USER LIST SCREEN
        try {
            String strResult = objDatabaseHelper.getMessageUserListItems(strShoutId);
            System.out.println("MESSAGE USER LIST DATABASE : " + strResult);
            if (strResult.length() > 0) {
                JSONArray objJsonArray = new JSONArray(strResult);
                arrUserMessageListModels = new ArrayList<UserMessageListModel>();
                for (int index = 0; index < objJsonArray.length(); index++) {
                    UserMessageListModel objUserMessageListModel = new UserMessageListModel(
                            objJsonArray.getJSONObject(index).getString("id"),
                            objJsonArray.getJSONObject(index).getString("name"),
                            objJsonArray.getJSONObject(index).getString("photo"),
                            objJsonArray.getJSONObject(index).getString("time"),
                            objJsonArray.getJSONObject(index).getString("count"),
                            objJsonArray.getJSONObject(index).getString("message"),
                            objJsonArray.getJSONObject(index).getString("message_type")
                    );
                    arrUserMessageListModels.add(objUserMessageListModel);
                }
                if (arrUserMessageListModels.size() > 0) {
                    objListViewShoutUsers.setVisibility(ListView.VISIBLE);
                    objListViewShoutUsers.setAdapter(new UserMessageListAdapter(arrUserMessageListModels, ShoutUsersListActivity.this));
                }
            } else {
                arrUserMessageListModels = new ArrayList<UserMessageListModel>();
                for (int index = 0; index < 2; index++) {
                    UserMessageListModel objUserMessageListModel = new UserMessageListModel(
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            ""
                    );
                    arrUserMessageListModels.add(objUserMessageListModel);
                }
                if (arrUserMessageListModels.size() > 0) {
                    objListViewShoutUsers.setVisibility(ListView.VISIBLE);
                    objListViewShoutUsers.setAdapter(new UserMessageListAdapter(arrUserMessageListModels, ShoutUsersListActivity.this));
                }
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initialize();
            // CALLES CONNETIVITY CHECK LISTENER
            objAppController.setConnectivityListener(this);
            objAppController.setNotificationListener(this);

//            new ShoutMessageUsersAPI().execute();
            if (state != null) {
                objListViewShoutUsers.onRestoreInstanceState(state);
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            // CALLES CONNETIVITY CHECK LISTENER
            objAppController.setConnectivityListener(this);
            objAppController.setNotificationListener(this);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initialize() {
        objListViewShoutUsers = (ListView) findViewById(R.id.listview_shout_users);
        objImageButtonBack = (ImageButton) findViewById(R.id.image_button_shout_users_list_back);
        objTextViewShoutTitle = (CustomFontTextView) findViewById(R.id.txt_screen_shout_users_list_title);
        objTextViewShoutTitle.setText(strShoutTitle.toUpperCase());

        setDrawerOpenCloseListener(this);
        BaseActivity.objImageNotificationCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent objIntent = new Intent(ShoutUsersListActivity.this, NotificationListActivity.class);
                startActivity(objIntent);
            }
        });
        setListener();
    }

    private void setListener() {
        objImageButtonBack.setOnClickListener(this);
        objListViewShoutUsers.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent objIntent;
        switch (v.getId()) {
            case R.id.image_button_shout_users_list_back:
                objIntent = new Intent(ShoutUsersListActivity.this, MessageBoardActivity.class);
                objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(objIntent);
                finish();
                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            UserMessageListModel objUserMessageListModel = arrUserMessageListModels.get(position);

            System.out.println("MESSAGE__USER_LIST : " + objUserMessageListModel.getApponentUserId());
            System.out.println("MESSAGE__USER_LIST : " + objUserMessageListModel.getMessageTitle());
            System.out.println("MESSAGE__USER_LIST : " + objUserMessageListModel.getItemImage());

            SharedPreferences objChatPreferences = getSharedPreferences(Constants.CHAT_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor objDataChatEditor = objChatPreferences.edit();
            objDataChatEditor.putString(Constants.CHAT_APPONENT_ID, objUserMessageListModel.getApponentUserId());
            objDataChatEditor.putString(Constants.CHAT_APPONENT_USER_NAME, objUserMessageListModel.getMessageTitle());
            objDataChatEditor.putString(Constants.CHAT_APPONENT_PROFILE_PIC, objUserMessageListModel.getItemImage());
            objDataChatEditor.putString(Constants.CHAT_BACK, Constants.SHOUT_MESSAGE_USER_LIST_SCREEN);
            objDataChatEditor.commit();

            Utils.d("MESSAGE_USER_LIST", "SHOUT USER NAME" + objChatPreferences.getString(Constants.CHAT_APPONENT_USER_NAME, ""));
            Utils.d("MESSAGE_USER_LIST", "SHOUT USER PROFILE" + objChatPreferences.getString(Constants.CHAT_APPONENT_PROFILE_PIC, ""));


            Intent objIntent = new Intent(ShoutUsersListActivity.this, ChatForShoutActivity.class);
            startActivity(objIntent);
            overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent objIntent = new Intent(ShoutUsersListActivity.this, MessageBoardActivity.class);
        objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(objIntent);
        finish();
        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
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
    public void isDrawerOpen(boolean result) {

    }

    @Override
    public void onNotificationReceived(int count) {
        System.out.println("PRASANNA COMMENT : " + count);
        SharedPreferences.Editor editor = objSharedPreferences.edit();
        editor.putString(Constants.USER_NOTIFICATION_COUNT, String.valueOf(count));
        editor.commit();
        updateNotificationCount(getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_NOTIFICATION_COUNT, ""));
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        startWorkingNormal();
    }

    public class ShoutMessageUsersAPI extends AsyncTask<String, Void, String> {

//        final ProgressDialog objProgressDialog = new ProgressDialog(ShoutUsersListActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*if (objDatabaseHelper.getRecordCountOfTable(DatabaseHelper.strTableNameMessageUserList) == false) {
                objProgressDialog.setMessage("Loading...");
                objProgressDialog.show();
                objProgressDialog.setCanceledOnTouchOutside(false);
            }*/
        }

        @Override
        protected String doInBackground(String... params) {
            String strResult = "";

            try {
                JSONObject objJsonObject = new JSONObject();
                objJsonObject.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
                objJsonObject.put("shout_id", objChatSharedPreferences.getString(Constants.CHAT_SHOUT_ID, ""));
                strResult = NetworkUtils.postData(Constants.MESSAGE_USER_SHOUT_LIST_API, objJsonObject.toString());
                return strResult;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
              /*  if (objProgressDialog.isShowing()) {
                    objProgressDialog.dismiss();
                }*/
                JSONObject objJsonObject = new JSONObject(s);
                if (objJsonObject.getString("result").equals("true")) {

                    // DELETING OLD MESSAGE USER LIST DATA
                    //objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameMessageUserList);

                    // SAVE NEW MESSAGE USER LIST DATA
                    objDatabaseHelper.insertMessageUserListData(strShoutId, objJsonObject.getString("users"));

                    JSONArray objJsonArray = new JSONArray(objJsonObject.getString("users"));
                    arrUserMessageListModels = new ArrayList<UserMessageListModel>();
                    for (int index = 0; index < objJsonArray.length(); index++) {
                        UserMessageListModel objUserMessageListModel = new UserMessageListModel(
                                objJsonArray.getJSONObject(index).getString("id"),
                                objJsonArray.getJSONObject(index).getString("name"),
                                objJsonArray.getJSONObject(index).getString("photo"),
                                objJsonArray.getJSONObject(index).getString("time"),
                                objJsonArray.getJSONObject(index).getString("count"),
                                objJsonArray.getJSONObject(index).getString("message"),
                                objJsonArray.getJSONObject(index).getString("message_type")
                        );
                        arrUserMessageListModels.add(objUserMessageListModel);
                    }
                    if (arrUserMessageListModels.size() > 0) {
                        objListViewShoutUsers.setVisibility(ListView.VISIBLE);
                        objListViewShoutUsers.setAdapter(new UserMessageListAdapter(arrUserMessageListModels, ShoutUsersListActivity.this));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
