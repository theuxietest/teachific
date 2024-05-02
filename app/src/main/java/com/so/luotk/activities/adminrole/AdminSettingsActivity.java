package com.so.luotk.activities.adminrole;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.so.luotk.adapter.adminrole.AdminSettingsAdapter;
import com.so.luotk.databinding.ActivityAdminSettingsBinding;
import com.so.luotk.models.output.AdminSettings;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

public class AdminSettingsActivity extends AppCompatActivity {
    private ActivityAdminSettingsBinding binding;
    private List<AdminSettings> adminSettingsList;
    private AdminSettingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
         Utilities.setUpStatusBar(this); Utilities.setLocale(this);
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColor(this));
        binding.toolbar.setNavigationOnClickListener(view -> finish());
        setUpUi();
    }

    private void setUpUi() {
        adminSettingsList = new ArrayList<>();
        binding.recyclerSettings.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerSettings.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adminSettingsList.add(new AdminSettings("Overview"));
        adminSettingsList.add(new AdminSettings("Students"));
        adminSettingsList.add(new AdminSettings("Attendance"));
        adminSettingsList.add(new AdminSettings("Announcement"));
        adminSettingsList.add(new AdminSettings("Assignment"));
        adminSettingsList.add(new AdminSettings("Test"));
        adminSettingsList.add(new AdminSettings("Video"));
        adminSettingsList.add(new AdminSettings("Live"));
        adminSettingsList.add(new AdminSettings("Study Material"));
        if (!adminSettingsList.isEmpty()) {
            binding.recyclerSettings.setAdapter(new AdminSettingsAdapter(adminSettingsList));
        }


    }
}