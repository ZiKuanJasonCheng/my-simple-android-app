package com.jc.simpleapp.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.jc.simpleapp.R;
import com.jc.simpleapp.constant.BroadcastActionConstant;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationManager {
    private static final String TAG ="NotificationManager";
    // Sets an ID for the notification
    private static final int FOREGROUND_NEED_HELP_NOTIFICATION_ID = 1;
    private static final int FOREGROUND_PRICE_CHANGED_NOTIFICATION_ID = 2;

    public static void showForegroundNotification(Context context){
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        Log.d(TAG,"showForegroundNotification");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.alvose_white)
                        .setContentTitle("Alvoshop")
                        .setContentText("Need help for lower price?")
                        .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
                ;


        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(BroadcastActionConstant.FOREGROUND_NEED_HELP_NOTIFICATION_ONCLICK);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        0,
                        intentBroadcast,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(pendingIntent);



        // Gets an instance of the NotificationManager service
        android.app.NotificationManager mNotifyMgr =
                (android.app.NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(FOREGROUND_NEED_HELP_NOTIFICATION_ID, mBuilder.build());
        //End of "The main service should execute after the phone starts up"

    }

    public static void showPriceChangedNotification(Context context){
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        Log.d(TAG,"showPriceChangedNotification");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.alvose_white)
                        .setContentTitle("Alvoshop")
                        .setContentText("Alvoshop Price Report is available for view.")
                        .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
                ;


        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        Intent intentBroadcast = new Intent();
        intentBroadcast.setAction(BroadcastActionConstant.FOREGROUND_PRICE_CHANGED_NOTIFICATION_ONCLICK);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        0,
                        intentBroadcast,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setPriority(Notification.PRIORITY_MAX); //To show heads-up notification
        mBuilder.setAutoCancel(true);

        // Gets an instance of the NotificationManager service
        android.app.NotificationManager mNotifyMgr =
                (android.app.NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(FOREGROUND_PRICE_CHANGED_NOTIFICATION_ID, mBuilder.build());
        //End of "The main service should execute after the phone starts up"

    }

    public static void stopForegroundNotification(Context c){
        android.app.NotificationManager mNotifyMgr =  (android.app.NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(FOREGROUND_NEED_HELP_NOTIFICATION_ID);
    }

    public static void stopPriceChangedNotification(Context c){
        android.app.NotificationManager mNotifyMgr =  (android.app.NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(FOREGROUND_PRICE_CHANGED_NOTIFICATION_ID);
    }

    public static void stopAllNotification(Context c){
        android.app.NotificationManager mNotifyMgr =  (android.app.NotificationManager) c.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancelAll();
    }

}
