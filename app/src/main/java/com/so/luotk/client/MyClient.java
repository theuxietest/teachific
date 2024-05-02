package com.so.luotk.client;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.so.luotk.models.input.CheckUserExistInput;
import com.so.luotk.models.input.CreateZoomRoomInput;
import com.so.luotk.models.input.GetAttendenceInput;
import com.so.luotk.models.input.VerifyLoginInput;
/*import com.smartowls.smartowls.models.newmodels.SendBatchRequestResponse;
import com.smartowls.smartowls.models.newmodels.ServerResponse;
import com.smartowls.smartowls.models.newmodels.adminBatchModel.AdminBatchModel;
import com.smartowls.smartowls.models.newmodels.liveRoomModel.LiveRoomModel;
import com.smartowls.smartowls.models.newmodels.loginModel.LoginSignupInput;
import com.smartowls.smartowls.models.newmodels.loginModel.LoginSignupResponse;
import com.smartowls.smartowls.models.newmodels.orgModel.VerifyOrgModel;
import com.smartowls.smartowls.models.newmodels.otpModel.OTPResponse;*/
import com.so.luotk.models.newmodels.ServerResponse;
import com.so.luotk.models.newmodels.adminBatchModel.AdminBatchModel;
import com.so.luotk.models.newmodels.courseModel.CourseModel;
import com.so.luotk.models.newmodels.courseModel.CourseOverViewModel;
import com.so.luotk.models.newmodels.liveRoomModel.LiveRoomModel;
import com.so.luotk.models.newmodels.loginModel.LoginSignupInput;
import com.so.luotk.models.newmodels.loginModel.LoginSignupResponse;
import com.so.luotk.models.newmodels.orgModel.VerifyOrgModel;
import com.so.luotk.models.newmodels.otpModel.OTPResponse;
import com.so.luotk.models.newmodels.study.StudyMaterialModel;
import com.so.luotk.models.output.CheckuserexistResponse;
import com.so.luotk.models.output.CreateZoomRoomResponse;
import com.so.luotk.models.output.DeleteStudentResponse;
import com.so.luotk.models.output.GetAnnouncementListResponse;
import com.so.luotk.models.output.GetAssignmentTestListResponse;
import com.so.luotk.models.output.GetAttendenceResponse;
import com.so.luotk.models.output.GetBatchFolderVideosResponse;
import com.so.luotk.models.output.GetBatchListResponse;
import com.so.luotk.models.output.GetBatchOverviewResponse;
import com.so.luotk.models.output.GetBatchSettingsResponse;
import com.so.luotk.models.output.GetBatchSubmitAssignmentTestResponse;
import com.so.luotk.models.output.ImportantInfoResponse;
import com.so.luotk.models.output.JoinRequestResponse;
import com.so.luotk.models.output.KtServerResponse;
import com.so.luotk.models.output.LogoutResponse;
import com.so.luotk.models.output.ObjectiveTestDetailsResponse;
import com.so.luotk.models.output.PopularVideoResponse;
import com.so.luotk.models.output.PreCoursePaymentResponse;
import com.so.luotk.models.output.ReportCountResponse;
import com.so.luotk.models.output.ServiceResponse;
import com.so.luotk.models.output.GetCourseSettingsResponse;
import com.so.luotk.models.output.GetReportTestListResponse;
import com.so.luotk.models.output.GetStudentDataResponse;
import com.so.luotk.models.output.GetUserProfileResponse;
import com.so.luotk.models.output.MakeAnnouncementResponse;
import com.so.luotk.models.output.UpdateUserProfileResponse;
import com.so.luotk.utils.PreferenceHandler;

import org.jetbrains.annotations.NotNull;

import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import timber.log.Timber;

public class MyClient {
    private final ApiInterface apiInterface;
    private final String TAG = this.getClass().getSimpleName();
    private static final String BASE_URL = "https://web.smartowls.in/api/v1/";
    private final Map<String, String> headers;
    private boolean isAdmin;

    public MyClient(Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .proxy(Proxy.NO_PROXY)
                .addInterceptor(interceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
        apiInterface = retrofit.create(ApiInterface.class);
        PreferenceHandler handler = new PreferenceHandler(context);
        headers = new HashMap<>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));

        Log.d(TAG, "MyClient: " + PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        Log.d(TAG, "MyClientDeciceId: " + PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));

    }


    private interface ApiInterface {
        @POST("checkuserexist")
        Call<CheckuserexistResponse> userExists(@Body CheckUserExistInput CheckUserExistInput);

        @GET("get-admin-batch-list")
        Call<GetBatchListResponse> getAdminBatchList(@HeaderMap Map<String, String> headers);

        @POST("registerlogin")
        Call<LoginSignupResponse> login(@Body LoginSignupInput signupInput);

        @POST("registeremaillogin")
        Call<LoginSignupResponse> emaillogin(@Body LoginSignupInput signupInput);

        @POST("verifielogin")
        Call<OTPResponse> verifyLogin(@Body VerifyLoginInput verifyLoginInput);

        @GET("get-batch-list")
        Call<AdminBatchModel> getBatchList(@HeaderMap Map<String, String> headers);

        @POST("get-batch-assignments?")
        Call<GetAssignmentTestListResponse> getAssignmentList(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId, @Query("search") String search, @Query("pageLength") String pageLength, @Query("page") String page);

        @POST("get-batch-tests?")
        Call<GetAssignmentTestListResponse> getTestList(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId, @Query("search") String search, @Query("pageLength") String pageLength, @Query("page") String page);

        @POST("get-batch-announcements")
        Call<GetAnnouncementListResponse> getAnnouncementList(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> map);

        @POST("get-batch-folder-videos?")
        Call<GetBatchFolderVideosResponse> getBatchFolderList(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId, @Query("pageLength") String pageLength, @Query("page") String page);

        @POST("get-batch-folder-videos?")
        Call<GetBatchFolderVideosResponse> getBatchVideoListUnderFolder(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId, @Query("folderId") String folderId, @Query("pageLength") String pageLength, @Query("page") String page);

/*
        @POST("batch-join-request")
        Call<SendBatchRequestResponse> sendBatchJoinRequest(@HeaderMap Map<String, String> headers, @Query("batchCode") String batchCode);*/

        //zoom api
        @POST("batch-create-zoom-room")
        Call<CreateZoomRoomResponse> createZoomRoom(@HeaderMap Map<String, String> headers, @Body CreateZoomRoomInput createZoomRoomInput);

        @POST("get-batch-live-room")
        Call<LiveRoomModel> getLiveRoom(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> map);

        @POST("get-course-live-room")
        Call<LiveRoomModel> getCourseLiveRoom(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> map);

        @POST("batch-end-room")
        Call<ServerResponse> endLiveRoom(@HeaderMap Map<String, String> headers, @QueryMap Map<String, Integer> map);


        @POST("verifieorg")
        Call<VerifyOrgModel> verifyOrgCode(@Query("orgCode") String orgCode);

        @GET("get-admin-batch-list")
        Call<AdminBatchModel> getLiveAdminBatchList(@HeaderMap Map<String, String> headers);

        @Multipart
        @POST("get-batch-submit-assignment")
        Call<GetBatchSubmitAssignmentTestResponse> submitBatchAssignment(@HeaderMap Map<String, String> headers, @Query("assignmentId") String assignmentId, @Part MultipartBody.Part[] image);

        @Multipart
        @POST("get-batch-submit-test")
        Call<GetBatchSubmitAssignmentTestResponse> submitBatchTest(@HeaderMap Map<String, String> headers, @Query("testId") String testId, @Part MultipartBody.Part[] image);

        @POST("get-assignments-report?")
        Call<GetAssignmentTestListResponse> getReportAssignmentList(@HeaderMap Map<String, String> headers, @Query("search") String search, @Query("pageLength") String pageLength, @Query("page") String page);

        @POST("get-tests-report?")
        Call<GetReportTestListResponse> getReportTestList(@HeaderMap Map<String, String> headers, @Query("search") String search, @Query("pageLength") String pageLength, @Query("page") String page);

/*        @POST("batch-join-request-list")
        Call<AdminBatchModel> getBatchJoinRequestList(@HeaderMap Map<String, String> headers);*/

        @POST("get-user-profile")
        Call<GetUserProfileResponse> getUserProfile(@HeaderMap Map<String, String> headers);

        @Multipart
        @POST("update-user-profile")
        Call<UpdateUserProfileResponse> updateUserProfileWithImage(@HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params, @Part MultipartBody.Part image);

        @Multipart
        @POST("update-user-profile")
        Call<UpdateUserProfileResponse> updateUserProfile(@HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params);

        /*        //jitsi api
                @POST("batch-create-room")
                Call<LiveRoomModel> createJitsiRoom(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId);

                @POST("logout")
                Call<ServerResponse> logout(@HeaderMap Map<String, String> headers);

                @POST("mark/attendance")
                Call<ServerResponse> markStudentLiveAttendence(@HeaderMap Map<String, String> headers, @QueryMap Map<String, Integer> map);*/
        @POST("get-batch-students")
        Call<GetStudentDataResponse> getBatchStudents(@HeaderMap Map<String, String> headers, @Query("search") String search, @Query("batchId") String batchId, @Query("pageLength") String pageLength, @Query("page") int page);


        @POST("batch-attendance")
        Call<GetAttendenceResponse> getStudentAttendance(@HeaderMap Map<String, String> headers, @Body GetAttendenceInput getAttendenceInput);

        @POST("get-batch-overview")
        Call<GetBatchOverviewResponse> getBatchOverview(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId);

        @POST("get-batch-settings")
        Call<GetBatchSettingsResponse> getBatchSettings(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId);

        @POST("get-study-material")
        Call<StudyMaterialModel> getStudyMaterialList(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> map);


        @POST("get-free-study-material")
        Call<StudyMaterialModel> getFreeStudyMaterialList(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> map);

        @POST("batch/delete/announcement")
        Call<MakeAnnouncementResponse> deleteAnnouncement(@HeaderMap Map<String, String> headers, @Query("id") int id);

        @POST("batch/save/announcement")
        Call<MakeAnnouncementResponse> saveAnnouncement(@HeaderMap Map<String, String> headers, @QueryMap Map<String, Object> map);

        @POST("batch/multple/announcement")
        Call<MakeAnnouncementResponse> makeMutipleAnnouncement(@HeaderMap Map<String, String> headers, @Query("batchId[]") List<Integer> batchId, @Query("announcement") String announcement);

        @POST("delete-student-from-batch")
        Call<DeleteStudentResponse> deleteStudent(@HeaderMap Map<String, String> headers, @QueryMap Map<String, Object> map);

        @POST("delete/video")
        Call<GetBatchSubmitAssignmentTestResponse> deleteVideo(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

        @POST("delete/video/folder")
        Call<GetBatchSubmitAssignmentTestResponse> deleteVideoFolder(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

        @POST("delete/study/material/folder")
        Call<GetBatchSubmitAssignmentTestResponse> deleteStudyFolder(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

        @POST("delete/study/material")
        Call<GetBatchSubmitAssignmentTestResponse> deleteStudyMaterial(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);


        @POST("delete/assignment")
        Call<GetBatchSubmitAssignmentTestResponse> deleteAssignment(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

        @POST("delete/batch")
        Call<GetBatchSubmitAssignmentTestResponse> deleteBatch(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);


        @POST("popular/videos")
        Call<PopularVideoResponse> getPopularVideos(@HeaderMap Map<String, String> headers);

        @POST("testimonial/videos")
        Call<PopularVideoResponse> getTestimonialVideos(@HeaderMap Map<String, String> headers);

        //course apis
        @POST("organisation/course/overview")
        Flowable<CourseOverViewModel> getCourseOverView(@HeaderMap Map<String, String> headers, @Query("courseId") String courseId);

        @POST("course-study-material")
        Call<StudyMaterialModel> getCourseMaterialList(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> map);

        @POST("organisation/course")
        Flowable<CourseModel> getCourseList(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> map);

        @POST("get-course-settings")
        Call<GetCourseSettingsResponse> getCourseSettings(@HeaderMap Map<String, String> headers, @Query("courseId") String course);

        @POST("toggle-course-like")
        Call<ServerResponse> hitCourseLike(@HeaderMap Map<String, String> headers, @Query("courseId") String course);

        @POST("get-course-announcements")
        Call<GetAnnouncementListResponse> getCourseAnnouncements(@HeaderMap Map<String, String> headers, @QueryMap Map<String, Object> map);

        @POST("get-course-tests")
        Call<GetAssignmentTestListResponse> getCourseTests(@HeaderMap Map<String, String> headers, @QueryMap Map<String, Object> map);

        @POST("get-course-folder-videos")
        Call<GetBatchFolderVideosResponse> getCoursevideos(@HeaderMap Map<String, String> headers, @QueryMap Map<String, Object> querymap);

        @POST("course/payment")
        Call<PreCoursePaymentResponse> getCoursePaymentData(@HeaderMap Map<String, String> headers, @QueryMap Map<String, Object> map);

        @POST("course/payment/success")
        Call<ServiceResponse> getCoursePaymentSuccess(@HeaderMap Map<String, String> headers, @QueryMap Map<String, Object> map);

        @POST("videos/save/views")
        Call<ServiceResponse> videoView(@HeaderMap Map<String, String> headers, @Query("videoId") String id);

        @POST("important/information")
        Call<ImportantInfoResponse> getImportantInfo(@HeaderMap Map<String, String> headers);

        @POST("information")
        Call<ImportantInfoResponse> getOrgInfo(@HeaderMap Map<String, String> headers);

        @POST("report/count")
        Call<ReportCountResponse> getReportCount(@HeaderMap Map<String, String> headers);

        @POST("add/student")
        Call<ServerResponse> addStudentToBatch(@HeaderMap Map<String, String> headers, @QueryMap Map<String, Object> map);

        @POST("logout")
        Call<LogoutResponse> logout(@HeaderMap Map<String, String> headers);

        @POST("batch/join/request/list")
        Flowable<JoinRequestResponse> getJoinRequests(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId);

        @POST("batch/decline/request")
        Call<ServerResponse> declineRequest(@HeaderMap Map<String, String> headers, @Query("requestId") String requestId);

        @POST("batch/accept/request")
        Call<ServerResponse> acceptRequest(@HeaderMap Map<String, String> headers, @Query("requestId") String requestId);

        @POST("get-test-details")
        Call<ObjectiveTestDetailsResponse> getTestDetails(@HeaderMap Map<String, String> headers, @Query("testId") String testId);

        @POST("submit-answer")
        @FormUrlEncoded
        Call<KtServerResponse> submitObjectiveTestAns(@HeaderMap Map<String, String> headers, @Field("testId") String testId, @Field("questions") String question);


    }

    public void hitSetVideoView(String id, ResponseObject fetched) {
        Call<ServiceResponse> call = apiInterface.videoView(headers, id);
        call.enqueue(new Callback<ServiceResponse>() {
            @Override
            public void onResponse(Call<ServiceResponse> call, Response<ServiceResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<ServiceResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public LiveData<AdminBatchModel> getAdminBatches() {
        final MutableLiveData<AdminBatchModel> data = new MutableLiveData();
        Call<AdminBatchModel> call;
        call = apiInterface.getLiveAdminBatchList(headers);
        call.enqueue(new Callback<AdminBatchModel>() {
            @Override
            public void onResponse(@NotNull Call<AdminBatchModel> call, @NotNull Response<AdminBatchModel> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<AdminBatchModel> call, Throwable t) {
                Timber.e("onFailure: %s", t.getMessage());
            }
        });
        return data;
    }

    public LiveData<StudyMaterialModel> getMaterial(Map<String, String> map, boolean flag) {
        final MutableLiveData<StudyMaterialModel> data = new MutableLiveData();
        Call<StudyMaterialModel> call;
        if (flag)
            call = apiInterface.getStudyMaterialList(headers, map);
        else
            call = apiInterface.getCourseMaterialList(headers, map);
        call.enqueue(new Callback<StudyMaterialModel>() {
            @Override
            public void onResponse(@NotNull Call<StudyMaterialModel> call, @NotNull Response<StudyMaterialModel> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<StudyMaterialModel> call, @NotNull Throwable t) {
                Timber.e("onFailure: %s", t.getMessage());
            }
        });
        return data;
    }


    public LiveData<VerifyOrgModel> verifyOrg(String code) {
//           final MutableLiveData<VerifyOrgModel> data = new MutableLiveData();
//           Call<VerifyOrgModel> call = apiInterface.verifyOrgCode(code);
//           call.enqueue(new Callback<VerifyOrgModel>() {
//               @Override
//               public void onResponse(@NotNull Call<VerifyOrgModel> call, @NotNull Response<VerifyOrgModel> response) {
//                   data.setValue(response.body());
//               }
//
//               @Override
//               public void onFailure(@NotNull Call<VerifyOrgModel> call, @NotNull Throwable t) {
//                   Timber.e("onFailure: %s", t.getMessage());
//               }
//           });
        return null;
    }

    public LiveData<CheckuserexistResponse> checkUserExist(CheckUserExistInput input) {
        final MutableLiveData<CheckuserexistResponse> data = new MutableLiveData();
        Call<CheckuserexistResponse> call = apiInterface.userExists(input);
        call.enqueue(new Callback<CheckuserexistResponse>() {
            @Override
            public void onResponse(@NotNull Call<CheckuserexistResponse> call, @NotNull Response<CheckuserexistResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<CheckuserexistResponse> call, @NotNull Throwable t) {
                Timber.e("onFailure: %s", t.getMessage());
            }
        });
        return data;
    }

    public LiveData<LoginSignupResponse> userLogin(LoginSignupInput input) {
        final MutableLiveData<LoginSignupResponse> data = new MutableLiveData();
        Call<LoginSignupResponse> call = apiInterface.login(input);
        call.enqueue(new Callback<LoginSignupResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoginSignupResponse> call, @NotNull Response<LoginSignupResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<LoginSignupResponse> call, @NotNull Throwable t) {
                Timber.e("onFailure: %s", t.getMessage());
            }
        });
        return data;
    }

    public LiveData<OTPResponse> verifyOTP(VerifyLoginInput input) {
//           final MutableLiveData<OTPResponse> data = new MutableLiveData();
//           Call<OTPResponse> call = apiInterface.verifyLogin(input);
//           call.enqueue(new Callback<OTPResponse>() {
//               @Override
//               public void onResponse(@NotNull Call<OTPResponse> call, @NotNull Response<OTPResponse> response) {
//                   data.setValue(response.body());
//               }
//
//               @Override
//               public void onFailure(@NotNull Call<OTPResponse> call, @NotNull Throwable t) {
//                   Timber.e("onFailure: %s", t.getMessage());
//               }
//           });
//           return data;
        return null;
    }

    public LiveData<LiveRoomModel> getLiveRooms(Map<String, String> map) {
        final MutableLiveData<LiveRoomModel> data = new MutableLiveData();
        Call<LiveRoomModel> call = apiInterface.getLiveRoom(headers, map);
        call.enqueue(new Callback<LiveRoomModel>() {
            @Override
            public void onResponse(@NotNull Call<LiveRoomModel> call, @NotNull Response<LiveRoomModel> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<LiveRoomModel> call, @NotNull Throwable t) {

            }
        });
        return data;

    }

    public void endLiveRooms(Map<String, Integer> map, ResponseObject fetched) {
//           Call<ServerResponse> call = apiInterface.endLiveRoom(headers, map);
//           call.enqueue(new Callback<ServerResponse>() {
//               @Override
//               public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
//                   if (response.code() == 200)
//                       fetched.OnCallback(response.body(), null);
//                   else
//                       fetched.OnCallback(null, response.code() + "");
//               }
//
//               @Override
//               public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
//                   fetched.OnCallback(null, t.getMessage());
//               }
//           });
    }

    public LiveData<LiveRoomModel> createLiveRooms(String id) {
//           final MutableLiveData<LiveRoomModel> data = new MutableLiveData();
//           Call<LiveRoomModel> call = apiInterface.createJitsiRoom(headers, id);
//           call.enqueue(new Callback<LiveRoomModel>() {
//               @Override
//               public void onResponse(@NotNull Call<LiveRoomModel> call, @NotNull Response<LiveRoomModel> response) {
//                   data.setValue(response.body());
//                   Log.e(TAG, "onResponse: " + response.body().getStatus());
//               }
//
//               @Override
//               public void onFailure(@NotNull Call<LiveRoomModel> call, @NotNull Throwable t) {
//                   Timber.e("onFailure: %s", t.getMessage());
//               }
//           });
//           return data;
        return null;
    }

    public void userLogout(ResponseObject fetched) {
//           Log.e(TAG, "userLogout: " + new Gson().toJson(headers));
//           Call<ServerResponse> call = apiInterface.logout(headers);
//           call.enqueue(new Callback<ServerResponse>() {
//               @Override
//               public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
//                   if (response.code() == 200)
//                       fetched.OnCallback(response.body(), null);
//                   else
//                       fetched.OnCallback(null, response.code() + "");
//               }
//
//               @Override
//               public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
//                   fetched.OnCallback(null, t.getMessage());
//               }
//           });
    }

    public void getRequestBatches(ResponseObject fetched) {
//           Call<AdminBatchModel> call = apiInterface.getBatchJoinRequestList(headers);
//           call.enqueue(new Callback<AdminBatchModel>() {
//               @Override
//               public void onResponse(@NotNull Call<AdminBatchModel> call, @NotNull Response<AdminBatchModel> response) {
//                   if (response.code() == 200)
//                       fetched.OnCallback(response.body(), null);
//                   else
//                       fetched.OnCallback(null, response.message());
//               }
//
//               @Override
//               public void onFailure(@NotNull Call<AdminBatchModel> call, Throwable t) {
//                   fetched.OnCallback(null, t.getMessage());
//               }
//           });
    }

    public void sendRequestBatches(ResponseObject fetched, String code) {
//           Call<SendBatchRequestResponse> call = apiInterface.sendBatchJoinRequest(headers, code);
//           call.enqueue(new Callback<SendBatchRequestResponse>() {
//               @Override
//               public void onResponse(@NotNull Call<SendBatchRequestResponse> call, @NotNull Response<SendBatchRequestResponse> response) {
//                   if (response.code() == 200)
//                       fetched.OnCallback(response.body(), null);
//                   else
//                       fetched.OnCallback(null, response.message());
//               }
//
//               @Override
//               public void onFailure(@NotNull Call<SendBatchRequestResponse> call, Throwable t) {
//                   fetched.OnCallback(null, t.getMessage());
//               }
        // });
    }

    public void markAttendance(Map<String, Integer> map, ResponseObject fetched) {
//           Call<ServerResponse> call = apiInterface.markStudentLiveAttendence(headers, map);
//           call.enqueue(new Callback<ServerResponse>() {
//               @Override
//               public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
//                   if (response.code() == 200)
//                       fetched.OnCallback(response.body(), null);
//                   else
//                       fetched.OnCallback(null, response.code() + "");
//               }
//
//               @Override
//               public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
//                   fetched.OnCallback(null, t.getMessage());
//               }
//           });
    }

    public void getBatchSettingsService(String batchid, ResponseObject fetched) {
        Call<GetBatchSettingsResponse> call = apiInterface.getBatchSettings(headers, batchid);
        call.enqueue(new Callback<GetBatchSettingsResponse>() {
            @Override
            public void onResponse(Call<GetBatchSettingsResponse> call, Response<GetBatchSettingsResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");

            }

            @Override
            public void onFailure(Call<GetBatchSettingsResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });
    }

    public void hitGetStudentListService(String searchKey, String batchId, String pageLength, int pageNo, ResponseObject fetched) {
        Call<GetStudentDataResponse> call = apiInterface.getBatchStudents(headers, searchKey, batchId, pageLength, pageNo);
        call.enqueue(new Callback<GetStudentDataResponse>() {
            @Override
            public void onResponse(Call<GetStudentDataResponse> call, Response<GetStudentDataResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<GetStudentDataResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public Flowable<CourseModel> getCourses(Map<String, String> map) {
        return apiInterface.getCourseList(headers, map);

    }

    public Flowable<CourseOverViewModel> hitGetCourseOverviewService(String courseId) {
        return apiInterface.getCourseOverView(headers, courseId);


    }

    public Flowable<JoinRequestResponse> hitGetJoinRequestList(String batchId) {
        return apiInterface.getJoinRequests(headers, batchId);


    }

    public void hitMakeMultibatchAnnouncementService(List<Integer> batchid, String announcement, ResponseObject fetched) {
        Call<MakeAnnouncementResponse> call = apiInterface.makeMutipleAnnouncement(headers, batchid, announcement);
        call.enqueue(new Callback<MakeAnnouncementResponse>() {
            @Override
            public void onResponse(Call<MakeAnnouncementResponse> call, Response<MakeAnnouncementResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<MakeAnnouncementResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void hitGetbatchAnnouncementService(Map<String, String> map, ResponseObject fetched) {
        Call<GetAnnouncementListResponse> call = apiInterface.getAnnouncementList(headers, map);
        call.enqueue(new Callback<GetAnnouncementListResponse>() {
            @Override
            public void onResponse(Call<GetAnnouncementListResponse> call, Response<GetAnnouncementListResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<GetAnnouncementListResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void hitDeleteAnnouncementService(int id, ResponseObject fetched) {
        Call<MakeAnnouncementResponse> call = apiInterface.deleteAnnouncement(headers, id);
        call.enqueue(new Callback<MakeAnnouncementResponse>() {
            @Override
            public void onResponse(Call<MakeAnnouncementResponse> call, Response<MakeAnnouncementResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<MakeAnnouncementResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void hitSaveAnnouncementService(Map<String, Object> map, ResponseObject fetched) {
        Call<MakeAnnouncementResponse> call = apiInterface.saveAnnouncement(headers, map);
        call.enqueue(new Callback<MakeAnnouncementResponse>() {
            @Override
            public void onResponse(Call<MakeAnnouncementResponse> call, Response<MakeAnnouncementResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<MakeAnnouncementResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void hitGetLiveRoomService(Map<String, String> map, String isFrom, ResponseObject fetched) {
        Call<LiveRoomModel> call;
        if (isFrom.equalsIgnoreCase("batch"))
            call = apiInterface.getLiveRoom(headers, map);
        else
            call = apiInterface.getCourseLiveRoom(headers, map);
        call.enqueue(new Callback<LiveRoomModel>() {
            @Override
            public void onResponse(Call<LiveRoomModel> call, Response<LiveRoomModel> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<LiveRoomModel> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void hitStudyMaterial(Map<String, String> map, String isFrom, ResponseObject fetched) {
        Call<StudyMaterialModel> call;
        if (isFrom.equalsIgnoreCase("batch"))
            call = apiInterface.getStudyMaterialList(headers, map);
        else if (isFrom.equalsIgnoreCase("freeMaterial"))
            call = apiInterface.getFreeStudyMaterialList(headers, map);
        else
            call = apiInterface.getCourseMaterialList(headers, map);
        call.enqueue(new Callback<StudyMaterialModel>() {
            @Override
            public void onResponse(@NotNull Call<StudyMaterialModel> call, @NotNull Response<StudyMaterialModel> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(@NotNull Call<StudyMaterialModel> call, @NotNull Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }

        });
    }

    public void getCourseSettingsService(String courseId, ResponseObject fetched) {
        Call<GetCourseSettingsResponse> call = apiInterface.getCourseSettings(headers, courseId);
        call.enqueue(new Callback<GetCourseSettingsResponse>() {
            @Override
            public void onResponse(Call<GetCourseSettingsResponse> call, Response<GetCourseSettingsResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<GetCourseSettingsResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void courseLikeService(String courseId, ResponseObject fetched) {
        Call<ServerResponse> call = apiInterface.hitCourseLike(headers, courseId);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void getCourseAnnouncements(Map<String, Object> map, ResponseObject fetched) {
        Call<GetAnnouncementListResponse> call = apiInterface.getCourseAnnouncements(headers, map);
        call.enqueue(new Callback<GetAnnouncementListResponse>() {
            @Override
            public void onResponse(Call<GetAnnouncementListResponse> call, Response<GetAnnouncementListResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<GetAnnouncementListResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void getCourseTests(Map<String, Object> map, ResponseObject fetched) {
        Call<GetAssignmentTestListResponse> call = apiInterface.getCourseTests(headers, map);
        call.enqueue(new Callback<GetAssignmentTestListResponse>() {
            @Override
            public void onResponse(Call<GetAssignmentTestListResponse> call, Response<GetAssignmentTestListResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<GetAssignmentTestListResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void getCourseVideos(Map<String, Object> map, ResponseObject fetched) {
        Call<GetBatchFolderVideosResponse> call = apiInterface.getCoursevideos(headers, map);
        call.enqueue(new Callback<GetBatchFolderVideosResponse>() {
            @Override
            public void onResponse(Call<GetBatchFolderVideosResponse> call, Response<GetBatchFolderVideosResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<GetBatchFolderVideosResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void getCoursePaymentData(Map<String, Object> map, ResponseObject fetched) {
        Call<PreCoursePaymentResponse> call = apiInterface.getCoursePaymentData(headers, map);
        call.enqueue(new Callback<PreCoursePaymentResponse>() {
            @Override
            public void onResponse(Call<PreCoursePaymentResponse> call, Response<PreCoursePaymentResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(response.body(), response.code() + "");
            }

            @Override
            public void onFailure(Call<PreCoursePaymentResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });
    }

    public void getCoursePaymentSuccess(Map<String, Object> map, ResponseObject fetched) {
        Call<ServiceResponse> call = apiInterface.getCoursePaymentSuccess(headers, map);
        call.enqueue(new Callback<ServiceResponse>() {
            @Override
            public void onResponse(Call<ServiceResponse> call, Response<ServiceResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<ServiceResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void getPopularVideos(boolean isPopular, ResponseObject fetched) {
        Call<PopularVideoResponse> call;
        if (isPopular)
            call = apiInterface.getPopularVideos(headers);
        else call = apiInterface.getTestimonialVideos(headers);
        call.enqueue(new Callback<PopularVideoResponse>() {
            @Override
            public void onResponse(Call<PopularVideoResponse> call, Response<PopularVideoResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<PopularVideoResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }


    public void getImportantInfo(ResponseObject fetched) {
        Call<ImportantInfoResponse> call = apiInterface.getImportantInfo(headers);
        call.enqueue(new Callback<ImportantInfoResponse>() {
            @Override
            public void onResponse(Call<ImportantInfoResponse> call, Response<ImportantInfoResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<ImportantInfoResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void getOrgInfo(ResponseObject fetched) {
        Call<ImportantInfoResponse> call = apiInterface.getOrgInfo(headers);
        call.enqueue(new Callback<ImportantInfoResponse>() {
            @Override
            public void onResponse(Call<ImportantInfoResponse> call, Response<ImportantInfoResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<ImportantInfoResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void getReportCount(ResponseObject fetched) {
        Log.d(TAG, "getReportCount: " + headers);
        Call<ReportCountResponse> call = apiInterface.getReportCount(headers);
        call.enqueue(new Callback<ReportCountResponse>() {
            @Override
            public void onResponse(Call<ReportCountResponse> call, Response<ReportCountResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<ReportCountResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void deleteStudentFromBatch(Map<String, Object> map, ResponseObject fetched) {
        Call<DeleteStudentResponse> call = apiInterface.deleteStudent(headers, map);
        call.enqueue(new Callback<DeleteStudentResponse>() {
            @Override
            public void onResponse(Call<DeleteStudentResponse> call, Response<DeleteStudentResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<DeleteStudentResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void deleteVideoFromBatch(Map<String, String> map, ResponseObject fetched) {
        Call<GetBatchSubmitAssignmentTestResponse> call = apiInterface.deleteVideo(headers, map);
        call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
            @Override
            public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void deleteFolderFromBatch(Map<String, String> map, ResponseObject fetched) {
        Call<GetBatchSubmitAssignmentTestResponse> call = apiInterface.deleteVideoFolder(headers, map);
        call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
            @Override
            public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void deleteMaterialFromStudy(Map<String, String> map, ResponseObject fetched) {
        Call<GetBatchSubmitAssignmentTestResponse> call = apiInterface.deleteStudyMaterial(headers, map);
        call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
            @Override
            public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void deleteFolderFromStudy(Map<String, String> map, ResponseObject fetched) {
        Call<GetBatchSubmitAssignmentTestResponse> call = apiInterface.deleteStudyFolder(headers, map);
        call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
            @Override
            public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void deleteAssignmentFromBatch(Map<String, String> map, ResponseObject fetched) {
        Call<GetBatchSubmitAssignmentTestResponse> call = apiInterface.deleteAssignment(headers, map);
        call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
            @Override
            public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void deleteBatchFrom(Map<String, String> map, ResponseObject fetched) {
        Call<GetBatchSubmitAssignmentTestResponse> call = apiInterface.deleteBatch(headers, map);
        call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
            @Override
            public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }


    public void addStudentToBatch(Map<String, Object> map, ResponseObject fetched) {
        Call<ServerResponse> call = apiInterface.addStudentToBatch(headers, map);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void hitLogout(ResponseObject fetched) {
        Call<LogoutResponse> call = apiInterface.logout(headers);
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void hitAcceptDeclineJoinRequests(String requestId, boolean isAccept, ResponseObject fetched) {
        Call<ServerResponse> call;
        if (isAccept)
            call = apiInterface.acceptRequest(headers, requestId);
        else call = apiInterface.declineRequest(headers, requestId);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });

    }

    public void getBatchAttendance(GetAttendenceInput input, ResponseObject fetched) {

        Call<GetAttendenceResponse> call = apiInterface.getStudentAttendance(headers, input);
        call.enqueue(new Callback<GetAttendenceResponse>() {
            @Override
            public void onResponse(Call<GetAttendenceResponse> call, Response<GetAttendenceResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200)
                        fetched.OnCallback(response.body(), null);

                } else
                    fetched.OnCallback(null, response.code() + "");

            }

            @Override
            public void onFailure(Call<GetAttendenceResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });
    }

    public void getObjectiveTestDetails(String testId, ResponseObject fetched) {
        Call<ObjectiveTestDetailsResponse> call = apiInterface.getTestDetails(headers, testId);
        call.enqueue(new Callback<ObjectiveTestDetailsResponse>() {
            @Override
            public void onResponse(Call<ObjectiveTestDetailsResponse> call, Response<ObjectiveTestDetailsResponse> response) {
                Log.e(TAG, "onResponse: success" + response.code());
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else fetched.OnCallback(null, response.code() + " ");
            }

            @Override
            public void onFailure(Call<ObjectiveTestDetailsResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                fetched.OnCallback(null, t.getMessage());
            }
        });
    }

    public void submitObTestAns(String testId, String answ, ResponseObject fetched) {
        Call<KtServerResponse> call = apiInterface.submitObjectiveTestAns(headers, testId, answ);
        call.enqueue(new Callback<KtServerResponse>() {
            @Override
            public void onResponse(Call<KtServerResponse> call, Response<KtServerResponse> response) {
                Log.e(TAG, "onResponse: success" + response.code());
                if (response.code() == 200)
                    fetched.OnCallback(response.body(), null);
                else fetched.OnCallback(null, response.code() + " ");
            }

            @Override
            public void onFailure(Call<KtServerResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                fetched.OnCallback(null, t.getMessage());
            }
        });
    }

    public void emailLogin(LoginSignupInput input, ResponseObject fetched) {
        Call<LoginSignupResponse> call = apiInterface.emaillogin(input);
        call.enqueue(new Callback<LoginSignupResponse>() {
            @Override
            public void onResponse(Call<LoginSignupResponse> call, Response<LoginSignupResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200)
                        fetched.OnCallback(response.body(), null);
                } else
                    fetched.OnCallback(null, response.code() + "");
            }

            @Override
            public void onFailure(Call<LoginSignupResponse> call, Throwable t) {
                fetched.OnCallback(null, t.getMessage());
            }
        });
    }
}
