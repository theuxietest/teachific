package com.so.luotk.activities.adminrole;

/*import android.content.BroadcastReceiver;
import android.content.Context;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.react.modules.core.PermissionListener;
import com.so.lekbq.BuildConfig;
import com.so.lekbq.R;
import com.so.lekbq.api.APIInterface;
import com.so.lekbq.api.ApiUtils;
import com.so.lekbq.models.output.EndLiveRoomResponse;
import com.so.lekbq.models.output.MarkAttendenceResponse;
import com.so.lekbq.utils.PreferenceHandler;
import com.so.lekbq.utils.Utilities;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LiveJitsiMeetActivity extends AppCompatActivity implements JitsiMeetActivityInterface {
    public static final String TAG = "LiveJitsiMeetActivity";
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
        orgName = PreferenceHandler.readString(LiveJitsiMeetActivity.this, PreferenceHandler.ORG_NAME, null);
        loggedInUserName = PreferenceHandler.readString(LiveJitsiMeetActivity.this, PreferenceHandler.LOGGED_IN_USERNAME, null);
        live_option = PreferenceHandler.readString(LiveJitsiMeetActivity.this, PreferenceHandler.LIVE_OPTION, null);
        if (getIntent() != null) {
            jitsiRoomName = getIntent().getStringExtra(PreferenceHandler.ROOM_NAME);
            jitsiRoomPassword = getIntent().getStringExtra(PreferenceHandler.ROOM_PASSWORD);
            isAdmin = getIntent().getBooleanExtra(PreferenceHandler.IS_ADMIN, false);
            jitsiRoomId = getIntent().getStringExtra(PreferenceHandler.ROOM_ID);
            batchId = getIntent().getStringExtra(PreferenceHandler.BATCH_ID);
        }
        if (jitsiRoomName != null) {

            JitsiMeetConferenceOptions options = getOptions();
            JitsiMeetActivity.launch(this, options);
        }
        registerForBroadcastMessages();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isAdmin) {
            try {
                Log.d("registerReceiver:", "registerReceiver");
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
                Log.d("registerReceiver:", "registerReceiver");
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d("OnReceiver:", "registerReceiver" + intent.getAction());
                        if (intent != null) {
                            if (intent.getAction().equals(PreferenceHandler.END_JITSI_ROOM_ACTION)) {
                                batchIdFromBroadcast = intent.getStringExtra(PreferenceHandler.BATCH_ID);
                                roomIdFromBroadcast = intent.getStringExtra(PreferenceHandler.ROOM_ID);
                                courseIdFromBroadcast = intent.getStringExtra(PreferenceHandler.COURSE_ID);
                                if (!isAdmin) {
                                    PreferenceHandler.writeBoolean(LiveJitsiMeetActivity.this, PreferenceHandler.END_JITSI_ROOM_ACTION, true);
                                    if (batchId != null && (batchIdFromBroadcast != null || courseIdFromBroadcast != null) && jitsiRoomId != null && roomIdFromBroadcast != null)
                                        if ((batchId.equalsIgnoreCase(batchIdFromBroadcast) || batchId.equalsIgnoreCase(courseIdFromBroadcast)) && jitsiRoomId.equalsIgnoreCase(roomIdFromBroadcast)) {
                                            Log.d("StudentConference:", "TERMINATED");
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
        if (BuildConfig.DEBUG && keyCode == KeyEvent.KEYCODE_MENU) {
            JitsiMeet.showDevOptions();
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Log.d(TAG, "onUserLeaveHint: ");
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
//                .setWelcomePageEnabled(false)
                .setUserInfo(userInfo)
                .setSubject(" ")          *//*Set call subject here. use to display phone number here.*//*
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
                .setFeatureFlag("video-share.enabled", isAdmin)
                .setFeatureFlag("meeting-name.enabled", false)
                .setFeatureFlag("kick-out.enabled", isAdmin)
                .setFeatureFlag("help.enabled", false)
                .setFeatureFlag("overflow-menu.enabled", isAdmin)
                .setFeatureFlag("security-options.enabled", false)
                .setFeatureFlag("filmstrip.enabled", isAdmin)
                .setFeatureFlag("audio-mute.enabled", true)
                .setFeatureFlag("raise-hand.enabled", true)
                .setFeatureFlag("pip.enabled", false)
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
            Log.d(TAG, "onResponse: EndSession");
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
                            Utilities.openUnauthorizedDialog(LiveJitsiMeetActivity.this);
                        }
                    }

                }

                @Override
                public void onFailure(Call<EndLiveRoomResponse> call, Throwable t) {
                    Toast.makeText(LiveJitsiMeetActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
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
                            Log.d(TAG, "onResponse: AttendanceMArked");
                            Toast.makeText(LiveJitsiMeetActivity.this, "Attendence has been marked successfully", Toast.LENGTH_SHORT).show();
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(LiveJitsiMeetActivity.this);
                    } else {
                        Toast.makeText(LiveJitsiMeetActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<MarkAttendenceResponse> call, Throwable t) {
                Toast.makeText(LiveJitsiMeetActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
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
                        Log.d(TAG, "CONFERENCE_JOINED: ");
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
                        Log.d(TAG, "PARTICIPANT_JOINED: ");
                        Timber.i("Participant joined%s", event.getData().get("name"));
                        break;
                    case PARTICIPANT_LEFT:
                        Log.d(TAG, "PARTICIPANT_LEFT: " + event.getData());
                        String endCall = intent.getStringExtra("EndCall");
                        Log.d(TAG, "onBroadcastReceived: EndCall" + endCall);
                        break;
                    case AUDIO_MUTED_CHANGED:
                        break;
                    case CONFERENCE_WILL_JOIN:
                        break;
                    case CONFERENCE_TERMINATED:
                        Log.d(TAG, "CONFERENCE_TERMINATED: ");
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


}*/

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.so.luotk.utils.Utilities;

public class LiveJitsiMeetActivity extends AppCompatActivity {
    public static final String TAG = "LiveJitsiMeetActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
    }
}
