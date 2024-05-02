package com.so.luotk.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.so.luotk.R;
import com.so.luotk.activities.adminrole.AdminMainScreen;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.models.output.VerifyOrgResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    public static final String TAG = "SplashActivity";
    private static final int REQ_CODE_VERSION_UPDATE = 530;
    private static final int SPLASH_SCREEN_TIME_OUT = 2000;
    private static final int NOTIFICATION_PERMISSION_CODE = 2000;
    private boolean isLoggedIn, isAdminLoggedIn;
    private static final String[] NOTIFICATION_PERMISSION = {Manifest.permission.POST_NOTIFICATIONS};
    // private String locale = "en";
    String courseId = "", coursName = "", fromLink = "", courseData = "";
    AppUpdateManager appUpdateManager;
    protected ProgressBar progressBar;
    private final boolean screenShare = false;
    private final boolean fileDownload = false;
    private String youTubeSpeed = "4";
    private boolean useStaticNumber = false;
    private String folderInFolder = "0";
    private String is_lite = "0";
    private String courses_on_home = "1";
    private String share_course = "0";
    public static String StaticNumner = "9999966666";
    public static String StaticOtp = "1133";
    public static String StaticUserId = "496323";
    private APIInterface apiInterface;
    private Runnable runnable;
    private Handler handler;
    String lang = "en";
    private RelativeLayout promotion_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View decorView = getWindow().getDecorView();
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Utilities.restrictScreenShot(this);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_splash);
        changeStatusBarColor();
        progressBar = findViewById(R.id.progressBar);
        promotion_layout = findViewById(R.id.promotion_layout);
        apiInterface = ApiUtils.getApiInterface();
        handler = new Handler(Looper.myLooper());
        Uri uri = getIntent().getData();
        if (uri != null) {
            List<String> parameters = uri.getPathSegments();
            String param = parameters.get(parameters.size() - 1);

            Log.d("Splash", "onCreate: " + param);
        }
        PreferenceHandler.writeBoolean(SplashActivity.this, PreferenceHandler.SCREENSHARE, screenShare);
        PreferenceHandler.writeBoolean(SplashActivity.this, PreferenceHandler.FILE_DOWNLOADED, fileDownload);
        PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.YOUTUBE_SPEED, youTubeSpeed);
        PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.ISACTIVEORG, "1");
        PreferenceHandler.writeBoolean(SplashActivity.this, PreferenceHandler.USESTATIC_NUMBER, useStaticNumber);
        PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.FOLDER_IN_FOLDER, folderInFolder);
        PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.IS_LITE_APP, is_lite);
        PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.COURSES_ON_HOME, courses_on_home);
        PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.SHARE_COURSE, share_course);
        PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.WHATSAPP_NUMBER, getString(R.string.whatsap_no_more));
        PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.CALLING_NUMBER, getString(R.string.call_no_more));

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(myLocale);
        } else {
            conf.locale = myLocale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createConfigurationContext(conf);
        } else {
            res.updateConfiguration(conf, dm);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
        {
            runnable = () -> checkInternet();
            checkInternet();
        } else {
            ActivityCompat.requestPermissions(this, NOTIFICATION_PERMISSION, NOTIFICATION_PERMISSION_CODE);
            Log.d(TAG, "onCreate: not Granted");
        }

    }
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
    private void checkInternet() {
        Log.d("TAG", "checkInternet: ");
        if (Utilities.checkInternet(SplashActivity.this)) {
            handler.removeCallbacks(runnable);
            hitVerifyOrgCodeService();
        } else {
            handler.postDelayed(runnable, 5000);
            Utilities.makeToast(SplashActivity.this, getString(R.string.internet_connection_error));

        }
    }
    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Utilities.setLocale(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        inAppUpdate();
    }

    private void inAppUpdate() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {

                Log.e("AVAILABLE_VERSION_CODE", appUpdateInfo.availableVersionCode()+ " : " + appUpdateInfo.updateAvailability());
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Request the update.

                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.IMMEDIATE,
                                // The current activity making the update request.
                                SplashActivity.this,
                                // Include a request code to later monitor this update request.
                                REQ_CODE_VERSION_UPDATE);
                    } catch (IntentSender.SendIntentException ignored) {

                    }
                } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE){
                    openNextScreen();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        openNextScreen();
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                })
        ;

        appUpdateManager.registerListener(installStateUpdatedListener);

    }
    //lambda operation used for below listener
    InstallStateUpdatedListener installStateUpdatedListener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate();
        } else
            Log.e("UPDATE", "Not downloaded yet");
    };


    private void popupSnackbarForCompleteUpdate() {


        Snackbar snackbar =
                Snackbar.make(
                        findViewById(android.R.id.content),
                        "Update almost finished!",
                        Snackbar.LENGTH_INDEFINITE);
        //lambda operation used for below action
        snackbar.setAction(this.getString(R.string.resend_otp_in), view ->
                appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(getResources().getColor(R.color.primary_darker));
        snackbar.show();

        openNextScreen();

    }

    private void openNextScreen() {
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                isLoggedIn = PreferenceHandler.readBoolean(SplashActivity.this, PreferenceHandler.LOGGED_IN, false);
                isAdminLoggedIn = PreferenceHandler.readBoolean(SplashActivity.this, PreferenceHandler.ADMIN_LOGGED_IN, false);
                if (isLoggedIn || isAdminLoggedIn) {
                    if (isAdminLoggedIn) {
                        Intent i = new Intent(SplashActivity.this, AdminMainScreen.class);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        i.putExtra("courseName", coursName);
                        i.putExtra("courseId", courseId);
                        i.putExtra("fromLink", fromLink);
                        i.putExtra("courseData", courseData);
                        startActivity(i);
                    }
                    finish();
                } else {
//                    Intent i = new Intent(SplashActivity.this, WelcomeActivity.class);
                    Intent i = new Intent(SplashActivity.this, WelcomeActivityNew.class);
                    i.putExtra("courseName", coursName);
                    i.putExtra("courseId", courseId);
                    i.putExtra("fromLink", fromLink);
                    i.putExtra("courseData", courseData);
                    startActivity(i);
                    finish();
                }

            }
        }, SPLASH_SCREEN_TIME_OUT);
    }

    private void hitVerifyOrgCodeService() {
        retrofit2.Call<VerifyOrgResponse> call = apiInterface.verifyOrgCode(getString(R.string.organization_code));
        call.enqueue(new Callback<VerifyOrgResponse>() {
            @Override
            public void onResponse(retrofit2.Call<VerifyOrgResponse> call, Response<VerifyOrgResponse> response) {
//                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            if ((response.body().getResult() != null)) {
                                PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.ISACTIVEORG, response.body().getResult().getIsActive());
                                try {
                                    if (response.body().getResult().getRazorpaytoken() != null) {
                                        JSONObject jsonObject = new JSONObject(response.body().getResult().getRazorpaytoken());
                                        JSONObject staticResult = jsonObject.getJSONObject("static_result");
                                        if (staticResult.optString("youtube").equals("4")) {
                                            youTubeSpeed = "4";
                                        } else if (staticResult.optString("youtube").equals("3")) {
                                            youTubeSpeed = "4";
                                        } else if (staticResult.optString("youtube").equals("2")) {
                                            youTubeSpeed = "2";
                                        } else if (staticResult.optString("youtube").equals("1")) {
                                            youTubeSpeed = "1";
                                        } else {
                                            youTubeSpeed = "0";
                                        }
                                        PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.YOUTUBE_SPEED, youTubeSpeed);
                                        if (staticResult.optBoolean("staticNumber") == true) {
                                            useStaticNumber = true;
                                            PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.WHATSAPP_NUMBER, staticResult.optString("whatsapp_number"));
                                            PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.CALLING_NUMBER, staticResult.optString("call_number"));
                                        } else {
                                            useStaticNumber = false;
                                            PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.WHATSAPP_NUMBER, response.body().getResult().getPhone());
                                            PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.CALLING_NUMBER, response.body().getResult().getPhone());
                                        }
                                        PreferenceHandler.writeBoolean(SplashActivity.this, PreferenceHandler.USESTATIC_NUMBER, useStaticNumber);
                                        if (staticResult.has("folder")){
                                            if (staticResult.optString("folder").equals("1")) {
                                                folderInFolder = "1";
                                                PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.FOLDER_IN_FOLDER, folderInFolder);
                                            } else {
                                                folderInFolder = "0";
                                                PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.FOLDER_IN_FOLDER, folderInFolder);
                                            }
                                        } else {
                                            PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.FOLDER_IN_FOLDER, folderInFolder);
                                        }
                                        if (staticResult.has("courses_on_home")){
                                            if (staticResult.optString("courses_on_home").equalsIgnoreCase("1")) {
                                                courses_on_home = "1";
                                                PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.COURSES_ON_HOME, courses_on_home);
                                            } else {
                                                courses_on_home = "0";
                                                PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.COURSES_ON_HOME, courses_on_home);
                                            }
                                        } else {
                                            PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.COURSES_ON_HOME, courses_on_home);
                                        }
                                        if (staticResult.has("share_course")){
                                            if (staticResult.optString("share_course").equalsIgnoreCase("1")) {
                                                share_course = "1";
                                                PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.SHARE_COURSE, share_course);
                                            } else {
                                                share_course = "0";
                                                PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.SHARE_COURSE, share_course);
                                            }
                                        } else {
                                            PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.SHARE_COURSE, share_course);
                                        }
                                        if (staticResult.has("is_lite")){
                                            if (staticResult.optString("is_lite").equals("1")) {
                                                is_lite = "1";
                                                PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.IS_LITE_APP, is_lite);
                                                if (promotion_layout.getVisibility() == View.GONE) {
                                                    promotion_layout.setVisibility(View.VISIBLE);
                                                }
                                            } else {
                                                is_lite = "0";
                                                PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.IS_LITE_APP, is_lite);
                                                if (promotion_layout.getVisibility() == View.VISIBLE) {
                                                    promotion_layout.setVisibility(View.GONE);
                                                }
                                            }
                                        } else {
                                            PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.IS_LITE_APP, is_lite);
                                            if (is_lite.equalsIgnoreCase("1")) {
                                                if (promotion_layout.getVisibility() == View.GONE) {
                                                    promotion_layout.setVisibility(View.VISIBLE);
                                                }
                                            } else {
                                                if (promotion_layout.getVisibility() == View.VISIBLE) {
                                                    promotion_layout.setVisibility(View.GONE);
                                                }
                                            }

                                        }

                                    } else {
                                        youTubeSpeed = "4";
                                        PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.YOUTUBE_SPEED, youTubeSpeed);
                                        PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.FOLDER_IN_FOLDER, folderInFolder);
                                        PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.COURSES_ON_HOME, courses_on_home);
                                        PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.SHARE_COURSE, share_course);
                                        PreferenceHandler.writeString(SplashActivity.this, PreferenceHandler.IS_LITE_APP, is_lite);
                                    }

                                    inAppUpdate();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    inAppUpdate();
                                }
                            }
                        }
                    } else if ((response.body().getSuccess().equalsIgnoreCase("false") && response.body().getStatus().equalsIgnoreCase("402"))) {
                        openNextScreen();
                    }
                }

            }

            @Override
            public void onFailure(retrofit2.Call<VerifyOrgResponse> call, Throwable t) {
//                mProgressDialog.dismiss();
                openNextScreen();
                Log.e("Retrofit Failure", t.getMessage());
                Toast.makeText(SplashActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: If");
                runnable = () -> checkInternet();
                checkInternet();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: Else");
                runnable = () -> checkInternet();
                checkInternet();
            }
        }
    }
}