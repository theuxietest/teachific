package com.so.luotk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

public class AdminBatchListViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AdminBatchListViewModel}
     */
    private final Application application;
    private final Map<String, Integer> map;

    public AdminBatchListViewModelFactory(@NonNull Application application, Map<String, Integer> map) {
        super(application);
        this.map = map;
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AdminBatchListViewModel(application);
    }
}
