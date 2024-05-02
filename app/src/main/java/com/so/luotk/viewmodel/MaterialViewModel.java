package com.so.luotk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.so.luotk.client.MyClient;
import com.so.luotk.models.newmodels.study.StudyMaterialModel;

import java.util.Map;

public class MaterialViewModel extends AndroidViewModel {

    private final LiveData<StudyMaterialModel> observableData;

    public MaterialViewModel(@NonNull Application application, Map<String, String> map, boolean flag) {
        super(application);
        observableData = new MyClient(application.getApplicationContext()).getMaterial(map, flag);
    }

    public LiveData<StudyMaterialModel> getObservableData() {
        return observableData;
    }
}
