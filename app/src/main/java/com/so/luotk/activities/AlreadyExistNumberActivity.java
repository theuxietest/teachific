package com.so.luotk.activities;

import com.so.luotk.R;
import com.so.luotk.utils.Utilities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;


public class AlreadyExistNumberActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private String mobileNumber;
    private TextView tvExistPhoneNumber, btnLogin, tvSignupLink, toolbarTitle;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
//      getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(R.layout.activity_already_exist_number);
        setToolbar();
        setupUI();
    }

    private void setToolbar() {
        //toolbar setup
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        //  toolbarTitle.setText("Enter OTP");
        // toolbar.setTitle("Login");
        if (getIntent() != null) {
            mobileNumber = getIntent().getStringExtra("mobileNumber");
        }
        Typeface face = Typeface.createFromAsset(getAssets(), "font/ProductSansRegular.ttf");
        toolbarTitle.setTypeface(face);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.gray), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(upArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void setupUI() {
        tvExistPhoneNumber = findViewById(R.id.tv_exist_phone_number);
        if (mobileNumber != null) {
            tvExistPhoneNumber.setText("+91 " + mobileNumber);
        }
        btnLogin = findViewById(R.id.btn_login);
        tvSignupLink = findViewById(R.id.link_signup);
        btnLogin.setOnClickListener(this);
        tvSignupLink.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (v.getId()) {
            case R.id.btn_login:
                Intent loginIntent = new Intent(getApplicationContext(), WelcomeActivityNew.class);
                loginIntent.putExtra("mobileNumber", mobileNumber);
                startActivity(loginIntent);
                break;

            case R.id.link_signup:
                Intent signupIntent = new Intent(getApplicationContext(), SignUpActivityNew.class);
                startActivity(signupIntent);
                break;
            default:
                break;
        }

    }
}
