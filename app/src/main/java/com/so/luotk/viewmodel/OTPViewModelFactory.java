package com.so.luotk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.so.luotk.models.input.VerifyLoginInput;

public class OTPViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link OTPViewModel}
     */
    private final Application application;
    private final VerifyLoginInput input;

    public OTPViewModelFactory(@NonNull Application application, VerifyLoginInput input) {
        super(application);
        this.application = application;
        this.input = input;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new OTPViewModel(application,input);
    }
}
