package com.shout.shoutin.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.shout.shoutin.CustomClasses.CustomSnackBarLayout;
import com.shout.shoutin.R;
import com.shout.shoutin.Utils.CallWebService;
import com.shout.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shout.shoutin.Utils.Constants;
import com.shout.shoutin.Utils.Utils;
import com.shout.shoutin.app.AppController;
import com.shout.shoutin.base.BaseActivity;
import com.shout.shoutin.main.Adapter.SearchExpandableAdapter;
import com.shout.shoutin.main.Model.SearchContinent;
import com.shout.shoutin.main.Model.SearchModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShoutSearchActivity extends BaseActivity implements BaseActivity.DrawerOpenCloseListener, CallWebService.WebserviceResponse, View.OnClickListener, ConnectivityBroadcastReceiver.ConnectivityReceiverListener {

    EditText objEditTextSearch;
    ExpandableListView objExpandableListView;
    ImageView objImageViewCancelSearch;

    private ArrayList<SearchContinent> arrSearchContinents = new ArrayList<SearchContinent>();
    private ArrayList<SearchModel> arrRecentSearches = new ArrayList<SearchModel>();
    private ArrayList<SearchModel> arrAllCategories = new ArrayList<SearchModel>();
    private SearchExpandableAdapter searchAdapter;

    SharedPreferences objProfileSharedPreferences;

    AppController objAppController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shout_search);

        objAppController = new AppController();
        objAppController.setConnectivityListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!ShoutSearchActivity.super.mayRequestLocationAccess()) {
                return;
            }
        } else {
            startWorkingNormal();
        }
    }

    public void startWorkingNormal() {
        initialize();
        objProfileSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
        try {
            if (objProfileSharedPreferences.getString(Constants.CATEGORY_JSON, "").isEmpty()) {
                new CallWebService(Constants.CATEGORY_LIST_API, new JSONObject(), ShoutSearchActivity.this, ShoutSearchActivity.this, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                setData(objProfileSharedPreferences.getString(Constants.CATEGORY_JSON, ""));
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (objProfileSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY_ID, "").isEmpty() || objProfileSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY_ID, "").equals("0")) {
            objEditTextSearch.setText(objProfileSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, ""));
        }
    }

    private void initialize() {
        // BASE ACTIVITY LAYOUT HIDE SHOW
        showDefaultTopHeader();
        hideBottomTabs();

        setDrawerOpenCloseListener(this);

        BaseActivity.objImageNotificationCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent objIntent = new Intent(ShoutSearchActivity.this, NotificationListActivity.class);
                startActivity(objIntent);
            }
        });

        objEditTextSearch = (EditText) findViewById(R.id.edt_search_screen);
        objImageViewCancelSearch = (ImageView) findViewById(R.id.image_search_screen_cancel);
        objExpandableListView = (ExpandableListView) findViewById(R.id.listview_searched_data);
        objExpandableListView.setGroupIndicator(null);
        objExpandableListView.setChildIndicator(null);

        objEditTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Utils.hideshowKeyboard(ShoutSearchActivity.this, false, objEditTextSearch);
                    SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                    SharedPreferences.Editor objEditor = objSharedPreferences.edit();
                    objEditor.putString(Constants.USER_SEARCHED_CATEGORY, String.valueOf(objEditTextSearch.getText()));
                    objEditor.putString(Constants.USER_SEARCHED_CATEGORY_ID, "0");
                    objEditor.commit();

                    Intent objIntent = new Intent(ShoutSearchActivity.this, ShoutDefaultActivity.class);
                    objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(objIntent);
                    overridePendingTransition(0, 0);
                    finish();
                }
                return false;
            }
        });

        objExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                System.out.println("SEARCH ACTIVITY CHILD CLICKED : " + arrAllCategories.get(childPosition).getTitle());

                SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor objEditor = objSharedPreferences.edit();
                objEditor.putString(Constants.USER_SEARCHED_CATEGORY, String.valueOf(arrAllCategories.get(childPosition).getTitle()));
                objEditor.putString(Constants.USER_SEARCHED_CATEGORY_ID, String.valueOf(arrAllCategories.get(childPosition).getId()));
                objEditor.commit();

                Intent objIntent = new Intent(ShoutSearchActivity.this, ShoutDefaultActivity.class);
                objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(objIntent);
                overridePendingTransition(0, 0);
                finish();

                return false;
            }
        });

        setListener();

    }

    private void setListener() {
        objImageViewCancelSearch.setOnClickListener(this);

    }

    @Override
    public void onWebserviceResponce(String strUrl, String strResult) {
        if (strUrl.equals(Constants.CATEGORY_LIST_API)) {
            try {
                if (strResult.length() > 0) {
                    JSONObject objJsonObject = new JSONObject(strResult);
                    String result = objJsonObject.getString("result");
                    if (result.equals("true")) {
                        JSONArray objJsonArray = new JSONArray(objJsonObject.getString("categories"));

                        SharedPreferences.Editor objEditor = objProfileSharedPreferences.edit();
                        objEditor.putString(Constants.CATEGORY_JSON, objJsonArray.toString());
                        objEditor.commit();

                        setData(objJsonArray.toString());


                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setData(String s) {

        try {
            JSONArray objJsonArray = new JSONArray(s);
            for (int index = 0; index < objJsonArray.length(); index++) {
                SearchModel objSearchModel = new SearchModel(
                        objJsonArray.getJSONObject(index).getString("id"),
                        objJsonArray.getJSONObject(index).getString("title")
                );
                arrAllCategories.add(objSearchModel);
            }
            SearchContinent recentSearches = new SearchContinent("", arrRecentSearches);
            arrSearchContinents.add(recentSearches);
            SearchContinent allCategories = new SearchContinent("All Categories", arrAllCategories);
            arrSearchContinents.add(allCategories);
            searchAdapter = new SearchExpandableAdapter(arrSearchContinents, ShoutSearchActivity.this, ShoutSearchActivity.this);
            objExpandableListView.setAdapter(searchAdapter);
            objExpandableListView.expandGroup(0);
            objExpandableListView.expandGroup(1);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_search_screen_cancel:
                onBackPressed();
                break;
            default:
                // DO NOTHING
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent objIntent = new Intent(ShoutSearchActivity.this, ShoutDefaultActivity.class);
        objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(objIntent);
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    public void isDrawerOpen(boolean result) {
        System.out.println("SEARCH DRAWER RESULT : " + result);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showInternetView(isConnected);
    }

    private void showInternetView(boolean isConnected) {
        System.out.println("CONNECTIVITY LOGIN CHECK STATUS : " + isConnected);
        CustomSnackBarLayout obj = (CustomSnackBarLayout) findViewById(R.id.relative_internet_check);
        if (isConnected) {
            Constants.hideToBottom(obj);
        } else {
            obj.setVisibility(CustomSnackBarLayout.VISIBLE);
            Constants.show(obj);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        startWorkingNormal();
    }
}
