package com.so.luotk.activities.adminrole;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.so.luotk.activities.WelcomeActivityNew;
import com.so.luotk.chat.fragments.ChatFragment;
import com.so.luotk.chat.models.MessageModel;
import com.so.luotk.client.MyClient;
import com.so.luotk.firebase.NotificationDataModel;
import com.so.luotk.fragments.adminrole.AdminBatchUiFragment;
import com.so.luotk.fragments.adminrole.AdminProfileFragment;
import com.so.luotk.fragments.adminrole.adminbatches.AdminAnnouncementFragment;
import com.so.luotk.fragments.adminrole.adminbatches.AdminAssignmentTestVideoFragment;
import com.so.luotk.fragments.adminrole.adminbatches.AdminAttendanceFragment;
import com.so.luotk.fragments.adminrole.adminbatches.AdminLiveFragment;
import com.so.luotk.fragments.adminrole.adminbatches.AdminStudentFragment;
import com.so.luotk.fragments.adminrole.adminbatches.AdminStudyMaterialFragment;
import com.so.luotk.fragments.batches.OverviewFragment;
import com.so.luotk.models.output.BatchListResult;
import com.so.luotk.models.output.GetBatchSettingsResponse;
import com.so.luotk.models.output.GetBatchSettingsResult;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.so.luotk.utils.ZoomOutPageTransformer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.so.luotk.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.view.View.GONE;
import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class AdminBatchDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public static final int UPDATE_BATCH_REQUEST= 5001;
    public static LinearLayout batchLayout;
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private FrameLayout frameLayout;
    private TextView toolbarCustomTitle;
    private ArrayList<String> daysNameList, startTimeList, endTimeList;
    private String subjectName, className, batchCode, batchId, batchData, batchPosition;
    private ImageView notify_new_Announcemnt;
    private Bundle bundle;
    private int existClick = 0, tabCount = 5;
    private View layoutMainView;
    // private Settings batchSettings;
    private boolean isAttendenceOn, isAssignmentOn, isTestOn, isVideoOn, isLiveOn, isStudyMaterial, isAnnouncement;
    private ArrayList<String> tabList;
    public static ViewPager viewPager;
    private Handler handler;
    private Runnable runnable;
    private boolean isFirstInternetToastDone;
    private TabLayout tabLayout;
    private LinearProgressIndicator progressIndicator;
    private List<NotificationDataModel> notificationDataList;
    private final boolean isBatchOpen = true;
    private final long mLastClickTime=0;
    public static LinearLayout iconLayout;
    public static ImageView img_announcemenr_icon;
    public static ArrayList<MessageModel> messageModelsDelete = new ArrayList<>();
    private LinkedHashMap<String, Fragment> map;
    private TextView editIcon;
    String userType;
    private ProgressBar data_progress;
    private final ArrayList<BatchListResult> batchListResults = new ArrayList<>();
//    public static ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onStart() {
        super.onStart();
        if (!PreferenceHandler.readBoolean(this, PreferenceHandler.ADMIN_LOGGED_IN, false)) {
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
        setContentView(R.layout.admin_activity_batch_detail);
        setToolbar();
        setupUI();
    }

    private void setToolbar() {
        notificationDataList = PreferenceHandler.getNotificationDataList(this);
        toolbar = findViewById(R.id.toolbar);
        toolbarCustomTitle = findViewById(R.id.tv_toolbar_title);
        notify_new_Announcemnt = findViewById(R.id.notify_new_announcement);
        progressIndicator = findViewById(R.id.progress_indicator);
        iconLayout = findViewById(R.id.iconLayout);
        editIcon = findViewById(R.id.edit_icon);
        img_announcemenr_icon = findViewById(R.id.img_announcemenr_icon);
        frameLayout = findViewById(R.id.admin_container);
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

    }


    private void setupUI() {
        if (getIntent() != null) {
            batchId = getIntent().getStringExtra(PreferenceHandler.BATCH_ID);
            batchData = getIntent().getStringExtra("batchData");
            batchPosition = getIntent().getStringExtra("batch_position");
            Log.d("TAG", "setupUI: " + batchData);
        }
//        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        /*shimmerFrameLayout.setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmer();*/
        /*try {
            if (getIntent() != null) {
                if (getIntent().getExtras() != null) {
                    Bundle bundle = getIntent().getExtras();
                    batchListResults.clear();
                    batchListResults = (ArrayList<BatchListResult>) bundle.getSerializable("serializableData");
                    Log.d("TAG", "setupUI: " + batchListResults.size());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        PreferenceHandler.writeString(this, PreferenceHandler.VIDEO_CACHING, null);
        userType = PreferenceHandler.readString(this, PreferenceHandler.USER_TYPE, "null");
        setUpNotificationView();
        Utilities.hideKeyBoard(this);
        tabLayout = findViewById(R.id.tab_layout);

        layoutMainView = findViewById(R.id.layout_batch_detail);
        frameLayout = findViewById(R.id.admin_container);
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
        if (!userType.equals("organisation")) {
            editIcon.setVisibility(View.GONE);
        }
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminBatchDetailActivity.this, CreateBatchActivity.class);
                intent.putExtra("from", "edit");
                intent.putExtra("batchData", batchData);
                startActivityForResult(intent, UPDATE_BATCH_REQUEST);
            }
        });
        checkInternet();

    }

    /*@Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentMode = newConfig.uiMode;
        if (AppCompatDelegate.getDefaultNightMode() != currentMode) {
            Log.d("TAG", "onConfigurationChanged: ");
            startActivity(new Intent(AdminBatchDetailActivity.this, AdminBatchDetailActivity.class));
            finish();


        }
    }*/

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
        Log.d("TAG", "hitGetBatchSettingsService: " + batchId);
        new MyClient(AdminBatchDetailActivity.this).getBatchSettingsService(batchId, (content, error) -> {
            GetBatchSettingsResponse response = (GetBatchSettingsResponse) content;
            if (content != null) {
                if (response.getStatus() != null && response.getStatus().equalsIgnoreCase("200")) {
                    if (response.getResult() != null) {
                        applyBatchSettings(response.getResult());
                    } else {
                        GetBatchSettingsResult settings = (GetBatchSettingsResult) getIntent().getSerializableExtra(PreferenceHandler.BATCH_SETTINGS);
                        if (settings != null) {
                            applyBatchSettings(settings);
                        }
                    }
                    if (response.getExtra() != null) {
                        Log.d("TAG", "hitGetBatchSettingsService: " + response.getExtra().getBatchName());
                        toolbarCustomTitle.setText(response.getExtra().getBatchName());

                    }
                } else if (response.getStatus() != null && response.getStatus().equalsIgnoreCase("403")) {
                    Utilities.openUnauthorizedDialog(AdminBatchDetailActivity.this);
                } else Utilities.makeToast(AdminBatchDetailActivity.this, error);
            }
        });

    }
    private void applyBatchSettings(GetBatchSettingsResult batchSettings) {
        tabList = new ArrayList<>();
        map = new LinkedHashMap<>();

        if (batchSettings != null) {
            if (batchSettings.getAnnouncement().equalsIgnoreCase("1"))
                isAnnouncement = batchSettings.getAnnouncement().equalsIgnoreCase("1");
                isAttendenceOn = batchSettings.getAttendance().equalsIgnoreCase("1");
                isAssignmentOn = batchSettings.getManageAssignment().equalsIgnoreCase("1");
                isTestOn = batchSettings.getTest().equalsIgnoreCase("1");
                isVideoOn = batchSettings.getManageVideo().equalsIgnoreCase("1");
                isLiveOn = batchSettings.getLive().equalsIgnoreCase("1");
                isStudyMaterial = batchSettings.getStudy_material().equalsIgnoreCase("1");

            OverviewFragment fragment = OverviewFragment.newInstance(batchId,  "adminrole","batch");
            map.put(getString(R.string.overview), fragment);

            if (batchSettings != null) {
                if (batchSettings.getManageStudent().equals("1"))
                    map.put(getString(R.string.student), AdminStudentFragment.newInstance(batchId, ""));

                if (userType.equals("student")) {
                    if (batchSettings.getAttendance().equals("1"))
                        map.put(getString(R.string.attendance), AdminAttendanceFragment.newInstance(batchId));
                }
                if (batchSettings.getManageAssignment().equals("1"))
                    map.put(getString(R.string.assignments), AdminAssignmentTestVideoFragment.newInstance("assignment", batchId, true, ""));

                  /* if (batchSettings.getTest().equals("1"))
                        map.put(getString(R.string.tests), AdminAssignmentTestVideoFragment.newInstance("test", batchId, true, ""));*/

                if (batchSettings.getManageVideo().equals("1"))
                    map.put(getString(R.string.videos), AdminAssignmentTestVideoFragment.newInstance("video", batchId, true, ""));

                if (batchSettings.getAnnouncement().equals("1"))
                    map.put(getString(R.string.announcement), AdminAnnouncementFragment.newInstance(batchId));

                if (batchSettings.getLive().equals("1"))
                    map.put(getString(R.string.live), AdminLiveFragment.newInstance(batchId));

                if (batchSettings.getStudy_material().equals("1"))
                    map.put(getString(R.string.study_material), AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", ""));

//                map.put(getString(R.string.chat), ChatFragment.newInstance(batchId, "0", "batch", "", true));

            }
//        map.put("Chat", ChatFragment.newInstance(batchId, "0", "batch", ""));
            tabList = new ArrayList<>(map.keySet());
            if (data_progress.getVisibility() == View.VISIBLE) {
                data_progress.setVisibility(GONE);
            }
            setupSettingsUI();

        }
    }

    private void setupSettingsUI() {


      /*  OverviewFragment fragment = OverviewFragment.newInstance(batchId,  "adminrole","batch");
        map.put(getString(R.string.overview), fragment);
        if (batchSettings != null) {
            if (batchSettings.getManageStudent().equals("1"))
                map.put(context.getString(R.string.student), AdminStudentFragment.newInstance(batchId, ""));
            if (batchSettings.getManageAssignment().equals("1"))
                map.put(context.getString(R.string.assignments), AdminAssignmentTestVideoFragment.newInstance("assignment", batchId, true, ""));
            if (batchSettings.getTest().equals("1"))
                map.put(context.getString(R.string.tests), AdminAssignmentTestVideoFragment.newInstance("test", batchId, true, ""));
            if (batchSettings.getManageVideo().equals("1"))
                map.put(context.getString(R.string.videos), AdminAssignmentTestVideoFragment.newInstance("video", batchId, true, ""));
            if (batchSettings.getAnnouncement().equals("1"))
                map.put(context.getString(R.string.announcement), AdminAnnouncementFragment.newInstance(batchId));

      *//*      if (batchSettings.getAttendance().equals("1"))
                map.put("Attendance", AdminAttendanceFragment.newInstance(batchId));*//*

            if (batchSettings.getLive().equals("1"))
                map.put(context.getString(R.string.live), AdminLiveFragment.newInstance(batchId));
            if (batchSettings.getStudy_material().equals("1"))
                map.put(context.getString(R.string.study_material), AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", ""));

            map.put("Chat", ChatFragment.newInstance(batchId, "0", "batch", "", true));

        }


        tabList.add(getString(R.string.overview));
        if (isAttendenceOn) {
            tabList.add(getString(R.string.student));
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
        if (isAnnouncement) {
            tabList.add(getString(R.string.Announcements));
        }
        if (isLiveOn) {
            tabList.add(getString(R.string.live));
        }
        if (isStudyMaterial) {
            tabList.add(getString(R.string.study_material));
        }
        tabList.add("Chat");*/
        tabCount = tabList.size();


        tabCount = tabList.size();
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(new AdminViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setScrollBarFadeDuration(10);
        progressIndicator.setVisibility(View.GONE);
        batchLayout.setVisibility(View.VISIBLE);


        /*try {
            boolean isAdmn = PreferenceHandler.readBoolean(AdminBatchDetailActivity.this, PreferenceHandler.ADMIN_LOGGED_IN, false);
            if (isAdmn) {
                if (AdminBatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                    AdminBatchDetailActivity.shimmerFrameLayout.stopShimmer();
                    AdminBatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                    AdminBatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                    AdminBatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        Log.d("TAG", "setupSettingsUI: " + tabCount);
        /*viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        try {
            AdminViewPagerAdapter adminViewPagerAdapter = new AdminViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            viewPager.setAdapter(adminViewPagerAdapter);
//        viewPager.setAdapter(new AdminViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
            tabLayout.setupWithViewPager(viewPager);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    public class AdminViewPagerAdapter extends FragmentStatePagerAdapter {

        public AdminViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public AdminViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
//             try {
            for (int i = 0; i < tabCount; i++) {
                if (position == 0) {
                    fragment = OverviewFragment.newInstance(batchId, "adminrole", "batch");
                }
                if (position == 1 && position < tabCount) {
                    if (tabList.get(1).equalsIgnoreCase(getString(R.string.student))) {
                        fragment = AdminStudentFragment.newInstance(batchId, "");
                    } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.attendance))) {
                        fragment = AdminAttendanceFragment.newInstance(batchId);
                    } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.assignments))) {
                        fragment = AdminAssignmentTestVideoFragment.newInstance("assignment", batchId, true, "");
                    } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.tests))) {
                        fragment = AdminAssignmentTestVideoFragment.newInstance("test", batchId, true, "");
                    } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.videos))) {
                        fragment = AdminAssignmentTestVideoFragment.newInstance("video", batchId, true, "");
                    } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.announcement))) {
                        fragment = AdminAnnouncementFragment.newInstance(batchId);
                    } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.live))) {
                        fragment = AdminLiveFragment.newInstance(batchId);
                    } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.study_material))) {
                        fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    }  else if (tabList.get(1).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }
                if (position == 2 && position < tabCount) {

                    if (tabList.get(2).equalsIgnoreCase(getString(R.string.attendance))) {
                        fragment = AdminAttendanceFragment.newInstance(batchId);
                    } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.assignments))) {
                        fragment = AdminAssignmentTestVideoFragment.newInstance("assignment", batchId, true, "");
                    } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.tests))) {
                        fragment = AdminAssignmentTestVideoFragment.newInstance("test", batchId, true, "");
                    } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.videos))) {
                        fragment = AdminAssignmentTestVideoFragment.newInstance("video", batchId, true, "");
                    } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.announcement))) {
                        fragment = AdminAnnouncementFragment.newInstance(batchId);
                    } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.live))) {
                        fragment = AdminLiveFragment.newInstance(batchId);
                    } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.study_material))) {
                        fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    }  else if (tabList.get(2).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }
                if (position == 3 && position < tabCount) {

                    if (tabList.get(3).equalsIgnoreCase(getString(R.string.assignments))) {
                        fragment = AdminAssignmentTestVideoFragment.newInstance("assignment", batchId, true, "");
                    } else if (tabList.get(3).equalsIgnoreCase(getString(R.string.tests))) {
                        fragment = AdminAssignmentTestVideoFragment.newInstance("test", batchId, true, "");
                    } else if (tabList.get(3).equalsIgnoreCase(getString(R.string.videos))) {
                        fragment = AdminAssignmentTestVideoFragment.newInstance("video", batchId, true, "");
                    } else if (tabList.get(3).equalsIgnoreCase(getString(R.string.announcement))) {
                        fragment = AdminAnnouncementFragment.newInstance(batchId);
                    } else if (tabList.get(3).equalsIgnoreCase(getString(R.string.live))) {
                        fragment = AdminLiveFragment.newInstance(batchId);
                    } else if (tabList.get(3).equalsIgnoreCase(getString(R.string.study_material))) {
                        fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    }  else if (tabList.get(3).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }
                if (position == 4 && position < tabCount) {
                    if (tabList.get(4).equalsIgnoreCase(getString(R.string.tests))) {
                        fragment = AdminAssignmentTestVideoFragment.newInstance("test", batchId, true, "");
                    } else if (tabList.get(4).equalsIgnoreCase(getString(R.string.videos))) {
                        fragment = AdminAssignmentTestVideoFragment.newInstance("video", batchId, true, "");
                    } else if (tabList.get(4).equalsIgnoreCase(getString(R.string.announcement))) {
                        fragment = AdminAnnouncementFragment.newInstance(batchId);
                    } else if (tabList.get(4).equalsIgnoreCase(getString(R.string.live))) {
                        fragment = AdminLiveFragment.newInstance(batchId);
                    } else if (tabList.get(4).equalsIgnoreCase(getString(R.string.study_material))) {
                        fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    }  else if (tabList.get(4).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }
                if (position == 5 && position < tabCount) {
                    if (tabList.get(5).equalsIgnoreCase(getString(R.string.videos))) {
                        fragment = AdminAssignmentTestVideoFragment.newInstance("video", batchId, true, "");
                    } else if (tabList.get(5).equalsIgnoreCase(getString(R.string.announcement))) {
                        fragment = AdminAnnouncementFragment.newInstance(batchId);
                    } else if (tabList.get(5).equalsIgnoreCase(getString(R.string.live))) {
                        fragment = AdminLiveFragment.newInstance(batchId);
                    } else if (tabList.get(5).equalsIgnoreCase(getString(R.string.study_material))) {
                        fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    }  else if (tabList.get(5).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }
                if (position == 6 && position < tabCount) {
                    if (tabList.get(6).equalsIgnoreCase(getString(R.string.announcement))) {
                        fragment = AdminAnnouncementFragment.newInstance(batchId);
                    } else if (tabList.get(6).equalsIgnoreCase(getString(R.string.live))) {
                        fragment = AdminLiveFragment.newInstance(batchId);
                    } else if (tabList.get(6).equalsIgnoreCase(getString(R.string.study_material))) {
                        fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    }  else if (tabList.get(6).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }
                if (position == 7 && position < tabCount) {
                    if (tabList.get(7).equalsIgnoreCase(getString(R.string.live))) {
                        fragment = AdminLiveFragment.newInstance(batchId);
                    } else if (tabList.get(7).equalsIgnoreCase(getString(R.string.study_material))) {
                        fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    }  else if (tabList.get(7).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }
                if (position == 8 && position < tabCount) {
                    if (tabList.get(8).equalsIgnoreCase(getString(R.string.study_material))) {
                        fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    }  else if (tabList.get(8).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }

                if (position == 9 && position < tabCount) {
                    if (tabList.get(9).equalsIgnoreCase(getString(R.string.chat))) {
                        fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    }
                }
            }
            /*switch(position) {
                case 0:
                    fragment = OverviewFragment.newInstance(batchId, "adminrole", "batch");
                    break;
                case 1:
                    fragment = AdminStudentFragment.newInstance(batchId, "");
                    break;
                case 2:
                    fragment = AdminAttendanceFragment.newInstance(batchId);
                    break;
                case 3:
                    fragment = AdminAssignmentTestVideoFragment.newInstance("assignment", batchId, true, "");
                    break;
                case 4:
                    fragment = AdminAssignmentTestVideoFragment.newInstance("test", batchId, true, "");
                    break;
                case 5:
                    fragment = AdminAssignmentTestVideoFragment.newInstance("video", batchId, true, "");OverviewFragment.newInstance(batchId, "adminrole", "batch");
                    break;
                case 6:
                    fragment = AdminAnnouncementFragment.newInstance(batchId);
                    break;
                case 7:
                    fragment = AdminLiveFragment.newInstance(batchId);
                    break;
                case 8:
                    fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                    break;
                case 9:
                    fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                    break;

            }
*/
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
                    Log.d("ClickBottom", "onNavigationItemSelected: ");
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.admin_container);
                    switch (item.getItemId()) {
                        case R.id.batch_btn:
                            startActivity(new Intent(AdminBatchDetailActivity.this, AdminMainScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("isFrom", "adminhome"));
                            finish();
                            return true;
                        case R.id.account_btn:
                            startActivity(new Intent(AdminBatchDetailActivity.this, AdminMainScreen.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).putExtra("isFrom", "adminaccount"));
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
        transaction.replace(R.id.admin_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        //finish();

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.admin_container);
        if (f instanceof AdminBatchUiFragment || f instanceof AdminProfileFragment) {
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
        } else {
            finish();
        }


    }

    private void setUpNotificationView() {
        if (notificationDataList != null && notificationDataList.size() > 0)
            for (int i = 0; i < notificationDataList.size(); i++) {
                if (notificationDataList.get(i).getBatchId().equalsIgnoreCase(batchId) &&
                        notificationDataList.get(i).getNotificationType().equalsIgnoreCase("announcement")) {
//                    imgAnnouncementIcon.setImageResource(R.drawable.ic_bell_notify);
                }
            }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean dataaa = data != null;
        Log.e("TAG", "onActivityResult: in Batch $resultCode " + resultCode + " : "+ requestCode);
        if (requestCode == UPDATE_BATCH_REQUEST) {
            if (data != null) {
                boolean isDataSubmitted = data.getBooleanExtra(PreferenceHandler.IS_DATA_SUBMITTED, false);
                if (isDataSubmitted) {
                    try {
                        AdminBatchUiFragment.batchListResults.get(Integer.parseInt(batchPosition)).setBatchName(data.getStringExtra("batchUpdatedName"));
                        AdminBatchUiFragment.batchListResults.get(Integer.parseInt(batchPosition)).setCourseName(data.getStringExtra("batchUpdatedCourse"));
                        if (data.getStringArrayListExtra("batchUpdatedDays") != null) {
                            if (data.getStringArrayListExtra("batchUpdatedDays").size() > 0) {
                                JSONObject jArrayDayData = null;
                                JSONObject  jsonObject = new JSONObject();
                                for (int i = 0; i < data.getStringArrayListExtra("batchUpdatedDays").size(); i++) {
                                    jArrayDayData = new JSONObject();
                                    String[] splitData = data.getStringArrayListExtra("batchUpdatedDays").get(i).split("TimeSplit");
                                    String capitalIze = capitalizeString(splitData[0].replaceAll("\"", ""));
                                    jArrayDayData.put("day", capitalIze);
                                    jArrayDayData.put("startTime", splitData[1]);
                                    jArrayDayData.put("endTime", splitData[2]);

                                    jsonObject.put(capitalIze, jArrayDayData);
                                }
                                AdminBatchUiFragment.batchListResults.get(Integer.parseInt(batchPosition)).setDays_time(jsonObject.toString());
                            }
                        }
                        AdminBatchUiFragment.batchListAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("TAG", "hitGetBatchSettingsService:  updating error line 709");
                    }

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        }
    }
    public static String capitalizeString(String str) {
        String retStr = str;
        try { // We can face index out of bound exception if the string is null
            retStr = str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
        }catch (Exception e){}
        return retStr;
    }
}

