package com.so.luotk.firebase;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.so.luotk.R;
import com.so.luotk.activities.AnnouncementActivity;
import com.so.luotk.activities.BatchDetailActivity;
import com.so.luotk.activities.CourseDetailAcrivity;
import com.so.luotk.activities.MainActivity;
import com.so.luotk.activities.adminrole.AdminMainScreen;
import com.so.luotk.utils.PreferenceHandler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    private String body, title, courseId, room, senderId, batchId, recieverId, senderName, notificationType, image;
    private boolean isAdmin;
    ArrayList<NotificationDataModel> notificationDataList = new ArrayList<>();
    private Intent intent = null;
    private PendingIntent pendingIntent = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        isAdmin = PreferenceHandler.readBoolean(this, PreferenceHandler.ADMIN_LOGGED_IN, false);
        if (remoteMessage.getData() != null) {
            setUpNotificationData(remoteMessage);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sendOreoNotifications(remoteMessage);
            } else {
                sendNotifications(remoteMessage);
            }
        }
    }

    private void setUpNotificationData(RemoteMessage remoteMessage) {

        notificationType = remoteMessage.getData().get("notificationtype");
        title = remoteMessage.getData().get("title");
        body = remoteMessage.getData().get("message");
        if (notificationType != null && (notificationType.equalsIgnoreCase("Live Ended") || notificationType.equalsIgnoreCase("Live Ended of Course"))) {
            if (!isAdmin)
                hitBroadcastForJitsiRoom(remoteMessage, PreferenceHandler.END_JITSI_ROOM_ACTION);
            else {
                intent = new Intent(this, AdminMainScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pendingIntent = PendingIntent.getActivity(this,
                            1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                } else {
                    pendingIntent = PendingIntent.getActivity(this,
                            1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }

//                pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            }
        } else if (notificationType != null) {
            if (notificationType.equalsIgnoreCase("attendance") || notificationType.equalsIgnoreCase("courseAdded") || (notificationType.equalsIgnoreCase("Live Start of Course")) || notificationType.equalsIgnoreCase("courseAnnouncement") || notificationType.equalsIgnoreCase("addStudent") || notificationType.equalsIgnoreCase("materialAdd") || notificationType.equalsIgnoreCase("Live Start") || notificationType.equalsIgnoreCase("announcement") ||
                    notificationType.equalsIgnoreCase("test") || notificationType.equalsIgnoreCase("video") || notificationType.equalsIgnoreCase("testUpdate") || notificationType.equalsIgnoreCase("assignment") || notificationType.equalsIgnoreCase("assignmentUpdate") || notificationType.equalsIgnoreCase("testEvaluate")) {
                batchId = remoteMessage.getData().get("batchId");
                courseId = remoteMessage.getData().get("courseId");
                if (notificationType.equalsIgnoreCase("testEvaluate")) {
                    intent = new Intent(this, MainActivity.class);
                    String testid = remoteMessage.getData().get("testId");
                    intent.putExtra(PreferenceHandler.TEST_REPORT_ID, testid);
                    intent.putExtra(PreferenceHandler.BATCH_ID, remoteMessage.getData().get("batchId"));
                    intent.putExtra(PreferenceHandler.NOTIFICATION_TYPE, remoteMessage.getData().get("notificationtype"));
                    intent.putExtra(PreferenceHandler.IS_FROM_REPORT_NOTIFICATION, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        pendingIntent = PendingIntent.getActivity(this,
                                1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    } else {
                        pendingIntent = PendingIntent.getActivity(this,
                                1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

//                    pendingIntent = PendingIntent.getActivity(this, 1, intent,
//                            PendingIntent.FLAG_UPDATE_CURRENT);

                } else if (notificationType.equalsIgnoreCase("announcement")) {
                    if (remoteMessage.getData().get("batchId").equals("0")) {
                        if (!isAdmin) {
                            intent = new Intent(this, MainActivity.class);
                            intent.putExtra(PreferenceHandler.IS_FROM_BATCH_NOTIFICATION, false);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                pendingIntent = PendingIntent.getActivity(this,
                                        1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                            } else {
                                pendingIntent = PendingIntent.getActivity(this,
                                        1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            }
                        } else {
                            intent = new Intent(this, AdminMainScreen.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                pendingIntent = PendingIntent.getActivity(this,
                                        1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                            } else {
                                pendingIntent = PendingIntent.getActivity(this,
                                        1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            }
                        }
                    } else {
                        intent = new Intent(this, AnnouncementActivity.class);
                        intent.putExtra(PreferenceHandler.IS_FROM_ANNOUNCEMENT_NOTIFICATION, true);
                        intent.putExtra(PreferenceHandler.IS_FROM, "batch");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(PreferenceHandler.BATCH_ID, remoteMessage.getData().get("batchId"));
                        intent.putExtra(PreferenceHandler.NOTIFICATION_TYPE, remoteMessage.getData().get("notificationtype"));
                        Intent backIntent = new Intent(this, BatchDetailActivity.class);
                        backIntent.putExtra(PreferenceHandler.BATCH_ID, remoteMessage.getData().get("batchId"));
                        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Intent backIntent2 = new Intent(this, MainActivity.class);
                        backIntent2.putExtra(PreferenceHandler.BATCH_ID, remoteMessage.getData().get("batchId"));
                        backIntent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            pendingIntent = PendingIntent.getActivities(this,
                                    1, new Intent[]{backIntent2, backIntent, intent}, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                        } else {
                            pendingIntent = PendingIntent.getActivities(this,
                                    1, new Intent[]{backIntent2, backIntent, intent}, PendingIntent.FLAG_UPDATE_CURRENT);
                        }

//                        pendingIntent = PendingIntent.getActivities(this, 1, new Intent[]{backIntent2, backIntent, intent}, PendingIntent.FLAG_UPDATE_CURRENT);
                    }


                } else if (notificationType.equalsIgnoreCase("courseAnnouncement")) {
                    intent = new Intent(this, AnnouncementActivity.class);
                    intent.putExtra(PreferenceHandler.IS_FROM_ANNOUNCEMENT_NOTIFICATION, true);
                    intent.putExtra(PreferenceHandler.IS_FROM, "course");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("courseId", remoteMessage.getData().get("courseId"));
                    intent.putExtra(PreferenceHandler.NOTIFICATION_TYPE, remoteMessage.getData().get("notificationtype"));
                    Intent backIntent = new Intent(this, CourseDetailAcrivity.class);
                    backIntent.putExtra(PreferenceHandler.COURSE_ID, remoteMessage.getData().get("courseId"));
                    backIntent.putExtra("notify", true);
                    backIntent.putExtra("isFromMyCourses", true);
                    intent.putExtra("fromLink", false);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Intent backIntent2 = new Intent(this, MainActivity.class);
                    backIntent2.putExtra(PreferenceHandler.BATCH_ID, remoteMessage.getData().get("courseId"));
                    backIntent2.putExtra(PreferenceHandler.IS_PAYMENT_DONE, true);
                    backIntent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        pendingIntent = PendingIntent.getActivities(this,
                                1, new Intent[]{backIntent2, backIntent, intent}, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    } else {
                        pendingIntent = PendingIntent.getActivities(this,
                                1, new Intent[]{backIntent2, backIntent, intent}, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

//                    pendingIntent = PendingIntent.getActivities(this, 1, new Intent[]{backIntent2, backIntent, intent}, PendingIntent.FLAG_UPDATE_CURRENT);

                } else if (notificationType.equalsIgnoreCase("Live Start of Course")) {
                    intent = new Intent(this, CourseDetailAcrivity.class);
                    intent.putExtra(PreferenceHandler.COURSE_ID, remoteMessage.getData().get("courseId"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("notify", true);
                    intent.putExtra("isFromMyCourses", true);
                    intent.putExtra("fromLink", false);
                    intent.putExtra(PreferenceHandler.NOTIFICATION_TYPE, remoteMessage.getData().get("notificationtype"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        pendingIntent = PendingIntent.getActivity(this,
                                1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    } else {
                        pendingIntent = PendingIntent.getActivity(this,
                                1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

//                    pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                } else if (notificationType.equalsIgnoreCase("addStudent")) {
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra(PreferenceHandler.IS_FROM_BATCH_NOTIFICATION, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(PreferenceHandler.NOTIFICATION_TYPE, remoteMessage.getData().get("notificationtype"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        pendingIntent = PendingIntent.getActivity(this,
                                1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    } else {
                        pendingIntent = PendingIntent.getActivity(this,
                                1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
//                    pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                } else if (notificationType.equalsIgnoreCase("courseAdded")) {
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra(PreferenceHandler.IS_COURSE_ADDED, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(PreferenceHandler.NOTIFICATION_TYPE, remoteMessage.getData().get("notificationtype"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        pendingIntent = PendingIntent.getActivity(this,
                                1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    } else {
                        pendingIntent = PendingIntent.getActivity(this,
                                1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
//                    pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                } else {
                    intent = new Intent(this, BatchDetailActivity.class);
                    intent.putExtra(PreferenceHandler.BATCH_ID, remoteMessage.getData().get("batchId"));
                    intent.putExtra(PreferenceHandler.NOTIFICATION_TYPE, remoteMessage.getData().get("notificationtype"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent backIntent = new Intent(this, MainActivity.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        pendingIntent = PendingIntent.getActivities(this,
                                1, new Intent[]{backIntent, intent}, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    } else {
                        pendingIntent = PendingIntent.getActivities(this,
                                1, new Intent[]{backIntent, intent}, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

//                    pendingIntent = PendingIntent.getActivities(this, 1, new Intent[]{backIntent, intent}, PendingIntent.FLAG_UPDATE_CURRENT);
                }

                switch (notificationType) {
                    case "test":
                    case "testUpdate":
                    case "testEvaluate":
                    case "assignmentUpdate":
                    case "assignment":
                    case "announcement":
                    case "materialAdd":
                    case "addStudent":
                    case "Video":
                        List<NotificationDataModel> sharedNotificationList = PreferenceHandler.getNotificationDataList(this);
                        if (sharedNotificationList != null && sharedNotificationList.size() > 0) {
                            NotificationDataModel notificationDataModel = new NotificationDataModel();
                            notificationDataModel.setBatchId(remoteMessage.getData().get("batchId"));
                            notificationDataModel.setCourseId(remoteMessage.getData().get("courseId"));
                            notificationDataModel.setNotificationType(notificationType);
                            if (notificationType.equalsIgnoreCase("assignment") || notificationType.equalsIgnoreCase("assignmentUpdate")) {
                                notificationDataModel.setId(remoteMessage.getData().get("assignmentId"));
                            } else if (notificationType.equalsIgnoreCase("test") || notificationType.equalsIgnoreCase("testUpdate")) {
                                notificationDataModel.setId(remoteMessage.getData().get("testId"));
                            } else if (notificationType.equalsIgnoreCase("testEvaluate")) {
                                notificationDataModel.setId(remoteMessage.getData().get("testId"));
                            } else if (notificationType.equalsIgnoreCase("video")) {
                                notificationDataModel.setId(remoteMessage.getData().get("folderId"));
                            } else if (notificationType.equalsIgnoreCase("materialAdd")) {
                                notificationDataModel.setId(remoteMessage.getData().get("materialId"));
                            } else notificationDataModel.setId("-1");

                            sharedNotificationList.add(notificationDataModel);
                            Set<NotificationDataModel> set = new LinkedHashSet<>(sharedNotificationList);
                            sharedNotificationList.clear();
                            sharedNotificationList.addAll(set);
                            PreferenceHandler.setList(this, PreferenceHandler.NOTIFICATION_DATA, sharedNotificationList);

                        } else {
                            //For saving object
                            NotificationDataModel notificationDataModel = new NotificationDataModel();
                            notificationDataModel.setBatchId(remoteMessage.getData().get("batchId"));
                            notificationDataModel.setNotificationType(notificationType);
                            if (notificationType.equalsIgnoreCase("assignment") || notificationType.equalsIgnoreCase("assignmentUpdate")) {
                                notificationDataModel.setId(remoteMessage.getData().get("assignmentId"));
                            } else if (notificationType.equalsIgnoreCase("test") || notificationType.equalsIgnoreCase("testUpdate")) {
                                notificationDataModel.setId(remoteMessage.getData().get("testId"));
                            } else if (notificationType.equalsIgnoreCase("testEvaluate")) {
                                notificationDataModel.setId(remoteMessage.getData().get("testId"));
                            } else if (notificationType.equalsIgnoreCase("video")) {
                                notificationDataModel.setId(remoteMessage.getData().get("folderId"));
                            } else if (notificationType.equalsIgnoreCase("materialAdd")) {
                                notificationDataModel.setId(remoteMessage.getData().get("materialId"));
                            } else notificationDataModel.setId("-1");
                            notificationDataList.add(notificationDataModel);
                            Set<NotificationDataModel> set = new LinkedHashSet<>(notificationDataList);
                            notificationDataList.clear();
                            notificationDataList.addAll(set);
                            PreferenceHandler.setList(this, PreferenceHandler.NOTIFICATION_DATA, notificationDataList);
                        }
                }

            } else {
                if (!isAdmin) {
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra(PreferenceHandler.IS_FROM_BATCH_NOTIFICATION, false);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        pendingIntent = PendingIntent.getActivity(this,
                                1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    } else {
                        pendingIntent = PendingIntent.getActivity(this,
                                1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

//                    pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                } else {
                    intent = new Intent(this, AdminMainScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        pendingIntent = PendingIntent.getActivity(this,
                                1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    } else {
                        pendingIntent = PendingIntent.getActivity(this,
                                1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
//                    pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
            }
        } else {
            if (!isAdmin) {
                intent = new Intent(this, MainActivity.class);
                intent.putExtra(PreferenceHandler.IS_FROM_BATCH_NOTIFICATION, false);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pendingIntent = PendingIntent.getActivity(this,
                            1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                } else {
                    pendingIntent = PendingIntent.getActivity(this,
                            1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }

//                pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                intent = new Intent(this, AdminMainScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pendingIntent = PendingIntent.getActivity(this,
                            1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                } else {
                    pendingIntent = PendingIntent.getActivity(this,
                            1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }

//                pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOreoNotifications(RemoteMessage remoteMessage) {
        Uri customSound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.cassiopeia);
        /* Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);*/
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), customSound);
        if (r != null)
            r.play();
        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder;
        if (pendingIntent != null)
            builder = oreoNotification.getOreoNotification(getString(R.string.app_name), body, pendingIntent, customSound);
        else
            builder = oreoNotification.getOreoNotification(getString(R.string.app_name), body, customSound);
        oreoNotification.getManager().notify(1, builder.build());


    }

    private void sendNotifications(RemoteMessage remoteMessage) {
        Uri customSound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.cassiopeia);
        /*  Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);*/
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), customSound);
        if (r != null)
            r.play();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name)).setStyle(new NotificationCompat.BigTextStyle().bigText(body)).setContentText(title + ": " + body)
                .setAutoCancel(true).setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setSound(customSound);
        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        noti.notify(1, builder.build());
    }

    private boolean isForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(context.getApplicationContext().getPackageName());
    }

    private void hitBroadcastForJitsiRoom(RemoteMessage remoteMessage, String action) {

        Log.d("TAG", "hitBroadcastForJitsiRoom: " +  remoteMessage.getData().get("roomId") +" : "+ action);
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(PreferenceHandler.COURSE_ID, remoteMessage.getData().get("courseId"));
        intent.putExtra(PreferenceHandler.BATCH_ID, remoteMessage.getData().get("batchId"));
        intent.putExtra(PreferenceHandler.ROOM_ID, remoteMessage.getData().get("roomId"));
        intent.putExtra("EndCall", "True");
        sendBroadcast(intent);
    }

}
