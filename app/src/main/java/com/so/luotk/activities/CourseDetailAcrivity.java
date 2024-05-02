package com.so.luotk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.so.luotk.R;
import com.so.luotk.client.MyClient;
import com.so.luotk.databinding.ActivityCourseDetailAcrivityBinding;
import com.so.luotk.fragments.batches.AssignmentTestVideoListFragment;
import com.so.luotk.fragments.batches.LiveFragment;
import com.so.luotk.fragments.batches.StudyMaterialFragment;
import com.so.luotk.fragments.courses.CourseOverviewFragment;
import com.so.luotk.models.newmodels.courseModel.Datum;
import com.so.luotk.models.output.GetBatchSettingsResult;
import com.so.luotk.models.output.GetCourseSettingsResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.so.luotk.utils.ZoomOutPageTransformer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class CourseDetailAcrivity extends AppCompatActivity implements View.OnClickListener, CourseOverviewFragment.likeChangeListener {
    private ActivityCourseDetailAcrivityBinding binding;
    private Handler handler;
    private Runnable runnable;
    private boolean isFirstInternetToastDone, isFromMyCourses, isLikeChanged;
    private String courseId, url, notificationType, sellingPrice = "";
    private List<String> tabList;
    private int tabCount = 0, currentPosition;
    private LinkedHashMap<String, Fragment> map;
    private boolean flag, isNotify;
    private Datum course;
    public static String courseNameNew;
    public static ShimmerFrameLayout shimmerFrameLayout;
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TAGCourse", "onStart: ");
        if (!PreferenceHandler.readBoolean(this, PreferenceHandler.LOGGED_IN, false)) {
            startActivity(new Intent(this, WelcomeActivityNew.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
// getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        tabList = new ArrayList<>();
        map = new LinkedHashMap<>();
        binding = ActivityCourseDetailAcrivityBinding.inflate(getLayoutInflater());
        Utilities.setUpStatusBar(this); Utilities.setLocale(this);
        setContentView(binding.getRoot());

        setupToolbar();
        setupUI();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColor(this));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(binding.toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        this.getSupportActionBar().setHomeAsUpIndicator(upArrow);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        setResultData();
        finish();
    }

    private void setResultData() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("clicked", isLikeChanged);
        //  resultIntent.putExtra("isFromMyCourses", isFromMyCourses);
        if (isNotify && isFromMyCourses) {
            // resultIntent.putExtra("isFromMyCourses", isFromMyCourses);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(PreferenceHandler.IS_PAYMENT_DONE, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else
            setResult(1, resultIntent);
    }

    private void setupUI() {
        if (getIntent() != null) {
            isNotify = getIntent().getBooleanExtra("notify", false);
            if (!isNotify) {
                course = (Datum) getIntent().getSerializableExtra("course");
                courseId = course.getId() + "";
                sellingPrice = course.getSellingPrice();
                isFromMyCourses = getIntent().getBooleanExtra("isFromMyCourses", false);
            } else {
                courseId = getIntent().getStringExtra(PreferenceHandler.COURSE_ID);
                notificationType = getIntent().getStringExtra(PreferenceHandler.NOTIFICATION_TYPE);
                isFromMyCourses = getIntent().getBooleanExtra("isFromMyCourses", false);
                sellingPrice = getIntent().getStringExtra(PreferenceHandler.SELLING_PRICE);
            }
            url = getIntent().getStringExtra("base");

        }
        setupSettingsUI();
        handler = new Handler(Looper.myLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        checkInternet();
        binding.imgAnnouncemenrIcon.setOnClickListener(v -> {
            startActivity(new Intent(this, AnnouncementActivity.class).putExtra(PreferenceHandler.IS_FROM, "course")
                    .putExtra("courseId", courseId));
        });

    }


    private void checkInternet() {
        if (Utilities.checkInternet(this)) {
            handler.removeCallbacks(runnable);
            hitGetCourseSettingsService();
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    private void hitGetCourseSettingsService() {
        new MyClient(this).getCourseSettingsService(courseId, (content, error) -> {
            if (content != null) {
                GetCourseSettingsResponse response = (GetCourseSettingsResponse) content;
                if (response != null) {
                    if (response.getStatus() == 200 && response.getSuccess()) {
                        applyCourseSettings(response.getResult());
                        binding.toolbarTitle.setText(response.getExtra().getCourseName());
                        courseNameNew = response.getExtra().getCourseName();
                        Log.d("TAG", "hitGetCourseSettingsService: " + response.getExtra().getCourseId() +" : "+ courseNameNew);
                    } else if (response.getStatus() == 403)
                        Utilities.openUnauthorizedDialog(this);
                    else
                        Utilities.makeToast(this, getString(R.string.server_error));
                } else
                    Utilities.makeToast(this, getString(R.string.server_error));
            }
//            binding.progressIndicator.setVisibility(View.GONE);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private void applyCourseSettings(GetBatchSettingsResult courseSettings) {
        CourseOverviewFragment fragment = CourseOverviewFragment.newInstance(course, courseId, isFromMyCourses, isNotify, url);
        fragment.setChangeListener(this);
        map.put(getString(R.string.overview), fragment);

        if (courseSettings != null) {
            if (courseSettings.getTest().equals("1"))
                map.put(getString(R.string.tests), AssignmentTestVideoListFragment.newInstance("test", courseId, false, sellingPrice));
        }

        if (courseSettings.getManageVideo().equals("1"))
            map.put(getString(R.string.videos), AssignmentTestVideoListFragment.newInstance("video", courseId, false, sellingPrice));
        if (courseSettings != null) {
            if (courseSettings.getAnnouncement().equals("1"))
                binding.imgAnnouncemenrIcon.setVisibility(View.VISIBLE);
            else binding.imgAnnouncemenrIcon.setVisibility(View.GONE);
            if (courseSettings.getLive().equals("1"))
                map.put(getString(R.string.live), LiveFragment.newInstance(courseId, "course"));
            if (courseSettings.getStudy_material().equals("1"))
                map.put(getString(R.string.study_material), StudyMaterialFragment.newInstance(courseId, "0", "course", sellingPrice));

        }

        tabList = new ArrayList<>(map.keySet());
        setupSettingsUI();
    }


    private void setupSettingsUI() {
        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        tabCount = tabList.size();
        binding.pager.setPageTransformer(true, new ZoomOutPageTransformer());
        binding.pager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        binding.tabLayout.setupWithViewPager(binding.pager);
        if (notificationType != null && notificationType.equalsIgnoreCase("Live Start of Course")) {
            for (int i = 0; i < tabList.size(); i++) {
                if (tabList.get(i).equalsIgnoreCase(getString(R.string.live))) {
                    binding.tabLayout.getTabAt(i).select();
                }
            }
        }
        if (notificationType != null && notificationType.equalsIgnoreCase("buynow")) {
            for (int i = 0; i < tabList.size(); i++) {
                if (tabList.get(i).equalsIgnoreCase(getString(R.string.overview))) {
                    binding.tabLayout.getTabAt(i).select();
                }
            }
        }

    }

    @Override
    public void onClick(boolean flag) {
        Log.e("TAG", "onClick: " + flag);
        isLikeChanged = flag;
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return Objects.requireNonNull(map.get(tabList.get(position)));
        }

        @Override
        public int getCount() {
            return tabCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            return tabList.get(position);
        /*    for (int i = 0; i < tabList.size(); i++) {
                if (position == i && position < tabCount) {
                    title = (tabList.get(i));
                }
            }
            return title;*/
        }

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        startActivity(getIntent());
        finish();
    }

}