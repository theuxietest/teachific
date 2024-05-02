package com.so.luotk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.so.luotk.client.MyClient;
import com.so.luotk.models.newmodels.loginModel.LoginSignupInput;
import com.so.luotk.models.newmodels.loginModel.LoginSignupResponse;

public class LoginViewModel extends AndroidViewModel {
    final LiveData<LoginSignupResponse> observableData;

    public LoginViewModel(@NonNull Application application, LoginSignupInput input) {
        super(application);
        observableData = new MyClient(application.getApplicationContext()).userLogin(input);
    }

    public LiveData<LoginSignupResponse> getObservableData() {
        return observableData;
    }
}
