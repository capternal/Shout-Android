package com.shout.shoutin.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.PlusShare;
import com.shout.shoutin.CustomClasses.CustomSnackBarLayout;
import com.shout.shoutin.R;
import com.shout.shoutin.Utils.CallWebService;
import com.shout.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shout.shoutin.Utils.Constants;
import com.shout.shoutin.Utils.KeyboardUtils;
import com.shout.shoutin.Utils.NetworkUtils;
import com.shout.shoutin.Utils.NotificationBroadcastReceiver;
import com.shout.shoutin.Utils.Utils;
import com.shout.shoutin.app.AppController;
import com.shout.shoutin.base.BaseActivity;
import com.shout.shoutin.database.DatabaseHelper;
import com.shout.shoutin.main.Adapter.PopupComunityLayoutAdapter;
import com.shout.shoutin.main.Adapter.SearchTagLayoutAdapter;
import com.shout.shoutin.main.Adapter.SearchViewPagerAdapter;
import com.shout.shoutin.main.Adapter.ShoutDefaultListAdapter;
import com.shout.shoutin.main.Model.MyPreferencesModel;
import com.shout.shoutin.main.Model.SearchTagModel;
import com.shout.shoutin.main.Model.SearchViewPagerModel;
import com.shout.shoutin.main.Model.ShoutDefaultListModel;
import com.shout.shoutin.others.TouchableMapFragment;
import com.shout.shoutin.others.TouchableWrapper;
import com.shout.shoutin.service.ShoutBoardBackgroundService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by CapternalSystems on 7/5/2016.
 */
public class ShoutDefaultActivity extends BaseActivity implements View.OnClickListener, ConnectivityBroadcastReceiver.ConnectivityReceiverListener, BaseActivity.DrawerOpenCloseListener, OnMapReadyCallback, NotificationBroadcastReceiver.NotificationCountListener, CallWebService.WebserviceResponse {

    private static final int MAX_ROWS = 50;
    private static final int STATE_OFFSCREEN = 1;
    private static final int STATE_ONSCREEN = 0;
    private static final int STATE_RETURNING = 2;
    public static Boolean isListViewIdle;
    public static ShoutDefaultListAdapter objShoutDefaultListAdapter;
    public static int VIEW_PAGER_DEFAULT_POSITION = 1;
    public static ImageView btnFilter;
    //    public static EditText objEditTextSearch;
    public static RelativeLayout objRelativeLayoutSearchBox;
    public static boolean keyBoardOpen;
    public static int inSearchMode = 0;
    public SwipeRefreshLayout objShoutDefaultSwipableLayout;
    ArrayList<ShoutDefaultListModel> arrShoutDefaultListModel;
    ImageView btnSearch;
    ImageView btnSearchBoxCancelDone;
    ImageButton objImageButtonToggle;
    int intToggleFlag = 0;
    ImageView objImageViewCancelDone;
    RelativeLayout objRelativeLayoutHeader;
    SharedPreferences objSharedPreferences;
    RelativeLayout objRootLayout;
    int intOffset = 0;
    int intLimit = 10;
    RelativeLayout objRelativeToast;
    //    View objViewFilterApply;
    LinearLayout objLinearLayoutAskShoutType;
    RelativeLayout objRelativeLayoutNeedHelp;
    RelativeLayout objRelativeLayoutWantToHelp;

    // KEYBOARD VARIABLES
    ImageButton objImageButtonNeedHelp;
    TextView objTextViewNeedHelp;
    ImageButton objImageButtonWantToHelp;
    TextView objTextViewWantToHelp;
    static LinearLayout objLinearBottomLoad;
    DatabaseHelper objDatabaseHelper;
    boolean isLoading = true;
    Parcelable state;
    // BROADCAST RECEIVER FOR UPDATING SHOUTS FROM LIVE
    ShoutUpdateBroadcastReceiver objShoutUpdateBroadcastReceiver;
    IntentFilter shoutUpdateFilter;
    private TranslateAnimation anim;
    private int lastTopValue;
    private int mCachedVerticalScrollRange;
    private View mHeader;
    private int mMinRawY;
    private View mPlaceHolder;
    private int mQuickReturnHeight;
    private RelativeLayout mQuickReturnView;
    private int mScrollY;
    private int mState;
    private ListView objListViewShoutList;
    private ImageView imageViewNoDataFound;
    private TextView textViewNoDataFound;
    private LinearLayout linearLayoutNoDataFound;

    private GoogleApiClient client;
    // VIEW PAGER SEARCHING COMPONENTS
    private ViewPager searchViewPager;
    private RelativeLayout objRelativeLayoutViewPager;
    private SearchViewPagerAdapter searchViewPagerAdapter;
    private ArrayList<SearchViewPagerModel> arrayListSearchViewPagerModel = new ArrayList<SearchViewPagerModel>();
    private ShoutDefaultListModel objShoutDefaultListModel;


    // // TODO: 30/09/16 COMMUNITY WHEEL POPUP WINDOW
    private RelativeLayout relativeLayout;
    private PopupWindow popupWindowComunity;
    private PopupComunityLayoutAdapter popupComunityLayoutAdapter;
    private Activity activity;
    private ListView listViewComunityPopup;
    private View viewComunityPopup;
    public boolean isPopupOpen = false;
    private LinearLayout objLinearCommunityPopupRoot;
    public ArrayList<MyPreferencesModel> arrMyPreferencesModel = new ArrayList<MyPreferencesModel>();
    private String strPreferenceId = "1";


    // API ASYNCTASK OBJECTS
    LoadMoreShouts objLoadMoreShouts;
    LoadNearByShouts objLoadNearByShouts;
    StoreShoutDataForFirstTime objStoreShoutDataForFirstTime;
    GetLoggedInUserShout objGetLoggedInUserShout;

    public static ImageButton objCreateShout;

    // USED FOR NOT SHOWING DEFAULT API LOADER ON CENTER OF SCREEN WHEN USER LOADS MORE SHOUTS
    boolean isFirstTime = true;


    // MAP LAYOUT AND COMPONENTS
    private RelativeLayout objRelativeLayoutMapAddress;
    private TextView objTextViewMapAddress;
    private ImageView objImageViewOpenMap;
    private boolean isMapAddressVisible = false;

    // ACTUAL MAP LAYOUT
    private RelativeLayout objRelativeLayoutMap;
    private TouchableMapFragment mGoogleMap;
    private LinearLayout objLinearLayoutMapSearchbar;
    private TextView objTextViewDisplayMapAddress;
    private ImageView objImageViewMarker;
    private TextView objTextViewSelectAddress;
    private TextView objTextViewResetAddress;
    private static GoogleMap objGoogleMap;
    private LatLng latLong;
    private LatLng center;
    private Geocoder geocoder;
    public List<Address> addresses;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    // LISTVIEW SCROLL UP/DOWN
    int last = 0;
    boolean control = true;

    String strSearchedCategory = "";
    HorizontalScrollView objScrollViewSearchedTag;
    LinearLayout objLinearSearchedTagLayout;
    ArrayList<SearchTagModel> arrSearchTagModel = new ArrayList<SearchTagModel>();

    AppController objAppController;
    private static final int REQUEST_PERMISSIONS = 20;

    // SHOUT BOARD LIST API CALLING COUNT : TO MANAGE BLOG INTO LIST
    int intScrollCount = 0;

    String strLatitude = "";
    String strLongitude = "";
    String strZoom = "";
    String strAddress = "";

    private Intent objShoutBoardService;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shout_default);
        activity = ShoutDefaultActivity.this;
        objAppController = new AppController();

        objDatabaseHelper = new DatabaseHelper(ShoutDefaultActivity.this);
        objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!ShoutDefaultActivity.super.mayRequestLocationAccess()) {
                return;
            }
        } else {
            startWorkingNormally();
        }
    }

    public void startWorkingNormally() {

        try {
            shoutUpdateFilter = new IntentFilter(Constants.SHOUT_UPDATE_INTENT);
            objShoutUpdateBroadcastReceiver = new ShoutUpdateBroadcastReceiver();
            registerReceiver(objShoutUpdateBroadcastReceiver, shoutUpdateFilter);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateNotificationCount(getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_NOTIFICATION_COUNT, ""));
        updateMessageCount(getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_MESSAGE_COUNT, ""));

        objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor objEditor = objSharedPreferences.edit();
        objEditor.putString(Constants.IS_NEW_USER, "false");
        objEditor.commit();

        if (objSharedPreferences.getString(Constants.SHOUT_BOARD_LIST_API_SCROLL_COUNT, "").isEmpty() || objSharedPreferences.getString(Constants.SHOUT_BOARD_LIST_API_SCROLL_COUNT, "").equals("")) {
            setAPIScrollCount(0);
        }

        try {
            new CallWebService(Constants.BLOG_API, new JSONObject().put("user_id", objSharedPreferences.getString(Constants.USER_ID, "")), this, this, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("SEARCHED CATEGORY NAME : " + objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, ""));

        // FOR FIRST TIME WHEN USER GETS FRESH LOGIN TO THE APP
        if (objSharedPreferences.getString(Constants.IS_CURRENT_DATE, "").equals("")) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat(Constants.SHOUT_LIST_DATE_FORMAT);
            String currentDate = df.format(c.getTime());
            objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, STATE_ONSCREEN);
            SharedPreferences.Editor objDateEditor = objSharedPreferences.edit();
            objDateEditor.putString(Constants.IS_CURRENT_DATE, currentDate);
            objDateEditor.commit();
        }
        arrShoutDefaultListModel = new ArrayList<ShoutDefaultListModel>();

        // CALLES CONNETIVITY CHECK LISTENER
        objAppController.setConnectivityListener(this);
        objAppController.setNotificationListener(this);

        initialize();

        try {
            if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").length() > 0) {
                strPreferenceId = "1";
                intOffset = 0;
                arrShoutDefaultListModel.clear();
                objListViewShoutList.setAdapter(null);
                objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameShout);
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        showInternetView(Constants.internetCheck());

        try {
            initViewPagerComponents();
            String tag_json_obj = "json_obj_req";


            new CallWebService(Constants.MY_PREFRENCES_API, new JSONObject().put("user_id", objSharedPreferences.getString(Constants.USER_ID, "")), this, this, false).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }


        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                Log.d("keyboard", "keyboard visible: " + isVisible);
                if (isVisible) {
                    keyBoardOpen = true;
                } else {
                    keyBoardOpen = false;
                }
            }
        });
        if (objSharedPreferences.getString(Constants.PROFILE_BACK_SCREEN_NAME, "").equals(Constants.CALL_FROM_MY_SHOUTS)) {
            SharedPreferences.Editor objProfileEditor = objSharedPreferences.edit();
            objProfileEditor.putString(Constants.PROFILE_BACK_SCREEN_NAME, "");
            objProfileEditor.commit();
            new GetLoggedInUserShout().execute();
        } else {
            System.out.println("STORED DATE : " + objSharedPreferences.getString(Constants.IS_CURRENT_DATE, ""));
            System.out.println("CURRENT DATE : " + new SimpleDateFormat(Constants.SHOUT_LIST_DATE_FORMAT).format(Calendar.getInstance().getTime()));

            if (objSharedPreferences.getString(Constants.IS_CURRENT_DATE, "").equals(new SimpleDateFormat(Constants.SHOUT_LIST_DATE_FORMAT).format(Calendar.getInstance().getTime()))) {
                System.out.println("LOADING SHOUTS FOR CURRENT DATE : " + new SimpleDateFormat(Constants.SHOUT_LIST_DATE_FORMAT).format(Calendar.getInstance().getTime()));
                if (ConnectivityBroadcastReceiver.isConnected()) {
                    arrShoutDefaultListModel.clear();
                    if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").length() > 0) {
                        arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, true);
                    } else {
                        arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, false);
                    }
                    if (arrShoutDefaultListModel.size() == 0) {
                        new StoreShoutDataForFirstTime("0").execute();
                    } else {
                        objShoutDefaultListAdapter = new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this);
                        objListViewShoutList.setAdapter(objShoutDefaultListAdapter);
                    }
                } else {
                    arrShoutDefaultListModel.clear();
                    if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").length() > 0) {
                        arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, true);
                    } else {
                        arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, false);
                    }
                    System.out.println("OFFLINE DATA : " + arrShoutDefaultListModel);
                    if (arrShoutDefaultListModel.size() > 0) {
                        objShoutDefaultListAdapter = new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this);
                        objListViewShoutList.setAdapter(objShoutDefaultListAdapter);
                    }
                }
            } else {
                objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, STATE_ONSCREEN);
                SharedPreferences.Editor objDateEditor = objSharedPreferences.edit();
                objDateEditor.putString(Constants.IS_CURRENT_DATE, new SimpleDateFormat(Constants.SHOUT_LIST_DATE_FORMAT).format(Calendar.getInstance().getTime()));
                objDateEditor.commit();

                arrShoutDefaultListModel.clear();
                if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").length() > 0) {
                    arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, true);
                } else {
                    arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, false);
                }
                if (arrShoutDefaultListModel.size() > 0) {
                    objShoutDefaultListAdapter = new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this);
                    objListViewShoutList.setAdapter(objShoutDefaultListAdapter);
                } else {
                    new StoreShoutDataForFirstTime("0").execute();
                }
            }
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // CALLES CONNETIVITY CHECK LISTENER
        objAppController.setConnectivityListener(this);
        objAppController.setNotificationListener(this);
//        showInternetView(Constants.internetCheck());
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


    public static void openCloseSearchBar(boolean status, Context context) {
        if (status == true) {
            inSearchMode = 1;
            btnFilter.setVisibility(Button.INVISIBLE);
            btnFilter.setEnabled(false);
            objRelativeLayoutSearchBox.setVisibility(RelativeLayout.VISIBLE);
            Animation openAnimation = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
            objRelativeLayoutSearchBox.setAnimation(openAnimation);
            objRelativeLayoutSearchBox.animate();
            objCreateShout.setVisibility(ImageButton.GONE);
            objLinearBottomLoad.setVisibility(LinearLayout.GONE);
        } else {
            inSearchMode = 0;
            Animation closeAnimation = AnimationUtils.loadAnimation(context, R.anim.push_right_out);
            objRelativeLayoutSearchBox.setAnimation(closeAnimation);
            objRelativeLayoutSearchBox.animate();
            objRelativeLayoutSearchBox.setVisibility(RelativeLayout.GONE);
            btnFilter.setVisibility(Button.VISIBLE);
            btnFilter.setEnabled(true);
            objCreateShout.setVisibility(ImageButton.VISIBLE);
            objLinearBottomLoad.setVisibility(LinearLayout.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // CALLES CONNETIVITY CHECK LISTENER
        objAppController.setConnectivityListener(this);
        objAppController.setNotificationListener(this);
//        showInternetView(Constants.internetCheck());


        updateMessageCount(getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_MESSAGE_COUNT, ""));

        try {
//            AppController.getInstance().setConnectivityListener(this);
            // BROADCAST RECEIVER FOR UPDATING LOCAL SHOUTS
            objShoutUpdateBroadcastReceiver = new ShoutUpdateBroadcastReceiver();
            registerReceiver(objShoutUpdateBroadcastReceiver, shoutUpdateFilter);

            Calendar calendar = Calendar.getInstance();
            objShoutBoardService = new Intent(ShoutDefaultActivity.this, ShoutBoardBackgroundService.class);
            pendingIntent = PendingIntent.getService(this, 0, objShoutBoardService, 0);
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 60 * 500, pendingIntent);

//            startService(objShoutBoardService);

            try {
                init();
            } catch (ClassCastException ce) {
                ce.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            objLinearLayoutAskShoutType.setVisibility(LinearLayout.GONE);
            objDatabaseHelper = new DatabaseHelper(ShoutDefaultActivity.this);
            arrShoutDefaultListModel.clear();
            if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").length() > 0) {
                arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, true);
            } else {
                arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, false);
            }
            objListViewShoutList.setAdapter(new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this));
            if (state != null) {
                objListViewShoutList.onRestoreInstanceState(state);
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        try {
            setAPIScrollCount(0);
            state = objListViewShoutList.onSaveInstanceState();
            Utils.d("SERVICE :", "STOPPED");
            stopService(objShoutBoardService);
            unregisterReceiver(objShoutUpdateBroadcastReceiver);
            alarmManager.cancel(pendingIntent);

        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    private void initViewPagerComponents() {
        objRelativeLayoutViewPager = (RelativeLayout) findViewById(R.id.relative_layout_view_pager);
        searchViewPager = (ViewPager) findViewById(R.id.searchViewPager);
        searchViewPager.setOnClickListener(this);
        final GestureDetector objGestureDetector = new GestureDetector(this, new TapGestureListener());
        searchViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                objGestureDetector.onTouchEvent(event);
                return false;
            }
        });
        System.out.println("SIZE OF MY PREFERENCES MODEL ARRAY : " + arrMyPreferencesModel.size());

        arrMyPreferencesModel.clear();
        if (objSharedPreferences.getString(Constants.SHOUT_PREFERENCES, "").length() > 0) {
            try {
                JSONArray objJsonArray = new JSONArray(objSharedPreferences.getString(Constants.SHOUT_PREFERENCES, ""));
                for (int index = 0; index < objJsonArray.length(); index++) {
                    if (objJsonArray.getJSONObject(index).getString("status").equals("A")) {
                        MyPreferencesModel objMyPreferencesModel = new MyPreferencesModel(
                                objJsonArray.getJSONObject(index).getString("id"),
                                objJsonArray.getJSONObject(index).getString("preference_id"),
                                objJsonArray.getJSONObject(index).getString("title"),
                                objJsonArray.getJSONObject(index).getString("status"),
                                true,
                                objJsonArray.getJSONObject(index).getString("is_checked"),
                                Constants.HTTP_URL + objJsonArray.getJSONObject(index).getString("image"),
                                objJsonArray.getJSONObject(index).getString("message"));
                        arrMyPreferencesModel.add(objMyPreferencesModel);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        searchViewPagerAdapter = new SearchViewPagerAdapter(ShoutDefaultActivity.this, arrMyPreferencesModel);
        searchViewPager.setAdapter(searchViewPagerAdapter);

        adjustViewPagerView();

        searchViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                System.out.println("PAGE CHANGED");
            }

            @Override
            public void onPageSelected(final int position) {


                Handler objHandler = new Handler();
                objHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            showNoDataFoundUI(false);

                            // SET API SCROLL COUNT TO 0 (ZERO)
                            setAPIScrollCount(0);

                            // SET IS LOADING TO TRUE FOR GETTING LAST ITEM DETECT
                            isLoading = true;
                            // USED FOR DISPLAYING DEFAULT API LOADER ON CENTER OF SCREEN. LOADER IS VISIBLE WHEN IT IS TRUE.
                            isFirstTime = true;
                            // SET MAP ADDRESS LAYOUT CLOSE/OPEN VALUE
                            isMapAddressVisible = false;
                            openMapAddressLayout(false);

                            MyPreferencesModel objMyPreferencesModel = arrMyPreferencesModel.get(position);
                            strPreferenceId = objMyPreferencesModel.getPreference_id();
                            objSharedPreferences.edit().putString(Constants.SHOUT_CATEGORY_PREFERENCE_ID, strPreferenceId).commit();
                            arrShoutDefaultListModel.clear();
                            objListViewShoutList.setAdapter(null);
                            objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameShout);
                            intOffset = 0;
                            System.out.println("API SELECTED PREFERENCE ID : " + strPreferenceId);
                            if (strPreferenceId.equals("4")) {

                                objLoadNearByShouts = new LoadNearByShouts();
                                objLoadNearByShouts.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                objStoreShoutDataForFirstTime = new StoreShoutDataForFirstTime("0");
                                objStoreShoutDataForFirstTime.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                            // TODO: 26/11/16 removed below else if condition while testing for new server after adding more preferences.
                            /*else if (strPreferenceId.equals("1") || strPreferenceId.equals("2") || strPreferenceId.equals("3")) {
                                objStoreShoutDataForFirstTime = new StoreShoutDataForFirstTime("0");
                                objStoreShoutDataForFirstTime.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }*/

                            /*MyPreferencesModel objMyPreferencesModel = arrMyPreferencesModel.get(position);
                            strPreferenceId = objMyPreferencesModel.getPreference_id();
                            System.out.println("SELECTED PREFERENCE ID : " + strPreferenceId);
                            arrShoutDefaultListModel.clear();
                            objShoutDefaultListAdapter.notifyDataSetChanged();
                            objListViewShoutList.setAdapter(null);
                            objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameShout);
                            intOffset = 0;
                            if (strPreferenceId.equals("1")) {
                                objLoadNearByShouts = new LoadNearByShouts();
                                objLoadNearByShouts.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else if (strPreferenceId.equals("2")) {

                            } else if (strPreferenceId.equals("0")) {
                                objStoreShoutDataForFirstTime = new StoreShoutDataForFirstTime("0");
                                objStoreShoutDataForFirstTime.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }*/
                        } catch (NullPointerException ne) {
                            ne.printStackTrace();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 500);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void adjustViewPagerView() {
        // TO SET THE HEIGHT WITH RESPECT TO DEVICE RESOLUTION.
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        int density = dm.densityDpi;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        System.out.println("DENSITY WIDTH : " + width);

        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
                System.out.println("DENSITY LOW");
                height = 60;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                System.out.println("DENSITY MEDIUM");
                height = 75;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                System.out.println("DENSITY HIGH");
                height = 110;
                // SCREEN WIDTH : 540
                searchViewPager.setPageMargin(-100);
                searchViewPager.setClipToPadding(false);
                searchViewPager.setPadding(75, 0, 75, 0);
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                System.out.println("DENSITY XHIGH");
                height = 150;
                // SCREEN WIDTH : 720
                searchViewPager.setPageMargin(-150);
                searchViewPager.setClipToPadding(false);
                searchViewPager.setPadding(95, 0, 95, 0);
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                System.out.println("DENSITY XXHIGH");
                height = 250;
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                System.out.println("DENSITY XXXHIGH");
                height = 370;
                break;
            default:
                System.out.println("DENSITY DEFAULT");
                height = 320;
                searchViewPager.setPageMargin(-150);
                searchViewPager.setClipToPadding(false);
                searchViewPager.setPadding(95, 0, 95, 0);
                break;
        }

    }

    private int convertDpToPx(int i) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, getResources().getDisplayMetrics());
    }

    private void initialize() {

        // NO DATA LAYOUT COMPONENTS
        linearLayoutNoDataFound = (LinearLayout) findViewById(R.id.linearLayoutNoData);
        imageViewNoDataFound = (ImageView) findViewById(R.id.imgeViewNoData);
        textViewNoDataFound = (TextView) findViewById(R.id.textViewNoData);

        // TOP SEARCHE BAR TAG LAYOUT COMPONENTS
        objScrollViewSearchedTag = (HorizontalScrollView) findViewById(R.id.horizontal_scrollview_searched_tags);
        objLinearSearchedTagLayout = (LinearLayout) findViewById(R.id.linear_searched_tag_layout);


        // MAP ADDRESS LAYOUT COMPONENTS
        objRelativeLayoutMapAddress = (RelativeLayout) findViewById(R.id.relative_map_area);
        objTextViewMapAddress = (TextView) findViewById(R.id.txt_map_address);
        objImageViewOpenMap = (ImageView) findViewById(R.id.img_edit_address);

        objImageViewOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "").isEmpty() || objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "").equals("")) {
                    strLatitude = objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, "");
                    strLongitude = objSharedPreferences.getString(Constants.USER_REGISTERED_LONGITUDE, "");
                    strZoom = objSharedPreferences.getString(Constants.USER_REGISTERED_LOCATION_ZOOM, "");
                    strAddress = objTextViewMapAddress.getText().toString();
                } else {
                    strLatitude = objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "");
                    strLongitude = objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, "");
                    strZoom = objSharedPreferences.getString(Constants.USER_SEARCHED_LOCATION_ZOOM, "");
                    strAddress = objTextViewMapAddress.getText().toString();
                }

                System.out.println("ADDRESS BEFORE : " + strAddress);

                setMapVisibility(true);
            }
        });

        // ACTUAL MAP LAYOUT
        objRelativeLayoutMap = (RelativeLayout) findViewById(R.id.relative_shout_screen_map);
        mGoogleMap = (TouchableMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_shout_screen);
        objLinearLayoutMapSearchbar = (LinearLayout) findViewById(R.id.linear_top_map_search_bar_shout_screen);
        objTextViewDisplayMapAddress = (TextView) findViewById(R.id.txt_map_address_shout_screen);
        objImageViewMarker = (ImageView) findViewById(R.id.map_shout_screen_marker);
        objTextViewSelectAddress = (TextView) findViewById(R.id.map_loading_done_shout_screen);
        objTextViewResetAddress = (TextView) findViewById(R.id.map_reset_shout_screen);

        objTextViewResetAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapAddressLayout(false);
                setMapVisibility(false);
                resetMapToHomeLocation();
                arrShoutDefaultListModel.clear();
                objListViewShoutList.setAdapter(null);
                objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameShout);
                intOffset = 0;
                if (strPreferenceId.equals("4")) {
                    objLoadNearByShouts = new LoadNearByShouts();
                    objLoadNearByShouts.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else if (strPreferenceId.equals("1") || strPreferenceId.equals("2") || strPreferenceId.equals("3")) {
                    objStoreShoutDataForFirstTime = new StoreShoutDataForFirstTime("0");
                    objStoreShoutDataForFirstTime.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        objTextViewSelectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapAddressLayout(false);
                setMapVisibility(false);
                arrShoutDefaultListModel.clear();
                objListViewShoutList.setAdapter(null);
                objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameShout);
                intOffset = 0;
                if (strPreferenceId.equals("4")) {
                    objLoadNearByShouts = new LoadNearByShouts();
                    objLoadNearByShouts.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else if (strPreferenceId.equals("1") || strPreferenceId.equals("2") || strPreferenceId.equals("3")) {
                    objStoreShoutDataForFirstTime = new StoreShoutDataForFirstTime("0");
                    objStoreShoutDataForFirstTime.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        objTextViewDisplayMapAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(ShoutDefaultActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        setMap();

        System.out.println("SEARCH LAT LONG VALUE : " + objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, ""));

        if (objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "").isEmpty() && objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, "").isEmpty()) {
            new GetLocationAsync(Double.valueOf(objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, "")), Double.valueOf(objSharedPreferences.getString(Constants.USER_REGISTERED_LONGITUDE, ""))).execute();
        } else {
            new GetLocationAsync(Double.valueOf(objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "")), Double.valueOf(objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, ""))).execute();
        }


        setDrawerOpenCloseListener(this);
        objCreateShout = (ImageButton) findViewById(R.id.imagebutton_create_shout);
        objLinearBottomLoad = (LinearLayout) findViewById(R.id.linear_list_down_swipe_loading);
        objLinearLayoutAskShoutType = (LinearLayout) findViewById(R.id.linear_ask_shout_type);
        objRelativeLayoutNeedHelp = (RelativeLayout) findViewById(R.id.relative_need_help);
        objRelativeLayoutWantToHelp = (RelativeLayout) findViewById(R.id.relative_want_to_help);
        objImageButtonNeedHelp = (ImageButton) findViewById(R.id.image_button_need_help);
        objTextViewNeedHelp = (TextView) findViewById(R.id.txt_need_help);
        objImageButtonWantToHelp = (ImageButton) findViewById(R.id.image_button_want_to_help);
        objTextViewWantToHelp = (TextView) findViewById(R.id.txt_want_to_help);
//        objViewFilterApply = (View) findViewById(R.id.filter_applyied_view);
        objRelativeToast = (RelativeLayout) findViewById(R.id.relative_toast);
        objShoutDefaultSwipableLayout = (SwipeRefreshLayout) findViewById(R.id.shout_default_swipe_layout);
        objShoutDefaultSwipableLayout.setEnabled(false);
        objRelativeLayoutHeader = (RelativeLayout) findViewById(R.id.relative_grey_background_menu);
        btnSearch = (ImageView) findViewById(R.id.btn_shout_default_search);
        btnFilter = (ImageView) findViewById(R.id.btn_shout_default_filter);
//        objEditTextSearch = (EditText) findViewById(R.id.edt_search_shout_default_header);
        btnSearchBoxCancelDone = (ImageView) findViewById(R.id.btn_cancel_done_shout_default_header);
        objRelativeLayoutSearchBox = (RelativeLayout) findViewById(R.id.relative_search_box_shout_default_header);
        showDefaultTopHeader();
        hideBottomTabs();

        /*BaseActivity.objSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {

                BaseActivity.objDrawerOpen.setVisibility(ImageView.GONE);
                BaseActivity.objDrawerClose.setVisibility(ImageView.VISIBLE);
                objCreateShout.setVisibility(ImageButton.GONE);
            }
        });
        BaseActivity.objSlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                System.out.println("SHOUT DRAWER CLOSE");
                BaseActivity.objDrawerOpen.setVisibility(ImageView.GONE);
                BaseActivity.objDrawerClose.setVisibility(ImageView.GONE);
                objCreateShout.setVisibility(ImageButton.VISIBLE);
            }
        });*/

        BaseActivity.objImageNotificationCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent objIntent = new Intent(ShoutDefaultActivity.this, NotificationListActivity.class);
                startActivity(objIntent);
            }
        });

        objCreateShout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
                final Animation rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

                if (objLinearLayoutAskShoutType.getVisibility() == LinearLayout.GONE) {
                    objLinearLayoutAskShoutType.setVisibility(LinearLayout.VISIBLE);
                    objShoutDefaultSwipableLayout.setEnabled(false);
                    // HIDING + BUTTON
                    objCreateShout.startAnimation(rotate_forward);
                    Animation ComeFromLeftAnim = AnimationUtils.loadAnimation(ShoutDefaultActivity.this, R.anim.come_from_right);
                    objRelativeLayoutNeedHelp.startAnimation(ComeFromLeftAnim);
                    Animation ComeFromRightAnim = AnimationUtils.loadAnimation(ShoutDefaultActivity.this, R.anim.come_from_left);
                    objRelativeLayoutWantToHelp.startAnimation(ComeFromRightAnim);

                    objRelativeLayoutNeedHelp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //DO NOTHING
                            System.out.println("DO NOTHING");
                            objCreateShout.startAnimation(rotate_backward);
                            pushToCreateShoutScreen("N", true);
                        }
                    });

                    objRelativeLayoutWantToHelp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //DO NOTHING
                            System.out.println("DO NOTHING");
                            objCreateShout.startAnimation(rotate_backward);
                            pushToCreateShoutScreen("W", false);
                        }
                    });
                    objImageButtonNeedHelp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("NEED HELP CLICKED");
                            objCreateShout.startAnimation(rotate_backward);
                            pushToCreateShoutScreen("N", true);
                        }
                    });

                    objImageButtonWantToHelp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("WANT TO GIVE CLICKED");
                            objCreateShout.startAnimation(rotate_backward);
                            pushToCreateShoutScreen("W", false);
                        }
                    });
                } else {
                    objCreateShout.startAnimation(rotate_backward);
                    Animation outAnimationForNeedHelp = AnimationUtils.loadAnimation(ShoutDefaultActivity.this, R.anim.push_left_out);
                    objRelativeLayoutNeedHelp.startAnimation(outAnimationForNeedHelp);
                    Animation outAnimationForWantToHelp = AnimationUtils.loadAnimation(ShoutDefaultActivity.this, R.anim.push_right_out);
                    objRelativeLayoutWantToHelp.startAnimation(outAnimationForWantToHelp);

                    outAnimationForNeedHelp.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            objLinearLayoutAskShoutType.setVisibility(LinearLayout.GONE);
                            objCreateShout.setVisibility(ImageButton.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            }
        });
        objListViewShoutList = (ListView) findViewById(R.id.shout_default_listview);
        LayoutInflater objLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dummy_header = objLayoutInflater.inflate(R.layout.dummy_shout_default_list_header, objListViewShoutList, false);
        objListViewShoutList.addHeaderView(dummy_header);
        mQuickReturnView = (RelativeLayout) findViewById(R.id.relative_grey_background_menu);


        try {
            if (!objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").isEmpty()) {
                setSearchedValue(String.valueOf(objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "")), String.valueOf(objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY_ID, "")));
                /*objEditTextSearch.setText(objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, ""));
                objEditTextSearch.setFocusable(false);*/
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setListener();

        /*objListViewShoutList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (objRelativeLayoutSearchBox.getVisibility() == RelativeLayout.VISIBLE) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                return false;
            }
        });*/

      /*  objListViewShoutList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                switch (action) {
                    case (MotionEvent.ACTION_DOWN):
                        System.out.println("DIRECTION DOWN");
                        return true;
                    case (MotionEvent.ACTION_UP):
                        System.out.println("DIRECTION UP");
                        return true;
                    default:
                        break;
                }
                return false; // has to be false, or it will freeze the listView
            }
        });*/

       /* objListViewShoutList.setOnScrollListener(new OnScrollObserver() {
            @Override
            public void onScrollUp() {
                System.out.println("SCROLL UP");
                showViews();
            }

            @Override
            public void onScrollDown() {
                System.out.println("SCROLL DOWN");
                hideViews();
            }
        });*/

        objListViewShoutList.setOnScrollListener(new AbsListView.OnScrollListener() {
                                                     @Override
                                                     public void onScrollStateChanged(AbsListView view, int scrollState) {

                                                     }

                                                     @Override
                                                     public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                                                         if (firstVisibleItem < last && !control) {
                                                             showViews();
                                                             control = true;
                                                         } else if (firstVisibleItem > last && control) {
                                                             hideViews();
                                                             control = false;
                                                         }
                                                         last = firstVisibleItem;

                                                         if (objListViewShoutList.getChildCount() > 0) {
                                                             boolean topOfFirstItemVisible = objListViewShoutList.getChildAt(0).getTop() == 0;
                                                             if (firstVisibleItem == 0 && topOfFirstItemVisible) {
                                                                 objShoutDefaultSwipableLayout.setEnabled(true);
                                                             } else {
                                                                 objShoutDefaultSwipableLayout.setEnabled(false);
                                                             }
                                                         }
                                                         try {
                                                             if (objListViewShoutList.getAdapter() == null)
                                                                 return;

                                                             if (objListViewShoutList.getAdapter().getCount() == 0)
                                                                 return;

                                                             int l = visibleItemCount + firstVisibleItem;
                                                             if (arrShoutDefaultListModel.size() > 0) {

                                                                 System.out.println("ISLOADING STATUS : " + isLoading);

                                                                 if (l >= totalItemCount && isLoading) {
                                                                     isLoading = false;
                                                                     System.out.println("LAST ITEM CELL DETECTED");

                                                                     if (inSearchMode == 0) {
                                                                         int offset = objDatabaseHelper.getNearByFriendsShoutCount();
                                                                         System.out.println("SHOUT NEAR BY QUERY RESULT : " + offset);
                                                                         if (offset == 0) {
                                                                             System.out.println("AA : LOAD MORE SHOUTS");
                                                                             Animation fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
                                                                             objCreateShout.startAnimation(fab_close);
                                                                             objLinearBottomLoad.setVisibility(LinearLayout.VISIBLE);
                                                                             Constants.show(objLinearBottomLoad);
                                                                             new LoadMoreShouts().execute();
                                                                         } else {
                                                                             // IF CATEGORY IS EMPTY THEN AND TEHN ONLY CALL NEARBYAPI ELSE DO NOTHING
                                                                             System.out.println("CATEGORY FREZ ON SCROLL END : " + objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, ""));
                                                                             if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").isEmpty() || objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").equals("")) {
                                                                                 if (!strPreferenceId.equals("2")) {
                                                                                     Animation fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
                                                                                     objCreateShout.startAnimation(fab_close);
                                                                                     objLinearBottomLoad.setVisibility(LinearLayout.VISIBLE);
                                                                                     Constants.show(objLinearBottomLoad);
                                                                                     System.out.println("AA : LOAD NEAR BY SHOUTS");
                                                                                     new LoadNearByShouts().execute();
                                                                                 }
                                                                             }
                                                                         }
                                                                     } else {
                                                                         isLoading = true;
                                                                         Animation fab_close_done = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
                                                                         objCreateShout.startAnimation(fab_close_done);
                                                                         objLinearBottomLoad.setVisibility(LinearLayout.VISIBLE);
                                                                         Constants.show(objLinearBottomLoad);
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

        );
        objShoutDefaultSwipableLayout.setColorSchemeResources(R.color.red_background_color);
        objShoutDefaultSwipableLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                               @Override
                                                               public void onRefresh() {

                                                                   if (strPreferenceId.equals("1")) {
                                                                       objDatabaseHelper.deleteUnFriendShouts();
                                                                   } else {
                                                                       objDatabaseHelper.deleteShoutEntries();
                                                                   }
                                                                   isLoading = true;
                                                                   state = objListViewShoutList.onSaveInstanceState();
                                                                   arrShoutDefaultListModel.clear();
                                                                   if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").length() > 0) {
                                                                       objDatabaseHelper.deleteShoutEntries();
                                                                       arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, true);
                                                                   } else {
                                                                       arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, false);
                                                                   }
                                                                   System.out.println("ON REFRESH MODEL ARRAY SIZE : " + arrShoutDefaultListModel.size());
                                                                   objShoutDefaultListAdapter = new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this);
                                                                   objListViewShoutList.setAdapter(objShoutDefaultListAdapter);
                                                                   objShoutDefaultListAdapter.notifyDataSetChanged();
                                                                   objListViewShoutList.onRestoreInstanceState(state);
                                                                   setAPIScrollCount(0);
                                                                   if (strPreferenceId.equals("4")) {
                                                                       new LoadNearByShouts().execute();
                                                                   } else {
                                                                       new StoreShoutDataForFirstTime("1").execute();
                                                                   }
                                                                   objShoutDefaultSwipableLayout.setRefreshing(true);
                                                               }
                                                           }
        );
    }

    private void setSearchedValue(String strSentence, String strCategoryId) {
        objRelativeLayoutSearchBox.setVisibility(RelativeLayout.VISIBLE);
        btnFilter.setVisibility(ImageView.INVISIBLE);
        arrSearchTagModel.clear();
        if (Integer.parseInt(strCategoryId) == 0) {

            String[] searchedTextSpliter = strSentence.split(" ");
            for (int index = 0; index < searchedTextSpliter.length; index++) {
                SearchTagModel objSearchTagModel = new SearchTagModel(index, searchedTextSpliter[index]);
                arrSearchTagModel.add(objSearchTagModel);
            }
            SearchTagLayoutAdapter objSearchTagLayoutAdapter = new SearchTagLayoutAdapter(ShoutDefaultActivity.this, ShoutDefaultActivity.this, arrSearchTagModel);
            for (int index = 0; index < searchedTextSpliter.length; index++) {
                objLinearSearchedTagLayout.addView(objSearchTagLayoutAdapter.getView(index, null, objScrollViewSearchedTag));
            }
        } else if (Integer.parseInt(strCategoryId) > 0) {
            TextView objTextView = new TextView(ShoutDefaultActivity.this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            objTextView.setText("Showing results for : " + strSentence);
            params.setMargins(0, 0, 5, 0);
            objTextView.setLayoutParams(params);
            objTextView.setGravity(Gravity.CENTER);
            objTextView.setSingleLine(true);
            objTextView.setTextSize(12);
            objTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent objIntent = new Intent(ShoutDefaultActivity.this, ShoutSearchActivity.class);
                    objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(objIntent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
            objLinearSearchedTagLayout.addView(objTextView);
        }
        objScrollViewSearchedTag.addView(objLinearSearchedTagLayout);
    }

    public void updateSearchUI() {
        SearchTagLayoutAdapter objSearchTagLayoutAdapter = new SearchTagLayoutAdapter(ShoutDefaultActivity.this, ShoutDefaultActivity.this, arrSearchTagModel);
        objLinearSearchedTagLayout.removeAllViews();
        objScrollViewSearchedTag.removeAllViews();
        StringBuilder objBuilder = new StringBuilder();
        for (int index = 0; index < arrSearchTagModel.size(); index++) {
            objLinearSearchedTagLayout.addView(objSearchTagLayoutAdapter.getView(index, null, objScrollViewSearchedTag));
            objBuilder.append(String.valueOf(arrSearchTagModel.get(index).getSearchedTagWord()));
        }
        SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);

        if (arrSearchTagModel.size() == 0) {
            objBuilder.append("");
            isLoading = true;
        }

        if (String.valueOf(objBuilder).isEmpty() || String.valueOf(objBuilder).equals("")) {
            SharedPreferences.Editor objEditor = objSharedPreferences.edit();
            objEditor.putString(Constants.USER_SEARCHED_CATEGORY, "");
            objEditor.putString(Constants.USER_SEARCHED_CATEGORY_ID, "0");
            objEditor.commit();
        } else {
            SharedPreferences.Editor objEditor = objSharedPreferences.edit();
            objEditor.putString(Constants.USER_SEARCHED_CATEGORY, String.valueOf(objBuilder));
            objEditor.putString(Constants.USER_SEARCHED_CATEGORY_ID, "0");
            objEditor.commit();
        }
        System.out.println("SEARCHED TEXT UPDATED : " + objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, ""));

        objScrollViewSearchedTag.addView(objLinearSearchedTagLayout);
        if (arrSearchTagModel.isEmpty()) {
            objRelativeLayoutSearchBox.setVisibility(RelativeLayout.GONE);
            btnFilter.setVisibility(ImageView.VISIBLE);
        }

        strPreferenceId = "1";
        arrShoutDefaultListModel.clear();
        objListViewShoutList.setAdapter(null);
        objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameShout);
        intOffset = 0;
        objStoreShoutDataForFirstTime = new StoreShoutDataForFirstTime("0");
        objStoreShoutDataForFirstTime.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void resetMapToHomeLocation() {
        SharedPreferences.Editor objPreferenceEditor = objSharedPreferences.edit();
        objPreferenceEditor.putString(Constants.USER_SEARCHED_LATITUDE, objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, ""));
        objPreferenceEditor.putString(Constants.USER_SEARCHED_LONGITUDE, objSharedPreferences.getString(Constants.USER_REGISTERED_LONGITUDE, ""));
        /*objPreferenceEditor.putString(Constants.USER_SEARCHED_LATITUDE, "");
        objPreferenceEditor.putString(Constants.USER_SEARCHED_LONGITUDE, "");*/
        objPreferenceEditor.commit();

        try {
            addresses = null;
            geocoder = new Geocoder(ShoutDefaultActivity.this, Locale.ENGLISH);

            if (objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "").equals("") || objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "").isEmpty()) {
                addresses = geocoder.getFromLocation(Double.valueOf(objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, "")), Double.valueOf(objSharedPreferences.getString(Constants.USER_REGISTERED_LONGITUDE, "")), 1);
            } else {
                addresses = geocoder.getFromLocation(Double.valueOf(objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "")), Double.valueOf(objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, "")), 1);
            }
            StringBuilder str = new StringBuilder();
            if (geocoder.isPresent()) {
                if (addresses.size() > 0) {
                    String localityString = addresses.get(0).getLocality();
                    String city = addresses.get(0).getCountryName();
                    String region_code = addresses.get(0).getCountryCode();
                    String zipcode = addresses.get(0).getPostalCode();

                    str.append(localityString + "");
                    str.append(city + "" + region_code + "");
                    str.append(zipcode + "");
                    objTextViewDisplayMapAddress.setText(String.valueOf(str));
                }
            } else {
                ShoutDefaultActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        objTextViewDisplayMapAddress.setText("No Address found");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("USER PRINT LATITUDE : " + objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, ""));
            System.out.println("USER PRINT LONGITUDE : " + objSharedPreferences.getString(Constants.USER_REGISTERED_LONGITUDE, ""));
            latLong = new LatLng(Double.parseDouble(objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "")),
                    Double.parseDouble(objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, "")));
            final float zoomlevel = Float.parseFloat(objSharedPreferences.getString(Constants.USER_SEARCHED_LOCATION_ZOOM, ""));
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    objGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, zoomlevel));
                }
            });
        } catch (NumberFormatException numberEx) {
            numberEx.printStackTrace();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMapVisibility(boolean mapVisibility) {
        if (mapVisibility) {
            objRelativeLayoutMap.setVisibility(RelativeLayout.VISIBLE);
        } else {
            objRelativeLayoutMap.setVisibility(RelativeLayout.GONE);
        }
        objTextViewMapAddress.setText(String.valueOf(objTextViewDisplayMapAddress.getText()));
    }

    private void setMyLoactionButtonPosition() {
        Fragment fragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_shout_screen));
        ViewGroup v1 = (ViewGroup) fragment.getView();
        ViewGroup v2 = (ViewGroup) v1.getChildAt(0);
        ViewGroup v3 = (ViewGroup) v2.getChildAt(2);
        View position = (View) v3.getChildAt(0);
        int positionWidth = position.getLayoutParams().width;
        int positionHeight = position.getLayoutParams().height;

        //lay out position button
        RelativeLayout.LayoutParams positionParams = new RelativeLayout.LayoutParams(positionWidth, positionHeight);
        /*int margin = positionWidth / 5;
        positionParams.setMargins(margin, 0, 0, margin);*/
        positionParams.setMargins(0, 0, 10, 10);
        positionParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        positionParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        position.setLayoutParams(positionParams);
    }

    private void setMap() {
        try {
            mGoogleMap = (TouchableMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_shout_screen);
            mGoogleMap.getMapAsync(this);
            setMyLoactionButtonPosition();
            mGoogleMap.setTouchListener(new TouchableWrapper.OnTouchListener() {
                ImageView objMarker = (ImageView) findViewById(R.id.map_shout_screen_marker);

                @Override
                public void onTouch() {
                    System.out.println("MAP TOUCH ACTIVATED");
                    objLinearLayoutMapSearchbar.setVisibility(LinearLayout.GONE);
                    Constants.hideToTop(objLinearLayoutMapSearchbar);
                    objTextViewSelectAddress.setVisibility(TextView.GONE);
                    Constants.hideToBottom(objTextViewSelectAddress);
                    objTextViewResetAddress.setVisibility(TextView.GONE);
                    Constants.hideToBottom(objTextViewResetAddress);
                }

                @Override
                public void onRelease() {
                    System.out.println("MAP TOUCH DE-ACTIVATED");
                    objLinearLayoutMapSearchbar.setVisibility(LinearLayout.VISIBLE);
                    Constants.show(objLinearLayoutMapSearchbar);
                    objTextViewMapAddress.setVisibility(TextView.VISIBLE);
                    Constants.show(objTextViewSelectAddress);
                    objTextViewResetAddress.setVisibility(TextView.VISIBLE);
                    Constants.show(objTextViewResetAddress);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pushToCreateShoutScreen(final String identifier, boolean isRequestShown) {
        Intent objIntent = (new Intent(ShoutDefaultActivity.this, CreateShoutActivity.class));
        objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        objIntent.putExtra("IS_REQUEST", identifier);
        objIntent.putExtra("IS_REQUEST_SHOWN", isRequestShown);
        startActivity(objIntent);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }

    private void setListener() {
        btnSearch.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        btnSearchBoxCancelDone.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showInternetView(isConnected);
    }

    public void onBackPressed() {
        System.out.println("IN BACK PRESS OF SHOUT DEFAULT ACTIVITY");
       /* try {
            if (objPopupWindowLargeSource.isShowing()) {
                System.out.println("IN BACK PRESS OF SHOUT DEFAULT ACTIVITY");
                objPopupWindowLargeSource.dismiss();
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            if (objRelativeLayoutMap.getVisibility() == RelativeLayout.VISIBLE) {
                if (!objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "").equals(strLatitude) && !objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, "").equals(strLongitude)) {
                    try {
                        SharedPreferences.Editor objPreferenceEditor = objSharedPreferences.edit();
                        objPreferenceEditor.putString(Constants.USER_SEARCHED_LATITUDE, strLatitude);
                        objPreferenceEditor.putString(Constants.USER_SEARCHED_LONGITUDE, strLongitude);
                        objPreferenceEditor.putString(Constants.USER_SEARCHED_LOCATION_ZOOM, strZoom);
                        objPreferenceEditor.commit();

                        latLong = new LatLng(Double.parseDouble(strLatitude), Double.parseDouble(strLongitude));

//                        final float zoomlevel = Float.parseFloat(strZoom);
                        this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                objTextViewMapAddress.setText(strAddress);
                                objTextViewDisplayMapAddress.setText(strAddress);
                                objGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 15.552834f));
                            }
                        });
                    } catch (NumberFormatException numberEx) {
                        numberEx.printStackTrace();
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                objRelativeLayoutMap.setVisibility(RelativeLayout.GONE);
            } else if (isPopupOpen) {
                popupWindowComunity.dismiss();
            }
        /*else if (objRelativeLayoutSearchBox.getVisibility() == RelativeLayout.VISIBLE) {
            openCloseSearchBar(false, ShoutDefaultActivity.this);
//            objEditTextSearch.setText("");
        }*/
            else if (keyBoardOpen) {
            /*InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(objEditTextSearch.getApplicationWindowToken(), InputMethodManager.RESULT_HIDDEN);*/
            } else if (objLinearLayoutAskShoutType.getVisibility() == LinearLayout.VISIBLE) {

                Animation rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
                objCreateShout.startAnimation(rotate_backward);

                Animation outAnimationForNeedHelp = AnimationUtils.loadAnimation(ShoutDefaultActivity.this, R.anim.push_left_out);
                objRelativeLayoutNeedHelp.startAnimation(outAnimationForNeedHelp);
                Animation outAnimationForWantToHelp = AnimationUtils.loadAnimation(ShoutDefaultActivity.this, R.anim.push_right_out);
                objRelativeLayoutWantToHelp.startAnimation(outAnimationForWantToHelp);

                outAnimationForNeedHelp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        objLinearLayoutAskShoutType.setVisibility(LinearLayout.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else {
                new AlertDialog.Builder(ShoutDefaultActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("")
                        .setMessage("Do you wish to exit the Shout App ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int pid = Process.myPid();
                                Process.killProcess(pid);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_shout_default_search:
                InputMethodManager inputMethodManager;
                /*if (objRelativeLayoutSearchBox.getVisibility() == RelativeLayout.VISIBLE) {
                    inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(objEditTextSearch, 0);
                } else {
                    objEditTextSearch.requestFocus();
                    inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputMethodManager.toggleSoftInputFromWindow(objEditTextSearch.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                    inputMethodManager.showSoftInput(objEditTextSearch, 0);
                    openCloseSearchBar(true, ShoutDefaultActivity.this);
                }*/

                Intent objIntent = new Intent(ShoutDefaultActivity.this, ShoutSearchActivity.class);
                objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(objIntent);
                this.finish();
                overridePendingTransition(0, 0);


                break;
            case R.id.btn_shout_default_filter:
              /*  startActivity(new Intent(this, SortScreenActivity.class));
                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                finish();*/
                if (isMapAddressVisible) {
                    openMapAddressLayout(false);
                } else {
                    openMapAddressLayout(true);
                }
                break;
            case R.id.btn_cancel_done_shout_default_header:

                SharedPreferences.Editor objEditor = objSharedPreferences.edit();
                objEditor.putString(Constants.USER_SEARCHED_CATEGORY, "");
                objEditor.putString(Constants.USER_SEARCHED_CATEGORY_ID, "");
                objEditor.commit();

                objRelativeLayoutSearchBox.setVisibility(RelativeLayout.GONE);
                btnFilter.setVisibility(ImageView.VISIBLE);
                isLoading = true;
                strPreferenceId = "1";
                arrShoutDefaultListModel.clear();
                objListViewShoutList.setAdapter(null);
                intOffset = 0;
                objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameShout);
                objStoreShoutDataForFirstTime = new StoreShoutDataForFirstTime("0");
                objStoreShoutDataForFirstTime.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


//                objEditTextSearch.setText("");
//                inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(objEditTextSearch.getApplicationWindowToken(), 0);
//                objShoutDefaultListAdapter = new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this);
//                objListViewShoutList.setAdapter(objShoutDefaultListAdapter);
//                openCloseSearchBar(false, ShoutDefaultActivity.this);
                break;
            default:
                break;
        }
    }

    private void openMapAddressLayout(boolean result) {
        isMapAddressVisible = result;
        if (result) {
//            objRelativeLayoutMapAddress.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            objRelativeLayoutMapAddress.setVisibility(RelativeLayout.VISIBLE);
        } else {
//            objRelativeLayoutMapAddress.animate().translationY(-objRelativeLayoutMapAddress.getHeight()).setInterpolator(new AccelerateInterpolator(2));
            objRelativeLayoutMapAddress.setVisibility(RelativeLayout.GONE);
        }
    }

    @Override
    public void isDrawerOpen(boolean result) {
        System.out.println("RESULT : " + result);
        if (result) {
            Animation fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
            objCreateShout.startAnimation(fab_close);
        } else {
            Animation fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
            objCreateShout.startAnimation(fab_open);
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        try {
            objGoogleMap = googleMap;
            googleMap.setMyLocationEnabled(true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                googleMap.clear();
                System.out.println("LOCATION_TAG : DEFAULT LOCATION ");

                System.out.println("PRASANNA PRINT VALUE : " + objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, ""));

                if (objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, "").equals("")) {
                    latLong = new LatLng(0.0, 0.0);
                } else if (objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "").isEmpty()) {
                    latLong = new LatLng(Double.parseDouble(objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, "")),
                            Double.parseDouble(objSharedPreferences.getString(Constants.USER_REGISTERED_LONGITUDE, "")));
                } else {
                    latLong = new LatLng(Double.parseDouble(objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "")),
                            Double.parseDouble(objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, "")));
                }

                if (objSharedPreferences.getString(Constants.USER_SEARCHED_LOCATION_ZOOM, "").isEmpty()) {
                    if (objSharedPreferences.getString(Constants.USER_REGISTERED_LOCATION_ZOOM, "").equals("")) {
                        try {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 15.552834f));
                        } catch (NullPointerException ne) {
                            ne.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            float zoomlevel = Float.parseFloat(objSharedPreferences.getString(Constants.USER_REGISTERED_LOCATION_ZOOM, ""));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, zoomlevel));
                        } catch (NullPointerException ne) {
                            ne.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (objSharedPreferences.getString(Constants.USER_SEARCHED_LOCATION_ZOOM, "").equals("")) {
                        try {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 15.552834f));
                        } catch (NullPointerException ne) {
                            ne.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            float zoomlevel = Float.parseFloat(objSharedPreferences.getString(Constants.USER_SEARCHED_LOCATION_ZOOM, ""));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, zoomlevel));
                        } catch (NullPointerException ne) {
                            ne.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                    @Override
                    public void onCameraChange(CameraPosition arg0) {
                        // TODO Auto-generated method stub
                        center = googleMap.getCameraPosition().target;
                        googleMap.clear();
                        try {
                            SharedPreferences.Editor objEditor = objSharedPreferences.edit();
                            objEditor.putString(Constants.USER_SEARCHED_LATITUDE, String.valueOf(center.latitude));
                            objEditor.putString(Constants.USER_SEARCHED_LONGITUDE, String.valueOf(center.longitude));
                            objEditor.putString(Constants.USER_SEARCHED_LOCATION_ZOOM, String.valueOf(googleMap.getCameraPosition().zoom));
                            objEditor.commit();
                            new GetLocationAsync(center.latitude, center.longitude).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        System.out.println("PERMISSION GRANTED COUNT : " + requestCode);
        startWorkingNormally();
    }

    @Override
    public void onWebserviceResponce(String strUrl, String strResult) {
        if (Constants.BLOG_API.equals(strUrl)) {
            try {
                JSONArray objJsonArray = new JSONArray(new JSONObject(strResult).getString("blog"));
                System.out.println("BLOG JSON ARRAY : " + objJsonArray);
                objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameBlog);
                objDatabaseHelper.saveBlog(objJsonArray);
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Constants.MY_PREFRENCES_API.equals(strUrl)) {
            System.out.println("API RESPONSE : " + strResult.toString());
            try {
                JSONObject objJsonObject = new JSONObject(strResult.toString());

                if (objJsonObject.getString("result").equals("true")) {
                    JSONArray objJsonArray = new JSONArray(objJsonObject.getString("preferences"));
                    objSharedPreferences.edit().putString(Constants.SHOUT_PREFERENCES, objJsonArray.toString()).commit();
                            /*for (int index = 0; index < objJsonArray.length(); index++) {
                                if (objJsonArray.getJSONObject(index).getString("status").equals("A")) {
                                    MyPreferencesModel objMyPreferencesModel = new MyPreferencesModel(
                                            objJsonArray.getJSONObject(index).getString("id"),
                                            objJsonArray.getJSONObject(index).getString("preference_id"),
                                            objJsonArray.getJSONObject(index).getString("title"),
                                            objJsonArray.getJSONObject(index).getString("status"),
                                            true,
                                            objJsonArray.getJSONObject(index).getString("is_checked"));
                                    arrMyPreferencesModel.add(objMyPreferencesModel);
                                }
                            }*/
                    initViewPagerComponents();
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GetLocationAsync extends AsyncTask<String, Void, String> {
        // boolean duplicateResponse;
        double x, y;
        StringBuilder str;

        public GetLocationAsync(double latitude, double longitude) {
            // TODO Auto-generated constructor stub
            x = latitude;
            y = longitude;
        }

        @Override
        protected void onPreExecute() {
            objTextViewDisplayMapAddress.setText(" Getting location ");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                addresses = null;
                geocoder = new Geocoder(ShoutDefaultActivity.this, Locale.ENGLISH);
                addresses = geocoder.getFromLocation(x, y, 1);
                str = new StringBuilder();
                if (geocoder.isPresent()) {
                    if (addresses.size() > 0) {
                        String localityString = addresses.get(0).getLocality();
                        String city = addresses.get(0).getCountryName();
                        String region_code = addresses.get(0).getCountryCode();
                        String zipcode = addresses.get(0).getPostalCode();

                        str.append(localityString + "");
                        str.append(city + "" + region_code + "");
                        str.append(zipcode + "");
                    }
                } else {
                    ShoutDefaultActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            objTextViewDisplayMapAddress.setText("No Address found");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                System.out.println("ADDRESS LINE 1 :: " + addresses.get(0).getAddressLine(0));
                System.out.println("ADDRESS LINE 2 :: " + addresses.get(0).getAddressLine(1));
                objTextViewDisplayMapAddress.setText(addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1) + " ");
                objTextViewMapAddress.setText(addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(1) + " ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class GetLoggedInUserShout extends AsyncTask<String, Void, String> {

        final ProgressDialog objProgressDialog = new ProgressDialog(ShoutDefaultActivity.this);

        public GetLoggedInUserShout() {

        }

        protected void onPreExecute() {
            super.onPreExecute();
            BaseActivity.objRelativeLayoutDefaultLoading.setVisibility(RelativeLayout.GONE);
            objProgressDialog.setMessage("Loading...");
            objProgressDialog.show();
            objProgressDialog.setCancelable(false);
            objProgressDialog.setCanceledOnTouchOutside(false);
        }

        protected String doInBackground(String... params) {
            String strResult = "";
            try {
                SharedPreferences objSharedPreferences = ShoutDefaultActivity.this.getSharedPreferences(Constants.PROFILE_PREFERENCES, ShoutDefaultActivity.STATE_ONSCREEN);
                JSONObject objJsonObject = new JSONObject();
                objJsonObject.put(Constants.USER_ID, objSharedPreferences.getString(Constants.USER_ID, ""));
                System.out.println("INPUT JSON : " + objJsonObject.toString());
                strResult = NetworkUtils.postData(Constants.LOGGED_IN_USER_SHOUTS_API, objJsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResult;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            BaseActivity.objRelativeLayoutDefaultLoading.setVisibility(RelativeLayout.GONE);
            if (objProgressDialog.isShowing())
                objProgressDialog.dismiss();
            try {
                JSONObject jSONObject = new JSONObject(s);
                if (jSONObject.getString("result").equals("true")) {
                    JSONArray jSONArray = new JSONArray(jSONObject.getString("shout"));
                    ShoutDefaultActivity.this.arrShoutDefaultListModel.clear();
                    for (int index = ShoutDefaultActivity.STATE_ONSCREEN; index < jSONArray.length(); index += ShoutDefaultActivity.STATE_OFFSCREEN) {
                        if (jSONArray.getJSONObject(index).getString("shout_image").equals("null")) {
                            arrShoutDefaultListModel.add(new ShoutDefaultListModel(
                                    jSONArray.getJSONObject(index).getString("shout_id"),
                                    jSONArray.getJSONObject(index).getString(Constants.USER_ID),
                                    jSONArray.getJSONObject(index).getString(Constants.USER_NAME),
                                    jSONArray.getJSONObject(index).getString("user_pic"),
                                    jSONArray.getJSONObject(index).getString("comment_count"),
                                    jSONArray.getJSONObject(index).getString("like_count"),
                                    jSONArray.getJSONObject(index).getString("engaging_count"),
                                    jSONArray.getJSONObject(index).getString("shout_type"),
                                    jSONArray.getJSONObject(index).getString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE),
                                    jSONArray.getJSONObject(index).getString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION),
                                    Integer.parseInt(jSONArray.getJSONObject(index).getString("is_shout_like")),
                                    jSONArray.getJSONObject(index).getString("created_date"),
                                    "",
                                    Integer.parseInt(jSONArray.getJSONObject(index).getString("shout_hide_status")),
                                    ShoutDefaultActivity.this.VIEW_PAGER_DEFAULT_POSITION, Constants.SHOUT_PASS_ENGAGE_BUTTON_DYNAMIC_HEIGHT, Constants.DEFAULT_Y,
                                    jSONArray.getJSONObject(index).getString("images"),
                                    jSONArray.getJSONObject(index).getString("is_searchable"),
                                    jSONArray.getJSONObject(index).getString("latitude"),
                                    jSONArray.getJSONObject(index).getString("longitude"),
                                    jSONArray.getJSONObject(index).getString("address"),
                                    jSONArray.getJSONObject(index).getString("category"),
                                    jSONArray.getJSONObject(index).getString("category_id"),
                                    jSONArray.getJSONObject(index).getString("is_hidden"),
                                    jSONArray.getJSONObject(index).getString("start_date"),
                                    jSONArray.getJSONObject(index).getString("end_date"),
                                    jSONArray.getJSONObject(index).getString("reshout"),
                                    jSONArray.getJSONObject(index).getString("continue_chat"),
                                    jSONArray.getJSONObject(index).getString("km"),
                                    jSONArray.getJSONObject(index).getString("is_friend"),
                                    jSONArray.getJSONObject(index).getString("notional_value")));
                        } else {
                            arrShoutDefaultListModel.add(new ShoutDefaultListModel(
                                    jSONArray.getJSONObject(index).getString("shout_id"),
                                    jSONArray.getJSONObject(index).getString(Constants.USER_ID),
                                    jSONArray.getJSONObject(index).getString(Constants.USER_NAME),
                                    jSONArray.getJSONObject(index).getString("user_pic"),
                                    jSONArray.getJSONObject(index).getString("comment_count"),
                                    jSONArray.getJSONObject(index).getString("like_count"),
                                    jSONArray.getJSONObject(index).getString("engaging_count"),
                                    jSONArray.getJSONObject(index).getString("shout_type"),
                                    jSONArray.getJSONObject(index).getString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE),
                                    jSONArray.getJSONObject(index).getString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION),
                                    Integer.parseInt(jSONArray.getJSONObject(index).getString("is_shout_like")),
                                    jSONArray.getJSONObject(index).getString("created_date"),
                                    jSONArray.getJSONObject(index).getString("shout_image"),
                                    Integer.parseInt(jSONArray.getJSONObject(index).getString("shout_hide_status")),
                                    ShoutDefaultActivity.this.VIEW_PAGER_DEFAULT_POSITION,
                                    Constants.SHOUT_PASS_ENGAGE_BUTTON_DYNAMIC_HEIGHT,
                                    Constants.DEFAULT_Y,
                                    jSONArray.getJSONObject(index).getString("images"),
                                    jSONArray.getJSONObject(index).getString("is_searchable"),
                                    jSONArray.getJSONObject(index).getString("latitude"),
                                    jSONArray.getJSONObject(index).getString("longitude"),
                                    jSONArray.getJSONObject(index).getString("address"),
                                    jSONArray.getJSONObject(index).getString("category"),
                                    jSONArray.getJSONObject(index).getString("category_id"),
                                    jSONArray.getJSONObject(index).getString("is_hidden"),
                                    jSONArray.getJSONObject(index).getString("start_date"),
                                    jSONArray.getJSONObject(index).getString("end_date"),
                                    jSONArray.getJSONObject(index).getString("reshout"),
                                    jSONArray.getJSONObject(index).getString("continue_chat"),
                                    jSONArray.getJSONObject(index).getString("km"),
                                    jSONArray.getJSONObject(index).getString("is_friend"),
                                    jSONArray.getJSONObject(index).getString("notional_value")));
                        }
                    }
                    objShoutDefaultListAdapter = new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this);
                    objListViewShoutList.setAdapter(objShoutDefaultListAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class LoadMoreShouts extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            String strResult = "";
            try {
                intOffset = objDatabaseHelper.getShoutId("LAST");
                System.out.println("LAST INSERTED ID : " + intOffset);
                JSONObject objJsonObject = new JSONObject();
                objJsonObject.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
                objJsonObject.put("offset", intOffset);
                objJsonObject.put("pull_refresh", "0");

                /*if (SortScreenActivity.arrSortFilterSelectedCategoryId.size() > 0) {
                    JSONArray objJSONArraycategories = new JSONArray();
                    for (int index = 0; index < SortScreenActivity.arrSortFilterSelectedCategoryId.size(); index++) {
                        objJSONArraycategories.put(SortScreenActivity.arrSortFilterSelectedCategoryId.get(index));
                    }
                    objJsonObject.put("categories", objJSONArraycategories);
                }

                objJsonObject.put("popularity", objSharedPreferences.getString(Constants.SORT_POPULARITY, ""));
                objJsonObject.put("recency", objSharedPreferences.getString(Constants.SORT_RECENCY, ""));
                objJsonObject.put("location", objSharedPreferences.getString(Constants.SORT_LOCATION, ""));*/
                if (objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "").isEmpty() && objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, "").isEmpty()) {
                    objJsonObject.put("latitude", objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, ""));
                    objJsonObject.put("longitude", objSharedPreferences.getString(Constants.USER_REGISTERED_LONGITUDE, ""));
                } else {
                    objJsonObject.put("latitude", objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, ""));
                    objJsonObject.put("longitude", objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, ""));
                }
                objJsonObject.put("preference_id", strPreferenceId);
                // TODO: 08/11/16 ADDING TWO PARAMETERS FOR SEARCHED TAGS
                objJsonObject.put("category_id", objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY_ID, ""));
                if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY_ID, "").isEmpty() || objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY_ID, "").equals("0")) {
                    objJsonObject.put("category_frez", objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").trim());
                } else {
                    if (arrSearchTagModel.size() > 0) {
                        StringBuilder strFrez = new StringBuilder();
                        for (int index = 0; index < arrSearchTagModel.size(); index++) {
                            strFrez.append(" " + String.valueOf(arrSearchTagModel.get(index).getSearchedTagWord()));
                        }
                        System.out.println("MODIFIED FREZ : " + strFrez.toString());
                        objJsonObject.put("category_frez", String.valueOf(strFrez).trim());
                    } else {
                        objJsonObject.put("category_frez", "");
                    }
                }
//                objJsonObject.put("scroll_count", objSharedPreferences.getString(Constants.SHOUT_BOARD_LIST_API_SCROLL_COUNT, ""));

                strResult = NetworkUtils.postData(Constants.SHOUT_LIST, objJsonObject.toString());

                if (isCancelled()) {
                    return null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResult;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Animation fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
                objCreateShout.startAnimation(fab_open);
                objLinearBottomLoad.setVisibility(LinearLayout.GONE);
                Constants.hideToBottom(objLinearBottomLoad);
                final JSONObject jSONObject = new JSONObject(s);
                if (jSONObject.getString("result").equals("true")) {

                    // UPDATE API SCROLL COUNT
                    setAPIScrollCount(Integer.parseInt(objSharedPreferences.getString(Constants.SHOUT_BOARD_LIST_API_SCROLL_COUNT, "")) + 1);

                    isFirstTime = false;
                    // TELL THE USER THAT HE HAVE LOAD MORE SHOUTS AND NOW HE CAN SCROLL LISTVIEW TO BOTTOM AGAIN TILL NEWLY INSERTED LAST ITEM
                    // IT IS USED FOR CALLING LoadMoreAPI for once.

                    if (new JSONArray(jSONObject.getString("shout")).length() > 0) {
                        try {
                            arrShoutDefaultListModel.addAll(objDatabaseHelper.saveShout(new JSONArray(jSONObject.getString("shout")), "0"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("SHOUT LOCAL DATA ARRAY COUNT : " + arrShoutDefaultListModel.size());

                        state = objListViewShoutList.onSaveInstanceState();
                        arrShoutDefaultListModel.clear();

                        if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").length() > 0) {
                            arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, true);
                        } else {
                            arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, false);
                        }
                        objListViewShoutList.setAdapter(new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this));
                        if (state != null) {
                            objListViewShoutList.onRestoreInstanceState(state);
                        }
                        objShoutDefaultListAdapter.notifyDataSetChanged();
                        isLoading = true;
                        showNoDataFoundUI(false);

                        // TODO: 30/09/16 SAVING NOTIFICATION COUNT INTO PROFILE SHAREDPREFERENCES
                        if (jSONObject.getString("notification_count").equals("")) {
                            SharedPreferences.Editor editor = objSharedPreferences.edit();
                            editor.putString(Constants.USER_NOTIFICATION_COUNT, "0");
                            editor.putString(Constants.USER_MESSAGE_COUNT, "0");
                            editor.commit();
                        } else {
                            SharedPreferences.Editor editor = objSharedPreferences.edit();
                            editor.putString(Constants.USER_NOTIFICATION_COUNT, jSONObject.getString("notification_count"));
                            editor.putString(Constants.USER_MESSAGE_COUNT, jSONObject.getString("message_count"));
                            editor.commit();
                        }
                        updateNotificationCount(jSONObject.getString("notification_count"));
                        updateMessageCount(jSONObject.getString("message_count"));

                    } else {
                        // IF CATEGORY IS EMPTY THEN AND TEHN ONLY CALL NEARBYAPI ELSE DO NOTHING
                        System.out.println("CATEGORY FREZ ON LOAD MORE : " + objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, ""));
                        if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").isEmpty() || objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").equals("")) {
                            if (!strPreferenceId.equals("2")) {
                                //&& !strPreferenceId.equals("3")
                                new LoadNearByShouts().execute();
                            } else {
                                showNoDataFoundUI(true);
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class StoreShoutDataForFirstTime extends AsyncTask<String, Void, String> {
        final ProgressDialog objProgressDialog = new ProgressDialog(ShoutDefaultActivity.this);
        String key = "";

        public StoreShoutDataForFirstTime(String key) {
            this.key = key;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("KEY : " + key);
            if (key.equals("0")) {
                BaseActivity.objRelativeLayoutDefaultLoading.setVisibility(RelativeLayout.GONE);
                objProgressDialog.setMessage("Searching for shouts");
                objProgressDialog.show();
                objProgressDialog.setCanceledOnTouchOutside(false);
                objProgressDialog.setCancelable(false);
            } else {
                objShoutDefaultSwipableLayout.setRefreshing(true);
            }
        }

        protected String doInBackground(String... params) {
            String strResult = "";
            try {
                JSONObject objJsonObject = new JSONObject();
                objJsonObject.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
                if (key.equals("1")) {
                    objJsonObject.put("offset", objDatabaseHelper.getShoutId("FIRST"));
                } else {
                    objJsonObject.put("offset", intOffset);
                }
                objJsonObject.put("pull_refresh", key);

                /*if (SortScreenActivity.arrSortFilterSelectedCategoryId.size() > 0) {
                    JSONArray objJSONArraycategories = new JSONArray();
                    for (int index = 0; index < SortScreenActivity.arrSortFilterSelectedCategoryId.size(); index++) {
                        objJSONArraycategories.put(SortScreenActivity.arrSortFilterSelectedCategoryId.get(index));
                    }
                    objJsonObject.put("categories", objJSONArraycategories);
                    ShoutDefaultActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            objViewFilterApply.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    ShoutDefaultActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            objViewFilterApply.setVisibility(View.GONE);
                        }
                    });
                }
                objJsonObject.put("popularity", objSharedPreferences.getString(Constants.SORT_POPULARITY, ""));
                objJsonObject.put("recency", objSharedPreferences.getString(Constants.SORT_RECENCY, ""));
                objJsonObject.put("location", objSharedPreferences.getString(Constants.SORT_LOCATION, ""));*/
                if (objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "").isEmpty() && objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, "").isEmpty()) {
                    objJsonObject.put("latitude", objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, ""));
                    objJsonObject.put("longitude", objSharedPreferences.getString(Constants.USER_REGISTERED_LONGITUDE, ""));
                } else {
                    objJsonObject.put("latitude", objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, ""));
                    objJsonObject.put("longitude", objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, ""));
                }
                objJsonObject.put("preference_id", strPreferenceId);
                // TODO: 08/11/16 ADDING TWO PARAMETERS FOR SEARCHED TAGS
                objJsonObject.put("category_id", objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY_ID, ""));
                if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY_ID, "").isEmpty() || objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY_ID, "").equals("0")) {
                    objJsonObject.put("category_frez", objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").trim());
                } else {
                    if (arrSearchTagModel.size() > 0) {
                        StringBuilder strFrez = new StringBuilder();
                        for (int index = 0; index < arrSearchTagModel.size(); index++) {
                            strFrez.append(" " + String.valueOf(arrSearchTagModel.get(index).getSearchedTagWord()));
                        }
                        System.out.println("MODIFIED FREZ : " + strFrez.toString());
                        objJsonObject.put("category_frez", String.valueOf(strFrez).trim());
                    } else {
                        objJsonObject.put("category_frez", "");
                    }
                }
//                objJsonObject.put("scroll_count", objSharedPreferences.getString(Constants.SHOUT_BOARD_LIST_API_SCROLL_COUNT, ""));

                /*if (objJsonObject.getString("popularity").equals("0") && objJsonObject.getString("recency").equals("0") && objJsonObject.getString("location").equals("0") && SortScreenActivity.arrSortFilterSelectedCategoryId.size() == 0) {
                    System.out.println("IN POPULARITY EMPTY");
                    ShoutDefaultActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            objViewFilterApply.setVisibility(View.GONE);
                        }
                    });
                } else {
                    System.out.println("IN POPULARITY SELECTED");
                    ShoutDefaultActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            objViewFilterApply.setVisibility(View.VISIBLE);
                        }
                    });
                }*/
                Utils.d("PRASANNA", "PRASANNA INPUT : " + objJsonObject.toString());

                strResult = NetworkUtils.postData(Constants.SHOUT_LIST, objJsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResult;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            objShoutDefaultSwipableLayout.setRefreshing(false);
            BaseActivity.objRelativeLayoutDefaultLoading.setVisibility(RelativeLayout.GONE);
            if (objProgressDialog.isShowing())
                objProgressDialog.dismiss();
            try {
                JSONObject jSONObject = new JSONObject(s);
                if (jSONObject.getString("result").equals("true")) {

                    // UPDATE API SCROLL COUNT
                    setAPIScrollCount(Integer.parseInt(objSharedPreferences.getString(Constants.SHOUT_BOARD_LIST_API_SCROLL_COUNT, "")) + 1);

                    isFirstTime = false;
                    final JSONArray objJsonArray = new JSONArray(jSONObject.getString("shout"));

                    if (objJsonArray.length() > 0) {
                        if (key.equals("0")) {
                            objDatabaseHelper.deleteShoutEntries();
                        }

                        // TODO: 30/09/16 SAVING NOTIFICATION COUNT INTO PROFILE SHAREDPREFERENCES
                        if (jSONObject.getString("notification_count").equals("") && jSONObject.getString("message_count").equals("")) {
                            SharedPreferences.Editor editor = objSharedPreferences.edit();
                            editor.putString(Constants.USER_NOTIFICATION_COUNT, "0");
                            editor.putString(Constants.USER_MESSAGE_COUNT, "0");
                            editor.commit();
                        } else {
                            SharedPreferences.Editor editor = objSharedPreferences.edit();
                            editor.putString(Constants.USER_NOTIFICATION_COUNT, jSONObject.getString("notification_count"));
                            editor.putString(Constants.USER_MESSAGE_COUNT, jSONObject.getString("message_count"));
                            editor.commit();
                        }
                        updateNotificationCount(jSONObject.getString("notification_count"));
                        updateMessageCount(jSONObject.getString("message_count"));

                        System.out.println("SHOUT JSON ARRAY LENGTH : " + objJsonArray.toString().length());
                        System.out.println("TEST : " + key);
                        state = objListViewShoutList.onSaveInstanceState();

                        objDatabaseHelper.saveShout(objJsonArray, "0");
                        if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").length() > 0) {
                            arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, true);
                        } else {
                            arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, false);
                        }
                        System.out.println("PRASANNA PRINT : MODEL ARRAY : " + arrShoutDefaultListModel.size());
                        objShoutDefaultListAdapter = new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this);
                        objListViewShoutList.setAdapter(objShoutDefaultListAdapter);
                        objListViewShoutList.onRestoreInstanceState(state);
                    } else if (objJsonArray.length() < 5) {
                        // TODO: 06/10/16 LOADING NEAR BY FRIENDS SHOUT IF FRIENDS SHOUTS ARE NOT AVAILABLE.
                        new LoadNearByShouts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        // TODO: 06/10/16 LOADING NEAR BY FRIENDS SHOUT IF FRIENDS SHOUTS ARE AVAILABLE BUT THERE IS LESS THEN 5 SHOUTS .
                        new LoadNearByShouts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setAPIScrollCount(int intCount) {
        if (intCount == 0) {
            objSharedPreferences.edit().putString(Constants.SHOUT_BOARD_LIST_API_SCROLL_COUNT, "0").commit();
        } else {
            objSharedPreferences.edit().putString(Constants.SHOUT_BOARD_LIST_API_SCROLL_COUNT, String.valueOf(intCount)).commit();
        }
    }

    public class ShoutUpdateBroadcastReceiver extends BroadcastReceiver {

        public ShoutUpdateBroadcastReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (inSearchMode == 0) {
                    System.out.println("ON RECEIVE CALLED");
                    objDatabaseHelper = new DatabaseHelper(ShoutDefaultActivity.this);
                    state = objListViewShoutList.onSaveInstanceState();
                    arrShoutDefaultListModel.clear();
                    if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").length() > 0) {
                        arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, true);
                    } else {
                        arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, false);
                    }
                    objListViewShoutList.setAdapter(new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this));
                }
                if (state != null) {
                    objListViewShoutList.onRestoreInstanceState(state);
                }
                updateNotificationCount(getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_NOTIFICATION_COUNT, ""));
                updateMessageCount(getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_MESSAGE_COUNT, ""));
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class LoadNearByShouts extends AsyncTask<String, Void, String> {

        String strResult = "";
        final ProgressDialog objProgressDialog = new ProgressDialog(ShoutDefaultActivity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isFirstTime) {
                BaseActivity.objRelativeLayoutDefaultLoading.setVisibility(RelativeLayout.GONE);
                objProgressDialog.setMessage("Searching for shouts");
                objProgressDialog.show();
                objProgressDialog.setCanceledOnTouchOutside(false);
                objProgressDialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                int offset = objDatabaseHelper.getNearByFriendsShoutCount();
                System.out.println("SHOUT NEAR BY QUERY RESULT : " + offset);
                JSONObject objJsonObject = new JSONObject();
                objJsonObject.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
                objJsonObject.put("offset", offset);
                objJsonObject.put("pull_refresh", "0");

                /*if (SortScreenActivity.arrSortFilterSelectedCategoryId.size() > 0) {
                    JSONArray objJSONArraycategories = new JSONArray();
                    for (int index = 0; index < SortScreenActivity.arrSortFilterSelectedCategoryId.size(); index++) {
                        objJSONArraycategories.put(SortScreenActivity.arrSortFilterSelectedCategoryId.get(index));
                    }
                    objJsonObject.put("categories", objJSONArraycategories);
                }
                objJsonObject.put("popularity", objSharedPreferences.getString(Constants.SORT_POPULARITY, ""));
                objJsonObject.put("recency", objSharedPreferences.getString(Constants.SORT_RECENCY, ""));
                objJsonObject.put("location", objSharedPreferences.getString(Constants.SORT_LOCATION, ""));*/
                if (objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "").isEmpty() && objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, "").isEmpty()) {
                    objJsonObject.put("latitude", objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, ""));
                    objJsonObject.put("longitude", objSharedPreferences.getString(Constants.USER_REGISTERED_LONGITUDE, ""));
                } else {
                    objJsonObject.put("latitude", objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, ""));
                    objJsonObject.put("longitude", objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, ""));
                }
                objJsonObject.put("preference_id", strPreferenceId);
                // TODO: 08/11/16 ADDING TWO PARAMETERS FOR SEARCHED TAGS
                objJsonObject.put("category_id", objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY_ID, ""));
                if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY_ID, "").isEmpty() || objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY_ID, "").equals("0")) {
                    objJsonObject.put("category_frez", objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").trim());
                } else {
                    if (arrSearchTagModel.size() > 0) {
                        StringBuilder strFrez = new StringBuilder();
                        for (int index = 0; index < arrSearchTagModel.size(); index++) {
                            strFrez.append(" " + String.valueOf(arrSearchTagModel.get(index).getSearchedTagWord()));
                        }
                        System.out.println("MODIFIED FREZ : " + strFrez.toString());
                        objJsonObject.put("category_frez", String.valueOf(strFrez).trim());
                    } else {
                        objJsonObject.put("category_frez", "");
                    }
                }
//                objJsonObject.put("scroll_count", objSharedPreferences.getString(Constants.SHOUT_BOARD_LIST_API_SCROLL_COUNT, ""));

                strResult = NetworkUtils.postData(Constants.NEIGHBOURS_SHOUT_API, objJsonObject.toString());
                if (isCancelled()) {
                    return null;
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {


//                strPreferenceId = "0";
                if (objProgressDialog.isShowing()) {
                    objProgressDialog.dismiss();
                }
                objShoutDefaultSwipableLayout.setRefreshing(false);
                Animation fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
                objCreateShout.startAnimation(fab_open);
                objLinearBottomLoad.setVisibility(LinearLayout.GONE);
                Constants.hideToBottom(objLinearBottomLoad);
                final JSONObject jSONObject = new JSONObject(s);
                if (jSONObject.getString("result").equals("true")) {

                    // UPDATE API SCROLL COUNT
                    setAPIScrollCount(Integer.parseInt(objSharedPreferences.getString(Constants.SHOUT_BOARD_LIST_API_SCROLL_COUNT, "")) + 1);

                    isFirstTime = false;
                    // TELL THE USER THAT HE HAVE LOAD MORE SHOUTS AND NOW HE CAN SCROLL LISTVIEW TO BOTTOM AGAIN TILL NEWLY INSERTED LAST ITEM
                    // IT IS USED FOR CALLING LoadMoreAPI for once.
                    if (new JSONArray(jSONObject.getString("shout")).length() > 0) {
                        state = objListViewShoutList.onSaveInstanceState();
                        try {
                            arrShoutDefaultListModel.addAll(objDatabaseHelper.saveShout(new JSONArray(jSONObject.getString("shout")), "0"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        /*System.out.println("SHOUT LOCAL DATA ARRAY COUNT : " + arrShoutDefaultListModel.size());
                        objShoutDefaultListAdapter = new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this);
                        objListViewShoutList.setAdapter(objShoutDefaultListAdapter);
                        objListViewShoutList.onRestoreInstanceState(state);*/

                        state = objListViewShoutList.onSaveInstanceState();
                        arrShoutDefaultListModel.clear();
                        if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").length() > 0) {
                            arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, true);
                        } else {
                            arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, false);
                        }
                        objListViewShoutList.setAdapter(new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this));
                        if (state != null) {
                            objListViewShoutList.onRestoreInstanceState(state);
                        }
                        isLoading = true;
                        showNoDataFoundUI(false);
                    } else {
                        showNoDataFoundUI(true);
                    }
                    /*else {
                        if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").isEmpty() || objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").equals("")) {
                            isLoading = true;
                        }
                    }*/
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showNoDataFoundUI(boolean visibility) {
        if (visibility) {
            if (arrShoutDefaultListModel.size() > 0) {
                linearLayoutNoDataFound.setVisibility(LinearLayout.GONE);
            } else {
                linearLayoutNoDataFound.setVisibility(LinearLayout.VISIBLE);
            }
            String strNoDataImageUrl = "";
            String strNoDataTextMessage = "";

            for (int index = 0; index < arrMyPreferencesModel.size(); index++) {
                if (strPreferenceId.equals(arrMyPreferencesModel.get(index).getPreference_id())) {
                    System.out.println(" PRASAD PRINT :::: " + arrMyPreferencesModel.get(index).getImage());
                    if (arrMyPreferencesModel.get(index).getImage().equals(Constants.HTTP_URL + "null")) {
                        strNoDataImageUrl = "";
                    } else {
                        strNoDataImageUrl = arrMyPreferencesModel.get(index).getImage();
                    }

                    if (arrMyPreferencesModel.get(index).getMessage().equals("null")) {
                        strNoDataTextMessage = Constants.DEFAULT_NO_DATA_FOUND_MESSAGE;
                    } else {
                        strNoDataTextMessage = arrMyPreferencesModel.get(index).getMessage();
                    }
                }
            }
            textViewNoDataFound.setText(strNoDataTextMessage);
            if (strNoDataImageUrl.equals("")) {
                Picasso.with(ShoutDefaultActivity.this).load(R.drawable.shout_app_logo).into(imageViewNoDataFound);
            } else {
                Picasso.with(ShoutDefaultActivity.this).load(strNoDataImageUrl).into(imageViewNoDataFound);
            }
        } else {
            linearLayoutNoDataFound.setVisibility(LinearLayout.GONE);
        }
    }

    class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            System.out.println("IN TAP_GESTURE CLICKED");
//            showCommunityPopup(true,"");
            return true;
        }
    }

    public void showCommunityPopup(final boolean result, String title) {
        // COMMUNITY POPUP INTIALIZATION
//        initPoup();
        if (result) {
            LayoutInflater objCommunityPopupInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewComunityPopup = objCommunityPopupInflater.inflate(R.layout.popup_comunity_layout, null, true);
            listViewComunityPopup = (ListView) viewComunityPopup.findViewById(R.id.listViewComunityPopup);
            objLinearCommunityPopupRoot = (LinearLayout) viewComunityPopup.findViewById(R.id.linear_community_wheel_root);
            popupWindowComunity = new PopupWindow(viewComunityPopup);
            popupWindowComunity.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindowComunity.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindowComunity.setFocusable(true);
            popupWindowComunity.showAtLocation(viewComunityPopup, Gravity.CENTER, 0, 0);

            popupComunityLayoutAdapter = new PopupComunityLayoutAdapter(activity, activity, arrMyPreferencesModel, title);
            listViewComunityPopup.setAdapter(popupComunityLayoutAdapter);

            listViewComunityPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    popupWindowComunity.dismiss();
                    isPopupOpen = false;
                    try {
                        searchViewPager.setCurrentItem(position);
                        /*MyPreferencesModel objMyPreferencesModel = arrMyPreferencesModel.get(position);
                        strPreferenceId = objMyPreferencesModel.getPreference_id();
                        System.out.println("SELECTED PREFERENCE ID : " + strPreferenceId);
                        objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameShout);
//                    startActivity(new Intent(ShoutDefaultActivity.this, ShoutDefaultActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        arrShoutDefaultListModel.clear();
                        objShoutDefaultListAdapter.notifyDataSetChanged();
                        objListViewShoutList.setAdapter(null);
                        intOffset = 0;
                        if (strPreferenceId.equals("1")) {
                            new LoadNearByShouts().execute();
                        } else {
                            new StoreShoutDataForFirstTime("0").execute();
                        }*/
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            objLinearCommunityPopupRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindowComunity.dismiss();
                }
            });
            isPopupOpen = true;
        } else {
            isPopupOpen = false;
        }
    }

    private void initPoup() {
        relativeLayout = (RelativeLayout) findViewById(R.id.relative_default_root);
        objLinearCommunityPopupRoot = (LinearLayout) findViewById(R.id.linear_community_wheel_root);
        viewComunityPopup = LayoutInflater.from(activity).inflate(R.layout.popup_comunity_layout, objLinearCommunityPopupRoot, false);
        popupWindowComunity = new PopupWindow(ShoutDefaultActivity.this);
        popupWindowComunity.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        popupWindowComunity.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        popupWindowComunity.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.rounded_corner_like_popup_background));
        listViewComunityPopup = (ListView) viewComunityPopup.findViewById(R.id.listViewComunityPopup);
    }

    public void refreshWholeActivity() {

        try {
            updateNotificationCount(getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE).getString(Constants.USER_NOTIFICATION_COUNT, ""));

            objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, STATE_ONSCREEN);
            SharedPreferences.Editor objEditor = objSharedPreferences.edit();
            objEditor.putString(Constants.IS_NEW_USER, "false");
            objEditor.commit();

            // FOR FIRST TIME WHEN USER GETS FRESH LOGIN TO THE APP
            if (objSharedPreferences.getString(Constants.IS_CURRENT_DATE, "").equals("")) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat(Constants.SHOUT_LIST_DATE_FORMAT);
                String currentDate = df.format(c.getTime());
                objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, STATE_ONSCREEN);
                SharedPreferences.Editor objDateEditor = objSharedPreferences.edit();
                objDateEditor.putString(Constants.IS_CURRENT_DATE, currentDate);
                objDateEditor.commit();
            }
            arrShoutDefaultListModel = new ArrayList<ShoutDefaultListModel>();

            System.out.println("STORED DATE : " + objSharedPreferences.getString(Constants.IS_CURRENT_DATE, ""));
            System.out.println("CURRENT DATE : " + new SimpleDateFormat(Constants.SHOUT_LIST_DATE_FORMAT).format(Calendar.getInstance().getTime()));

            if (objSharedPreferences.getString(Constants.IS_CURRENT_DATE, "").equals(new SimpleDateFormat(Constants.SHOUT_LIST_DATE_FORMAT).format(Calendar.getInstance().getTime()))) {
                System.out.println("LOADING SHOUTS FOR CURRENT DATE : " + new SimpleDateFormat(Constants.SHOUT_LIST_DATE_FORMAT).format(Calendar.getInstance().getTime()));
                if (ConnectivityBroadcastReceiver.isConnected()) {
                    arrShoutDefaultListModel.clear();
                    if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").length() > 0) {
                        arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, true);
                    } else {
                        arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, false);
                    }
                    if (arrShoutDefaultListModel.size() == 0) {
                        new StoreShoutDataForFirstTime("0").execute();
                    } else {
                        objShoutDefaultListAdapter = new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this);
                        objListViewShoutList.setAdapter(objShoutDefaultListAdapter);
                    }
                } else {
                    arrShoutDefaultListModel.clear();
                    if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").length() > 0) {
                        arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, true);
                    } else {
                        arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, false);
                    }
                    System.out.println("OFFLINE DATA : " + arrShoutDefaultListModel);
                    objShoutDefaultListAdapter = new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this);
                    objListViewShoutList.setAdapter(objShoutDefaultListAdapter);
                }
            } else {

                objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, STATE_ONSCREEN);
                SharedPreferences.Editor objDateEditor = objSharedPreferences.edit();
                objDateEditor.putString(Constants.IS_CURRENT_DATE, new SimpleDateFormat(Constants.SHOUT_LIST_DATE_FORMAT).format(Calendar.getInstance().getTime()));
                objDateEditor.commit();

                arrShoutDefaultListModel.clear();
                if (objSharedPreferences.getString(Constants.USER_SEARCHED_CATEGORY, "").length() > 0) {
                    arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, true);
                } else {
                    arrShoutDefaultListModel = objDatabaseHelper.getShoutDefaultListModelArray("0", strPreferenceId, false);
                }
                System.out.println("OFFLINE DATA : " + arrShoutDefaultListModel);
                if (arrShoutDefaultListModel.size() > 0) {
                    objShoutDefaultListAdapter = new ShoutDefaultListAdapter(arrShoutDefaultListModel, ShoutDefaultActivity.this, ShoutDefaultActivity.this);
                    objListViewShoutList.setAdapter(objShoutDefaultListAdapter);
                } else {
                    new StoreShoutDataForFirstTime("0").execute();
                }
            }

            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showViews() {
        objRelativeLayoutHeader.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        objCreateShout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    private void hideViews() {
        objRelativeLayoutHeader.animate().translationY(-objRelativeLayoutHeader.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) objCreateShout.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        objCreateShout.animate().translationY(objCreateShout.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    objTextViewDisplayMapAddress.setText(place.getAddress());
                    objGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.552834f));
                    SharedPreferences.Editor objEditor = objSharedPreferences.edit();
                    objEditor.putString(Constants.USER_SEARCHED_LATITUDE, String.valueOf(place.getLatLng().latitude));
                    objEditor.putString(Constants.USER_SEARCHED_LONGITUDE, String.valueOf(place.getLatLng().longitude));
                    objEditor.commit();
                    System.out.println("LATLNG UPDATED : " + objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "") + " : " + objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, ""));
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    // TODO: Handle the error.
//                    Log.i(TAG, status.getStatusMessage());
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
        }
    }
}
