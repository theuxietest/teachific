package com.so.luotk.activities.adminrole;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.so.luotk.R;
import com.so.luotk.adapter.adminrole.AnnouncementBatchListAdapter;
import com.so.luotk.databinding.ActivitySelctBatchesForAnnouncementBinding;
import com.so.luotk.models.newmodels.adminBatchModel.AdminBatchModel;
import com.so.luotk.models.newmodels.adminBatchModel.Result;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.so.luotk.viewmodel.AdminBatchListViewModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SelectBatchesForAnnouncementActivity extends AppCompatActivity {
    private ActivitySelctBatchesForAnnouncementBinding binding;
    private AnnouncementBatchListAdapter adapter;
    private List<Result> batchListResult, selectedBatchList;
    private Handler handler;
    private Runnable runnable;
    private boolean isFirstInternetToastDone;
    private int refresh = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelctBatchesForAnnouncementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
         Utilities.setUpStatusBar(this); Utilities.setLocale(this);
        setUpToolbar();
        setUpUI();
    }

    private void setUpToolbar() {
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColor(this));
        binding.toolbar.setTitle("Select batches");
        binding.toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
    }

    private void setUpUI() {
        batchListResult = new ArrayList<>();
        selectedBatchList = new ArrayList<>();
        binding.recyclerBatchList.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerBatchList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.shimmerLayout.startShimmer();
        handler = new Handler(Looper.myLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        checkInternet();
        binding.btnMake.setOnClickListener(view -> {
            binding.toolbar.setTitle("Multi batch announcement");
            startActivityForResult(new Intent(this, MakeAnnouncementActivity.class)
                    .putExtra("batchlist", new Gson().toJson(adapter.getCurrentList()))
                    .putExtra(PreferenceHandler.IS_FROM, "multibatch"), 1);

        });
    }

    private void checkInternet() {
        if (Utilities.checkInternet(this)) {
            handler.removeCallbacks(runnable);
            hitGetBatchListService();
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    private void hitGetBatchListService() {
        AdminBatchListViewModel viewModel = new ViewModelProvider(this).get(refresh + "", AdminBatchListViewModel.class);
        viewModel.getObservableData().observe(this, (AdminBatchModel response) -> {

            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.layoutSelectBatches.setVisibility(View.VISIBLE);
            if (response != null) {
                if (response.getStatus() == 200) {
                    if (response.getResult().size() > 0) {
                        batchListResult.addAll(response.getResult());
                        setUpAdapter();
                    }

                } else if (response.getStatus() == 403) {
                    Utilities.openUnauthorizedDialog(SelectBatchesForAnnouncementActivity.this);
                } else
                    Utilities.makeToast(this, getString(R.string.server_error));
            }
            refresh++;
        });
    }

    private void setUpAdapter() {

        if (!batchListResult.isEmpty())
            adapter = new AnnouncementBatchListAdapter(batchListResult, "activity");
        if (adapter != null)
            binding.recyclerBatchList.setAdapter(adapter);
        adapter.setOnclickListener(position -> {
            if (adapter.getSelected().size() > 0) {
                binding.btnMake.setVisibility(View.VISIBLE);
            } else {
                binding.btnMake.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) {
            Type type = new TypeToken<List<Result>>() {
            }.getType();
            selectedBatchList = new Gson().fromJson(data.getStringExtra("selectedList"), type);
            if (adapter != null)
                adapter.updateList(selectedBatchList);
        }
        if (resultCode == 2)
            this.finish();


    }
}