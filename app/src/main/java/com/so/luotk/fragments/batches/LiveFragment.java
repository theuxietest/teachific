package com.so.luotk.fragments.batches;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.so.luotk.R;
import com.so.luotk.activities.LiveConferenceActivity;
import com.so.luotk.activities.MyLiveClass;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.models.newmodels.liveRoomModel.LiveRoomModel;
import com.so.luotk.models.output.MarkAttendenceResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveFragment extends Fragment {
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";
    private TextView btnJoinLiveClass;
    private final static String TAG = "SmartOwls";
    private final boolean mbPendingStartMeeting = false;
    private String batchId;
    private ProgressView mProgressDialog;
    private String roomName, roomPassword, roomId;
    private TextView tvLiveText;
    private IntentFilter broadcastFilter;
    private ImageView imgLive;
    private View layoutDataView, root_layout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Handler handler;
    private Runnable runnable;
    private boolean isFirstInternetToastDone;
    private String roomIdFromBroadcast, batchIdFromBroadcast, courseIdFromBroadcast;
    private String live_option, isFrom;
    private Context context;
    private Activity mActivity;
    private boolean isAttendenceMarked = false;

    public static LiveFragment newInstance(String batchId, String isFrom) {
        LiveFragment fragment = new LiveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, batchId);
        args.putString(ARG_PARAM2, isFrom);
        fragment.setArguments(args);
        return fragment;
    }

    public LiveFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        broadcastFilter = new IntentFilter(PreferenceHandler.END_JITSI_ROOM_ACTION);
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            batchId = getArguments().getString(ARG_PARAM1);
            isFrom = getArguments().getString(ARG_PARAM2);
        }
        Utilities.restrictScreenShot(getActivity());
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        live_option = PreferenceHandler.readString(context, PreferenceHandler.LIVE_OPTION, "");

        setupView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.hideKeyBoard(context);
        handler = new Handler(Looper.myLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        shimmerFrameLayout.startShimmer();
        checkInternet();

        if (endJitsiRoomReceiver != null)
            try {
                mActivity.registerReceiver(endJitsiRoomReceiver, broadcastFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        // if (isFrom.equals("batch"))
        if (PreferenceHandler.readBoolean(context, PreferenceHandler.END_JITSI_ROOM_ACTION, false)) {
            showMeetingEndPopUp();
        }
    }

    private void setupView(View view) {
        tvLiveText = view.findViewById(R.id.tv_live);
        btnJoinLiveClass = view.findViewById(R.id.btn_join_live_class);
        btnJoinLiveClass.setVisibility(View.GONE);
        imgLive = view.findViewById(R.id.live_img);
        root_layout = view.findViewById(R.id.root_layout);
        layoutDataView = view.findViewById(R.id.layout_data_view);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        btnJoinLiveClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(live_option) && live_option.equalsIgnoreCase("2")) {
                    startZoomMeeting();
                } else {
                    if (roomName != null && roomPassword != null) {
                        if (isCameraPermitted(mActivity)) {
                            if (isAudioPermitted(mActivity)) {
                                startJitsiConference();
                            } else {
                                requestAudioPermission(mActivity, 2000);
                            }
                        } else {
                            requestCameraPermission(1000);
                        }
                    }
                }
            }
        });
    }
    private void startZoomMeeting() {
        if (roomName != null && roomPassword != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("zoomus://join?action=join&confno="+roomName+"&pwd="+roomPassword +""));
            if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                hitMarkAttendenceService();
                try {
                    startActivity(intent);
                } catch(Exception e) {
                }
            }  else {
                openZoomAppIntent();
            }
        }
    }

    private void openZoomAppIntent() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=us.zoom.videomeetings")));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=us.zoom.videomeetings")));
        }
    }
    private void checkInternet() {
        if (Utilities.checkInternet(context)) {
            handler.removeCallbacks(runnable);
            if (!TextUtils.isEmpty(live_option) && live_option.equalsIgnoreCase("2")) {
                hitGetCourseLiveRoomService("zoom");
            } else {
                hitGetCourseLiveRoomService("jitsi");
            }

        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Utilities.makeToast(context, getString(R.string.internet_connection_error));
                isFirstInternetToastDone = true;
            }

        }
    }

    private void hitGetCourseLiveRoomService(String liveType) {
        Map<String, String> map = new HashMap<>();
        map.put("type", liveType);
        if (isFrom.equalsIgnoreCase("batch"))
            map.put("batchId", batchId);
        else map.put("courseId", batchId);
        new MyClient(context).hitGetLiveRoomService(map, isFrom, (content, error) -> {
            if (shimmerFrameLayout.isShimmerStarted()) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(GONE);
            }
            layoutDataView.setVisibility(View.VISIBLE);
            if (content != null) {
                LiveRoomModel response = (LiveRoomModel) content;
                if (response.getSuccess() && response.getStatus() == 200) {
                    if (response.getResult() != null) {
                        roomName = response.getResult().getRoomName();
                        roomPassword = response.getResult().getRoomPassword();
                        roomId = String.valueOf(response.getResult().getId());
                        if (roomName != null) {
                            tvLiveText.setText(R.string.live_now);
                            btnJoinLiveClass.setVisibility(View.VISIBLE);
                            imgLive.setImageResource(R.drawable.ic_live_new);
                        } else {
                            tvLiveText.setText(R.string.your_tutor_is_not_live_nright_now);
                            btnJoinLiveClass.setVisibility(View.GONE);
                            imgLive.setImageResource(R.drawable.ic_live_not_new);
                        }
                    } else {
                        tvLiveText.setText(R.string.your_tutor_is_not_live_nright_now);
                        btnJoinLiveClass.setVisibility(View.GONE);
                        imgLive.setImageResource(R.drawable.ic_live_not_new);
                    }

                } else if (response.getStatus() == 403)
                    Utilities.openUnauthorizedDialog(context);
            }
           /* try {
                if (isFrom.equals("batch")) {
                    boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                    if (!isAdmn) {
                        if (BatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                            BatchDetailActivity.shimmerFrameLayout.stopShimmer();
                            BatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                            BatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                            BatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (verifyPermissions(grantResults)) {
                if (isAudioPermitted(mActivity)) {
                    startJitsiConference();
                } else {
                    requestAudioPermission(mActivity, 2000);
                }
            }
        } else if (requestCode == 2000) {
            if (verifyPermissions(grantResults)) {
                startJitsiConference();
            }

        }
    }

    private void startJitsiConference() {
        if (batchId != null) {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                intent = new Intent(mActivity, MyLiveClass.class);
                /*if (PreferenceHandler.readBoolean(mActivity, PreferenceHandler.SCREENSHARE, false)) {
                    intent = new Intent(mActivity, LiveConferenceActivity.class);
                } else {
                    intent = new Intent(mActivity, LiveConferenceActivity.class);
                }*/

            } else {
                intent = new Intent(mActivity, LiveConferenceActivity.class);
            }
            intent.putExtra(PreferenceHandler.ROOM_NAME, roomName);
            intent.putExtra(PreferenceHandler.ROOM_PASSWORD, roomPassword);
            intent.putExtra(PreferenceHandler.ROOM_ID, roomId);
            intent.putExtra(PreferenceHandler.BATCH_ID, batchId);
            intent.putExtra(PreferenceHandler.IS_ADMIN, false);
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        if (endJitsiRoomReceiver != null)
            try {
                mActivity.unregisterReceiver(endJitsiRoomReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private final BroadcastReceiver endJitsiRoomReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(PreferenceHandler.END_JITSI_ROOM_ACTION)) {
                    batchIdFromBroadcast = intent.getStringExtra(PreferenceHandler.BATCH_ID);
                    roomIdFromBroadcast = intent.getStringExtra(PreferenceHandler.ROOM_ID);
                    courseIdFromBroadcast = intent.getStringExtra(PreferenceHandler.COURSE_ID);
                    //PreferenceHandler.writeString(LiveConferenceActivity.this, PreferenceHandler.BROADCAST_ROOM_ID, roomIdFromBroadcast);
                    PreferenceHandler.writeBoolean(context, PreferenceHandler.END_JITSI_ROOM_ACTION, true);
                    // PreferenceHandler.writeString(LiveConferenceActivity.this, PreferenceHandler.BROADCAST_BATCH_ID, batchIdFromBroadcast);

                    if (batchId != null && (batchIdFromBroadcast != null || courseIdFromBroadcast != null) && roomId != null && roomIdFromBroadcast != null)
                        if ((batchId.equalsIgnoreCase(batchIdFromBroadcast) || batchId.equalsIgnoreCase(courseIdFromBroadcast)) && roomId.equalsIgnoreCase(roomIdFromBroadcast)) {
                            tvLiveText.setText(R.string.your_tutor_is_not_live_nright_now);
                            imgLive.setImageResource(R.drawable.ic_live_not_new);
                            btnJoinLiveClass.setVisibility(View.GONE);

                        }


                }
            }
        }
    };


    public void showMeetingEndPopUp() {
        Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_live_end_popup);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDialog.show();
        mDialog.setCancelable(true);
        mDialog.getWindow().setAttributes(lp);
        TextView tvMsg = mDialog.findViewById(R.id.tv_msg);
        TextView tvUsername = mDialog.findViewById(R.id.tv_username);
        tvLiveText.setText(R.string.your_tutor_is_not_live_nright_now);
        imgLive.setImageResource(R.drawable.ic_live_not_new);
        btnJoinLiveClass.setVisibility(View.GONE);
        tvUsername.setText("Dear " + PreferenceHandler.readString(context, PreferenceHandler.LOGGED_IN_USERNAME, "") + " , ");
        tvMsg.setText(context.getString(R.string.live_end_msg));
        TextView btnOk = mDialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                PreferenceHandler.writeBoolean(context, PreferenceHandler.END_JITSI_ROOM_ACTION, false);
            }
        });
        mDialog.show();


    }

    // Check for audio permission

    private boolean isAudioPermitted(Activity activity) {
        return checkSelfPermission(mActivity, Manifest.permission.RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED;
    }

    // request audio permission

    private void requestAudioPermission(Activity activity, int requestCode) {
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, requestCode);
    }


    // Check for Camera permission

    private boolean isCameraPermitted(Activity activity) {
        return checkSelfPermission(activity, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED;
    }


    // request Camera permission

    private void requestCameraPermission(int requestCode) {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
    }

    private boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1)
            return false;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    private void hitMarkAttendenceService() {
        APIInterface apiInterface = ApiUtils.getApiInterface();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        Call<MarkAttendenceResponse> call = apiInterface.markStudentLiveAttendence(headers, batchId, roomId);
        //mProgressDialog = new ProgressView(this);
        //mProgressDialog.show();
        call.enqueue(new Callback<MarkAttendenceResponse>() {
            @Override
            public void onResponse(Call<MarkAttendenceResponse> call, Response<MarkAttendenceResponse> response) {
                //mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            Toast.makeText(context, "Attendence has been marked successfully", Toast.LENGTH_SHORT).show();
                            isAttendenceMarked = true;
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(context);
                    }
                }

            }

            @Override
            public void onFailure(Call<MarkAttendenceResponse> call, Throwable t) {
                // mProgressDialog.dismiss();
                Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
