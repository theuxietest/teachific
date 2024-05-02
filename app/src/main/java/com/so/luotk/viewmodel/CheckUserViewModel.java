package com.so.luotk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.so.luotk.client.MyClient;
import com.so.luotk.models.input.CheckUserExistInput;
import com.so.luotk.models.output.CheckuserexistResponse;


public class CheckUserViewModel extends AndroidViewModel {
    private final LiveData<CheckuserexistResponse> observableData;

    public CheckUserViewModel(@NonNull Application application, CheckUserExistInput input) {
        super(application);
        observableData = new MyClient(application.getApplicationContext()).checkUserExist(input);
    }

    public LiveData<CheckuserexistResponse> getObservableData() {
        return observableData;
    }
}
