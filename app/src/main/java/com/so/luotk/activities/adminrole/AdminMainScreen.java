package com.so.luotk.activities.adminrole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.so.luotk.R;
import com.so.luotk.activities.WelcomeActivityNew;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.ActivityAdminMainScreenBinding;
import com.so.luotk.fragments.adminrole.AdminBatchUiFragment;
import com.so.luotk.fragments.adminrole.AdminProfileFragment;
import com.so.luotk.fragments.adminrole.adminbatches.AdminBatchDetailsFragment;
import com.so.luotk.models.output.LogoutResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

public class AdminMainScreen extends AppCompatActivity {
    public static final String TAG = AdminMainScreen.class.getName();
    private String orgName, userType;
    ActivityAdminMainScreenBinding binding;
    private ProgressView mProgressDialog;
    private int existClick = 0;
    private final Handler exit_handler = new Handler(Looper.myLooper());
    private BottomNavigationView bottomNavigation;
//    DatabaseReference userRef;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivityAdminMainScreenBinding.inflate(getLayoutInflater());
        overridePendingTransition(0, 0);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(binding.getRoot());
        Log.d(TAG, "onCreate: MainScreen");
        setSupportActionBar(binding.toolbar);

        PreferenceHandler.writeString(this, PreferenceHandler.VIDEO_CACHING, null);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setItemIconTintList(null);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        orgName = PreferenceHandler.readString(AdminMainScreen.this, PreferenceHandler.ORG_NAME, null);
        userType = PreferenceHandler.readString(AdminMainScreen.this, PreferenceHandler.USER_TYPE, null);

        Log.d(TAG, "onCreate: " + userType);
        if (getIntent() != null) {
            String isfrom = getIntent().getStringExtra("isFrom");
            Log.d(TAG, "onCreateIsFrom: " + isfrom);
            if (isfrom != null && isfrom.equalsIgnoreCase("adminhome")) {
                bottomNavigation.setSelectedItemId(R.id.batch_btn);
            } else if (isfrom != null && isfrom.equalsIgnoreCase("adminaccount")) {
                bottomNavigation.setSelectedItemId(R.id.account_btn);
            } else {
                Log.d(TAG, "onCreate: EnterIf Run");
            /*    View view = bottomNavigation.findViewById(R.id.batch_btn);
                view.performClick();*/
               /* MenuItem item = bottomNavigation.getMenu().findItem(R.id.batch_btn);
                item.setChecked(true);*/
                openFragment(new AdminBatchUiFragment());
                bottomNavigation.setSelectedItemId(R.id.batch_btn);
               /* bottomNavigation.
                bottomNavigation.setSelectedItemId(R.id.batch_btn);
                bottomNavigation.setSelectedItemId(R.id.batch_btn);
                bottomNavigation.setSelectedItemId(R.id.batch_btn);
                bottomNavigation.setSelectedItemId(R.id.batch_btn);
                bottomNavigation.setSelectedItemId(R.id.batch_btn);*/
            }

        } else {
            Log.d(TAG, "onCreate: Enterelse");
            openFragment(new AdminBatchUiFragment());
            bottomNavigation.setSelectedItemId(R.id.batch_btn);

        }

//        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

//        openFragment(new AdminBatchFragment());
     /*   openFragment(new AdminBatchUiFragment());
        bottomNavigation.setSelectedItemId(R.id.bottomNavigationHomeMenuId);*/

//        getSupportFragmentManager().beginTransaction().replace(R.id.admin_container, new AdminBatchFragment()).commit();
        if (orgName != null)
            binding.toolbarTitle.setText(orgName);
    /*    binding.tvLogout.setOnClickListener(v -> {
            showLogoutAlert();
        });*/
//        signInWithFirebasePhone();
        if (PreferenceHandler.readString(AdminMainScreen.this, PreferenceHandler.ISACTIVEORG, "0").equals("0")) {
            Utilities.openUnauthorizedDialog(AdminMainScreen.this);
        }
    }

    public void hideToolbar() {
        binding.toolbar.setVisibility(View.GONE);
    }

    public void showToolbar() {
        binding.toolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
         /*   case R.id.menu_set_lanuage:
                startActivity(new Intent(this, SetLanguageActivity.class));
                return true;*/
            case R.id.menu_logout:
                showLogoutAlert();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentMode = newConfig.uiMode;
        if (AppCompatDelegate.getDefaultNightMode() != currentMode) {

            startActivity(new Intent(AdminMainScreen.this, AdminMainScreen.class));
            finish();


        }
    }

    public void showLogoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(orgName);
        builder.setMessage(getString(R.string.sure_you_want_to_logout));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.log_out), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mProgressDialog = new ProgressView(AdminMainScreen.this);
                mProgressDialog.show();
                hitLogoutService();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isFinishing())
                    dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        if (!isFinishing())
            dialog.show();
        Button pButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pButton.setTextColor(ContextCompat.getColor(this, R.color.blue_main));
        Button nButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nButton.setTextColor(ContextCompat.getColor(this, R.color.blue_main));
    }

    private void hitLogoutService() {
        new MyClient(this).hitLogout((content, error) -> {
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
            if (content != null) {
                LogoutResponse response = (LogoutResponse) content;
                if (response.getStatus() != null && (response.getStatus().equalsIgnoreCase("200") || response.getStatus().equalsIgnoreCase("403"))) {
                    PreferenceHandler.writeBoolean(AdminMainScreen.this, PreferenceHandler.ADMIN_LOGGED_IN, false);
                    Intent intent = new Intent(AdminMainScreen.this, WelcomeActivityNew.class);
                    intent.putExtra("isFromLogout", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else Utilities.makeToast(this, getString(R.string.server_error));
            } else Utilities.makeToast(this, getString(R.string.server_error));
        });
    }

    @Override
    public void onBackPressed() {

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.admin_container);
        if (!(f instanceof AdminBatchDetailsFragment)) {
            if (existClick == 0) {
                // Snackbar.make(layoutMainView, "Press again to exit", Snackbar.LENGTH_LONG).show();
                Toast.makeText(this, getString(R.string.press_again_to_exist), Toast.LENGTH_SHORT).show();
                existClick++;
                exit_handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        existClick = 0;
                    }
                }, 5000);
            } else {

                Utilities.hideKeyBoard(this);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Log.d("ClickBottom", "onNavigationItemSelected: ");
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.admin_container);
                    switch (item.getItemId()) {
                        case R.id.batch_btn:
                            if (!(f instanceof AdminBatchUiFragment))
                                openFragment(new AdminBatchUiFragment());
                            return true;
                        case R.id.account_btn:
                            if (!(f instanceof AdminProfileFragment))
                                openFragment(new AdminProfileFragment());
                            return true;
                    }
                    return false;
                }
            };
    public void openFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
            transaction.replace(R.id.admin_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }


    /*public void signInWithFirebasePhone() {

        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("username", PreferenceHandler.readString(AdminMainScreen.this, PreferenceHandler.ORG_NAME, null));
        hashMap.put("user_id", PreferenceHandler.readString(AdminMainScreen.this, PreferenceHandler.USERID, null));
        hashMap.put("mobile", PreferenceHandler.readString(AdminMainScreen.this, PreferenceHandler.ORG_PHONE_NO, null));
        userRef.child(PreferenceHandler.readString(AdminMainScreen.this, PreferenceHandler.USERID, null)).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(AdminMainScreen.this, "Successfull", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "signInWithFirebasePhone: " + task.getException().getMessage());
//                    Toast.makeText(AdminMainScreen.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/

}