package com.shoutin.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.shoutin.Utils.CallWebService;
import com.shoutin.Utils.ConnectivityBroadcastReceiver;
import com.shoutin.Utils.Constants;
import com.shoutin.Utils.Utils;
import com.shoutin.database.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jupitor on 30/09/16.
 */

public class ContactSyncService extends Service implements CallWebService.WebserviceResponse {

    DatabaseHelper objDatabaseHelper = new DatabaseHelper(this);
    ArrayList<String> arrStrContactName;
    ArrayList<String> arrStrContactNumber;
    SharedPreferences sharedPreferences;
    JSONArray jsonArray;
    public JSONArray objContactJsonArray;
    ContentResolver cr;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        objDatabaseHelper = new DatabaseHelper(this);
        cr = this.getContentResolver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Utils.d("SERVICE :", "CONTACT SERVICE CALLED");
        }

        if (ConnectivityBroadcastReceiver.isConnected()) {
            new SyncContacts().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        return Service.START_NOT_STICKY;
    }

    public class SyncContacts extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.d("SHOUTBOOK", "CONTACT SERVICE STARTED.");
            // TODO: 30/09/16 send the success and failure data to the activity from the intent service.

        }

        @Override
        protected String doInBackground(String... params) {

            /*objContactJsonArray = new JSONArray();
            arrStrContactName = new ArrayList<String>();
            arrStrContactNumber = new ArrayList<String>();

            Hashtable<String, String> objPhoneDirectory = new Hashtable<String, String>();
            Hashtable<String, String> arrayListContactPhoto = new Hashtable<String, String>();


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
                                    Log.e(name + "(mobile number)", phoneNumber);
                                    arrStrContactName.add(name);
                                    arrStrContactNumber.add(phoneNumber);
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
                                        *//*JSONObject objNewJsonObject = new JSONObject();
                                        objNewJsonObject.put("name", name);
                                        objNewJsonObject.put("phone", phoneNumber);
                                        objContactJsonArray.put(objNewJsonObject);*//*
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                    Log.e(name + "(home number)", phoneNumber);
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                    Log.e(name + "(work number)", phoneNumber);
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                    Log.e(name + "(other number)", phoneNumber);
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
                try {
                    objNewJsonObject.put("name", objPhoneDirectory.get(key));
                    objNewJsonObject.put("phone", key);
                    objNewJsonObject.put(Constants.PROFILE_IMAGE, arrayListContactPhoto.get(key));
                    objContactJsonArray.put(objNewJsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                objContactJsonArray.put(objNewJsonObject);
            }*/
            Utils.d("SHOUTBOOK", "CONTACT SERVICE READING CONTACTS.");

            try {
                Log.d("PRASANNA", "IN BACKGROUND OF CONTACT READING");

                objContactJsonArray = new JSONArray();

                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                phones.moveToFirst();

                List<String> tempNames = new ArrayList<String>();

                Log.d("PRASANNA", "CONTACT CURSOR COUNT = " + phones.getCount());
                while (phones.moveToNext()) {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String profileImage = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));


                    if (!tempNames.contains(name)) {
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
                Utils.d("SHOUTBOOK", "CONTACT SERVICE READING CONTACTS, see this details : " + objContactJsonArray);
                // close cursor
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Utils.d("SHOUTBOOK", "CONTACT SERVICE FINISHED.");
            if (ConnectivityBroadcastReceiver.isConnected()) {
                callWebService();
            } else {
                Utils.d("SERVICE :", "NO INTERNET CONNECTION");
            }
        }
    }


    private void callWebService() {
        try {
            JSONObject objRootJsonObject = new JSONObject();
            SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
            objRootJsonObject.put("user_id", objSharedPreferences.getString(Constants.USER_ID, ""));
            objRootJsonObject.put("contacts", objContactJsonArray);
            objRootJsonObject.put("facebook", objSharedPreferences.getString(Constants.USER_FACEBOOK_FRIENDS_JSON, ""));
            new CallWebService(Constants.FRIENDS_COMPARE_API, objRootJsonObject, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onWebserviceResponce(String strUrl, String strResult) {
        if (strUrl.equals(Constants.FRIENDS_COMPARE_API)) {
            try {
                JSONObject objJsonObject = new JSONObject(strResult);
                if (objJsonObject.getString("result").equals("true")) {

                    // TODO: Use your own attributes to track content views in your app
                    SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, MODE_PRIVATE);
                    Answers.getInstance().logCustom(new CustomEvent(Constants.EVENT_CONTACT_SYNC)
                            .putCustomAttribute("User Id", objSharedPreferences.getString(Constants.USER_ID, ""))
                            .putCustomAttribute("Username", objSharedPreferences.getString(Constants.USER_NAME, ""))
                            .putCustomAttribute("Facebook Id", objSharedPreferences.getString(Constants.USER_FACEBOOK_ID, ""))
                            .putCustomAttribute("Email Id", objSharedPreferences.getString(Constants.PROFILE_EMAIL_ID, ""))
                    );

                    final JSONArray objJsonArray = new JSONArray(objJsonObject.getString("friends"));
                    Utils.d("YOGESH CONTACT SYNC ", String.valueOf(objJsonArray));
                    Thread objSaveContactsThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            objDatabaseHelper.saveFriends(objJsonArray);
                        }
                    });
                    objSaveContactsThread.start();
                }
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
