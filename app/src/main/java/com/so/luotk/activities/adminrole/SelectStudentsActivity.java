package com.so.luotk.activities.adminrole;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.so.luotk.R;
import com.so.luotk.adapter.adminrole.SelectStudentsAdapter;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.ActivitySelectStudentsBinding;
import com.so.luotk.models.output.GetStudentDataResponse;
import com.so.luotk.models.output.StudentDetailsData;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SelectStudentsActivity extends AppCompatActivity {
    public static final int CREATE_ASSIGNMENT = 501;
    public static final int UPDATE_ASSIGNMENT = 502;
    private ActivitySelectStudentsBinding binding;
    private String isFrom;
    private SelectStudentsAdapter adapter;
    private List<StudentDetailsData> studentDetailsDataList;
    private String batchId;
    private final String searchKey = "";
    private final String pageLength = "1000";
    private int studentPageNo = 1;
    private ProgressBar mProgressBar;
    private ProgressView mProgressDialog;
    private boolean isListLoading;
    private boolean isSearchOpen, isFragmentLoaded;
    private final Handler searchHandler = new Handler(Looper.myLooper());
    private Runnable searchRunnable;
    private boolean isOnActivityResultCalled;
    private Integer total = 0;
    private final long mLastClickTime = 0;
    private String editable = "false";
    private String assignmentData;
    private String toolBartitleNew;
    public static Activity selectStudentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectStudentsBinding.inflate(getLayoutInflater());
        Utilities.restrictScreenShot(this);
        selectStudentActivity = this;
//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(binding.getRoot());
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setUpUi();
        setOnClickListeners();

    }

    private void setOnClickListeners() {
        binding.checkboxSelectAll.setOnClickListener(view -> {
            if (binding.checkboxSelectAll.isChecked()) {
                for (StudentDetailsData studentDetailsData : studentDetailsDataList) {
                    studentDetailsData.setSelected(true);
                    binding.layoutNext.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            } else {
                for (StudentDetailsData studentDetailsData : studentDetailsDataList) {
                    studentDetailsData.setSelected(false);
                    binding.layoutNext.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        binding.btnNext.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreateAssignmentTestActivity.class);
            intent.putExtra("selectedlist", adapter.getSelected());
            intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
            intent.putExtra(PreferenceHandler.BATCH_ID, batchId);
            intent.putExtra("title", toolBartitleNew);
            intent.putExtra("assignmentData", assignmentData);
            intent.putExtra("edit", editable);
            if (editable.equals("true")) {
                startActivityForResult(intent, UPDATE_ASSIGNMENT);
            } else {
                startActivityForResult(intent, CREATE_ASSIGNMENT);
            }

           /* startActivity(new Intent(this, CreateAssignmentTestActivity.class)
                    .putExtra("selectedlist", adapter.getSelected())
                    .putExtra(PreferenceHandler.BATCH_ID, batchId)
                    .putExtra(PreferenceHandler.IS_FROM, isFrom));*/
        });
    }

    private void setUpUi() {
        mProgressBar = binding.dataProgress;
        mProgressDialog = new ProgressView(SelectStudentsActivity.this);

        studentDetailsDataList = new ArrayList<>();
        if (getIntent() != null) {
            isFrom = getIntent().getStringExtra(PreferenceHandler.IS_FROM);
            batchId = getIntent().getStringExtra(PreferenceHandler.BATCH_ID);
            toolBartitleNew = getIntent().getStringExtra("title");
        }
        if (getIntent().getStringExtra("edit") != null) {
            editable = getIntent().getStringExtra("edit");
            assignmentData = getIntent().getStringExtra("assignmentData");
        }
        binding.toolbarLayout.toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(this));
        binding.toolbarLayout.textSteps.setText(getString(R.string.step) + " 1/2");
        binding.toolbarLayout.toolbar.setNavigationOnClickListener(view -> finish());
        if (!TextUtils.isEmpty(isFrom)) {
            String firstletter = isFrom.substring(0, 1).toUpperCase();
            String title = isFrom.replaceFirst(isFrom.substring(0, 1), firstletter);
            binding.toolbarLayout.toolbarTitle.setText(getString(R.string.select_student));
        }
        binding.recyclerStudentList.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerStudentList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        /*for (int i = 0; i < 4; i++) {
            StudentDetailsData student = new StudentDetailsData();
            student.setName("Student " + (i + 1));
            studentDetailsDataList.add(student);

        }*/

        if (!isFragmentLoaded) {
            hitService();
        } else {
            if (!isOnActivityResultCalled) {
                if (studentDetailsDataList != null && studentDetailsDataList.size() > 0) {
                    binding.layoutStudentList.setVisibility(VISIBLE);
                    binding.layoutNoAnyStudents.setVisibility(GONE);
                    setStudentListAdapter();

                } else {
                    binding.layoutStudentList.setVisibility(GONE);
                    binding.layoutNoAnyStudents.setVisibility(VISIBLE);
                }
                binding.shimmerLayout.setVisibility(GONE);
            }
        }

        if (!studentDetailsDataList.isEmpty()) {
            adapter = new SelectStudentsAdapter(studentDetailsDataList, binding.layoutNext, binding.checkboxSelectAll);
            binding.recyclerStudentList.setAdapter(adapter);
            setAdapterClickListener();
        }

    }

    private void setAdapterClickListener() {
        try {
            adapter.SetOnItemClickListener(position -> {
                if (adapter.getSelected().size() > 0)
                    binding.layoutNext.setVisibility(View.VISIBLE);
                else binding.layoutNext.setVisibility(View.GONE);
                binding.checkboxSelectAll.setChecked(adapter.getSelected().size() == studentDetailsDataList.size());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hitService() {
        if (!TextUtils.isEmpty(batchId))
            if (Utilities.checkInternet(SelectStudentsActivity.this)) {
                hitGetStudentListService();
                isFragmentLoaded = true;
            } else {
                Toast.makeText(SelectStudentsActivity.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
            }
    }

    private void hitGetStudentListService() {
        /*if (binding.swipeRefreshLayout.isRefreshing()) {
            mProgressBar.setVisibility(GONE);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } else if (!binding.swipeRefreshLayout.isRefreshing()) {
            if (!searchKey.isEmpty()) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                if (studentPageNo > 1) {
                    if (mProgressDialog != null && !mProgressDialog.isShowing())
                        mProgressDialog.show();
                }
            }
        }*/
        if (studentPageNo == 1) {
            studentDetailsDataList.clear();
        }
        new MyClient(SelectStudentsActivity.this).hitGetStudentListService(searchKey, batchId, pageLength, studentPageNo, (content, error) -> {
            binding.layoutStudentList.setVisibility(View.VISIBLE);
            if (binding.shimmerLayout.isShimmerStarted()) {
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(GONE);
            }
            GetStudentDataResponse response = (GetStudentDataResponse) content;
            if (content != null) {
                if (response.getStatus() == 200) {
                    if (response.getResult() != null) {
                        if (response.getResult().getData() != null && (response.getResult().getData().size() > 0)) {
                            total = response.getResult().getTotal();
                            studentDetailsDataList.addAll(response.getResult().getData());
                            setStudentListAdapter();
                            if (response.getResult().getData().size() < 50) {
                                isListLoading = false;
                            } else if (response.getResult().getTotal() == 50) {
                                isListLoading = false;
                            } else {
                                isListLoading = true;
                                studentPageNo++;
                            }
                        } else {
                            if (studentPageNo == 1) {
                                if (!searchKey.isEmpty()) {
                                    binding.layoutNoAnyStudents.setVisibility(View.VISIBLE);
                                    //binding.swipeRefreshLayout.setVisibility(GONE);
                                    binding.recyclerStudentList.setVisibility(GONE);
                                } else {
                                    binding.layoutNoAnyStudents.setVisibility(View.VISIBLE);
                                    //binding.swipeRefreshLayout.setVisibility(GONE);
                                    binding.recyclerStudentList.setVisibility(GONE);
                                }
                            }
                        }
                    }
                } else if (response.getStatus() == 403) {
                    Utilities.openUnauthorizedDialog(SelectStudentsActivity.this);
                } else {
                    /*if (binding.swipeRefreshLayout.isRefreshing()) {
                        binding.swipeRefreshLayout.setRefreshing(false);
                    } else */
                    if (mProgressBar.getVisibility() == VISIBLE) {
                        mProgressBar.setVisibility(GONE);
                    } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    } else if (binding.shimmerLayout.isShimmerStarted()) {
                        binding.shimmerLayout.stopShimmer();
                        binding.shimmerLayout.setVisibility(GONE);
                    }
                    binding.layoutStudentList.setVisibility(View.VISIBLE);
                    Utilities.makeToast(SelectStudentsActivity.this, getString(R.string.server_error));
                }


            } else {
               /* if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                } else */
                if (mProgressBar.getVisibility() == VISIBLE) {
                    mProgressBar.setVisibility(GONE);
                } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                } else if (binding.shimmerLayout.isShimmerStarted()) {
                    binding.shimmerLayout.stopShimmer();
                    binding.shimmerLayout.setVisibility(GONE);
                }
                binding.layoutStudentList.setVisibility(View.VISIBLE);
                Utilities.makeToast(SelectStudentsActivity.this, getString(R.string.server_error));
            }
            binding.shimmerLayout.setVisibility(GONE);
            isOnActivityResultCalled = false;
            binding.dataProgress.setVisibility(GONE);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        });

    }

    private void setStudentListAdapter() {
        Set set = new LinkedHashSet<>(studentDetailsDataList);
        studentDetailsDataList.clear();
        studentDetailsDataList.addAll(set);
        if (adapter == null)
            adapter = new SelectStudentsAdapter(studentDetailsDataList, binding.layoutNext, binding.checkboxSelectAll);
        else adapter.updateList(studentDetailsDataList);
        if (binding.recyclerStudentList.getAdapter() == null)
            binding.recyclerStudentList.setAdapter(adapter);
        binding.layoutNoAnyStudents.setVisibility(GONE);
        binding.batchTitle1.setVisibility(GONE);
        binding.recyclerStudentList.setVisibility(View.VISIBLE);
        //binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
        /*if (binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(false);
        } else */
        if (mProgressBar.getVisibility() == VISIBLE) {
            mProgressBar.setVisibility(GONE);
        } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        } else if (binding.shimmerLayout.isShimmerStarted()) {
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean dataaa = data != null;
        Log.e("TAG", "SEonActivityResult: in fragment $resultCode " + resultCode + " : " + requestCode + " : " + dataaa);
        if (requestCode == CREATE_ASSIGNMENT) {
            if (data != null) {
                Log.e("TAG", "SeonActivityResult: in fragment $data ");
                boolean isDataSubmitted = data.getBooleanExtra(PreferenceHandler.IS_DATA_SUBMITTED, false);
                String isFromIntent = data.getStringExtra(PreferenceHandler.IS_FROM);
                Log.d("TAG", "onActivityResult: " + isDataSubmitted);
                Intent intent = new Intent();
                intent.putExtra(PreferenceHandler.IS_DATA_SUBMITTED, true);
                intent.putExtra(PreferenceHandler.IS_FROM, isFromIntent);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        } else if (requestCode == UPDATE_ASSIGNMENT) {
            if (data != null) {
                Log.e("TAG", "EDITACTIVTY: in fragment $data ");
                boolean isDataSubmitted = data.getBooleanExtra(PreferenceHandler.IS_DATA_SUBMITTED, false);
                String isFromIntent = data.getStringExtra(PreferenceHandler.IS_FROM);
                String date = data.getStringExtra(PreferenceHandler.DATE);
                String time = data.getStringExtra(PreferenceHandler.TIME);
                String topicName = data.getStringExtra(PreferenceHandler.TOPIC_NAME);
                String notes = data.getStringExtra(PreferenceHandler.NOTES);
                String attachemnts = data.getStringExtra(PreferenceHandler.ATTACHMENT);
                String isSMS = data.getStringExtra(PreferenceHandler.IS_SMS);
                Log.d("TAG", "EDITACTIVTY: " + isDataSubmitted);

                Log.d("EditData", "onActivityResult: \nisDataSubmitted: " + isDataSubmitted
                +"\nDate: " + date
                        +"\ntime: " + time
                        +"\ntopicName: " + topicName
                        +"\nnotes: " + notes
                        +"\nattachemnts: " + attachemnts
                        +"\nisSMS: " + isSMS
                        );

                Intent intent = new Intent();
                intent.putExtra(PreferenceHandler.IS_DATA_SUBMITTED, true);
                intent.putExtra(PreferenceHandler.IS_FROM, isFromIntent);
                intent.putExtra(PreferenceHandler.DATE, date);
                intent.putExtra(PreferenceHandler.TIME, time);
                intent.putExtra(PreferenceHandler.TOPIC_NAME, topicName);
                intent.putExtra(PreferenceHandler.NOTES, notes);
                intent.putExtra(PreferenceHandler.ATTACHMENT, attachemnts);
                intent.putExtra(PreferenceHandler.IS_SMS, isSMS);

                intent.putExtra("editable", "true");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }/*else {
            layoutDataView.setVisibility(GONE);
            shimmerFrameLayout.setVisibility(VISIBLE);
            shimmerFrameLayout.startShimmer();
            if (isFromBatch)
                hitGetTestListService();
            else
                hitGetCourseTestListSevice();
        }*/
    }
}