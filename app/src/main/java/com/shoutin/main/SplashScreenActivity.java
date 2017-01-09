package com.shoutin.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.shoutin.R;
import com.shoutin.Utils.Constants;
import com.shoutin.database.DatabaseHelper;
import com.shoutin.login.LoginActivity;
import com.shoutin.login.OTPVerificationActivity;
import com.shoutin.login.ProfileScreenActivity;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashScreenActivity extends AppCompatActivity {

    SharedPreferences objSharedPreferences;
    SharedPreferences objChatPrefrences;
    DatabaseHelper objDatabaseHelper;
    SQLiteDatabase objSqLiteDatabase;
    ImageView shakeIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        objDatabaseHelper = new DatabaseHelper(this);
        objSqLiteDatabase = objDatabaseHelper.getWritableDatabase();

        shakeIcon = (ImageView) findViewById(R.id.image_shake_icon);

        Animation objShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
        objShakeAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                objChatPrefrences = getSharedPreferences(Constants.MY_PREFERENCES, MODE_PRIVATE);

                System.out.println("SHOUT ALERT STATUS : " + objSharedPreferences.getString(Constants.SHOUT_PASS_ALERT_SHOW_STATUS, ""));
                if (objSharedPreferences.getString(Constants.SHOUT_PASS_ALERT_SHOW_STATUS, "").equals("false")) {
                    SharedPreferences.Editor objEditor = objSharedPreferences.edit();
                    objEditor.putString(Constants.SHOUT_PASS_ALERT_SHOW_STATUS, "false");
                    objEditor.commit();
                } else if (objSharedPreferences.getString(Constants.SHOUT_PASS_ALERT_SHOW_STATUS, "").equals("true")) {
                    SharedPreferences.Editor objEditor = objSharedPreferences.edit();
                    objEditor.putString(Constants.SHOUT_PASS_ALERT_SHOW_STATUS, "true");
                    objEditor.commit();
                } else {
                    SharedPreferences.Editor objEditor = objSharedPreferences.edit();
                    objEditor.putString(Constants.SHOUT_PASS_ALERT_SHOW_STATUS, "true");
                    objEditor.commit();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!mayRequestLocationAccess()) {
                        return;
                    }
                } else {
                    Handler objHandler = new Handler();
                    objHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("SPLASH SCREEN  : LOGIN STATUS : " + objSharedPreferences.getString(Constants.PROFILE_LOGIN_STATUS, ""));
                            if (objSharedPreferences.getString(Constants.PROFILE_LOGIN_STATUS, "").equals("true")) {
                                // TODO: 9/29/2016 Chat screen preferences CHAT_SCREEN_ACTIVE set default value to false.
                                // TODO: 9/29/2016 So that it will indicate that user is already login and he is not a new user and his chat notifications
                                // TODO: 9/29/2016 will be generated in offline mode.
                                // TODO: 9/29/2016 Issue was when user logouts from app and again login. He does not gets chat notifications until and unless he does not go to chat screen.
                                SharedPreferences.Editor editor = objChatPrefrences.edit();
                                editor.putString(Constants.CHAT_SCREEN_ACTIVE, "false");
                                editor.commit();

                                if (objSharedPreferences.getString(Constants.OTP_VERIFIED, "").equals("1")) {
                                    if (objSharedPreferences.getString(Constants.PROFILE_COMPLETE, "").equals("1")) {
                                        Intent objIntent;
                                        if (objSharedPreferences.getString(Constants.USER_CITY, "").equals("") && objSharedPreferences.getString(Constants.USER_ADDRESS, "").equals("")) {
                                            objIntent = new Intent(SplashScreenActivity.this, ProfileScreenActivity.class);
                                        } else {
                                            objIntent = new Intent(SplashScreenActivity.this, ShoutDefaultActivity.class);

                                        }
                                        startActivity(objIntent);
                                        finish();
                                        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);

                                    } else {
                                        Intent objIntent = new Intent(SplashScreenActivity.this, ProfileScreenActivity.class);
                                        startActivity(objIntent);
                                        finish();
                                        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                                    }
                                } else {
                                    Intent objIntent = new Intent(SplashScreenActivity.this, OTPVerificationActivity.class);
                                    startActivity(objIntent);
                                    finish();
                                    overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                                }
                            } else {
                                Intent objIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                                startActivity(objIntent);
                                finish();
                                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                            }
                        }
                    }, 2000);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        shakeIcon.startAnimation(objShakeAnim);


    }

    private boolean mayRequestLocationAccess() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(READ_SMS) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(SEND_SMS) == PackageManager.PERMISSION_GRANTED
                ) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA, READ_PHONE_STATE, READ_SMS, SEND_SMS}, 1);
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, READ_CONTACTS, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA, READ_PHONE_STATE, READ_SMS, SEND_SMS}, 1);
        }
        return false;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SplashScreenActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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
            Handler objHandler = new Handler();
            objHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.out.println("FACEBOOK PROFILE LOGIN STATUS : " + objSharedPreferences.getString(Constants.PROFILE_LOGIN_STATUS, ""));
                    if (objSharedPreferences.getString(Constants.PROFILE_LOGIN_STATUS, "").equals("true")) {

                        // TODO: 9/29/2016 Chat screen preferences CHAT_SCREEN_ACTIVE set default value to false.
                        // TODO: 9/29/2016 So that it will indicate that user is already login and he is not a new user and his chat notifications
                        // TODO: 9/29/2016 will be generated in offline mode.
                        // TODO: 9/29/2016 Issue was when user logouts from app and again login. He does not gets chat notifications until and unless he does not go to chat screen.
                        SharedPreferences.Editor editor = objChatPrefrences.edit();
                        editor.putString(Constants.CHAT_SCREEN_ACTIVE, "false");
                        editor.commit();

                        if (objSharedPreferences.getString(Constants.OTP_VERIFIED, "").equals("1")) {
                            if (objSharedPreferences.getString(Constants.PROFILE_COMPLETE, "").equals("1")) {
                                Intent objIntent;
                                if (objSharedPreferences.getString(Constants.USER_CITY, "").equals("") && objSharedPreferences.getString(Constants.USER_ADDRESS, "").equals("")) {
                                    objIntent = new Intent(SplashScreenActivity.this, ProfileScreenActivity.class);
                                } else {
                                    objIntent = new Intent(SplashScreenActivity.this, ShoutDefaultActivity.class);
                                }
                                startActivity(objIntent);
                                finish();
                                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                            } else {
                                Intent objIntent = new Intent(SplashScreenActivity.this, ProfileScreenActivity.class);
                                startActivity(objIntent);
                                finish();
                                overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                            }
                        } else {
                            Intent objIntent = new Intent(SplashScreenActivity.this, OTPVerificationActivity.class);
                            startActivity(objIntent);
                            finish();
                            overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                        }
                    } else {
                        Intent objIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(objIntent);
                        finish();
                        overridePendingTransition(R.anim.fade_in_activity, R.anim.fade_out_activity);
                    }
                }
            }, 2000);
        }
    }
}
