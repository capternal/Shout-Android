package com.shout.shoutin.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.shout.shoutin.CustomClasses.CustomFontTextView;
import com.shout.shoutin.CustomClasses.CustomSnackBarLayout;
import com.shout.shoutin.R;
import com.shout.shoutin.RegistrationIntentService;
import com.shout.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shout.shoutin.Utils.Constants;
import com.shout.shoutin.Utils.NetworkUtils;
import com.shout.shoutin.Utils.NotificationBroadcastReceiver;
import com.shout.shoutin.Utils.Utils;
import com.shout.shoutin.app.AppController;
import com.shout.shoutin.base.BaseActivity;
import com.shout.shoutin.database.DatabaseHelper;
import com.shout.shoutin.main.Adapter.MessageBoardListAdapter;
import com.shout.shoutin.main.Model.MessageBoardModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageBoardActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, ConnectivityBroadcastReceiver.ConnectivityReceiverListener, BaseActivity.DrawerOpenCloseListener, NotificationBroadcastReceiver.NotificationCountListener {

    ListView objListViewMessageBoard;
    ImageButton objImageButtonBack;
    TextView objTextViewNoShoutFound;
    CustomFontTextView objTextViewScreenTitle;

    ArrayList<MessageBoardModel> arrMessageBoardModels;
    ArrayList<String> arrMessageTitle;
    ArrayList<String> arrMessageGroupMembers;
    ArrayList<String> arrMessageMessageCount;
    ArrayList<String> arrMessageImageUrl;

    DatabaseHelper objDatabaseHelper;
    // SWIPRE REFRESH LAYOUT
    private SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences objSharedPreferences;

    AppController objAppController;
    Parcelable listviewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board);

        RegistrationIntentService.arrMessages.clear();

        objDatabaseHelper = new DatabaseHelper(this);
        objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);

        updateNotificationCount(getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_NOTIFICATION_COUNT, ""));
        updateMessageCount(getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_MESSAGE_COUNT, ""));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!MessageBoardActivity.super.mayRequestLocationAccess()) {
                return;
            }
        } else {
            startWorkingNormally();
        }

    }

    public void startWorkingNormally() {
        hideBottomTabs();
        showDefaultTopHeader();

        // CALLES CONNETIVITY CHECK LISTENER
        objAppController = new AppController();
        objAppController.setConnectivityListener(this);
        objAppController.setNotificationListener(this);

        initialize();


        // SET OFFLINE DATA FOR MESSAGE BOARD LIST SCREEN
        try {
            String strResult = objDatabaseHelper.getMessageBoardListItems();
            if (strResult.length() > 0) {
                arrMessageBoardModels = new ArrayList<MessageBoardModel>();
                JSONArray objDBJsonArray = new JSONArray(strResult);
                for (int index = 0; index < objDBJsonArray.length(); index++) {
                    MessageBoardModel objMessageBoardModel = new MessageBoardModel(
                            objDBJsonArray.getJSONObject(index).getString("id"),//shout_id
                            objDBJsonArray.getJSONObject(index).getString("title"),//shout_title
                            objDBJsonArray.getJSONObject(index).getString("count"),//group_message_count
                            Constants.HTTP_URL + objDBJsonArray.getJSONObject(index).getString("photo"), // shout_image
                            objDBJsonArray.getJSONObject(index).getString("shout_owner_id"),// shout_owner_id
                            objDBJsonArray.getJSONObject(index).getString("users"),// shout_members_name
                            objDBJsonArray.getJSONObject(index).getString("user_pic"),// shout owner profile image
                            objDBJsonArray.getJSONObject(index).getString("user_name"),// shout_owner_name
                            objDBJsonArray.getJSONObject(index).getString("shout_type"),//shout_type
                            objDBJsonArray.getJSONObject(index).getString("time"));//time_stamp
                    arrMessageBoardModels.add(objMessageBoardModel);
                }
                if (arrMessageBoardModels.size() > 0) {
                    objListViewMessageBoard.setVisibility(ListView.VISIBLE);
                    objTextViewNoShoutFound.setVisibility(TextView.GONE);
                    objListViewMessageBoard.setAdapter(new MessageBoardListAdapter(arrMessageBoardModels, MessageBoardActivity.this));
                } else {
                    objTextViewNoShoutFound.setVisibility(TextView.VISIBLE);
                    objListViewMessageBoard.setVisibility(ListView.GONE);
                }
            } else {
                Utils.d("MESSAGE_BOARD", "DATA NOT AVAILABLE IN LOCAL DB.");
                arrMessageBoardModels = new ArrayList<MessageBoardModel>();
                for (int index = 0; index < 2; index++) {
                    MessageBoardModel objMessageBoardModel = new MessageBoardModel(
                            "",//shout_id
                            "",//shout_title
                            "",//group_message_count
                            "", // shout_image
                            "",// shout_owner_id
                            "",// shout_members_name
                            "",// shout owner profile image
                            "",// shout_owner_name
                            "",//shout_type
                            "");//time_stamp
                    arrMessageBoardModels.add(objMessageBoardModel);
                }
                if (arrMessageBoardModels.size() > 0) {
                    objListViewMessageBoard.setVisibility(ListView.VISIBLE);
                    objTextViewNoShoutFound.setVisibility(TextView.GONE);
                    objListViewMessageBoard.setAdapter(new MessageBoardListAdapter(arrMessageBoardModels, MessageBoardActivity.this));
                } else {
                    objTextViewNoShoutFound.setVisibility(TextView.VISIBLE);
                    objListViewMessageBoard.setVisibility(ListView.GONE);
                }
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        new CallShoutListAPI().execute();

        objListViewMessageBoard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageBoardModel objMessageBoardModel = null;
                if (arrMessageBoardModels.size() > 0) {
                    System.out.print("DATA Message Board Position : " + position);
                    objMessageBoardModel = arrMessageBoardModels.get(position);
                    SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                    if (objMessageBoardModel.getShoutOwnerId().equals(objSharedPreferences.getString(Constants.USER_ID, ""))) {

                        SharedPreferences objChatPreferences = getSharedPreferences(Constants.CHAT_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor objDataChatEditor = objChatPreferences.edit();
                        objDataChatEditor.putString(Constants.CHAT_APPONENT_ID, objMessageBoardModel.getShoutOwnerId());
                        objDataChatEditor.putString(Constants.CHAT_APPONENT_USER_NAME, objMessageBoardModel.getUserName());
                        objDataChatEditor.putString(Constants.CHAT_APPONENT_PROFILE_PIC, objMessageBoardModel.getProfilePic());
                        objDataChatEditor.putString(Constants.CHAT_SHOUT_ID, objMessageBoardModel.getShoutId());
                        objDataChatEditor.putString(Constants.CHAT_SHOUT_TITLE, objMessageBoardModel.getMessageTitle());
                        objDataChatEditor.putString(Constants.CHAT_SHOUT_TYPE, objMessageBoardModel.getShout_type());
                        objDataChatEditor.putString(Constants.CHAT_SHOUT_IMAGE, objMessageBoardModel.getItemImage());
                        objDataChatEditor.putString(Constants.CHAT_BACK, Constants.SHOUT_MESSAGE_BOARD_SCREEN);
                        objDataChatEditor.commit();

                        Intent objIntent = new Intent(MessageBoardActivity.this, ShoutUsersListActivity.class);
                        startActivity(objIntent);
                        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                    } else {
                        SharedPreferences objChatPreferences = getSharedPreferences(Constants.CHAT_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor objDataChatEditor = objChatPreferences.edit();
                        objDataChatEditor.putString(Constants.CHAT_APPONENT_ID, objMessageBoardModel.getShoutOwnerId());
                        objDataChatEditor.putString(Constants.CHAT_APPONENT_USER_NAME, objMessageBoardModel.getUserName());
                        objDataChatEditor.putString(Constants.CHAT_APPONENT_PROFILE_PIC, objMessageBoardModel.getProfilePic());
                        objDataChatEditor.putString(Constants.CHAT_SHOUT_ID, objMessageBoardModel.getShoutId());
                        objDataChatEditor.putString(Constants.CHAT_SHOUT_TITLE, objMessageBoardModel.getMessageTitle());
                        objDataChatEditor.putString(Constants.CHAT_SHOUT_TYPE, objMessageBoardModel.getShout_type());
                        objDataChatEditor.putString(Constants.CHAT_SHOUT_IMAGE, objMessageBoardModel.getItemImage());
                        objDataChatEditor.putString(Constants.CHAT_BACK, Constants.SHOUT_MESSAGE_BOARD_SCREEN);
                        objDataChatEditor.commit();

                        Intent objIntent = new Intent(MessageBoardActivity.this, ChatForShoutActivity.class);
                        startActivity(objIntent);
                        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initialize();
            // CALLES CONNETIVITY CHECK LISTENER
            objAppController.setConnectivityListener(this);
            objAppController.setNotificationListener(this);

            new CallShoutListAPI().execute();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        BaseActivity.objShoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent objIntent = new Intent(MessageBoardActivity.this, ShoutDefaultActivity.class);
                startActivity(objIntent);
                finish();
                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
            }
        });

        BaseActivity.btnDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objSlidingDrawer.animateOpen();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red_background_color);
        objListViewMessageBoard = (ListView) findViewById(R.id.listview_message_board);
        objImageButtonBack = (ImageButton) findViewById(R.id.image_button_message_board_back);
        objTextViewNoShoutFound = (TextView) findViewById(R.id.txt_no_shout_found_message_board);
        objTextViewScreenTitle = (CustomFontTextView) findViewById(R.id.txt_screen_message_board_title);

        setDrawerOpenCloseListener(this);

        BaseActivity.objImageNotificationCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent objIntent = new Intent(MessageBoardActivity.this, NotificationListActivity.class);
                startActivity(objIntent);
            }
        });
        setListener();
    }

    private void setListener() {
        objImageButtonBack.setOnClickListener(this);
        objListViewMessageBoard.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }

            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent objIntent;
        switch (v.getId()) {
            case R.id.image_button_message_board_back:
                objIntent = new Intent(MessageBoardActivity.this, ShoutDefaultActivity.class);
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
    public void onBackPressed() {
        super.onBackPressed();
        try {
            Intent objIntent = new Intent(MessageBoardActivity.this, ShoutDefaultActivity.class);
            objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(objIntent);
            finish();
            overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        new CallShoutListAPI().execute();
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
    protected void onRestart() {
        super.onRestart();
        // CALLES CONNETIVITY CHECK LISTENER
        objAppController.setConnectivityListener(this);
        objAppController.setNotificationListener(this);

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showInternetView(isConnected);
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
        startWorkingNormally();
    }

    public class CallShoutListAPI extends AsyncTask<String, Void, String> {
        // final ProgressDialog objProgressDialog = new ProgressDialog(MessageBoardActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  if (objDatabaseHelper.getRecordCountOfTable(DatabaseHelper.strTableNameMessageBoard) == false) {
                objProgressDialog.setMessage("Loading...");
                objProgressDialog.show();
                objProgressDialog.setCanceledOnTouchOutside(false);
            }*/
        }

        @Override
        protected String doInBackground(String... params) {
            String strResult = "";
            try {
                SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                JSONObject objJsonObject = new JSONObject();
                objJsonObject.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
                strResult = NetworkUtils.postData(Constants.MESSAGE_SHOUT_LIST_API, objJsonObject.toString());
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
               /* if (objProgressDialog.isShowing()) {
                    objProgressDialog.dismiss();
                }*/
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                JSONObject objJsonObject = new JSONObject(s);
                if (objJsonObject.getString("result").equals("true")) {

                    // DELETING MESSAGE BOARD DATA FROM DATABASE
                    objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameMessageBoard);

                    // SAVING MESSAGE BOARD LIST DATA INTO DATABASE
                    objDatabaseHelper.insertMessageBoardItems(objJsonObject.getString("shouts"));

                    JSONArray objJsonArray = new JSONArray(objJsonObject.getString("shouts"));

                    arrMessageBoardModels = new ArrayList<MessageBoardModel>();
                    for (int index = 0; index < objJsonArray.length(); index++) {
                        MessageBoardModel objMessageBoardModel = new MessageBoardModel(
                                objJsonArray.getJSONObject(index).getString("id"),//shout_id
                                objJsonArray.getJSONObject(index).getString("title"),//shout_title
                                objJsonArray.getJSONObject(index).getString("count"),//group_message_count
                                objJsonArray.getJSONObject(index).getString("photo"), // shout_image
                                objJsonArray.getJSONObject(index).getString("shout_owner_id"),// shout_owner_id
                                objJsonArray.getJSONObject(index).getString("users"),// shout_members_name
                                objJsonArray.getJSONObject(index).getString("user_pic"),// shout owner profile image
                                objJsonArray.getJSONObject(index).getString("user_name"),// shout_owner_name
                                objJsonArray.getJSONObject(index).getString("shout_type"),//shout_type
                                objJsonArray.getJSONObject(index).getString("time"));//time_stamp
                        arrMessageBoardModels.add(objMessageBoardModel);
                    }
                    if (arrMessageBoardModels.size() > 0) {
                        objListViewMessageBoard.setVisibility(ListView.VISIBLE);
                        objTextViewNoShoutFound.setVisibility(TextView.GONE);
                        objListViewMessageBoard.setAdapter(new MessageBoardListAdapter(arrMessageBoardModels, MessageBoardActivity.this));
                    } else {
                        objTextViewNoShoutFound.setVisibility(TextView.VISIBLE);
                        objListViewMessageBoard.setVisibility(ListView.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
