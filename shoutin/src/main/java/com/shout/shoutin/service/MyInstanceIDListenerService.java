package com.shout.shoutin.service;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.shout.shoutin.Utils.Constants;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private SharedPreferences objSharedPreferences;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("DEVICE_TOKEN_USING_FCM", "Refreshed token: " + refreshedToken);


        objSharedPreferences = getSharedPreferences(Constants.MY_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor objEditor = objSharedPreferences.edit();
        objEditor.putString(Constants.DEVICE_TOKEN, refreshedToken);
        objEditor.putBoolean(Constants.SENT_TOKEN_TO_SERVER, true);
        objEditor.commit();

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {

    }
}