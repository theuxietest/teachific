package com.so.luotk.fragments.batches;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.so.luotk.R;
import com.so.luotk.activities.ListDetailActivity;
import com.so.luotk.models.video.DatumVideo;
import com.so.luotk.testmodule.ObTestActivity;
import com.so.luotk.activities.VideoListActivity;
import com.so.luotk.adapter.AssignmentTestListAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.FragmentAssignmentTestBinding;
import com.so.luotk.firebase.NotificationDataModel;
import com.so.luotk.models.output.Data;
import com.so.luotk.models.output.GetAssignmentTestListResponse;
import com.so.luotk.models.output.GetBatchFolderVideosResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AssignmentTestVideoListFragment extends Fragment {
    private FragmentAssignmentTestBinding binding;
    private static final String TAG = "StudeTet";
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";
    private static final String ARG_PARAM3 = "ARG_PARAM3";
    private static final String ARG_PARAM4 = "ARG_PARAM4";
    private RecyclerView mRrecylerView;
    private AssignmentTestListAdapter mAdapter;
    private SearchView searchView;
    private TextView tvNoResultFound;
    private APIInterface apiInterface;
    private ProgressView mProgressDialog;
    private String isFrom;
    private String courseId;
    private String batchId;
    private final String pageLength = "25";
    private int assignmentCount, testCount, videoCount;
    private ArrayList<Data> assignmentList;
    private ArrayList<Data> testList;
    private ArrayList<DatumVideo> folderList;
    private boolean isAssignmentLoading, isTestLoading, isVideoLoading;
    private int videopageNo = 1, assignmentPageNo = 1, testPageNo = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static final int REQUEST_UPDATE_SUBMIT_STATUS = 101;
    private View layoutDataView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Handler handler;
    private final Handler searchHandler = new Handler(Looper.myLooper());
    private Runnable runnable, searchRunnable;
    private boolean isFirstInternetToastDone;
    private View layout_empty_list, root_layout, divider_line;
    private ImageView empty_list_img, img_right_arrow;
    private List<NotificationDataModel> notificationDataList;
    private ProgressBar mProgressBar;
    private boolean isSearchOpen, isFragmentLoaded, isFromBatch;
    private String searchKey = "", sellingPrice;
    private Context context;
    private Activity mActivity;
    private boolean isRefreshing;
    private boolean onUpdate = false;
    public AssignmentTestVideoListFragment() {

    }

    public static AssignmentTestVideoListFragment newInstance(String isFrom, String batchId, boolean isFromBatch, String sellingPrice) {
        AssignmentTestVideoListFragment fragment = new AssignmentTestVideoListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, isFrom);
        args.putString(ARG_PARAM2, batchId);
        args.putBoolean(ARG_PARAM3, isFromBatch);
        args.putString(ARG_PARAM4, sellingPrice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isFrom = getArguments().getString(ARG_PARAM1);
            isFromBatch = getArguments().getBoolean(ARG_PARAM3);
            sellingPrice = getArguments().getString(ARG_PARAM4);
            if (isFromBatch)
                batchId = getArguments().getString(ARG_PARAM2);
            else
                courseId = getArguments().getString(ARG_PARAM2);

        }
        assignmentList = new ArrayList<>();
        testList = new ArrayList<>();
        folderList = new ArrayList<>();
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };

    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utilities.restrictScreenShot(getActivity());
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = FragmentAssignmentTestBinding.inflate(inflater, container, false);
        apiInterface = ApiUtils.getApiInterface();
        Utilities.hideKeyBoard(getContext());
        notificationDataList = PreferenceHandler.getNotificationDataList(context);
        tvNoResultFound = binding.tvNoMatch;
        mRrecylerView = binding.assignmentRecyclerView;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setAutoMeasureEnabled(false);
        mRrecylerView.setLayoutManager(mLayoutManager);
        mProgressBar = binding.searchDataProgress;
        layoutDataView = binding.layoutDataView;
        shimmerFrameLayout = binding.shimmerLayout;
        layout_empty_list = binding.layoutEmptyList;
        empty_list_img = binding.noAssignmentImg;
        root_layout = binding.getRoot();
        divider_line = binding.viewBelowToolbar;
        divider_line.setVisibility(GONE);
        handler = new Handler(Looper.myLooper());
        shimmerFrameLayout.startShimmer();
        searchView = binding.searchView;
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        return binding.getRoot();
    }

    private void setupUI() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                Utilities.hideKeyBoard(getContext());
                isSearchOpen = true;
                searchKey = newText;
                if (isFrom.equalsIgnoreCase("video")) {
                    if (mAdapter != null) {
                        mAdapter.getFilter().filter(newText);
                        if (mAdapter.getItemCount() < 1) {
                            tvNoResultFound.setVisibility(View.VISIBLE);
                            mRrecylerView.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(GONE);
                        } else {
                            tvNoResultFound.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            mRrecylerView.setVisibility(View.VISIBLE);
                        }
                        if (TextUtils.isEmpty(newText)) {
                            isSearchOpen = false;
                            searchKey = "";
                            Utilities.hideKeyBoard(context);
                            searchView.clearFocus();
                            tvNoResultFound.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            mRrecylerView.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    searchHandler.removeCallbacks(searchRunnable);
                    searchRunnable = new Runnable() {
                        @Override
                        public void run() {
                            hitApiWithSearch(newText);
                        }
                    };
                    searchHandler.postDelayed(searchRunnable, 400);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSearchOpen = true;
                searchKey = newText;
                if (isFrom.equalsIgnoreCase("video")) {
                    if (mAdapter != null) {
                        mAdapter.getFilter().filter(newText);
                        if (mAdapter.getItemCount() < 1) {
                            layout_empty_list.setVisibility(View.VISIBLE);
                            tvNoResultFound.setVisibility(View.VISIBLE);
                            empty_list_img.setVisibility(GONE);
                            tvNoResultFound.setText(R.string.no_result);
                            //swipeRefreshLayout.setVisibility(GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            mRrecylerView.setVisibility(GONE);
                        } else {
                            tvNoResultFound.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            mRrecylerView.setVisibility(View.VISIBLE);
                        }
                        if (TextUtils.isEmpty(newText)) {
                            isSearchOpen = false;
                            searchKey = "";
                            tvNoResultFound.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            mRrecylerView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    searchHandler.removeCallbacks(searchRunnable);
                    searchRunnable = new Runnable() {
                        @Override
                        public void run() {
                            hitApiWithSearch(newText);
                        }
                    };
                    searchHandler.postDelayed(searchRunnable, 400);
                }
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showInputMethod(view);
                }
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
                                hitGetAssignmentListService();
                            }
                        } else if (isFrom.equalsIgnoreCase("test")) {
                            if (isTestLoading) {
                                if (isFromBatch)
                                    hitGetTestListService();
                                else
                                    hitGetCourseTestListSevice();
                            }
                        } else {
                            Log.d(TAG, "onScrolled: " + isVideoLoading);
                            if (isVideoLoading) {
                                if (!isSearchOpen) {
                                    if (isFromBatch) {
                                        hitGetVideoListService("load");
                                    } else  {
                                        hitGetCourseVideoListService();
                                    }
                                }

                            }
                        }
                    }
                } else {
                    Utilities.makeToast(context, getString(R.string.internet_connection_error));
                }
            }

        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isSearchOpen = false;
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
                        hitGetAssignmentListService();
                    } else if (isFrom.equalsIgnoreCase("test")) {
                        testPageNo = 1;
                        if (testList.size() > 0) {
                            testList.clear();
                        }
                        if (isFromBatch)
                            hitGetTestListService();
                        else
                            hitGetCourseTestListSevice();
                    } else {
                        videopageNo = 1;
                        if (folderList.size() > 0) {
                            folderList.clear();
                        }
                        if (!isSearchOpen)
                            if (isFromBatch)
                                hitGetVideoListService("refresh");
                            else
                                hitGetCourseVideoListService();
                    }
                } else {
                    Utilities.makeToast(context, getString(R.string.internet_connection_error));
                }

            }
        });
    }


    private void hitApiWithSearch(String newText) {
        if (newText != null && !newText.isEmpty()) {
            isRefreshing = true;
            searchKey = newText;
            if (Utilities.checkInternet(context)) {
                if (isFrom.equalsIgnoreCase("assignment")) {
                    if (assignmentList.size() > 0) {
                        assignmentList.clear();
                    }
                    assignmentPageNo = 1;
                    hitGetAssignmentListService();
                } else if (isFrom.equalsIgnoreCase("test")) {
                    if (testList.size() > 0) {
                        testList.clear();
                    }
                    testPageNo = 1;
                    if (isFromBatch)
                        hitGetTestListService();
                    else
                        hitGetCourseTestListSevice();
                }
            } else {
                Utilities.makeToast(context, getString(R.string.internet_connection_error));
            }
        } else if (TextUtils.isEmpty(newText)) {
            if (!isRefreshing) {
                searchKey = "";
                searchView.clearFocus();
                if (isFrom.equalsIgnoreCase("assignment")) {
                    assignmentPageNo = 1;
                    if (assignmentList.size() > 0) {
                        assignmentList.clear();
                    }
                    hitGetAssignmentListService();
                } else if (isFrom.equalsIgnoreCase("test")) {
                    testPageNo = 1;
                    if (testList.size() > 0) {
                        testList.clear();
                    }

                    if (isFromBatch)
                        hitGetTestListService();
                    else
                        hitGetCourseTestListSevice();
                }
            }
        }
    }

    private void hitGetCourseTestListSevice() {
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
                    if (binding.loadMoreLay.getVisibility() == GONE) {
                        binding.loadMoreLay.setVisibility(VISIBLE);
                    }
                   /* if (!mProgressDialog.isShowing())
                        mProgressDialog.show();*/
                }
            }
        }
        if (testPageNo == 1) {
            testList.clear();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("courseId", courseId);
        map.put("pageLength", pageLength);
        map.put("page", testPageNo);
        map.put("search", searchKey);
        new MyClient(context).getCourseTests(map, (content, error) -> {
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (mProgressBar.getVisibility() == VISIBLE) {
                mProgressBar.setVisibility(GONE);
            }
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                binding.loadMoreLay.setVisibility(GONE);
            }
            if (shimmerFrameLayout != null && shimmerFrameLayout.isShimmerStarted()) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(GONE);
            }
            layoutDataView.setVisibility(View.VISIBLE);
            if (content != null) {
                GetAssignmentTestListResponse response = (GetAssignmentTestListResponse) content;
                if (response.getStatus() != null && response.getStatus().equals("200")) {
                    if (response.getResult() != null) {
                        if (response.getResult().getData() != null && (response.getResult().getData().size() > 0)) {
                            mRrecylerView.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            layout_empty_list.setVisibility(GONE);
                            tvNoResultFound.setVisibility(GONE);
                            testList.addAll(response.getResult().getData());
                            testCount = Integer.parseInt(response.getResult().getTotal());
                            setAssignmentTestListAdapter();
                            /*if (response.getResult().getData().size() < 25) {
                                isTestLoading = false;
                            } else if (response.getResult().getTotal().equalsIgnoreCase("25")) {
                                isTestLoading = false;
                            } else {
                                isTestLoading = true;
                                testPageNo++;
                            }*/

                        } else {
                            if (testPageNo == 1) {
                                if (!searchKey.isEmpty()) {
                                    layout_empty_list.setVisibility(View.VISIBLE);
                                    tvNoResultFound.setVisibility(View.VISIBLE);
                                    empty_list_img.setVisibility(GONE);
                                    tvNoResultFound.setText(R.string.no_result_found);
                                    //swipeRefreshLayout.setVisibility(GONE);
                                    swipeRefreshLayout.setRefreshing(false);
                                    mRrecylerView.setVisibility(GONE);
                                } else {
                                    layout_empty_list.setVisibility(View.VISIBLE);
                                    tvNoResultFound.setVisibility(View.VISIBLE);
                                    empty_list_img.setVisibility(VISIBLE);
                                    empty_list_img.setImageResource(R.drawable.test_assignment);
                                    tvNoResultFound.setText(R.string.test_assigned_will_appear);
                                    //swipeRefreshLayout.setVisibility(GONE);
                                    swipeRefreshLayout.setRefreshing(false);
                                    mRrecylerView.setVisibility(GONE);
                                }
                            } else isTestLoading = false;


                        }
                    }

                } else if (response.getStatus() != null && response.getStatus().equals("403")) {
                    Utilities.openUnauthorizedDialog(context);
                } else {
                    Utilities.makeToast(context, getString(R.string.server_error));
                }
            } else {
                Utilities.makeToast(context, getString(R.string.server_error));
            }

        });
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (binding.loadMoreLay.getVisibility() == VISIBLE) {
            binding.loadMoreLay.setVisibility(GONE);
        }
        /*try {
            if (isFromBatch) {
                boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                if (!isAdmn) {
                    if (BatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                        BatchDetailActivity.shimmerFrameLayout.stopShimmer();
                        BatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                        BatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                        BatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void checkInternet() {
        if (Utilities.checkInternet(context)) {
            if (runnable != null)
                handler.removeCallbacks(runnable);
            if (isFrom.equalsIgnoreCase("assignment")) {
                hitGetAssignmentListService();
            } else if (isFrom.equalsIgnoreCase("test")) {
                if (isFromBatch)
                    hitGetTestListService();
                else
                    hitGetCourseTestListSevice();
            } else {
                if (isFromBatch)
                    try {
                        if (PreferenceHandler.readString(getActivity(), PreferenceHandler.VIDEO_CACHING, null) != null) {
                            hitGetVideoListService("start");
                            /*GetBatchFolderVideosResponse videoCacheData = new Gson().fromJson(PreferenceHandler.readString(getActivity(), PreferenceHandler.VIDEO_CACHING, ""), GetBatchFolderVideosResponse.class);
                            Log.d("TAG", "CachedData: " + videoCacheData.getResult().getFolders().getData().size());
                                if (videoCacheData.getResult().getFolders().getData().size() > 0) {

                                    mRrecylerView.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setVisibility(View.VISIBLE);

                                    tvNoResultFound.setVisibility(GONE);

                                    folderList.addAll(videoCacheData.getResult().getFolders().getData());
                                    setAssignmentTestListAdapter();
                                    if (videoCacheData.getResult().getFolders().getData().size() < 25) {
                                        isVideoLoading = false;
                                    } else if (videoCacheData.getResult().getFolders().getTotal().equalsIgnoreCase("25")) {
                                        isVideoLoading = false;
                                    } else {
                                        isVideoLoading = true;
                                        videopageNo++;
                                    }
                                    Log.d("Running", "checkInternet: ");
                                } else {
                                    if (videopageNo == 1) {
                                        layout_empty_list.setVisibility(View.VISIBLE);
                                        tvNoResultFound.setVisibility(View.VISIBLE);
                                        empty_list_img.setImageResource(R.drawable.ic_no_video);
                                        tvNoResultFound.setText(R.string.video_shared_will_appear);
                                        //swipeRefreshLayout.setVisibility(GONE);
                                        swipeRefreshLayout.setRefreshing(false);
                                        mRrecylerView.setVisibility(GONE);
                                    } else isVideoLoading = false;
                                }
                                shimmerFrameLayout.setVisibility(GONE);
                                shimmerFrameLayout.stopShimmer();
                                layoutDataView.setVisibility(VISIBLE);*/

                        } else{
                            hitGetVideoListService("start");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                else hitGetCourseVideoListService();
            }
            isFragmentLoaded = true;
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Utilities.makeToast(context, getString(R.string.internet_connection_error));
                isFirstInternetToastDone = true;
            }

        }
    }

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }


    /*private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }*/

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (isFrom.equalsIgnoreCase("assignment")) {
                if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1 &&
                        (assignmentCount > 24)) {
                    assignmentPageNo++;
                    isAssignmentLoading = true;
                    return true;
                }
            } else if (isFrom.equalsIgnoreCase("test")) {
                if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1 &&
                        (testCount > 24)) {
                    testPageNo++;
                    isTestLoading = true;
                    return true;
                }
            } else if (isFrom.equalsIgnoreCase("video")) {
                if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1 &&
                        (videoCount > 24)) {
                    videopageNo++;
                    isVideoLoading = true;
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Method to set click listener on recycler view
     */
    private void setClickListener() {
        if (mAdapter != null)
            mAdapter.SetOnItemClickListener(new AssignmentTestListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    if (!isRefreshing) {
                        searchView.clearFocus();
                        String topicName = "";
                        Intent intent = null;
                        notificationDataList = PreferenceHandler.getNotificationDataList(context);
                        if (isFrom.equalsIgnoreCase("video")) {
                            topicName = folderList.get(position).getFolderName();
                            intent = new Intent(context, VideoListActivity.class);
                            intent.putExtra(PreferenceHandler.FOLDER_ID, folderList.get(position).getId());
                            intent.putExtra(PreferenceHandler.SELLING_PRICE, sellingPrice);
                            if (isFromBatch) {
                                intent.putExtra(PreferenceHandler.IS_FROM, "batch");
                                intent.putExtra(PreferenceHandler.BATCH_ID, batchId);
                            } else {
                                intent.putExtra(PreferenceHandler.IS_FROM, "course");
                                intent.putExtra(PreferenceHandler.COURSE_ID, courseId);
                            }
                            try {
                                if (notificationDataList != null && notificationDataList.size() > 0) {
                                    for (int i = 0; i < notificationDataList.size(); i++) {
                                        if (notificationDataList.get(i).getId() != null && notificationDataList.get(i).getId().equalsIgnoreCase(folderList.get(position).getId())) {
                                            intent.putExtra(PreferenceHandler.NOTIFICATION_TYPE, notificationDataList.get(i).getNotificationType());
                                            notificationDataList.remove(notificationDataList.get(i));
                                            folderList.get(position).setNewItem(false);
                                            mAdapter.notifyItemChanged(position);
                                            PreferenceHandler.setList(context, PreferenceHandler.NOTIFICATION_DATA, notificationDataList);
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //remove notification dot

                            intent.putExtra(PreferenceHandler.TOPIC_NAME, topicName);
                            startActivityForResult(intent, REQUEST_UPDATE_SUBMIT_STATUS);

                        } else if (isFrom.equalsIgnoreCase("test")) {
                            // startActivity(new Intent(context, ObTestActivity.class));
                            if (!(testList.get(position).getIs_locked() == 1)) {
                                try {
                                    if (notificationDataList != null && notificationDataList.size() > 0) {
                                        for (int i = 0; i < notificationDataList.size(); i++) {
                                            if (notificationDataList.get(i).getId() != null && notificationDataList.get(i).getId().equalsIgnoreCase(testList.get(position).getId())) {
                                                //  intent.putExtra(PreferenceHandler.NOTIFICATION_TYPE, notificationDataList.get(i).getNotificationType());
                                                notificationDataList.remove(notificationDataList.get(i));
                                                testList.get(position).setNewItem(false);
                                                mAdapter.notifyItemChanged(position);
                                                PreferenceHandler.setList(context, PreferenceHandler.NOTIFICATION_DATA, notificationDataList);
                                                break;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (testList.get(position).getTest_type() == 1) {
                                    topicName = testList.get(position).getTopic();
                                    String attachment = testList.get(position).getAttachment();
                                    String notes = testList.get(position).getNotes();
                                    intent = new Intent(mActivity, ListDetailActivity.class);
                                    intent.putExtra(PreferenceHandler.ATTACHMENT, attachment);
                                    intent.putExtra(PreferenceHandler.NOTES, notes);
                                    intent.putExtra(PreferenceHandler.DATE, testList.get(position).getSubmitDate());
                                    intent.putExtra(PreferenceHandler.TIME, testList.get(position).getSubmitTime());
                                    intent.putExtra(PreferenceHandler.ASSIGNMENT_ID, testList.get(position).getId());
                                    intent.putExtra(PreferenceHandler.STATUS, testList.get(position).getStatus());
                                    intent.putExtra(PreferenceHandler.TEST_DURATION, testList.get(position).getDuration());
                                    intent.putExtra(PreferenceHandler.IS_FROM_BATCH, isFromBatch);
                                    intent.putExtra(PreferenceHandler.BATCH_ID, batchId);
                                    intent.putExtra(PreferenceHandler.IS_FROM, isFrom);

                                    //remove notification dot

                                    intent.putExtra(PreferenceHandler.TOPIC_NAME, topicName);
                                    startActivityForResult(intent, REQUEST_UPDATE_SUBMIT_STATUS);
                                } else
                                    startActivityForResult(new Intent(context, ObTestActivity.class).putExtra(PreferenceHandler.TESTID, testList.get(position).getId()), REQUEST_UPDATE_SUBMIT_STATUS);

                            } else
                                Utilities.openContentLockedDialog(context, sellingPrice, courseId, "test");
                        } else {
                            topicName = assignmentList.get(position).getTopic();
                            String attachment = assignmentList.get(position).getAttachment();
                            String notes = assignmentList.get(position).getNotes();
                            intent = new Intent(mActivity, ListDetailActivity.class);
                            intent.putExtra(PreferenceHandler.ATTACHMENT, attachment);
                            intent.putExtra(PreferenceHandler.NOTES, notes);
                            intent.putExtra(PreferenceHandler.DATE, assignmentList.get(position).getSubmitDate());
                            intent.putExtra(PreferenceHandler.TIME, assignmentList.get(position).getSubmitTime());
                            intent.putExtra(PreferenceHandler.ASSIGNMENT_ID, assignmentList.get(position).getId());
                            intent.putExtra(PreferenceHandler.STATUS, assignmentList.get(position).getStatus());
                            intent.putExtra(PreferenceHandler.BATCH_ID, batchId);
                            intent.putExtra("position", ""+position);
//                            intent.putExtra("assignmentList", assignmentList);
                            /*Bundle args = new Bundle();
                            args.putSerializable("assignmentList",(Serializable)assignmentList);
                            intent.putExtra("assignmentBundle",args);*/
                            intent.putExtra(PreferenceHandler.IS_FROM, isFrom);

                            //remove notification dot
                            try {
                                if (notificationDataList != null && notificationDataList.size() > 0) {
                                    for (int i = 0; i < notificationDataList.size(); i++) {
                                        if (notificationDataList.get(i).getId() != null && notificationDataList.get(i).getId().equalsIgnoreCase(assignmentList.get(position).getId())) {
                                            intent.putExtra(PreferenceHandler.NOTIFICATION_TYPE, notificationDataList.get(i).getNotificationType());
                                            notificationDataList.remove(notificationDataList.get(i));
                                            assignmentList.get(position).setNewItem(false);
                                            mAdapter.notifyItemChanged(position);
                                            PreferenceHandler.setList(context, PreferenceHandler.NOTIFICATION_DATA, notificationDataList);
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            intent.putExtra(PreferenceHandler.TOPIC_NAME, topicName);
                            startActivityForResult(intent, REQUEST_UPDATE_SUBMIT_STATUS);
                        }

                    }
                }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFragmentLoaded) {
            setupUI();
            checkInternet();

        } else {
            /*if (isFrom.equalsIgnoreCase("video")) {
                Log.d("BatchFrom", "onResume: " + isFromBatch);
                if (!isFromBatch) {
                    hitGetCourseVideoListService();
                } else {
                    if (PreferenceHandler.readString(getActivity(), PreferenceHandler.VIDEO_CACHING, null) != null) {
                        hitGetVideoListService("resume");
                        *//*GetBatchFolderVideosResponse videoCacheData = new Gson().fromJson(PreferenceHandler.readString(getActivity(), PreferenceHandler.VIDEO_CACHING, ""), GetBatchFolderVideosResponse.class);
                        Log.d("onResume", "CachedData: " + videoCacheData.getResult().getFolders().getData().size());
                        if (videoCacheData.getResult().getFolders().getData().size() > 0) {

                            Log.d("TAG", "onResume:  call this");
                            mRrecylerView.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);

                            tvNoResultFound.setVisibility(GONE);
                            folderList.clear();
                            folderList.addAll(videoCacheData.getResult().getFolders().getData());
                            Log.d("VideoCount", "onResume: " + folderList.size());
                            setAssignmentTestListAdapter();
                            if (videoCacheData.getResult().getFolders().getData().size() < 25) {
                                isVideoLoading = false;
                            } else if (videoCacheData.getResult().getFolders().getTotal().equalsIgnoreCase("25")) {
                                isVideoLoading = false;
                            } else {
                                isVideoLoading = true;
                                videopageNo++;
                            }
                            Log.d("ResumeRunning", "checkInternet: ");
                        } else {
                            if (videopageNo == 1) {
                                layout_empty_list.setVisibility(View.VISIBLE);
                                tvNoResultFound.setVisibility(View.VISIBLE);
                                empty_list_img.setImageResource(R.drawable.ic_no_video);
                                tvNoResultFound.setText(R.string.video_shared_will_appear);
                                //swipeRefreshLayout.setVisibility(GONE);
                                swipeRefreshLayout.setRefreshing(false);
                                mRrecylerView.setVisibility(GONE);
                            } else isVideoLoading = false;
                        }*//*
                    } else{
                        hitGetVideoListService("resume");
                    }
                }

                shimmerFrameLayout.setVisibility(GONE);
                shimmerFrameLayout.stopShimmer();
                layoutDataView.setVisibility(VISIBLE);
            } else {
                Log.d(TAG, "onResume: Calling This");

                if (onUpdate) {
                    onUpdate = false;
                } else {
                    shimmerFrameLayout.setVisibility(GONE);
                    shimmerFrameLayout.stopShimmer();
                    setupUI();
                    if (mAdapter != null) {
                        mRrecylerView.setAdapter(mAdapter);
                    } else {
                        layout_empty_list.setVisibility(VISIBLE);
                    }
                    setAssignmentTestListAdapter();
                    layoutDataView.setVisibility(VISIBLE);
                }
            }*/

            if (onUpdate) {
                onUpdate = false;
            } else {
                shimmerFrameLayout.setVisibility(GONE);
                shimmerFrameLayout.stopShimmer();
                setupUI();
                if (mAdapter != null) {
                    mRrecylerView.setAdapter(mAdapter);
                } else {
                    layout_empty_list.setVisibility(VISIBLE);
                }
                setAssignmentTestListAdapter();
                layoutDataView.setVisibility(VISIBLE);
            }

        }
        Utilities.hideKeyBoard(getContext());

    }

    private void hitGetAssignmentListService() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        Call<GetAssignmentTestListResponse> call = apiInterface.getAssignmentList(headers, batchId, searchKey, pageLength, String.valueOf(assignmentPageNo));
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            mProgressBar.setVisibility(GONE);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        } else if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
            if (!searchKey.isEmpty()) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                if (assignmentPageNo > 1) {
                    mProgressDialog = new ProgressView(mActivity);
                    if (binding.loadMoreLay.getVisibility() == GONE) {
                        binding.loadMoreLay.setVisibility(VISIBLE);
                    }
                   /* if (!mProgressDialog.isShowing())
                        mProgressDialog.show();*/
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
                }
                if (mProgressBar.getVisibility() == VISIBLE) {
                    mProgressBar.setVisibility(GONE);
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                    binding.loadMoreLay.setVisibility(GONE);
                }
                if (shimmerFrameLayout != null && shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }

                layoutDataView.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null) {
                                if (response.body().getResult().getData() != null && (response.body().getResult().getData().size() > 0)) {
                                    mRrecylerView.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                                    tvNoResultFound.setVisibility(GONE);
                                    layout_empty_list.setVisibility(GONE);
                                    assignmentList.addAll(response.body().getResult().getData());
                                    assignmentCount = Integer.parseInt(response.body().getResult().getTotal());
                                    setAssignmentTestListAdapter();

                                    /*if (response.body().getResult().getData().size() < 25) {
                                        isAssignmentLoading = false;
                                    } else if (response.body().getResult().getTotal().equalsIgnoreCase("25")) {
                                        isAssignmentLoading = false;
                                    } else {
                                        isAssignmentLoading = true;
                                        assignmentPageNo++;
                                    }*/
                                } else {
                                    if (assignmentPageNo == 1) {
                                        if (!searchKey.isEmpty()) {
                                            layout_empty_list.setVisibility(View.VISIBLE);
                                            tvNoResultFound.setVisibility(View.VISIBLE);
                                            empty_list_img.setVisibility(GONE);
                                            tvNoResultFound.setText(R.string.no_result);
                                            //swipeRefreshLayout.setVisibility(GONE);
                                            swipeRefreshLayout.setRefreshing(false);
                                            mRrecylerView.setVisibility(GONE);
                                        } else {
                                            layout_empty_list.setVisibility(View.VISIBLE);
                                            tvNoResultFound.setVisibility(View.VISIBLE);
                                            empty_list_img.setVisibility(View.VISIBLE);
                                            empty_list_img.setImageResource(R.drawable.test_assignment);
                                            tvNoResultFound.setText(R.string.assignment_will_appear_here);
                                            //swipeRefreshLayout.setVisibility(GONE);
                                            swipeRefreshLayout.setRefreshing(false);
                                            mRrecylerView.setVisibility(GONE);
                                        }

                                    } else isAssignmentLoading = false;
                                }
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(mActivity);
                    } else {
                        Utilities.makeToast(context, getString(R.string.server_error));
                    }
                }
                /*try {
                    if (isFromBatch) {
                        boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                        if (!isAdmn) {
                            if (BatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                                BatchDetailActivity.shimmerFrameLayout.stopShimmer();
                                BatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                                BatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                                BatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void onFailure(Call<GetAssignmentTestListResponse> call, Throwable t) {
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (mProgressBar.getVisibility() == VISIBLE) {
                    mProgressBar.setVisibility(GONE);
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                    binding.loadMoreLay.setVisibility(GONE);
                }
                if (shimmerFrameLayout != null && shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }
                /*try {
                    if (isFromBatch) {
                        boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                        if (!isAdmn) {
                            if (BatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                                BatchDetailActivity.shimmerFrameLayout.stopShimmer();
                                BatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                                BatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                                BatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                layoutDataView.setVisibility(View.VISIBLE);
                Utilities.makeToast(context, getString(R.string.server_error));
            }
        });
    }


    private void hitGetTestListService() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        Call<GetAssignmentTestListResponse> call = apiInterface.getTestList(headers, batchId, searchKey, pageLength, String.valueOf(testPageNo));
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
                    mProgressDialog = new ProgressView(mActivity);
                    if (binding.loadMoreLay.getVisibility() == GONE) {
                        binding.loadMoreLay.setVisibility(VISIBLE);
                    }
                   /* if (!mProgressDialog.isShowing())
                        mProgressDialog.show();*/
                }
            }
        }
        if (testPageNo == 1) {
            testList.clear();
        }
        call.enqueue(new Callback<GetAssignmentTestListResponse>() {
            @Override
            public void onResponse(Call<GetAssignmentTestListResponse> call, Response<GetAssignmentTestListResponse> response) {
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (mProgressBar.getVisibility() == VISIBLE) {
                    mProgressBar.setVisibility(GONE);
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (shimmerFrameLayout != null && shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }
                layoutDataView.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null) {
                                if (response.body().getResult().getData() != null && (response.body().getResult().getData().size() > 0)) {
                                    mRrecylerView.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                                    layout_empty_list.setVisibility(GONE);
                                    tvNoResultFound.setVisibility(GONE);

                                    testList.addAll(response.body().getResult().getData());
                                    testCount = Integer.parseInt(response.body().getResult().getTotal());
                                    setAssignmentTestListAdapter();
                                    /*if (response.body().getResult().getData().size() < 25) {
                                        isTestLoading = false;
                                    } else if (response.body().getResult().getTotal().equalsIgnoreCase("25")) {
                                        isTestLoading = false;
                                    } else {
                                        isTestLoading = true;
                                        testPageNo++;
                                    }*/

                                } else {
                                    if (testPageNo == 1) {
                                        if (!searchKey.isEmpty()) {
                                            layout_empty_list.setVisibility(View.VISIBLE);
                                            tvNoResultFound.setVisibility(View.VISIBLE);
                                            empty_list_img.setVisibility(GONE);
                                            tvNoResultFound.setText(R.string.no_result_found);
                                            //swipeRefreshLayout.setVisibility(GONE);
                                            swipeRefreshLayout.setRefreshing(false);
                                            mRrecylerView.setVisibility(GONE);
                                        } else {
                                            layout_empty_list.setVisibility(View.VISIBLE);
                                            tvNoResultFound.setVisibility(View.VISIBLE);
                                            empty_list_img.setVisibility(VISIBLE);
                                            empty_list_img.setImageResource(R.drawable.test_assignment);
                                            tvNoResultFound.setText(R.string.test_assigned_will_appear);
                                            //swipeRefreshLayout.setVisibility(GONE);
                                            swipeRefreshLayout.setRefreshing(false);
                                            mRrecylerView.setVisibility(GONE);
                                        }
                                    } else isTestLoading = false;

                                }
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(context);
                    } else {
                        Utilities.makeToast(context, getString(R.string.server_error));
                    }

                    if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                        binding.loadMoreLay.setVisibility(GONE);
                    }
                    /*try {
                        if (isFromBatch) {
                            boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                            if (!isAdmn) {
                                if (BatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                                    BatchDetailActivity.shimmerFrameLayout.stopShimmer();
                                    BatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                                    BatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                                    BatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }


            }

            @Override
            public void onFailure(Call<GetAssignmentTestListResponse> call, Throwable t) {
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (mProgressBar.getVisibility() == VISIBLE) {
                    mProgressBar.setVisibility(GONE);
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                    binding.loadMoreLay.setVisibility(GONE);
                }
                if (shimmerFrameLayout != null && shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }
                layoutDataView.setVisibility(View.VISIBLE);
                Utilities.makeToast(context, getString(R.string.server_error));
            }

        });
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (binding.loadMoreLay.getVisibility() == VISIBLE) {
            binding.loadMoreLay.setVisibility(GONE);
        }
        /*try {
            if (isFromBatch) {
                boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                if (!isAdmn) {
                    if (BatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                        BatchDetailActivity.shimmerFrameLayout.stopShimmer();
                        BatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                        BatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                        BatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void hitGetCourseVideoListService() {
        Map<String, Object> querymap = new HashMap<>();
        querymap.put("courseId", courseId);
        querymap.put("pageLength", pageLength);
        querymap.put("page", videopageNo);
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            mProgressBar.setVisibility(GONE);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        } else if (!swipeRefreshLayout.isRefreshing()) {
            if (videopageNo > 1) {
                mProgressDialog = new ProgressView(context);
                if (binding.loadMoreLay.getVisibility() == GONE) {
                    binding.loadMoreLay.setVisibility(VISIBLE);
                }
               /* if (!mProgressDialog.isShowing())
                    mProgressDialog.show();*/
            }
        }
        if (videopageNo == 1) {
            folderList.clear();
        }
        new MyClient(context).getCourseVideos(querymap, (content, error) -> {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            } else {
                if (videopageNo > 1) {
                    if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                        binding.loadMoreLay.setVisibility(GONE);
                    }
                    mProgressDialog.dismiss();

                } else {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }
            }
            layoutDataView.setVisibility(View.VISIBLE);
            if (content != null) {
                GetBatchFolderVideosResponse response = (GetBatchFolderVideosResponse) content;
                if (response.getStatus() != null && response.getStatus().equalsIgnoreCase("200")) {
                    if (response.getSuccess().equalsIgnoreCase("true")) {
                        if (response.getResult() != null) {
                            if (response.getResult().getFolders().getData().size() > 0) {
                                mRrecylerView.setVisibility(View.VISIBLE);
                                //swipeRefreshLayout.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setRefreshing(false);
                                tvNoResultFound.setVisibility(GONE);
                                folderList.addAll(response.getResult().getFolders().getData());
                                videoCount = Integer.parseInt(response.getResult().getFolders().getTotal());
                                setAssignmentTestListAdapter();
                                /*if (response.getResult().getFolders().getData().size() < 25) {
                                    isVideoLoading = false;
                                } else if (response.getResult().getFolders().getTotal().equalsIgnoreCase("25")) {
                                    isVideoLoading = false;
                                } else {
                                    isVideoLoading = true;
                                    videopageNo++;
                                }*/

                            } else {
                                if (videopageNo == 1) {
                                    layout_empty_list.setVisibility(View.VISIBLE);
                                    tvNoResultFound.setVisibility(View.VISIBLE);
                                    empty_list_img.setVisibility(View.VISIBLE);
                                    empty_list_img.setImageResource(R.drawable.ic_no_video);
                                    tvNoResultFound.setText(R.string.video_shared_will_appear);
                                    //swipeRefreshLayout.setVisibility(GONE);
                                    swipeRefreshLayout.setRefreshing(false);
                                    mRrecylerView.setVisibility(GONE);
                                } else isVideoLoading = false;
                            }
                        }
                    }
                } else if (response.getStatus() != null && response.getStatus().equalsIgnoreCase("403")) {
                    Utilities.openUnauthorizedDialog(context);
                }
                else {
                    Utilities.makeToast(context, getString(R.string.server_error));
                }

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                /*try {
                    if (isFromBatch) {
                        boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                        if (!isAdmn) {
                            if (BatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                                BatchDetailActivity.shimmerFrameLayout.stopShimmer();
                                BatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                                BatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                                BatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            } else {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (videopageNo > 1) {
                        if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                            binding.loadMoreLay.setVisibility(GONE);
                        }
                        mProgressDialog.dismiss();
                    } else {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(GONE);
                    }
                }
                /*try {
                    if (isFromBatch) {
                        boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                        if (!isAdmn) {
                            if (BatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                                BatchDetailActivity.shimmerFrameLayout.stopShimmer();
                                BatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                                BatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                                BatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                Utilities.makeToast(context, getString(R.string.server_error));
            }
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            /*try {
                if (isFromBatch) {
                    boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                    if (!isAdmn) {
                        if (BatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                            BatchDetailActivity.shimmerFrameLayout.stopShimmer();
                            BatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                            BatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                            BatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        });
    }

    private void hitGetVideoListService(String from) {
        Log.d(TAG, "hitGetVideoListServiceLoad: " + from);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        Call<GetBatchFolderVideosResponse> call = apiInterface.getBatchFolderList(headers, batchId, pageLength, String.valueOf(videopageNo));
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            Log.d(TAG, "hitGetVideoListService11: ");
            mProgressBar.setVisibility(GONE);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
            Log.d(TAG, "hitGetVideoListService22: ");
            mProgressDialog.dismiss();
        } else if (!swipeRefreshLayout.isRefreshing()) {
            Log.d(TAG, "hitGetVideoListService33: ");
            if (videopageNo > 1) {
                mProgressDialog = new ProgressView(context);
                if (binding.loadMoreLay.getVisibility() == GONE) {
                    binding.loadMoreLay.setVisibility(VISIBLE);
                }
             /*   if (!mProgressDialog.isShowing())
                    mProgressDialog.show();*/
            }
        }
        if (videopageNo == 1) {
            folderList.clear();
        }
        call.enqueue(new Callback<GetBatchFolderVideosResponse>() {
            @Override
            public void onResponse(Call<GetBatchFolderVideosResponse> call, Response<GetBatchFolderVideosResponse> response) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (videopageNo > 1) {
                        if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                            binding.loadMoreLay.setVisibility(GONE);
                        }
                        mProgressDialog.dismiss();
                    } else {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(GONE);
                    }
                }
                layoutDataView.setVisibility(View.VISIBLE);

                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null && response.body().getResult() != null) {
                                if (response.body().getResult().getFolders().getData().size() > 0) {

                                    // Caching Video List
                                    if (isFromBatch) {
                                        String videoListJson = new Gson().toJson(response.body());
                                        PreferenceHandler.writeString(getActivity(), PreferenceHandler.VIDEO_CACHING, videoListJson);
                                    }

                                    mRrecylerView.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setVisibility(View.VISIBLE);

                                    tvNoResultFound.setVisibility(GONE);

                                    if (from.equals("refresh")) {
                                        folderList.clear();
                                    }
                                    folderList.addAll(response.body().getResult().getFolders().getData());
                                    videoCount = Integer.parseInt(response.body().getResult().getFolders().getTotal());
                                    setAssignmentTestListAdapter();
                                   /* if (response.body().getResult().getFolders().getData().size() < 25) {
                                        isVideoLoading = false;
                                    } else if (response.body().getResult().getFolders().getTotal().equalsIgnoreCase("25")) {
                                        isVideoLoading = false;
                                    } else {
                                        isVideoLoading = true;
                                        videopageNo++;
                                    }*/
                                } else {
                                    if (videopageNo == 1) {
                                        layout_empty_list.setVisibility(View.VISIBLE);
                                        tvNoResultFound.setVisibility(View.VISIBLE);
                                        empty_list_img.setVisibility(View.VISIBLE);
                                        empty_list_img.setImageResource(R.drawable.ic_no_video);
                                        tvNoResultFound.setText(R.string.video_shared_will_appear);
                                        //swipeRefreshLayout.setVisibility(GONE);
                                        swipeRefreshLayout.setRefreshing(false);
                                        mRrecylerView.setVisibility(GONE);
                                    } else isVideoLoading = false;
                                }
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(context);
                    } else {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            if (videopageNo > 1) {
                                if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                                    binding.loadMoreLay.setVisibility(GONE);
                                }
                                mProgressDialog.dismiss();
                            } else {
                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(GONE);
                            }
                        }
                        layoutDataView.setVisibility(View.VISIBLE);
                        Utilities.makeToast(context, getString(R.string.server_error));
                    }
                }
                /*try {
                    if (isFromBatch) {
                        boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                        if (!isAdmn) {
                            if (BatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                                BatchDetailActivity.shimmerFrameLayout.stopShimmer();
                                BatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                                BatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                                BatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            }

            @Override
            public void onFailure(Call<GetBatchFolderVideosResponse> call, Throwable t) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (videopageNo > 1) {
                        if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                            binding.loadMoreLay.setVisibility(GONE);
                        }
                        mProgressDialog.dismiss();
                    } else {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(GONE);
                    }
                }
                /*try {
                    if (isFromBatch) {
                        boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                        if (!isAdmn) {
                            if (BatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                                BatchDetailActivity.shimmerFrameLayout.stopShimmer();
                                BatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                                BatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                                BatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                layoutDataView.setVisibility(View.VISIBLE);
                Utilities.makeToast(context, getString(R.string.server_error));
            }
        });
    }

    private void setAssignmentTestListAdapter() {
        if (isFrom.equalsIgnoreCase("video")) {
            if (folderList != null && folderList.size() > 0) {
                layout_empty_list.setVisibility(GONE);
                Set<DatumVideo> set = new LinkedHashSet<>(folderList);
                folderList.clear();
                folderList.addAll(set);
                if (mAdapter == null) {
                    mRrecylerView.setVisibility(View.VISIBLE);
                    mAdapter = new AssignmentTestListAdapter(context, folderList, true);
                    mRrecylerView.setAdapter(mAdapter);
                } else {

                    mAdapter.setUpdatedList(folderList, true);
                    /*mRrecylerView.setVisibility(View.VISIBLE);
                    mAdapter = new AssignmentTestListAdapter(context, folderList, true);
                    mRrecylerView.setAdapter(mAdapter);*/
                }
                setVideoNotificationView();
            } else {
                layout_empty_list.setVisibility(VISIBLE);
                tvNoResultFound.setVisibility(View.VISIBLE);
                empty_list_img.setVisibility(View.VISIBLE);
                empty_list_img.setImageResource(R.drawable.ic_no_video);
                tvNoResultFound.setText(R.string.video_shared_will_appear);
                //swipeRefreshLayout.setVisibility(GONE);
                swipeRefreshLayout.setRefreshing(false);
                mRrecylerView.setVisibility(GONE);
            }
        } else if (isFrom.equalsIgnoreCase("test")) {
            if (testList != null && testList.size() > 0) {
                Set<Data> set = new LinkedHashSet<>(testList);
                testList.clear();
                testList.addAll(set);
                if (mAdapter == null) {
                    mRrecylerView.setVisibility(View.VISIBLE);
                    mAdapter = new AssignmentTestListAdapter(context, testList, false, false);
                    mRrecylerView.setAdapter(mAdapter);
                } else {
                    // mRrecylerView.setAdapter(mAdapter);
//                    mAdapter.notifyDataSetChanged();
                    mAdapter.setUpdatedDataList(testList, false);
                }
                setTestNotificationView();
            } else {
                layout_empty_list.setVisibility(VISIBLE);
                tvNoResultFound.setVisibility(View.VISIBLE);
                empty_list_img.setVisibility(VISIBLE);
                empty_list_img.setImageResource(R.drawable.test_assignment);
                tvNoResultFound.setText(R.string.test_assigned_will_appear);
                //swipeRefreshLayout.setVisibility(GONE);
                swipeRefreshLayout.setRefreshing(false);
                mRrecylerView.setVisibility(GONE);
            }
        } else {
            if (assignmentList != null && assignmentList.size() > 0) {
                Set<Data> set = new LinkedHashSet<>(assignmentList);
                assignmentList.clear();
                assignmentList.addAll(set);
                if (mAdapter == null) {
                    mRrecylerView.setVisibility(View.VISIBLE);
                    mAdapter = new AssignmentTestListAdapter(context, assignmentList, true, false);
                    mRrecylerView.setAdapter(mAdapter);
                } else {
                    // mRrecylerView.setAdapter(mAdapter);
                    mAdapter.setUpdatedDataList(assignmentList, true);
                }
                setAssignmentNotificationView();
            } else {
                layout_empty_list.setVisibility(VISIBLE);
                tvNoResultFound.setVisibility(VISIBLE);
                empty_list_img.setVisibility(View.VISIBLE);
                empty_list_img.setImageResource(R.drawable.test_assignment);
                tvNoResultFound.setText(R.string.assignment_will_appear_here);
                //swipeRefreshLayout.setVisibility(GONE);
                swipeRefreshLayout.setRefreshing(false);
                mRrecylerView.setVisibility(GONE);
            }
        }
        isRefreshing = false;
        if (mAdapter != null)

            setClickListener();

    }

    private void setAssignmentNotificationView() {
        if (notificationDataList != null && notificationDataList.size() > 0) {
            int bigList = assignmentList.size() > notificationDataList.size() ? 1 : 0;
            if (bigList == 1) {
                for (int i = 0; i < notificationDataList.size(); i++) {
                    for (int j = 0; j < assignmentList.size(); j++) {
                        if (notificationDataList.get(i).getId() != null && notificationDataList.get(i).getId().equalsIgnoreCase(assignmentList.get(j).getId())) {
                            assignmentList.get(j).setNewItem(true);
                            //  mAdapter.notifyItemChanged(j);

                            // mAdapter.notifyItemRangeChanged(j, assignmentList.size() - 1);
                        }
                    }
                }
            } else {
                for (int i = 0; i < assignmentList.size(); i++) {
                    for (int j = 0; j < notificationDataList.size(); j++) {
                        if (notificationDataList.get(j).getId() != null && notificationDataList.get(j).getId().equalsIgnoreCase(assignmentList.get(i).getId())) {
                            assignmentList.get(i).setNewItem(true);
                            // mAdapter.notifyItemChanged(i);
                            //mAdapter.setUpdatedDataList(assignmentList, true);
                            // mAdapter.notifyItemRangeChanged(i, assignmentList.size() - 1);
                        }
                    }
                }
            }

        }
        mAdapter.setUpdatedDataList(assignmentList, true);
    }

    private void setTestNotificationView() {
        if (notificationDataList != null && notificationDataList.size() > 0) {
            int bigList = testList.size() > notificationDataList.size() ? 1 : 0;
            if (bigList == 1) {
                for (int i = 0; i < notificationDataList.size(); i++) {
                    for (int j = 0; j < testList.size(); j++) {
                        if (notificationDataList.get(i).getId() != null && notificationDataList.get(i).getId().equalsIgnoreCase(testList.get(j).getId())) {
                            testList.get(j).setNewItem(true);
                            // mAdapter.notifyItemChanged(j);
                            //  mAdapter.notifyItemRangeChanged(j, testList.size() - 1);
                            //    mAdapter.setUpdatedDataList(testList, false);

                        }
                    }
                }
            } else {
                for (int i = 0; i < testList.size(); i++) {
                    for (int j = 0; j < notificationDataList.size(); j++) {
                        if (notificationDataList.get(j).getId() != null && notificationDataList.get(j).getId().equalsIgnoreCase(testList.get(i).getId())) {
                            testList.get(i).setNewItem(true);
                            // mAdapter.notifyItemChanged(i);
                            // mAdapter.notifyItemRangeChanged(i, testList.size() - 1);

                        }
                    }
                }
            }

        }
        mAdapter.setUpdatedDataList(testList, false);
    }

    private void setVideoNotificationView() {
        if (notificationDataList != null && notificationDataList.size() > 0) {
            int bigList = folderList.size() > notificationDataList.size() ? 1 : 0;
            if (bigList == 1) {
                for (int i = 0; i < notificationDataList.size(); i++) {
                    for (int j = 0; j < folderList.size(); j++) {
                        if (notificationDataList.get(i).getId() != null && notificationDataList.get(i).getId().equalsIgnoreCase(folderList.get(j).getId())) {
                            folderList.get(j).setNewItem(true);
                            // mAdapter.notifyItemChanged(j);
                            // mAdapter.notifyItemRangeChanged(j, folderList.size() - 1);

                        }
                    }
                }
            } else {
                for (int i = 0; i < folderList.size(); i++) {
                    for (int j = 0; j < notificationDataList.size(); j++) {
                        if (notificationDataList.get(j).getId() != null && notificationDataList.get(j).getId().equalsIgnoreCase(folderList.get(i).getId())) {
                            folderList.get(i).setNewItem(true);
                            // mAdapter.notifyItemChanged(i);
                            //   mAdapter.notifyItemRangeChanged(i, folderList.size() - 1);
                            // mAdapter.setUpdatedList(folderList);
                        }
                    }
                }
            }

        }
        mAdapter.setUpdatedList(folderList, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_UPDATE_SUBMIT_STATUS) {
            if (data != null) {
                boolean isDataSubmitted = data.getBooleanExtra(PreferenceHandler.IS_DATA_SUBMITTED, false);
                if (isDataSubmitted) {
                    String isFromIntent = data.getStringExtra(PreferenceHandler.IS_FROM);
                    if (Utilities.checkInternet(getContext())) {
                        if (isFromIntent.equalsIgnoreCase("assignment")) {
                            assignmentPageNo = 1;
                            if (assignmentList.size() > 0) {
                                assignmentList.clear();
                            }
                            hitGetAssignmentListService();
                        } else if (isFromIntent.equalsIgnoreCase("test")) {
                            testPageNo = 1;
                            onUpdate = true;
                            /*if (testList.size() > 0) {
                                testList.clear();
                            }*/
                            layoutDataView.setVisibility(GONE);
                            layout_empty_list.setVisibility(GONE);
                            shimmerFrameLayout.setVisibility(VISIBLE);
                            shimmerFrameLayout.startShimmer();
                            if (isFromBatch)
                                hitGetTestListService();
                            else
                                hitGetCourseTestListSevice();
                        }
                    } else {
                        Utilities.makeToast(context, getString(R.string.internet_connection_error));

                    }
                }
            }
        } /*else {
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
