package com.so.luotk.activities.adminrole;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.so.luotk.databinding.ActivityYoutubePlayerVideoBinding;
import com.so.luotk.utils.Utilities;

public class YoutubePlayerVideo extends AppCompatActivity {

    ActivityYoutubePlayerVideoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityYoutubePlayerVideoBinding.inflate(getLayoutInflater());
        overridePendingTransition(0, 0);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(binding.getRoot());

    }
}