package com.shoutin.login;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.shoutin.CustomClasses.CustomSnackBarLayout;
import com.shoutin.R;
import com.shoutin.Utils.CallWebService;
import com.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shoutin.Utils.Constants;
import com.shoutin.Utils.Utils;
import com.shoutin.app.AppController;
import com.shoutin.database.DatabaseHelper;
import com.shoutin.login.adapter.MyShoutsAdapter;
import com.shoutin.main.Model.ShoutDefaultListModel;
import com.shoutin.main.ShoutDefaultActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.shoutin.database.DatabaseHelper.strShoutAddress;
import static com.shoutin.database.DatabaseHelper.strShoutCategoryId;
import static com.shoutin.database.DatabaseHelper.strShoutCategoryName;
import static com.shoutin.database.DatabaseHelper.strShoutCommentCount;
import static com.shoutin.database.DatabaseHelper.strShoutContinueChat;
import static com.shoutin.database.DatabaseHelper.strShoutCreateDate;
import static com.shoutin.database.DatabaseHelper.strShoutDescription;
import static com.shoutin.database.DatabaseHelper.strShoutDistance;
import static com.shoutin.database.DatabaseHelper.strShoutEndDate;
import static com.shoutin.database.DatabaseHelper.strShoutEngageCount;
import static com.shoutin.database.DatabaseHelper.strShoutHideStatus;
import static com.shoutin.database.DatabaseHelper.strShoutId;
import static com.shoutin.database.DatabaseHelper.strShoutImage;
import static com.shoutin.database.DatabaseHelper.strShoutImages;
import static com.shoutin.database.DatabaseHelper.strShoutIsFriend;
import static com.shoutin.database.DatabaseHelper.strShoutIsHidden;
import static com.shoutin.database.DatabaseHelper.strShoutIsSearchable;
import static com.shoutin.database.DatabaseHelper.strShoutLatitude;
import static com.shoutin.database.DatabaseHelper.strShoutLikeCount;
import static com.shoutin.database.DatabaseHelper.strShoutLikeStatus;
import static com.shoutin.database.DatabaseHelper.strShoutLongitude;
import static com.shoutin.database.DatabaseHelper.strShoutNotionalValue;
import static com.shoutin.database.DatabaseHelper.strShoutReShout;
import static com.shoutin.database.DatabaseHelper.strShoutShareLink;
import static com.shoutin.database.DatabaseHelper.strShoutShortAddress;
import static com.shoutin.database.DatabaseHelper.strShoutStartDate;
import static com.shoutin.database.DatabaseHelper.strShoutTitle;
import static com.shoutin.database.DatabaseHelper.strShoutType;
import static com.shoutin.database.DatabaseHelper.strShoutUserId;
import static com.shoutin.database.DatabaseHelper.strShoutUserName;
import static com.shoutin.database.DatabaseHelper.strShoutUserPic;

public class MyShoutActivity extends Activity implements CallWebService.WebserviceResponse, View.OnClickListener, ConnectivityBroadcastReceiver.ConnectivityReceiverListener {

    SharedPreferences objSharedPreferences;
    private MyShoutsAdapter myShoutAdapter;
    private ListView objMyShoutsListView;
    private Button btnBackToProfileScreen;
    private TextView objTextViewNoShouts;


    DatabaseHelper objDatabaseHelper;
    private ArrayList<ShoutDefaultListModel> arrShoutDefaultListModel = new ArrayList<ShoutDefaultListModel>();
    Parcelable listviewState;
    String strUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_shout);
        // PROFILE SHARED PREFERENCES
        objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
        objDatabaseHelper = new DatabaseHelper(this);

        // CALLES CONNETIVITY CHECK LISTENER
        new AppController().setConnectivityListener(this);

        init();


        listviewState = objMyShoutsListView.onSaveInstanceState();

        try {
            strUserId = getIntent().getExtras().getString("MY_SHOUT_ID");
            if (strUserId.equals(objSharedPreferences.getString(Constants.USER_ID, ""))) {
                arrShoutDefaultListModel.clear();
                arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("1", "2", true);
                if (arrShoutDefaultListModel.size() > 0) {
                    myShoutAdapter = new MyShoutsAdapter(arrShoutDefaultListModel, MyShoutActivity.this, MyShoutActivity.this);
                    objMyShoutsListView.setAdapter(myShoutAdapter);
                    myShoutAdapter.notifyDataSetChanged();
                    loadMyShouts(false);
                } else {
                    if (ConnectivityBroadcastReceiver.isConnected()) {
                        loadMyShouts(true);
                    } else {
                        Constants.showInternetToast(MyShoutActivity.this);
                    }
                }
            } else {
                if (ConnectivityBroadcastReceiver.isConnected()) {
                    loadMyShouts(true);
                } else {
                    Constants.showInternetToast(MyShoutActivity.this);
                }
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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

    private void init() {
        btnBackToProfileScreen = (Button) findViewById(R.id.my_shouts_back);
        objMyShoutsListView = (ListView) findViewById(R.id.my_shouts_listview);
        objTextViewNoShouts = (TextView) findViewById(R.id.txt_no_my_shouts);
        setListener();
    }

    private void setListener() {
        btnBackToProfileScreen.setOnClickListener(this);
    }

    private void loadMyShouts(boolean showProgress) {
        try {
            JSONObject objJsonObject = new JSONObject();
            objJsonObject.put(Constants.USER_ID, strUserId);
            objJsonObject.put("latitude", objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, ""));
            objJsonObject.put("longitude", objSharedPreferences.getString(Constants.USER_REGISTERED_LONGITUDE, ""));
            new CallWebService(Constants.LOGGED_IN_USER_SHOUTS_API, objJsonObject, this, this, showProgress).execute();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebserviceResponce(String strUrl, String strResult) {
        if (Constants.LOGGED_IN_USER_SHOUTS_API.equals(strUrl)) {
            try {
                arrShoutDefaultListModel = new ArrayList<ShoutDefaultListModel>();
                JSONObject objJsonObject = new JSONObject(strResult);
                if (objJsonObject.getBoolean("result")) {

                    if (strUserId.equals(objSharedPreferences.getString(Constants.USER_ID, ""))) {
                        arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("1", "2", true);
                        if (arrShoutDefaultListModel.size() > 0) {
                            objDatabaseHelper.deleteMyShouts();
                        }
                        arrShoutDefaultListModel.clear();
                        arrShoutDefaultListModel.addAll(objDatabaseHelper.saveShout(new JSONArray(objJsonObject.getString("shout")), "1"));
                        Utils.d("MY_SHOUTS", "MY SHOUT MODEL ARRAY COUNT : " + arrShoutDefaultListModel.size());
                        if (arrShoutDefaultListModel.size() > 0) {
                            objTextViewNoShouts.setVisibility(TextView.GONE);
                            objMyShoutsListView.setAdapter(new MyShoutsAdapter(arrShoutDefaultListModel, MyShoutActivity.this, MyShoutActivity.this));
                        } else {
                            objTextViewNoShouts.setVisibility(TextView.VISIBLE);
                        }
                    } else {
                        try {
                            JSONArray jsonArray = new JSONArray(objJsonObject.getString("shout"));

                            for (int index = 0; index < jsonArray.length(); index++) {
                                try {
                                    ShoutDefaultListModel objShoutDefaultListModel = new ShoutDefaultListModel();
                                    if (jsonArray.getJSONObject(index).getString(strShoutDistance).equals("NAN Km")) {
                                        objShoutDefaultListModel = new ShoutDefaultListModel(
                                                jsonArray.getJSONObject(index).getString(strShoutId).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutUserId).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutUserName).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutUserPic).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutCommentCount).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutLikeCount).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutEngageCount).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutType).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutTitle).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutDescription).toString(),
                                                Integer.parseInt(jsonArray.getJSONObject(index).getString(strShoutLikeStatus).toString()),
                                                jsonArray.getJSONObject(index).getString(strShoutCreateDate).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutImage).toString(),
                                                Integer.parseInt(jsonArray.getJSONObject(index).getString(strShoutHideStatus).toString()),
                                                ShoutDefaultActivity.VIEW_PAGER_DEFAULT_POSITION,
                                                Constants.SHOUT_PASS_ENGAGE_BUTTON_DYNAMIC_HEIGHT,
                                                Constants.DEFAULT_Y,
                                                jsonArray.getJSONObject(index).getString(strShoutImages).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutIsSearchable),
                                                jsonArray.getJSONObject(index).getString(strShoutLatitude),
                                                jsonArray.getJSONObject(index).getString(strShoutLongitude),
                                                jsonArray.getJSONObject(index).getString(strShoutAddress),
                                                jsonArray.getJSONObject(index).getString(strShoutCategoryName),
                                                jsonArray.getJSONObject(index).getString(strShoutCategoryId),
                                                jsonArray.getJSONObject(index).getString(strShoutIsHidden),
                                                jsonArray.getJSONObject(index).getString(strShoutStartDate),
                                                jsonArray.getJSONObject(index).getString(strShoutEndDate),
                                                jsonArray.getJSONObject(index).getString(strShoutReShout),
                                                jsonArray.getJSONObject(index).getString(strShoutContinueChat),
                                                jsonArray.getJSONObject(index).getString("0 Km"),
                                                jsonArray.getJSONObject(index).getString(strShoutIsFriend),
                                                jsonArray.getJSONObject(index).getString(strShoutNotionalValue),
                                                jsonArray.getJSONObject(index).getString(strShoutShortAddress),
                                                jsonArray.getJSONObject(index).getString(strShoutShareLink)
                                        );
                                    } else {
                                        objShoutDefaultListModel = new ShoutDefaultListModel(
                                                jsonArray.getJSONObject(index).getString(strShoutId).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutUserId).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutUserName).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutUserPic).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutCommentCount).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutLikeCount).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutEngageCount).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutType).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutTitle).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutDescription).toString(),
                                                Integer.parseInt(jsonArray.getJSONObject(index).getString(strShoutLikeStatus).toString()),
                                                jsonArray.getJSONObject(index).getString(strShoutCreateDate).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutImage).toString(),
                                                Integer.parseInt(jsonArray.getJSONObject(index).getString(strShoutHideStatus).toString()),
                                                ShoutDefaultActivity.VIEW_PAGER_DEFAULT_POSITION,
                                                Constants.SHOUT_PASS_ENGAGE_BUTTON_DYNAMIC_HEIGHT,
                                                Constants.DEFAULT_Y,
                                                jsonArray.getJSONObject(index).getString(strShoutImages).toString(),
                                                jsonArray.getJSONObject(index).getString(strShoutIsSearchable),
                                                jsonArray.getJSONObject(index).getString(strShoutLatitude),
                                                jsonArray.getJSONObject(index).getString(strShoutLongitude),
                                                jsonArray.getJSONObject(index).getString(strShoutAddress),
                                                jsonArray.getJSONObject(index).getString(strShoutCategoryName),
                                                jsonArray.getJSONObject(index).getString(strShoutCategoryId),
                                                jsonArray.getJSONObject(index).getString(strShoutIsHidden),
                                                jsonArray.getJSONObject(index).getString(strShoutStartDate),
                                                jsonArray.getJSONObject(index).getString(strShoutEndDate),
                                                jsonArray.getJSONObject(index).getString(strShoutReShout),
                                                jsonArray.getJSONObject(index).getString(strShoutContinueChat),
                                                jsonArray.getJSONObject(index).getString(strShoutDistance),
                                                jsonArray.getJSONObject(index).getString(strShoutIsFriend),
                                                jsonArray.getJSONObject(index).getString(strShoutNotionalValue),
                                                jsonArray.getJSONObject(index).getString(strShoutShortAddress),
                                                jsonArray.getJSONObject(index).getString(strShoutShareLink)
                                        );
                                    }
                                    arrShoutDefaultListModel.add(objShoutDefaultListModel);
                                    if (arrShoutDefaultListModel.size() > 0) {
                                        objTextViewNoShouts.setVisibility(TextView.GONE);
                                        objMyShoutsListView.setAdapter(new MyShoutsAdapter(arrShoutDefaultListModel, MyShoutActivity.this, MyShoutActivity.this));
                                    } else {
                                        objTextViewNoShouts.setVisibility(TextView.VISIBLE);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (NullPointerException ne) {
                            ne.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_shouts_back:
                super.onBackPressed();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        init();

        new AppController().setConnectivityListener(this);

        if (listviewState != null) {
        /*    arrShoutDefaultListModel.clear();
            arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("1");
            myShoutAdapter.notifyDataSetChanged();*/
            objMyShoutsListView.onRestoreInstanceState(listviewState);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        new AppController().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        listviewState = objMyShoutsListView.onSaveInstanceState();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showInternetView(isConnected);
    }
}
