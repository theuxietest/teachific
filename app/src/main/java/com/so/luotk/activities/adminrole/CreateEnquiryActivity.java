package com.so.luotk.activities.adminrole;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.so.luotk.databinding.ActivityCreateEnquiryBinding;
import com.so.luotk.utils.Utilities;

public class CreateEnquiryActivity extends AppCompatActivity {
    private ActivityCreateEnquiryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateEnquiryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
         Utilities.setUpStatusBar(this); Utilities.setLocale(this);
        setUpToolbar();
    }

    private void setUpToolbar() {
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColor(this));
        binding.toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
    }
}