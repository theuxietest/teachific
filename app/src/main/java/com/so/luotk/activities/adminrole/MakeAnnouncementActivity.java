package com.so.luotk.activities.adminrole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.so.luotk.R;
import com.so.luotk.adapter.adminrole.AnnouncementBatchListAdapter;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.ActivityMakeAnnouncementsBinding;
import com.so.luotk.models.newmodels.adminBatchModel.Result;
import com.so.luotk.models.output.MakeAnnouncementResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MakeAnnouncementActivity extends AppCompatActivity {
    private ActivityMakeAnnouncementsBinding binding;
    private List<Result> batchList;
    private AnnouncementBatchListAdapter adapter;
    private String announcement, isFrom;
    private Integer batchId = -1, id = -1, position;
    private long mLastClickTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMakeAnnouncementsBinding.inflate(getLayoutInflater());
        Utilities.restrictScreenShot(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        overridePendingTransition(0, 0);
        Utilities.setUpStatusBar(this); Utilities.setLocale(this);
        setContentView(binding.getRoot());

        setUpUi();
        setUpToolbar();
        setOnClicks();

    }

    private void setUpToolbar() {
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(getApplicationContext()));
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(view ->
        {
            Utilities.hideKeyBoard(this);
            if (isFrom.equalsIgnoreCase("multibatch")) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedList", new Gson().toJson(adapter.getCurrentList()));
                setResult(1, resultIntent);
                finish();
            } else finish();
        });
    }

    private void setUpUi() {
        if (getIntent() != null) {
            Type type = new TypeToken<List<Result>>() {
            }.getType();
            batchList = new Gson().fromJson(getIntent().getStringExtra("batchlist"), type);
            isFrom = getIntent().getStringExtra(PreferenceHandler.IS_FROM);
            if (isFrom.equalsIgnoreCase("batch") || isFrom.equalsIgnoreCase("edit")) {
                batchId = Integer.parseInt(getIntent().getStringExtra(PreferenceHandler.BATCH_ID));
//                binding.edtTypeMsg.requestFocus();
//                Utilities.openKeyboard(this, binding.edtTypeMsg);
            }
            if (isFrom.equalsIgnoreCase("edit") || isFrom.equalsIgnoreCase("view")) {
                batchId = Integer.parseInt(getIntent().getStringExtra(PreferenceHandler.BATCH_ID));
                announcement = getIntent().getStringExtra("announcement");
                id = Integer.parseInt(getIntent().getStringExtra("id"));
                position = getIntent().getIntExtra("position", -1);
            }
        }

        if (isFrom.equalsIgnoreCase("multibatch")) {
            binding.recyclerSelectedClasses.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            Set<Result> set = new HashSet<>(batchList);
            batchList.clear();
            batchList.addAll(set);
            adapter = new AnnouncementBatchListAdapter(batchList, "fragment");
            if (!batchList.isEmpty()) {
                binding.recyclerSelectedClasses.setAdapter(adapter);
            }
        } else {
            binding.tvSelected.setVisibility(View.GONE);
            binding.layoutBatchAttached.setVisibility(View.GONE);
            if (isFrom.equalsIgnoreCase("edit")) {
                if (!TextUtils.isEmpty(announcement)) {
                    binding.edtTypeMsg.setText(announcement);

                }
            } else if (isFrom.equalsIgnoreCase("view")) {
                binding.tvTypeMsgTitle.setText(R.string.message_txt);
                binding.edtTypeMsg.setText(announcement);
                binding.notEditableLayer.setVisibility(View.VISIBLE);
                binding.tvDone.setVisibility(View.GONE);
            }
        }

        binding.edtTypeMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String otp_textbox_oneStr = binding.edtTypeMsg.getText().toString();
                if (!otp_textbox_oneStr.isEmpty()) {
                    binding.tvDone.setEnabled(true);
                   /* binding.tvDone.setBackgroundColor(getColor(R.color.blue_main));
                    binding.tvDone.setTextColor(getColor(R.color.white));*/
                } else {
                    binding.tvDone.setEnabled(true);
                  /*  binding.tvDone.setBackgroundColor(getColor(R.color.disabledButtonColor));
                    binding.tvDone.setTextColor(getColor(R.color.disabled_btn));*/
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("TAG", "afterTextChanged: " + s.toString());

                // TODO Auto-generated method stub
            }
        });

    }


    private void setOnClicks() {
        binding.imgAddBatch.setOnClickListener(view -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedList", new Gson().toJson(adapter.getCurrentList()));
            setResult(1, resultIntent);
            finish();
        });
        binding.tvDone.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (isFrom.equalsIgnoreCase("batch") || isFrom.equalsIgnoreCase("multibatch")) {
                if (!TextUtils.isEmpty(binding.edtTypeMsg.getText().toString().trim())) {
                    announcement = binding.edtTypeMsg.getText().toString();
                    if (!getSelected().isEmpty()) {
                        if (Utilities.checkInternet(getApplicationContext())) {
                            hitMakeAnnouncementService();
                        } else {
                            Utilities.makeToast(getApplicationContext(), getString(R.string.internet_connection_error));
                        }
                    } else
                        Snackbar.make(binding.getRoot(), "Select batch(es)", Snackbar.LENGTH_SHORT).show();
                } else {
                    Utilities.hideKeyBoard(this);
                    Snackbar.make(binding.getRoot(), "Please enter message first", Snackbar.LENGTH_SHORT).show();
                }
            }
            if (isFrom.equalsIgnoreCase("edit")) {
                if (!TextUtils.isEmpty(binding.edtTypeMsg.getText().toString().trim())) {
                    if (batchId != null && id != null && id != 0) {
                        if (Utilities.checkInternet(this)) {
                            hitEditAnnouncementService();
                        } else {
                            Utilities.makeToast(this, getString(R.string.internet_connection_error));
                        }
                    }

                } else {
                    Utilities.hideKeyBoard(this);
                    Snackbar.make(binding.getRoot(), "Please enter message first", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void hitDeleteAnnouncementService() {
        new MyClient(this).hitDeleteAnnouncementService(id, (content, error) -> {
            if (content != null) {
                MakeAnnouncementResponse response = (MakeAnnouncementResponse) content;
                if (response.getStatus() == 200 && response.isSuccess()) {
                    if (response.getResult() != null) {
                        Utilities.makeToast(this, response.getResult());
                        setResult(1);
                        finish();
                    } else
                        Utilities.makeToast(this, "Announcement not deleted");
                } else if (response.getStatus() == 403) {
                    Utilities.openUnauthorizedDialog(this);
                } else Utilities.makeToast(this, getString(R.string.server_error));
            } else {
                Utilities.makeToast(this, getString(R.string.server_error));
            }
        });
    }

    private void hitEditAnnouncementService() {
        ProgressView progressView = new ProgressView(this);
        progressView.show();
        Map<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(binding.edtTypeMsg.getText().toString()) && batchId != -1 && id != -1) {
            map.put("announcement", binding.edtTypeMsg.getText().toString());
            map.put("fk_batchId", batchId);
            map.put("id", id);
        }
        new MyClient(this).hitSaveAnnouncementService(map, (content, error) -> {
            if (!isFinishing())
                progressView.dismiss();

            if (content != null) {
                MakeAnnouncementResponse response = (MakeAnnouncementResponse) content;
                if (response.getStatus() == 200 && response.isSuccess()) {
                    if (response.getResult() != null) {
                        binding.edtTypeMsg.setText("");
                        Utilities.hideKeyBoard(this);
                        Utilities.makeToast(this, response.getResult());
                        Intent resultIntent = new Intent();
                        //    resultIntent.putExtra("position",position);
                        setResult(1, resultIntent);
                        finish();
                    } else Utilities.makeToast(this, "Announcement not saved");
                } else if (response.getStatus() == 403)
                    Utilities.openUnauthorizedDialog(this);
                else Utilities.makeToast(this, getString(R.string.server_error));
            } else Utilities.makeToast(this, "Announcement not saved");

        });
    }


    public List<Integer> getSelected() {
        List<Integer> selected = new ArrayList<>();
        if (isFrom.equalsIgnoreCase("multibatch")) {
            List<Result> list = adapter.getCurrentList();
            int size = list.size();

            for (int i = 0; i < size; i++) {
                if (list.get(i).isSelected()) {
                    selected.add(list.get(i).getId());
                }
            }
        }
        if (isFrom.equalsIgnoreCase("batch")) {
            if (batchId != null)
                selected.add(batchId);
        }

        return selected;
    }


    public void hitMakeAnnouncementService() {
        ProgressView progressView = new ProgressView(this);
        progressView.show();
        new MyClient(this).hitMakeMultibatchAnnouncementService(getSelected(), announcement, (content, error) -> {
            if (!isFinishing())
                progressView.dismiss();
            if (content != null) {
                MakeAnnouncementResponse response = (MakeAnnouncementResponse) content;
                if (response.isSuccess() && response.getStatus() == 200) {
                    if (response.getResult() != null) {
                        Utilities.makeToast(getApplicationContext(), response.getResult());
                        // setResult(2);
                        setResult(1);
                        finish();
                    } else
                        Utilities.makeToast(this, "Announcement not submitted");

                } else if (response.getStatus() == 403)
                    Utilities.openUnauthorizedDialog(getApplicationContext());
                else
                    Utilities.makeToast(this, "Announcement not submitted");

            } else
                Utilities.makeToast(this, getString(R.string.server_error));

        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isFrom.equalsIgnoreCase("view")) {
            getMenuInflater().inflate(R.menu.options_menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                editAnnouncement();
                return true;
            case R.id.menu_delete:
                openConfirmDeleteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editAnnouncement() {
        binding.tvTypeMsgTitle.setText(R.string.edit_message);
        if (!TextUtils.isEmpty(binding.edtTypeMsg.getText()))
            binding.edtTypeMsg.setSelection(binding.edtTypeMsg.getText().length());
        binding.notEditableLayer.setVisibility(View.GONE);
        Utilities.openKeyboard(this, binding.edtTypeMsg);
        isFrom = "edit";
        binding.tvDone.setVisibility(View.VISIBLE);
    }

    private void openConfirmDeleteDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.do_you_want_to_delete);
        alertBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Utilities.checkInternet(getApplicationContext()))
                    if (batchId > 0 && id != null && id > 0) {
                        hitDeleteAnnouncementService();
                    } else
                        Utilities.makeToast(getApplicationContext(), getString(R.string.internet_connection_error));
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utilities.hideKeyBoard(this);
    }
}