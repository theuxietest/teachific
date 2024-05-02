package com.so.luotk.fragments.reports;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.so.luotk.R;
import com.so.luotk.adapter.AssignmentTestListAdapter;
import com.so.luotk.adapter.TestAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.activities.ReportListDetailActivity;
import com.so.luotk.firebase.NotificationDataModel;
import com.so.luotk.models.output.Data;
import com.so.luotk.models.output.FolderData;
import com.so.luotk.models.output.GetAssignmentTestListResponse;
import com.so.luotk.models.output.GetReportTestListResponse;
import com.so.luotk.models.output.ReportTestData;
import com.so.luotk.testmodule.TestResultActivity;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ReportAssignmentTestFragment extends Fragment {
    private static final String TAG = "AssignmentTestVideoListFragment";
    private static final String ARGS_1 = "ARGS_1";
    private static final String ARGS_2 = "ARGS_2";
    private RecyclerView mRrecylerView;
    private AssignmentTestListAdapter mAdapter;
    private SearchView searchView;
    private TextView tvNoResultFound;
    private APIInterface apiInterface;
    private ProgressView mProgressDialog;
    private String isFrom;
    private final String pageLength = "25";
    private ArrayList<Data> assignmentList;
    private ArrayList<ReportTestData> testList;
    private ArrayList<FolderData> folderList;
    private boolean isAssignmentLoading, isTestLoading;
    private int assignmentPageNo = 1, testPageNo = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private TestAdapter testAdapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Handler handler;
    private final Handler searchHandler = new Handler(Looper.myLooper());
    private Runnable runnable, searchRunnable;
    private View layoutDataView;
    private boolean isFirstInternetToastDone;
    private View imgCart, divider;
    private boolean isFromNotification, isRefreshing;
    private View view, root_layout, layout_empty_list;
    private List<NotificationDataModel> notificationDataList;
    private ProgressBar mProgressBar;
    private ImageView empty_list_img;
    private String searchKey = "";
    private Context context;
    private Activity mActivity;
    private long mLastClickTime = 0;

    public ReportAssignmentTestFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    public static ReportAssignmentTestFragment newInstance(String isFrom, boolean isFromNotify) {
        ReportAssignmentTestFragment fragment = new ReportAssignmentTestFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_1, isFrom);
        args.putBoolean(ARGS_2, isFromNotify);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utilities.restrictScreenShot(getActivity());
//      getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (getArguments() != null) {
            isFrom = getArguments().getString(ARGS_1);
            isFromNotification = getArguments().getBoolean(ARGS_2);
        }
        testList = new ArrayList<>();
        folderList = new ArrayList<>();
        view = inflater.inflate(R.layout.fragment_assignment_test, container, false);
        setupUI(view);
        setToolbar(view);
        Log.d("Reporrt", "onCreateView: ");
        return view;
    }

    private void setToolbar(View view) {


        toolbar = view.findViewById(R.id._report_test_assignment_toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(upArrow);
        toolbar.setVisibility(View.VISIBLE);
        if (isFrom != null) {
            if (isFrom.equalsIgnoreCase("assignment")) {
                toolbar.setTitle(R.string.assign_submitted);
            } else {
                toolbar.setVisibility(GONE);
                divider.setVisibility(GONE);

            }

        } else {
            openFragment(new ReportFragment());
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    if (!isFromNotification) {
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction trans = manager.beginTransaction();
                        trans.remove(ReportAssignmentTestFragment.this);
                        trans.commit();
                        manager.popBackStack();
                    } else {
                        openFragment(new ReportFragment());
                    }
                }
            }

        });

    }

    public void openFragment(Fragment fragment) {

        if (fragment != null && getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            // transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    private void setupUI(View view) {
        apiInterface = ApiUtils.getApiInterface();
        Utilities.hideKeyBoard(context);
        notificationDataList = PreferenceHandler.getNotificationDataList(context);
        tvNoResultFound = view.findViewById(R.id.tv_no_match);
        empty_list_img = view.findViewById(R.id.no_assignment_img);
        layout_empty_list = view.findViewById(R.id.layout_empty_list);
        mRrecylerView = view.findViewById(R.id.assignment_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mProgressBar = view.findViewById(R.id.search_data_progress);
        divider = view.findViewById(R.id.view_below_toolbar);
        assignmentList = new ArrayList<>();

        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(context);
        mRrecylerView.setLayoutManager(mLayoutManager);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        layoutDataView = view.findViewById(R.id.layout_data_view);
        root_layout = view.findViewById(R.id.root_layout);
        shimmerFrameLayout.startShimmer();
        handler = new Handler(Looper.myLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        checkInternet();
        searchView = view.findViewById(R.id.search_view);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                Utilities.hideKeyBoard(context);
                searchKey = newText;
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        hitApiWithSearch(newText);
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
                        hitApiWithSearch(newText);
                    }
                };
                searchHandler.postDelayed(searchRunnable, 400);
                return false;
            }

        });


        mRrecylerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0)
                    Utilities.hideKeyBoard(context);
                if (Utilities.checkInternet(context)) {
                    if (isLastItemDisplaying(recyclerView)) {

                        if (isFrom.equalsIgnoreCase("assignment")) {
                            if (isAssignmentLoading) {
                                hitGetReportAssignmentListService();
                            }
                        } else /*if (isFrom.equalsIgnoreCase("batchtest"))*/ {
                            if (isTestLoading) {
                                hitGetReportTestListService(isFrom);
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                }
            }

        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchView.setQuery("", false);
                searchKey = "";
                searchView.clearFocus();
                isRefreshing = true;
                if (Utilities.checkInternet(context)) {
                    if (isFrom.equalsIgnoreCase("assignment")) {
                        assignmentPageNo = 1;
                        if (assignmentList.size() > 0) {
                            assignmentList.clear();
                        }
                        hitGetReportAssignmentListService();
                    } else /*if (isFrom.equalsIgnoreCase("batchtest"))*/ {
                        testPageNo = 1;
                        if (testList.size() > 0) {
                            testList.clear();
                        }
                        hitGetReportTestListService(isFrom);
                    }
                } else {
                    Toast.makeText(context, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void hitApiWithSearch(String newText) {
        isRefreshing = true;
        if (newText != null && !newText.isEmpty()) {
            searchKey = newText;
            if (Utilities.checkInternet(context)) {
                if (isFrom.equalsIgnoreCase("assignment")) {
                    if (assignmentList.size() > 0) {
                        assignmentList.clear();
                    }
                    assignmentPageNo = 1;
                    hitGetReportAssignmentListService();
                } else /*if (isFrom.equalsIgnoreCase("batchtest"))*/ {
                    if (testList.size() > 0) {
                        testList.clear();
                    }
                    testPageNo = 1;
                    hitGetReportTestListService(isFrom);
                }
            } else {
                Utilities.makeToast(context, getString(R.string.internet_connection_error));
            }
        } else if (TextUtils.isEmpty(newText)) {
            searchKey = "";
            searchView.clearFocus();
            if (isFrom.equalsIgnoreCase("assignment")) {
                assignmentPageNo = 1;
                if (assignmentList.size() > 0) {
                    assignmentList.clear();
                }
                hitGetReportAssignmentListService();
            } else /*if (isFrom.equalsIgnoreCase("batchtest"))*/ {
                testPageNo = 1;
                if (testList.size() > 0) {
                    testList.clear();
                }
                hitGetReportTestListService(isFrom);
            }
        }
    }

    private void checkInternet() {
        if (Utilities.checkInternet(context)) {
            handler.removeCallbacks(runnable);
            if (isFrom.equalsIgnoreCase("assignment")) {
                hitGetReportAssignmentListService();
            } else/* if (isFrom.equalsIgnoreCase("batchtest"))*/ {
                hitGetReportTestListService(isFrom);
            }
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(context, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }

    private void hitGetReportAssignmentListService() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        Call<GetAssignmentTestListResponse> call = apiInterface.getReportAssignmentList(headers, searchKey, pageLength, String.valueOf(assignmentPageNo));
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            mProgressBar.setVisibility(GONE);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } else if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
            if (!searchKey.isEmpty()) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                if (assignmentPageNo > 1) {
                    mProgressDialog = new ProgressView(context);
                    mProgressDialog.show();
                }
            }
        }
        if (assignmentPageNo == 1) {
            assignmentList.clear();
        }
        call.enqueue(new Callback<GetAssignmentTestListResponse>() {
            @Override
            public void onResponse(Call<GetAssignmentTestListResponse> call, Response<GetAssignmentTestListResponse> response) {
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else if (mProgressBar.getVisibility() == VISIBLE) {
                    mProgressBar.setVisibility(GONE);
                } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                } else if (shimmerFrameLayout != null && shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }
                layoutDataView.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getStatus() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null && response.body().getResult().getData().size() > 0) {
                                mRrecylerView.setVisibility(View.VISIBLE);
                                layout_empty_list.setVisibility(GONE);
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                                tvNoResultFound.setVisibility(GONE);
                                assignmentList.addAll(response.body().getResult().getData());
                                setAssignmentTestListAdapter();
                                if (response.body().getResult().getData().size() < 25) {
                                    isAssignmentLoading = false;
                                } else if (response.body().getResult().getTotal().equalsIgnoreCase("25")) {
                                    isAssignmentLoading = false;
                                } else {
                                    isAssignmentLoading = true;
                                    assignmentPageNo++;
                                }

                            } else {
                                if (assignmentPageNo == 1) {
                                    if (!searchKey.isEmpty()) {
                                        layout_empty_list.setVisibility(View.VISIBLE);
                                        tvNoResultFound.setVisibility(View.VISIBLE);
                                        empty_list_img.setVisibility(GONE);
                                        tvNoResultFound.setText(getString(R.string.no_result));
                                        swipeRefreshLayout.setVisibility(GONE);
                                        mRrecylerView.setVisibility(GONE);
                                    } else {
                                        mRrecylerView.setVisibility(GONE);
                                        layout_empty_list.setVisibility(VISIBLE);
                                        empty_list_img.setVisibility(View.VISIBLE);
                                        tvNoResultFound.setVisibility(View.VISIBLE);
                                    }

                                } else isAssignmentLoading = false;
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(context);
                    } else {
                        Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<GetAssignmentTestListResponse> call, Throwable t) {
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else if (mProgressBar.getVisibility() == VISIBLE) {
                    mProgressBar.setVisibility(GONE);
                } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                } else if (shimmerFrameLayout != null && shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hitGetReportTestListService(String isFrom) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        Call<GetReportTestListResponse> call = null;
        if (isFrom.equalsIgnoreCase("batchtest"))
            call = apiInterface.getReportTestList(headers, searchKey, pageLength, String.valueOf(testPageNo));
        else
            call = apiInterface.getCourseReportTestList(headers, searchKey, pageLength, String.valueOf(testPageNo));
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            mProgressBar.setVisibility(GONE);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } else if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
            if (!searchKey.isEmpty()) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                if (testPageNo > 1) {
                    mProgressDialog = new ProgressView(context);
                    mProgressDialog.show();
                }
            }
        }
        if (testPageNo == 1) {
            testList.clear();
        }
        call.enqueue(new Callback<GetReportTestListResponse>() {
            @Override
            public void onResponse(Call<GetReportTestListResponse> call, Response<GetReportTestListResponse> response) {
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else if (mProgressBar.getVisibility() == VISIBLE) {
                    mProgressBar.setVisibility(GONE);
                } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                } else if (shimmerFrameLayout != null && shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }
                layoutDataView.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getStatus() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null && response.body().getResult().getData().size() > 0) {
                                mRrecylerView.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                                layout_empty_list.setVisibility(GONE);
                                tvNoResultFound.setVisibility(GONE);
                                testList.addAll(response.body().getResult().getData());
                                setAssignmentTestListAdapter();
                                if (response.body().getResult().getData().size() < 25) {
                                    isTestLoading = false;
                                } else if (response.body().getResult().getTotal().equalsIgnoreCase("25")) {
                                    isTestLoading = false;
                                } else {
                                    isTestLoading = true;
                                    testPageNo++;
                                }
                            } else {
                                if (testPageNo == 1) {
                                    if (!searchKey.isEmpty()) {
                                        layout_empty_list.setVisibility(View.VISIBLE);
                                        tvNoResultFound.setVisibility(View.VISIBLE);
                                        empty_list_img.setVisibility(GONE);
                                        tvNoResultFound.setText(getString(R.string.no_result));
                                        swipeRefreshLayout.setVisibility(GONE);
                                        mRrecylerView.setVisibility(GONE);
                                    } else {
                                        layout_empty_list.setVisibility(VISIBLE);
                                        empty_list_img.setVisibility(View.VISIBLE);
                                        mRrecylerView.setVisibility(GONE);
                                        tvNoResultFound.setText(getString(R.string.no_test_attempted));
                                        tvNoResultFound.setVisibility(View.VISIBLE);
                                    }
                                } else isTestLoading = false;

                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(context);
                    } else {
                        Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<GetReportTestListResponse> call, Throwable t) {
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else if (mProgressBar.getVisibility() == VISIBLE) {
                    mProgressBar.setVisibility(GONE);
                } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                } else if (shimmerFrameLayout != null && shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }
                layoutDataView.setVisibility(View.VISIBLE);
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAssignmentTestListAdapter() {
        //  notificationDataList = PreferenceHandler.getNotificationDataList(context);
        if (isFrom.equalsIgnoreCase("batchtest") || isFrom.equalsIgnoreCase("coursetest")) {
            Set<ReportTestData> set = new LinkedHashSet<>();
            set.addAll(testList);
            testList.clear();
            testList.addAll(set);
            if (testAdapter == null) {
                mRrecylerView.setVisibility(View.VISIBLE);
                testAdapter = new TestAdapter(context, testList);
                mRrecylerView.setAdapter(testAdapter);
                setNotificationView();
            } else {
                setNotificationView();
                mRrecylerView.getRecycledViewPool().clear();
                testAdapter.setUpdatedList(testList);

            }
        } else {
            Set<Data> set = new LinkedHashSet<>();
            set.addAll(assignmentList);
            assignmentList.clear();
            assignmentList.addAll(set);
            if (mAdapter == null) {
                mRrecylerView.setVisibility(View.VISIBLE);
                mAdapter = new AssignmentTestListAdapter(context, assignmentList, true, true);
                mRrecylerView.setAdapter(mAdapter);
            } else {
                mRrecylerView.getRecycledViewPool().clear();
                mAdapter.notifyDataSetChanged();
            }
        }
        isRefreshing = false;
        if (mAdapter != null || testAdapter != null)
            setClickListener();

    }

    private void setNotificationView() {
        if (notificationDataList != null && notificationDataList.size() > 0) {
            int bigList = testList.size() > notificationDataList.size() ? 1 : 0;
            if (bigList == 1) {
                for (int i = 0; i < notificationDataList.size(); i++) {
                    for (int j = 0; j < testList.size(); j++) {
                        if (notificationDataList.get(i).getId() != null && notificationDataList.get(i).getId().equalsIgnoreCase(testList.get(j).getId())) {
                            testList.get(j).setNewItem(true);
                            // testAdapter.notifyItemChanged(j);
                            // testAdapter.notifyItemRangeChanged(j, testList.size() - 1);
                            testAdapter.setUpdatedList(testList);

                        }
                    }
                }
            } else {
                for (int i = 0; i < testList.size(); i++) {
                    for (int j = 0; j < notificationDataList.size(); j++) {
                        if (notificationDataList.get(j).getId() != null && notificationDataList.get(j).getId().equalsIgnoreCase(testList.get(i).getId())) {
                            testList.get(i).setNewItem(true);
                            // testAdapter.notifyItemChanged(i);
                            // testAdapter.notifyItemRangeChanged(i, testList.size() - 1);
                            testAdapter.setUpdatedList(testList);
                        }
                    }
                }
            }

        } else {
            testAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Method to set click listener on recycler view
     */
    private void setClickListener() {
        if (isFrom.equalsIgnoreCase("batchtest") || isFrom.equalsIgnoreCase("coursetest")) {
            if (testAdapter != null) {
                testAdapter.SetOnItemClickListener(new TestAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (!isRefreshing) {
                            searchView.clearFocus();
                            notificationDataList = PreferenceHandler.getNotificationDataList(context);
                            try {
                                if (notificationDataList != null && notificationDataList.size() > 0)
                                    for (int i = 0; i < notificationDataList.size(); i++) {
                                        if (notificationDataList.get(i).getId() != null && notificationDataList.get(i).getId().equalsIgnoreCase(testList.get(position).getId())) {
                                            notificationDataList.remove(notificationDataList.get(i));
                                            testList.get(position).setNewItem(false);
                                            testAdapter.notifyItemChanged(position);
                                            PreferenceHandler.setList(context, PreferenceHandler.NOTIFICATION_DATA, notificationDataList);
                                            break;
                                        }
                                    }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (testList.get(position).getTest_type() == 1) {
                                Intent intent = new Intent(context, ReportListDetailActivity.class);
                                intent.putExtra(PreferenceHandler.LIST_ITEM, testList.get(position));
                                intent.putExtra(PreferenceHandler.IS_FROM, "test");
                                startActivity(intent);
                            } else if (testList.get(position).getTest_type() == 2/* && Integer.parseInt(testList.get(position).getAttempted()) > 0*/) {
                                startActivity(new Intent(context, TestResultActivity.class).putExtra(PreferenceHandler.TESTID, testList.get(position).getId()));
                            }
                        }
                    }
                });
            }
        } else {
            if (mAdapter != null)
                mAdapter.SetOnItemClickListener(new AssignmentTestListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        // if (assignmentList.get(position).getAnswer() != null) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (!isRefreshing) {
                            Intent intent = new Intent(context, ReportListDetailActivity.class);
                            intent.putExtra(PreferenceHandler.LIST_ITEM, assignmentList.get(position));
                            intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                            startActivity(intent);
                            // }
                        }
                    }
                });
        }
    }

    public void onBackPressed() {
        if (getActivity() != null) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.popBackStack();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.hideKeyBoard(context);

    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}
