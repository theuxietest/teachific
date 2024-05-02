package com.so.luotk.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.so.luotk.R;
import com.so.luotk.fragments.SendJoinRequestFragment;
import com.so.luotk.fragments.batches.BatchFragment;
import com.so.luotk.fragments.HomeFragment;
import com.so.luotk.fragments.courses.StoreFragment;
import com.so.luotk.fragments.feestructure.FeeStructureFragment;
import com.so.luotk.fragments.more.AnnoucementsFragment;
import com.so.luotk.fragments.more.MoreOptionFragment;

import com.so.luotk.fragments.more.SetLanguageFragment;
import com.so.luotk.fragments.more.SettingFragment;
import com.so.luotk.fragments.reports.ReportAssignmentTestFragment;
import com.so.luotk.fragments.reports.ReportFragment;
import com.so.luotk.fragments.reports.ReportsTabbedFragment;
import com.so.luotk.utils.PreferenceHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.so.luotk.utils.Utilities;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getName();
    private BottomNavigationView bottomNavigation;
    private Fragment fragment;
    private int existClick = 0;
    private RelativeLayout layoutMainView;
    private Fragment activeFragment, homeFragment, batchFragment, reportFragment, feesFragment, moreOptionFragment;
    private boolean isHomeFragmentLoaded;
//    DatabaseReference userRef;
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
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        overridePendingTransition(0, 0);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(R.layout.materialised_main);
        Log.d(TAG, "onCreate: Main");
        PreferenceHandler.writeString(this, PreferenceHandler.HOMEFRAGMENT_CACHE, null);
        PreferenceHandler.saveFeaturedCoursetList(this, PreferenceHandler.FEATURED_COURSE, null);
        PreferenceHandler.writeString(this, PreferenceHandler.VIDEO_CACHING, null);
        PreferenceHandler.saveMyCoursetList(this, PreferenceHandler.MY_COURSE, null);
        PreferenceHandler.saveBatchList(this, PreferenceHandler.BATCH_LIST, null);
        PreferenceHandler.writeString(this, PreferenceHandler.TOTAL_ASSIGNMENT_REPORT, "");
        PreferenceHandler.writeString(this, PreferenceHandler.TOTAL_TEST_REPORT, "");
        PreferenceHandler.writeBoolean(this, PreferenceHandler.IS_COURSE_LOADED, false);
        PreferenceHandler.writeBoolean(MainActivity.this, PreferenceHandler.START_JITSI_ROOM_ACTION, false);
        PreferenceHandler.writeBoolean(MainActivity.this, PreferenceHandler.END_JITSI_ROOM_ACTION, false);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        layoutMainView = findViewById(R.id.main_layout);
        bottomNavigation.setItemIconTintList(null);
        if (PreferenceHandler.readBoolean(this, PreferenceHandler.ISSTATICLOGIN, false)) {
            bottomNavigation.inflateMenu(R.menu.static_menu_bottom);
        } else {
            if (PreferenceHandler.readString(this, PreferenceHandler.IS_LITE_APP, "0").equalsIgnoreCase("1")) {
                bottomNavigation.inflateMenu(R.menu.lite_bottom_menu);
            } else {
                bottomNavigation.inflateMenu(R.menu.bottom_navigation_menu);
            }
        }
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        if (getIntent() != null) {
            boolean isFromJoinRequest = getIntent().getBooleanExtra(PreferenceHandler.IS_FROM_SEND_JOIN_REQUEST, false);
            boolean isFromBatchNotification = getIntent().getBooleanExtra(PreferenceHandler.IS_FROM_BATCH_NOTIFICATION, false);
            boolean isFromReportNotification = getIntent().getBooleanExtra(PreferenceHandler.IS_FROM_REPORT_NOTIFICATION, false);
            boolean isCourseAdded = getIntent().getBooleanExtra(PreferenceHandler.IS_COURSE_ADDED, false);
            boolean isPaymentDone = getIntent().getBooleanExtra(PreferenceHandler.IS_PAYMENT_DONE, false);
            boolean isVisitLater = getIntent().getBooleanExtra(PreferenceHandler.IS_VISIT_LATER, false);
            String isfrom = getIntent().getStringExtra("isFrom");
            if (isFromJoinRequest) {
                bottomNavigation.setSelectedItemId(R.id.bottomNavigationBatchesMenuId);
                openFragment(new BatchFragment());
            } else if (isFromBatchNotification) {
                bottomNavigation.setSelectedItemId(R.id.bottomNavigationBatchesMenuId);
                openFragment(new BatchFragment(true));
            } else if (isFromReportNotification) {
                bottomNavigation.setSelectedItemId(R.id.bottomNavigationReportMenuId);
                openFragment(ReportsTabbedFragment.newInstance("batchtest", true));
            } else if (isCourseAdded) {
                bottomNavigation.setSelectedItemId(R.id.bottomNavigationFeeMenuId);
                openFragment(StoreFragment.newInstance(false));
            } else if (isPaymentDone) {
                bottomNavigation.setSelectedItemId(R.id.bottomNavigationFeeMenuId);
                openFragment(StoreFragment.newInstance(true));
            } else if (isVisitLater) {
                bottomNavigation.setSelectedItemId(R.id.bottomNavigationFeeMenuId);
                openFragment(StoreFragment.newInstance(false));
            } else if (isfrom != null && isfrom.equalsIgnoreCase("batch")) {
                bottomNavigation.setSelectedItemId(R.id.bottomNavigationBatchesMenuId);

            } else if (isfrom != null && isfrom.equalsIgnoreCase("report")) {
                bottomNavigation.setSelectedItemId(R.id.bottomNavigationReportMenuId);

            } else if (isfrom != null && isfrom.equalsIgnoreCase("course")) {
                bottomNavigation.setSelectedItemId(R.id.bottomNavigationFeeMenuId);

            } else if (isfrom != null && isfrom.equalsIgnoreCase("more")) {
                bottomNavigation.setSelectedItemId(R.id.bottomNavigationMoreOptionId);

            } else if (isfrom != null && isfrom.equalsIgnoreCase("home")) {
                bottomNavigation.setSelectedItemId(R.id.bottomNavigationHomeMenuId);
            } else if (getIntent().getStringExtra("courseId") != null && getIntent().getStringExtra("courseName") != null) {
                if (getIntent().getStringExtra("courseId").length() > 0) {
                    Log.d(TAG, "onCreateCourese: " + getIntent().getStringExtra("courseId"));
                    if (getIntent().getStringExtra("fromLink").equalsIgnoreCase("result")) {
                        openFragment(new HomeFragment());
                        bottomNavigation.setSelectedItemId(R.id.bottomNavigationHomeMenuId);
                    } else {
                        openFragment(StoreFragment.newInstance(false, true, getIntent().getStringExtra("courseId"), getIntent().getStringExtra("courseData")));
                        bottomNavigation.setSelectedItemId(R.id.bottomNavigationFeeMenuId);
                    }
                } else {
                    openFragment(new HomeFragment());
                    bottomNavigation.setSelectedItemId(R.id.bottomNavigationHomeMenuId);
                }
            } else {
                openFragment(new HomeFragment());
                bottomNavigation.setSelectedItemId(R.id.bottomNavigationHomeMenuId);
            }
        } else {
            openFragment(new HomeFragment());
            bottomNavigation.setSelectedItemId(R.id.bottomNavigationHomeMenuId);

        }
      /*  userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        signInWithFirebasePhone();*/

        /*if (getIntent().getStringExtra("courseId") != null && getIntent().getStringExtra("courseName") != null) {

            if (getIntent().getStringExtra("courseId").length() > 0) {
                Log.d(TAG, "onCreateCourese: " + getIntent().getStringExtra("courseId"));
                Intent intent = new Intent(MainActivity.this, CourseDetailAcrivity.class);
                intent.putExtra("courseId", getIntent().getStringExtra("courseId"));
                intent.putExtra("fromLink", true);
                startActivityForResult(intent, 1);
            }
            *//*openFragment(new StoreFragment());
            bottomNavigation.setSelectedItemId(R.id.bottomNavigationFeeMenuId);*//*
        }*/
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentMode = newConfig.uiMode;
        if (AppCompatDelegate.getDefaultNightMode() != currentMode) {

            startActivity(new Intent(MainActivity.this, MainActivity.class));
            finish();


        }
    }

    @Override
    public void onBackPressed() {
        boolean homefragment = true;
        Handler exit_handler = new Handler(Looper.myLooper());
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);

        if (f instanceof SettingFragment) {
            ((SettingFragment) f).onBackPressed();
        } else if (f instanceof AnnoucementsFragment) {
            ((AnnoucementsFragment) f).onBackPressed();
        } else if (f instanceof SetLanguageFragment) {
            ((SetLanguageFragment) f).onBackPressed();
        } else if (f instanceof SendJoinRequestFragment) {
            ((SendJoinRequestFragment) f).onBackPressed();
        } else if (f instanceof FeeStructureFragment) {
            ((FeeStructureFragment) f).onBackPressed();
        } else if (f instanceof ReportsTabbedFragment) {
            ((ReportsTabbedFragment) f).onBackPressed();
        } else if (f instanceof ReportAssignmentTestFragment) {
            ((ReportAssignmentTestFragment) f).onBackPressed();
        } else {
            if (existClick == 0) {

                /*boolean reportCheck = f instanceof ReportFragment;
                boolean batchCheck = f instanceof BatchFragment;
                boolean storeCheck = f instanceof StoreFragment;
                boolean moreCheck = f instanceof MoreOptionFragment;
                if (reportCheck) {
                    homefragment = false;
                    bottomNavigation.setSelectedItemId(R.id.bottomNavigationHomeMenuId);
                    openFragment(new HomeFragment());
                } else if (batchCheck) {
                    homefragment = false;
                    bottomNavigation.setSelectedItemId(R.id.bottomNavigationHomeMenuId);
                    openFragment(new HomeFragment());
                } else if (storeCheck) {
                    homefragment = false;
                    bottomNavigation.setSelectedItemId(R.id.bottomNavigationHomeMenuId);
                    openFragment(new HomeFragment());
                } else if (moreCheck) {
                    homefragment = false;
                    bottomNavigation.setSelectedItemId(R.id.bottomNavigationHomeMenuId);
                    openFragment(new HomeFragment());
                }*/
                if (homefragment) {
                    // Snackbar.make(layoutMainView, "Press again to exit", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();
                    existClick++;
                    exit_handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            existClick = 0;
                        }
                    }, 5000);
                }
            } else {

                Utilities.hideKeyBoard(this);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        existClick = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Log.d("ClickBottom", "onNavigationItemSelected: ");
                    Utilities.hideKeyBoard(MainActivity.this);
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
                    switch (item.getItemId()) {
                        case R.id.bottomNavigationHomeMenuId:
                            if (!(f instanceof HomeFragment))
                                openFragment(new HomeFragment());

                            return true;
                        case R.id.bottomNavigationBatchesMenuId:
                            if (!(f instanceof BatchFragment))
                                openFragment(new BatchFragment());
                            return true;
                        case R.id.bottomNavigationReportMenuId:
                            if (!(f instanceof ReportFragment))
                                openFragment(new ReportFragment());
                            return true;
                        case R.id.bottomNavigationFeeMenuId:
                            // openFragment(new FeeStructureFragment());
                            if (!(f instanceof StoreFragment))
                                openFragment(StoreFragment.newInstance(false));
                            return true;
                        case R.id.bottomNavigationMoreOptionId:
                            if (!(f instanceof MoreOptionFragment))
                                openFragment(new MoreOptionFragment());
                            return true;
                    }
                    return false;
                }
            };


    public void openFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }


    public void setFragment(Fragment newfragment, Fragment activeFragment) {
        getSupportFragmentManager().beginTransaction().show(newfragment).hide(activeFragment).commit();
    }

    public void setHomeFragmentLoaded(boolean loaded) {

        this.isHomeFragmentLoaded = loaded;
    }
    public void goToCourse() {
        Log.d(TAG, "goToCourse: Call This");
        bottomNavigation.setSelectedItemId(R.id.bottomNavigationFeeMenuId);
    }
    /*public void signInWithFirebasePhone() {

        try {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("username", PreferenceHandler.readString(MainActivity.this, PreferenceHandler.USER_NAME, null));
            hashMap.put("user_id", PreferenceHandler.readString(MainActivity.this, PreferenceHandler.USERID, null));
            hashMap.put("mobile", PreferenceHandler.readString(MainActivity.this, PreferenceHandler.USER_MOBILE, null));
            userRef.child(PreferenceHandler.readString(MainActivity.this, PreferenceHandler.USERID, null)).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if (task.isSuccessful()) {
                    } else {
                        Log.d(TAG, "signInWithFirebasePhone: " + task.getException().getMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
