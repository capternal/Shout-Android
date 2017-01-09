package com.shoutin.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.shoutin.app.AppController;

/**
 * Created by CapternalSystems on 8/29/2016.
 */
public class ConnectivityBroadcastReceiver extends BroadcastReceiver {

    public static ConnectivityReceiverListener objConnectivity;

    public ConnectivityBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            System.out.println("IN CONNECTIVITY BROADCAST");

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            objConnectivity.onNetworkConnectionChanged(isConnected);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            if (exitValue == 0) {
                System.out.println("CONNECTIVITY SUCCESS");
                objConnectivity.onNetworkConnectionChanged(true);
            } else {
                System.out.println("CONNECTIVITY FAIL");
                objConnectivity.onNetworkConnectionChanged(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public static boolean isConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) AppController.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
