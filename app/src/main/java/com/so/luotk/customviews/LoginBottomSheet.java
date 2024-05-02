package com.so.luotk.customviews;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.so.luotk.R;

import org.jetbrains.annotations.NotNull;

public class LoginBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "LoginSheet";
    String message, messageBody;
    Context context;
    public LoginBottomSheet(Context context, String s) {
        this.message = s;
        this.context = context;
        this.messageBody = messageBody;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_bottom_layout, container, false);
        new KeyboardUtils(getActivity(), v);

        TextInputLayout textInputLayout = v.findViewById(R.id.phone_number);
        MaterialButton send_otp_button = v.findViewById(R.id.send_otp_button);
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s.toString() +" : "+ count);
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: " + s.toString() +" : "+ count);
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s.toString());
                send_otp_button.setEnabled(s.toString().length() > 9);
                // TODO Auto-generated method stub
            }
        });
        /*((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);*/
        return v;
    }


}
