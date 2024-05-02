package com.so.luotk.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.so.luotk.R;

public class OreoNotification extends ContextWrapper {
    private static String CHANNEL_ID = "com.smartowls.smartowls";
    private static String CHANNEL_NAME = "Smart Owls";
    private NotificationManager notificationManager;

    public OreoNotification(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {

       /* try {
            if (getManager().getNotificationChannel("com.smartowls.smartowls").getName().equals("Smart Owls")) {
                getManager().deleteNotificationChannel("com.smartowls.smartowls");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        CHANNEL_ID = getPackageName();
        CHANNEL_NAME = getString(R.string.app_name);

        Log.d("TAG", "createChannel: " + CHANNEL_ID +" : "+ CHANNEL_NAME);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(true);
   /*     channel.setVibrationPattern(new long[]{1000, 1000, 1000});*/
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);


    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotification(String title, String body, PendingIntent pendingIntent, Uri soundUri) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID).
                setContentIntent(pendingIntent).setContentText(body).setContentTitle(title).setStyle(new Notification.BigTextStyle().bigText(body)).setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher)/*.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher))*/.setSound(soundUri);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotification(String title, String body, Uri soundUri) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentText(body).setContentTitle(title).setStyle(new Notification.BigTextStyle().bigText(body)).setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher)/*.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification_icon))*/.setSound(soundUri);
    }
}
