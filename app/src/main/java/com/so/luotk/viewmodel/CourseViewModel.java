package com.so.luotk.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.so.luotk.client.MyClient;
import com.so.luotk.models.newmodels.courseModel.CourseModel;
import com.so.luotk.utils.PreferenceHandler;

import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CourseViewModel extends ViewModel {
    private final MediatorLiveData<AuthResource<CourseModel>> observableData;

    public CourseViewModel() {
        observableData = new MediatorLiveData<>();
    }

    public void getList(Context context, Map<String, String> map, boolean flag) {
        observableData.setValue(AuthResource.loading(null));
        final LiveData<AuthResource<CourseModel>> liveData = LiveDataReactiveStreams.fromPublisher(
                new MyClient(context).getCourses(map).onErrorReturn(new Function<Throwable, CourseModel>() {
                    @Override
                    public CourseModel apply(@NonNull Throwable throwable) throws Exception {
                        CourseModel model = new CourseModel();
                        model.setStatus(-1);
                        if (throwable.getMessage().contains("host"))
                            model.setMessage("Check internet connection");
                        else
                            model.setStatus(1);
                        return model;
                    }
                }).map(new Function<CourseModel, AuthResource<CourseModel>>() {
                    @Override
                    public AuthResource<CourseModel> apply(@NonNull CourseModel listResponse) throws Exception {

                        Log.d("TAG", "getList: CallMap");
                        if (listResponse.getStatus() < 0)
                            return AuthResource.error(listResponse.getMessage(), null);
                        if (listResponse.getStatus() == 1)
                            return AuthResource.extra("No Result");
                        if (listResponse.getStatus() == 403)
                            return AuthResource.not_authenticated();
                        if (listResponse.getStatus() != 200)
                            return AuthResource.error(listResponse.getMessage(), null);
                        if (listResponse.getResult() == null)
                            return AuthResource.extra("No Result");
                        if (flag) {
                            if (listResponse.getResult().getPurchasedCourses().getTotal() == 0)
                                return AuthResource.extra("No Result");
                        } else {
                            if (listResponse.getResult().getOtherCourses().getTotal() == 0)
                                return AuthResource.extra("No Result");
                        }
                        PreferenceHandler.writeString(context, PreferenceHandler.FEATURED_COURSE, new Gson().toJson(listResponse));
                        return AuthResource.authenticated(listResponse);
                    }
                }).subscribeOn(Schedulers.io()));
        observableData.addSource(liveData, data -> {
            Log.d("TAG", "getList: CallObserver");
            observableData.setValue(data);
            observableData.removeSource(liveData);
        });
    }

    public LiveData<AuthResource<CourseModel>> getObservableData() {
        return observableData;
    }
}
