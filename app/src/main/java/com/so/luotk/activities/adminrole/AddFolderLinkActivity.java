package com.so.luotk.activities.adminrole;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.so.luotk.R;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.ActivityAddFolderLinkBinding;
import com.so.luotk.models.output.GetBatchSubmitAssignmentTestResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFolderLinkActivity extends AppCompatActivity {

    ActivityAddFolderLinkBinding binding;
    private String batchId, fromWhere, isLink, editable, folderId, folderIdEdit, folderName, videoUrl, videoId, content, id;
    private ProgressView progressView;
    private Handler handler;
    private final Handler searchHandler = new Handler(Looper.myLooper());
    private boolean isFirstInternetToastDone;
    private Runnable runnable, searchRunnable;
    private ProgressView mProgressDialog;
    private APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
        binding = ActivityAddFolderLinkBinding.inflate(getLayoutInflater());
        overridePendingTransition(0, 0);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(binding.getRoot());
        apiInterface = ApiUtils.getApiInterface();
        if (getIntent() != null)
            batchId = getIntent().getStringExtra(PreferenceHandler.BATCH_ID);
        fromWhere = getIntent().getStringExtra("fromWhere");
        isLink = getIntent().getStringExtra("isLink");
        editable = getIntent().getStringExtra("editable");
        progressView = new ProgressView(this);
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(this));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyBoard(AddFolderLinkActivity.this);
                finish();
            }
        });
        handler = new Handler(Looper.myLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        if (fromWhere.equals("video")) {
            if (editable.equals("true")) {
                folderId = getIntent().getStringExtra("folderId");
                folderName = getIntent().getStringExtra("folderName");
                folderIdEdit = getIntent().getStringExtra("id");
                binding.edtName.setText(folderName);
                if (isLink.equals("true")) {
                    binding.toolbar.setTitle(getString(R.string.edit_video));
                    binding.edtLinkLay.setVisibility(View.VISIBLE);
                    binding.edtNameLay.setVisibility(View.GONE);
                    videoId = getIntent().getStringExtra("videoId");
                    videoUrl = getIntent().getStringExtra("videoUrl");
                    binding.edtLink.setText(videoUrl);
                    binding.edtLink.requestFocus();
                    binding.edtLink.setSelection(binding.edtLink.getText().toString().length());
                } else {
                    binding.toolbar.setTitle(getString(R.string.edit_folder));
                    binding.edtLinkLay.setVisibility(View.GONE);
                    binding.edtNameLay.setVisibility(View.VISIBLE);
                    binding.edtName.requestFocus();
                    binding.edtName.setSelection(binding.edtName.getText().toString().length());
                }
            } else {
                folderId = getIntent().getStringExtra("folderId");
                Log.d("FolderId", "onCreate: " + folderId);
                if (isLink.equals("true")) {
                    binding.toolbar.setTitle(getString(R.string.add_video));
                    binding.edtLinkLay.setVisibility(View.VISIBLE);
                    binding.edtNameLay.setVisibility(View.GONE);
                    binding.edtLink.requestFocus();
                } else {
                    binding.toolbar.setTitle(getString(R.string.add_folder));
                    binding.edtLinkLay.setVisibility(View.GONE);
                    binding.edtNameLay.setVisibility(View.VISIBLE);
                    binding.edtName.requestFocus();
                }
            }
        } else if (fromWhere.equals("studyMaterial")) {
            if (editable.equals("true")) {
                if (isLink.equals("true")) {
                    folderId = getIntent().getStringExtra("folderId");
                    folderName = getIntent().getStringExtra("folderName");
                    folderIdEdit = getIntent().getStringExtra("id");
                    content = getIntent().getStringExtra("content");
                    id = getIntent().getStringExtra("id");
                    binding.toolbar.setTitle(getString(R.string.edit_ext_link));
                    binding.edtLinkLay.setVisibility(View.VISIBLE);
                    binding.edtNameLay.setVisibility(View.VISIBLE);
                    binding.edtName.setText(folderName);
                    binding.edtNameLay.setHint(getString(R.string.enter_link_name));
                    binding.edtLink.setText(content);
                    binding.edtLinkLay.setHint((getString(R.string.enter_link_here)));
                    binding.edtName.setSelection(binding.edtName.getText().toString().length());
                } else {
                    folderId = getIntent().getStringExtra("folderId");
                    folderName = getIntent().getStringExtra("folderName");
                    id = getIntent().getStringExtra("id");
                    binding.edtName.setText(folderName);
                    binding.toolbar.setTitle(getString(R.string.edit_folder));
                    binding.edtLinkLay.setVisibility(View.GONE);
                    binding.edtNameLay.setVisibility(View.VISIBLE);
                    binding.edtName.setSelection(binding.edtName.getText().toString().length());
                }
            } else {
                if (isLink.equals("true")) {
                    if (getIntent().getStringExtra("folderId") != null) {
                        folderId = getIntent().getStringExtra("folderId");
                    } else {
                        folderId = "0";
                    }

                    binding.toolbar.setTitle(getString(R.string.add_ext_link));
                    binding.edtLinkLay.setVisibility(View.VISIBLE);
                    binding.edtNameLay.setVisibility(View.VISIBLE);
                    binding.edtNameLay.setHint(getString(R.string.enter_link_name));
                    binding.edtLinkLay.setHint(getString(R.string.enter_link_here));
                } else {
                    Log.d("AddFolderCall", "onCreate: " + getIntent().getStringExtra("folderId"));
                    if (getIntent().getStringExtra("folderId") != null) {
                        folderId = getIntent().getStringExtra("folderId");
                    } else {
                        folderId = "0";
                    }
                    binding.toolbar.setTitle(getString(R.string.add_folder));
                    binding.edtLinkLay.setVisibility(View.GONE);
                    binding.edtNameLay.setVisibility(View.VISIBLE);
                }
            }
            Utilities.openKeyboard(this, binding.edtName);
        }
        binding.btnSubmit.setOnClickListener(v -> {

            if (fromWhere.equals("video")) {
                if (editable.equals("true")) {
                    if (isLink.equals("true")) {
                        if (!TextUtils.isEmpty(binding.edtLink.getText().toString())) {
                            binding.edtLinkLay.setErrorEnabled(false);
                            binding.edtLinkLay.setError(null);
                            updateLink(videoId, binding.edtLink.getText().toString());
                        } else {
                            binding.edtLinkLay.setErrorEnabled(true);
                            binding.edtLinkLay.setError(getString(R.string.required_youtube_link));
                            binding.edtLinkLay.setErrorIconDrawable(0);
//                            Toast.makeText(AddFolderLinkActivity.this, "The video link field is required.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!TextUtils.isEmpty(binding.edtName.getText().toString())) {
                            binding.edtNameLay.setErrorEnabled(false);
                            binding.edtNameLay.setError(null);
                            updateFolder(folderId, binding.edtName.getText().toString(), folderIdEdit);
                        } else {
                            binding.edtNameLay.setErrorEnabled(true);
                            binding.edtNameLay.setError(getString(R.string.required_folder_name));
                            binding.edtNameLay.setErrorIconDrawable(0);
//                            Toast.makeText(AddFolderLinkActivity.this, "The video link field is required.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (isLink.equals("true")) {
                        if (!TextUtils.isEmpty(binding.edtLink.getText().toString())) {
                            binding.edtLinkLay.setErrorEnabled(false);
                            binding.edtLinkLay.setError(null);
                            createLink(folderId, binding.edtLink.getText().toString(), isLink);
                        } else {
                            binding.edtLinkLay.setErrorEnabled(true);
                            binding.edtLinkLay.setError(getString(R.string.required_youtube_link));
                            binding.edtLinkLay.setErrorIconDrawable(0);
//                            Toast.makeText(AddFolderLinkActivity.this, "The field is required.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!TextUtils.isEmpty(binding.edtName.getText().toString())) {
                            binding.edtNameLay.setErrorEnabled(false);
                            binding.edtNameLay.setError(null);
                            createFolder(binding.edtName.getText().toString(), isLink, folderId);
                        } else {
                            binding.edtNameLay.setErrorEnabled(true);
                            binding.edtNameLay.setError(getString(R.string.required_folder_name));
                            binding.edtNameLay.setErrorIconDrawable(0);
//                            Toast.makeText(AddFolderLinkActivity.this, "The field is required.", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            } else if (fromWhere.equals("studyMaterial")) {
                if (editable.equals("true")) {
                    if (isLink.equals("true")) {
                        if (checkValidation(true)) {
                            updateStudyMaterialLink(binding.edtLink.getText().toString(), binding.edtName.getText().toString(), isLink, id);
                        }
                    } else {
                        if (checkValidation(false)) {
                            updateStudyMaterialFolder("", binding.edtName.getText().toString(), isLink, id);
                        }
                    }
                } else {
                    if (isLink.equals("true")) {
                        if (checkValidation(true)) {
                            createStudyMaterialLink(folderId, binding.edtLink.getText().toString(), binding.edtName.getText().toString(), isLink);
                        } else {
//                            Toast.makeText(AddFolderLinkActivity.this, "The field is required.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (checkValidation(false)) {
                            createStudyMaterialFolder(folderId, "", binding.edtName.getText().toString(), isLink);
                        } else {
//                            Toast.makeText(AddFolderLinkActivity.this, "The field is required.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });
    }

    private boolean checkValidation(boolean isLink) {
        boolean val = true;
        if (isLink) {
            if (TextUtils.isEmpty(binding.edtLink.getText().toString().trim())) {
//            binding.erTime.setVisibility(View.VISIBLE);
                binding.edtLink.requestFocus();
                binding.edtLink.getParent().requestChildFocus(binding.edtLink, binding.edtLink);
                binding.edtLinkLay.setErrorEnabled(true);
                binding.edtLinkLay.setError(getString(R.string.required_link));
                binding.edtLinkLay.setErrorIconDrawable(0);
                val = false;
            } else {
                binding.edtLinkLay.setErrorEnabled(false);
                binding.edtLinkLay.setError(null);
            }
        }
        if (TextUtils.isEmpty(binding.edtName.getText().toString().trim())) {
//            binding.erDate.setVisibility(View.VISIBLE);
            binding.edtName.requestFocus();
            binding.edtName.getParent().requestChildFocus(binding.edtName, binding.edtName);
            binding.edtNameLay.setErrorEnabled(true);
            binding.edtNameLay.setError(getString(R.string.required_name));
            binding.edtNameLay.setErrorIconDrawable(0);
            val = false;
        } else {
            binding.edtNameLay.setErrorEnabled(false);
            binding.edtNameLay.setError(null);
        }

        return val;
    }

    private void updateStudyMaterialLink(String folderLinkContent, String linkFolderName, String isLink, String folderIdEdit) {
        if (Utilities.checkInternet(AddFolderLinkActivity.this)) {
            handler.removeCallbacks(runnable);
            if (batchId != null)
                hitSubmitUpdateFolderLink(folderLinkContent, linkFolderName, isLink, folderIdEdit);
            else {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.invalid_match), Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    private void updateStudyMaterialFolder(String folderLinkContent, String linkFolderName, String isLink, String folderIdEdit) {
        if (Utilities.checkInternet(AddFolderLinkActivity.this)) {
            handler.removeCallbacks(runnable);
            if (batchId != null)
                hitSubmitUpdateFolderLink(folderLinkContent, linkFolderName, isLink, folderIdEdit);
            else {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.invalid_match), Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }


    private void hitSubmitUpdateFolderLink(final String folderLinkName, final String linkFolderName, final String isLink, String folderIdEdit) {
        try {
            Map<String, String> map = new HashMap<>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(AddFolderLinkActivity.this, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(AddFolderLinkActivity.this, PreferenceHandler.DEVICE_ID, ""));
            Call<GetBatchSubmitAssignmentTestResponse> call = null;

            Log.d("TAG", "field: " + folderLinkName + " : " + folderId + " : " + folderIdEdit);

            if (!batchId.isEmpty())
                map.put("fk_batchId", batchId);

            if (isLink.equals("true")) {
                if (!folderLinkName.isEmpty())
                    map.put("content", folderLinkName);

                map.put("fk_folderId", folderId);
                map.put("id", folderIdEdit);

                if (!linkFolderName.isEmpty())
                    map.put("name", linkFolderName);

                call = apiInterface.updateMaterialLink(headers, map);


            } else {
                if (!linkFolderName.isEmpty())
                    map.put("folderName", linkFolderName);

                map.put("id", folderIdEdit);
                map.put("folderId", folderId);
                call = apiInterface.updateMaterialFolder(headers, map);

            }

            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.d("TAG", "hitSubmitAddFolderLink: " + key + " : " + value);
                // do stuff
            }

            mProgressDialog = new ProgressView(AddFolderLinkActivity.this);
            mProgressDialog.show();
            call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
                @Override
                public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                    mProgressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                                if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                                    Utilities.hideKeyBoard(AddFolderLinkActivity.this);
                                    Intent intent = new Intent();
                                    intent.putExtra(PreferenceHandler.IS_FOLDER_CREATED, true);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(AddFolderLinkActivity.this);
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(AddFolderLinkActivity.this, "406 error");
                            } else {
                                Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.server_error));
                            }       /*else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("402")) {
                               mProgressDialog.dismiss();
                           }*/
                        } else
                            Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.folder_not_created));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void createStudyMaterialLink(String folderId, String LinkContent, String linkName, String isLink) {
        if (Utilities.checkInternet(AddFolderLinkActivity.this)) {
            handler.removeCallbacks(runnable);
            if (batchId != null)
                hitAddMaterialLink(folderId, LinkContent, linkName, isLink);
            else {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.invalid_match), Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    private void createStudyMaterialFolder(String folderId, String folderLinkContent, String linkFolderName, String isLink) {
        if (Utilities.checkInternet(AddFolderLinkActivity.this)) {
            handler.removeCallbacks(runnable);
            if (batchId != null)
                hitAddMaterialFolder(folderId, folderLinkContent, linkFolderName, isLink);
            else {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.invalid_match), Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    private void updateLink(String id, String videoLink) {
        if (Utilities.checkInternet(AddFolderLinkActivity.this)) {
            handler.removeCallbacks(runnable);
            if (id != null)
                hitUpdateVideoLink(videoLink, id);
            else {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.invalid_match), Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    private void hitUpdateVideoLink(final String videoLink, final String videoId) {
        try {
            Map<String, String> map = new HashMap<>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(AddFolderLinkActivity.this, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(AddFolderLinkActivity.this, PreferenceHandler.DEVICE_ID, ""));
            Call<GetBatchSubmitAssignmentTestResponse> call = null;

            Log.d("TAG", "field: " + videoLink + " : " + videoId + " : " + folderId);
            if (!videoId.isEmpty())
                map.put("id", videoId);
            if (!videoLink.isEmpty())
                map.put("videoUrl", videoLink);

            call = apiInterface.updateVideoLink(headers, map);
            mProgressDialog = new ProgressView(AddFolderLinkActivity.this);
            mProgressDialog.show();
            call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
                @Override
                public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                    mProgressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                                if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                                    Utilities.hideKeyBoard(AddFolderLinkActivity.this);
                                    Intent intent = new Intent();
                                    intent.putExtra(PreferenceHandler.IS_FOLDER_CREATED, true);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(AddFolderLinkActivity.this);
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(AddFolderLinkActivity.this, "406 error");
                            } else {
                                Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.server_error));
                            }       /*else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("402")) {
                               mProgressDialog.dismiss();
                           }*/
                        } else
                            Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.video_not_submitted));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createLink(String folderId, String folderLinkName, String isLink) {
        if (Utilities.checkInternet(AddFolderLinkActivity.this)) {
            handler.removeCallbacks(runnable);
            if (batchId != null)
                hitSubmitAddLink(folderId, folderLinkName, isLink);
            else {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.invalid_match), Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    private void hitSubmitAddLink(final String folderId, final String folderLinkName, final String isLink) {
        try {
            Map<String, String> map = new HashMap<>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(AddFolderLinkActivity.this, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(AddFolderLinkActivity.this, PreferenceHandler.DEVICE_ID, ""));
            Call<GetBatchSubmitAssignmentTestResponse> call = null;

            Log.d("TAG", "field: " + folderLinkName + " : " + batchId + " : " + folderId + " : " + isLink);
            if (!folderLinkName.isEmpty())
                map.put("videoUrl", folderLinkName);
            if (!batchId.isEmpty())
                map.put("fk_batchId", batchId);
            if (!folderId.isEmpty())
                map.put("fk_folderId", folderId);

            if (isLink.equals("true")) {
                call = apiInterface.createNewVideoLink(headers, map);
            }
            mProgressDialog = new ProgressView(AddFolderLinkActivity.this);
            mProgressDialog.show();
            call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
                @Override
                public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                    mProgressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                                if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                                    Utilities.hideKeyBoard(AddFolderLinkActivity.this);
                                    Intent intent = new Intent();
                                    intent.putExtra(PreferenceHandler.IS_FOLDER_CREATED, true);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(AddFolderLinkActivity.this);
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(AddFolderLinkActivity.this, "406 error");
                            } else {
                                Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.server_error));
                            }       /*else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("402")) {
                               mProgressDialog.dismiss();
                           }*/
                        } else
                            Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.video_not_submitted));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utilities.hideKeyBoard(AddFolderLinkActivity.this);
    }

    private void updateFolder(String id, String folderName, String folderIdEdit) {
        if (Utilities.checkInternet(AddFolderLinkActivity.this)) {
            handler.removeCallbacks(runnable);
            if (id != null)
                hitUpdateFolder(folderName, id, folderIdEdit);
            else {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.invalid_folder), Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    private void hitUpdateFolder(final String folderName, final String folderID, String folderIdEdit) {
        try {
            Map<String, String> map = new HashMap<>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(AddFolderLinkActivity.this, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(AddFolderLinkActivity.this, PreferenceHandler.DEVICE_ID, ""));
            Call<GetBatchSubmitAssignmentTestResponse> call = null;

            Log.d("TAG", "field: " + folderName + " : " + batchId + " : " + folderID);
            if (!folderID.isEmpty())
                map.put("folderId", folderID);
            if (!folderIdEdit.isEmpty())
                map.put("id", folderIdEdit);
            if (!folderName.isEmpty())
                map.put("folderName", folderName);

            call = apiInterface.updateFolder(headers, map);
            mProgressDialog = new ProgressView(AddFolderLinkActivity.this);
            mProgressDialog.show();
            call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
                @Override
                public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                    mProgressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                                if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                                    Utilities.hideKeyBoard(AddFolderLinkActivity.this);
                                    PreferenceHandler.writeString(AddFolderLinkActivity.this, PreferenceHandler.VIDEO_CACHING, null);
                                    Intent intent = new Intent();
                                    intent.putExtra(PreferenceHandler.IS_FOLDER_EDIT, true);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(AddFolderLinkActivity.this);
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(AddFolderLinkActivity.this, "406 error");
                            } else {
                                Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.server_error));
                            }       /*else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("402")) {
                               mProgressDialog.dismiss();
                           }*/
                        } else
                            Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.video_not_submitted));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFolder(String folderLinkName, String isLink, String folderId) {
        if (Utilities.checkInternet(AddFolderLinkActivity.this)) {
            handler.removeCallbacks(runnable);
            if (batchId != null)
                hitSubmitAddVideoLink(folderLinkName, isLink, folderId);
            else {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.invalid_match), Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(AddFolderLinkActivity.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }


    private void hitSubmitAddVideoLink(final String folderLinkName, final String isLink, final String folderId) {
        try {
            Map<String, String> map = new HashMap<>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(AddFolderLinkActivity.this, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(AddFolderLinkActivity.this, PreferenceHandler.DEVICE_ID, ""));
            Call<GetBatchSubmitAssignmentTestResponse> call = null;

            Log.d("TAG", "field: " + folderLinkName + " : " + isLink);
            if (!folderLinkName.isEmpty())
                map.put("folderName", folderLinkName);
            if (!batchId.isEmpty())
                map.put("fk_batchId", batchId);

            if (folderId != null) {
                if (!folderId.isEmpty()) {
                    map.put("folderId", folderId);
                    Log.d("call This", "hitSubmitAddVideoLink: " + folderId);
                }
            }
            if (!isLink.equals("true")) {
                call = apiInterface.createNewVideoFolder(headers, map);
            }
            mProgressDialog = new ProgressView(AddFolderLinkActivity.this);
            mProgressDialog.show();
            call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
                @Override
                public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                    mProgressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                                if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                                    Utilities.hideKeyBoard(AddFolderLinkActivity.this);
                                    PreferenceHandler.writeString(AddFolderLinkActivity.this, PreferenceHandler.VIDEO_CACHING, null);
                                    Intent intent = new Intent();
                                    intent.putExtra(PreferenceHandler.IS_FOLDER_CREATED, true);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(AddFolderLinkActivity.this);
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(AddFolderLinkActivity.this, "406 error");
                            } else {
                                Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.server_error));
                            }       /*else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("402")) {
                               mProgressDialog.dismiss();
                           }*/
                        } else
                            Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.folder_not_created));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hitAddMaterialLink(final String folderId, final String linkContent, final String linkName, final String isLink) {
        try {
            Map<String, String> map = new HashMap<>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(AddFolderLinkActivity.this, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(AddFolderLinkActivity.this, PreferenceHandler.DEVICE_ID, ""));
            Call<GetBatchSubmitAssignmentTestResponse> call = null;

            Log.d("TAG", linkName + "field: " + linkContent + " : " + folderId);

            if (!batchId.isEmpty())
                map.put("fk_batchId", batchId);

            if (isLink.equals("true")) {
                if (!linkContent.isEmpty())
                    map.put("content", linkContent);
                map.put("fk_folderId", folderId);
                if (!linkName.isEmpty())
                    map.put("name", linkName);

                call = apiInterface.createNewMaterialLink(headers, map);
            }

            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.d("TAG", "hitSubmitAddFolderLink: " + key + " : " + value);
                // do stuff
            }

            mProgressDialog = new ProgressView(AddFolderLinkActivity.this);
            mProgressDialog.show();
            call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
                @Override
                public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                    mProgressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                                if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                                    Utilities.hideKeyBoard(AddFolderLinkActivity.this);
                                    Intent intent = new Intent();
                                    intent.putExtra(PreferenceHandler.IS_FOLDER_CREATED, true);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(AddFolderLinkActivity.this);
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(AddFolderLinkActivity.this, "406 error");
                            } else {
                                Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.server_error));
                            }       /*else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("402")) {
                               mProgressDialog.dismiss();
                           }*/
                        } else
                            Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.folder_not_created));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hitAddMaterialFolder(final String folderId, final String folderLinkName, final String linkFolderName, final String isLink) {
        try {
            Map<String, String> map = new HashMap<>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(AddFolderLinkActivity.this, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(AddFolderLinkActivity.this, PreferenceHandler.DEVICE_ID, ""));
            Call<GetBatchSubmitAssignmentTestResponse> call = null;

            Log.d("TAG", "field: " + folderLinkName + " : " + folderId);


            if (!batchId.isEmpty())
                map.put("fk_batchId", batchId);

            if (!linkFolderName.isEmpty())
                map.put("folderName", linkFolderName);

            if (!folderId.isEmpty())
                map.put("fk_folderId", folderId);
            if (!folderId.isEmpty())
                map.put("folderId", folderId);


            call = apiInterface.createNewMaterialFolder(headers, map);


            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.d("TAG", "hitSubmitAddFolderLink: " + key + " : " + value);
                // do stuff
            }

            mProgressDialog = new ProgressView(AddFolderLinkActivity.this);
            mProgressDialog.show();
            call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
                @Override
                public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                    mProgressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                                if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                                    Utilities.hideKeyBoard(AddFolderLinkActivity.this);
                                    Intent intent = new Intent();
                                    intent.putExtra(PreferenceHandler.IS_FOLDER_CREATED, true);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(AddFolderLinkActivity.this);
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(AddFolderLinkActivity.this, "406 error");
                            } else {
                                Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.server_error));
                            }       /*else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("402")) {
                               mProgressDialog.dismiss();
                           }*/
                        } else
                            Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.folder_not_created));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Utilities.makeToast(AddFolderLinkActivity.this, getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

