package com.so.luotk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.so.luotk.client.MyClient;
import com.so.luotk.models.newmodels.adminBatchModel.AdminBatchModel;

public class AdminBatchListViewModel extends AndroidViewModel {
    private final LiveData<AdminBatchModel> observableData;


    public AdminBatchListViewModel(@NonNull Application application) {
        super(application);
        observableData = new MyClient(application.getApplicationContext()).getAdminBatches();
    }

    public LiveData<AdminBatchModel> getObservableData() {
        return observableData;
    }
}
