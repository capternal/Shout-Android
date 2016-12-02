package com.shout.shoutin;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shout.shoutin.Utils.Constants;
import com.shout.shoutin.Utils.NotificationBroadcastReceiver;
import com.shout.shoutin.login.ProfileScreenActivity;
import com.shout.shoutin.main.MessageBoardActivity;
import com.shout.shoutin.main.ShoutDefaultActivity;
import com.shout.shoutin.main.ShoutDetailActivity;

import java.util.Map;
import java.util.Random;

/**
 * Created by Capternal on 04/02/16.
 */
public class GCMIntentListener extends FirebaseMessagingService {
    private static final String TAG = "MyGcmListenerService";
    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
    NotificationBroadcastReceiver objNotificationBroadcastReceiver;
    private String order_id;
    private String notification_id;
    private String from_id = "";
    private String to_id = "";
    // Sets a title for the Inbox style big view
    private String shout_id = "";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map data = message.getData();

        System.out.println("DATA MESSAGE : " + message.getData());
        System.out.println("FROM MESSAGE : " + message.getFrom());
       /* if (message.getNotification().getBody() != null) {
            Log.i("PVL", "RECEIVED MESSAGE: " + message.getNotification().getBody());
        } else {
            Log.i("PVL", "RECEIVED MESSAGE: " + message.getData().get("message"));
        }*/


        try {
            String alert_message = String.valueOf(data.get("alert"));
            String profile_url = String.valueOf(data.get("profile_url"));
            shout_id = String.valueOf(data.get("shout_id"));
            String type = String.valueOf(data.get("type"));
            from_id = String.valueOf(data.get("from_id"));
            to_id = String.valueOf(data.get("to_id"));
            String user_name = String.valueOf(data.get("user_name"));
            String created = String.valueOf(data.get("created"));
            String message_type = String.valueOf(data.get("m_type"));
            String image_url = String.valueOf(data.get("image_path"));
            String strIsProcessed = String.valueOf(data.get("is_processed"));
            String strApponentName = String.valueOf(data.get("other_user"));
            String strRequestCount = String.valueOf(data.get("send_request"));
            String strChatRowId = String.valueOf(data.get("chat_id"));
            String strChatThumbPath = String.valueOf(data.get("image_thumb_path"));
            String strNotificationType = String.valueOf(data.get("notification_type"));
            String strNotificationCount = String.valueOf(data.get("notification_count"));

            System.out.println("PRASAD PRINT NOTIFICATION COUNT : " + strNotificationCount);
            System.out.println("PRASAD PRINT NOTIFICATION TYPE : " + strNotificationType);
            System.out.println("PRASAD PRINT FROM ID : " + from_id);

            Intent objIntent = new Intent("NOTIFICATION_COUNT_LISTENER");
            objIntent.putExtra("NOTIFICATION_COUNT", String.valueOf(data.get("notification_count")));
            sendBroadcast(objIntent);

            // UNIQUE NOTIFICATION ID USED FOR EACH NOTIFICATION
            String Unique_Integer_Number = String.valueOf(data.get("id"));
            String strNotificationAlert = String.valueOf(data.get("notification_alert"));

            if (from.startsWith("/topics/")) {
                // message received from some topic.
            } else {
                // normal downstream message.
            }

            if (strNotificationType.equals("C")) {
                sendNotification(alert_message, profile_url, shout_id, type, from_id, to_id, user_name, Unique_Integer_Number, strNotificationAlert, created, message_type, image_url, strIsProcessed, strApponentName, strRequestCount, strChatRowId, strChatThumbPath, strNotificationType);
            } else {
                generateNotification(strNotificationType, alert_message, shout_id);
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void generateNotification(String strNotificationType, String strMessage, String shout_id) {

        System.out.println("NOTIFICATION SHOUT ID : " + shout_id);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.facebook_logo)
                        .setContentTitle("Shout Application")
                        .setAutoCancel(true)
                        .setContentText(strMessage);

        SharedPreferences objSharedPreferences = getSharedPreferences(Constants.PROFILE_PREFERENCES, 0);
        SharedPreferences.Editor objEditor;
        Intent resultIntent = null;

        switch (strNotificationType) {
            case "CS":
            case "CO":
            case "LC":
                // CS : Create Shout
                // CO : COMMENT COUNT
                // L  : Like
//                new DatabaseHelper(this).deleteShoutEntries();
                resultIntent = new Intent(this, ShoutDetailActivity.class);
                objEditor = objSharedPreferences.edit();
                objEditor.putString(Constants.SHOUT_ID_FOR_DETAIL_SCREEN, "");
                objEditor.putString(Constants.IS_BLOG_CLICKED, "false");
                objEditor.commit();
                Bundle objBundle = new Bundle();
                objBundle.putString(Constants.SHOUT_ID_FOR_DETAIL_SCREEN, shout_id);
                resultIntent.putExtra("NOTIFICATION_DATA", objBundle);
                resultIntent.setAction("com.shout.shout_test.ShoutDetailActivity");
                break;
            case "FR":
                // FR  : FRIEND REQUEST OR FRIEND
                SharedPreferences.Editor objProfileEditor = objSharedPreferences.edit();
                objProfileEditor.putString(Constants.PROFILE_SCREEN_USER_ID, from_id);
                objProfileEditor.putString(Constants.IS_BLOG_CLICKED, "false");
                objProfileEditor.putString(Constants.PROFILE_BACK_SCREEN_NAME, Constants.SHOUT_DETAIL_SCREEN);
                objProfileEditor.commit();

                resultIntent = new Intent(this, ProfileScreenActivity.class);
                objEditor = objSharedPreferences.edit();
                objEditor.putString(Constants.PROFILE_SCREEN_USER_ID, from_id);
                objEditor.putString(Constants.PROFILE_BACK_SCREEN_NAME, Constants.SHOUT_DETAIL_SCREEN);
                objEditor.putString(Constants.PROFILE_NOTIFICATION_BACK, "1");
                objEditor.putString(Constants.IS_BLOG_CLICKED, "false");
                objEditor.commit();
                break;
        }
        try {
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(ShoutDefaultActivity.class);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(new Random().nextInt(), PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(new Random().nextInt(), mBuilder.build());
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /*@Override
    public void onMessageReceived(String from, Bundle data) {

    }*/

    private void sendNotification(String message, String strProfileUrl, String strShoutId,
                                  String strType, String strFromId, String strToId, String strUserName,
                                  String Unique_Integer_Number, String strNotificationAlert,
                                  String created, String message_type, String image_url, String strIsProcessed,
                                  String strApponentName, String strRequestCount, String strChatRowId, String strChatThumbPath, String strNotificationType) {
        SharedPreferences objChatPrefrences = getSharedPreferences(Constants.MY_PREFERENCES, MODE_PRIVATE);
        int numMessages = 0;
        RegistrationIntentService.arrMessages.add(strNotificationAlert);

        System.out.println("CHAT INFO FROM ID " + strFromId);
        System.out.println("CHAT INFO TO ID " + strToId);

        if (strNotificationType.equals("C")) {
            if (objChatPrefrences.getString(Constants.CHAT_SCREEN_ACTIVE, "").equals("true")) {

                System.out.println("RECEIVED DATA : " + strProfileUrl);

                SharedPreferences objProfilePreferences = getSharedPreferences(Constants.CHAT_PREFERENCES, MODE_PRIVATE);
                if (strShoutId.equals(objProfilePreferences.getString(Constants.CHAT_SHOUT_ID, "")) && strFromId.equals(objProfilePreferences.getString(Constants.CHAT_APPONENT_ID, ""))) {

                    System.out.println("IS PROCESSEED : " + strIsProcessed);
                    Intent objEditor = new Intent();
                    objEditor.setAction("appendChatScreenMsg");
                    objEditor.putExtra(Constants.SHOUT_ID_FOR_DETAIL_SCREEN, strShoutId);
                    objEditor.putExtra(Constants.USER_ID_FOR_DETAIL_SCREEN, String.valueOf(strToId));
                    objEditor.putExtra(Constants.SHOUT_TYPE_FOR_DETAIL_SCREEN, String.valueOf(strType));
                    objEditor.putExtra(Constants.USER_PROFILE_URL_FOR_DETAIL_SCREEN, String.valueOf(strProfileUrl));
                    objEditor.putExtra(Constants.CHAT_MESSAGE, message);
                    objEditor.putExtra(Constants.CHAT_CREATED, created);
                    objEditor.putExtra(Constants.CHAT_MESSAGE_TYPE, message_type);
                    objEditor.putExtra(Constants.CHAT_IMAGE_URL, image_url);
                    objEditor.putExtra(Constants.CHAT_FROM_ID, strFromId);
                    objEditor.putExtra(Constants.CHAT_TO_ID, strToId);
                    objEditor.putExtra(Constants.CHAT_IS_PROCESSED, strIsProcessed);
                    objEditor.putExtra("REQUEST_COUNT", strRequestCount);
                    objEditor.putExtra("CHAT_ROW_ID", strChatRowId);
                    objEditor.putExtra("CHAT_THUMB_IMAGE", strChatThumbPath);

                    String[] strName = strUserName.toString().split(" ");
                    objEditor.putExtra(Constants.CHAT_SCREEN_APPONENT_USER_NAME, strName[0] + " " + strName[1].charAt(0));
                    this.sendBroadcast(objEditor);
                } else {
                    Intent intent = new Intent(this, MessageBoardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.facebook_logo)
                            .setContentTitle("Shout Application")
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentText(strNotificationAlert);

                    notificationBuilder.setContentIntent(pendingIntent);
                    // Moves events into the big view
                    for (int i = 0; i < RegistrationIntentService.arrMessages.size(); i++) {
                        inboxStyle.addLine(RegistrationIntentService.arrMessages.get(i));
                    }
                    inboxStyle.setBigContentTitle("Shout App");
                    notificationBuilder.setStyle(inboxStyle);
                    notificationManager.notify(Integer.parseInt(Unique_Integer_Number), notificationBuilder.build());
                }
            } else {
                Intent intent = new Intent(this, MessageBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            /*Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.facebook_logo)
                    .setContentTitle("Shout Application")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());*/

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.facebook_logo)
                        .setContentTitle("Shout Application")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentText(strNotificationAlert);

                notificationBuilder.setContentIntent(pendingIntent);

                inboxStyle.setBigContentTitle("Shout App");
                // Moves events into the big view
                for (int i = 0; i < RegistrationIntentService.arrMessages.size(); i++) {
                    inboxStyle.addLine(RegistrationIntentService.arrMessages.get(i));
                }
                notificationBuilder.setStyle(inboxStyle);
                notificationManager.notify(Integer.parseInt(Unique_Integer_Number), notificationBuilder.build());
            }
        } else if (strNotificationType.equals("FR")) {
            // FRIEND REQUEST
        } else if (strNotificationType.equals("O")) {
            // OFFERS
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void newOrderReceivedNotification(String message) {
        int intTag = new Random().nextInt();
        System.out.println("Final Random Number====>" + intTag);
        /*Utils.d("ORDER ID NOTIFY", String.valueOf(order_id));

        // Prepare intent which is triggered if the
        // notification is selected
        AcceptButtonReciever acceptButtonReciever = new AcceptButtonReciever();
        registerReceiver(acceptButtonReciever, new IntentFilter("com.neonrunner.runner.ACCEPT"));
        Intent acceptIntent = new Intent("com.neonrunner.runner.ACCEPT");
        Bundle bundle = new Bundle();
        bundle.putString("order_id", order_id);
        bundle.putString("notification_id", notification_id);
        bundle.putInt("msg_id", intTag);
        acceptIntent.putExtra("order_details", bundle);
        PendingIntent pIntentAccept = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), acceptIntent, 0);

        RejectButtonReciever rejectButtonReciever = new RejectButtonReciever();
        registerReceiver(acceptButtonReciever, new IntentFilter("com.neonrunner.runner.REJECT"));
        Intent rejectIntent = new Intent("com.neonrunner.runner.REJECT");
        PendingIntent pIntentReject = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), rejectIntent, 0);
        Intent unAssignedOrderIntent = new Intent();
        unAssignedOrderIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntentOrders = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), unAssignedOrderIntent, 0);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Order Received")
                    .setContentText("")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(pIntentOrders)
                    .setSmallIcon(R.drawable.symbol)
                    .addAction(R.drawable.tick, "Accept", pIntentAccept)
                    .addAction(R.drawable.cross, "Reject", pIntentReject)
                    .setAutoCancel(true)
                    .setColor(Color.BLACK)
                    .build();
        } else {
            // do something for phones running an SDK before lollipop
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Order Received")
                    .setContentText("")
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(pIntentOrders)
                    .setSmallIcon(R.drawable.neon_launcher)
                    .addAction(R.drawable.tick, "Accept", pIntentAccept)
                    .addAction(R.drawable.cross, "Reject", pIntentReject)
                    .setAutoCancel(true)
                    .build();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        // noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(intTag, notification);*/
    }

    public void CustomNotification(String message) {
        int intTag = new Random().nextInt();
        System.out.println("Final Random Number====>" + intTag);
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.customnotification);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // Set Icon
                .setSmallIcon(R.drawable.facebook_logo)
                // Set Ticker Message
                .setTicker(getString(R.string.app_name))
                // Dismiss Notification
                .setAutoCancel(true)
                // Set PendingIntent into Notification
                // .setContentIntent(pIntent)
                // Set RemoteViews into Notification
                .setContent(remoteViews);

        // Locate and set the Image into customnotificationtext.xml ImageViews
        remoteViews.setTextViewText(R.id.txt_ordered_item_title_description, "Order Received");

        /*Intent switchIntent = new Intent(this, AcceptButtonReciever.class);
        switchIntent.putExtra("id", intTag);
        switchIntent.setAction(String.valueOf(intTag));
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0,
                switchIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_accept_order_notification, pendingSwitchIntent);

        Intent switchIntent1 = new Intent(this, RejectButtonReciever.class);
        switchIntent1.putExtra("id", intTag);
        switchIntent1.setAction(String.valueOf(intTag));
        PendingIntent pendingSwitchIntent1 = PendingIntent.getBroadcast(this, 0,
                switchIntent1, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_reject_order_notification, pendingSwitchIntent1);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(intTag, builder.build());*/
    }


    /*@Override
    public void onWebserviceResponce(String strUrl, String strResult) {
        try {
            Utils.d("PUSH MESSAGES SAVE API URL : ", strUrl);
            Utils.d("PUSH MESSAGES SAVE API RESULT : ", strResult);

            JSONObject objJsonObject = new JSONObject(strResult);
            if (objJsonObject.getBoolean("result")) {
                JSONArray objJsonArray = new JSONArray(objJsonObject.getString("chats"));
                Utils.d("CHAT_JSON", objJsonArray.toString());
                new DatabaseHelper(this).saveChatMessages(shout_id, to_id, objJsonArray.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
