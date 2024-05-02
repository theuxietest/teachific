package com.so.luotk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;

import java.util.Map;

public class JitsiViewModelFactory extends AndroidViewModelFactory {
    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link JitsiRoomLiveViewModel}
     */
    private final Application application;
    private final String type;
    private final String id;
    private final Map<String, String> map;

    public JitsiViewModelFactory(@NonNull Application application, String type, Map<String, String> map, String id) {
        super(application);
        this.application = application;
        this.type = type;
        this.id = id;
        this.map = map;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new JitsiRoomLiveViewModel(application, type, map, id);
    }
}
