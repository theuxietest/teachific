package com.so.luotk.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.so.luotk.models.newmodels.loginModel.LoginSignupInput;

public class LoginViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link LoginViewModel}
     */
    private final Application application;
    private final LoginSignupInput input;

    public LoginViewModelFactory(@NonNull Application application, LoginSignupInput input) {
        super(application);
        this.application = application;
        this.input = input;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LoginViewModel(application, input);
    }
}
