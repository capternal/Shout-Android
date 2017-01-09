package com.shoutin.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.shoutin.CustomClasses.CustomFontTextView;
import com.shoutin.CustomClasses.CustomSnackBarLayout;
import com.shoutin.R;
import com.shoutin.Utils.ApplicationUtils;
import com.shoutin.Utils.CallWebService;
import com.shoutin.Utils.ConnectionDetector;
import com.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shoutin.Utils.Constants;
import com.shoutin.Utils.ImageUtils;
import com.shoutin.Utils.KeyboardUtils;
import com.shoutin.Utils.NetworkUtils;
import com.shoutin.Utils.Utils;
import com.shoutin.app.AppController;
import com.shoutin.database.DatabaseHelper;
import com.shoutin.emojicons.EmojiconEditText;
import com.shoutin.emojicons.EmojiconGridFragment;
import com.shoutin.emojicons.EmojiconsFragment;
import com.shoutin.emojicons.emoji.Emojicon;
import com.shoutin.main.Adapter.ChatArrayAdapter;
import com.shoutin.main.Model.ChatMessage;
import com.shoutin.main.Model.ShoutDefaultListModel;
import com.shoutin.others.CircleTransform;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ChatForShoutActivity extends AppCompatActivity implements View.OnClickListener, KeyboardUtils.SoftKeyboardToggleListener, EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener, ConnectivityBroadcastReceiver.ConnectivityReceiverListener, CallWebService.WebserviceResponse {

    private static final String TAG = "ChatActivity";

    ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EmojiconEditText chatText;
    private Button buttonSend;
    private CustomFontTextView objTextViewApponentUserName;

    private boolean side = false;
    ImageButton objImageButtonBack;
    Button btnOpenHelpMenu;
    Button btnOpenAddResourcePopup;
    SharedPreferences objSharedPreferences;
    private String strLoggedInUserId = "";
    private String strLoggedInUserProfileImageUrl = "";
    ArrayList<ChatMessage> arrChatMessages;
    private ImageView objApponentProfileImage;
    private ImageView objShoutImage;
    private CustomFontTextView objShoutTitle;
    private ImageView objHandShakeImage;
    private ImageView objImageShoutTitleArrow;

    //    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    String strShoutTitle = "";
    String strShoutType = "";
    public static String strShoutOwnerId = "";
    String strIsOwner = "";
    String strApponentUsername = "";
    String strBackScreenName = "";
    public static boolean isHandshakeDoneByMe = false;

    DatabaseHelper objDatabaseHelper;

    Parcelable objChatListState;

    SharedPreferences objChatPreferences;
    /*objDataChatEditor.putString(Constants.CHAT_APPONENT_ID, objSharedPreferences.getString(Constants.USER_ID_FOR_DETAIL_SCREEN, ""));
    objDataChatEditor.putString(Constants.CHAT_APPONENT_USER_NAME, strShoutUsername);
    objDataChatEditor.putString(Constants.CHAT_APPONENT_PROFILE_PIC, strUserProfileUrl);
    objDataChatEditor.putString(Constants.CHAT_SHOUT_ID, strChatShoutId);
    objDataChatEditor.putString(Constants.CHAT_SHOUT_TITLE, strShoutTitle);
    objDataChatEditor.putString(Constants.CHAT_SHOUT_TYPE, strShoutType);
    objDataChatEditor.putString(Constants.CHAT_SHOUT_IMAGE, objSharedPreferences.getString(Constants.SHOUT_SINGLE_IMAGE_FOR_DETAIL, ""));
    objDataChatEditor.putString(Constants.CHAT_BACK, Constants.SHOUT_DEFAULT_SCREEN);*/

    String strChatApponentId = "";
    String strChatApponentUserName = "";
    String strChatApponentUserProfileUrl = "";
    public static String strChatShoutId = "";
    String strChatShoutTitle = "";
    String strChatShoutType = "";
    String strChatShoutImage = "";
    // CHAT POPUP COMPONENTS
    private FrameLayout frameLayout;
    private Activity activity;
    private InputMethodManager inputMethodManager;
    private Animation animation;
    private static boolean isEmojiShowing = false;

    ApplicationUtils applicationUtils;
    private static Uri mCapturedImageURI;
    AppController objAppController;

    // MESSAGE CHATTING STRINGS
    String strINeedIt = "";
    String strICanHelp = "";
    public static String strListingOwnerUser = "";
    public static String strListingUserOwner = "";
    public static String strRequestOwnerUser = "";
    public static String strRequestUserOwner = "";

    public static String strChatListingOwnerUser = "";
    public static String strChatListingUserOwner = "";
    public static String strChatRequestOwnerUser = "";
    public static String strChatRequestUserOwner = "";

    public static String strReplacingText = "##USERNAME##";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_for_shout);
        // STORE ACTIVITY CONTEXT IN CONTEXT VARIABLES
        activity = ChatForShoutActivity.this;
        applicationUtils = new ApplicationUtils(this);
        // CHAT EMOJI COMPONENTS

        objAppController = new AppController();

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        objDatabaseHelper = new DatabaseHelper(ChatForShoutActivity.this);

        System.out.println("ACTIVITY ON CREATE CALLED");

        registerReceiver(this.appendChatScreenMsgReceiver, new IntentFilter("appendChatScreenMsg"));


        // CHAT OTHER DETAILS TAKEN FROM CALLED ACTIVITY
        objChatPreferences = getSharedPreferences(Constants.CHAT_PREFERENCES, MODE_PRIVATE);
        strChatApponentId = objChatPreferences.getString(Constants.CHAT_APPONENT_ID, "");
        strChatApponentUserName = objChatPreferences.getString(Constants.CHAT_APPONENT_USER_NAME, "");
        strChatApponentUserProfileUrl = objChatPreferences.getString(Constants.CHAT_APPONENT_PROFILE_PIC, "");
        strChatShoutId = objChatPreferences.getString(Constants.CHAT_SHOUT_ID, "");
        strApponentUsername = objChatPreferences.getString(Constants.CHAT_APPONENT_USER_NAME, "");

        strChatShoutTitle = objChatPreferences.getString(Constants.CHAT_SHOUT_TITLE, "");
        strChatShoutType = objChatPreferences.getString(Constants.CHAT_SHOUT_TYPE, "");
        strChatShoutImage = objChatPreferences.getString(Constants.CHAT_SHOUT_IMAGE, "");
        strBackScreenName = objChatPreferences.getString(Constants.CHAT_BACK, "");

        // LOGGED IN USER DETAILS PREFERENCES
        objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
        strLoggedInUserId = objSharedPreferences.getString(Constants.USER_ID, "");
        strLoggedInUserProfileImageUrl = objSharedPreferences.getString(Constants.PROFILE_IMAGE_URL, "");
//        strShoutType = objSharedPreferences.getString(Constants.CHAT_SHOUT_TYPE, "");
        strShoutType = strChatShoutType;

        // CALLED SHOUT DETAIL API
        try {
            JSONObject objJsonObject = new JSONObject();
            objJsonObject.put("shout_id", strChatShoutId);
            if (objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, "").isEmpty() && objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, "").isEmpty()) {
                objJsonObject.put("latitude", objSharedPreferences.getString(Constants.USER_REGISTERED_LATITUDE, ""));
                objJsonObject.put("longitude", objSharedPreferences.getString(Constants.USER_REGISTERED_LONGITUDE, ""));
            } else {
                objJsonObject.put("latitude", objSharedPreferences.getString(Constants.USER_SEARCHED_LATITUDE, ""));
                objJsonObject.put("longitude", objSharedPreferences.getString(Constants.USER_SEARCHED_LONGITUDE, ""));
            }
            objJsonObject.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
            new CallWebService(Constants.SHOUT_DETAIL_API, objJsonObject, ChatForShoutActivity.this, ChatForShoutActivity.this, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("DATA SHOUT ID : " + strChatShoutId);
        System.out.println("DATA FROM ID : " + strLoggedInUserId);
        System.out.println("DATA FROM PROFILE URL : " + strLoggedInUserProfileImageUrl);
        System.out.println("DATA APPONENT ID : " + strChatApponentId);
        System.out.println("DATA APPONENT UserName : " + strApponentUsername);
        System.out.println("DATA TO PROFILE URL : " + strChatApponentUserProfileUrl);
        System.out.println("DATA TO SHOUT TYPE : " + strShoutType);


//        new GetOldMessagesAPI().execute();
        new CallWebService(Constants.MESSAGE_SETTINGS_API, new JSONObject(), this, this, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        makeActive(strLoggedInUserId, strChatApponentId);
        arrChatMessages = new ArrayList<ChatMessage>();
        chatArrayAdapter = new ChatArrayAdapter(ChatForShoutActivity.this, arrChatMessages);
        initializeView();

        // SAVING STATE OF LISTVIEW FOR INITIAL
        objChatListState = listView.onSaveInstanceState();

        // SET STATIC DATA
        objShoutTitle.setText(strChatShoutTitle);
        objShoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushToDetailScreen(strChatShoutId);
            }
        });
        objShoutTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushToDetailScreen(strChatShoutId);
            }
        });
        objImageShoutTitleArrow.setVisibility(ImageView.VISIBLE);
        if (ConnectivityBroadcastReceiver.isConnected()) {
            if (strChatShoutImage.equals("")) {
                Picasso.with(ChatForShoutActivity.this).load(R.drawable.app_icon).placeholder(R.drawable.app_icon_circle).error(R.drawable.app_icon_circle).noFade().transform(new CircleTransform()).into(objShoutImage);
            } else {
                Picasso.with(ChatForShoutActivity.this).load(strChatShoutImage).placeholder(R.drawable.app_icon_circle).error(R.drawable.app_icon_circle).noFade().transform(new CircleTransform()).into(objShoutImage);
            }
        } else {
            if (strChatShoutImage.equals("")) {
                Picasso.with(ChatForShoutActivity.this).load(R.drawable.app_icon).placeholder(R.drawable.app_icon_circle).error(R.drawable.app_icon_circle).noFade().transform(new CircleTransform()).into(objShoutImage);
            } else {
                Picasso.with(ChatForShoutActivity.this).load(strChatShoutImage).placeholder(R.drawable.app_icon_circle).error(R.drawable.app_icon_circle).noFade().networkPolicy(NetworkPolicy.OFFLINE).transform(new CircleTransform()).into(objShoutImage);
            }
        }
        objShoutImage.setPadding(Constants.CHAT_SCREEN_SHOUT_IMAGE_BORDER_WIDTH,
                Constants.CHAT_SCREEN_SHOUT_IMAGE_BORDER_WIDTH,
                Constants.CHAT_SCREEN_SHOUT_IMAGE_BORDER_WIDTH,
                Constants.CHAT_SCREEN_SHOUT_IMAGE_BORDER_WIDTH);
        objTextViewApponentUserName.setText(strChatApponentUserName);
//        objApponentProfileImage.setImageUrl(strChatApponentUserProfileUrl, imageLoader);
        try {
            if ("".equals(strChatApponentUserProfileUrl))
                Picasso.with(ChatForShoutActivity.this).load(R.drawable.default_profile).placeholder(R.drawable.default_profile).error(R.drawable.default_profile).noFade().transform(new CircleTransform()).into(objApponentProfileImage);
            else
                Picasso.with(ChatForShoutActivity.this).load(strChatApponentUserProfileUrl).placeholder(R.drawable.default_profile).error(R.drawable.default_profile).noFade().transform(new CircleTransform()).into(objApponentProfileImage);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadLocalChatMessages();


        if (ConnectivityBroadcastReceiver.isConnected()) {
            new MessageReadAPI().execute();
        }
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });
    }

    public void loadLocalChatMessages() {
        try {
            arrChatMessages = objDatabaseHelper.getLastMessages(strChatShoutId, strChatApponentId);
            if (arrChatMessages.size() > 0) {
                chatArrayAdapter = new ChatArrayAdapter(ChatForShoutActivity.this, arrChatMessages);
                listView.setAdapter(chatArrayAdapter);
                chatArrayAdapter.notifyDataSetChanged();
                listView.setSelection(chatArrayAdapter.getCount());
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pushToDetailScreen(String shoutId) {
        Intent objIntent = new Intent(ChatForShoutActivity.this, ShoutDetailActivity.class);
//        objIntent.putExtra("SHOUT_IMAGES", objShoutModel.getStrShoutImages());
//        objIntent.putExtra("COMMENT_COUNT", objShoutModel.getCOMMENT_COUNT());
        startActivity(objIntent);
        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);

        SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor objEditor = objSharedPreferences.edit();
        objEditor.putString(Constants.SHOUT_ID_FOR_DETAIL_SCREEN, shoutId);
        objEditor.commit();
    }

    // CODE FOR EMOJICONS FOR CHAT

    /**
     * Set the Emoticons in Fragment.
     *
     * @param useSystemDefault
     */
    private void setEmojiconFragment(boolean useSystemDefault) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, EmojiconsFragment.newInstance(useSystemDefault))
                .commitAllowingStateLoss();
        System.out.println("FRAGMENT SET TO FRAME LAYOUT");
    }

    /**
     * It called, when click on icon of Emoticons.
     *
     * @param emojicon
     */
    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(chatText, emojicon);
    }

    /**
     * It called, when backspace button of Emoticons pressed
     *
     * @param view
     */
    @Override
    public void onEmojiconBackspaceClicked(View view) {

        EmojiconsFragment.backspace(chatText);
    }

    @Override
    public void onToggleSoftKeyboard(boolean isVisible) {
        Log.d("keyboard", "keyboard visible: " + isVisible);
        if (isVisible) {
            showEmojiFrameLayout(false);
            /*showKeyboard(false, chatText);*/
        }

        /* else {
            Handler objHandler = new Handler();
            objHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showEmojiFrameLayout(true);
                }
            }, 300);*/
    }


    private void showEmojiFrameLayout(boolean show) {
        if (show) {
            isEmojiShowing = true;
            System.out.println("if : Show Framelayout called " + show);
//            showKeyboard(false, chatText);
            //frameLayout.startAnimation(animation);
            frameLayout.setVisibility(FrameLayout.VISIBLE);
        } else {
            isEmojiShowing = false;
            System.out.println(" else : Show Framelayout called " + show);
            frameLayout.setVisibility(FrameLayout.GONE);
        }
    }

    //

    private void initializeView() {
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
//        animation = AnimationUtils.makeInChildBottomAnimation(activity);
        animation = AnimationUtils.loadAnimation(activity, R.anim.bottom_up);

        frameLayout.setAnimation(animation);

        KeyboardUtils.addKeyboardToggleListener(ChatForShoutActivity.this, this);
        objTextViewApponentUserName = (CustomFontTextView) findViewById(R.id.txt_screen_shout_chat_screen_title);

        objImageButtonBack = (ImageButton) findViewById(R.id.image_button_shout_users_chat_back);
        btnOpenAddResourcePopup = (Button) findViewById(R.id.chat_open_media);
        btnOpenHelpMenu = (Button) findViewById(R.id.chat_help);

        objApponentProfileImage = (ImageView) findViewById(R.id.static_apponent_profile_image);
        objShoutImage = (ImageView) findViewById(R.id.image_view_shout_pic);
        objShoutTitle = (CustomFontTextView) findViewById(R.id.txt_chat_shout_title);
        objHandShakeImage = (ImageView) findViewById(R.id.image_hand_shake);
        objImageShoutTitleArrow = (ImageView) findViewById(R.id.image_chat_shout_title_arrow);

        objHandShakeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Use your own attributes to track content views in your app
                SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                Answers.getInstance().logCustom(new CustomEvent(Constants.EVENT_USER_HANDSHALKE)
                        .putCustomAttribute("Status", "Handshake")
                        .putCustomAttribute("User Id", objSharedPreferences.getString(Constants.USER_ID, ""))
                        .putCustomAttribute("Username", objSharedPreferences.getString(Constants.USER_NAME, ""))
                        .putCustomAttribute("Facebook Id", objSharedPreferences.getString(Constants.USER_FACEBOOK_ID, ""))
                        .putCustomAttribute("Email Id", objSharedPreferences.getString(Constants.PROFILE_EMAIL_ID, ""))
                );

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                 /*strINeedIt = objJsonSettings.getString("I_NEED_IT").toString();
                    strICanHelp = objJsonSettings.getString("I_CAN_HELP").toString();
                    strListingOwnerUser = objJsonSettings.getString("LISTING_OWNER_USER").toString();
                    strListingUserOwner = objJsonSettings.getString("LISTING_USER_OWNER").toString();
                    strRequestOwnerUser = objJsonSettings.getString("REQUEST_OWNER_USER").toString();
                    strRequestUserOwner = objJsonSettings.getString("REQUEST_USER_OWNER").toString();*/

                if (strINeedIt.length() > 0) {
                    if (strShoutType.equals("L")) {
                        System.out.println("SHOUT TYPE : LISTING ");
                        if (strShoutOwnerId.equals(strLoggedInUserId)) {
                            System.out.println("SHOUT OWNER");
                            strIsOwner = "Y";
//                            showDialog("Would you like to share your product with " + objTextViewApponentUserName.getText().toString());
                            showDialog(strListingOwnerUser.replace(strReplacingText, objTextViewApponentUserName.getText().toString()));
                        } else {
                            System.out.println("SHOUT USER");
                            strIsOwner = "N";
//                            showDialog("Would you like to request " + objTextViewApponentUserName.getText().toString() + " to accept and share the product with you.");
                            showDialog(strListingUserOwner.replace(strReplacingText, objTextViewApponentUserName.getText().toString()));
                        }
                    } else {
                        System.out.println("SHOUT TYPE : REQUEST ");
                        if (strShoutOwnerId.equals(strLoggedInUserId)) {
                            strIsOwner = "Y";
                            System.out.println("SHOUT OWNER");
//                            showDialog("Would you like to request " + objTextViewApponentUserName.getText().toString() + " to accept and share the product with you.");
                            showDialog(strRequestOwnerUser.replace(strReplacingText, objTextViewApponentUserName.getText().toString()));
                        } else {
                            strIsOwner = "N";
                            System.out.println("SHOUT USER");
//                            showDialog("Would you like to share your product with " + objTextViewApponentUserName.getText().toString());
                            showDialog(strRequestUserOwner.replace(strReplacingText, objTextViewApponentUserName.getText().toString()));
                        }
                    }
                }


            }
        });

        objImageButtonBack.setOnClickListener(this);
        btnOpenAddResourcePopup.setOnClickListener(this);
        btnOpenHelpMenu.setOnClickListener(this);


        buttonSend = (Button) findViewById(R.id.chat_send);
        listView = (ListView) findViewById(R.id.listview_user_chat);
        chatText = (EmojiconEditText) findViewById(R.id.msg);
        // CHAT EMOTICONS LISTENERS
        chatText.setOnClickListener(this);
        chatText.addTextChangedListener(new TextWatcher() {

            /**
             * This notify that, within s,
             * the count characters beginning at start are about to be replaced by new text with length
             * @param s
             * @param start
             * @param count
             * @param after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * This notify that, somewhere within s, the text has been changed.
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {
            }

            /**
             * This notify that, within s, the count characters beginning at start have just
             * replaced old text that had length
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

//                mTxtEmojicon.setText(s);
            }
        });

        setEmojiconFragment(false);
    }

    public void showDialog(String strMessage) {
        LayoutInflater objPopupInflater;
        View customCategoryAlertLayout;
        final PopupWindow objPopupWindowLargeSource;
        TextView objCancel;
        TextView objOk;
        TextView objMessage;

        objPopupInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        customCategoryAlertLayout = objPopupInflater.inflate(R.layout.hand_shake_alert_layout, null, true);
        objCancel = (TextView) customCategoryAlertLayout.findViewById(R.id.txt_handshake_layout_cancel);
        objOk = (TextView) customCategoryAlertLayout.findViewById(R.id.txt_handshake_layout_ok);
        objMessage = (TextView) customCategoryAlertLayout.findViewById(R.id.txt_handshake_layout_message);

        objPopupWindowLargeSource = new PopupWindow(customCategoryAlertLayout);
        objPopupWindowLargeSource.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        objPopupWindowLargeSource.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        objPopupWindowLargeSource.setFocusable(true);
        objPopupWindowLargeSource.showAtLocation(customCategoryAlertLayout, Gravity.CENTER, 0, 0);

        objMessage.setText(strMessage);

        objCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHandshakeDoneByMe = false;
                strIsOwner = "";
                objPopupWindowLargeSource.dismiss();
            }
        });
        objOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHandshakeDoneByMe = true;
                objPopupWindowLargeSource.dismiss();
                String strMessageType = "";
                if (strShoutType.equals("L")) {
                    strMessageType = "LR";
                } else {
                    strMessageType = "RR";
                }

                System.out.println("APPONENT USER NAME : " + strChatApponentUserName);

                if (ConnectionDetector.isConnectingToInternet(ChatForShoutActivity.this)) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("MMM dd h:m a");
                    String created_date = df.format(c.getTime());
                    arrChatMessages.add(new ChatMessage(
                            side,
                            chatText.getText().toString().trim(),
                            strLoggedInUserProfileImageUrl,
                            created_date,
                            strMessageType,
                            "",
                            null, strChatShoutId,
                            "P",
                            strLoggedInUserId,
                            strChatApponentId,
                            strChatApponentUserName,
                            strShoutTitle,
                            "",
                            ""));
                    // IN CASE OF SENDING TEXT MESSAGE THE LAST PARAMETER i.e. image thumb path should be empty.

                    System.out.println("CHAT ARRAY : " + arrChatMessages);
                    chatArrayAdapter = new ChatArrayAdapter(ChatForShoutActivity.this, arrChatMessages);
                    listView.setAdapter(chatArrayAdapter);
                    chatArrayAdapter.notifyDataSetChanged();
                    listView.setSelection(chatArrayAdapter.getCount());
                    chatText.setText("");
                    new sendMessage().execute("", "0");
                }
            }
        });
    }


    private boolean sendChatMessage() {
        String strTempChatMessage = chatText.getText().toString();
        if (strTempChatMessage.trim().length() > 0) {
            if (ConnectionDetector.isConnectingToInternet(ChatForShoutActivity.this)) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("MMM dd h:m a");

                try {
                    System.out.println("TEXT NORMAL : " + chatText.getText().toString().trim());
                    System.out.println("TEXT ENCODED : " + URLEncoder.encode(chatText.getText().toString().trim(), "UTF-8"));
                    System.out.println("TEXT DECODED : " + URLDecoder.decode(chatText.getText().toString().trim(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String created_date = df.format(c.getTime());
                arrChatMessages.add(new ChatMessage(
                        side,
                        chatText.getText().toString().trim(),
                        strLoggedInUserProfileImageUrl,
                        created_date,
                        "T",
                        "",
                        null, strChatShoutId,
                        "",
                        strLoggedInUserId,
                        strChatApponentId, strChatApponentUserName,
                        strShoutTitle, "",
                        ""));
                // IN CASE OF SENDING TEXT MESSAGE THE LAST PARAMETER i.e. image thumb path should be empty.

                System.out.println("CHAT ARRAY : " + arrChatMessages);
                chatArrayAdapter = new ChatArrayAdapter(ChatForShoutActivity.this, arrChatMessages);
                listView.setAdapter(chatArrayAdapter);
                chatArrayAdapter.notifyDataSetChanged();
                listView.setSelection(chatArrayAdapter.getCount());
                new sendMessage().execute(chatText.getText().toString().trim(), "0");
                chatText.setText("");
            }
        } else {
            chatText.setError("Please enter message");
        }
        return true;
    }

    BroadcastReceiver appendChatScreenMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("BROAD CAST RECEIVER CALLED...");
            Bundle b = intent.getExtras();
            if (b != null) {
                int totalItems = chatArrayAdapter.getCount() - 1;
                if (Integer.parseInt(b.getString("REQUEST_COUNT")) >= 1) {
                    objHandShakeImage.setVisibility(ImageView.GONE);
                } else {
                    objHandShakeImage.setVisibility(ImageView.VISIBLE);
                }
                arrChatMessages.add(new ChatMessage(
                        true,
                        b.getString(Constants.CHAT_MESSAGE),
                        b.getString(Constants.USER_PROFILE_URL_FOR_DETAIL_SCREEN),
                        b.getString(Constants.CHAT_CREATED),
                        b.getString(Constants.CHAT_MESSAGE_TYPE),
                        b.getString(Constants.CHAT_IMAGE_URL),
                        null,
                        b.getString(Constants.SHOUT_ID_FOR_DETAIL_SCREEN),
                        b.getString(Constants.CHAT_IS_PROCESSED),
                        b.getString(Constants.CHAT_FROM_ID),
                        b.getString(Constants.CHAT_TO_ID),
                        b.getString(Constants.CHAT_SCREEN_APPONENT_USER_NAME),
                        strShoutTitle,
                        b.getString("CHAT_ROW_ID"),
                        b.getString("CHAT_THUMB_IMAGE")
                ));
                chatArrayAdapter.notifyDataSetChanged();
                if (chatArrayAdapter != null) {
                    if (listView.getLastVisiblePosition() == totalItems) {
                        chatArrayAdapter.notifyDataSetChanged();
                        listView.setSelection(chatArrayAdapter.getCount());
                    } else {
                        chatArrayAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (b.getString(Constants.CHAT_IS_PROCESSED).equals("P")) {
                        objHandShakeImage.setVisibility(ImageView.GONE);
                    } else {
                        objHandShakeImage.setVisibility(ImageView.VISIBLE);
                    }
                    arrChatMessages.add(new ChatMessage(true,
                            b.getString(Constants.CHAT_MESSAGE),
                            b.getString(Constants.USER_PROFILE_URL_FOR_DETAIL_SCREEN),
                            b.getString(Constants.CHAT_CREATED),
                            b.getString(Constants.CHAT_MESSAGE_TYPE),
                            b.getString(Constants.CHAT_IMAGE_URL),
                            null,
                            b.getString(Constants.SHOUT_ID_FOR_DETAIL_SCREEN),
                            b.getString(Constants.CHAT_IS_PROCESSED),
                            b.getString(Constants.CHAT_FROM_ID),
                            b.getString(Constants.CHAT_TO_ID),
                            b.getString(Constants.CHAT_SCREEN_APPONENT_USER_NAME),
                            strShoutTitle,
                            b.getString("CHAT_ROW_ID"),
                            b.getString("CHAT_THUMB_IMAGE")));
                    listView.setAdapter(chatArrayAdapter);
                    chatArrayAdapter.notifyDataSetChanged();
                    listView.setSelection(chatArrayAdapter.getCount());
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        makeNonActive();
        super.onDestroy();
        System.out.println("ACTIVITY ON DESTROY");
        unregisterReceiver(appendChatScreenMsgReceiver);
    }


    @Override
    public void onClick(View view) {
        Intent objIntent;
        switch (view.getId()) {
            case R.id.image_button_shout_users_chat_back:
//                handleBackEvent();
                onBackPressed();
                break;
            case R.id.chat_open_media:
                OpenMediaOptionPopup();
                break;
            case R.id.chat_help:

                //  YOGA EDITED CODE FOR SHOWING CHAT EMOJICONS
                showKeyboard(false, view);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showEmojiFrameLayout(true);
                    }
                }, 100);


                break;
            case R.id.msg:
                showEmojiFrameLayout(false);
                showKeyboard(true, view);


                break;
            default:
                break;
        }
    }

    private void showKeyboard(boolean show, View view) {
        if (show) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showEmojiFrameLayout(false);
                }
            }, 200);
            inputMethodManager.showSoftInput(view, 0);
            System.out.println("KeyBoard SHOW");

        } else {

            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            System.out.println("KeyBoard HIDE");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // CALLES CONNETIVITY CHECK LISTENER
        objAppController.setConnectivityListener(this);

        objChatPreferences = getSharedPreferences(Constants.CHAT_PREFERENCES, MODE_PRIVATE);
        // LOGGED IN USER DETAILS PREFERENCES
        objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
        System.out.println("ON RESUME  LOGGED IN USER ID : " + objSharedPreferences.getString(Constants.USER_ID, ""));
        System.out.println("ON RESUME  CHAT APPONENT USER ID : " + objChatPreferences.getString(Constants.CHAT_APPONENT_ID, ""));
        makeActive(objSharedPreferences.getString(Constants.USER_ID, ""), objChatPreferences.getString(Constants.CHAT_APPONENT_ID, ""));
        System.out.println("ACTIVITY ON RESUME CALLED");
        if (objChatListState != null) {
            listView.onRestoreInstanceState(objChatListState);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        makeNonActive();
        System.out.println("ACTIVITY ON PAUSE CALLED");
        objChatListState = listView.onSaveInstanceState();
    }

    private void handleBackEvent() {
        Intent objIntent;

        System.out.println("BACK SCREEN ACTIVITY NAME : " + strBackScreenName);

        if (strBackScreenName.equals(Constants.SHOUT_DETAIL_SCREEN)) {
/*            objIntent = new Intent(ChatForShoutActivity.this, ShoutDetailActivity.class);
            startActivity(objIntent);
            finish();
            overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);*/
            onBackPressed();
        } else if (strBackScreenName.equals(Constants.SHOUT_DEFAULT_SCREEN)) {
            /*objIntent = new Intent(ChatForShoutActivity.this, ShoutDefaultActivity.class);
            startActivity(objIntent);
            finish();
            overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);*/
            onBackPressed();
        } else if (strBackScreenName.equals(Constants.SHOUT_MESSAGE_USER_LIST_SCREEN)) {
            /*objIntent = new Intent(ChatForShoutActivity.this, ShoutUsersListActivity.class);
            startActivity(objIntent);
            finish();
            overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);*/
            onBackPressed();
        } else if (strBackScreenName.equals(Constants.SHOUT_MESSAGE_BOARD_SCREEN)) {
            /*objIntent = new Intent(ChatForShoutActivity.this, MessageBoardActivity.class);
            startActivity(objIntent);
            finish();
            overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);*/
            onBackPressed();
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


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showInternetView(isConnected);
    }

    @Override
    public void onWebserviceResponce(String strUrl, String strResult) {
        switch (strUrl) {
            case Constants.MESSAGE_SETTINGS_API:
                try {
                    JSONObject objJsonObject = new JSONObject(strResult);
                    if (objJsonObject.getBoolean("result")) {
                        JSONObject objInnerJsonObject = new JSONObject(objJsonObject.getString("response"));
                        JSONObject objJsonSettings = new JSONObject(objInnerJsonObject.getString("settings"));
                        strINeedIt = objJsonSettings.getString("I_NEED_IT").toString();
                        strICanHelp = objJsonSettings.getString("I_CAN_HELP").toString();
                        strListingOwnerUser = objJsonSettings.getString("LISTING_OWNER_USER").toString();
                        strListingUserOwner = objJsonSettings.getString("LISTING_USER_OWNER").toString();
                        strRequestOwnerUser = objJsonSettings.getString("REQUEST_OWNER_USER").toString();
                        strRequestUserOwner = objJsonSettings.getString("REQUEST_USER_OWNER").toString();

                        strChatListingOwnerUser = objJsonSettings.getString("CHAT_LISTING_OWNER_USER").toString();
                        strChatListingUserOwner = objJsonSettings.getString("CHAT_LISTING_USER_OWNER").toString();
                        strChatRequestOwnerUser = objJsonSettings.getString("CHAT_REQUEST_OWNER_USER").toString();
                        strChatRequestUserOwner = objJsonSettings.getString("CHAT_REQUEST_USER_OWNER").toString();
                    }
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new GetOldMessagesAPI().execute();
                break;
            case Constants.SHOUT_DETAIL_API:

                try {
                    JSONObject objJsonObject = new JSONObject(strResult);

                    if (objJsonObject.getString("result").equals("true")) {
                        JSONObject objNestedJsonObject = new JSONObject(objJsonObject.getString("shout_details"));
                        System.out.println("PRASANNA KESKAR PRINT : USER ID 3 : " + objNestedJsonObject.getString("user_id"));
                        strShoutOwnerId = objNestedJsonObject.getString("user_id");

                    }
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    private class sendMessage extends AsyncTask<String, Void, String> {

        // TO CHECK IS THE WELCOME MESSAGE OR NOT.
        // IF IT IS THEN ON RESPONSE OF API UPDATE Continue_chat count to 1 else 0
        String isWelcomeMessage = "";

        @Override
        protected String doInBackground(String... params) {
            String strResult = "";
            try {
                if (ConnectionDetector.isConnectingToInternet(ChatForShoutActivity.this)) {

                    JSONObject objJsonObject = new JSONObject();
                    objJsonObject.put("from_id", strLoggedInUserId);
                    objJsonObject.put("to_id", strChatApponentId);
                    objJsonObject.put("shout_id", strChatShoutId);
                    objJsonObject.put("message", URLEncoder.encode(params[0].toString(), "UTF-8"));
                    objJsonObject.put("shout_type", strShoutType);
                    objJsonObject.put("chat_start", params[1].toString());
                    isWelcomeMessage = params[1].toString();
                    if (strIsOwner.equals("Y") || strIsOwner.equals("N")) {
                        objJsonObject.put("is_owner", strIsOwner);
                        objJsonObject.put("request_message", "Y");
                        strIsOwner = "";
                        ChatForShoutActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                objHandShakeImage.setVisibility(ImageView.GONE);
                            }
                        });
                    } else {
                        objJsonObject.put("request_message", "N");
                    }
                    strResult = NetworkUtils.postData(Constants.CHAT_SEND_MESSAGE_API, objJsonObject.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject objJsonObject = new JSONObject(s);
                if (objJsonObject.getBoolean("result")) {
                    JSONArray objJsonArray = new JSONArray(objJsonObject.getString("chats"));
                    Utils.d("CHAT_JSON", objJsonArray.toString());
                    objDatabaseHelper.saveChatMessages(strChatShoutId, strChatApponentId, objJsonArray.toString());
                    ShoutDefaultListModel objShoutDefaultListModel = objDatabaseHelper.getShoutDetails(strChatShoutId);
                    objShoutDefaultListModel.setCONTINUE_CHAT("1");
                    objDatabaseHelper.updateShout(objShoutDefaultListModel, strChatShoutId);
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class SendAdminMessage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String strResult = "";
            try {
                if (ConnectionDetector.isConnectingToInternet(ChatForShoutActivity.this)) {
                    JSONObject objJsonObject = new JSONObject();
                    objJsonObject.put("from_id", strLoggedInUserId);
                    objJsonObject.put("to_id", strChatApponentId);
                    objJsonObject.put("shout_id", strChatShoutId);
                    objJsonObject.put("is_owner", strIsOwner);
                    strResult = NetworkUtils.postData(Constants.CHAT_SEND_MESSAGE_API, objJsonObject.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        makeNonActive();
        if (isEmojiShowing) {
            showKeyboard(false, chatText);
            showEmojiFrameLayout(false);
        } else {
            showKeyboard(false, chatText);
            super.onBackPressed();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("ACTIVITY ON RESTART CALLED");

        // CALLES CONNETIVITY CHECK LISTENER
        objAppController.setConnectivityListener(this);
        showInternetView(Constants.internetCheck());

        objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
        strLoggedInUserId = objSharedPreferences.getString(Constants.USER_ID, "");
        strLoggedInUserProfileImageUrl = objSharedPreferences.getString(Constants.PROFILE_IMAGE_URL, "");
        strBackScreenName = objChatPreferences.getString(Constants.CHAT_BACK, "");

        /*strChatApponentId = objSharedPreferences.getString(Constants.USER_ID_FOR_DETAIL_SCREEN, "");
        strChatShoutId = objSharedPreferences.getString(Constants.SHOUT_ID_FOR_DETAIL_SCREEN, "");
        strChatApponentUserProfileUrl = objSharedPreferences.getString(Constants.USER_PROFILE_URL_FOR_DETAIL_SCREEN, "");
        strChatMessage = objSharedPreferences.getString(Constants.CHAT_MESSAGE, "");*/
        makeActive(strLoggedInUserId, strChatApponentId);
        // CALLES CONNETIVITY CHECK LISTENER
        initializeView();
//        new GetOldMessagesAPI().execute();
    }

    private void makeActive(String from_id, String to_id) {
        SharedPreferences objChatPrefrences = getSharedPreferences(Constants.MY_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor objEditor = objChatPrefrences.edit();
        objEditor.putString(Constants.CHAT_SCREEN_ACTIVE, "true");
        objEditor.putString(Constants.CHAT_ACTIVE_FROM_ID, from_id);
        objEditor.putString(Constants.CHAT_ACTIVE_TO_ID, to_id);
        objEditor.commit();
    }


    private void makeNonActive() {
        SharedPreferences objChatPrefrences = getSharedPreferences(Constants.MY_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor objEditor = objChatPrefrences.edit();
        objEditor.putString(Constants.CHAT_SCREEN_ACTIVE, "false");
        objEditor.putString(Constants.CHAT_ACTIVE_FROM_ID, "");
        objEditor.putString(Constants.CHAT_ACTIVE_TO_ID, "");
        objEditor.commit();

    }

    public class MessageReadAPI extends AsyncTask<String, Void, String> {
        String strResult;

        public MessageReadAPI() {
            strResult = "";
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            try {
                System.out.println("SHOUT ID : " + strChatShoutId);
                JSONObject objJsonObject = new JSONObject();
                objJsonObject.put("from_id", strChatApponentId);
                objJsonObject.put("to_id", strLoggedInUserId);
                objJsonObject.put("shout_id", strChatShoutId);
                strResult = NetworkUtils.postData(Constants.CHAT_MESSAGES_READ_API, objJsonObject.toString());
                return strResult;
            } catch (Exception e) {
                e.printStackTrace();
                return strResult;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public class GetOldMessagesAPI extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String strResult = "";
            try {
                JSONObject objJsonObject = new JSONObject();
                objJsonObject.put("user_id", strLoggedInUserId);
                objJsonObject.put("shout_id", strChatShoutId);
                objJsonObject.put("to_id", strChatApponentId);
                return NetworkUtils.postData(Constants.LOAD_OLD_MESSAGES_API, objJsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strResult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject objJsonObject = new JSONObject(s);
                arrChatMessages = new ArrayList<ChatMessage>();
                if (objJsonObject.getString("result").equals("true")) {

                    JSONObject objJsonObjectNested = new JSONObject(objJsonObject.getString("chats"));

                    JSONArray objJsonArray = new JSONArray(objJsonObjectNested.getString("chats_detail"));

                    System.out.println("CHAT JSONARRAY SIZE : " + objJsonArray.length());

                    if (objJsonArray.length() == 0) {
                        String strTempChatMessage = "";
                        System.out.println("PRASAD PRINT : SHOUT ID : " + strShoutType);
                        if (strShoutType.equals("R")) {
                            // I_NEED_IT
                            strTempChatMessage = strINeedIt.replace(strReplacingText, strApponentUsername);
//                            strTempChatMessage = "Hello " + strApponentUsername + " ,\n I want to help you.";
                            System.out.println(" IN R : Shout Type : " + strShoutType + ",\n Apponent Name :" + strApponentUsername);
                        } else if (strShoutType.equals("L")) {
                            // I_CAN_HELP
                            strTempChatMessage = strICanHelp.replace(strReplacingText, strApponentUsername);
//                            strTempChatMessage = "Hello " + strApponentUsername + " ,\n I need help from you";
                            System.out.println(" IN L : Shout Type : " + strShoutType + ",\n Apponent Name :" + strApponentUsername);
                        }
                        System.out.println("Shout Type : " + strShoutType + ",\n Apponent Name :" + strApponentUsername);
                        System.out.println("SHOUT TEMP MESSAGE :" + strTempChatMessage);

                        if (strTempChatMessage.trim().length() > 0) {
                            if (ConnectionDetector.isConnectingToInternet(ChatForShoutActivity.this)) {
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("MMM dd h:m a");
                                String created_date = df.format(c.getTime());
                                arrChatMessages.add(new ChatMessage(
                                        side,
                                        strTempChatMessage,
                                        strLoggedInUserProfileImageUrl,
                                        created_date,
                                        "T",
                                        "",
                                        null, strChatShoutId,
                                        "",
                                        strLoggedInUserId,
                                        strChatApponentId, strChatApponentUserName,
                                        strShoutTitle, "", "chat_large_image"));
                                System.out.println("CHAT ARRAY : " + arrChatMessages);
                                chatArrayAdapter = new ChatArrayAdapter(ChatForShoutActivity.this, arrChatMessages);
                                listView.setAdapter(chatArrayAdapter);
                                chatArrayAdapter.notifyDataSetChanged();
                                listView.setSelection(chatArrayAdapter.getCount());

                                // IN BELOW API CALL, THE SECOND PARAMETER IS FOR CHAT_START IDENTIFICATION.
                                // IT IS '1' ONLY IF THERE IS NO INTERACTION BETWEEN BOTH THE USER>.
                                // AND SO ON THERE WILL BE SEND DEFAULT WELCOME MESSAGE TO THE APPONENT USER.
                                new sendMessage().execute(strTempChatMessage, "1");
                            }
                        } else {
                            chatText.setError("Please enter message");
                        }
                    } else {
                        for (int index = 0; index < objJsonArray.length(); index++) {
                            ChatMessage objChatMessage = new ChatMessage(
                                    Boolean.parseBoolean(objJsonArray.getJSONObject(index).getString("side")),
                                    objJsonArray.getJSONObject(index).getString("message"),
                                    objJsonArray.getJSONObject(index).getString("profile_url"),
                                    objJsonArray.getJSONObject(index).getString("timeformat"),
                                    objJsonArray.getJSONObject(index).getString("message_type"),
                                    objJsonArray.getJSONObject(index).getString("image_path"),
                                    null,
                                    objJsonArray.getJSONObject(index).getString("shout_id"),
                                    objJsonArray.getJSONObject(index).getString("is_processed"),
                                    objJsonArray.getJSONObject(index).getString("from_id"),
                                    objJsonArray.getJSONObject(index).getString("to_id"),
                                    objJsonArray.getJSONObject(index).getString("name"),
                                    strShoutTitle, objJsonArray.getJSONObject(index).getString("chat_id"), objJsonArray.getJSONObject(index).getString("image_thumb_path")
                            );
                            arrChatMessages.add(objChatMessage);
                        }
                        if (Integer.parseInt(objJsonObjectNested.getString("send_request")) >= 1) {
                            objHandShakeImage.setVisibility(ImageView.GONE);
                        } else {
                            objHandShakeImage.setVisibility(ImageView.VISIBLE);
                        }

                        chatArrayAdapter = new ChatArrayAdapter(ChatForShoutActivity.this, arrChatMessages);
                        listView.setAdapter(chatArrayAdapter);
                        chatArrayAdapter.notifyDataSetChanged();
                        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                        listView.setAdapter(chatArrayAdapter);

                        System.out.println("OLD CHAT SHOUT ID : " + strChatShoutId);
                        System.out.println("OLD CHAT APPONENT ID : " + strChatApponentId);
                        Utils.d("OLD_HISTORY_DATA", objJsonArray.toString());
                        objDatabaseHelper.saveChatMessages(strChatShoutId, strChatApponentId, objJsonArray.toString());

                        //TO SCROLL LISTVIEW TO BOTTOM ON DATA CHANGE
                        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
                            @Override
                            public void onChanged() {
                                super.onChanged();
                                listView.setSelection(chatArrayAdapter.getCount() - 1);
                            }
                        });
                    }
                }
            } catch (NullPointerException n) {
                n.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // SELECT MEDIA OPTION POPUP

    private void OpenMediaOptionPopup() {
        // POPUP WINDOWS OBJECTS FOR SHOWING USER CHOOSED RESOURCES
        LayoutInflater objPopupInflater;
        View customCategoryAlertLayout;
        final PopupWindow objPopupWindowCategories;
        ImageView objImageViewVideo;
        ImageView objImageViewPhoto;
        ImageView objImageViewGallery;
        RelativeLayout objRelativeCategoryPopupOutsideTouch;

        objPopupInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        customCategoryAlertLayout = objPopupInflater.inflate(R.layout.capture_shoot_popup, null, true);
        objRelativeCategoryPopupOutsideTouch = (RelativeLayout) customCategoryAlertLayout.findViewById(R.id.relative_shoot_out_side_touch);
        objImageViewVideo = (ImageView) customCategoryAlertLayout.findViewById(R.id.imageViewVideo);
        objImageViewPhoto = (ImageView) customCategoryAlertLayout.findViewById(R.id.imageViewPhoto);
        objImageViewGallery = (ImageView) customCategoryAlertLayout.findViewById(R.id.imageViewGallery);

        objPopupWindowCategories = new PopupWindow(customCategoryAlertLayout);
        objPopupWindowCategories.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        objPopupWindowCategories.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        objPopupWindowCategories.setFocusable(true);

        objPopupWindowCategories.showAtLocation(customCategoryAlertLayout, Gravity.CENTER, 0, 0);

        objRelativeCategoryPopupOutsideTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objPopupWindowCategories.dismiss();
            }
        });

        objImageViewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objPopupWindowCategories.dismiss();
                try {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    Uri fileUri = getOutputMediaFileUri(CreateShoutActivity.MEDIA_TYPE_VIDEO);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    startActivityForResult(intent, Constants.REQUEST_MADE_FOR_VIDEO);
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        objImageViewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objPopupWindowCategories.dismiss();
                try {
                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "CRASH_" + System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                    System.out.println("APPLICATION PATH IN LAUNCH CAMERA :" + Constants.APPLICATION_PATH);
                    ChatForShoutActivity.mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, ChatForShoutActivity.mCapturedImageURI);
                    startActivityForResult(intentCamera, Constants.REQUEST_MADE_FOR_CAMERA);
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        objImageViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objPopupWindowCategories.dismiss();
                try {
                    Intent intentGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intentGallery, Constants.REQUEST_MADE_FOR_GALLERY);
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        // Check that the SDCard is mounted
        File mediaStorageDir = new File(Constants.APPLICATION_PATH);
        // Create the storage directory(MyCameraVideo) if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.");
                return null;
            }
        }
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date.getTime());
        File mediaFile;
        if (type == CreateShoutActivity.MEDIA_TYPE_VIDEO) {
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // After camera screen this code will excuted
        if (requestCode == Constants.REQUEST_MADE_FOR_VIDEO) {
            try {
                if (resultCode == RESULT_OK) {
                    System.out.println("VIDEO SAVED TO PATH : " + data.getData());
//                    setResourceToView(data.getData(), "V");
                } else if (resultCode == RESULT_CANCELED) {
                    // USER CANCELLED
                } else {
                    // FAILED TO CAPTURE VIDEO
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == Constants.REQUEST_MADE_FOR_CAMERA) {
            cameraIntent();
        } else if (requestCode == Constants.REQUEST_MADE_FOR_GALLERY) {
            galleryIntent(data);
        }
    }

    private void galleryIntent(Intent data) {
       /* try {
            String realPath;
            // SDK < API11
            if (Build.VERSION.SDK_INT < 11)
                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
                // SDK >= 11 && SDK < 19
            else if (Build.VERSION.SDK_INT < 19)
                realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
                // SDK > 19 (Android 4.4)
            else
                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
            System.out.println("CHAT ARRAY SIZE : " + arrChatMessages.size());
//          new ImageCompressionAsyncTask(true, Uri.parse(realPath), arrChatMessages.size()).execute();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        try {
            final Uri uri = data.getData();
            ChatForShoutActivity.ImageCompressionAsyncTask imageCompressionAsyncTaskCamera = new ChatForShoutActivity.ImageCompressionAsyncTask() {
                @Override
                protected void onPostExecute(final byte[] imageBytes) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new UploadResource(arrChatMessages.size()).execute(String.valueOf(applicationUtils.saveImageOnSdCard(imageBytes)), "I");
                            } catch (NullPointerException ne) {
                                ne.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            imageCompressionAsyncTaskCamera.execute(applicationUtils.getRealPathFromURI(uri));// imagePath as a string
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void cameraIntent() {
        System.out.println("CHAT ARRAY SIZE : " + arrChatMessages.size());
//        new ImageCompressionAsyncTask(false, data.getData(), arrChatMessages.size()).execute();
        try {
            Uri uri = null;
            ChatForShoutActivity.ImageCompressionAsyncTask imageCompressionAsyncTaskCamera = new ChatForShoutActivity.ImageCompressionAsyncTask() {
                @Override
                protected void onPostExecute(final byte[] imageBytes) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new UploadResource(arrChatMessages.size()).execute(String.valueOf(applicationUtils.saveImageOnSdCard(imageBytes)), "I");
                            } catch (NullPointerException ne) {
                                ne.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            imageCompressionAsyncTaskCamera.execute(applicationUtils.getRealPathFromURI(mCapturedImageURI));// imagePath as a string
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {
        private boolean fromGallery;
        private Uri imageUri;
        int intNewUploadCellPosition;

        public ImageCompressionAsyncTask(boolean fromGallery, Uri imageUri, int position) {
            this.fromGallery = fromGallery;
            this.imageUri = imageUri;
            intNewUploadCellPosition = position;
        }

        @Override
        protected String doInBackground(String... params) {
            String filePath = "";
            if (fromGallery) {
                filePath = compressImage(fromGallery, String.valueOf(imageUri));
                System.out.println("FILE PATH GALLEY : " + filePath);
            } else {
                filePath = compressImage(fromGallery, String.valueOf(imageUri));
                System.out.println("FILE PATH PHOTO : " + filePath);
            }
            return filePath;
        }

        private String getRealPathFromURI(String contentURI) {
            Uri contentUri = Uri.parse(contentURI);
            Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) {
                return contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return cursor.getString(idx);
            }
        }


        public String compressImage(boolean fromGallery, String imageUri) {

            String filePath = "";
            if (fromGallery) {
                filePath = imageUri;
                System.out.println("FROM GALLERY PATH : " + filePath);
            } else {
                filePath = getRealPathFromURI(imageUri);
                System.out.println("FROM CAMERA PATH : " + filePath);
            }

            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            System.out.println("PRASAD PRINT FILE PATH : " + filePath);
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;
                }
            }

//            options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out = null;
            String filename = getFilename();
            try {
                out = new FileOutputStream(filename);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return filename;
        }

        public String getFilename() {
            File file = new File(Environment.getExternalStorageDirectory().getPath(), "Shout/Images");
            if (!file.exists()) {
                file.mkdirs();
            }
            String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
            return uriSting;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("FINAL IMAGE PATH : " + result);
            new UploadResource(intNewUploadCellPosition).execute(result, "I");
        }
    }*/

    public class UploadResource extends AsyncTask<String, Void, String> {

        int intChatCellPosition;

        public UploadResource(int intChatCellPosition) {
            this.intChatCellPosition = intChatCellPosition;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String strResult = "";
            try {
                final Uri resourcePath = Uri.parse(params[0]);
                final String strResourceType = params[1];

                System.out.println("INPUT RESOURCE PATH : " + resourcePath + "TYPE : " + strResourceType);

                ChatForShoutActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (strResourceType.equals("I")) {
                            if (resourcePath != null) {
                                if (ConnectionDetector.isConnectingToInternet(ChatForShoutActivity.this)) {
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat df = new SimpleDateFormat("MMM dd h:m a");
                                    String created_date = df.format(c.getTime());
                                    arrChatMessages.add(new ChatMessage(
                                            side,
                                            "",
                                            strLoggedInUserProfileImageUrl,
                                            created_date,
                                            "I",
                                            "",
                                            resourcePath,
                                            strChatShoutId,
                                            "",
                                            strLoggedInUserId,
                                            strChatApponentId,
                                            strChatApponentUserName,
                                            strShoutTitle, "", ""));
                                    System.out.println("CHAT IMAGE ARRAY : " + arrChatMessages);
                                    chatArrayAdapter = new ChatArrayAdapter(ChatForShoutActivity.this, arrChatMessages);
                                    listView.setAdapter(chatArrayAdapter);
                                    chatArrayAdapter.notifyDataSetChanged();
                                    listView.setSelection(chatArrayAdapter.getCount());
                                    chatText.setText("");
                                }
                            }
                        }
                    }
                });

                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(Constants.CHAT_SEND_PHOTO_API);
                    HttpResponse response = null;
                    MultipartEntity entityBuilder = new MultipartEntity();

                    entityBuilder.addPart("from_id", new StringBody(strLoggedInUserId));
                    entityBuilder.addPart("to_id", new StringBody(strChatApponentId));
                    entityBuilder.addPart("shout_id", new StringBody(strChatShoutId));
                    entityBuilder.addPart("message_type", new StringBody(strResourceType));

                    System.out.println("DATA FROM ID :" + strLoggedInUserId + " TO ID :" + strChatApponentId + " SHOUT ID :" + strChatShoutId + " MEESSAGE TYPE :" + strResourceType);
                    System.out.println("DATA BUILDER :" + entityBuilder.toString());
                    /*for (int index = 0; index < arrResourceType.size(); index++) {

                        File file = new File(arrResourcePath.get(index).getPath());
                        FileBody objFile = new FileBody(file);
                        if (arrResourceType.get(index).equals("C")) {
                            entityBuilder.addPart("Image-" + index, objFile);
                        } else if (arrResourceType.get(index).equals("V")) {
                            entityBuilder.addPart("Video-" + index, objFile);
                        } else if (arrResourceType.get(index).equals("D")) {
                            entityBuilder.addPart("File-" + index, objFile);
                        }
                    }*/
                    File file = new File(resourcePath.getPath());
                    FileBody objFile = new FileBody(file);
                    if (strResourceType.equals("I")) {
                        entityBuilder.addPart("Image", objFile);
                    }
                    /* else if (arrResourceType.get(index).equals("V")) {
                        entityBuilder.addPart("Video-" + index, objFile);
                    } else if (arrResourceType.get(index).equals("D")) {
                        entityBuilder.addPart("File-" + index, objFile);
                    }*/
                    post.setEntity(entityBuilder);
                    response = client.execute(post);
                    HttpEntity httpEntity = response.getEntity();
                    strResult = EntityUtils.toString(httpEntity);
                    Utils.d("MULTIPART RESPONSE", strResult);
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (ClientProtocolException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
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
                JSONObject objJsonObject = new JSONObject(s);
                if (objJsonObject.getString("result").equals("true")) {
                    ChatMessage objChatUpdateMessage = arrChatMessages.get(intChatCellPosition);
                    objChatUpdateMessage.setTemp_uri(null);
                    objChatUpdateMessage.setChat_thumb_image("");
                    chatArrayAdapter.notifyDataSetChanged();
                    JSONArray objJsonArray = new JSONArray(objJsonObject.getString("chats"));
                    objChatUpdateMessage.setChat_thumb_image(objJsonArray.getJSONObject(objJsonArray.length() - 1).getString("image_thumb_path"));
                    objChatUpdateMessage.setImage_url(objJsonArray.getJSONObject(objJsonArray.length() - 1).getString("image_path"));
                    chatArrayAdapter.notifyDataSetChanged();
                    objDatabaseHelper.saveChatMessages(strChatShoutId, strChatApponentId, objJsonArray.toString());
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public abstract class ImageCompressionAsyncTask extends AsyncTask<String, Void, byte[]> {
        @Override
        protected byte[] doInBackground(String... strings) {
            if (strings.length == 0 || strings[0] == null)
                return null;
            return ImageUtils.compressImage(strings[0]);
        }

        protected abstract void onPostExecute(byte[] imageBytes);
    }

}
