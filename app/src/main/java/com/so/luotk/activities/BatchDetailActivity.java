package com.so.luotk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.chat.fragments.ChatFragment;
import com.so.luotk.firebase.NotificationDataModel;
import com.so.luotk.fragments.courses.CourseStoreFragment;
import com.so.luotk.fragments.SendJoinRequestFragment;
import com.so.luotk.fragments.batches.StudyMaterialFragment;
import com.so.luotk.fragments.batches.AssignmentTestVideoListFragment;
import com.so.luotk.fragments.batches.AttendenceFragment;
import com.so.luotk.fragments.HomeFragment;
import com.so.luotk.fragments.batches.LiveFragment;
import com.so.luotk.fragments.batches.OverviewFragment;
import com.so.luotk.fragments.courses.StoreFragment;
import com.so.luotk.fragments.feestructure.FeeStructureFragment;
import com.so.luotk.fragments.more.AnnoucementsFragment;
import com.so.luotk.fragments.more.MoreOptionFragment;
import com.so.luotk.fragments.more.SetLanguageFragment;
import com.so.luotk.fragments.more.SettingFragment;
import com.so.luotk.fragments.reports.ReportAssignmentTestFragment;
import com.so.luotk.fragments.reports.ReportFragment;
import com.so.luotk.models.output.GetBatchSettingsResponse;
import com.so.luotk.models.output.GetBatchSettingsResult;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.so.luotk.utils.ZoomOutPageTransformer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.so.luotk.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.view.View.GONE;
import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class BatchDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public static LinearLayout batchLayout;
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private FrameLayout frameLayout;
    private TextView toolbarCustomTitle;
    private ArrayList<String> daysNameList, startTimeList, endTimeList;
    private String subjectName, className, batchCode, batchName, batchId, batch_color, notification_type;
    private ImageView imgAnnouncementIcon, notify_new_Announcemnt;
    private Bundle bundle;
    private int existClick = 0, tabCount = 5;
    private View layoutMainView;
    // private Settings batchSettings;
    private boolean isAttendenceOn, isAssignmentOn, isTestOn, isVideoOn, isLiveOn, isStudyMaterial;
    private ArrayList<String> tabList;
    private ViewPager viewPager;
    private Handler handler;
    private Runnable runnable;
    private boolean isFirstInternetToastDone;
    private TabLayout tabLayout;
    private LinearProgressIndicator progressIndicator;
    private List<NotificationDataModel> notificationDataList;
    private boolean isBatchOpen = true;
    private long mLastClickTime=0;
    private ProgressBar data_progress;
//    public static ShimmerFrameLayout shimmerFrameLayout;
    @Override
    protected void onStart() {
        super.onStart();
        if (!PreferenceHandler.readBoolean(this, PreferenceHandler.LOGGED_IN, false)) {
            startActivity(new Intent(this, WelcomeActivityNew.class));
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
// getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        overridePendingTransition(0, 0);
        Utilities.setUpStatusBar(this); Utilities.setLocale(this);
        setContentView(R.layout.activity_batch_detail);
        setToolbar();
        setupUI();
    }

    private void setToolbar() {
        notificationDataList = PreferenceHandler.getNotificationDataList(this);
        toolbar = findViewById(R.id.toolbar);
        toolbarCustomTitle = findViewById(R.id.tv_toolbar_title);
        imgAnnouncementIcon = toolbar.findViewById(R.id.img_announcemenr_icon);
        notify_new_Announcemnt = findViewById(R.id.notify_new_announcement);
        progressIndicator = findViewById(R.id.progress_indicator);

        setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        this.getSupportActionBar().setHomeAsUpIndicator(upArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgAnnouncementIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                imgAnnouncementIcon.setImageResource(R.drawable.ic_notification_new);
                startActivity(new Intent(BatchDetailActivity.this, AnnouncementActivity.class).putExtra(PreferenceHandler.BATCH_ID, batchId)
                        .putExtra(PreferenceHandler.IS_FROM, "batch"));
            }
        });
    }

    /*@Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentMode = newConfig.uiMode;
        if (AppCompatDelegate.getDefaultNightMode() != currentMode) {
            startActivity(new Intent(BatchDetailActivity.this, BatchDetailActivity.class));
            finish();
        }
    }*/
    private void setupUI() {
        if (getIntent() != null) {
            batchId = getIntent().getStringExtra(PreferenceHandler.BATCH_ID);
            batchName = getIntent().getStringExtra(PreferenceHandler.BATCH_NAME);
            batch_color = getIntent().getStringExtra(PreferenceHandler.BATCH_COLOR_CODE);
            notification_type = getIntent().getStringExtra(PreferenceHandler.NOTIFICATION_TYPE);

        }

        PreferenceHandler.writeString(this, PreferenceHandler.VIDEO_CACHING, null);
        setUpNotificationView();
        Utilities.hideKeyBoard(this);
        tabLayout = findViewById(R.id.tab_layout);

        layoutMainView = findViewById(R.id.layout_batch_detail);
//        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        /*shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmer();*/
        frameLayout = findViewById(R.id.container);
        batchLayout = findViewById(R.id.batch_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavigationBatchesMenuId);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigationView.setVisibility(View.GONE);
        data_progress = findViewById(R.id.data_progress);
        bundle = new Bundle();
        if (batchId != null) {
            bundle.putString(PreferenceHandler.BATCH_ID, batchId);
        }
        if (batch_color != null) {
            bundle.putString(PreferenceHandler.BATCH_COLOR_CODE, batch_color);
        }

        tabLayout.setSelectedTabIndicatorColor(ResourcesCompat.getColor(getResources(), R.color.blue_main, null));
        PreferenceHandler.writeBoolean(this, PreferenceHandler.END_JITSI_ROOM_ACTION, false);
        viewPager = findViewById(R.id.pager);
//        viewPager.setVisibility(View.VISIBLE);
        handler = new Handler(Looper.myLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        checkInternet();

    }

    private void checkInternet() {
        if (Utilities.checkInternet(this)) {
            handler.removeCallbacks(runnable);
            if (batchId != null)
                hitGetBatchSettingsService();
            else {
                Toast.makeText(getApplicationContext(), "Invalid batch", Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Utilities.makeToast(this, getString(R.string.internet_connection_error));
                isFirstInternetToastDone = true;
            }
        }
    }

    private void hitGetBatchSettingsService() {
        APIInterface apiInterface = ApiUtils.getApiInterface();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));

        Log.d("BatchDetail", "hitGetBatchSettingsService: " + PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
        Log.d("BatchDetail", "hitGetBatchSettingsService: " + PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));
        Call<GetBatchSettingsResponse> call = apiInterface.getBatchSettings(headers, batchId);
        call.enqueue(new Callback<GetBatchSettingsResponse>() {
            @Override
            public void onResponse(Call<GetBatchSettingsResponse> call, Response<GetBatchSettingsResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                        if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                            if (response.body().getResult() != null) {
                                applyBatchSettings(response.body().getResult());
                            } else {
                                GetBatchSettingsResult settings = (GetBatchSettingsResult) getIntent().getSerializableExtra(PreferenceHandler.BATCH_SETTINGS);
                                if (settings != null) {
                                    applyBatchSettings(settings);
                                }
                            }
                            if (response.body().getExtra() != null) {
                                toolbarCustomTitle.setText(response.body().getExtra().getBatchName());
                            }
                        }

                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(BatchDetailActivity.this);
                    }
                }
                progressIndicator.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<GetBatchSettingsResponse> call, Throwable t) {
                progressIndicator.setVisibility(View.GONE);
                Log.e("Retrofit Failure", t.getMessage());
                Toast.makeText(BatchDetailActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                GetBatchSettingsResult settings = (GetBatchSettingsResult) getIntent().getSerializableExtra(PreferenceHandler.BATCH_SETTINGS);
                if (settings != null) {
                    applyBatchSettings(settings);
                }
            }
        });
    }

    private void applyBatchSettings(GetBatchSettingsResult batchSettings) {
        if (batchSettings != null) {
            if (batchSettings.getAnnouncement().equalsIgnoreCase("1"))
                imgAnnouncementIcon.setVisibility(View.VISIBLE);
            else imgAnnouncementIcon.setVisibility(View.GONE);
            isAttendenceOn = batchSettings.getAttendance().equalsIgnoreCase("1");
            isAssignmentOn = batchSettings.getManageAssignment().equalsIgnoreCase("1");

            if (PreferenceHandler.readString(this, PreferenceHandler.IS_LITE_APP, "0").equalsIgnoreCase("1")) {
                isTestOn = batchSettings.getTest().equalsIgnoreCase("1");
                isTestOn = false;
            } else {
                isTestOn = batchSettings.getTest().equalsIgnoreCase("1");
            }
            isVideoOn = batchSettings.getManageVideo().equalsIgnoreCase("1");
            if (PreferenceHandler.readBoolean(this, PreferenceHandler.ISSTATICLOGIN, false)) {
                isLiveOn = false;
            } else {
                isLiveOn = batchSettings.getLive().equalsIgnoreCase("1");
            }

            isStudyMaterial = batchSettings.getStudy_material().equalsIgnoreCase("1");
            if (data_progress.getVisibility() == View.VISIBLE) {
                data_progress.setVisibility(GONE);
            }

            setupSettingsUI();
        }
    }

    private void setupSettingsUI() {
        tabList = new ArrayList<>();

        tabList.add(getString(R.string.overview));
        if (isAttendenceOn) {
            tabList.add(getString(R.string.attendance));
        }
        if (isAssignmentOn) {
            tabList.add(getString(R.string.assignments));
        }
        if (isTestOn) {
            tabList.add(getString(R.string.tests));
        }
        if (isVideoOn) {
            tabList.add(getString(R.string.videos));
        }
        if (isLiveOn) {
            tabList.add(getString(R.string.live));
        }
        if (isStudyMaterial) {
            tabList.add(getString(R.string.study_material));
        }
//        tabList.add("Chat");
        tabCount = tabList.size();
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        tabLayout.setupWithViewPager(viewPager);
        if (notification_type != null) {
            if (notification_type.equalsIgnoreCase("assignment") || notification_type.equalsIgnoreCase("assignmentUpdate")) {
                for (int i = 0; i < tabList.size(); i++) {
                    if (tabList.get(i).equalsIgnoreCase(getString(R.string.assignments))) {
                        tabLayout.getTabAt(i).select();
                    }
                }
            } else if (notification_type.equalsIgnoreCase("test") || notification_type.equalsIgnoreCase("testUpdate")) {
                for (int i = 0; i < tabList.size(); i++) {
                    if (tabList.get(i).equalsIgnoreCase(getString(R.string.tests))) {
                        tabLayout.getTabAt(i).select();
                    }
                }

            } else if (notification_type.equalsIgnoreCase("video")) {
                for (int i = 0; i < tabList.size(); i++) {
                    if (tabList.get(i).equalsIgnoreCase(getString(R.string.videos))) {
                        tabLayout.getTabAt(i).select();
                    }
                }

            } else if (notification_type.equalsIgnoreCase("Live Start")) {
                for (int i = 0; i < tabList.size(); i++) {
                    if (tabList.get(i).equalsIgnoreCase(getString(R.string.live))) {
                        tabLayout.getTabAt(i).select();
                    }
                }

            } else if (notification_type.equalsIgnoreCase("attendance")) {
                for (int i = 0; i < tabList.size(); i++) {
                    if (tabList.get(i).equalsIgnoreCase(getString(R.string.attendance))) {
                        tabLayout.getTabAt(i).select();
                    }
                }

            } else if (notification_type.equalsIgnoreCase("Live Ended")) {
                for (int i = 0; i < tabList.size(); i++) {
                    if (tabList.get(i).equalsIgnoreCase(getString(R.string.live))) {
                        tabLayout.getTabAt(i).select();
                    }
                }

            } else if (notification_type.equalsIgnoreCase("materialAdd")) {
                for (int i = 0; i < tabList.size(); i++) {
                    if (tabList.get(i).equalsIgnoreCase(getString(R.string.study_material))) {
                        tabLayout.getTabAt(i).select();
                    }
                }

            } /*else if (notification_type.equalsIgnoreCase("chat_notification")) {
                for (int i = 0; i < tabList.size(); i++) {
                    if (tabList.get(i).equalsIgnoreCase("Chat")) {
                        tabLayout.getTabAt(i).select();
                    }
                }

            }*/


        }
       /* try {
            boolean isAdmn = PreferenceHandler.readBoolean(BatchDetailActivity.this, PreferenceHandler.ADMIN_LOGGED_IN, false);
            if (!isAdmn) {
                if (BatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                    BatchDetailActivity.shimmerFrameLayout.stopShimmer();
                    BatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                    BatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                    BatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            for (int i = 0; i < tabList.size(); i++) {
                if (position == 0) {
                    fragment = OverviewFragment.newInstance(batchId, "student", "batch");
                }
                if (position == 1 && position < tabCount) {
                    if (tabList.get(1).equalsIgnoreCase(getString(R.string.attendance))) {
                        fragment = AttendenceFragment.newInstance(batchId);
                    } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.assignments))) {
                        fragment = AssignmentTestVideoListFragment.newInstance("assignment", batchId, true, "");
                    } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.tests))) {
                        fragment = AssignmentTestVideoListFragment.newInstance("test", batchId, true, "");
                    } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.videos))) {
                        fragment = AssignmentTestVideoListFragment.newInstance("video", batchId, true, "");
                    } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.live))) {
                        fragment = LiveFragment.newInstance(batchId, "batch");
                    } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.study_material))) {
                        fragment = StudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }
                if (position == 2 && position < tabCount) {

                    if (tabList.get(2).equalsIgnoreCase(getString(R.string.assignments))) {
                        fragment = AssignmentTestVideoListFragment.newInstance("assignment", batchId, true, "");
                    } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.tests))) {
                        fragment = AssignmentTestVideoListFragment.newInstance("test", batchId, true, "");
                    } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.videos))) {
                        fragment = AssignmentTestVideoListFragment.newInstance("video", batchId, true, "");
                    } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.live))) {
                        fragment = LiveFragment.newInstance(batchId, "batch");
                    } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.study_material))) {
                        fragment = StudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }
                if (position == 3 && position < tabCount) {

                    if (tabList.get(3).equalsIgnoreCase(getString(R.string.tests))) {
                        fragment = AssignmentTestVideoListFragment.newInstance("test", batchId, true, "");
                    } else if (tabList.get(3).equalsIgnoreCase(getString(R.string.videos))) {
                        fragment = AssignmentTestVideoListFragment.newInstance("video", batchId, true, "");
                    } else if (tabList.get(3).equalsIgnoreCase(getString(R.string.live))) {
                        fragment = LiveFragment.newInstance(batchId, "batch");
                    } else if (tabList.get(3).equalsIgnoreCase(getString(R.string.study_material))) {
                        fragment = StudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    } else if (tabList.get(3).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }
                if (position == 4 && position < tabCount) {
                    if (tabList.get(4).equalsIgnoreCase(getString(R.string.videos))) {
                        fragment = AssignmentTestVideoListFragment.newInstance("video", batchId, true, "");
                    } else if (tabList.get(4).equalsIgnoreCase(getString(R.string.live))) {
                        fragment = LiveFragment.newInstance(batchId, "batch");
                    } else if (tabList.get(4).equalsIgnoreCase(getString(R.string.study_material))) {
                        fragment = StudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    } else if (tabList.get(4).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }
                if (position == 5 && position < tabCount) {
                    if (tabList.get(5).equalsIgnoreCase(getString(R.string.live))) {
                        fragment = LiveFragment.newInstance(batchId, "batch");
                    } else if (tabList.get(5).equalsIgnoreCase(getString(R.string.study_material))) {
                        fragment = StudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    } else if (tabList.get(5).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }
                if (position == 6 && position < tabCount) {
                    if (tabList.get(6).equalsIgnoreCase(getString(R.string.study_material))) {
                        fragment = StudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    } else if (tabList.get(6).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }
                if (position == 7 && position < tabCount) {
                    if (tabList.get(7).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return tabCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            for (int i = 0; i < tabList.size(); i++) {
                if (position == i && position < tabCount) {
                    title = (tabList.get(i));
                }
            }
            return title;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        handler.removeCallbacks(runnable);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottomNavigationHomeMenuId:
                            startActivity(new Intent(BatchDetailActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("isFrom", "home"));
                            finish();
                            return true;
                        case R.id.bottomNavigationBatchesMenuId:
                            startActivity(new Intent(BatchDetailActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("isFrom", "batch"));
                            finish();
                            isBatchOpen = true;
                            return true;
                        case R.id.bottomNavigationReportMenuId:
                            fragment = new ReportFragment();
                            startActivity(new Intent(BatchDetailActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("isFrom", "report"));
                            finish();
                            return true;
                        case R.id.bottomNavigationFeeMenuId:
                            fragment = new StoreFragment();
                            startActivity(new Intent(BatchDetailActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("isFrom", "course"));
                            finish();
                            return true;
                        case R.id.bottomNavigationMoreOptionId:
                            fragment = new MoreOptionFragment();
                            startActivity(new Intent(BatchDetailActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("isFrom", "more"));
                            finish();
                            return true;
                    }
                    return false;
                }
            };

    public void openFragment(Fragment fragment) {
        batchLayout.setVisibility(View.GONE);
        frameLayout.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        //finish();

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
        if (f instanceof HomeFragment || f instanceof ReportFragment || f instanceof FeeStructureFragment || f instanceof CourseStoreFragment || f instanceof MoreOptionFragment) {
            if (existClick == 0) {
                Utilities.makeToast(this, "Press again to exit");
                existClick++;
            } else {
                Utilities.hideKeyBoard(this);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else if (f instanceof SettingFragment) {
            ((SettingFragment) f).onBackPressed();
        } else if (f instanceof AnnoucementsFragment) {
            ((AnnoucementsFragment) f).onBackPressed();
        } else if (f instanceof SetLanguageFragment) {
            ((SetLanguageFragment) f).onBackPressed();
        } else if (f instanceof SendJoinRequestFragment) {
            ((SendJoinRequestFragment) f).onBackPressed();
        } else if (f instanceof ReportAssignmentTestFragment) {
            ((ReportAssignmentTestFragment) f).onBackPressed();
        } else {
            finish();
        }


    }

    private void setUpNotificationView() {
        if (notificationDataList != null && notificationDataList.size() > 0)
            for (int i = 0; i < notificationDataList.size(); i++) {
                if (notificationDataList.get(i).getBatchId().equalsIgnoreCase(batchId) &&
                        notificationDataList.get(i).getNotificationType().equalsIgnoreCase("announcement")) {
                    imgAnnouncementIcon.setImageResource(R.drawable.ic_notify_batch);
                }
            }
    }


}
