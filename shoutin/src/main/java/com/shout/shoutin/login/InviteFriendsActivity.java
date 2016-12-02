package com.shout.shoutin.login;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.shout.shoutin.CustomClasses.CustomSnackBarLayout;
import com.shout.shoutin.R;
import com.shout.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shout.shoutin.Utils.Constants;
import com.shout.shoutin.Utils.KeyboardUtils;
import com.shout.shoutin.Utils.NetworkUtils;
import com.shout.shoutin.Utils.Utils;
import com.shout.shoutin.app.AppController;
import com.shout.shoutin.database.DatabaseHelper;
import com.shout.shoutin.login.adapter.ContactExpandableListAdapter;
import com.shout.shoutin.login.model.ContactModel;
import com.shout.shoutin.main.Model.Continent;
import com.shout.shoutin.main.ShoutDefaultActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class InviteFriendsActivity extends MPermission implements View.OnClickListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener, SwipeRefreshLayout.OnRefreshListener, ConnectivityBroadcastReceiver.ConnectivityReceiverListener {

    //    SpotsDialog progressDialog;
    private ProgressBar progressBar;
    ProgressDialog objProgressDialog;
    RelativeLayout objRelativeBeforeLoading;
    //    RelativeLayout objRelativeAfterLoading;
    ImageView objImageProgress;
    Button btnSkipAddingShouts;
    public static Button btnGiveThemShout;
    EditText objEditTextSearchFilter;
//    LinearLayout objLinearTopView;

    ArrayList<String> arrStrContactName;
    ArrayList<String> arrStrContactNumber;
    ContactExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    private ArrayList<Continent> continentList = new ArrayList<Continent>();
    ArrayList<ContactModel> NonFriendList = new ArrayList<ContactModel>();
    ArrayList<ContactModel> arrayListFriendList = new ArrayList<ContactModel>();


    SharedPreferences objSharedPreferences;
    Boolean isNewUser = true;

    DatabaseHelper objDatabaseHelper;
    JSONArray objFriendsJson = new JSONArray();
    SwipeRefreshLayout objSwipeRefreshLayout;
    InputMethodManager im;


    private RelativeLayout stickyView;
    private View stickyViewSpacer;
    private int MAX_ROWS = 20;
    private View heroImageView;
    View listHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!InviteFriendsActivity.super.mayRequestLocationAccess()) {
                return;
            }
        } else {
            startWorkingNormally();
        }
    }

    @Override
    public void onPermissionsGrantedListener(int requestCode) {
        startWorkingNormally();
    }

    private void startWorkingNormally() {
        objDatabaseHelper = new DatabaseHelper(InviteFriendsActivity.this);
        objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
        if (objSharedPreferences.getString(Constants.IS_NEW_USER, "").equals("true")) {
            isNewUser = true;
        } else {
            isNewUser = false;
        }

        // CALLES CONNETIVITY CHECK LISTENER
        new AppController().setConnectivityListener(this);

        init();


        try {
            objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameShout);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Contacts syncing", Toast.LENGTH_SHORT).show();
        getFacebookFriends();

        arrStrContactName = new ArrayList<String>();
        arrStrContactNumber = new ArrayList<String>();
        im = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        RelativeLayout objRelativeLayoutRoot = (RelativeLayout) findViewById(R.id.relative_root_invite_friends);

        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                Log.d("keyboard", "keyboard visible: " + isVisible);
                if (isVisible) {
//                    objSwipeRefreshLayout.setEnabled(false);
                    btnGiveThemShout.setVisibility(Button.GONE);
                    /*objLinearTopView.setVisibility(LinearLayout.GONE);
                    listHeader.setVisibility(View.GONE);
                    expListView.smoothScrollToPosition(120);*/

                    /*listHeader.setVisibility(View.GONE);
                    expListView.smoothScrollToPosition(10);
                    stickyView.setY(0);
                    expListView.setEnabled(false);*/
                } else {
                    //objLinearTopView.setVisibility(LinearLayout.VISIBLE);
                    /*openCloseSearchBar(false, ShoutDefaultActivity.this);
                    objEditTextSearch.setText("");
                    listHeader.setVisibility(View.VISIBLE);*/
//                    objSwipeRefreshLayout.setEnabled(true);
                    btnGiveThemShout.setVisibility(Button.VISIBLE);

                    /*listHeader.setVisibility(View.VISIBLE);
                    View header = expListView.getChildAt(0);
                    View layout = header.findViewById(R.id.linear_invite_contact_screen_top_view);
                    System.out.println("LAYOUT BOTTOM : " + layout.getBottom());
                    stickyView.setY(layout.getBottom());
                    expListView.smoothScrollToPosition(0);
                    expListView.setEnabled(true);*/
                }
            }
        });
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

    public void setLocalData() {
        try {
            objRelativeBeforeLoading.setVisibility(RelativeLayout.GONE);
//            objRelativeAfterLoading.setVisibility(RelativeLayout.VISIBLE);
            objSwipeRefreshLayout.setVisibility(SwipeRefreshLayout.VISIBLE);
            objImageProgress.setVisibility(ImageView.GONE);
            arrayListFriendList.clear();
            continentList.clear();
            NonFriendList.clear();
            arrayListFriendList = objDatabaseHelper.getAllFriendsList();
            NonFriendList = objDatabaseHelper.getAllNonFriendsList();
            /*Continent continentFriends = new Continent("DISPLAYING YOUR CONTACTS ON SHOUTIN ", arrayListFriendList);
            continentList.add(continentFriends);*/
            if (arrayListFriendList.size() == 0) {
                arrayListFriendList.add(new ContactModel(0, "image", "http://vignette4.wikia.nocookie.net/disney/images/4/4c/Cinderella_Diamond_Edition_Banner_3.jpg", "", "", "", false, "", 0, "", ""));
                Continent continentFriends = new Continent("DISPLAYING YOUR CONTACTS ON SHOUTIN ", arrayListFriendList);
                continentList.add(continentFriends);
            } else {
                Continent continentFriends = new Continent("DISPLAYING YOUR CONTACTS ON SHOUTIN ", arrayListFriendList);
                continentList.add(continentFriends);
            }
            Continent continentNotFriends = new Continent("DISPLAYING YOUR CONTACTS AWAITED ON SHOUTIN ", NonFriendList);
            continentList.add(continentNotFriends);
            listAdapter = new ContactExpandableListAdapter(InviteFriendsActivity.this, InviteFriendsActivity.this, continentList);
            expListView.setAdapter(listAdapter);
            expandAll();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFacebookFriends() {
        try {
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/{education-experience-id}",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            System.out.println("NEW RESPONSE : " + response);
                        }
                    }
            ).executeAsync();

            GraphRequestBatch batch = new GraphRequestBatch(
                    GraphRequest.newMyFriendsRequest(
                            AccessToken.getCurrentAccessToken(),
                            new GraphRequest.GraphJSONArrayCallback() {
                                @Override
                                public void onCompleted(
                                        final JSONArray jsonArray,
                                        GraphResponse response) {
                                    System.out.println("SURESH RESPONSE 1: " + response);
                                    System.out.println("SURESH RESPONSE 2: " + jsonArray);
                                    if (isNewUser) {
/*//                                        objRelativeAfterLoading.setVisibility(RelativeLayout.GONE);
                                        objSwipeRefreshLayout.setVisibility(SwipeRefreshLayout.GONE);
                                        objRelativeBeforeLoading.setVisibility(RelativeLayout.VISIBLE);
                                        objImageProgress.setVisibility(ImageView.VISIBLE);
                                        Animation sampleFadeAnimation = AnimationUtils.loadAnimation(InviteFriendsActivity.this, R.anim.rotating_progress);
                                        objImageProgress.startAnimation(sampleFadeAnimation);

                                        Handler objHandler = new Handler();
                                        objHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                               *//* prasad
                                                new LoadingDataForFirstTime().execute(jsonArray.toString());*//*
                                                setLocalData();
                                            }
                                        }, 3000);*/
                                    } else {

                                        if (ConnectivityBroadcastReceiver.isConnected()) {
                                            objRelativeBeforeLoading.setVisibility(RelativeLayout.GONE);
//                                            objRelativeAfterLoading.setVisibility(RelativeLayout.VISIBLE);
                                            objSwipeRefreshLayout.setVisibility(SwipeRefreshLayout.VISIBLE);
                                            objImageProgress.setVisibility(ImageView.GONE);

//                                            new LoadingData().execute(jsonArray.toString());
                                            new LoadingContactsForFirstTime().execute(jsonArray.toString());
                                        }
                                    }
                                }
                            })

            );
            batch.addCallback(new GraphRequestBatch.Callback() {
                @Override
                public void onBatchCompleted(GraphRequestBatch graphRequests) {
                    // Application code for when the batch finishes
                }
            });
            batch.executeAsync();

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name,last_name,name,email,link,picture.type(large),gender,user_birthday,user_education_history,user_work_history");

        } catch (NullPointerException ne) {
            ne.printStackTrace();
            System.out.println("FACEBOOK ACCESS TOKEN EXPIRED");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {

        stickyView = (RelativeLayout) findViewById(R.id.stickyView);
        objSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_contact_list);
        objSwipeRefreshLayout.setColorSchemeResources(R.color.red_background_color);
        expListView = (ExpandableListView) findViewById(R.id.contact_list);
        expListView.setGroupIndicator(null);
        expListView.setChildIndicator(null);
        expListView.setChildDivider(getResources().getDrawable(R.color.contact_list_divider_color));
        expListView.setDivider(getResources().getDrawable(R.color.transparent));
        objRelativeBeforeLoading = (RelativeLayout) findViewById(R.id.relative_request_contact_list);
//        objRelativeAfterLoading = (RelativeLayout) findViewById(R.id.relative_updated_contact_list);
        objImageProgress = (ImageView) findViewById(R.id.loading_progress);
        btnGiveThemShout = (Button) findViewById(R.id.btn_give_them_a_shout);
//        objLinearTopView = (LinearLayout) findViewById(R.id.linear_invite_contact_screen_top_view);

        /* Inflate list header layout */
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listHeader = inflater.inflate(R.layout.list_header, null);
        stickyViewSpacer = listHeader.findViewById(R.id.stickyViewPlaceholder);
        heroImageView = listHeader.findViewById(R.id.linear_invite_contact_screen_top_view);
        progressBar = (ProgressBar) listHeader.findViewById(R.id.progressBar);
        btnSkipAddingShouts = (Button) listHeader.findViewById(R.id.btn_skip_invite_friends);
        /* Add list view header */
        expListView.addHeaderView(listHeader);

        if (isNewUser) {
            objSwipeRefreshLayout.setVisibility(SwipeRefreshLayout.GONE);
            objRelativeBeforeLoading.setVisibility(RelativeLayout.VISIBLE);
            objImageProgress.setVisibility(ImageView.VISIBLE);
            Animation sampleFadeAnimation = AnimationUtils.loadAnimation(InviteFriendsActivity.this, R.anim.rotating_progress);
            objImageProgress.startAnimation(sampleFadeAnimation);
            Handler objHandler = new Handler();
            objHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setLocalData();
                }
            }, 3000);
        } else {
            objSwipeRefreshLayout.setVisibility(SwipeRefreshLayout.VISIBLE);
            objRelativeBeforeLoading.setVisibility(RelativeLayout.GONE);
            objImageProgress.setVisibility(ImageView.GONE);
            setLocalData();
        }



        /* Handle list View scroll events */
        expListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                /* Check if the first item is already reached to top.*/

                if (expListView.getFirstVisiblePosition() == 0) {
                    View firstChild = expListView.getChildAt(0);
                    int topY = 0;
                    if (firstChild != null) {
                        topY = firstChild.getTop();
                    }
                    int heroTopY = stickyViewSpacer.getTop();
                    stickyView.setY(Math.max(0, heroTopY + topY));
                    /* Set the image to scroll half of the amount that of ListView */
                    heroImageView.setY(topY * 0.4f);

                    if (topY == 0) {
                        objSwipeRefreshLayout.setEnabled(true);
                    } else {
                        objSwipeRefreshLayout.setEnabled(false);
                    }
                }
            }
        });

        objSwipeRefreshLayout.setOnRefreshListener(this);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (btnGiveThemShout.getVisibility() == Button.GONE) {
                    KeyboardUtils.hideKeyboard(v, im);
                } else {
                    if (parent.isGroupExpanded(groupPosition)) {
                        parent.collapseGroup(groupPosition);
                    } else {
                        parent.expandGroup(groupPosition);
                    }
                }
                return true;
            }
        });

      /*  expListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    objSwipeRefreshLayout.setEnabled(true);
                } else {
                    objSwipeRefreshLayout.setEnabled(false);
                }
            }
        });*/

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        objEditTextSearchFilter = (EditText) findViewById(R.id.edt_search_invite_friends);
        objEditTextSearchFilter.setHint("Search Address Book");
        objEditTextSearchFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listAdapter.filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {

                    /*if (s.length() == 0) {
                        listAdapter = new ContactExpandableListAdapter(InviteFriendsActivity.this, InviteFriendsActivity.this, continentList);
                        expListView.setAdapter(listAdapter);
                    } else {
                        listAdapter.filterData(s.toString());
                    }*/
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /*objEditTextSearchFilter.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        objEditTextSearchFilter.setIconifiedByDefault(false);
        objEditTextSearchFilter.setOnQueryTextListener(this);
        objEditTextSearchFilter.setOnCloseListener(this);*/

        /*LinearLayout ll = (LinearLayout) objEditTextSearchFilter.getChildAt(0);
        LinearLayout ll2 = (LinearLayout) ll.getChildAt(2);
        LinearLayout ll3 = (LinearLayout) ll2.getChildAt(1);
        SearchView.SearchAutoComplete autoComplete = (SearchView.SearchAutoComplete) ll3.getChildAt(0);
        autoComplete.setHintTextColor(Color.GRAY);
        autoComplete.setTextSize(13);
        autoComplete.setTextColor(Color.BLACK);*/


        setListener();
    }

    private void setListener() {
        btnSkipAddingShouts.setOnClickListener(this);
        btnGiveThemShout.setOnClickListener(this);
        objEditTextSearchFilter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("EDITTEXT TOUCH CALLED");
                expListView.smoothScrollToPosition(9);
                objEditTextSearchFilter.requestFocus();
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_skip_invite_friends:
                try {
                    if (progressBar.isShown()) {
                        Toast.makeText(this, "Contacts syncing", Toast.LENGTH_SHORT).show();
                    } else {
                        getFacebookFriends();
                    }
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case R.id.btn_give_them_a_shout:
                if (btnGiveThemShout.getText().equals("INVITE TO SHOUTIN")) {
                    ArrayList<String> arrSelectedContactNumbers = new ArrayList<String>();
                    for (int index = 0; index < NonFriendList.size(); index++) {
                        ContactModel objContactModel = NonFriendList.get(index);
                        if (objContactModel.getCheckBokChecked()) {
                            arrSelectedContactNumbers.add(objContactModel.getContactNumber());
                        }
                    }
                    if (arrSelectedContactNumbers.size() > 0) {
                        String toNumbers = "";
                        for (String number : arrSelectedContactNumbers) {
                            toNumbers = toNumbers + number + ";";
                        }
                        // TO REMOVE LAST SEMI COLON FROM STRING
                        toNumbers = toNumbers.substring(0, toNumbers.length() - 1);
                        System.out.println("SELECTED CONTACTS : " + toNumbers);
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address", toNumbers);
                        smsIntent.putExtra("sms_body", "Checkout shout application for your smartphone. Download it today from. http://shout.com");
                        smsIntent.putExtra(android.content.Intent.EXTRA_PHONE_NUMBER, toNumbers);
                        startActivity(smsIntent);
                    } else {
                        Toast.makeText(InviteFriendsActivity.this, "Please select at least one friend", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent objIntent = new Intent(InviteFriendsActivity.this, ShoutDefaultActivity.class);
                    objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(objIntent);
                    finish();
                    overridePendingTransition(0, 0);
                }
                break;
            default:
                break;
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

    @Override
    public void onRefresh() {
        if (progressBar.isShown()) {
            Toast.makeText(this, "Contacts syncing", Toast.LENGTH_SHORT).show();
        } else {
            objSwipeRefreshLayout.setRefreshing(true);
            getFacebookFriends();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showInternetView(isConnected);
    }

    public class LoadingDataForFirstTime extends AsyncTask<String, Void, String> {

        String strResult = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject objRootJsonObject = new JSONObject();
                JSONArray objContactJsonArray = new JSONArray();
                Hashtable<String, String> objPhoneDirectory = new Hashtable<String, String>();
                ContentResolver cr = InviteFriendsActivity.this.getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            while (pCur.moveToNext()) {
                                int phoneType = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                String phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                switch (phoneType) {
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                        Log.d(name + "(mobile number)", phoneNumber);
                                        arrStrContactName.add(name);
                                        arrStrContactNumber.add(phoneNumber);
                                        objPhoneDirectory.put(phoneNumber.replaceAll("[^+0-9]", ""), name);
                                        /*JSONObject objNewJsonObject = new JSONObject();
                                        objNewJsonObject.put("name", name);
                                        objNewJsonObject.put("phone", phoneNumber);
                                        objContactJsonArray.put(objNewJsonObject);*/
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                        Log.d(name + "(home number)", phoneNumber);
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                        Log.d(name + "(work number)", phoneNumber);
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                        Log.d(name + "(other number)", phoneNumber);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            pCur.close();
                        }
                    }
                }

                for (String key : objPhoneDirectory.keySet()) {
                    System.out.println("PHONE NUMBER : " + key + " NAME : " + objPhoneDirectory.get(key));
                    JSONObject objNewJsonObject = new JSONObject();
                    objNewJsonObject.put("name", objPhoneDirectory.get(key));
                    objNewJsonObject.put("phone", key);
                    objContactJsonArray.put(objNewJsonObject);
                }

                ArrayList<ContactModel> arrContactModel = new ArrayList<ContactModel>();

                arrContactModel = objDatabaseHelper.getAllNonFriendsList();
                for (int i = 0; i < arrContactModel.size(); i++) {
                    ContactModel objContactModel = arrContactModel.get(i);
                    JSONObject objNewJsonObject = new JSONObject();
                    objNewJsonObject.put("name", objContactModel.getContactName());
                    objNewJsonObject.put("phone", objContactModel.getContactNumber());
                    objContactJsonArray.put(objNewJsonObject);
                }

                SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                objRootJsonObject.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
                objRootJsonObject.put("contacts", objContactJsonArray);
                objRootJsonObject.put("facebook", params[0]);
                strResult = NetworkUtils.postData(Constants.FRIENDS_COMPARE_API, objRootJsonObject.toString());
                return strResult;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Utils.d("CONTACT API RESPONSE : ", s);
            objRelativeBeforeLoading.setVisibility(RelativeLayout.GONE);
//            objRelativeAfterLoading.setVisibility(RelativeLayout.VISIBLE);
            objSwipeRefreshLayout.setVisibility(SwipeRefreshLayout.VISIBLE);

            System.out.println("ARRAY NAME:" + arrStrContactName);
            System.out.println("ARRAY NUMBER:" + arrStrContactNumber);

            try {
                JSONObject objJsonObject = new JSONObject(s);

                if (objJsonObject.getString("result").equals("true")) {

                    enableSearchView(objEditTextSearchFilter, true);

                    JSONArray objJsonArray = new JSONArray(objJsonObject.getString("friends"));

                    ArrayList<ContactModel> FriendList = new ArrayList<ContactModel>();
                    NonFriendList.clear();

                    /*for (int index = 0; index < objJsonArray.length(); index++) {
                        if (objJsonArray.getJSONObject(index).getString("is_friend").equals("Y")) {
                            if (objJsonArray.getJSONObject(index).getString("phone").length() >= 10) {
                                ContactModel objContactModel = new ContactModel(
                                        objJsonArray.getJSONObject(index).getString("name"),
                                        objJsonArray.getJSONObject(index).getString("phone"),
                                        objJsonArray.getJSONObject(index).getString("id"),
                                        objJsonArray.getJSONObject(index).getString("is_facebook_friend"),
                                        objJsonArray.getJSONObject(index).getString("is_phone_friend"),
                                        false, 0);
                                FriendList.add(objContactModel);
                            }
                        } else if (objJsonArray.getJSONObject(index).getString("is_friend").equals("N")) {
                            if (objJsonArray.getJSONObject(index).getString("phone").length() >= 10) {
                                ContactModel objContactModel = new ContactModel(
                                        objJsonArray.getJSONObject(index).getString("name"),
                                        objJsonArray.getJSONObject(index).getString("phone"),
                                        objJsonArray.getJSONObject(index).getString("id"),
                                        objJsonArray.getJSONObject(index).getString("is_facebook_friend"),
                                        objJsonArray.getJSONObject(index).getString("is_phone_friend"),
                                        false, 1);
                                NonFriendList.add(objContactModel);
                            }
                        }
                    }

                    Continent continentFriends = new Continent("DISPLAYING YOUR CONTACTS ON SHOUTIN ", FriendList);
                    continentList.add(continentFriends);
                    Continent continentNotFriends = new Continent("DISPLAYING YOUR CONTACTS AWAITED ON SHOUTIN ", NonFriendList);
                    continentList.add(continentNotFriends);
                    listAdapter = new ContactExpandableListAdapter(InviteFriendsActivity.this, continentList);
                    expListView.setAdapter(listAdapter);
                    //expand all Groups
                    expandAll();
                    expListView.setGroupIndicator(null);
                    expListView.setChildIndicator(null);
                    expListView.setChildDivider(getResources().getDrawable(R.color.contact_list_divider_color));
                    expListView.setDivider(getResources().getDrawable(R.color.transparent));*/


                    // SAVING CONTACT JSON FOR FIRST TIME WHILE COMING FROM LOGIN FLOW
                    objDatabaseHelper.dropFriendsTable(DatabaseHelper.strTableNameFriends);

                    objDatabaseHelper.saveFriends(objJsonArray);

                    arrayListFriendList.clear();
                    continentList.clear();
                    NonFriendList.clear();

                    arrayListFriendList = objDatabaseHelper.getAllFriendsList();
                    NonFriendList = objDatabaseHelper.getAllNonFriendsList();

                    /*for (int index = 0; index < objJsonArray.length(); index++) {
                        if (objJsonArray.getJSONObject(index).getString("is_friend").equals("N")) {
                            if (objJsonArray.getJSONObject(index).getString("phone").length() >= 10) {
                                ContactModel objContactModel = new ContactModel(
                                        objJsonArray.getJSONObject(index).getString("name"),
                                        objJsonArray.getJSONObject(index).getString("phone"),
                                        objJsonArray.getJSONObject(index).getString("id"),
                                        objJsonArray.getJSONObject(index).getString("is_facebook_friend"),
                                        objJsonArray.getJSONObject(index).getString("is_phone_friend"),
                                        false, "", 1);
                                NonFriendList.add(objContactModel);
                            }
                        }
                    }*/
                    if (arrayListFriendList.size() == 0) {
                        arrayListFriendList.add(new ContactModel(0, "image", "http://vignette4.wikia.nocookie.net/disney/images/4/4c/Cinderella_Diamond_Edition_Banner_3.jpg", "", "", "", false, "", 0, "", ""));
                        Continent continentFriends = new Continent("DISPLAYING YOUR CONTACTS ON SHOUTIN ", arrayListFriendList);
                        continentList.add(continentFriends);
                    } else {
                        Continent continentFriends = new Continent("DISPLAYING YOUR CONTACTS ON SHOUTIN ", arrayListFriendList);
                        continentList.add(continentFriends);
                    }


                    Continent continentNotFriends = new Continent("DISPLAYING YOUR CONTACTS AWAITED ON SHOUTIN ", NonFriendList);
                    continentList.add(continentNotFriends);
                    listAdapter = new ContactExpandableListAdapter(InviteFriendsActivity.this, InviteFriendsActivity.this, continentList);
                    expListView.setAdapter(listAdapter);
                    //expand all Groups
                    expandAll();
                    expListView.setGroupIndicator(null);
                    expListView.setChildIndicator(null);
                    expListView.setChildDivider(getResources().getDrawable(R.color.contact_list_divider_color));
                    expListView.setDivider(getResources().getDrawable(R.color.transparent));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showProgressBar(boolean show) {
        if (show)
            progressBar.setVisibility(ProgressBar.VISIBLE);
        else
            progressBar.setVisibility(ProgressBar.GONE);
    }

    public class LoadingData extends AsyncTask<String, Void, String> {

        String strResult = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar(true);
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                JSONObject objRootJsonObject = new JSONObject();
                JSONArray objContactJsonArray = new JSONArray();

                Hashtable<String, String> objPhoneDirectory = new Hashtable<String, String>();
                Hashtable<String, String> arrayListContactPhoto = new Hashtable<String, String>();

                ContentResolver cr = InviteFriendsActivity.this.getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            while (pCur.moveToNext()) {
                                int phoneType = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                String phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                switch (phoneType) {
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                        Log.d(name + "(mobile number)", phoneNumber);
                                        arrStrContactName.add(name);
                                        arrStrContactNumber.add(phoneNumber);
                                        /*JSONObject objNewJsonObject = new JSONObject();
                                        objNewJsonObject.put("name", name);
                                        objNewJsonObject.put("phone", phoneNumber);
                                        objContactJsonArray.put(objNewJsonObject);*/

                                        String photoUri = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                                        System.out.println("PHONE PHOTO :" + photoUri);

                                        if (photoUri == null || photoUri.isEmpty()) {
                                            // ADD THE PROTO URI IN THE ARRAY LIST.
                                            arrayListContactPhoto.put(phoneNumber.replaceAll("[^+0-9]", ""), "");

                                        } else {
                                            // IF NO CONTACT PHOTO FOUND IN CONTACT LIST.
                                            arrayListContactPhoto.put(phoneNumber.replaceAll("[^+0-9]", ""), photoUri);
                                        }

                                        objPhoneDirectory.put(phoneNumber.replaceAll("[^+0-9]", ""), name);
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                        Log.d(name + "(home number)", phoneNumber);
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                        Log.d(name + "(work number)", phoneNumber);
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                        Log.d(name + "(other number)", phoneNumber);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            pCur.close();
                        }
                    }
                }

                for (String key : objPhoneDirectory.keySet()) {
                    System.out.println("PHONE NUMBER : " + key + " NAME : " + objPhoneDirectory.get(key));
                    JSONObject objNewJsonObject = new JSONObject();
                    objNewJsonObject.put("name", objPhoneDirectory.get(key));
                    objNewJsonObject.put("phone", key);
                    objNewJsonObject.put(Constants.PROFILE_IMAGE, arrayListContactPhoto.get(key));
                    objContactJsonArray.put(objNewJsonObject);
                }

                SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                objRootJsonObject.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
                objRootJsonObject.put("contacts", objContactJsonArray);
                objRootJsonObject.put("facebook", params[0]);

                Utils.d("CONTACT API INPUT : ", objRootJsonObject.toString());

                strResult = NetworkUtils.postData(Constants.FRIENDS_COMPARE_API, objRootJsonObject.toString());
                return strResult;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            showProgressBar(false);
            System.out.println("ARRAY NAME:" + arrStrContactName);
            System.out.println("ARRAY NUMBER:" + arrStrContactNumber);

            try {

               /* if (objProgressDialog.isShowing()) {
                    objProgressDialog.dismiss();
                }*/

                if (objSwipeRefreshLayout.isShown()) {
                    objSwipeRefreshLayout.setRefreshing(false);
                }

                JSONObject objJsonObject = new JSONObject(s);

                if (objJsonObject.getString("result").equals("true")) {

                    enableSearchView(objEditTextSearchFilter, true);

                    JSONArray objJsonArray = new JSONArray(objJsonObject.getString("friends"));

                    // ArrayList<ContactModel> FriendList = new ArrayList<ContactModel>();

                    arrayListFriendList.clear();
                    continentList.clear();
                    NonFriendList.clear();
                    arrayListFriendList.clear();
                   /* for (int index = 0; index < objJsonArray.length(); index++) {
                        if (objJsonArray.getJSONObject(index).getString("is_friend").equals("N")) {
                            if (objJsonArray.getJSONObject(index).getString("phone").length() >= 10) {
                                ContactModel objContactModel = new ContactModel(
                                        objJsonArray.getJSONObject(index).getString("name"),
                                        objJsonArray.getJSONObject(index).getString("phone"),
                                        objJsonArray.getJSONObject(index).getString("id"),
                                        objJsonArray.getJSONObject(index).getString("is_facebook_friend"),
                                        objJsonArray.getJSONObject(index).getString("is_phone_friend"),
                                        false, "", 1);
                                NonFriendList.add(objContactModel);
                            }
                        }
                    }*/
                    // TODO: 2/9/2016 add Friends to the database for performance loading

                    objDatabaseHelper.dropFriendsTable(DatabaseHelper.strTableNameFriends);

                    objDatabaseHelper.saveFriends(objJsonArray);
                    arrayListFriendList = objDatabaseHelper.getAllFriendsList();
                    NonFriendList = objDatabaseHelper.getAllNonFriendsList();

                    /*Continent continentFriends = new Continent("DISPLAYING YOUR CONTACTS ON SHOUTIN ", arrayListFriendList);
                    continentList.add(continentFriends);*/

                    if (arrayListFriendList.size() == 0) {
                        arrayListFriendList.add(new ContactModel(0, "image", "http://vignette4.wikia.nocookie.net/disney/images/4/4c/Cinderella_Diamond_Edition_Banner_3.jpg", "", "", "", false, "", 0, "", ""));
                        Continent continentFriends = new Continent("DISPLAYING YOUR CONTACTS ON SHOUTIN ", arrayListFriendList);
                        continentList.add(continentFriends);
                    } else {
                        Continent continentFriends = new Continent("DISPLAYING YOUR CONTACTS ON SHOUTIN ", arrayListFriendList);
                        continentList.add(continentFriends);
                    }

                    Continent continentNotFriends = new Continent("DISPLAYING YOUR CONTACTS AWAITED ON SHOUTIN ", NonFriendList);
                    continentList.add(continentNotFriends);
                    listAdapter = new ContactExpandableListAdapter(InviteFriendsActivity.this, InviteFriendsActivity.this, continentList);
                    expListView.setAdapter(listAdapter);
                    //expand all Groups
                    expandAll();
                    expListView.setGroupIndicator(null);
                    expListView.setChildIndicator(null);
                    expListView.setChildDivider(getResources().getDrawable(R.color.contact_list_divider_color));
                    expListView.setDivider(getResources().getDrawable(R.color.transparent));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //method to expand all groups
    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            expListView.expandGroup(i);
        }
    }

    @Override
    public boolean onClose() {
        System.out.println("SEARCH CLOSE");
//        objLinearTopView.setVisibility(LinearLayout.VISIBLE);
        return false;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
//        objLinearTopView.setVisibility(LinearLayout.GONE);
        if (arrayListFriendList.size() > 0 && NonFriendList.size() > 0) {
            listAdapter.filterData(query);
            expandAll();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
//        objLinearTopView.setVisibility(LinearLayout.GONE);
        if (arrayListFriendList.size() > 0 && NonFriendList.size() > 0) {
            listAdapter.filterData(query);
            expandAll();
        }
        return false;
    }

    // TODO: 9/29/2016 BELOW METHOD USED FOR ENABLE AND DISABLE THE SEARCH VIEW. USED WHEN THERE IS NO DATA IN BELOW LIST AT THAT TIME DISABLE SEARCH VIEW ELSE ENABLE IT.
    private void enableSearchView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                enableSearchView(child, enabled);
            }
        }
    }

    public void updateBottomButtonText() {
        int count = 0;
        ArrayList<ContactModel> arrContactModel = new ArrayList<ContactModel>();
        arrContactModel.addAll(continentList.get(1).getArrContactList());
        for (int index = 0; index < arrContactModel.size(); index++) {
            ContactModel objContactModel = arrContactModel.get(index);
            if (objContactModel.getCheckBokChecked() == true) {
                count = count + 1;
            }
        }
        if (count > 0) {
            btnGiveThemShout.setText("INVITE TO SHOUTIN");
        } else {
            btnGiveThemShout.setText("GO TO SHOUTBOARD");
        }
    }

    @Override
    public void onBackPressed() {
        Intent objIntent = new Intent(InviteFriendsActivity.this, ShoutDefaultActivity.class);
        objIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(objIntent);
        finish();
        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
        /*new AlertDialog.Builder(InviteFriendsActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("")
                .setMessage("Do you wish to exit the Shout App ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int pid = android.os.Process.myPid();
                        android.os.Process.killProcess(pid);
                    }
                })
                .setNegativeButton("No", null)
                .show();*/
    }

    public class LoadingContactsForFirstTime extends AsyncTask<String, Void, String> {

        String strResult = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar(true);
            Log.d("PRASANNA", "in preexecute of async");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Log.d("PRASANNA", "IN BACKGROUND OF CONTACT READING");

                ContentResolver cr = InviteFriendsActivity.this.getContentResolver();
                JSONArray objContactJsonArray = new JSONArray();

                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                phones.moveToFirst();

                List<String> tempNames = new ArrayList<String>();

                Log.d("PRASANNA", "CONTACT CURSOR COUNT = " + phones.getCount());
                while (phones.moveToNext()) {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String profileImage = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));


                    if (!tempNames.contains(name)) {
//                        NonFriendList.add(new ContactModel(0, name, phoneNumber, "", "N", "N", false, "", 1, "N", "N"));
//                        NonFriendList.add(new ContactModel(0, name, phoneNumber, "", "N", "N", false, profileImage, 1, "N", "N"));
                        JSONObject objNewJsonObject = new JSONObject();
                        objNewJsonObject.put("name", name);
                        objNewJsonObject.put("phone", phoneNumber);
                        if (profileImage == null)
                            objNewJsonObject.put(Constants.PROFILE_IMAGE, "");
                        else
                            objNewJsonObject.put(Constants.PROFILE_IMAGE, profileImage);
                        objContactJsonArray.put(objNewJsonObject);
                    }
                    tempNames.add(name);
                }
                phones.close();
                // close cursor


                JSONObject objJsonObject = new JSONObject();
                SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                objJsonObject.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
                objJsonObject.put("contacts", objContactJsonArray);
                objJsonObject.put("facebook", params[0]);

                Utils.d("CONTACT API INPUT : ", objJsonObject.toString());

                return NetworkUtils.postData(Constants.FRIENDS_COMPARE_API, objJsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("PRASANNA", "in postexecute of async");
            try {
                showProgressBar(false);
                try {
                    if (objSwipeRefreshLayout.isShown()) {
                        objSwipeRefreshLayout.setRefreshing(false);
                    }

                    Utils.d("CONTACT_API_OUTPUT", s);

                    JSONObject objJsonObject = new JSONObject(s);

                    if (objJsonObject.getString("result").equals("true")) {

                        enableSearchView(objEditTextSearchFilter, true);

                        final JSONArray objJsonArray = new JSONArray(objJsonObject.getString("friends"));

                        // ArrayList<ContactModel> FriendList = new ArrayList<ContactModel>();

                        arrayListFriendList.clear();
                        continentList.clear();
                        NonFriendList.clear();
                        arrayListFriendList.clear();

                        // TODO: 2/9/2016 add Friends to the database for performance loading

                       /*
                        arrayListFriendList = objDatabaseHelper.getAllFriendsList();
                        NonFriendList = objDatabaseHelper.getAllNonFriendsList();*/

                        objDatabaseHelper.dropFriendsTable(DatabaseHelper.strTableNameFriends);

                        Thread objSaveContactsThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                objDatabaseHelper.saveFriends(objJsonArray);
                            }
                        });
                        objSaveContactsThread.start();


                        for (int index = 0; index < objJsonArray.length(); index++) {
                            if (objJsonArray.getJSONObject(index).getString("is_friend").equals("Y")) {
                                if (objJsonArray.getJSONObject(index).getString("phone").length() >= 10) {
                                    ContactModel contactModel = new ContactModel(
                                            0,
                                            objJsonArray.getJSONObject(index).getString("name"),
                                            objJsonArray.getJSONObject(index).getString("phone"),
                                            objJsonArray.getJSONObject(index).getString("id"),
                                            objJsonArray.getJSONObject(index).getString("is_facebook_friend"),
                                            objJsonArray.getJSONObject(index).getString("is_phone_friend"),
                                            false, objJsonArray.getJSONObject(index).getString("profile_image"), 0,
                                            objJsonArray.getJSONObject(index).getString("button_type"),
                                            objJsonArray.getJSONObject(index).getString("is_shout_friend"));
                                    arrayListFriendList.add(contactModel);
                                }
                            } else if (objJsonArray.getJSONObject(index).getString("is_friend").equals("N")) {
                                if (objJsonArray.getJSONObject(index).getString("phone").length() >= 10) {
                                    ContactModel contactModel = new ContactModel(
                                            0,
                                            objJsonArray.getJSONObject(index).getString("name"),
                                            objJsonArray.getJSONObject(index).getString("phone"),
                                            objJsonArray.getJSONObject(index).getString("id"),
                                            objJsonArray.getJSONObject(index).getString("is_facebook_friend"),
                                            objJsonArray.getJSONObject(index).getString("is_phone_friend"),
                                            false, objJsonArray.getJSONObject(index).getString("profile_image"), 1,
                                            objJsonArray.getJSONObject(index).getString("button_type"),
                                            objJsonArray.getJSONObject(index).getString("is_shout_friend"));
                                    NonFriendList.add(contactModel);
                                }
                            }
                        }
                        if (arrayListFriendList.size() == 0) {
                            arrayListFriendList.add(new ContactModel(0, "image", "http://vignette4.wikia.nocookie.net/disney/images/4/4c/Cinderella_Diamond_Edition_Banner_3.jpg", "", "", "", false, "", 0, "", ""));
                            Continent continentFriends = new Continent("DISPLAYING YOUR CONTACTS ON SHOUTIN ", arrayListFriendList);
                            continentList.add(continentFriends);
                        } else {
                            Continent continentFriends = new Continent("DISPLAYING YOUR CONTACTS ON SHOUTIN ", arrayListFriendList);
                            continentList.add(continentFriends);
                        }

                        Continent continentNotFriends = new Continent("DISPLAYING YOUR CONTACTS AWAITED ON SHOUTIN ", NonFriendList);
                        continentList.add(continentNotFriends);
                        listAdapter = new ContactExpandableListAdapter(InviteFriendsActivity.this, InviteFriendsActivity.this, continentList);
                        expListView.setAdapter(listAdapter);
                        //expand all Groups
                        expandAll();
                        expListView.setGroupIndicator(null);
                        expListView.setChildIndicator(null);
                        expListView.setChildDivider(getResources().getDrawable(R.color.contact_list_divider_color));
                        expListView.setDivider(getResources().getDrawable(R.color.transparent));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
