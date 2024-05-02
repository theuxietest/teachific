package com.so.luotk.activities;


/*import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.modules.core.PermissionListener;
import com.so.vsznk.R;
import com.so.vsznk.api.APIInterface;
import com.so.vsznk.api.ApiUtils;
import com.so.vsznk.models.output.EndLiveRoomResponse;
import com.so.vsznk.models.output.GetBatchLiveRoomResponse;
import com.so.vsznk.models.output.MarkAttendenceResponse;
import com.so.vsznk.utils.PreferenceHandler;
import com.so.vsznk.utils.Utilities;

import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.JitsiMeetViewListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LiveConferenceActivity extends AppCompatActivity implements JitsiMeetActivityInterface, JitsiMeetViewListener {
    public static final String TAG = "LiveConference";
    private JitsiMeetView view;
    private String jitsiRoomId, batchId, jitsiRoomName, jitsiRoomPassword, orgName, loggedInUserName;
    private boolean isAdmin, isLiveStreamOn;
    private IntentFilter broadcastFilter;
    private String live_option;
    private String roomIdFromBroadcast, batchIdFromBroadcast, courseIdFromBroadcast, currentBatchId, currentRoomName, currentRoomId;
    private boolean isFullScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
        orgName = PreferenceHandler.readString(LiveConferenceActivity.this, PreferenceHandler.ORG_NAME, null);
        loggedInUserName = PreferenceHandler.readString(LiveConferenceActivity.this, PreferenceHandler.LOGGED_IN_USERNAME, null);
        live_option = PreferenceHandler.readString(LiveConferenceActivity.this, PreferenceHandler.LIVE_OPTION, null);
        if (getIntent() != null) {
            jitsiRoomName = getIntent().getStringExtra(PreferenceHandler.ROOM_NAME);
            jitsiRoomPassword = getIntent().getStringExtra(PreferenceHandler.ROOM_PASSWORD);
            isAdmin = getIntent().getBooleanExtra(PreferenceHandler.IS_ADMIN, false);
            jitsiRoomId = getIntent().getStringExtra(PreferenceHandler.ROOM_ID);
            batchId = getIntent().getStringExtra(PreferenceHandler.BATCH_ID);
        }
        view = new JitsiMeetView(this);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(view);

        if (jitsiRoomName != null) {
            JitsiMeetConferenceOptions options = getOptions();
            view.join(options);
        }
        view.setListener(this);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        JitsiMeetActivityDelegate.onActivityResult(
                this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        showExitMeetingDialog();
    }

    private void showExitMeetingDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LiveConferenceActivity.this);
        alertBuilder.setTitle("Live Class");
        if (isAdmin) {
            alertBuilder.setMessage("Do you want to end the class?");
        } else {
            alertBuilder.setMessage("Do you want to leave the class?");
        }
        alertBuilder.setPositiveButton("Yes", (dialogInterface, i) -> {
            if (isAdmin) {
                if (jitsiRoomId != null) {
                    if (Utilities.checkInternet(LiveConferenceActivity.this)) {
                        hitEndLiveRoomService(jitsiRoomId);
                    } else {
                        Toast.makeText(LiveConferenceActivity.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (jitsiRoomId != null)
                    leaveMeeting();
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = alertBuilder.create();
        dialog.show();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveMeeting();

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        JitsiMeetActivityDelegate.onNewIntent(intent);
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
    protected void onResume() {
        super.onResume();
        if (!isAdmin) {
            try {
                registerReceiver(endJitsiRoomReceiver, broadcastFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (view != null && batchId != null && !isAdmin) {
                if (Utilities.checkInternet(this)) {
                } else {
                    Toast.makeText(this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAdmin && endJitsiRoomReceiver != null) {
            try {
                unregisterReceiver(endJitsiRoomReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //  JitsiMeetActivityDelegate.onHostPause(this);
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
                .setFeatureFlag("pip.enabled", false)
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
                .setFeatureFlag("pip.enabled", false)
                .setFeatureFlag("welcomepage.enabled", false)

                .build();
    }

    @Override
    public void onConferenceJoined(Map<String, Object> map) {
        Log.d("TAG", "onConferenceJoined: ");
        if (Utilities.checkInternet(this)) {
            if (!isAdmin) {
                if (batchId != null && jitsiRoomId != null) {
                    hitMarkAttendenceService();
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConferenceTerminated(Map<String, Object> map) {
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
            leaveMeeting();
            finish();
        }
    }

    private void leaveMeeting() {
        if (view != null) {
            view.leave();
        }
    }

    @Override
    public void onConferenceWillJoin(Map<String, Object> map) {
    }

    @Override
    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {

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
                                leaveMeeting();
                                finish();
                            }
                        } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                            Utilities.openUnauthorizedDialog(LiveConferenceActivity.this);
                        }
                    }

                }

                @Override
                public void onFailure(Call<EndLiveRoomResponse> call, Throwable t) {
                    Toast.makeText(LiveConferenceActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isAdmin) {
            broadcastFilter = new IntentFilter(PreferenceHandler.END_JITSI_ROOM_ACTION);
            try {
                registerReceiver(endJitsiRoomReceiver, broadcastFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final BroadcastReceiver endJitsiRoomReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(PreferenceHandler.END_JITSI_ROOM_ACTION)) {
                    batchIdFromBroadcast = intent.getStringExtra(PreferenceHandler.BATCH_ID);
                    roomIdFromBroadcast = intent.getStringExtra(PreferenceHandler.ROOM_ID);
                    courseIdFromBroadcast = intent.getStringExtra(PreferenceHandler.COURSE_ID);
                    if (!isAdmin) {
                        PreferenceHandler.writeBoolean(LiveConferenceActivity.this, PreferenceHandler.END_JITSI_ROOM_ACTION, true);
                        if (batchId != null && (batchIdFromBroadcast != null || courseIdFromBroadcast != null) && jitsiRoomId != null && roomIdFromBroadcast != null)
                            if ((batchId.equalsIgnoreCase(batchIdFromBroadcast) || batchId.equalsIgnoreCase(courseIdFromBroadcast)) && jitsiRoomId.equalsIgnoreCase(roomIdFromBroadcast)) {
                                leaveMeeting();
                                finish();
                            }
                    }
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (!isAdmin && endJitsiRoomReceiver != null) {
            try {
                unregisterReceiver(endJitsiRoomReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    private void hitGetLiveRoomService() {
        APIInterface apiInterface = ApiUtils.getApiInterface();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));
        Call<GetBatchLiveRoomResponse> call = apiInterface.getLiveRoom(headers, batchId, "jitsi");
        call.enqueue(new Callback<GetBatchLiveRoomResponse>() {
            @Override
            public void onResponse(Call<GetBatchLiveRoomResponse> call, Response<GetBatchLiveRoomResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null) {
                                currentBatchId = response.body().getResult().getFk_batchId();
                                currentRoomName = response.body().getResult().getRoomName();
                                currentRoomId = response.body().getResult().getRoomPassword();
                                if (currentRoomName != null && currentRoomId != null) {
                                }
                            } else {
                                if (!isAdmin) {
                                    PreferenceHandler.writeBoolean(LiveConferenceActivity.this, PreferenceHandler.END_JITSI_ROOM_ACTION, true);
                                    leaveMeeting();
                                    finish();
                                }
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(LiveConferenceActivity.this);
                    } else {
                        Toast.makeText(LiveConferenceActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<GetBatchLiveRoomResponse> call, Throwable t) {
                Toast.makeText(LiveConferenceActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
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
                            Toast.makeText(LiveConferenceActivity.this, "Attendence has been marked successfully", Toast.LENGTH_SHORT).show();
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(LiveConferenceActivity.this);
                    } else {
                        Toast.makeText(LiveConferenceActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<MarkAttendenceResponse> call, Throwable t) {
                Toast.makeText(LiveConferenceActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
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

}*/

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.so.luotk.utils.Utilities;

public class LiveConferenceActivity extends AppCompatActivity {
    public static final String TAG = "LiveJitsiMeetActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
    }
}

