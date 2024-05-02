package com.so.luotk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.so.luotk.adapter.BatchListAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.models.output.BatchListResult;
import com.so.luotk.models.output.CreateJitsiRoomResponse;
import com.so.luotk.models.output.EndLiveRoomResponse;
import com.so.luotk.models.output.GetBatchListResponse;
import com.so.luotk.models.output.GetBatchLiveRoomResponse;
import com.so.luotk.models.output.LogoutResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.so.luotk.R;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AdminMainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigation;
    private RelativeLayout layoutMainView;
    private Fragment activeFragment, chatFragment, liveFragment, settingFragment;


    private RecyclerView recylerViewBatches;
    private ArrayList<BatchListResult> batchLists;
    private BatchListAdapter batchListAdapter;
    private LinearLayout layoutBatchList;
    private CardView layoutNoAnyBatch;
    private RelativeLayout layoutBatch;
    private FloatingActionButton fab;
    private APIInterface apiInterface;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvLogout, tvWelcomeTitle, toolbar_title;
    private String batchId, orgName;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Handler handler;
    private Runnable runnable;
    private boolean isFirstInternetToastDone;
    private final static String TAG = "VideoConference";
    private String zoomMeetingId, zoomMeetingPassword;
    private String previousRoomName, previousRoomPassword, previousRoomId;
    private ProgressView mProgressDialog;
    private String api_key, api_secret, sdk_key, sdk_secret;
    private String roomName, jitsiRoomName, jitsiRoomPassword;
    private String jitsiRoomId;
    private static AdminMainActivity instance;
    private boolean isDarkMode;
    private String live_option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
        setContentView(R.layout.activity_admin_main);
        Log.d(TAG, "onCreate: Admin Teacher" );
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        int currentNightMode = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                isDarkMode = false;
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                isDarkMode = true;
                break;
        }
        instance = this;
        setupUI();
    }

    public static AdminMainActivity getInstance() {
        return instance;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    private void setupUI() {
        apiInterface = ApiUtils.getApiInterface();
        api_key = PreferenceHandler.readString(this, PreferenceHandler.ZOOM_APP_KEY, null);
        api_secret = PreferenceHandler.readString(this, PreferenceHandler.ZOOM_APP_SECRET, null);
        sdk_key = PreferenceHandler.readString(this, PreferenceHandler.ZOOM_SDK_KEY, null);
        sdk_secret = PreferenceHandler.readString(this, PreferenceHandler.ZOOM_SDK_SECRET, null);
        live_option = PreferenceHandler.readString(this, PreferenceHandler.LIVE_OPTION, "");
        orgName = PreferenceHandler.readString(AdminMainActivity.this, PreferenceHandler.ORG_NAME, null);
        tvLogout = findViewById(R.id.tv_logout);
        recylerViewBatches = findViewById(R.id.batch_recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        layoutBatchList = findViewById(R.id.layout_batch_list);
        layoutNoAnyBatch = findViewById(R.id.layout_no_any_batch);
        layoutBatch = findViewById(R.id.layout_batch);
        tvWelcomeTitle = findViewById(R.id.welcome_title);
        toolbar_title = findViewById(R.id.toolbar_title);
        batchLists = new ArrayList<>();
        recylerViewBatches.setLayoutManager(new LinearLayoutManager(this));
        recylerViewBatches.setNestedScrollingEnabled(false);
        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        shimmerFrameLayout.startShimmer();
        if (orgName != null) {
            /*  tvWelcomeTitle.setText("Welcome to " *//*+ "\n"*//* + orgName);*/
            toolbar_title.setText(orgName);
        }
        tvWelcomeTitle.setText("Batches");
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Utilities.showLogoutAlert(AdminMainActivity.this, orgName, true);
                showLogoutAlert(AdminMainActivity.this, orgName);
            }
        });

        handler = new Handler(Looper.myLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        checkInternet();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.checkInternet(AdminMainActivity.this)) {
                    hitGetBatchListService();
                } else {
                    Toast.makeText(AdminMainActivity.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void checkInternet() {
        if (Utilities.checkInternet(this)) {
            handler.removeCallbacks(runnable);
            hitGetBatchListService();
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    private void hitGetBatchListService() {
        batchLists.clear();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));
        Call<GetBatchListResponse> call = apiInterface.getAdminBatchList(headers);
        if (!swipeRefreshLayout.isRefreshing()) {
            //mProgressDialog = new ProgressView(this);
            // mProgressDialog.show();
        }

        call.enqueue(new Callback<GetBatchListResponse>() {
            @Override
            public void onResponse(Call<GetBatchListResponse> call, Response<GetBatchListResponse> response) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                } else {
                    //mProgressDialog.dismiss();
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
                tvWelcomeTitle.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null && response.body().getResult().size() > 0) {
                                layoutBatchList.setVisibility(View.VISIBLE);
                                layoutNoAnyBatch.setVisibility(View.GONE);
                                batchLists.addAll(response.body().getResult());
                                setBatchListAdapter();
                            } else {
                                layoutBatchList.setVisibility(View.GONE);
                                layoutNoAnyBatch.setVisibility(View.VISIBLE);
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(AdminMainActivity.this);
                    } else {
                        Toast.makeText(AdminMainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<GetBatchListResponse> call, Throwable t) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
                tvWelcomeTitle.setVisibility(View.VISIBLE);
                Toast.makeText(AdminMainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBatchListAdapter() {
        if (batchLists != null) {
            batchListAdapter = new BatchListAdapter(this, batchLists, "admin", isDarkMode);
            recylerViewBatches.setAdapter(batchListAdapter);
            setClickListener();
        }
    }

    /**
     * Method to set click listener on recycler view
     */
    private void setClickListener() {

        batchListAdapter.SetOnItemClickListener(new BatchListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (!TextUtils.isEmpty(live_option) && live_option.equalsIgnoreCase("2")) {
                    Toast.makeText(AdminMainActivity.this, "Live opton is not supported on this app", Toast.LENGTH_SHORT).show();
                } else {
                    batchId = batchLists.get(position).getId();
                    if (Utilities.checkInternet(AdminMainActivity.this)) {
                        hitGetLiveRoomService();
                    } else {
                        Toast.makeText(AdminMainActivity.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    /**
     * callback method for permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (Utilities.verifyPermissions(grantResults)) {
                if (Utilities.isAudioPermitted(AdminMainActivity.this)) {
                    startJitsiConference();
                } else {
                    Utilities.requestAudioPermission(AdminMainActivity.this, 2000);
                }
            }
        } else if (requestCode == 2000) {
            if (Utilities.verifyPermissions(grantResults)) {
                startJitsiConference();
            }

        }
    }

    private void startJitsiConference() {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            intent = new Intent(AdminMainActivity.this, MyLiveClass.class);
           /* if (PreferenceHandler.readBoolean(AdminMainActivity.this, PreferenceHandler.SCREENSHARE, false)) {
                intent = new Intent(AdminMainActivity.this, LiveJitsiMeetActivity.class);
            } else {
                intent = new Intent(AdminMainActivity.this, LiveConferenceActivity.class);
            }*/
        } else {
            intent = new Intent(AdminMainActivity.this, LiveConferenceActivity.class);
        }
        intent.putExtra(PreferenceHandler.ROOM_NAME, jitsiRoomName);
        intent.putExtra(PreferenceHandler.ROOM_PASSWORD, jitsiRoomPassword);
        intent.putExtra(PreferenceHandler.ROOM_ID, jitsiRoomId);
        intent.putExtra(PreferenceHandler.BATCH_ID, batchId);
        intent.putExtra(PreferenceHandler.IS_ADMIN, true);
        startActivity(intent);
    }


    private void hitEndLiveRoomService(String zoomRoomId) {
        if (!zoomRoomId.isEmpty()) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));
            Call<EndLiveRoomResponse> call = apiInterface.endLiveRoom(headers, batchId, zoomRoomId);
            mProgressDialog = new ProgressView(this);
            if (!isFinishing())
                mProgressDialog.show();
            call.enqueue(new Callback<EndLiveRoomResponse>() {
                @Override
                public void onResponse(Call<EndLiveRoomResponse> call, Response<EndLiveRoomResponse> response) {
                    mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("200")) {
                            if (response.body().getStatus() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                                if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                                    hitCreateJitsiRoomService();
                                }
                            }
                        } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                            Utilities.openUnauthorizedDialog(AdminMainActivity.this);
                        } else {
                            Toast.makeText(AdminMainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onFailure(Call<EndLiveRoomResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(AdminMainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void hitGetLiveRoomService() {
        APIInterface apiInterface = ApiUtils.getApiInterface();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));
        Call<GetBatchLiveRoomResponse> call = apiInterface.getLiveRoom(headers, batchId, "jitsi");
        mProgressDialog = new ProgressView(this);
        mProgressDialog.show();
        call.enqueue(new Callback<GetBatchLiveRoomResponse>() {
            @Override
            public void onResponse(Call<GetBatchLiveRoomResponse> call, Response<GetBatchLiveRoomResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getStatus() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null) {
                                previousRoomName = response.body().getResult().getRoomName();
                                previousRoomPassword = response.body().getResult().getRoomPassword();
                                previousRoomId = response.body().getResult().getId();
                                if (previousRoomName != null && previousRoomPassword != null) {
                                    hitEndLiveRoomService(previousRoomId);

                                }
                           /* if (!previousRoomName.equalsIgnoreCase(zoomMeetingId) && !previousRoomPassword.equalsIgnoreCase(zoomMeetingPassword)) {
                                if (Utilities.checkInternet(AdminMainActivity.this)) {
                                    hitEndLiveRoomService(previousRoomId);
                                   hitCreateZoomRoomService();

                                } else {
                                    Toast.makeText(AdminMainActivity.this, "Check internet connection", Toast.LENGTH_SHORT).show();
                                }
                            }*/
                            } else {
                                hitCreateJitsiRoomService();
                           /* if (Utilities.checkInternet(AdminMainActivity.this)) {
                                hitCreateZoomRoomService();
                            } else {
                                Toast.makeText(AdminMainActivity.this, "Check internet connection", Toast.LENGTH_SHORT).show();
                            }*/
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(AdminMainActivity.this);
                    } else {
                        Toast.makeText(AdminMainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<GetBatchLiveRoomResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(AdminMainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void hitCreateJitsiRoomService() {
        APIInterface apiInterface = ApiUtils.getApiInterface();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));
        Call<CreateJitsiRoomResponse> call = apiInterface.createJitsiRoom(headers, batchId);
        mProgressDialog = new ProgressView(this);
        mProgressDialog.show();
        call.enqueue(new Callback<CreateJitsiRoomResponse>() {
            @Override
            public void onResponse(Call<CreateJitsiRoomResponse> call, Response<CreateJitsiRoomResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {

                    if (response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                        if (response.body().getResult() != null && response.body().getResult() != null && response.body().getResult() != null && response.body().getResult().getStatus().equalsIgnoreCase("true")) {
                            Toast.makeText(AdminMainActivity.this, "Room has been created successfully", Toast.LENGTH_SHORT).show();
                            PreferenceHandler.writeString(AdminMainActivity.this, PreferenceHandler.ROOM_ID, response.body().getResult().getRoomName());
                            PreferenceHandler.writeString(AdminMainActivity.this, PreferenceHandler.ROOM_PASSWORD, response.body().getResult().getRoomPassword());
                            jitsiRoomName = response.body().getResult().getRoomName();
                            jitsiRoomPassword = response.body().getResult().getRoomPassword();
                            jitsiRoomId = response.body().getResult().getId();

                            if (Utilities.isCameraPermitted(AdminMainActivity.this)) {
                                if (Utilities.isAudioPermitted(AdminMainActivity.this)) {
                                    startJitsiConference();
                                } else {
                                    Utilities.requestAudioPermission(AdminMainActivity.this, 2000);
                                }
                            } else {
                                Utilities.requestCameraPermission(AdminMainActivity.this, 1000);
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(AdminMainActivity.this);
                    } else {
                        Toast.makeText(AdminMainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<CreateJitsiRoomResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(AdminMainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showLogoutAlert(final Context context, String appName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(appName);
        builder.setMessage("Are you sure you want to logout?");
        builder.setCancelable(false);
        builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hitLogoutService();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        Button pButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pButton.setTextColor(ContextCompat.getColor(context, R.color.blue_main));
        Button nButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nButton.setTextColor(ContextCompat.getColor(context, R.color.blue_main));
    }

    public void hitLogoutService() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));
        Call<LogoutResponse> call = apiInterface.logout(headers);
        mProgressDialog = new ProgressView(this);
        mProgressDialog.show();
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                        PreferenceHandler.writeBoolean(AdminMainActivity.this, PreferenceHandler.ADMIN_LOGGED_IN, false);
                        Intent intent = new Intent(AdminMainActivity.this, WelcomeActivityNew.class);
                        intent.putExtra("isFromLogout", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);


                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(AdminMainActivity.this);
                    } else {
                        Toast.makeText(AdminMainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(AdminMainActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
