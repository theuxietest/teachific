package com.so.luotk.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.so.luotk.client.MyClient;
import com.so.luotk.models.newmodels.courseModel.CourseOverViewModel;


import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CourseOverviewViewModel extends ViewModel {
    private final MediatorLiveData<AuthResource<CourseOverViewModel>> observableData;

    public CourseOverviewViewModel() {
        observableData = new MediatorLiveData<>();
    }

    public void getView(Context context, String courseId) {
        observableData.setValue(AuthResource.loading(null));
        final LiveData<AuthResource<CourseOverViewModel>> liveData = LiveDataReactiveStreams.fromPublisher(
                new MyClient(context).hitGetCourseOverviewService(courseId).onErrorReturn(new Function<Throwable, CourseOverViewModel>() {
                    @Override
                    public CourseOverViewModel apply(@NonNull Throwable throwable) throws Exception {
                        CourseOverViewModel model = new CourseOverViewModel();
                        model.setStatus(-1);
                        if (throwable.getMessage().contains("host"))
                            model.setMessage("Check internet connection");
                        else
                            model.setStatus(1);
                        return model;
                    }
                }).map(new Function<CourseOverViewModel, AuthResource<CourseOverViewModel>>() {
                    @Override
                    public AuthResource<CourseOverViewModel> apply(@NonNull CourseOverViewModel model) throws Exception {
                        if (model.getStatus() < 0)
                            return AuthResource.error(model.getMessage(), null);
                        if (model.getStatus() == 1)
                            return AuthResource.extra("No Result");
                        if (model.getStatus() == 403)
                            return AuthResource.not_authenticated();
                        if (model.getStatus() != 200)
                            return AuthResource.error(model.getMessage(), null);
                        if (model.getDatum() == null)
                            return AuthResource.extra("No Result");
                        return AuthResource.authenticated(model);
                    }
                }).subscribeOn(Schedulers.io()));
        observableData.addSource(liveData, data -> {
            observableData.setValue(data);
            observableData.removeSource(liveData);
        });
    }

    public LiveData<AuthResource<CourseOverViewModel>> getObservableData() {
        return observableData;
    }
}
