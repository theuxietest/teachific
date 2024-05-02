package com.so.luotk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

public class MaterialViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link MaterialViewModel}
     */
    private final Application application;
    private final boolean flag;
    private final Map<String, String> map;

    public MaterialViewModelFactory(@NonNull Application application, boolean flag, Map<String, String> map) {
        super(application);
        this.application = application;
        this.flag = flag;
        this.map = map;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MaterialViewModel(application, map, flag);
    }
}
