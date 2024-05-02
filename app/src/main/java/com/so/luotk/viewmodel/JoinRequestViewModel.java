package com.so.luotk.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.so.luotk.client.MyClient;
import com.so.luotk.models.output.JoinRequestResponse;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class JoinRequestViewModel extends ViewModel {
    private final MediatorLiveData<AuthResource<JoinRequestResponse>> observableData;

    public JoinRequestViewModel() {
        observableData = new MediatorLiveData<>();

    }

    public void getView(Context context, String batchId) {
        observableData.setValue(AuthResource.loading(null));
        final LiveData<AuthResource<JoinRequestResponse>> liveData = LiveDataReactiveStreams.fromPublisher(
                new MyClient(context).hitGetJoinRequestList(batchId).onErrorReturn(throwable -> {
                    JoinRequestResponse model = new JoinRequestResponse();
                    model.setStatus(-1);
                    if (throwable.getMessage().contains("host"))
                        model.setMessage("Check internet connection");
                    else model.setStatus(1);
                    return model;
                }).map(new Function<JoinRequestResponse, AuthResource<JoinRequestResponse>>() {
                    @Override
                    public AuthResource<JoinRequestResponse> apply(@NonNull JoinRequestResponse joinRequestResponse) throws Exception {
                        if (joinRequestResponse.getStatus() < 0)
                            return AuthResource.error(joinRequestResponse.getMessage(), null);
                        if (joinRequestResponse.getStatus() == 1)
                            return AuthResource.extra("No Result");
                        if (joinRequestResponse.getStatus() == 403)
                            return AuthResource.not_authenticated();
                        if (joinRequestResponse.getStatus() != 200)
                            return AuthResource.error(joinRequestResponse.getMessage(), null);
                        if (joinRequestResponse.getResult() == null)
                            return AuthResource.extra("No Result");
                        if (joinRequestResponse.getResult().size()== 0)
                            return AuthResource.extra("No Result");
                        return AuthResource.authenticated(joinRequestResponse);
                    }
                }).subscribeOn(Schedulers.io()));
        observableData.addSource(liveData, data -> {
            observableData.setValue(data);
            observableData.removeSource(liveData);

        });


    }
    public LiveData<AuthResource<JoinRequestResponse>> getObservableData() {
        return observableData;
    }
}
