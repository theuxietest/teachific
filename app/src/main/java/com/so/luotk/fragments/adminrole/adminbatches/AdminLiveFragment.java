package com.so.luotk.fragments.adminrole.adminbatches;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import com.so.luotk.R;
import com.so.luotk.activities.LiveConferenceActivity;
import com.so.luotk.activities.MyLiveClass;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.FragmentAdminLiveBinding;
import com.so.luotk.models.output.CreateJitsiRoomResponse;
import com.so.luotk.models.output.EndLiveRoomResponse;
import com.so.luotk.models.output.GetBatchLiveRoomResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class AdminLiveFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentAdminLiveBinding binding;
    private String live_option;
    APIInterface apiInterface;
    private String batchId, previousRoomName, previousRoomPassword, previousRoomId,
            jitsiRoomName, jitsiRoomPassword, jitsiRoomId;
    private String mParam2;
    private ProgressView mProgressDialog;
    private Context context;
    private Activity mActivity;

    public AdminLiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }


    public static AdminLiveFragment newInstance(String param1) {
        AdminLiveFragment fragment = new AdminLiveFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            batchId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminLiveBinding.inflate(inflater, container, false);
        live_option = PreferenceHandler.readString(context, PreferenceHandler.LIVE_OPTION, "");
        apiInterface = ApiUtils.getApiInterface();
        mProgressDialog = new ProgressView(context);
        binding.btnGoLive.setOnClickListener(view -> {
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
        });
        return binding.getRoot();
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

    private boolean isAudioPermitted(Activity activity) {
        return checkSelfPermission(mActivity, Manifest.permission.RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED;
    }

    // request audio permission

    private void requestAudioPermission(int requestCode) {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PreferenceHandler.readBoolean(getActivity(), PreferenceHandler.ADMIN_LOGGED_IN, false)) {
//            AdminBatchDetailsFragment.img_settings_icon.setVisibility(View.GONE);
        }
    }


}