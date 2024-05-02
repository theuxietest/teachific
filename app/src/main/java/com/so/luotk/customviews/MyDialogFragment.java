package com.so.luotk.customviews;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.so.luotk.R;
import com.so.luotk.activities.WelcomeActivityNew;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.models.output.LogoutResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyDialogFragment extends DialogFragment {
    private ProgressView mProgressDialog;
    private final Context context;

    public MyDialogFragment(Context activity) {
        this.context = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        if (getArguments() != null) {
            if (getArguments().getBoolean("notAlertDialog")) {
                return super.onCreateDialog(savedInstanceState);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert Dialog");
        builder.setMessage("Alert Dialog inside DialogFragment");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return builder.create();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.logout_popup, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button logout_button = view.findViewById(R.id.logout_button);
        Button cancel_button = view.findViewById(R.id.cancel_button);
        ImageView cross_sign = view.findViewById(R.id.cross_sign);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PreferenceHandler.readBoolean(context, PreferenceHandler.ISSTATICLOGIN, false)) {
                    PreferenceHandler.writeBoolean(context, PreferenceHandler.IS_EMAIL_LOGIN, false);
                    PreferenceHandler.writeBoolean(context, PreferenceHandler.LOGGED_IN, false);
                    PreferenceHandler.writeBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                    PreferenceHandler.setList(context, PreferenceHandler.NOTIFICATION_DATA, null);
                    dismiss();
                    Intent intent = new Intent(context, WelcomeActivityNew.class);
                    intent.putExtra("isFromLogout", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                } else {
                    hitLogoutService();
                }
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        cross_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("API123", "onCreate");

        boolean setFullScreen = false;
        if (getArguments() != null) {
            setFullScreen = getArguments().getBoolean("fullScreen");
        }

        if (setFullScreen)
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public interface DialogListener {
        void onFinishEditDialog(String inputText);
    }

    public void hitLogoutService() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        APIInterface apiInterface = ApiUtils.getApiInterface();
        Call<LogoutResponse> call = apiInterface.logout(headers);
        mProgressDialog = new ProgressView(context);
        mProgressDialog.show();
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess().equalsIgnoreCase("true")) {
                            Log.d("TAG", "onResponse: " + "call");
                            PreferenceHandler.writeBoolean(context, PreferenceHandler.IS_EMAIL_LOGIN, false);
                            PreferenceHandler.writeBoolean(context, PreferenceHandler.LOGGED_IN, false);
                            PreferenceHandler.writeBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                            PreferenceHandler.setList(context, PreferenceHandler.NOTIFICATION_DATA, null);
                            dismiss();
                            Log.d("TAG", "onResponse: " + "call11");
                            Intent intent = new Intent(context, WelcomeActivityNew.class);
                            intent.putExtra("isFromLogout", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(getActivity());
                    }
                }

            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
