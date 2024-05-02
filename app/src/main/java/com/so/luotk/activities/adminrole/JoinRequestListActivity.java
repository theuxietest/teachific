package com.so.luotk.activities.adminrole;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.so.luotk.R;
import com.so.luotk.adapter.adminrole.JoinRequestListAdapter;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.ActivityJoinRequestListBinding;
import com.so.luotk.models.newmodels.ServerResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.so.luotk.viewmodel.JoinRequestViewModel;

public class JoinRequestListActivity extends AppCompatActivity {
    private ActivityJoinRequestListBinding binding;
    private JoinRequestViewModel viewModel;
    private ProgressView progressView;
    private boolean toast, isDataChanged;
    private Handler handler;
    private Runnable runnable;
    private String batchId;
    private JoinRequestListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Utilities.restrictScreenShot(this);
        binding = ActivityJoinRequestListBinding.inflate(getLayoutInflater());
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(binding.getRoot());

        if (getIntent() != null) {
            batchId = getIntent().getStringExtra(PreferenceHandler.BATCH_ID);
        }
        progressView = new ProgressView(this);
        handler = new Handler(Looper.myLooper());
        runnable = this::hitService;
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(this));
        binding.toolbar.setNavigationOnClickListener(v -> {
            setResultData();

            finish();
        });
        binding.recyclerJoinRequest.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerJoinRequest.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new JoinRequestListAdapter((id, isAccept) -> {
            showConfirmDialog(isAccept, id);
            // hitRequestAcceptDeclineService(isAccept, id);
        });
        binding.recyclerJoinRequest.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(JoinRequestViewModel.class);
        hitService();
        viewModel.getObservableData().observe(this, response -> {
            if (response != null) {
                switch (response.status) {
                    case EXTRA:
                        adapter.clearList();
                        binding.tvNoRequests.setVisibility(View.VISIBLE);
                        binding.recyclerJoinRequest.setVisibility(View.GONE);
                        binding.shimmerLayout.setVisibility(View.GONE);
                        break;
                    case LOADING:
                        break;
                    case AUTHENTICATED:
                        handler.removeCallbacks(runnable);
                        if (response.data.getResult().size() > 0) {
                            adapter.clearList();
                            adapter.updateList(response.data.getResult());
                            binding.tvNoRequests.setVisibility(View.GONE);
                            binding.recyclerJoinRequest.setVisibility(View.VISIBLE);
                        } else binding.tvNoRequests.setVisibility(View.VISIBLE);
                        binding.shimmerLayout.setVisibility(View.GONE);
                        break;
                    case NOT_AUTHENTICATED:
                        progressView.dismiss();
                        Utilities.openUnauthorizedDialog(this);
                        break;
                    case ERROR:
                        if (toast)
                            Utilities.makeToast(this, response.message);
                        handler.postDelayed(runnable, 5000);
                        if (response.message.contains("network"))
                            toast = false;
                        else
                            progressView.dismiss();
                        Utilities.makeToast(this, response.message);
                        break;
                }
            }

        });


    }

    private void setResultData() {
       /* if (isDataChanged) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("isDataChanged", isDataChanged);
            setResult(5, resultIntent);
        }*/
        if (isDataChanged)
            setResult(5);
    }

    private void showConfirmDialog(boolean isAccept, String id) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        String s = isAccept ? getString(R.string.accept_req) : getString(R.string.decline_req);
        String locale = PreferenceHandler.readString(this, PreferenceHandler.LOCALE, "en");
        switch (locale) {
            case "en":
                alertBuilder.setMessage(getString(R.string.are_you_sure)+" " + s);
                break;
            case "mr":case "kn":
            case "hi":
                alertBuilder.setMessage(getString(R.string.are_you_sure)+" " + s + " " + getString(R.string.do_task));
                break;
        }
        /*    alertBuilder.setMessage(getString(R.string.are_you_sure) + s);*/
        alertBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utilities.checkInternet(JoinRequestListActivity.this)) {
                    if (progressView != null && !progressView.isShowing())
                        progressView.show();
                    if (!isFinishing())
                        dialog.dismiss();
                    hitRequestAcceptDeclineService(isAccept, id);

                } else
                    Utilities.makeToast(JoinRequestListActivity.this, getString(R.string.internet_connection_error));
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isFinishing())
                    dialog.dismiss();
            }
        });
        AlertDialog dialog = alertBuilder.create();
        if (!isFinishing())
            dialog.show();
    }

    private void hitRequestAcceptDeclineService(boolean isAccept, String id) {
        new MyClient(this).hitAcceptDeclineJoinRequests(id, isAccept, (content, error) -> {
            if (progressView != null && !isFinishing())
                progressView.dismiss();
            if (content != null) {
                ServerResponse response = (ServerResponse) content;
                if (response.getStatus() != null && response.getStatus() == 200) {
                    if (response.getSuccess() && response.getResult() != null) {
                        Utilities.makeToast(this, response.getResult());
                        isDataChanged = true;
                        hitService();
                    } else if (response.getResult() != null)
                        Utilities.makeToast(this, response.getResult());
                } else if (response.getStatus() != null && response.getStatus() == 403)
                    Utilities.openUnauthorizedDialog(this);
                else Utilities.makeToast(this, getString(R.string.server_error));
            } else Utilities.makeToast(this, getString(R.string.server_error));

        });
    }

    @Override
    public void onBackPressed() {
        setResultData();
        finish();
    }

    private void hitService() {
        if (batchId != null)
            viewModel.getView(this, batchId);
    }
}