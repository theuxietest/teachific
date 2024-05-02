package com.so.luotk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.so.luotk.models.input.CheckUserExistInput;

public class CheckUserViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link CheckUserViewModel}
     */
    private final Application application;
    private final CheckUserExistInput input;

    public CheckUserViewModelFactory(@NonNull Application application, CheckUserExistInput input) {
        super(application);
        this.application = application;
        this.input = input;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CheckUserViewModel(application, input);
    }
}
