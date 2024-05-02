package com.so.luotk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.so.luotk.client.MyClient;
import com.so.luotk.models.newmodels.orgModel.VerifyOrgModel;

public class VerifyOrgViewModel extends AndroidViewModel {
    private final LiveData<VerifyOrgModel> observableData;

    public VerifyOrgViewModel(@NonNull Application application, String code) {
        super(application);
        observableData = new MyClient(application.getApplicationContext()).verifyOrg(code);
    }

    public LiveData<VerifyOrgModel> getObservableData() {
        return observableData;
    }
}
