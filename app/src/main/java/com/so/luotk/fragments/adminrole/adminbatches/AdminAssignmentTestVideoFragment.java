package com.so.luotk.fragments.adminrole.adminbatches;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.so.luotk.R;
import com.so.luotk.activities.ListDetailActivity;
import com.so.luotk.activities.VideoListActivity;
import com.so.luotk.activities.adminrole.AddFolderLinkActivity;
import com.so.luotk.activities.adminrole.SelectStudentsActivity;
import com.so.luotk.adapter.AssignmentTestListAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.AddStudentDialogFragment;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.CustomAddLinkFolderDialogBinding;
import com.so.luotk.databinding.CustomDialogAddVideoBinding;
import com.so.luotk.databinding.FragmentAdminAssignmentTestVideoBinding;
import com.so.luotk.firebase.NotificationDataModel;
import com.so.luotk.models.output.Data;
import com.so.luotk.models.output.GetAssignmentTestListResponse;
import com.so.luotk.models.output.GetBatchFolderVideosResponse;
import com.so.luotk.models.output.GetBatchSubmitAssignmentTestResponse;
import com.so.luotk.models.output.GetStudentDataResponse;
import com.so.luotk.models.video.DatumVideo;
import com.so.luotk.testmodule.ObTestActivity;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminAssignmentTestVideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminAssignmentTestVideoFragment extends Fragment {

    public static final int FOLDER_ADD_STATUS = 1234;
    public static final int FOLDER_EDIT_STATUS = 1235;
    private static final String TAG = "AdminAssit";
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String ARG_PARAM2 = "ARG_PARAM2";
    private static final String ARG_PARAM3 = "ARG_PARAM3";
    private static final String ARG_PARAM4 = "ARG_PARAM4";
    private TextView tvNoResultFound;
    private ArrayList<DatumVideo> folderList;
    private RecyclerView mRrecylerView;
    private AssignmentTestListAdapter mAdapter;
    private SearchView searchView;
    private String mParam2;
    private APIInterface apiInterface;
    private ProgressView mProgressDialog;
    private String isFrom;
    private String courseId;
    private String batchId;
    private final String pageLength = "25";
    private ArrayList<Data> assignmentList;
    private ArrayList<Data> testList;
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
    private View layout_empty_list, root_layout;
    private ImageView empty_list_img, img_right_arrow;
    private List<NotificationDataModel> notificationDataList;
    private ProgressBar mProgressBar;
    private boolean isSearchOpen, isFragmentLoaded, isFromBatch;
    private String searchKey = "", sellingPrice;
    private Context context;
    private Activity mActivity;
    private boolean isRefreshing;
    AddStudentDialogFragment myDialogFragment;
    private boolean onUpdate = false;
    private int assignmentCount, testCount, videoCount;
    private FragmentAdminAssignmentTestVideoBinding binding;

    public AdminAssignmentTestVideoFragment() {
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

    public static AdminAssignmentTestVideoFragment newInstance(String isFrom, String batchId, boolean isFromBatch, String sellingPrice) {
        AdminAssignmentTestVideoFragment fragment = new AdminAssignmentTestVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, isFrom);
        args.putString(ARG_PARAM2, batchId);
        args.putBoolean(ARG_PARAM3, isFromBatch);
        args.putString(ARG_PARAM4, sellingPrice);
        fragment.setArguments(args);
        return fragment;
    }

    public static AdminAssignmentTestVideoFragment newInstance(String param1, String param2) {
        AdminAssignmentTestVideoFragment fragment = new AdminAssignmentTestVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static AdminAssignmentTestVideoFragment newInstance(String param1) {
        AdminAssignmentTestVideoFragment fragment = new AdminAssignmentTestVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Utilities.restrictScreenShot(context);
        binding = FragmentAdminAssignmentTestVideoBinding.inflate(getLayoutInflater(), container, false);

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
        handler = new Handler(Looper.myLooper());
        shimmerFrameLayout.startShimmer();
        searchView = binding.searchView;
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        setClickListeners();
        return binding.getRoot();
    }

    private void setClickListeners() {
        binding.btnAddFirstAssignment.setOnClickListener(view -> {
            if (!isFrom.equalsIgnoreCase("video")) {
                checkStudent();
            } else {
                Intent in = new Intent(context, AddFolderLinkActivity.class);
                in.putExtra(PreferenceHandler.BATCH_ID, batchId);
                in.putExtra("isLink", "false");
                in.putExtra("editable", "false");
                in.putExtra("fromWhere", "video");
                startActivityForResult(in, FOLDER_ADD_STATUS);
//                showAddLinkDialog(false);
            }

        });
    }

    public void checkStudent() {
        mProgressDialog = new ProgressView((context));
        mProgressDialog.show();
        new MyClient(context).hitGetStudentListService(searchKey, batchId, pageLength, 1, (content, error) -> {
            GetStudentDataResponse response = (GetStudentDataResponse) content;
            if (content != null) {
                if (response.getStatus() == 200) {
                    if (response.getResult() != null) {
                        if (response.getResult().getData() != null && (response.getResult().getData().size() > 0)) {
                            if (response.getResult().getTotal() > 0) {
                                Intent intent = new Intent(getContext(), SelectStudentsActivity.class);
                                intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                intent.putExtra("title", "Create Assignment");
                                intent.putExtra(PreferenceHandler.BATCH_ID, batchId);
                                intent.putExtra("assignmentData", "");
                                intent.putExtra("edit", "false");
                                startActivityForResult(intent, REQUEST_UPDATE_SUBMIT_STATUS);
                            } else {
                                showAlertDialogButtonClicked();
//                                Toast.makeText(context, "No Student Available", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            showAlertDialogButtonClicked();
//                            Toast.makeText(context, "No Student Available", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (response.getStatus() == 403) {
                    Utilities.openUnauthorizedDialog(context);
                } else {
                    Utilities.makeToast(context, getString(R.string.server_error));
                }


            } else {
                Utilities.makeToast(context, getString(R.string.server_error));
            }
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        });
    }

    public void showAlertDialogButtonClicked() {

        myDialogFragment = new AddStudentDialogFragment(context);

        Bundle bundle = new Bundle();
        bundle.putBoolean("studentAlertDialog", true);
        myDialogFragment.setArguments(bundle);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);


        myDialogFragment.show(ft, "dialog");
    }

    private void showDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_add_video, null, false);
        CustomDialogAddVideoBinding dialogBinding = CustomDialogAddVideoBinding.bind(view);
        Dialog dialog = new Dialog(getContext());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_corners_drawable);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        dialogBinding.tvAddLink.setVisibility(GONE);
        dialogBinding.tvAddLink.setOnClickListener(view1 -> {
            dialog.dismiss();
            showAddLinkDialog(true);
        });
        dialogBinding.tvAddFolder.setOnClickListener(view1 -> {
            dialog.dismiss();
            showAddLinkDialog(false);
        });
    }

    private void showAddLinkDialog(boolean isLink) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_add_link_folder_dialog, null, false);
        CustomAddLinkFolderDialogBinding dialogBinding = CustomAddLinkFolderDialogBinding.bind(view);
        Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_corners_drawable);
        if (!isLink) {
            dialogBinding.textAddLink.setText("Add new folder");
            dialogBinding.edtEnterLink.setHint("Enter folder name");
            dialogBinding.edtLinkFolderName.setVisibility(GONE);
        }
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        dialog.getWindow().setLayout(800, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialogBinding.txtCancel.setOnClickListener(view1 -> {
            Utilities.hideKeyboardFrom(getContext(), dialogBinding.edtEnterLink);
            dialog.dismiss();

        });
        dialogBinding.edtEnterLink.requestFocus();
        dialogBinding.txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(dialogBinding.edtEnterLink.getText().toString())) {
                    createLinkFolder(dialog, dialogBinding.edtEnterLink.getText().toString(), isLink);
                } else {
                    Toast.makeText(context, "The field is required.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Utilities.openKeyboard(getContext(), dialogBinding.edtEnterLink);
    }

    private void createLinkFolder(Dialog dialog, String folderLinkName, boolean isLink) {
        if (Utilities.checkInternet(getContext())) {
            handler.removeCallbacks(runnable);
            if (batchId != null)
                hitSubmitAddVideoLink(folderLinkName, isLink, dialog);
            else {
                Toast.makeText(getContext(), "Invalid batch", Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(getContext(), getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }


    private void hitSubmitAddVideoLink(final String folderLinkName, final boolean isLink, final Dialog dialog) {
        try {
            Map<String, String> map = new HashMap<>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
            Call<GetBatchSubmitAssignmentTestResponse> call = null;

            if (!folderLinkName.isEmpty())
                map.put("folderName", folderLinkName);
            if (!batchId.isEmpty())
                map.put("fk_batchId", batchId);

            if (!isLink) {
                call = apiInterface.createNewVideoFolder(headers, map);
            }
            mProgressDialog = new ProgressView(context);
            mProgressDialog.show();
            call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
                @Override
                public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                    mProgressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                                if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                                    dialog.dismiss();
                                    Utilities.hideKeyBoard(getContext());
                                    videopageNo = 1;
                                    PreferenceHandler.writeString(context, PreferenceHandler.VIDEO_CACHING, null);
                                    checkInternet();
                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(context);
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(context, "406 error");
                            } else {
                                Utilities.makeToast(context, getString(R.string.server_error));
                            }       /*else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("402")) {
                                mProgressDialog.dismiss();
                            }*/
                        } else
                            Utilities.makeToast(context, getString(R.string.folder_not_created));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Utilities.makeToast(context, getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setUpUI() {
        if (isFrom.equalsIgnoreCase("test")) {
            binding.tvNoMatch.setText("Add your test");
            binding.batchTitle2.setText("Tap the button to add your first test");
            binding.batchTitle2.setVisibility(GONE);
            binding.btnAddFirstAssignment.setText("Create Test");
        }
        if (isFrom.equalsIgnoreCase("video")) {
            binding.noAssignmentImg.setImageResource(R.drawable.ic_no_video);
            binding.tvNoMatch.setText("You haven't uploaded any content yet");
            binding.batchTitle2.setText("Click on the  \"Add\" button to upload content");
            binding.batchTitle2.setVisibility(GONE);
            binding.btnAddFirstAssignment.setText("Add Folder");
        }


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
                            if (isVideoLoading) {
                                if (!isSearchOpen) {
                                    if (isFromBatch)
                                        hitGetVideoListService("load");
                                    else hitGetCourseVideoListService("load");
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
                                hitGetCourseVideoListService("refresh");
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
                /*    mProgressDialog = new ProgressView(context);
                    if (!mProgressDialog.isShowing())
                        mProgressDialog.show();*/

                    if (binding.loadMoreLay.getVisibility() == GONE) {
                        binding.loadMoreLay.setVisibility(VISIBLE);
                    }
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
            if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                binding.loadMoreLay.setVisibility(GONE);
            }
          /*  if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }*/
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
                                    tvNoResultFound.setText(R.string.test_will_appear_here);
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
        /*if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }*/
        if (binding.loadMoreLay.getVisibility() == VISIBLE) {
            binding.loadMoreLay.setVisibility(GONE);
        }
       /* try {
            boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
            if (isAdmn) {
                if (AdminBatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                    AdminBatchDetailActivity.shimmerFrameLayout.stopShimmer();
                    AdminBatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                    AdminBatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                    AdminBatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
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
                        if (PreferenceHandler.readString(context, PreferenceHandler.VIDEO_CACHING, null) != null) {
                            hitGetVideoListService("start");
                            /*GetBatchFolderVideosResponse videoCacheData = new Gson().fromJson(PreferenceHandler.readString(context, PreferenceHandler.VIDEO_CACHING, ""), GetBatchFolderVideosResponse.class);
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

                        } else {
                            hitGetVideoListService("start");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                else hitGetCourseVideoListService("start");
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
            mAdapter.SetOnEditClickListener(new AssignmentTestListAdapter.OnEditClickListener() {
                @Override
                public void onEditClick(int position, RelativeLayout threeDotsLay) {

                    Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                    PopupMenu popupMenu = new PopupMenu(wrapper, threeDotsLay);

                    // Inflating popup menu from popup_menu.xml file
                    popupMenu.getMenuInflater().inflate(R.menu.listing_setting, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            // Toast message on menu item clicked
                            if (menuItem.getItemId() == R.id.editTv) {
                                if (isFrom.equalsIgnoreCase("video")) {
                                    String folderName = folderList.get(position).getFolderName();
                                    String folderId = folderList.get(position).getId();

                                    Intent in = new Intent(context, AddFolderLinkActivity.class);
                                    in.putExtra(PreferenceHandler.BATCH_ID, batchId);
                                    in.putExtra("isLink", "false");
                                    in.putExtra("editable", "true");
                                    in.putExtra("fromWhere", "video");
                                    in.putExtra("folderId", folderId);
                                    in.putExtra("folderName", folderName);
                                    startActivityForResult(in, FOLDER_EDIT_STATUS);
//                    showAddLinkDialog(false);

//                   showUpdateLinkDialog(folderId, folderName);
                                } else if (isFrom.equalsIgnoreCase("assignment")) {
                                    Intent intent = new Intent(context, SelectStudentsActivity.class);
                                    intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                    intent.putExtra("title", "Edit Assignment");
                                    intent.putExtra(PreferenceHandler.BATCH_ID, batchId);
                                    intent.putExtra("assignmentData", new Gson().toJson(assignmentList.get(position)));
                                    intent.putExtra("edit", "true");
                                    startActivityForResult(intent, REQUEST_UPDATE_SUBMIT_STATUS);
                                }
                            } else if (menuItem.getItemId() == R.id.deleteTv) {
                                if (isFrom.equalsIgnoreCase("video")) {
                                    openConfirmDelete("folder", position);
                                } else if (isFrom.equalsIgnoreCase("assignment")) {
                                    openConfirmDelete("assignment",position);
                                }

//                                Toast.makeText(context, "You Clicked Write Feedback" + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                            }

                            return true;
                        }
                    });
                    // Showing the popup menu
                    popupMenu.show();

                }
            });
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
                        intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                        intent.putExtra("assignmentData", new Gson().toJson(assignmentList.get(position)));
                        intent.putExtra("assignmentGson", new Gson().toJson(assignmentList));
                        intent.putExtra("position", ""+position);

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

    private void openConfirmDelete(String fromWhere, int position) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View deleteDialogView = factory.inflate(R.layout.delete_student_popup, null);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setView(deleteDialogView);
        AlertDialog dialog = alertBuilder.create();

        TextView are_you_sure = deleteDialogView.findViewById(R.id.are_you_sure_text);
        if (fromWhere.equals("folder")) {
            are_you_sure.setText("Are you sure you want to delete this folder?");
        } else {
            are_you_sure.setText("Are you sure you want to delete this assignment?");
        }
        deleteDialogView.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.checkInternet(context)) {
                    dialog.dismiss();
                    if (fromWhere.equals("folder")) {
                        hitDeleteFolderService(position);
                    } else {
                        hitDeleteAssignmentService(position);
                    }

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
        dialog.show();
    }


    private void hitDeleteFolderService(int position) {
        Map<String, String> map = new HashMap<>();
        /*if (batchId != null)
            map.put("batchId", batchId);*/
        if (folderList != null && folderList.size() > 0 && folderList.get(position).getId() != null)
            map.put("folderId", folderList.get(position).getId());
        new MyClient(context).deleteFolderFromBatch(map, (content, error) -> {
            if (content != null) {
                GetBatchSubmitAssignmentTestResponse response = (GetBatchSubmitAssignmentTestResponse) content;
                if (response.getStatus() != null && response.getStatus().equals("200")) {
                    try {
                        folderList = mAdapter.removeAt(position);
                        /*     studentDetailsDataList.remove(position);*/

                        if (folderList.size() == 0) {
                            layout_empty_list.setVisibility(View.VISIBLE);
                            tvNoResultFound.setVisibility(View.VISIBLE);
                            empty_list_img.setVisibility(VISIBLE);
                            empty_list_img.setImageResource(R.drawable.ic_no_video);
                            tvNoResultFound.setText("You haven't uploaded any content yet");
                            swipeRefreshLayout.setRefreshing(false);
                            mRrecylerView.setVisibility(GONE);
                        }
                    } catch (Exception e) {
                        Utilities.makeToast(context, "Please refresh the content");
                    }

                    Utilities.makeToast(context, "Folder deleted successfully");

                } else if (response.getStatus() != null && response.getStatus().equals("403"))
                    Utilities.openUnauthorizedDialog(context);
                else Utilities.makeToast(context, getString(R.string.server_error));
            } else Utilities.makeToast(context, getString(R.string.server_error));
        });
    }

    private void hitDeleteAssignmentService(int position) {
        Map<String, String> map = new HashMap<>();
        /*if (batchId != null)
            map.put("batchId", batchId);*/
        if (assignmentList != null && assignmentList.size() > 0 && assignmentList.get(position).getId() != null)
            map.put("assignmentId", assignmentList.get(position).getId());
        new MyClient(context).deleteAssignmentFromBatch(map, (content, error) -> {
            if (content != null) {
                GetBatchSubmitAssignmentTestResponse response = (GetBatchSubmitAssignmentTestResponse) content;
                if (response.getStatus() != null && response.getStatus().equals("200")) {
                    try {
                        assignmentList = mAdapter.removeAssignmentAt(position);
                        /*     studentDetailsDataList.remove(position);*/

                        if (assignmentList.size() == 0) {
                            layout_empty_list.setVisibility(View.VISIBLE);
                            tvNoResultFound.setVisibility(View.VISIBLE);
                            empty_list_img.setVisibility(VISIBLE);
                            empty_list_img.setImageResource(R.drawable.test_assignment);
                            tvNoResultFound.setText(R.string.assignment_will_appear_here);
                            //swipeRefreshLayout.setVisibility(GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            mRrecylerView.setVisibility(GONE);
                        }
                    } catch (Exception e) {
                        Utilities.makeToast(context, "Please refresh the content");
                    }

                    Utilities.makeToast(context, "Assignment deleted successfully");

                } else if (response.getStatus() != null && response.getStatus().equals("403"))
                    Utilities.openUnauthorizedDialog(context);
                else Utilities.makeToast(context, getString(R.string.server_error));
            } else Utilities.makeToast(context, getString(R.string.server_error));
        });
    }

    private void showUpdateLinkDialog(String folderId, String folderName) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_add_link_folder_dialog, null, false);
        CustomAddLinkFolderDialogBinding dialogBinding = CustomAddLinkFolderDialogBinding.bind(view);
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_corners_drawable);
        dialogBinding.textAddLink.setText("Edit Folder Name");
        dialogBinding.edtLinkFolderName.setVisibility(GONE);
        dialogBinding.edtEnterLink.setHint("Enter folder name");
        dialogBinding.edtEnterLink.setText(folderName);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        dialog.getWindow().setLayout(800, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialogBinding.txtCancel.setOnClickListener(view1 -> {
            Utilities.hideKeyboardFrom(context, dialogBinding.edtEnterLink);
            dialog.dismiss();

        });

        dialogBinding.txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(dialogBinding.edtEnterLink.getText().toString())) {
                    updateFolder(dialog, folderId, dialogBinding.edtEnterLink.getText().toString());
                } else {
                    Toast.makeText(context, "The field is required.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogBinding.edtEnterLink.requestFocus();
        Utilities.openKeyboard(context, dialogBinding.edtEnterLink);
    }

    private void updateFolder(Dialog dialog, String id, String folderName) {
        if (Utilities.checkInternet(context)) {
            handler.removeCallbacks(runnable);
            if (id != null)
                hitUpdateFolder(folderName, id, dialog);
            else {
                Toast.makeText(context, "Invalid folder", Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(context, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    private void hitUpdateFolder(final String folderName, final String folderID, final Dialog dialog) {
        try {
            Map<String, String> map = new HashMap<>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
            Call<GetBatchSubmitAssignmentTestResponse> call = null;

            if (!folderID.isEmpty())
                map.put("id", folderID);
            if (!folderName.isEmpty())
                map.put("folderName", folderName);

            call = apiInterface.updateFolder(headers, map);
            mProgressDialog = new ProgressView(context);
            mProgressDialog.show();
            call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
                @Override
                public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                    mProgressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                                if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                                    dialog.dismiss();
                                    Utilities.hideKeyBoard(context);
                                    videopageNo = 1;
                                    PreferenceHandler.writeString(context, PreferenceHandler.VIDEO_CACHING, null);
                                    checkInternet();
                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(context);
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(context, "406 error");
                            } else {
                                Utilities.makeToast(context, getString(R.string.server_error));
                            }       /*else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("402")) {
                                mProgressDialog.dismiss();
                            }*/
                        } else
                            Utilities.makeToast(context, getString(R.string.video_not_submitted));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Utilities.makeToast(context, getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isFragmentLoaded) {
            setUpUI();
            checkInternet();

        } else {
            /*if (isFrom.equalsIgnoreCase("video")) {
                Log.d("BatchFrom", "onResume: " + isFromBatch);
                if (!isFromBatch) {
                    hitGetCourseVideoListService();
                } else {
                    binding.btnAddAssignment.setText("Add New Folder");
                    if (PreferenceHandler.readString(context, PreferenceHandler.VIDEO_CACHING, null) != null) {
                        hitGetVideoListService("resume");
                        *//*GetBatchFolderVideosResponse videoCacheData = new Gson().fromJson(PreferenceHandler.readString(context, PreferenceHandler.VIDEO_CACHING, ""), GetBatchFolderVideosResponse.class);
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
                    } else {
                        hitGetVideoListService("resume");
                    }
                }

                shimmerFrameLayout.setVisibility(GONE);
                shimmerFrameLayout.stopShimmer();
                layoutDataView.setVisibility(VISIBLE);
            } else {

                if (onUpdate) {
                    onUpdate = false;
                } else {
                    shimmerFrameLayout.setVisibility(GONE);
                    shimmerFrameLayout.stopShimmer();
                    setUpUI();
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
                setUpUI();
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
        Call<GetAssignmentTestListResponse> call = apiInterface.getAllAssignmentList(headers, batchId, searchKey, pageLength, String.valueOf(assignmentPageNo));
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
                   /* mProgressDialog = new ProgressView(mActivity);
                    if (!mProgressDialog.isShowing())
                        mProgressDialog.show();*/
                    if (binding.loadMoreLay.getVisibility() == GONE) {
                        binding.loadMoreLay.setVisibility(VISIBLE);
                    }
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
                /*if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }*/
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
                                            empty_list_img.setVisibility(VISIBLE);
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

                    /*try {
                        boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                        if (isAdmn) {
                            if (AdminBatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                                AdminBatchDetailActivity.shimmerFrameLayout.stopShimmer();
                                AdminBatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                                AdminBatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                                AdminBatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
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
                /*if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }*/
                if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                    binding.loadMoreLay.setVisibility(GONE);
                }
                if (shimmerFrameLayout != null && shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }
                /*try {
                    boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                    if (isAdmn) {
                        if (AdminBatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                            AdminBatchDetailActivity.shimmerFrameLayout.stopShimmer();
                            AdminBatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                            AdminBatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                            AdminBatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
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
                   /* mProgressDialog = new ProgressView(mActivity);
                    if (!mProgressDialog.isShowing())
                        mProgressDialog.show();*/
                    if (binding.loadMoreLay.getVisibility() == GONE) {
                        binding.loadMoreLay.setVisibility(VISIBLE);
                    }
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
                /*if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }*/
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
                                            tvNoResultFound.setText(R.string.test_will_appear_here);
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
                   /* try {
                        boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                        if (isAdmn) {
                            if (AdminBatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                                AdminBatchDetailActivity.shimmerFrameLayout.stopShimmer();
                                AdminBatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                                AdminBatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                                AdminBatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
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
                /*if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }*/
                if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                    binding.loadMoreLay.setVisibility(GONE);
                }
                if (shimmerFrameLayout != null && shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }
                /*try {
                    boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                    if (isAdmn) {
                        if (AdminBatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                            AdminBatchDetailActivity.shimmerFrameLayout.stopShimmer();
                            AdminBatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                            AdminBatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                            AdminBatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                layoutDataView.setVisibility(View.VISIBLE);
                Utilities.makeToast(context, getString(R.string.server_error));
            }

        });
        /*if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }*/
        if (binding.loadMoreLay.getVisibility() == VISIBLE) {
            binding.loadMoreLay.setVisibility(GONE);
        }


    }

    private void hitGetCourseVideoListService(String from) {

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
               /* mProgressDialog = new ProgressView(context);
                if (!mProgressDialog.isShowing())
                    mProgressDialog.show();*/
                if (binding.loadMoreLay.getVisibility() == GONE) {
                    binding.loadMoreLay.setVisibility(VISIBLE);
                }
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
                    /*mProgressDialog.dismiss();*/
                    if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                        binding.loadMoreLay.setVisibility(GONE);
                    }
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
                                if (from.equals("refresh")) {
                                    folderList.clear();
                                }
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
                                    tvNoResultFound.setText("You haven't uploaded any content yet");
                                    //swipeRefreshLayout.setVisibility(GONE);
                                    swipeRefreshLayout.setRefreshing(false);
                                    mRrecylerView.setVisibility(GONE);
                                } else isVideoLoading = false;
                            }
                        }
                    }
                } else if (response.getStatus() != null && response.getStatus().equalsIgnoreCase("403")) {
                    Utilities.openUnauthorizedDialog(context);
                } else {
                    Utilities.makeToast(context, getString(R.string.server_error));
                }

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            } else {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (videopageNo > 1) {
                        /* mProgressDialog.dismiss();*/
                        if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                            binding.loadMoreLay.setVisibility(GONE);
                        }
                    } else {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(GONE);
                    }
                }
                Utilities.makeToast(context, getString(R.string.server_error));
            }
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            /*try {
                boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                if (isAdmn) {
                    if (AdminBatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                        AdminBatchDetailActivity.shimmerFrameLayout.stopShimmer();
                        AdminBatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                        AdminBatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                        AdminBatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        });
    }

    private void hitGetVideoListService(String from) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        Call<GetBatchFolderVideosResponse> call = apiInterface.getBatchFolderList(headers, batchId, pageLength, String.valueOf(videopageNo));
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            mProgressBar.setVisibility(GONE);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        } else if (!swipeRefreshLayout.isRefreshing()) {
            if (videopageNo > 1) {
               /* mProgressDialog = new ProgressView(context);
                if (!mProgressDialog.isShowing())
                    mProgressDialog.show();*/
                if (binding.loadMoreLay.getVisibility() == GONE) {
                    binding.loadMoreLay.setVisibility(VISIBLE);
                }
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
                        /* mProgressDialog.dismiss();*/
                        if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                            binding.loadMoreLay.setVisibility(GONE);
                        }
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
                                        PreferenceHandler.writeString(context, PreferenceHandler.VIDEO_CACHING, videoListJson);
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
                                    /*if (response.body().getResult().getFolders().getData().size() < 25) {
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
                                        empty_list_img.setVisibility(VISIBLE);
                                        empty_list_img.setImageResource(R.drawable.ic_no_video);
                                        tvNoResultFound.setText("You haven't uploaded any content yet");
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
                                /* mProgressDialog.dismiss();*/
                                if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                                    binding.loadMoreLay.setVisibility(GONE);
                                }
                            } else {
                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(GONE);
                            }
                        }
                        /*try {
                            boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                            if (isAdmn) {
                                if (AdminBatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                                    AdminBatchDetailActivity.shimmerFrameLayout.stopShimmer();
                                    AdminBatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                                    AdminBatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                                    AdminBatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/
                        layoutDataView.setVisibility(View.VISIBLE);
                        Utilities.makeToast(context, getString(R.string.server_error));
                    }
                }

            }

            @Override
            public void onFailure(Call<GetBatchFolderVideosResponse> call, Throwable t) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (videopageNo > 1) {
                        /* mProgressDialog.dismiss();*/
                        if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                            binding.loadMoreLay.setVisibility(GONE);
                        }
                    } else {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(GONE);
                    }
                }
                /*try {
                    boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                    if (isAdmn) {
                        if (AdminBatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                            AdminBatchDetailActivity.shimmerFrameLayout.stopShimmer();
                            AdminBatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                            AdminBatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                            AdminBatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
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
                    //  mRrecylerView.setAdapter(mAdapter);
//                    mAdapter.setUpdatedList(folderList, true);
                }
                setVideoNotificationView();
            } else {
                layout_empty_list.setVisibility(VISIBLE);
                tvNoResultFound.setVisibility(View.VISIBLE);
                empty_list_img.setVisibility(VISIBLE);
                empty_list_img.setImageResource(R.drawable.ic_no_video);
                tvNoResultFound.setText("You haven't uploaded any content yet");
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
                tvNoResultFound.setText(R.string.test_will_appear_here);
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
                tvNoResultFound.setVisibility(View.VISIBLE);
                empty_list_img.setVisibility(VISIBLE);
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
        boolean dataaa = data != null;
        if (requestCode == REQUEST_UPDATE_SUBMIT_STATUS) {
            if (data != null) {
                boolean isDataSubmitted = data.getBooleanExtra(PreferenceHandler.IS_DATA_SUBMITTED, false);
                if (isDataSubmitted) {
                    String isFromIntent = data.getStringExtra(PreferenceHandler.IS_FROM);
                    if (Utilities.checkInternet(getContext())) {
                        if (isFromIntent.equalsIgnoreCase("assignment")) {
                            onUpdate = true;
                            assignmentPageNo = 1;
                            if (assignmentList.size() > 0) {
                                assignmentList.clear();
                            }
                            hitGetAssignmentListService();
                        } else if (isFromIntent.equalsIgnoreCase("test")) {
                            testPageNo = 1;
                            if (testList.size() > 0) {
                                testList.clear();
                            }
                            layoutDataView.setVisibility(GONE);
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
        } else if (requestCode == FOLDER_ADD_STATUS) {
            videopageNo = 1;
            folderList.clear();
            Utilities.hideKeyBoard(context);
            checkInternet();
        } else if (requestCode == FOLDER_EDIT_STATUS) {
            videopageNo = 1;
            folderList.clear();
            Utilities.hideKeyBoard(context);
            checkInternet();
        }

        /*else {
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