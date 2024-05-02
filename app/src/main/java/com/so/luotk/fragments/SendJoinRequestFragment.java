package com.so.luotk.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.so.luotk.R;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.activities.MainActivity;
import com.so.luotk.models.output.GetBatchRequestListResponse;
import com.so.luotk.models.output.GetBatchRequestListResult;
import com.so.luotk.models.output.SendBatchJoinRequestResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendJoinRequestFragment extends Fragment implements View.OnClickListener {
    private static final String ARGS1 = "ARGS1";
    private Toolbar toolBarBack;
    private String titlerName, batchCode;
    private EditText edtClassCode;
    private TextView tvError, tvEnterDemoCode;
    private ProgressView mProgressDialog;
    private LinearLayout /*layoutEditTextBatchCode*/layoutSendRequest;
    private boolean isFromHome;
    private boolean isBatchCreated;
    private APIInterface apiInterface;
    private TextView btnSend;
    private ImageView close_join_batch;
    private boolean isAallowedToSendRequest;
    private ArrayList<GetBatchRequestListResult> requestList;
    private Context context;
    RelativeLayout backArrow;

    public SendJoinRequestFragment() {

    }

    public static SendJoinRequestFragment newInstance(boolean isFromHome) {
        SendJoinRequestFragment fragment = new SendJoinRequestFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGS1, isFromHome);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utilities.restrictScreenShot(getActivity());
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (getArguments() != null) {
            isFromHome = getArguments().getBoolean(ARGS1);
        }

        View view = inflater.inflate(R.layout.dialog_send_join_request, container, false);
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.statusToolBarColor));
//        setToolbar(view);
        setupUI(view);

        return view;
    }

    private void setToolbar(View view) {
        /*toolbar = view.findViewById(R.id.toolbar);
        close_join_batch = view.findViewById(R.id.close_join_batch);
        close_join_batch.setOnClickListener(v -> {
            hideKeyboardFrom(context, edtClassCode);
            Fragment fragment;
            if (isFromHome) {
                fragment = new HomeFragment();
            } else {
                fragment = new BatchFragment();

            }
            if (getActivity() != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });*/

    }


    public void onBackPressed() {
        if (getActivity() != null) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.popBackStack();
        }
    }


    private void setupUI(View view) {
        isBatchCreated = PreferenceHandler.readBoolean(context, PreferenceHandler.CREATED_BATCH, false);
        apiInterface = ApiUtils.getApiInterface();
        requestList = new ArrayList<>();
        edtClassCode = view.findViewById(R.id.edt_class_code);
        tvError = view.findViewById(R.id.tv_error);
        layoutSendRequest = view.findViewById(R.id.layout_send_join_request);
        tvEnterDemoCode = view.findViewById(R.id.tv_enter_demo_code);
        tvError.setVisibility(View.INVISIBLE);
        btnSend = view.findViewById(R.id.btn_send);
        backArrow = view.findViewById(R.id.backArrow);
        toolBarBack = view.findViewById(R.id.toolBarBack);
        btnSend.setOnClickListener(this);
        toolBarBack.setNavigationIcon(Utilities.setNavigationIconColorBlack(getActivity()));
        toolBarBack.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyBoard(getActivity());
                onBackPressed();
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (Utilities.checkInternet(context)) {
            hitGetRequestListService();
        } else {
            Toast.makeText(getActivity(), getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        openKeyboard(edtClassCode);

        edtClassCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvError.setVisibility(View.INVISIBLE);
                if (edtClassCode.getText().toString().isEmpty()) {
                    // layoutEditTextBatchCode.setBackgroundResource(R.drawable.bg_flag_box);

                } else {
                    //layoutEditTextBatchCode.setBackgroundResource(R.drawable.bg_flag_box);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean checkBatchCodeValidation(EditText editText, TextView textView) {
        if (editText.getText().toString().isEmpty()) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(R.string.please_enter_batch_code);
            //   layoutEditTextBatchCode.setBackgroundResource(R.drawable.bg_red_outline);
            DrawableCompat.setTint(editText.getBackground(), ContextCompat.getColor(getActivity(), R.color.light_red));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (checkBatchCodeValidation(edtClassCode, tvError)) {
            Utilities.hideKeyBoard(context);
            if (requestList.size() > 0) {
                for (int i = 0; i < requestList.size(); i++) {
                    isAallowedToSendRequest = !edtClassCode.getText().toString().equals(requestList.get(i).getBatchCode());
                }
            }
            if (Utilities.checkInternet(context)) {
                if (isAallowedToSendRequest) {
                    hitSendBatchJoinRequestService();
                } else {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText(R.string.already_requested_to_batch);
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
            }

        }

    }


    public void openKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        editText.requestFocus();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void hitSendBatchJoinRequestService() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        Call<SendBatchJoinRequestResponse> call = apiInterface.sendBatchJoinRequest(headers, edtClassCode.getText().toString());
        mProgressDialog = new ProgressView(getActivity());
        mProgressDialog.show();
        call.enqueue(new Callback<SendBatchJoinRequestResponse>() {
            @Override
            public void onResponse(Call<SendBatchJoinRequestResponse> call, Response<SendBatchJoinRequestResponse> response) {
                if (getActivity() != null && !getActivity().isFinishing())
                    mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getStatus() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null && response.body().getResult() != null && response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("true")) {
                                Toast.makeText(getActivity(), "Request has been sent successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.putExtra(PreferenceHandler.IS_FROM_SEND_JOIN_REQUEST, true);
                                startActivity(intent);
                                // getActivity().getSupportFragmentManager().popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                        }
                    } else if ((response.body().getSuccess().equalsIgnoreCase("false") && response.body().getStatus().equalsIgnoreCase("402"))) {
                        // {"success":false,"status":402,"message":"user already added"}
                        if (response.body().getMessage().equalsIgnoreCase("invalid batch code")) {
                            tvError.setVisibility(View.VISIBLE);
                            tvError.setText(R.string.invalid_batch_code);
                            //  layoutEditTextBatchCode.setBackgroundResource(R.drawable.bg_red_outline);
                        } else if (response.body().getMessage().equalsIgnoreCase("user already added")) {
                            tvError.setVisibility(View.VISIBLE);
                            tvError.setText(R.string.already_added_in_batch);
                            //  layoutEditTextBatchCode.setBackgroundResource(R.drawable.bg_red_outline);
                        }


                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(getActivity());
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<SendBatchJoinRequestResponse> call, Throwable t) {
                if (getActivity() != null && !getActivity().isFinishing())
                    mProgressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                //  Snackbar.make(layoutSelectClass, "Server error", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void hitGetRequestListService() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        Call<GetBatchRequestListResponse> call = apiInterface.getBatchJoinRequestList(headers);
        mProgressDialog = new ProgressView(getActivity());
//        mProgressDialog.show();
        call.enqueue(new Callback<GetBatchRequestListResponse>() {
            @Override
            public void onResponse(Call<GetBatchRequestListResponse> call, Response<GetBatchRequestListResponse> response) {
                if (getActivity() != null && !getActivity().isFinishing())
//                    mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getStatus() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null && response.body().getResult().size() > 0) {
                                requestList.addAll(response.body().getResult());
                            } else {
                                isAallowedToSendRequest = true;
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(getActivity());
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<GetBatchRequestListResponse> call, Throwable t) {
                if (getActivity() != null && !getActivity().isFinishing())
//                    mProgressDialog.dismiss();
                     Toast.makeText(getActivity(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
