package com.shoutin.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;

import com.shoutin.Utils.CallWebService;
import com.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shoutin.Utils.Constants;
import com.shoutin.Utils.Utils;
import com.shoutin.database.DatabaseHelper;
import com.shoutin.main.Model.ShoutDefaultListModel;
import com.shoutin.main.ShoutDefaultActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by CapternalSystems on 9/15/2016.
 */
public class ShoutBoardBackgroundService extends Service implements CallWebService.WebserviceResponse {


    DatabaseHelper objDatabaseHelper = new DatabaseHelper(this);
    SharedPreferences objSharedPreferences;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        objDatabaseHelper = new DatabaseHelper(this);
        try {
            System.out.println("DATABASE SHOUT EXISTANCE : " + objDatabaseHelper.getAllShoutId().length());
        } catch (CursorIndexOutOfBoundsException ce) {
            ce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Utils.d("SERVICE :", "SHOUTBOARD SERVICE CALLED");
        }
        if (ConnectivityBroadcastReceiver.isConnected()) {
            try {
                if (objDatabaseHelper.getAllShoutId().length() > 0) {
                    callNewShoutsApi();
                }
            } catch (CursorIndexOutOfBoundsException ce) {
                ce.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Utils.d("SERVICE :", "NO INTERNET CONNECTION");
        }
        return Service.START_NOT_STICKY;
    }

    private void callWebService() {
        try {
            objDatabaseHelper = new DatabaseHelper(this);
            JSONObject objJsonObject = new JSONObject();
            SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
            objJsonObject.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
            if (objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "").isEmpty() && objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, "").isEmpty()) {
                objJsonObject.put("latitude", objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, ""));
                objJsonObject.put("longitude", objSharedPreferences.getString(Constants.USER_REGISTERED_LONGITUDE, ""));
            } else {
                objJsonObject.put("latitude", objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, ""));
                objJsonObject.put("longitude", objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, ""));
            }
            objJsonObject.put("shout_ids", objDatabaseHelper.getAllShoutId());
            if (objDatabaseHelper.getAllShoutId().length() > 0)
                new CallWebService(Constants.GET_SHOUT_BOARD_UPDATED_RECORDS, objJsonObject, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        System.out.println("BACKGROUND SERVICE : ON DESTROY CALLED ");
        stopSelf();
        super.onDestroy();
    }

    @Override
    public void onWebserviceResponce(String strUrl, String strResult) {
        if (strUrl.equals(Constants.GET_SHOUT_BOARD_UPDATED_RECORDS)) {

            try {
                JSONObject objJsonObject = new JSONObject(strResult);
                if (objJsonObject.getBoolean("result")) {
                    JSONArray objJsonArray = objJsonObject.getJSONArray("shout");
                    for (int index = 0; index < objJsonArray.length(); index++) {
                        try {
                            ShoutDefaultListModel objShoutDefaultListModel;
                            if (objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutDistance).equals("NAN Km")) {
                                objShoutDefaultListModel =
                                        new ShoutDefaultListModel(
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutId).toString(),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutUserId).toString(),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutUserName).toString(),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutUserPic).toString(),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutCommentCount).toString(),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutLikeCount).toString(),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutEngageCount).toString(),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutType).toString(),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutTitle).toString(),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutDescription).toString(),
                                                Integer.parseInt(objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutLikeStatus).toString()),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutCreateDate).toString(),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutImage).toString(),
                                                Integer.parseInt(objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutHideStatus).toString()),
                                                ShoutDefaultActivity.VIEW_PAGER_DEFAULT_POSITION,
                                                Constants.SHOUT_PASS_ENGAGE_BUTTON_DYNAMIC_HEIGHT,
                                                Constants.DEFAULT_Y,
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutImages).toString(),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutIsSearchable),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutLatitude),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutLongitude),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutAddress),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutCategoryName),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutCategoryId),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutIsHidden),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutStartDate),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutEndDate),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutReShout),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutContinueChat),
                                                "0 Km",
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutIsFriend),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutNotionalValue),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutShortAddress),
                                                objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutShareLink)
                                        );
                            } else {
                                objShoutDefaultListModel = new ShoutDefaultListModel(
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutId).toString(),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutUserId).toString(),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutUserName).toString(),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutUserPic).toString(),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutCommentCount).toString(),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutLikeCount).toString(),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutEngageCount).toString(),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutType).toString(),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutTitle).toString(),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutDescription).toString(),
                                        Integer.parseInt(objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutLikeStatus).toString()),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutCreateDate).toString(),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutImage).toString(),
                                        Integer.parseInt(objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutHideStatus).toString()),
                                        ShoutDefaultActivity.VIEW_PAGER_DEFAULT_POSITION,
                                        Constants.SHOUT_PASS_ENGAGE_BUTTON_DYNAMIC_HEIGHT,
                                        Constants.DEFAULT_Y,
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutImages).toString(),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutIsSearchable),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutLatitude),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutLongitude),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutAddress),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutCategoryName),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutCategoryId),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutIsHidden),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutStartDate),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutEndDate),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutReShout),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutContinueChat),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutDistance),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutIsFriend),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutNotionalValue),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutShortAddress),
                                        objJsonArray.getJSONObject(index).getString(DatabaseHelper.strShoutShareLink)
                                );
                            }
                            objShoutDefaultListModel = objDatabaseHelper.updateShout(objShoutDefaultListModel, objShoutDefaultListModel.getSHOUT_ID());

                            if (objShoutDefaultListModel != null) {
                                Intent objIntentShoutUpdateBroadcast = new Intent(Constants.SHOUT_UPDATE_INTENT);
//                                objIntentShoutUpdateBroadcast.putExtra("SHOUT_ID", objShoutDefaultListModel.getSHOUT_ID());
                                sendBroadcast(objIntentShoutUpdateBroadcast);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // TODO: 07/11/16 SAVING NOTIFICATION COUNT INTO PROFILE SHAREDPREFERENCES

                    System.out.println("NOTIFICATION N COUNT IN SHOUT BACKGROUND SERVICE : " + objJsonObject.getString("notification_count"));
                    System.out.println("NOTIFICATION M COUNT IN SHOUT BACKGROUND SERVICE : " + objJsonObject.getString("message_count"));

                    SharedPreferences objSharedPreferences = this.getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                    if (objJsonObject.getString("notification_count").equals("") && objJsonObject.getString("message_count").equals("")) {
                        SharedPreferences.Editor editor = objSharedPreferences.edit();
                        editor.putString(Constants.USER_NOTIFICATION_COUNT, "0");
                        editor.putString(Constants.USER_MESSAGE_COUNT, "0");
                        editor.commit();
                    } else {
                        SharedPreferences.Editor editor = objSharedPreferences.edit();
                        editor.putString(Constants.USER_NOTIFICATION_COUNT, objJsonObject.getString("notification_count"));
                        editor.putString(Constants.USER_MESSAGE_COUNT, objJsonObject.getString("message_count"));
                        editor.commit();
                    }
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Constants.SHOUT_LIST.equals(strUrl)) {
            callWebService();
            try {
                Utils.d("NEW_SHOUTS", strResult);
                JSONObject jSONObject = new JSONObject(strResult);
                if (jSONObject.getString("result").equals("true")) {
                    JSONArray objJsonArray = new JSONArray(jSONObject.getString("shout"));
                    if (objJsonArray.length() > 0) {
                        ArrayList<ShoutDefaultListModel> arrShoutDefaultListModel = new ArrayList<ShoutDefaultListModel>();
                        arrShoutDefaultListModel = objDatabaseHelper.saveShout(objJsonArray, "0");
                        if (arrShoutDefaultListModel.size() > 0) {
                            Intent objIntentShoutUpdateBroadcast = new Intent(Constants.SHOUT_UPDATE_INTENT);
                            sendBroadcast(objIntentShoutUpdateBroadcast);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void callNewShoutsApi() {
        try {
            if (objSharedPreferences.getString(Constants.SHOUT_CATEGORY_PREFERENCE_ID, "").equals("") || objSharedPreferences.getString(Constants.SHOUT_CATEGORY_PREFERENCE_ID, "").equals("0")) {
                objSharedPreferences.edit().putString(Constants.SHOUT_CATEGORY_PREFERENCE_ID, "1").commit();
            }

            Utils.d("PRASANNA", "FIRST : " + objDatabaseHelper.getShoutId("FIRST"));

            if (objSharedPreferences.getString(Constants.SHOUT_CATEGORY_PREFERENCE_ID, "").equals("1")) {

                JSONObject objJsonObject = new JSONObject();
                objJsonObject.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
                objJsonObject.put("offset", objDatabaseHelper.getShoutId("FIRST"));
                objJsonObject.put("pull_refresh", 1);
                /*objJsonObject.put("popularity", objSharedPreferences.getString(Constants.SORT_POPULARITY, ""));
                objJsonObject.put("recency", objSharedPreferences.getString(Constants.SORT_RECENCY, ""));
                objJsonObject.put("location", objSharedPreferences.getString(Constants.SORT_LOCATION, ""));*/
                if (objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "").isEmpty() && objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, "").isEmpty()) {
                    objJsonObject.put("latitude", objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, ""));
                    objJsonObject.put("longitude", objSharedPreferences.getString(Constants.USER_REGISTERED_LONGITUDE, ""));
                } else {
                    objJsonObject.put("latitude", objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, ""));
                    objJsonObject.put("longitude", objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, ""));
                }
                objJsonObject.put("preference_id", 1);
                // TODO: 08/11/16 ADDING TWO PARAMETERS FOR SEARCHED TAGS
                objJsonObject.put("category_id", objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY_ID, ""));
                objJsonObject.put("category_frez", objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").trim());
//                objJsonObject.put("scroll_count", objSharedPreferences.getString(Constants.SHOUT_BOARD_LIST_API_SCROLL_COUNT, ""));
                Utils.d("PRASANNA", "BACKGROUND SHOUT LIST INPUT : " + objJsonObject.toString());
                new CallWebService(Constants.SHOUT_LIST, objJsonObject, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
