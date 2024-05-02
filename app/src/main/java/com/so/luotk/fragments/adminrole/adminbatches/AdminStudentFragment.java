package com.so.luotk.fragments.adminrole.adminbatches;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.R;
import com.so.luotk.activities.adminrole.AddStudentActivity;
import com.so.luotk.activities.adminrole.JoinRequestListActivity;
import com.so.luotk.adapter.adminrole.AttendanceStudentListAdapter;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.FragmentAdminStudentBinding;
import com.so.luotk.models.output.DeleteStudentResponse;
import com.so.luotk.models.output.GetStudentDataResponse;
import com.so.luotk.models.output.StudentDetailsData;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class AdminStudentFragment extends Fragment {
    public static final String TAG = "AdminSt";
    private FragmentAdminStudentBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String batchId;
    private String searchKey = "";
    private final String pageLength = "50";
    private int studentPageNo = 1;
    private List<StudentDetailsData> studentDetailsDataList;
    private AttendanceStudentListAdapter adapter;
    private String mParam2;
    private ProgressBar mProgressBar;
    private ProgressView mProgressDialog;
    private boolean isListLoading;
    private boolean isSearchOpen, isFragmentLoaded;
    private final Handler searchHandler = new Handler(Looper.myLooper());
    private Runnable searchRunnable;
    private Context context;
    private Activity mActivity;
    private boolean isOnActivityResultCalled;
    private Integer total = 0;
    private long mLastClickTime = 0;
    int currentNightMode;
    private boolean isDarkMode;
    public AdminStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    public static AdminStudentFragment newInstance(String param1, String param2) {
        AdminStudentFragment fragment = new AdminStudentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentDetailsDataList = new ArrayList<>();
        if (getArguments() != null) {
            batchId = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        currentNightMode = getActivity().getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                isDarkMode = false;
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                isDarkMode = true;
                break;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentAdminStudentBinding.inflate(inflater, container, false);
        binding.studentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        binding.studentRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        mProgressDialog = new ProgressView(mActivity);
        binding.searchView.setVisibility(GONE);
        binding.btnAddStudent.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            startActivityForResult(new Intent(context, AddStudentActivity.class).putExtra(PreferenceHandler.BATCH_ID, batchId), 1);
        });
        binding.tvSeeJoinRequest.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            startActivityForResult(new Intent(context, JoinRequestListActivity.class).putExtra(PreferenceHandler.BATCH_ID, batchId), 2);
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
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
                    binding.layoutAddStudent.setVisibility(VISIBLE);
                }
                binding.shimmerLayout.setVisibility(GONE);
            }
        }

        setUpUi();
    }

    private void hitService() {
        if (!TextUtils.isEmpty(batchId))
            if (Utilities.checkInternet(context)) {
                hitGetStudentListService();
                isFragmentLoaded = true;
            } else {
                Toast.makeText(context, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
            }
    }

    private void setUpUi() {
        mProgressBar = binding.searchDataProgress;
        binding.searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.searchView.setQuery("", false);
            Utilities.hideKeyBoard(context);
            binding.searchView.clearFocus();
            if (Utilities.checkInternet(context)) {
                studentPageNo = 1;
                if (studentDetailsDataList.size() > 0) {
                    studentDetailsDataList.clear();
                }
                hitGetStudentListService();
            }
        });
        binding.studentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    Utilities.hideKeyBoard(context);
                    if (Utilities.checkInternet(context)) {
                        if (isLastItemDisplaying(recyclerView)) {
                            if (isListLoading) {
                                hitGetStudentListService();
                            }
                        }

                    }
                }
            }
        });
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchKey = query;
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
//                        hitApiWithSearch(query);
                    }
                };
                searchHandler.postDelayed(searchRunnable, 400);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchKey = newText;
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
//                        hitApiWithSearch(newText);
                    }
                };
                searchHandler.postDelayed(searchRunnable, 400);
                return false;
            }
        });
        binding.searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showInputMethod(view);
                }
            }
        });
    }

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }

    private void hitGetStudentListService() {
        if (binding.swipeRefreshLayout.isRefreshing()) {
            mProgressBar.setVisibility(GONE);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } else if (!binding.swipeRefreshLayout.isRefreshing()) {
            if (!searchKey.isEmpty()) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                if (studentPageNo > 1) {
                   /* if (mProgressDialog != null && !mProgressDialog.isShowing())
                        mProgressDialog.show();*/
                    if (binding.loadMoreLay.getVisibility() == GONE) {
                        binding.loadMoreLay.setVisibility(VISIBLE);
                    }
                }
            }
        }
        if (studentPageNo == 1) {
            studentDetailsDataList.clear();
        }
        new MyClient(context).hitGetStudentListService(searchKey, batchId, pageLength, studentPageNo, (content, error) -> {
            binding.layoutStudentList.setVisibility(View.VISIBLE);
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
                            binding.layoutAddStudent.setVisibility(View.VISIBLE);
                        } else {
                            if (studentPageNo == 1) {
                                if (!searchKey.isEmpty()) {
                                    binding.layoutNoAnyStudents.setVisibility(View.VISIBLE);
                                    binding.swipeRefreshLayout.setVisibility(GONE);
                                    binding.studentRecyclerView.setVisibility(GONE);
                                } else {
                                    binding.layoutNoAnyStudents.setVisibility(View.VISIBLE);
                                    binding.swipeRefreshLayout.setVisibility(GONE);
                                    binding.studentRecyclerView.setVisibility(GONE);
                                }
                            }
                        }
                        binding.layoutAddStudent.setVisibility(View.VISIBLE);
                    }
                } else if (response.getStatus() == 403) {
                    Utilities.openUnauthorizedDialog(context);
                } else {
                    if (binding.swipeRefreshLayout.isRefreshing()) {
                        binding.swipeRefreshLayout.setRefreshing(false);
                    } else if (mProgressBar.getVisibility() == VISIBLE) {
                        mProgressBar.setVisibility(GONE);
                    } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    } else if (binding.shimmerLayout.isShimmerStarted()) {
                        binding.shimmerLayout.stopShimmer();
                        binding.shimmerLayout.setVisibility(GONE);
                    }

                    if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                        binding.loadMoreLay.setVisibility(GONE);
                    }

                    binding.layoutStudentList.setVisibility(View.VISIBLE);
                    binding.layoutAddStudent.setVisibility(View.VISIBLE);
                    Utilities.makeToast(context, getString(R.string.server_error));
                }


            } else {
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                } else if (mProgressBar.getVisibility() == VISIBLE) {
                    mProgressBar.setVisibility(GONE);
                } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                } else if (binding.shimmerLayout.isShimmerStarted()) {
                    binding.shimmerLayout.stopShimmer();
                    binding.shimmerLayout.setVisibility(GONE);
                }

                if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                    binding.loadMoreLay.setVisibility(GONE);
                }

                binding.layoutStudentList.setVisibility(View.VISIBLE);
                binding.layoutAddStudent.setVisibility(View.VISIBLE);
                Utilities.makeToast(context, getString(R.string.server_error));
            }
            binding.shimmerLayout.setVisibility(GONE);
            isOnActivityResultCalled = false;
            /*if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }*/
            if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                binding.loadMoreLay.setVisibility(GONE);
            }
        });

        /*try {
            boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
            if (isAdmn) {
                if (AdminBatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                    AdminBatchDetailActivity.shimmerFrameLayout.stopShimmer();
                    AdminBatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                    AdminBatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                    AdminBatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);

                }
            }*//* else {
                if (BatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                    BatchDetailActivity.shimmerFrameLayout.stopShimmer();
                    BatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                    BatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                    BatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                }
            }*//*
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    private void setStudentListAdapter() {
        Set set = new LinkedHashSet<>(studentDetailsDataList);
        studentDetailsDataList.clear();
        studentDetailsDataList.addAll(set);
        if (adapter == null)
            adapter = new AttendanceStudentListAdapter(studentDetailsDataList, "student");
        else adapter.updateList(studentDetailsDataList, "student");
        if (binding.studentRecyclerView.getAdapter() == null)
            binding.studentRecyclerView.setAdapter(adapter);

        try {
            binding.studentCount.setText(getString(R.string.student)+"(" + total + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.layoutNoAnyStudents.setVisibility(GONE);
        binding.tvNoResults.setVisibility(GONE);
        binding.studentRecyclerView.setVisibility(View.VISIBLE);
        binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
        if (binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(false);
        } else if (mProgressBar.getVisibility() == VISIBLE) {
            mProgressBar.setVisibility(GONE);
        } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        } else if (binding.shimmerLayout.isShimmerStarted()) {
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(GONE);
        }

        if (binding.loadMoreLay.getVisibility() == VISIBLE) {
            binding.loadMoreLay.setVisibility(GONE);
        }
        adapter.setOnClickListener((position, delete) -> {
            if (delete) {
                openConfirmDeleteDialogNew(position);
//                openConfirmDeleteDialog(position);
            }
        });
    }

    private void openConfirmDeleteDialogNew(int position) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View deleteDialogView = factory.inflate(R.layout.delete_student_popup, null);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setView(deleteDialogView);
        AlertDialog dialog = alertBuilder.create();
        deleteDialogView.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.checkInternet(context)) {
                    dialog.dismiss();
                    hitDeleteStudentService(position);
                } else {
                    Utilities.makeToast(context, getString(R.string.internet_connection_error));
                }
            }
        });
        deleteDialogView.findViewById(R.id.no_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        deleteDialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /*alertBuilder.setMessage(R.string.do_you_want_to_delete_student);
        alertBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utilities.checkInternet(context))
                    hitDeleteStudentService(position);
                else Utilities.makeToast(context, getString(R.string.internet_connection_error));
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });*/

        dialog.show();
    }

    private void openConfirmDeleteDialog(int position) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage(R.string.do_you_want_to_delete_student);
        alertBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utilities.checkInternet(context))
                    hitDeleteStudentService(position);
                else Utilities.makeToast(context, getString(R.string.internet_connection_error));
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }

    private void hitDeleteStudentService(int position) {
        Map<String, Object> map = new HashMap<>();
        if (batchId != null)
            map.put("batchId", batchId);
        if (studentDetailsDataList != null && studentDetailsDataList.size() > 0 && studentDetailsDataList.get(position).getId() != null)
            map.put("studentId", studentDetailsDataList.get(position).getId());
        new MyClient(context).deleteStudentFromBatch(map, (content, error) -> {
            if (content != null) {
                DeleteStudentResponse response = (DeleteStudentResponse) content;
                if (response.getStatus() != null && response.getStatus() == 200) {
                    if (response.getSuccess() && response.getResult() != null) {
                        try {
                            studentDetailsDataList = adapter.removeAt(position);
                            /*     studentDetailsDataList.remove(position);*/
                            total = total - 1;
                            binding.studentCount.setText(getString(R.string.student)+"(" + total + ")");
                            if (studentDetailsDataList.size() == 0) {
                                binding.layoutNoAnyStudents.setVisibility(VISIBLE);
                                binding.layoutStudentList.setVisibility(GONE);
                            }
                        } catch (Exception e) {
                            Utilities.makeToast(context, getString(R.string.refresh_content));
                        }

                        Utilities.makeToast(context, response.getResult());
                    } else Utilities.makeToast(context, getString(R.string.student_not_deleted));
                } else if (response.getStatus() != null && response.getStatus() == 403)
                    Utilities.openUnauthorizedDialog(context);
                else Utilities.makeToast(context, getString(R.string.server_error));
            } else Utilities.makeToast(context, getString(R.string.server_error));
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1 || resultCode == 5) {
            studentPageNo = 1;
            hitGetStudentListService();
            isOnActivityResultCalled = true;
        } else
            isOnActivityResultCalled = false;
    }

    private void hitApiWithSearch(String newText) {
        if (newText != null && !newText.isEmpty()) {
            searchKey = newText;
            if (Utilities.checkInternet(context)) {
                if (studentDetailsDataList.size() > 0) {
                    studentDetailsDataList.clear();
                }
                studentPageNo = 1;
                hitGetStudentListService();
            } else {
                Toast.makeText(context, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
            }
        } else if (TextUtils.isEmpty(newText)) {
            searchKey = "";
            studentPageNo = 1;
            if (studentDetailsDataList.size() > 0) {
                studentDetailsDataList.clear();
            }
            hitGetStudentListService();

        }
    }
}