package com.shoutin.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public static NotificationCountListener objNotificationCountListener;

    public NotificationBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            System.out.println("IN NOTIFICATION ONRECEIVE BROADCAST RECEIVER.");
            objNotificationCountListener.onNotificationReceived(Integer.parseInt(intent.getExtras().getString("NOTIFICATION_COUNT").equals("null") ? "0" : intent.getExtras().getString("NOTIFICATION_COUNT")));
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface NotificationCountListener {
        public void onNotificationReceived(int count);
    }

}


