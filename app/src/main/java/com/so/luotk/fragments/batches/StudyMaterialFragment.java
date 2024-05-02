package com.so.luotk.fragments.batches;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.so.luotk.R;
import com.so.luotk.activities.PDFViewNew;
import com.so.luotk.activities.SmFolderDetailsActivity;
import com.so.luotk.activities.ViewAttachmentActivity;
import com.so.luotk.adapter.StudyMaterialAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.FragmentStudyMaterialBinding;
import com.so.luotk.firebase.NotificationDataModel;
import com.so.luotk.models.newmodels.study.Datum;
import com.so.luotk.models.newmodels.study.StudyMaterialModel;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class StudyMaterialFragment extends Fragment {
    private final static int PERMISSION_ALL_NEW = 959;
    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };


    private static final int PDF_VIEW_REQUEST = 1010;
    private static final int DOC_FILE_VIEW = 1011;
    private FragmentStudyMaterialBinding binding;
    private String batchId;
    private final String pageLength = "10";
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private View layoutDataView, empty_list_layout, rootLayout;
    private RecyclerView mRecylerView;
    private SearchView searchView;
    private Handler handler;
    private final Handler searchHandler = new Handler(Looper.myLooper());
    private Runnable runnable, searchRunnable;
    private boolean isFirstInternetToastDone;
    private StudyMaterialAdapter mAdapter;
    private ProgressBar mProgressBar;
    private APIInterface apiInterface;
    private int folderpageNo = 1, file, folder;
    private boolean isListLoading;
    private ImageView empty_list_img;
    private TextView tv_no_data;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private String folderId, courseId;
    private ProgressView mProgressDialog;
    private List<Datum> dataList;
    private String searchKey = "", isFrom;
    private boolean isSearchOpen, isFragmentLoaded, isRefreshing;
    private final int i = 0;
    private Context context;
    private List<NotificationDataModel> notificationDataList;
    private Activity mActivity;
    private String selllingPrice = "";
    private long mLastClickTime = 0;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    public static StudyMaterialFragment newInstance(String batchId, String folderId, String isFrom, String sellingPrice) {
        StudyMaterialFragment fragment = new StudyMaterialFragment();
        Bundle args = new Bundle();
        args.putString(PreferenceHandler.BATCH_ID, batchId);
        args.putString(PreferenceHandler.FOLDER_ID, folderId);
        args.putString(PreferenceHandler.IS_FROM, isFrom);
        args.putString(PreferenceHandler.SELLING_PRICE, sellingPrice);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(getActivity());
//     getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        handler = new Handler(Looper.myLooper());
        dataList = new ArrayList<>();
        if (getArguments() != null) {
            folderId = getArguments().getString(PreferenceHandler.FOLDER_ID);
            isFrom = getArguments().getString(PreferenceHandler.IS_FROM);
            selllingPrice = getArguments().getString(PreferenceHandler.SELLING_PRICE);
            assert isFrom != null;
            if (isFrom.equalsIgnoreCase("batch"))
                batchId = getArguments().getString(PreferenceHandler.BATCH_ID);
            else if (isFrom.equalsIgnoreCase("course"))
                courseId = getArguments().getString(PreferenceHandler.BATCH_ID);
        }
        PreferenceHandler.writeBoolean(mActivity, PreferenceHandler.EXTERNAL_APP, false);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Utilities.hideKeyBoard(getContext());
        binding = FragmentStudyMaterialBinding.inflate(inflater, container, false);
        notificationDataList = PreferenceHandler.getNotificationDataList(context);
        swipeRefreshLayout = binding.swipeRefreshLayout;
        mRecylerView = binding.studyMaterialRecyclerView;
        mRecylerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecylerView.setNestedScrollingEnabled(false);
        layoutDataView = binding.layoutDataView;
        empty_list_layout = binding.layoutEmptyList;
        empty_list_img = binding.emptyListImg;
        tv_no_data = binding.tvNoMatch;
        shimmerFrameLayout = binding.shimmerLayout;
        shimmerFrameLayout.startShimmer();
        // setShimmerTimer();
        rootLayout = binding.getRoot();
        mProgressBar = binding.searchDataProgress;
        apiInterface = ApiUtils.getApiInterface();
        mProgressDialog = new ProgressView(mActivity);
        searchView = binding.searchView;
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        return binding.getRoot();
    }

    private void setupView() {
        Utilities.hideKeyBoard(getContext());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                Utilities.hideKeyBoard(getContext());
                isSearchOpen = true;
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
                isSearchOpen = true;
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
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showInputMethod(view);
                }
            }
        });

        mRecylerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0)
                    Utilities.hideKeyBoard(getContext());
                if (Utilities.checkInternet(getContext())) {
                    if (isLastItemDisplaying(recyclerView) && isListLoading)
                        hitGetStudyMaterialService();

                } else {
                    Toast.makeText(context, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
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
                if (Utilities.checkInternet(getContext())) {
                    setShimmerTimer();
                    folderpageNo = 1;
                    if (dataList.size() > 0) {
                        dataList.clear();
                    }

                    hitGetStudyMaterialService();
                } else {
                    Toast.makeText(context, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
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
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1 &&
                    (file > 10 || folder > 10)) {
                folderpageNo++;
                isListLoading = true;
                return true;
            }
        }
        return false;
    }

    private void setShimmerTimer() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        layoutDataView.setVisibility(View.VISIBLE);
                        /*createDummyList();*/

                    }
                }, 1000);
    }

    private void checkInternet() {
        if (Utilities.checkInternet(getContext())) {
            handler.removeCallbacks(runnable);
            if (batchId != null || courseId != null || isFrom.equalsIgnoreCase("freeMaterial"))
                hitGetStudyMaterialService();
            isFragmentLoaded = true;
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(context, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    private void hitApiWithSearch(String newText) {
        if (newText != null && !newText.isEmpty()) {
            isRefreshing = true;
            searchKey = newText;
            if (Utilities.checkInternet(getContext())) {
                if (dataList.size() > 0) {
                    dataList.clear();
                }
                folderpageNo = 1;
                hitGetStudyMaterialService();

            } else {
                Utilities.makeToast(context, getString(R.string.internet_connection_error));
            }
        } else if (TextUtils.isEmpty(newText)) {
            if (!isRefreshing) {
                searchKey = "";
                searchView.clearFocus();
                folderpageNo = 1;
                if (dataList.size() > 0) {
                    dataList.clear();
                }
                hitGetStudyMaterialService();
            }
        }

    }

    private void hitGetStudyMaterialService() {

        Map<String, String> map = new HashMap<>();
        boolean flag;
        map.put("search", searchKey);
        map.put("pageLength", pageLength);
        map.put("page", folderpageNo + "");
        map.put("folderId", folderId);
        if (isFrom.equals("batch")) {
            map.put("batchId", batchId);
            flag = true;
        } else if (isFrom.equals("course")) {
            map.put("courseId", courseId);
            flag = false;
        }
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            mProgressBar.setVisibility(GONE);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } else if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
            if (!searchKey.isEmpty()) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        } else {
            if (folderpageNo > 1) {
                mProgressDialog = new ProgressView(context);
                if (binding.loadMoreLay.getVisibility() == GONE) {
                    binding.loadMoreLay.setVisibility(VISIBLE);
                }
                /*if (!mProgressDialog.isShowing())
                    mProgressDialog.show();*/
            }
        }
        new MyClient(getContext()).hitStudyMaterial(map, isFrom, (content, error) -> {
            isListLoading = false;
            if (content != null) {
                StudyMaterialModel response = (StudyMaterialModel) content;
                if (response != null) {

                    if (response.getSuccess() && response.getStatus() == 200) {
//                        dataList.clear();
                        layoutDataView.setVisibility(View.VISIBLE);
                        layoutDataView.setVisibility(View.VISIBLE);
                        searchView.setVisibility(VISIBLE);
                        mRecylerView.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        binding.loadMoreLay.setVisibility(GONE);
                        empty_list_layout.setVisibility(GONE);
                        folder = response.getResult().getFolders().getTotal();
                        file = response.getResult().getFiles().getTotal();
                        if (response.getResult().getFiles().getData() != null || response.getResult().getFolders().getData() != null ||
                                response.getResult().getFolders().getData().size() > 0 || response.getResult().getFiles().getData().size() > 0) {

                            if (response.getResult().getFolders().getData().size() > 0) {
                                dataList.addAll(response.getResult().getFolders().getData());
                            }
                            if (response.getResult().getFiles().getData().size() > 0) {
                                dataList.addAll(response.getResult().getFiles().getData());
                            }
                            setStudyMaterialListAdapter();

                        } else {
                            if (folderpageNo == 1) {
                                if (!searchKey.isEmpty()) {
                                    empty_list_layout.setVisibility(View.VISIBLE);
                                    tv_no_data.setVisibility(View.VISIBLE);
                                    empty_list_img.setVisibility(GONE);
                                    tv_no_data.setText(R.string.no_result);
                                    //swipeRefreshLayout.setVisibility(GONE);
                                    swipeRefreshLayout.setRefreshing(false);
                                    mRecylerView.setVisibility(GONE);
                                } else {
                                    searchView.setVisibility(GONE);
                                    empty_list_layout.setVisibility(View.VISIBLE);
                                    tv_no_data.setVisibility(View.VISIBLE);
                                    empty_list_img.setVisibility(VISIBLE);
                                    empty_list_img.setImageResource(R.drawable.no_study_material);
                                    tv_no_data.setText(R.string.study_material_will_appear_here);
                                    //swipeRefreshLayout.setVisibility(GONE);
                                    swipeRefreshLayout.setRefreshing(false);
                                    mRecylerView.setVisibility(GONE);
                                }
                            }
                        }
                    } else if (response.getStatus() == 403)
                        Utilities.openUnauthorizedDialog(getContext());
                    else Utilities.makeToast(getContext(), getString(R.string.server_error));
                } else {
                    Utilities.makeToast(getContext(), getString(R.string.server_error));
                }

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

                if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                    binding.loadMoreLay.setVisibility(GONE);
                }
            }
        });
    }

    private void setStudyMaterialListAdapter() {

        if (dataList != null && dataList.size() > 0) {
            setMaterialAddNotificationView();
            setClickListener();
        } else if (searchKey.isEmpty()) {
            empty_list_layout.setVisibility(View.VISIBLE);
            tv_no_data.setVisibility(View.VISIBLE);
            empty_list_img.setVisibility(VISIBLE);
            empty_list_img.setImageResource(R.drawable.no_study_material);
            tv_no_data.setText(R.string.study_material_will_appear_here);
            //swipeRefreshLayout.setVisibility(GONE);
            swipeRefreshLayout.setRefreshing(false);
            mRecylerView.setVisibility(GONE);
        } else if (!searchKey.isEmpty() && folderpageNo == 1) {
            empty_list_layout.setVisibility(View.VISIBLE);
            tv_no_data.setVisibility(View.VISIBLE);
            empty_list_img.setVisibility(GONE);
            tv_no_data.setText(R.string.no_result_found);
            //swipeRefreshLayout.setVisibility(GONE);
            swipeRefreshLayout.setRefreshing(false);
            mRecylerView.setVisibility(GONE);
        }
        isRefreshing = false;

    }

    private void setClickListener() {
        mAdapter.SetOnItemClickListener(new StudyMaterialAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if ((!isRefreshing)) {
                    try {
                        if (notificationDataList != null && notificationDataList.size() > 0) {
                            for (int i = 0; i < notificationDataList.size(); i++) {
                                if (notificationDataList.get(i).getId() != null && notificationDataList.get(i).getId().equalsIgnoreCase(String.valueOf(dataList.get(position).getId()))) {
                                    notificationDataList.remove(notificationDataList.get(i));
                                    dataList.get(position).setNewItem(false);
                                    mAdapter.notifyItemChanged(position);
                                    PreferenceHandler.setList(context, PreferenceHandler.NOTIFICATION_DATA, notificationDataList);
                                    break;
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (isFrom.equals("course") && dataList.get(position).getIs_locked() == 1) {
                        Utilities.openContentLockedDialog(context, selllingPrice, courseId, "study");
                        //  Utilities.makeToast(context, "Content is locked");
                    } else {

                        if (!hasPermissions(mActivity, permissions())) {
                            ActivityCompat.requestPermissions(mActivity,
                                    permissions(),
                                    PERMISSION_ALL_NEW);
                            return;
                        } else {
                            newStoragePermission(position);
                        }
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                            if (Utilities.isStoragePermissionRequired(mActivity)) {
//                                Utilities.requestStoragePermission(mActivity, 1000);
//                            } else {
//                                newStoragePermission(position);
//                            }
//
//                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                            if (Utilities.isStoragePermissionRequired(mActivity)) {
//                                Utilities.requestStoragePermission(mActivity, 1000);
//                            } else {
//                                newStoragePermission(position);
//                            }
//
//                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                            if (Utilities.isStoragePermissionRequired(mActivity)) {
//                                Utilities.requestStoragePermission(mActivity, 1000);
//                            } else {
//                                newStoragePermission(position);
//                            }
//                        } else {
//                            newStoragePermission(position);
//                        }

                    }
                }
            }
        });


    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Return", "hasPermissions: ");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)){
                    } else {
                        ActivityCompat.requestPermissions(mActivity,
                                permissions(),
                                PERMISSION_ALL_NEW);
                    }
                    return false;
                }
            }
        }
        Log.d("Return", "hasPermissions: return");
        return true;
    }

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }
    private void newStoragePermission(int position) {

        if (!hasPermissions(mActivity, permissions())) {
            ActivityCompat.requestPermissions(mActivity,
                    permissions(),
                    PERMISSION_ALL_NEW);
            return;
        } else {
            if (dataList.get(position).getType().equalsIgnoreCase("folder")) {
                String folderId = dataList.get(position).getId() + "";
                String id;
                if (isFrom.equals("batch"))
                    id = batchId;
                else
                    id = courseId;
                startActivity(new Intent(mActivity, SmFolderDetailsActivity.class)
                        .putExtra(PreferenceHandler.BATCH_ID, id)
                        .putExtra(PreferenceHandler.SmFolderId, folderId)
                        .putExtra(PreferenceHandler.IS_FROM, isFrom)
                        .putExtra(PreferenceHandler.SELLING_PRICE, selllingPrice)
                        .putExtra(PreferenceHandler.smFolderName, dataList.get(position).getName())
                );

            } else if (dataList.get(position).getContent().size() > 0) {
                File myFilesDir = null;
                File myFile = null;


                File file = new File(dataList.get(position).getContent().get(0));
                String strFileName = file.getName();
                if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                    myFilesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), strFileName);
                } else {
                    myFilesDir = new File(Environment
                            .getExternalStorageDirectory().getAbsolutePath()
                            + "/Download/"+ getString(R.string.app_name).replaceAll(" ", "") + "Files");
                    myFile = new File(myFilesDir, strFileName);
                }

                if (dataList.get(position).getType().equalsIgnoreCase("link")) {
                    String link = dataList.get(position).getContent().get(0);
                    if (!(link.contains("https://") || link.contains("http://"))) {
                        link = "http://" + link;
                    }
                    Intent intent = new Intent(mActivity, ViewAttachmentActivity.class);
                    intent.putExtra(PreferenceHandler.PDF_NAME, link);
                    startActivity(intent);

                } else if (dataList.get(position).getType().equalsIgnoreCase("file")
                        && dataList.get(position).getContent().size() > 0 && dataList.get(position).getContent().get(0).contains(".doc")
                ) {

                    /*Intent in = new Intent(mActivity, VisitWebsiteActivity.class);
                    in.putExtra(PreferenceHandler.DOC_URL, dataList.get(position).getContent().get(0));
                    startActivity(in);*/
                    if (myFile.exists()) {
                        try {
                            if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {

                                MediaScannerConnection.scanFile(mActivity,
                                        new String[] {myFile.getAbsolutePath() }, null,
                                        new MediaScannerConnection.OnScanCompletedListener() {
                                            public void onScanCompleted(String path, Uri uri) {
                                                Log.e("ExternalStorage", "Scanned " + path + ":");
                                                Log.e("ExternalStorage", "-> uri=" + uri);
                                                openFile(mActivity, new File(path), uri);
                                            }
                                        });
                                // only for gingerbread and newer versions
                            } else {
                                openFile(mActivity, myFile, Uri.fromFile(myFile));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        downloadAttachments(strFileName, "doc");
                    }


                } else if (dataList.get(position).getType().equalsIgnoreCase("file")
                        && dataList.get(position).getContent().size() > 0 && dataList.get(position).getContent().get(0).contains(".ppt")) {
                    /*Intent in = new Intent(mActivity, VisitWebsiteActivity.class);
                    in.putExtra(PreferenceHandler.DOC_URL, dataList.get(position).getContent().get(0));
                    startActivity(in);*/
                    if (myFile.exists()) {
                        try {
                            if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {

                                MediaScannerConnection.scanFile(mActivity,
                                        new String[] {myFile.getAbsolutePath() }, null,
                                        new MediaScannerConnection.OnScanCompletedListener() {
                                            public void onScanCompleted(String path, Uri uri) {
                                                Log.e("ExternalStorage", "Scanned " + path + ":");
                                                Log.e("ExternalStorage", "-> uri=" + uri);
                                                openFile(mActivity, new File(path), uri);
                                            }
                                        });
                                // only for gingerbread and newer versions
                            } else {
                                openFile(mActivity, myFile, Uri.fromFile(myFile));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        downloadAttachments(strFileName, "doc");
                    }

                } else if (dataList.get(position).getType().equalsIgnoreCase("file")
                        && dataList.get(position).getContent().size() > 0 && dataList.get(position).getContent().get(0).endsWith(".pdf")) {

                    String fileName = "", cahce_file_name = "";
                    if (dataList.get(position).getContent().get(0).contains("studymaterial")){
                        if (isFrom.equalsIgnoreCase("freeMaterial")) {
                            fileName =  dataList.get(position).getName().replaceAll("studymaterial/free/", "");
                        } else {
                            fileName =  dataList.get(position).getName().replaceAll("studymaterial/", "");
                        }
                    } else {
                        fileName = dataList.get(position).getName();
                    }
                    if (dataList.get(position).getContent().get(0).contains("studymaterial")){
                        if (isFrom.equalsIgnoreCase("freeMaterial")) {
                            cahce_file_name =  dataList.get(position).getContent().get(0).replaceAll("studymaterial/free/", "");
                        } else {
                            cahce_file_name =  dataList.get(position).getContent().get(0).replaceAll("studymaterial/", "");
                        }
                    } else {
                        cahce_file_name = dataList.get(position).getContent().get(0);
                    }
                    String dirPath = Utilities.getRootDirPath(mActivity);
                    File downloadedFile = new File(dirPath, cahce_file_name);
                    Intent in = new Intent(mActivity, PDFViewNew.class);
                    in.putExtra(PreferenceHandler.FILE_NAME, fileName);
                    in.putExtra(PreferenceHandler.PDF_FILE_CACHE, cahce_file_name);
                    Log.d("TAG", "newStoragePermission: " + downloadedFile.getAbsolutePath());
                    if (downloadedFile.exists()) {
                        in.putExtra(PreferenceHandler.DOC_URL,downloadedFile.getAbsolutePath());
                        in.putExtra(PreferenceHandler.FROM_LOCAL, "true");
                    } else {
                        String docUrl = "";
                        if (isFrom.equalsIgnoreCase("freeMaterial"))
                            docUrl = "https://web.smartowls.in/" + dataList.get(position).getContent().get(0);
                        else
                            docUrl = "https://web.smartowls.in/" + dataList.get(position).getContent().get(0);
                        in.putExtra(PreferenceHandler.DOC_URL, docUrl);
                        in.putExtra(PreferenceHandler.FROM_LOCAL, "false");
                    }
                    startActivity(in);
                } else if (dataList.get(position).getType().equalsIgnoreCase("file") && dataList.get(position).getContent().get(0).contains(".jpg") || dataList.get(position).getContent().get(0).contains(".JPG") || dataList.get(position).getContent().get(0).contains(".JPEG") || dataList.get(position).getContent().get(0).contains(".jpeg")
                        || dataList.get(position).getContent().get(0).contains(".png") || dataList.get(position).getContent().get(0).contains(".PNG") || dataList.get(position).getContent().get(0).contains(".GIF") || dataList.get(position).getContent().get(0).contains(".gif")) {
                    //downloadAttachments(dataList.get(position).getContent().get(0), "image");
                    Intent intent = new Intent(mActivity, ViewAttachmentActivity.class);
                    intent.putExtra("isFrom", "studymaterial");
                    intent.putExtra(PreferenceHandler.PDF_NAME, dataList.get(position).getContent().get(0));
                    startActivity(intent);
                }
            }
        }
    }

    private void downloadAttachments(String attachmentToDownload, String type) {
        String state = Environment.getExternalStorageState();
        DownloadAttachedFiles downloadFile = new DownloadAttachedFiles(mActivity, attachmentToDownload, type, isFrom/* "studymaterial"*/);
        downloadFile.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    requestAllFilesAccessPermission(position);
                } else {
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFragmentLoaded) {
            checkInternet();
            setupView();

        } else {
            setupView();
            if (mAdapter != null)
                mRecylerView.setAdapter(mAdapter);
            setStudyMaterialListAdapter();
            layoutDataView.setVisibility(VISIBLE);
            shimmerFrameLayout.setVisibility(GONE);
        }
        Utilities.hideKeyBoard(getContext());

        if (PreferenceHandler.readBoolean(mActivity, PreferenceHandler.EXTERNAL_APP, false)) {
            File filll = new File(PreferenceHandler.readString(mActivity, PreferenceHandler.EXTERNAL_DOC, ""));
            if (filll.exists()) {
                if (!PreferenceHandler.readBoolean(mActivity, PreferenceHandler.FILE_DOWNLOADED, false)) {
                    filll.delete();
                    PreferenceHandler.writeBoolean(mActivity, PreferenceHandler.EXTERNAL_APP, false);
                    Log.d("TAG", "openFile: Deleted" + filll.getAbsolutePath());
                }
            } else {
                Log.d("TAG", "onRestart: Not Exist");
            }
        }

    }


    private class DownloadAttachedFiles extends AsyncTask<Void, Void, String> {
        private final ProgressView mProgressDialog;
        private final Context context;
        private final String filename;
        private File file;
        private String downloadResult;
        private final String type;
        private final String isFrom;

        public DownloadAttachedFiles(Context context, String fileName, String type, String isFrom) {
            this.context = context;
            this.filename = fileName;
            this.type = type;
            this.isFrom = isFrom;
            mProgressDialog = new ProgressView(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            String path = "true";
            HttpURLConnection c;
            if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                Uri newFileUri = addFileToDownloadsApi29(filename);

                OutputStream outputStream;
                ResponseBody responseBody;
                if (isFrom.equalsIgnoreCase("freeMaterial"))
                    responseBody = downloadFileFromInternet("https://web.smartowls.in/studymaterial/free/" + filename);
                else
                    responseBody = downloadFileFromInternet("https://web.smartowls.in/studymaterial/" + filename);
                try {
                    outputStream = mActivity.getContentResolver().openOutputStream(newFileUri, "w");
                    outputStream.write(responseBody.bytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("TAG", "doInBackground: " + responseBody);
                path = getMediaStoreEntryPathApi29(newFileUri);

                Log.d("Path", "doInBackground: " + path);

                // only for gingerbread and newer versions
            } else {
                try {
                    URL url;
                    if (isFrom.equalsIgnoreCase("freeMaterial"))
                        url = new URL("https://web.smartowls.in/studymaterial/free/" + filename);
                    else
                        url = new URL("https://web.smartowls.in/studymaterial/" + filename);
                    c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setDoOutput(true);
                    c.connect();
                } catch (IOException e1) {
                    return e1.getMessage();
                }
                File myFilesDir = new File(Environment
                        .getExternalStorageDirectory().getAbsolutePath()
                        + "/Download/"+ getString(R.string.app_name).replaceAll(" ", "") + "Files");

                file = new File(myFilesDir, filename);
                Log.d("PView", "doInBackground1: ");
                if (file.exists()) {
                    file.delete();
                }
                if ((myFilesDir.mkdirs() || myFilesDir.isDirectory())) {
                    try {
                        InputStream is = c.getInputStream();
                        FileOutputStream fos = new FileOutputStream(myFilesDir
                                + "/" + filename);

                        byte[] buffer = new byte[1024];
                        int len1 = 0;
                        while ((len1 = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len1);
                        }
                        fos.close();
                        is.close();

                    } catch (Exception e) {
                        Log.d("TAG", "doInBackground: " + e.getMessage());
                        return e.getMessage();
                    }


                } else {
                    return "Unable to create folder";
                }
            }
            return path;
        }


        @Override
        protected void onPostExecute(String result) {
            downloadResult = result;
            mProgressDialog.dismiss();

            if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                File file = new File(mActivity.getExternalFilesDir(null), filename);
                Log.d("TAG", "onPostExecute: " + file.getAbsolutePath());
                Log.d("TAG", "onPostExecute: " + file.exists());

                MediaScannerConnection.scanFile(mActivity,
                        new String[] {result }, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.e("ExternalStorage", "Scanned " + path + ":");
                                Log.e("ExternalStorage", "-> uri=" + uri);
                                File fileeee = new File(path);
                                if (type.equalsIgnoreCase("pdf") && fileeee != null) {
                                } else {
                                    openFile(context, fileeee, uri);
                                }
                            }
                        });

            } else {
                if (result != null && result.equalsIgnoreCase("true")) {
                    if (type.equalsIgnoreCase("doc") && file != null) {
                        try {
                            openFile(context, file, Uri.parse(""));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            super.onPostExecute(result);
        }


    }
    private Uri addFileToDownloadsApi29(String filename) {
        Uri collection = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, filename);
        ContentResolver contentResolver = mActivity.getContentResolver();

        Uri downloadedFileUri = contentResolver.insert(collection, values);
        return downloadedFileUri;
    }

    private String getMediaStoreEntryPathApi29(Uri uri) {

        Cursor cursor = mActivity.getContentResolver().query(
                uri,
                new String[] {MediaStore.Files.FileColumns.DATA} ,
                null,
                null,
                null
        );
        Log.d("TAG", "getMediaStoreEntryPathApi29: " + cursor);
        while (!cursor.moveToNext()) {
            // Use an ID column from the projection to get
            // a URI representing the media item itself.
            return null;
        }
        Log.d("TAG", "getMediaStoreEntryPathApi29: " + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)));
        return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));

    }


    private void openFile(Context context, File url, Uri uriFrom) {
        // Create URI
        File file = url;
        Uri uri;
        if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
            uri = uriFrom;
        } else {
            uri = Uri.fromFile(file);
        }
        Log.d("TAG", "openFile: " + file.getAbsolutePath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getPath()));
        Log.d("TAG", "openFile: " + mimeType);
        intent.setDataAndType(uri, mimeType);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PreferenceHandler.PDF_NAME, url.getName());
        PackageManager packageManager = mActivity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        List resolvedActivityList;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resolvedActivityList =
                    packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        } else {
            resolvedActivityList = packageManager.queryIntentActivities(intent, 0);
        }
        boolean isIntentSafe = resolvedActivityList.size() > 0;
        intent = Intent.createChooser(intent, "Open File");
        if (isIntentSafe) {
            // startActivityForResult(intent,101);
            startActivityForResult(intent, DOC_FILE_VIEW);
        } else {
            Toast.makeText(getContext(), getString(R.string.device_doesnot_support_file), Toast.LENGTH_SHORT).show();

        }
        PreferenceHandler.writeBoolean(mActivity, PreferenceHandler.EXTERNAL_APP, true);
        PreferenceHandler.writeString(mActivity, PreferenceHandler.EXTERNAL_DOC, url.getAbsolutePath());

    }
    private void setMaterialAddNotificationView() {
        if (notificationDataList != null && notificationDataList.size() > 0) {
            int bigList = dataList.size() > notificationDataList.size() ? 1 : 0;
            if (bigList == 1) {
                for (int i = 0; i < notificationDataList.size(); i++) {
                    for (int j = 0; j < dataList.size(); j++) {
                        if (notificationDataList.get(i).getId() != null && notificationDataList.get(i).getId().equalsIgnoreCase(String.valueOf(dataList.get(j).getId()))) {
                            dataList.get(j).setNewItem(true);
                        }
                    }
                }
            } else {
                for (int i = 0; i < dataList.size(); i++) {
                    for (int j = 0; j < notificationDataList.size(); j++) {
                        if (notificationDataList.get(j).getId() != null && notificationDataList.get(j).getId().equalsIgnoreCase(String.valueOf(dataList.get(i).getId()))) {
                            dataList.get(i).setNewItem(true);

                        }
                    }
                }
            }

        }
        Set<Datum> set = new LinkedHashSet<>(dataList);
        dataList.clear();
        dataList.addAll(set);
        if (mAdapter == null) {
            mAdapter = new StudyMaterialAdapter(context, dataList, isFrom);
            mRecylerView.setAdapter(mAdapter);
        } else {
            mAdapter.updateList(dataList);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PDF_VIEW_REQUEST) {
            if (data != null) {
                String pdf_name = data.getStringExtra(PreferenceHandler.PDF_NAME);
                if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                    String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                    File myFile = new File(filepath, pdf_name);
                    Log.d("TAG", "onActivityResult: " + myFile.getAbsolutePath());
                    if (!PreferenceHandler.readBoolean(mActivity, PreferenceHandler.FILE_DOWNLOADED, false)) {
                        myFile.delete();
                    }
                } else {
                    File myFilesDir = new File(Environment
                            .getExternalStorageDirectory().getAbsolutePath()
                    );
                    File myFile = new File(myFilesDir, pdf_name);
                    if (!PreferenceHandler.readBoolean(mActivity, PreferenceHandler.FILE_DOWNLOADED, false)) {
                        myFile.delete();
                    }
                }
            }
        } else if (requestCode == DOC_FILE_VIEW) {
            try {
                String doc_name = PreferenceHandler.readString(mActivity, PreferenceHandler.EXTERNAL_DOC, "");
                Log.d("TAG", "onActivityResult: " + doc_name);
                if (Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                    String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                    File myFile = new File(doc_name);
                    Log.d("TAG", "onActivityResult: " + myFile.getAbsolutePath());
                    if (!PreferenceHandler.readBoolean(mActivity, PreferenceHandler.FILE_DOWNLOADED, false)) {
                        myFile.delete();
                    }
                } else {
                    File myFilesDir = new File(Environment
                            .getExternalStorageDirectory().getAbsolutePath()
                    );
                    File myFile = new File(myFilesDir, doc_name);
                    if (!PreferenceHandler.readBoolean(mActivity, PreferenceHandler.FILE_DOWNLOADED, false)) {
                        myFile.delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ResponseBody downloadFileFromInternet(String url){
        // We use OkHttp to create HTTP request
        OkHttpClient httpClient = new OkHttpClient();
        Response response = null;
        try {
            response = httpClient.newCall(new Request.Builder().url(url).build()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "downloadFileFromInternet: " + response +" : "+ response.body());
        return response.body();
    }

}



