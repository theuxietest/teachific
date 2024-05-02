package com.so.luotk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class VerifyOrgViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link VerifyOrgViewModel}
     */
    private final Application application;
    private final String code;

    public VerifyOrgViewModelFactory(@NonNull Application application, String code) {
        super(application);
        this.code = code;
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new VerifyOrgViewModel(application, code);
    }
}
