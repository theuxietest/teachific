package com.so.luotk.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.react.modules.core.PermissionListener;
import com.so.luotk.R;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.models.output.EndLiveRoomResponse;
import com.so.luotk.models.output.MarkAttendenceResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.JitsiMeetView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MyLiveClass extends AppCompatActivity implements JitsiMeetActivityInterface {
    public static final String TAG = "MyLiveClass";
    private JitsiMeetView view;
    private String jitsiRoomId, batchId, jitsiRoomName, jitsiRoomPassword, orgName, loggedInUserName;
    private boolean isAdmin, isLiveStreamOn;
    private IntentFilter broadcastFilter;
    private String live_option;
    private String roomIdFromBroadcast, batchIdFromBroadcast, courseIdFromBroadcast, currentBatchId, currentRoomName, currentRoomId;
    private boolean isFullScreen;

    private BroadcastReceiver broadcastReceiver;
    private final BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
        Utilities.restrictKeepScreenOn(this);
        orgName = PreferenceHandler.readString(MyLiveClass.this, PreferenceHandler.ORG_NAME, null);
        loggedInUserName = PreferenceHandler.readString(MyLiveClass.this, PreferenceHandler.LOGGED_IN_USERNAME, null);
        live_option = PreferenceHandler.readString(MyLiveClass.this, PreferenceHandler.LIVE_OPTION, null);
        if (getIntent() != null) {
            jitsiRoomName = getIntent().getStringExtra(PreferenceHandler.ROOM_NAME);
            jitsiRoomPassword = getIntent().getStringExtra(PreferenceHandler.ROOM_PASSWORD);
            isAdmin = getIntent().getBooleanExtra(PreferenceHandler.IS_ADMIN, false);
            jitsiRoomId = getIntent().getStringExtra(PreferenceHandler.ROOM_ID);
            batchId = getIntent().getStringExtra(PreferenceHandler.BATCH_ID);
        }
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        if (isAdmin) {
            if (jitsiRoomName != null) {
                JitsiMeetConferenceOptions options = getOptions();
                JitsiMeetActivity.launch(this, options);
            }
        } else {
            view = new JitsiMeetView(this);
            setContentView(view);
            if (jitsiRoomName != null) {
                JitsiMeetConferenceOptions options = getOptions();
                view.join(options);
            }
        }
        registerForBroadcastMessages();
    }

    @Override
    public void onBackPressed() {
        if (!isAdmin) {
            showExitMeetingDialog();
        }

    }
    private void showExitMeetingDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyLiveClass.this);
        alertBuilder.setTitle("Live Class");
        if (isAdmin) {
            alertBuilder.setMessage("Do you want to end the class?");
        } else {
            alertBuilder.setMessage("Do you want to leave the class?");
        }
        alertBuilder.setPositiveButton("Yes", (dialogInterface, i) -> {
            if (isAdmin) {
                if (jitsiRoomId != null) {
                    if (Utilities.checkInternet(MyLiveClass.this)) {
                        hitEndLiveRoomService(jitsiRoomId);
                    } else {
                        Toast.makeText(MyLiveClass.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {

                if (jitsiRoomId != null)
                    leaveMeeting();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }
    private void leaveMeeting() {
        JitsiMeetActivityDelegate.onBackPressed();
        JitsiMeetActivityDelegate.onHostDestroy(this);
        finish();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (!isAdmin && broadcastReceiver != null) {
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isAdmin) {
            try {
                registerReceiver(broadcastReceiver, broadcastFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        if (!isAdmin) {
            try {
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent != null) {
                            if (intent.getAction().equals(PreferenceHandler.END_JITSI_ROOM_ACTION)) {
                                batchIdFromBroadcast = intent.getStringExtra(PreferenceHandler.BATCH_ID);
                                roomIdFromBroadcast = intent.getStringExtra(PreferenceHandler.ROOM_ID);
                                courseIdFromBroadcast = intent.getStringExtra(PreferenceHandler.COURSE_ID);
                                if (!isAdmin) {
                                    PreferenceHandler.writeBoolean(MyLiveClass.this, PreferenceHandler.END_JITSI_ROOM_ACTION, true);
                                    if (batchId != null && (batchIdFromBroadcast != null || courseIdFromBroadcast != null) && jitsiRoomId != null && roomIdFromBroadcast != null)
                                        if ((batchId.equalsIgnoreCase(batchIdFromBroadcast) || batchId.equalsIgnoreCase(courseIdFromBroadcast)) && jitsiRoomId.equalsIgnoreCase(roomIdFromBroadcast)) {
                                            JitsiMeetActivityDelegate.onBackPressed();
                                            finish();
                                        }
                                }
                            }
                        }
                    }
                };
                registerReceiver(broadcastReceiver,
                        new IntentFilter(PreferenceHandler.END_JITSI_ROOM_ACTION));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onDestroy() {
        try {
            if (broadcastReceiver1 != null) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
                LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver1);
            }
            if (isFullScreen) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isAdmin) {
                        if (jitsiRoomId != null) {
                            hitEndLiveRoomService(jitsiRoomId);
                            JitsiMeetActivityDelegate.onBackPressed();
                        }
                    } else {
                        finish();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            JitsiMeet.showDevOptions();
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    protected JitsiMeetConferenceOptions getOptions() {
        URL serverURL = null;
        try {
            if (live_option != null && live_option.equalsIgnoreCase("1")) {
                serverURL = new URL("https://live.smartowls.in/");
            } else if (live_option != null && live_option.equalsIgnoreCase("3"))
                serverURL = new URL("https://meet.jit.si");
            else if (live_option != null && live_option.equalsIgnoreCase("2"))
                Toast.makeText(getApplicationContext(), "This live option is not supported by this app", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getApplicationContext(), "Live option is not valid", Toast.LENGTH_SHORT).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
        if (isAdmin) {
            userInfo.setDisplayName(orgName);
            isLiveStreamOn = true;
        } else {
            userInfo.setDisplayName(loggedInUserName);
            isLiveStreamOn = false;

        }


        return new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setUserInfo(userInfo)
                .setSubject(" ")
                .setRoom(jitsiRoomName)
                .setAudioOnly(false)
                .setAudioMuted(!isAdmin)
                .setVideoMuted(false)
                .setFeatureFlag("recording.enabled", false)
                .setFeatureFlag("tile-view.enabled", isAdmin)
                .setFeatureFlag("live-streaming.enabled", isLiveStreamOn)
                .setFeatureFlag("call-integration.enabled", true)
                .setFeatureFlag("invite.enabled", false)
                .setFeatureFlag("lobby-mode.enabled", false)
                .setFeatureFlag("meeting-password.enabled", false)
                .setFeatureFlag("video-share.enabled", false)
                .setFeatureFlag("meeting-name.enabled", false)
                .setFeatureFlag("kick-out.enabled", isAdmin)
                .setFeatureFlag("help.enabled", false)
                .setFeatureFlag("overflow-menu.enabled", isAdmin)
                .setFeatureFlag("security-options.enabled", false)
                .setFeatureFlag("filmstrip.enabled", isAdmin)
                .setFeatureFlag("audio-mute.enabled", true)
                .setFeatureFlag("raise-hand.enabled", true)
                .setFeatureFlag("notifications.enabled", false)
                .setFeatureFlag("prejoinpage.enabled", false)
                .setFeatureFlag("car-mode.enabled", false)
                .setFeatureFlag("pip.enabled", false)
                .setFeatureFlag("resolution", 720)
                .setFeatureFlag("welcomepage.enabled", false)

                .build();

    }

    @Override
    public void onUserInteraction() {
        Log.d(TAG, "onUserInteraction: ");
        super.onUserInteraction();
    }

    private void hitEndLiveRoomService(String jitsiRoomId) {
        APIInterface apiInterface = ApiUtils.getApiInterface();
        if (!jitsiRoomId.isEmpty()) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));
            Call<EndLiveRoomResponse> call = apiInterface.endLiveRoom(headers, batchId, jitsiRoomId);
            call.enqueue(new Callback<EndLiveRoomResponse>() {
                @Override
                public void onResponse(Call<EndLiveRoomResponse> call, Response<EndLiveRoomResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus() != null
                                && response.body().getStatus().equalsIgnoreCase("200")
                                && response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                                finish();
                            }
                        } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                            Utilities.openUnauthorizedDialog(MyLiveClass.this);
                        }
                    }

                }

                @Override
                public void onFailure(Call<EndLiveRoomResponse> call, Throwable t) {
                    Toast.makeText(MyLiveClass.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void hitMarkAttendenceService() {
        APIInterface apiInterface = ApiUtils.getApiInterface();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));
        Call<MarkAttendenceResponse> call = apiInterface.markStudentLiveAttendence(headers, batchId, jitsiRoomId);
        call.enqueue(new Callback<MarkAttendenceResponse>() {
            @Override
            public void onResponse(Call<MarkAttendenceResponse> call, Response<MarkAttendenceResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            Toast.makeText(MyLiveClass.this, "Attendence has been marked successfully", Toast.LENGTH_SHORT).show();
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(MyLiveClass.this);
                    } else {
                        Toast.makeText(MyLiveClass.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<MarkAttendenceResponse> call, Throwable t) {
                Toast.makeText(MyLiveClass.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            isFullScreen = true;
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            isFullScreen = false;

        }
    }



    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            final String[] permissions,
            final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {

    }

    private void registerForBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();
        for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.getAction());
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver1, intentFilter);
    }
    private void onBroadcastReceived(Intent intent) {
        if (intent != null) {
            try {
                BroadcastEvent event = new BroadcastEvent(intent);
                switch (event.getType()) {
                    case CONFERENCE_JOINED:
                        if (Utilities.checkInternet(this)) {
                            if (!isAdmin) {
                                if (batchId != null && jitsiRoomId != null) {
                                    hitMarkAttendenceService();
                                }
                            }
                        } else {
                            Toast.makeText(this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                        }
                        Timber.i("Conference Joined with url%s", event.getData().get("url"));
                        break;
                    case PARTICIPANT_JOINED:
                        Timber.i("Participant joined%s", event.getData().get("name"));
                        break;
                    case PARTICIPANT_LEFT:
                        String endCall = intent.getStringExtra("EndCall");
                        break;
                    case AUDIO_MUTED_CHANGED:
                        break;
                    case CONFERENCE_WILL_JOIN:
                        break;
                    case CONFERENCE_TERMINATED:
                        if (isFullScreen) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                        if (isAdmin) {
                            if (jitsiRoomId != null) {
                                if (Utilities.checkInternet(this)) {
                                    hitEndLiveRoomService(jitsiRoomId);
                                } else {
                                    Toast.makeText(this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            finish();
                        }
                        break;
                    case ENDPOINT_TEXT_MESSAGE_RECEIVED:
                        break;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void hangUp() {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
    }


}

/*import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.so.vsznk.utils.Utilities;

public class MyLiveClass extends AppCompatActivity {
    public static final String TAG = "MyLiveClass";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
    }
}*/
