package com.so.luotk.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.so.luotk.R;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

public class FeeStructureActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout layoutPayNow, layoutNoFeeStructure, layoutFeeStructurePhoto;
    private LinearLayout layoutFeeStructure;
    private boolean isBatchCreated;
    private String email;
    private ImageView imageViewFeeStructure;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(R.layout.activity_fee_structure);
        setToolbar();
        setupUI();
        getWindow().setStatusBarColor(ContextCompat.getColor(FeeStructureActivity.this,R.color.white));
    }

    private void setToolbar() {
        //toolbar setup
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(FeeStructureActivity.this));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    private void setupUI() {
        layoutNoFeeStructure = findViewById(R.id.layout_no_fee_structure);
        layoutFeeStructure = findViewById(R.id.layout_fee_structure);
        layoutFeeStructurePhoto = findViewById(R.id.layout_fee_structure_image);
        layoutPayNow = findViewById(R.id.layout_pay_now);
        imageViewFeeStructure = findViewById(R.id.img_fee_structure);
        layoutPayNow.setOnClickListener(this);
        layoutPayNow.setVisibility(View.GONE);
        isBatchCreated = PreferenceHandler.readBoolean(FeeStructureActivity.this, PreferenceHandler.CREATED_BATCH, false);
        String photoUrl = PreferenceHandler.readString(FeeStructureActivity.this, PreferenceHandler.FEE_STRUCTURE_PHOTO, null);
        Log.d("TAG", "setupUI: " + photoUrl);
        layoutNoFeeStructure.setVisibility(View.GONE);
        layoutFeeStructurePhoto.setVisibility(View.VISIBLE);
        if (photoUrl != null) {
            Glide.with(FeeStructureActivity.this).load(photoUrl).into(imageViewFeeStructure);
        } /*else {
            layoutFeeStructurePhoto.setVisibility(View.GONE);
            layoutNoFeeStructure.setVisibility(View.VISIBLE);
        }*/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        startActivity(new Intent(FeeStructureActivity.this, FeeStructureActivity.class)
        );
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layout_pay_now) {
         /*   Intent intent = new Intent(context, PaymentActivity.class);
            startActivity(intent);*/
            // startPayment();
        }
    }
}