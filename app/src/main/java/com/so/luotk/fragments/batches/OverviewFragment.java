package com.so.luotk.fragments.batches;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.so.luotk.R;
import com.so.luotk.activities.LiveConferenceActivity;
import com.so.luotk.activities.MyLiveClass;
import com.so.luotk.adapter.OverviewTimingAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.BottomSheetDialogCommon;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.FragmentOverviewBinding;
import com.so.luotk.models.newmodels.liveRoomModel.LiveRoomModel;
import com.so.luotk.models.output.CreateJitsiRoomResponse;
import com.so.luotk.models.output.EndLiveRoomResponse;
import com.so.luotk.models.output.GetBatchLiveRoomResponse;
import com.so.luotk.models.output.GetBatchOverviewResponse;
import com.so.luotk.models.output.GetBatchOverviewResult;
import com.so.luotk.models.output.MarkAttendenceResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class OverviewFragment extends Fragment {

    private FragmentOverviewBinding binding;
    private RecyclerView recylerViewAssignment;
    private ArrayList<GetBatchOverviewResult> dayList;
    private OverviewTimingAdapter overviewTimingAdapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private String subjectName, className, batchCode, batch_color;
    private TextView tvSubjectName, tvClassName, tvBatchCode;
    private CardView layout_joining_request;
    private View layoutDataView, root_layout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Handler handler;
    private Runnable runnable;
    private boolean isFirstInternetToastDone, isFragmentLoaded;
    private boolean isDarkMode;
    int currentNightMode;
    private Context context;
    private Activity mActivity;
    private String live_option;

    //Changes By Vishal
    APIInterface apiInterface;
    private ProgressView mProgressDialog;
    private String isFROM, batchId, previousRoomName, previousRoomPassword, previousRoomId,
            jitsiRoomName, jitsiRoomPassword, jitsiRoomId, batcOrCourse, api_key, api_secret, sdk_key, sdk_secret;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinkedHashMap<String, Fragment> map;
    private ArrayList<String> bottomSheetList;
    private final Timer _timer = new Timer();
    private String roomName, roomPassword, roomId;

    //StudentLive
    private IntentFilter broadcastFilter;
    private String zoomMeetingId, zoomMeetingPassword;
    private String roomIdFromBroadcast, batchIdFromBroadcast, courseIdFromBroadcast;
    private boolean isAttendenceMarked = false;
    public OverviewFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    public static OverviewFragment newInstance(String batchId) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, batchId);
        fragment.setArguments(args);
        return fragment;
    }

    public static OverviewFragment newInstance(String batchId, String is_from) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, batchId);
        args.putString(ARG_PARAM2, is_from);
        fragment.setArguments(args);
        return fragment;
    }

    public static OverviewFragment newInstance(String batchId, String is_from, String batcOrCourse) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, batchId);
        args.putString(ARG_PARAM2, is_from);
        args.putString(ARG_PARAM3, batcOrCourse);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        broadcastFilter = new IntentFilter(PreferenceHandler.END_JITSI_ROOM_ACTION);
        super.onCreate(savedInstanceState);
        dayList = new ArrayList<>();
        currentNightMode = getActivity().getResources().getConfiguration().uiMode &
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


    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        if (isFROM.equalsIgnoreCase("student")) {
        if (endJitsiRoomReceiver != null)
            try {
                mActivity.unregisterReceiver(endJitsiRoomReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utilities.restrictScreenShot(getActivity());
// getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (getArguments() != null) {
            batchId = getArguments().getString(ARG_PARAM1);
        }
        if (getArguments() != null) {
            isFROM = getArguments().getString(ARG_PARAM2);
        }
        if (getArguments() != null) {
            batcOrCourse = getArguments().getString(ARG_PARAM3);
        }
        binding = FragmentOverviewBinding.inflate(inflater, container, false);

        if (isFROM.equalsIgnoreCase("student")) {
            binding.inviteButton.setText(getString(R.string.invite_friends));
            binding.goLiveButton.setText(getString(R.string.join_class));
        }
        map = new LinkedHashMap<>();
        bottomSheetList = new ArrayList<>();
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetOverView.bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        live_option = PreferenceHandler.readString(context, PreferenceHandler.LIVE_OPTION, "");

        //Zoom
        api_key = PreferenceHandler.readString(context, PreferenceHandler.ZOOM_APP_KEY, null);
        api_secret = PreferenceHandler.readString(context, PreferenceHandler.ZOOM_APP_SECRET, null);
        sdk_key = PreferenceHandler.readString(context, PreferenceHandler.ZOOM_SDK_KEY, null);
        sdk_secret = PreferenceHandler.readString(context, PreferenceHandler.ZOOM_SDK_SECRET, null);

        apiInterface = ApiUtils.getApiInterface();
        mProgressDialog = new ProgressView(context);
        tvSubjectName = binding.tvSubjectName;
        tvClassName = binding.tvClassName;
        tvBatchCode = binding.tvBatchCode;
        recylerViewAssignment = binding.overviewRecyclerView;
        layout_joining_request = binding.layoutJoiningRequest;
        recylerViewAssignment.setLayoutManager(new LinearLayoutManager(context));
        recylerViewAssignment.setNestedScrollingEnabled(false);
        root_layout = binding.getRoot();
        layoutDataView = binding.layoutDataView;
        shimmerFrameLayout = binding.shimmerLayout;
        handler = new Handler(Looper.myLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        if (PreferenceHandler.readBoolean(mActivity, PreferenceHandler.ISSTATICLOGIN, false)) {
            binding.goLiveButton.setVisibility(GONE);
        } else {
            binding.goLiveButton.setVisibility(View.VISIBLE);
        }

        if (isFROM.equalsIgnoreCase("adminrole")) {
            if (!TextUtils.isEmpty(live_option) && live_option.equalsIgnoreCase("2")) {
                binding.goLiveButton.setVisibility(GONE);
            }
        }
        binding.goLiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFROM.equalsIgnoreCase("adminrole")) {
                    if (!TextUtils.isEmpty(live_option) && live_option.equalsIgnoreCase("2")) {
                        Toast.makeText(context, getString(R.string.live_not_supported), Toast.LENGTH_SHORT).show();
                    } else {
                        if (!TextUtils.isEmpty(batchId))
                            if (Utilities.checkInternet(context)) {
                                hitGetLiveRoomService();
                            } else {
                                Toast.makeText(context, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                            }
                    }
                } else if (isFROM.equalsIgnoreCase("student")){
                    if (!TextUtils.isEmpty(live_option) && live_option.equalsIgnoreCase("2")) {
                        startZoomMeeting();
                       // Toast.makeText(context, context.getString(R.string.live_not_supported), Toast.LENGTH_SHORT).show();
                    } else {
                        if (roomName != null && roomPassword != null) {
                            if (isCameraPermitted(mActivity)) {
                                if (isAudioPermitted(mActivity)) {
                                    startJitsiConferenceStudent();
                                } else {
                                    requestAudioPermission(mActivity, 2000);
                                }
                            } else {
                                requestCameraPermission(1000);
                            }
                        }
                    }
                }
                /*if (!TextUtils.isEmpty(live_option) && live_option.equalsIgnoreCase("2")) {
                    Toast.makeText(context, getString(R.string.live_not_supported), Toast.LENGTH_SHORT).show();
                } else {
                    if (!TextUtils.isEmpty(batchId))
                        if (Utilities.checkInternet(context)) {
                            hitGetLiveRoomService();
                        } else {
                            Toast.makeText(context, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                        }
                }*/
            }
        });

        binding.inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int partyunicode = 0x1F389;
                int startStuckunicode = 0x1F929;
//                mytextview.setText(Html.fromHtml(sourceString));
                String BottomSheetMessage = "<b> Class: </b>" + className +"<br><b>Subject: </b>" + subjectName +"<br><b>Batch Code: </b>"+ batchCode;
                String OrgName = PreferenceHandler.readString(getActivity(), PreferenceHandler.ORG_NAME, null);
                String messageBody = "Wohoo!"+ getEmojiByUnicode(partyunicode) +"\n" + OrgName +" is now on google play store!"+getEmojiByUnicode(startStuckunicode)+"\nJoin the Batch " + subjectName + " \nBatch Code- "+ batchCode
                        +" \n \nTo download the app, click \nhttps://play.google.com/store/apps/details?id=" + getActivity().getPackageName();
                BottomSheetDialogCommon bottomSheetDialogCommon = new BottomSheetDialogCommon(getActivity(),BottomSheetMessage, messageBody);
                bottomSheetDialogCommon.show(getActivity().getSupportFragmentManager(), "exampleBottomSheet");
            }
        });
        binding.bottomSheetOverView.transparentClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        binding.batchId.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = binding.batchId.getCompoundDrawables()[2];
                if (drawable == null) {
                    return false;
                }
                //drawleft is less than, drawright is greater than
                //The left, right, up and down correspond to 0 1 2 3
                if (event.getX() > binding.batchId.getWidth() - binding.batchId.getCompoundDrawables()[2].getBounds().width()) {
                    setClipboard(getActivity(), binding.batchId.getText().toString());
                    if (isFROM.equals("student")) {
                        Toast.makeText(getActivity(), getString(R.string.batch_with_friends), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(),  getString(R.string.batch_with_students), Toast.LENGTH_SHORT).show();
                    }


                    return false;
                }
                return false;
            }
        });


        return binding.getRoot();
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
    private void hitGetCourseLiveRoomService(boolean val, String liveValue) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("type", liveValue);
        if (batcOrCourse.equalsIgnoreCase("batch"))
            map.put("batchId", batchId);
        else map.put("courseId", batchId);
            new MyClient(context).hitGetLiveRoomService(map, batcOrCourse, (content, error) -> {
               /* if (shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }
                layoutDataView.setVisibility(View.VISIBLE);*/
                if (content != null) {
                    LiveRoomModel response = (LiveRoomModel) content;
                    if (response.getSuccess() && response.getStatus() == 200) {
                        if (response.getResult() != null) {

                            if (!TextUtils.isEmpty(live_option) && live_option.equalsIgnoreCase("2")) {
                                roomName = response.getResult().getRoomName();
                                roomPassword = response.getResult().getRoomPassword();
                                roomId = response.getResult().getId().toString();
                                if (roomName != null) {
                                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                        binding.goLiveButton.setBackgroundColor(getActivity().getColor(R.color.join_red));
                                    } else {
                                        binding.goLiveButton.setBackgroundColor(getActivity().getResources().getColor(R.color.join_red));
                                    }
                                } else {
                                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                        binding.goLiveButton.setBackgroundColor(getActivity().getColor(R.color.disabled_btn));
                                    } else {
                                        binding.goLiveButton.setBackgroundColor(getActivity().getResources().getColor(R.color.disabled_btn));
                                    }

                                    binding.goLiveButton.setEnabled(false);
                                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                        binding.goLiveButton.setTextColor(getActivity().getColor(R.color.white));
                                    } else {
                                        binding.goLiveButton.setTextColor(getActivity().getResources().getColor(R.color.white));
                                    }
                                }
                            } else if (!TextUtils.isEmpty(live_option) && live_option.equalsIgnoreCase("3")) {
                                roomName = response.getResult().getRoomName();
                                roomPassword = response.getResult().getRoomPassword();
                                roomId = String.valueOf(response.getResult().getId());
                                try {
                                    if (roomName != null) {
                                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                            binding.goLiveButton.setBackgroundColor(getActivity().getColor(R.color.join_red));
                                        } else {
                                            binding.goLiveButton.setBackgroundColor(getActivity().getResources().getColor(R.color.join_red));
                                        }
                                        binding.goLiveButton.setEnabled(true);
                                    } else {
                                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                            binding.goLiveButton.setBackgroundColor(getActivity().getColor(R.color.disabled_btn));
                                        } else {
                                            binding.goLiveButton.setBackgroundColor(getActivity().getResources().getColor(R.color.disabled_btn));
                                        }

                                        binding.goLiveButton.setEnabled(false);
                                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                            binding.goLiveButton.setTextColor(getActivity().getColor(R.color.white));
                                        } else {
                                            binding.goLiveButton.setTextColor(getActivity().getResources().getColor(R.color.white));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                        binding.goLiveButton.setBackgroundColor(getActivity().getColor(R.color.disabled_btn));
                                    } else {
                                        binding.goLiveButton.setBackgroundColor(getActivity().getResources().getColor(R.color.disabled_btn));
                                    }
                                binding.goLiveButton.setEnabled(false);
                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                    binding.goLiveButton.setTextColor(getActivity().getColor(R.color.white));
                                } else {
                                    binding.goLiveButton.setTextColor(getActivity().getResources().getColor(R.color.white));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (val) {
                            checkInternet();
                        } else {
                            shimmerFrameLayout.setVisibility(GONE);
                            layoutDataView.setVisibility(View.VISIBLE);
                        }
                    } else if (response.getStatus() == 403)
                        Utilities.openUnauthorizedDialog(context);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }


    private void setupUI() {

        if (batch_color != null && !isDarkMode) {
            layout_joining_request.setCardBackgroundColor(Color.parseColor(batch_color));
        }
        if (subjectName != null || className != null || batchCode != null) {
            tvSubjectName.setText(subjectName);
            tvClassName.setText(className);
            tvBatchCode.setText(batchCode);
            binding.batchId.setText(batchCode);
            binding.classNameNew.setText(className);
        }
    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }
    private void checkInternet() {
        if (context != null && Utilities.checkInternet(context)) {
            handler.removeCallbacks(runnable);
            hitGetBatchOverviewService();
            isFragmentLoaded = true;
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Utilities.makeToast(context, getString(R.string.internet_connection_error));
                isFirstInternetToastDone = true;
            }

        }
    }


    private void setListAdapter() {
        if (dayList != null) {
            overviewTimingAdapter = new OverviewTimingAdapter(context, dayList, isDarkMode);
            recylerViewAssignment.setAdapter(overviewTimingAdapter);
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        Utilities.hideKeyBoard(context);
        if (!isFragmentLoaded) {
            shimmerFrameLayout.startShimmer();
//            checkInternet();
            setupUI();

            if (isFROM.equalsIgnoreCase("adminrole")) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    binding.goLiveButton.setBackgroundColor(getActivity().getColor(R.color.join_red));
                } else {
                    binding.goLiveButton.setBackgroundColor(getActivity().getResources().getColor(R.color.join_red));
                }

                binding.goLiveButton.setText(getString(R.string.go_live));
                checkInternet();
            } else if (isFROM.equalsIgnoreCase("student")){
                if (Utilities.checkInternet(context)) {
                    handler.removeCallbacks(runnable);
                    checkLiveOprion();
                } else {
                    handler.postDelayed(runnable, 5000);
                    if (!isFirstInternetToastDone) {
                        Utilities.makeToast(context, getString(R.string.internet_connection_error));
                        isFirstInternetToastDone = true;
                    }

                }
            }

        } else {
            if (isFROM.equalsIgnoreCase("adminrole")) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    binding.goLiveButton.setBackgroundColor(getActivity().getColor(R.color.join_red));
                } else {
                    binding.goLiveButton.setBackgroundColor(getActivity().getResources().getColor(R.color.join_red));
                }
                binding.goLiveButton.setText(getString(R.string.go_live));
            }

            if (isFROM.equalsIgnoreCase("student")) {
                handler = new Handler(Looper.myLooper());
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        checkLiveOprion();
                    }
                };
                checkLiveOprion();

            }
            setupUI();
            setListAdapter();
        }

        if (isFROM.equalsIgnoreCase("student")) {
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
    }

    private void checkLiveOprion() {
        if (!TextUtils.isEmpty(live_option) && live_option.equalsIgnoreCase("2")) {
            hitGetCourseLiveRoomService(true, "zoom");
        } else if (!TextUtils.isEmpty(live_option) && live_option.equalsIgnoreCase("3")) {
            hitGetCourseLiveRoomService(true, "jitsi");
        }
    }


    private void hitGetBatchOverviewService() {
        APIInterface apiInterface = ApiUtils.getApiInterface();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        Call<GetBatchOverviewResponse> call = apiInterface.getBatchOverview(headers, batchId);
        //mProgressDialog = new ProgressView(this);
        //mProgressDialog.show();
        call.enqueue(new Callback<GetBatchOverviewResponse>() {
            @Override
            public void onResponse(Call<GetBatchOverviewResponse> call, Response<GetBatchOverviewResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            dayList.clear();
                            dayList.addAll(response.body().getResult());
                            setListAdapter();
                        }
                        if (response.body().getExtra() != null) {
                            if (response.body().getExtra().getSubjectName() != null) {
                                subjectName = response.body().getExtra().getSubjectName();
                                tvSubjectName.setText(subjectName);

                            }
                            if (response.body().getExtra().getCourseName() != null) {
                                className = response.body().getExtra().getCourseName();
                                tvClassName.setText(className);
                                binding.classNameNew.setText(className);


                            }
                            if (response.body().getExtra().getBatchCode() != null) {
                                batchCode = response.body().getExtra().getBatchCode();
                                tvBatchCode.setText(batchCode);
                                binding.batchId.setText(batchCode);

                            }
                            if (response.body().getExtra().getHexColor() != null && !isDarkMode) {
                                batch_color = response.body().getExtra().getHexColor();
                                layout_joining_request.setCardBackgroundColor(Color.parseColor(batch_color));
                            }
                        }
                        if (shimmerFrameLayout.isShimmerStarted()) {
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(GONE);
                        }
                        layoutDataView.setVisibility(View.VISIBLE);
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(context);
                    } else {
                        Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<GetBatchOverviewResponse> call, Throwable t) {
                if (shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }
                layoutDataView.setVisibility(View.VISIBLE);
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hitGetLiveRoomService() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        Call<GetBatchLiveRoomResponse> call = apiInterface.getLiveRoom(headers, batchId, "jitsi");

        if (!mActivity.isFinishing() && !mProgressDialog.isShowing())
            mProgressDialog.show();
        call.enqueue(new Callback<GetBatchLiveRoomResponse>() {
            @Override
            public void onResponse(Call<GetBatchLiveRoomResponse> call, Response<GetBatchLiveRoomResponse> response) {
                if (!mActivity.isFinishing())
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

                            } else {
                                hitCreateJitsiRoomService();
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(context);
                    } else {
                        Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<GetBatchLiveRoomResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hitEndLiveRoomService(String zoomRoomId) {
        if (!zoomRoomId.isEmpty()) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
            Call<EndLiveRoomResponse> call = apiInterface.endLiveRoom(headers, batchId, zoomRoomId);
            mProgressDialog = new ProgressView(context);
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
                            Utilities.openUnauthorizedDialog(context);
                        } else {
                            Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onFailure(Call<EndLiveRoomResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void hitCreateJitsiRoomService() {
        APIInterface apiInterface = ApiUtils.getApiInterface();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        Call<CreateJitsiRoomResponse> call = apiInterface.createJitsiRoom(headers, batchId);
        mProgressDialog = new ProgressView(context);
        mProgressDialog.show();
        call.enqueue(new Callback<CreateJitsiRoomResponse>() {
            @Override
            public void onResponse(Call<CreateJitsiRoomResponse> call, Response<CreateJitsiRoomResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {

                    if (response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                        if (response.body().getResult() != null && response.body().getResult() != null && response.body().getResult() != null && response.body().getResult().getStatus().equalsIgnoreCase("true")) {
                            Toast.makeText(context, getString(R.string.room_created), Toast.LENGTH_SHORT).show();
                            PreferenceHandler.writeString(context, PreferenceHandler.ROOM_ID, response.body().getResult().getRoomName());
                            PreferenceHandler.writeString(context, PreferenceHandler.ROOM_PASSWORD, response.body().getResult().getRoomPassword());
                            jitsiRoomName = response.body().getResult().getRoomName();
                            jitsiRoomPassword = response.body().getResult().getRoomPassword();
                            jitsiRoomId = response.body().getResult().getId();

                            if (isCameraPermitted(mActivity)) {
                                if (isAudioPermitted(mActivity)) {
                                    startJitsiConference();
                                } else {
                                    requestAudioPermission(2000);
                                }
                            } else {
                                requestCameraPermission(1000);
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(context);
                    } else {
                        Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<CreateJitsiRoomResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startJitsiConference() {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            intent = new Intent(mActivity, MyLiveClass.class);
           /* if (PreferenceHandler.readBoolean(mActivity, PreferenceHandler.SCREENSHARE, false)) {
                intent = new Intent(mActivity, LiveJitsiMeetActivity.class);
            } else {
                intent = new Intent(mActivity, LiveJitsiMeetActivity.class);
            }*/

        } else {
            intent = new Intent(mActivity, LiveConferenceActivity.class);
        }
        intent.putExtra(PreferenceHandler.ROOM_NAME, jitsiRoomName);
        intent.putExtra(PreferenceHandler.ROOM_PASSWORD, jitsiRoomPassword);
        intent.putExtra(PreferenceHandler.ROOM_ID, jitsiRoomId);
        intent.putExtra(PreferenceHandler.BATCH_ID, batchId);
        intent.putExtra(PreferenceHandler.IS_ADMIN, true);
        startActivity(intent);
    }

    private void startJitsiConferenceStudent() {
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

    private boolean isAudioPermitted(Activity activity) {
        return checkSelfPermission(mActivity, Manifest.permission.RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED;
    }

    // request audio permission

    private void requestAudioPermission(int requestCode) {
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, requestCode);
    }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (isFROM.equalsIgnoreCase("adminrole")) {
            if (requestCode == 1000) {
                if (verifyPermissions(grantResults)) {
                    if (isAudioPermitted(mActivity)) {
                        startJitsiConference();
                    } else {
                        requestAudioPermission(2000);
                    }
                }
            } else if (requestCode == 2000) {
                if (verifyPermissions(grantResults)) {
                    startJitsiConference();
                }

            }
        } else if (isFROM.equalsIgnoreCase("student")){
            if (requestCode == 1000) {
                if (verifyPermissions(grantResults)) {
                    if (isAudioPermitted(mActivity)) {
                        startJitsiConferenceStudent();
                    } else {
                        requestAudioPermission(2000);
                    }
                }
            } else if (requestCode == 2000) {
                if (verifyPermissions(grantResults)) {
                    startJitsiConferenceStudent();
                }

            }
        }
    }

    /*private class BottomSheetAdapter extends RecyclerView.Adapter<ViewHolder>{

        public BottomSheetAdapter(Context context, List<String> bottomList, String isFrom, boolean darkMode) {
            this.context = context;
            this.batchList = batchList;
            this.isFrom = isFrom;
            dataListFull = new ArrayList<>(batchList);
            dayList = new ArrayList<>();
            this.isDarkMode = darkMode;

        }

    }*/


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

                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                binding.goLiveButton.setBackgroundColor(getActivity().getColor(R.color.disabled_btn));
                            } else {
                                binding.goLiveButton.setBackgroundColor(getActivity().getResources().getColor(R.color.disabled_btn));
                            }
                            binding.goLiveButton.setEnabled(false);
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                binding.goLiveButton.setTextColor(getActivity().getColor(R.color.white));
                            } else {
                                binding.goLiveButton.setTextColor(getActivity().getResources().getColor(R.color.white));
                            }

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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            binding.goLiveButton.setBackgroundColor(getActivity().getColor(R.color.disabled_btn));
        } else {
            binding.goLiveButton.setBackgroundColor(getActivity().getResources().getColor(R.color.disabled_btn));
        }
        binding.goLiveButton.setEnabled(false);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            binding.goLiveButton.setTextColor(getActivity().getColor(R.color.white));
        } else {
            binding.goLiveButton.setTextColor(getActivity().getResources().getColor(R.color.white));
        }
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
