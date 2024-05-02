package com.so.luotk.api;

import com.so.luotk.models.input.CheckUserExistInput;
import com.so.luotk.models.input.CreateZoomRoomInput;
import com.so.luotk.models.input.GetAttendenceInput;
import com.so.luotk.models.input.LoginSignupInput;

import com.so.luotk.models.input.VerifyLoginInput;
import com.so.luotk.models.newmodels.adminBatchModel.GetBatchCreateId;
import com.so.luotk.models.newmodels.adminBatchModel.GetCourseIdResponse;
import com.so.luotk.models.newmodels.adminBatchModel.GetSubjectIdResponse;
import com.so.luotk.models.newmodels.study.StudyMaterialModel;
import com.so.luotk.models.output.CheckuserexistResponse;
import com.so.luotk.models.output.CreateJitsiRoomResponse;
import com.so.luotk.models.output.CreateZoomRoomResponse;
import com.so.luotk.models.output.EndLiveRoomResponse;
import com.so.luotk.models.output.GetAnnouncementListResponse;
import com.so.luotk.models.output.GetAssignmentTestListResponse;
import com.so.luotk.models.output.GetBatchFolderVideosResponse;
import com.so.luotk.models.output.GetBatchListResponse;
import com.so.luotk.models.output.GetBatchLiveRoomResponse;
import com.so.luotk.models.output.GetBatchOverviewResponse;
import com.so.luotk.models.output.GetBatchRequestListResponse;
import com.so.luotk.models.output.GetBatchSettingsResponse;
import com.so.luotk.models.output.GetBatchSubmitAssignmentTestResponse;
import com.so.luotk.models.output.GetReportTestListResponse;
import com.so.luotk.models.output.GetStudentDataResponse;
import com.so.luotk.models.output.GetUserProfileResponse;
import com.so.luotk.models.output.LoginSignupResponse;
import com.so.luotk.models.output.LogoutResponse;
import com.so.luotk.models.output.GetAttendenceResponse;
import com.so.luotk.models.output.MarkAttendenceResponse;
import com.so.luotk.models.output.SendBatchJoinRequestResponse;
import com.so.luotk.models.output.UpdateUserProfileResponse;
import com.so.luotk.models.output.VerifyLoginResponse;
import com.so.luotk.models.output.VerifyOrgResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface APIInterface {
  /*  @GET("checkuserexist")
    Call<CheckuserexistResponse> checkIfUserAlreadyExist();
*/


    @POST("checkuserexist")
    Call<CheckuserexistResponse> checkIfUserAlreadyExist(@Body CheckUserExistInput CheckUserExistInput);

    @POST("registerlogin")
    Call<LoginSignupResponse> loginSignup(@Body LoginSignupInput signupInput);

    @POST("registeremaillogin")
    Call<LoginSignupResponse> emaillogin(@Body LoginSignupInput signupInput);

    @POST("verifielogin")
    Call<VerifyLoginResponse> verifyLogin(@Body VerifyLoginInput verifyLoginInput);

    @GET("get-batch-list")
    Call<GetBatchListResponse> getBatchList(@HeaderMap Map<String, String> headers);

    @POST("get-batch-assignments?")
    Call<GetAssignmentTestListResponse> getAssignmentList(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId, @Query("search") String search, @Query("pageLength") String pageLength, @Query("page") String page);

    @POST("get-batch-tests?")
    Call<GetAssignmentTestListResponse> getTestList(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId, @Query("search") String search, @Query("pageLength") String pageLength, @Query("page") String page);

    @POST("get-batch-announcements")
    Call<GetAnnouncementListResponse> getAnnouncementList(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId, @Query("pageLength") String pageLength, @Query("page") String page);

    @POST("get-batch-folder-videos?")
    Call<GetBatchFolderVideosResponse> getBatchFolderList(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId, @Query("pageLength") String pageLength, @Query("page") String page);

    @POST("get-batch-folder-videos?")
    Call<GetBatchFolderVideosResponse> getBatchVideoListUnderFolder(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId, @Query("folderId") String folderId, @Query("pageLength") String pageLength, @Query("page") String page);


    @POST("batch-join-request")
    Call<SendBatchJoinRequestResponse> sendBatchJoinRequest(@HeaderMap Map<String, String> headers, @Query("batchCode") String batchCode);

    //zoom api
    @POST("batch-create-zoom-room")
    Call<CreateZoomRoomResponse> createZoomRoom(@HeaderMap Map<String, String> headers, @Body CreateZoomRoomInput createZoomRoomInput);

    @POST("get-batch-live-room")
    Call<GetBatchLiveRoomResponse> getLiveRoom(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId, @Query("type") String type);

    @POST("batch-end-room")
    Call<EndLiveRoomResponse> endLiveRoom(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId, @Query("roomId") String roomId);

    @GET("get-admin-batch-list")
    Call<GetBatchListResponse> getAdminBatchList(@HeaderMap Map<String, String> headers);

    @POST("verifieorg")
    Call<VerifyOrgResponse> verifyOrgCode(@Query("orgCode") String orgCode);

    //Vishal

    @Multipart
    @POST("create-assignment")
    Call<GetBatchSubmitAssignmentTestResponse> createNewAssignment(@HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params, @Query("students") List<String> students, @Part MultipartBody.Part[] image);

    @Multipart
    @POST("create-assignment")
    Call<GetBatchSubmitAssignmentTestResponse> createNewAssignment(@HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params, @Query("students") List<String> students);


    @Multipart
    @POST("update-assignment")
    Call<GetBatchSubmitAssignmentTestResponse> updateAssignment(@HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params, @Query("students") String students, @Query("old_attachment[]") List<String> old_attachment, @Part MultipartBody.Part[] image);

    @Multipart
    @POST("update-assignment")
    Call<GetBatchSubmitAssignmentTestResponse> updateAssignment(@HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params, @Query("students") String students, @Part MultipartBody.Part[] image);

    @Multipart
    @POST("update-assignment")
    Call<GetBatchSubmitAssignmentTestResponse> updateAssignment(@HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params, @Query("students") String students, @Query("old_attachment[]") List<String> old_attachment);

    @Multipart
    @POST("update-assignment?")
    Call<GetBatchSubmitAssignmentTestResponse> updateAssignment(@HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params, @Query("students") String students);

    @POST("delete/assignment")
    Call<GetBatchSubmitAssignmentTestResponse> deleteAssignment(@HeaderMap Map<String, String> headers, @QueryMap Map<String, RequestBody> params);


    @POST("create/folder")
    Call<GetBatchSubmitAssignmentTestResponse> createNewVideoFolder(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    @POST("update/folder")
    Call<GetBatchSubmitAssignmentTestResponse> updateFolder(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    @POST("create/video")
    Call<GetBatchSubmitAssignmentTestResponse> createNewVideoLink(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    @POST("update/video")
    Call<GetBatchSubmitAssignmentTestResponse> updateVideoLink(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    @POST("delete/video")
    Call<GetBatchSubmitAssignmentTestResponse> deleteVideo(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    @POST("delete/video/folder")
    Call<GetBatchSubmitAssignmentTestResponse> deleteVideoFolder(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);


    @POST("create/study/material/folder")
    Call<GetBatchSubmitAssignmentTestResponse> createNewMaterialFolder(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    @POST("update/study/material/folder")
    Call<GetBatchSubmitAssignmentTestResponse> updateMaterialFolder(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    @POST("create/study/material/link")
    Call<GetBatchSubmitAssignmentTestResponse> createNewMaterialLink(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    @POST("update/study/material/link")
    Call<GetBatchSubmitAssignmentTestResponse> updateMaterialLink(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    @Multipart
    @POST("create/study/material/file")
    Call<GetBatchSubmitAssignmentTestResponse> createNewMaterialFile(@HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params, @Part MultipartBody.Part[] file);

    @Multipart
    @POST("update/study/material/file")
    Call<GetBatchSubmitAssignmentTestResponse> updateMaterialFile(@HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params, @Part MultipartBody.Part[] file);

    @POST("create/batch")
    Call<GetBatchSubmitAssignmentTestResponse> createBatch(@HeaderMap Map<String, String> headers, @Query("days") String days, @QueryMap Map<String, String> params);

    @POST("update/batch")
    Call<GetBatchSubmitAssignmentTestResponse> updateBatch(@HeaderMap Map<String, String> headers, @Query("days") String days, @QueryMap Map<String, String> params);

    @POST("delete/batch")
    Call<GetBatchSubmitAssignmentTestResponse> deleteBatch(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);


    @POST("all-assignments?")
    Call<GetAssignmentTestListResponse> getAllAssignmentList(@HeaderMap Map<String, String> headers, @Query("fk_batchId") String batchId, @Query("search") String search, @Query("pageLength") String pageLength, @Query("page") String page);

    @POST("categories")
    Call<GetBatchCreateId> getBatchCatIds(@HeaderMap Map<String, String> headers);

    @POST("get-course-by-category")
    Call<GetCourseIdResponse> getBatchCourseIds(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);

    @POST("get-subject-by-course")
    Call<GetSubjectIdResponse> getBatchSubjectIds(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> params);


    // Testt
    @Multipart
    @POST("get-batch-submit-assignment")
    Call<GetBatchSubmitAssignmentTestResponse> submitBatchAssignment(@HeaderMap Map<String, String> headers, @Query("assignmentId") String assignmentId, @Query("submit_date") String submit_date, @Part MultipartBody.Part[] image);

    @Multipart
    @POST("get-batch-submit-test")
    Call<GetBatchSubmitAssignmentTestResponse> submitBatchTest(@HeaderMap Map<String, String> headers, @Query("testId") String testId, @Query("submit_date") String submit_date, @Part MultipartBody.Part[] image);

    @Multipart
    @POST("submit-course-test")
    Call<GetBatchSubmitAssignmentTestResponse> submitCourseTest(@HeaderMap Map<String, String> headers, @Query("testId") String testId, @Part MultipartBody.Part[] image);

    @POST("get-assignments-report?")
    Call<GetAssignmentTestListResponse> getReportAssignmentList(@HeaderMap Map<String, String> headers, @Query("search") String search, @Query("pageLength") String pageLength, @Query("page") String page);

    @POST("get-tests-report?")
    Call<GetReportTestListResponse> getReportTestList(@HeaderMap Map<String, String> headers, @Query("search") String search, @Query("pageLength") String pageLength, @Query("page") String page);

    @POST("get-course-tests-report")
    Call<GetReportTestListResponse> getCourseReportTestList(@HeaderMap Map<String, String> headers, @Query("search") String search, @Query("pageLength") String pageLength, @Query("page") String page);

    @POST("batch-join-request-list")
    Call<GetBatchRequestListResponse> getBatchJoinRequestList(@HeaderMap Map<String, String> headers);

    @POST("get-user-profile")
    Call<GetUserProfileResponse> getUserProfile(@HeaderMap Map<String, String> headers);

    @Multipart
    @POST("update-user-profile")
    Call<UpdateUserProfileResponse> updateUserProfileWithImage(@HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params, @Part MultipartBody.Part image);

    @Multipart
    @POST("update-user-profile")
    Call<UpdateUserProfileResponse> updateUserProfile(@HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params);

    //jitsi api
    @POST("batch-create-room")
    Call<CreateJitsiRoomResponse> createJitsiRoom(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId);

    @POST("logout")
    Call<LogoutResponse> logout(@HeaderMap Map<String, String> headers);

    @POST("mark/attendance")
    Call<MarkAttendenceResponse> markStudentLiveAttendence(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId, @Query("roomId") String roomId);

    @POST("batch-attendance")
    Call<GetAttendenceResponse> getStudentAttendence(@HeaderMap Map<String, String> headers, @Body GetAttendenceInput getAttendenceInput);

    @POST("get-batch-overview")
    Call<GetBatchOverviewResponse> getBatchOverview(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId);

    @POST("get-batch-settings")
    Call<GetBatchSettingsResponse> getBatchSettings(@HeaderMap Map<String, String> headers, @Query("batchId") String batchId);

    @POST("get-batch-students")
    Call<GetStudentDataResponse> getBatchStudents(@HeaderMap Map<String, String> headers, @Query("search") String search, @Query("batchId") String batchId, @Query("pageLength") String pageLength, @Query("page") String page);

    @POST("get-study-material")
    Call<StudyMaterialModel> getStudyMaterialrList(@HeaderMap Map<String, String> headers, @Query("search") String search, @Query("batchId") String batchId, @Query("pageLength") String pageLength, @Query("folderId") String folderId, @Query("page") String page);
}

