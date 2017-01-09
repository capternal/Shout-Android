package com.shoutin.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.shoutin.CustomClasses.BadgeView;
import com.shoutin.R;
import com.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shoutin.Utils.Constants;
import com.shoutin.Utils.Utils;
import com.shoutin.app.AppController;
import com.shoutin.database.DatabaseHelper;
import com.shoutin.login.InviteFriendsActivity;
import com.shoutin.login.LoginActivity;
import com.shoutin.login.ProfileScreenActivity;
import com.shoutin.main.MessageBoardActivity;
import com.shoutin.main.NotificationListActivity;
import com.shoutin.main.PreferencesActivity;
import com.shoutin.main.ShoutDefaultActivity;
import com.shoutin.others.CircleTransform;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public abstract class BaseActivity extends ActionBarActivity {


    public static SlidingDrawer objSlidingDrawer;
    public static ImageView objDrawerOpen;
    public static ImageView objDrawerClose;
    RelativeLayout objRelativeBase;
    FrameLayout objFrameLayoutContainer;
    public static Button btnDrawer;
    TextView objTextViewProfileName;
    ImageView objProfileImage;
    SharedPreferences objSharedPreferences;

    boolean blockFrameLayout = false;
    DatabaseHelper objDatabaseHelper;


    TextView objTextViewShoutBook;
    TextView objTextViewNotifications;
    TextView objTextViewMessages;
    TextView objTextViewSpreadLove;
    TextView objTextViewPreferences;
    TextView objTextViewLogout;
    TextView objTextViewBaseEditProfile;

    // BOTTOM TABS IMAGE BUTTON
    public static LinearLayout objLinearLayoutDrawerOptions;

    public static ImageButton objImageButtonCreateShout;

    public static ImageButton objImageButtonShoutBook;
    public static ImageButton objImageButtonNotifications;
    public static ImageButton objImageButtonMessages;
    public static ImageButton objImageButtonSpreadLove;
    public static ImageButton objImageButtonPreferences;
    public static ImageButton objImageButtonLogout;

    public static RelativeLayout objRelativeDefaultTop;
    public static RelativeLayout objRelativeCustomTop;
    public static TextView objTextViewDrawerCustomBack;
    public static TextView objTextViewDrawerCustomDownArrow;

    public static RelativeLayout objRelativeBaseBottomTabs;
    public static ImageView objShoutImage;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    ImageView objImageViewDefaultLoading;
    //    public static TextView objTextViewNotificationCount;
    public static ImageView objImageNotificationCount;
    public static RelativeLayout objRelativeLayoutDefaultLoading;


    DrawerOpenCloseListener objDrawerOpenCloseListener;

    BadgeView objActionbarBadge;
    BadgeView objDrawerBadge;
    BadgeView objMessageBadge;


    @Override
    public void setContentView(int layoutResID) {
        objRelativeBase = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        objFrameLayoutContainer = (FrameLayout) objRelativeBase.findViewById(R.id.frame_layout_container);
        getLayoutInflater().inflate(layoutResID, objFrameLayoutContainer, true);
        super.setContentView(objRelativeBase);


        objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
        objDatabaseHelper = new DatabaseHelper(this);

        init();

        objFrameLayoutContainer.setOnTouchListener(onTouchListener);
    }

    public boolean mayRequestLocationAccess() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED
                ) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA, READ_PHONE_STATE}, 1);
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA, READ_PHONE_STATE}, 1);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        System.out.println("ONE PERMISSION GRANTED");

        int intCount = 0;
        if (requestCode == 1) {
            for (int intIndex = 0; intIndex < grantResults.length; intIndex++) {
                if (grantResults[intIndex] == PackageManager.PERMISSION_DENIED) {
                    intCount++;
                }
            }
            System.out.println("PERMISSION GRANTED COUNT : " + intCount);
        }
        if (intCount > 0) {
            // all permission granted
            onPermissionsGranted(intCount);
        }
    }


    public abstract void onPermissionsGranted(int requestCode);

    public interface DrawerOpenCloseListener {
        void isDrawerOpen(boolean result);
    }

    public void setDrawerOpenCloseListener(DrawerOpenCloseListener objDrawerOpenCloseListener) {
        this.objDrawerOpenCloseListener = objDrawerOpenCloseListener;
    }

    protected void init() {

        RelativeLayout objRelativeLayout = (RelativeLayout) findViewById(R.id.content);
        objRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

//        objTextViewNotificationCount = (TextView) findViewById(R.id.txt_notification_count);
        objImageNotificationCount = (ImageView) findViewById(R.id.img_notification_count);

        objImageViewDefaultLoading = (ImageView) findViewById(R.id.default_loading);
        objRelativeLayoutDefaultLoading = (RelativeLayout) findViewById(R.id.relative_default_progress);

        Animation sampleFadeAnimation = AnimationUtils.loadAnimation(BaseActivity.this, R.anim.loading_progress_animation);
        objImageViewDefaultLoading.startAnimation(sampleFadeAnimation);

        objRelativeDefaultTop = (RelativeLayout) findViewById(R.id.relative_upper_base);
        objRelativeCustomTop = (RelativeLayout) findViewById(R.id.relative_upper_custom);
        objTextViewDrawerCustomBack = (TextView) findViewById(R.id.txt_drawer_back_custom);
        objTextViewDrawerCustomDownArrow = (TextView) findViewById(R.id.drawer_down_arrow_custom);
        objShoutImage = (ImageView) findViewById(R.id.image_shout_base_drawer);

        objSlidingDrawer = (SlidingDrawer) findViewById(R.id.slidingDrawer);
        objDrawerOpen = (ImageView) findViewById(R.id.drawer_open);
        objDrawerClose = (ImageView) findViewById(R.id.drawer_close);
        btnDrawer = (Button) findViewById(R.id.btn_base_drawer);
        objTextViewProfileName = (TextView) findViewById(R.id.txt_profile_name);
        objProfileImage = (ImageView) findViewById(R.id.img_profile);


        objLinearLayoutDrawerOptions = (LinearLayout) findViewById(R.id.linear_drawer_options);
        objTextViewBaseEditProfile = (TextView) findViewById(R.id.txt_base_edit_profile);

        objLinearLayoutDrawerOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("DO NOTHING");
            }
        });

        objTextViewBaseEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences objProfileSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor objEditor = objProfileSharedPreferences.edit();
                objEditor.putString(Constants.PROFILE_BACK_SCREEN_NAME, Constants.SHOUT_DEFAULT_SCREEN);
                objEditor.commit();

                Intent objIntent = new Intent(BaseActivity.this, ProfileScreenActivity.class);
                startActivity(objIntent);
                finish();
                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
            }
        });

        objTextViewShoutBook = (TextView) findViewById(R.id.txt_base_shoutbook);
        objTextViewNotifications = (TextView) findViewById(R.id.txt_base_notifications);
        objTextViewMessages = (TextView) findViewById(R.id.txt_base_messages);
        objTextViewSpreadLove = (TextView) findViewById(R.id.txt_base_spreadlove);
        objTextViewPreferences = (TextView) findViewById(R.id.txt_base_preferences);
        objTextViewLogout = (TextView) findViewById(R.id.txt_base_logout);


        objImageButtonShoutBook = (ImageButton) findViewById(R.id.image_shout_book);
        objImageButtonNotifications = (ImageButton) findViewById(R.id.image_notification);
        objImageButtonMessages = (ImageButton) findViewById(R.id.image_messages);
        objImageButtonSpreadLove = (ImageButton) findViewById(R.id.image_spread_love);
        objImageButtonPreferences = (ImageButton) findViewById(R.id.image_preferences);
        objImageButtonLogout = (ImageButton) findViewById(R.id.imgbutton_drawer_logout);


        objActionbarBadge = new BadgeView(this, objImageNotificationCount);
        objDrawerBadge = new BadgeView(this, objImageButtonNotifications);
        objMessageBadge = new BadgeView(this, objImageButtonMessages);


        objImageButtonShoutBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: Use your own attributes to track content views in your app
                SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                Answers.getInstance().logCustom(new CustomEvent(Constants.EVENT_MENU_CLICK)
                        .putCustomAttribute("User Id", objSharedPreferences.getString(Constants.USER_ID, ""))
                        .putCustomAttribute("Clicked On", "Shout Book")
                        .putCustomAttribute("Username", objSharedPreferences.getString(Constants.USER_NAME, ""))
                        .putCustomAttribute("Facebook Id", objSharedPreferences.getString(Constants.USER_FACEBOOK_ID, ""))
                        .putCustomAttribute("Email Id", objSharedPreferences.getString(Constants.PROFILE_EMAIL_ID, ""))
                );

                Intent objIntent = new Intent(BaseActivity.this, InviteFriendsActivity.class);
                startActivity(objIntent);
                finish();
                /*
                CLEARING THE SORT FILTERS

                SharedPreferences.Editor objEditor = objSharedPreferences.edit();
                objEditor.putString(Constants.SORT_POPULARITY, "0");
                objEditor.putString(Constants.SORT_RECENCY, "0");
                objEditor.putString(Constants.SORT_LOCATION, "0");
                objEditor.commit();*/

                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
            }
        });

        objImageButtonNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Use your own attributes to track content views in your app
                SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                Answers.getInstance().logCustom(new CustomEvent(Constants.EVENT_MENU_CLICK)
                        .putCustomAttribute("User Id", objSharedPreferences.getString(Constants.USER_ID, ""))
                        .putCustomAttribute("Clicked On", "Notifications")
                        .putCustomAttribute("Username", objSharedPreferences.getString(Constants.USER_NAME, ""))
                        .putCustomAttribute("Facebook Id", objSharedPreferences.getString(Constants.USER_FACEBOOK_ID, ""))
                        .putCustomAttribute("Email Id", objSharedPreferences.getString(Constants.PROFILE_EMAIL_ID, ""))
                );
                objSlidingDrawer.animateClose();
                Intent objIntent = new Intent(BaseActivity.this, NotificationListActivity.class);
                startActivity(objIntent);
                overridePendingTransition(0, 0);
            }
        });

        objImageButtonMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Use your own attributes to track content views in your app
                SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                Answers.getInstance().logCustom(new CustomEvent(Constants.EVENT_MENU_CLICK)
                        .putCustomAttribute("User Id", objSharedPreferences.getString(Constants.USER_ID, ""))
                        .putCustomAttribute("Clicked On", "Messages")
                        .putCustomAttribute("Username", objSharedPreferences.getString(Constants.USER_NAME, ""))
                        .putCustomAttribute("Facebook Id", objSharedPreferences.getString(Constants.USER_FACEBOOK_ID, ""))
                        .putCustomAttribute("Email Id", objSharedPreferences.getString(Constants.PROFILE_EMAIL_ID, ""))
                );
                Intent objIntent = new Intent(BaseActivity.this, MessageBoardActivity.class);
                startActivity(objIntent);
                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                finish();
            }
        });

        objImageButtonSpreadLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Use your own attributes to track content views in your app
                SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                Answers.getInstance().logCustom(new CustomEvent(Constants.EVENT_MENU_CLICK)
                        .putCustomAttribute("User Id", objSharedPreferences.getString(Constants.USER_ID, ""))
                        .putCustomAttribute("Clicked On", "Spread Love")
                        .putCustomAttribute("Username", objSharedPreferences.getString(Constants.USER_NAME, ""))
                        .putCustomAttribute("Facebook Id", objSharedPreferences.getString(Constants.USER_FACEBOOK_ID, ""))
                        .putCustomAttribute("Email Id", objSharedPreferences.getString(Constants.PROFILE_EMAIL_ID, ""))
                );
                objSlidingDrawer.animateClose();
                String shareBody = "http://www.shoutin.co";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, objSharedPreferences.getString(Constants.USER_SPREAD_THE_LOVE_CONTENT, ""));
                startActivity(Intent.createChooser(sharingIntent, "Share this"));
            }
        });

        objImageButtonPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Use your own attributes to track content views in your app
                SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                Answers.getInstance().logCustom(new CustomEvent(Constants.EVENT_MENU_CLICK)
                        .putCustomAttribute("User Id", objSharedPreferences.getString(Constants.USER_ID, ""))
                        .putCustomAttribute("Clicked On", "Preferences")
                        .putCustomAttribute("Username", objSharedPreferences.getString(Constants.USER_NAME, ""))
                        .putCustomAttribute("Facebook Id", objSharedPreferences.getString(Constants.USER_FACEBOOK_ID, ""))
                        .putCustomAttribute("Email Id", objSharedPreferences.getString(Constants.PROFILE_EMAIL_ID, ""))
                );
                objSlidingDrawer.animateClose();
                Intent objIntent = new Intent(BaseActivity.this, PreferencesActivity.class);
                startActivity(objIntent);
                finish();
                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
            }
        });

        objImageButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

//        setListener();
        objRelativeBaseBottomTabs = (RelativeLayout) findViewById(R.id.relative_bottom_tab);
        try {
            Typeface custom_font = Typeface.createFromAsset(BaseActivity.this.getAssets(), "AvenirNext-Medium.ttf");
            objTextViewShoutBook.setTypeface(custom_font);
            objTextViewNotifications.setTypeface(custom_font);
            objTextViewMessages.setTypeface(custom_font);
            objTextViewSpreadLove.setTypeface(custom_font);
            objTextViewPreferences.setTypeface(custom_font);
            objTextViewLogout.setTypeface(custom_font);
            objTextViewProfileName.setTypeface(custom_font);
            objTextViewBaseEditProfile.setTypeface(custom_font);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objImageButtonCreateShout = (ImageButton) findViewById(R.id.imagebutton_base_add);
        btnDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (ShoutDefaultActivity.objRelativeLayoutSearchBox.getVisibility() == RelativeLayout.VISIBLE) {
                    ShoutDefaultActivity.openCloseSearchBar(false, BaseActivity.this);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                } else {
                    objSlidingDrawer.animateOpen();
                }*/
                objSlidingDrawer.animateOpen();
            }
        });

        objSlidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                try {
                    objDrawerOpen.setVisibility(ImageView.GONE);
                    objDrawerClose.setVisibility(ImageView.VISIBLE);
                    blockFrameLayout = true;
                    objDrawerOpenCloseListener.isDrawerOpen(true);
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                }
            }
        });

        objSlidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                try {
                    ShoutDefaultActivity.objCreateShout.setVisibility(ImageButton.VISIBLE);
                    objDrawerOpen.setVisibility(ImageView.GONE);
                    objDrawerClose.setVisibility(ImageView.GONE);
                    blockFrameLayout = false;
                    objDrawerOpenCloseListener.isDrawerOpen(false);
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                }
            }
        });

        objDrawerClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objSlidingDrawer.animateClose();
                try {
                    ShoutDefaultActivity.objCreateShout.setVisibility(ImageButton.VISIBLE);
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                }
            }
        });


        if (objSharedPreferences.getString(Constants.PROFILE_IMAGE_URL, "").toString().length() > 0) {
            System.out.println("UPDATED URL DRAWER : " + objSharedPreferences.getString(Constants.PROFILE_IMAGE_URL, ""));
            BaseActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //objProfileImage.setImageUrl(objSharedPreferences.getString(Constants.PROFILE_IMAGE_URL, ""), imageLoader);
                    if (ConnectivityBroadcastReceiver.isConnected()) {
                        Picasso.with(BaseActivity.this).load(objSharedPreferences.getString(Constants.PROFILE_IMAGE_URL, "").toString()).transform(new CircleTransform()).into(objProfileImage);
                    } else {
                        Picasso.with(BaseActivity.this).load(objSharedPreferences.getString(Constants.PROFILE_IMAGE_URL, "").toString()).networkPolicy(NetworkPolicy.OFFLINE).transform(new CircleTransform()).into(objProfileImage);
                    }
                }
            });
            objProfileImage.setPadding(4, 4, 4, 4);
        } else {
            Picasso.with(BaseActivity.this).load(R.drawable.dummy_image).transform(new CircleTransform()).
                    into(objProfileImage);
        }
        if (objSharedPreferences.getString(Constants.USER_NAME, "").toString().length() > 0) {
            objTextViewProfileName.setText(objSharedPreferences.getString(Constants.USER_NAME, "").toString());
        }
        objProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objSlidingDrawer.animateClose();
                SharedPreferences.Editor objEditor = objSharedPreferences.edit();
                objEditor.putString(Constants.PROFILE_SCREEN_USER_ID, objSharedPreferences.getString(Constants.USER_ID, ""));
                objEditor.commit();
                Intent objIntent = new Intent(BaseActivity.this, ProfileScreenActivity.class);
                startActivity(objIntent);
                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
            }
        });
    }

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Utils.d("DEBUG_TAG", "SCROLL VIEW NOT SCROLL");
            return blockFrameLayout;
        }
    };

    public void hideBottomTabs() {
        objRelativeBaseBottomTabs.setVisibility(RelativeLayout.GONE);
    }

    public void showBottomTabs() {
        objRelativeBaseBottomTabs.setVisibility(RelativeLayout.VISIBLE);
    }

    public void hideDefaultTopHeader() {
        objRelativeDefaultTop.setVisibility(RelativeLayout.GONE);
        objRelativeCustomTop.setVisibility(RelativeLayout.VISIBLE);
    }

    public void showDefaultTopHeader() {
        objRelativeDefaultTop.setVisibility(RelativeLayout.VISIBLE);
        objRelativeCustomTop.setVisibility(RelativeLayout.GONE);
    }

    public void hideBothTopHeader() {
        objRelativeDefaultTop.setVisibility(RelativeLayout.GONE);
        objRelativeCustomTop.setVisibility(RelativeLayout.GONE);
    }

    public void hideAllView() {
        objRelativeDefaultTop.setVisibility(RelativeLayout.GONE);
        objRelativeCustomTop.setVisibility(RelativeLayout.GONE);
    }

    public void showDialog() {
        // CUSTOM ALERT DIALOG
       /* LayoutInflater factory = LayoutInflater.from(BaseActivity.this);
        final View customAlertLayout = factory.inflate(R.layout.custom_alert_dialog_layout, null);
        final AlertDialog objLogoutAlertDialog = new AlertDialog.Builder(BaseActivity.this).create();
        objLogoutAlertDialog.setView(customAlertLayout);
        customAlertLayout.findViewById(R.id.txt_alert_yes).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });
        customAlertLayout.findViewById(R.id.txt_alert_no).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                objLogoutAlertDialog.dismiss();
            }
        });
        objLogoutAlertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        objLogoutAlertDialog.show();*/

        new AlertDialog.Builder(BaseActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("")
                .setMessage("Are you sure, You want to logout ?")
                .setPositiveButton("No", null)
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FacebookSdk.sdkInitialize(BaseActivity.this);
                        LoginManager.getInstance().logOut();

                        // TODO: Use your own attributes to track content views in your app
                        Answers.getInstance().logCustom(new CustomEvent(Constants.EVENT_USER_LOGOUT)
                                .putCustomAttribute("ID", objSharedPreferences.getString(Constants.USER_ID, ""))
                                .putCustomAttribute("Username", objSharedPreferences.getString(Constants.USER_NAME, "")));

                        objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameMessageUserList);
                        objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameMessageBoard);
                        objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameChat);
//                objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameFriends);
                        objDatabaseHelper.deleteTable(DatabaseHelper.strTableNameShout);

                        System.out.println("SURESH FACEBOOK LOGOUT SUCCESSFUL ...");
                        System.out.println("LOGOUT BEFORE: LOGIN STATUS : " + objSharedPreferences.getString(Constants.PROFILE_LOGIN_STATUS, ""));
                        objSharedPreferences.edit().clear().commit();
                        System.out.println("LOGOUT AFTER: LOGIN STATUS : " + objSharedPreferences.getString(Constants.PROFILE_LOGIN_STATUS, ""));
                        SharedPreferences.Editor objEditor = objSharedPreferences.edit();
                        objEditor.putString(Constants.PROFILE_LOGIN_STATUS, "false");
                        objEditor.putString(Constants.USER_NAME, "");
                        objEditor.putString(Constants.PROFILE_IMAGE_URL, "");
                        objEditor.putString(Constants.PROFILE_EMAIL_ID, "");
                        objEditor.putString(Constants.LOGIN_FLAG, "0");
                        objEditor.putString(Constants.USER_ID, "");
                        objEditor.putString(Constants.USER_FACEBOOK_ID, "");
                        objEditor.commit();

                        Intent objIntent = new Intent(BaseActivity.this, LoginActivity.class);
                        startActivity(objIntent);
                        finish();
                        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                    }
                }).show();

    }

    /*@Override
    public void onNotificationReceived(int count) {
        Utils.d("NOTIFICATION RECEIVED COUNT", String.valueOf(count));
        updateNotificationCount(String.valueOf(count));
    }*/

    public void updateNotificationCount(String strCount) {
        System.out.println("NOTIFICATION COUNT : " + strCount);
        try {
            if ("".equals(strCount) || "null".equals(strCount)) {
                strCount = "0";
            }
            if (Integer.parseInt(strCount) > 0) {
                // ACTIONBAR MENU NOTIFICATION COUNT
                objActionbarBadge.setText(String.valueOf(strCount));
                objActionbarBadge.show();
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 0);
                lp.gravity = Gravity.RIGHT | Gravity.TOP;
                objActionbarBadge.setLayoutParams(lp);
                objActionbarBadge.setTextSize(9);

                // DRAWER MENU NOTIFICATION COUNT
                objDrawerBadge.setText(String.valueOf(strCount));
                objDrawerBadge.show();
            } else {
                objActionbarBadge.hide();
                objDrawerBadge.hide();
            }
        } catch (ClassCastException ce) {
            ce.printStackTrace();
        } catch (NumberFormatException number_exception) {
            number_exception.printStackTrace();
        }
    }

    public void updateMessageCount(String strMessageCount) {
        try {
            System.out.println("NOTIFICATION MESSAGE COUNT : " + strMessageCount);
            if (strMessageCount.equals("") || strMessageCount.equals(null)) {
                strMessageCount = "0";
            }
            if (Integer.parseInt(strMessageCount) > 0) {
                objMessageBadge.setText(strMessageCount);
                objMessageBadge.show();
            }
        } catch (ClassCastException ce) {
            ce.printStackTrace();
        } catch (NumberFormatException number_exception) {
            number_exception.printStackTrace();
        }
    }
}
