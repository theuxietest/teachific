package com.so.luotk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.so.luotk.R;
import com.so.luotk.fragments.adminrole.adminbatches.AdminStudyMaterialFragment;
import com.so.luotk.fragments.batches.StudyMaterialFragment;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

public class SmFolderDetailsActivity extends AppCompatActivity {
    private String folderId, batchId, folderName, isFrom;
    private TextView toolbar;
    private Toolbar main;
    private String sellingPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
//      getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(R.layout.activity_sm_folder_details);

        toolbar = findViewById(R.id.toolbar);
        main = findViewById(R.id.main);
        folderId = getIntent().getStringExtra(PreferenceHandler.SmFolderId);
        batchId = getIntent().getStringExtra(PreferenceHandler.BATCH_ID);
        folderName = getIntent().getStringExtra(PreferenceHandler.smFolderName);
        isFrom = getIntent().getStringExtra(PreferenceHandler.IS_FROM);
        sellingPrice = getIntent().getStringExtra(PreferenceHandler.SELLING_PRICE);
        setUpToolbar();
        if (isFrom.equalsIgnoreCase("admin"))
            openFragment(AdminStudyMaterialFragment.newInstance(batchId, folderId));
        else
            openFragment(StudyMaterialFragment.newInstance(batchId, folderId, isFrom, sellingPrice));
        Log.d("FolderDetailActivity", "onCreate: " + isFrom);
        getWindow().setStatusBarColor(ContextCompat.getColor(SmFolderDetailsActivity.this,R.color.white));
    }

    private void setUpToolbar() {
        main.setNavigationIcon(Utilities.setNavigationIconColorBlack(this));
        if (folderName != null)
            toolbar.setText(folderName);
        main.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void openFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.folderDetailsLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        startActivity(new Intent(SmFolderDetailsActivity.this, SmFolderDetailsActivity.class)
                .putExtra(PreferenceHandler.BATCH_ID, batchId)
                .putExtra(PreferenceHandler.SmFolderId, folderId)
                .putExtra(PreferenceHandler.smFolderName, folderName).putExtra(PreferenceHandler.SELLING_PRICE, sellingPrice).putExtra(PreferenceHandler.IS_FROM, isFrom));
        finish();
    }
}