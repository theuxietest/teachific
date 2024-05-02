package com.so.luotk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.so.luotk.client.MyClient;
import com.so.luotk.models.newmodels.liveRoomModel.LiveRoomModel;

import java.util.Map;

public class JitsiRoomLiveViewModel extends AndroidViewModel {
    private final LiveData<LiveRoomModel> observableData;

    public JitsiRoomLiveViewModel(@NonNull Application application, String type, Map<String, String> map, String id) {
        super(application);
        MyClient client = new MyClient(application.getApplicationContext());
        if (type.equals("Get"))
            observableData = client.getLiveRooms(map);
        else
            observableData = client.createLiveRooms(id);
    }

    public LiveData<LiveRoomModel> getObservableData() {
        return observableData;
    }
}
