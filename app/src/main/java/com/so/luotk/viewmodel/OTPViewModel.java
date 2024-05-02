package com.so.luotk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.so.luotk.client.MyClient;
import com.so.luotk.models.input.VerifyLoginInput;
import com.so.luotk.models.newmodels.otpModel.OTPResponse;

public class OTPViewModel extends AndroidViewModel {
    private final LiveData<OTPResponse> observableData;

    public OTPViewModel(@NonNull Application application, VerifyLoginInput input) {
        super(application);
        observableData = new MyClient(application.getApplicationContext()).verifyOTP(input);
    }

    public LiveData<OTPResponse> getObservableData() {
        return observableData;
    }
}
