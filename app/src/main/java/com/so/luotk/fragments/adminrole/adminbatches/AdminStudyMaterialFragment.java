package com.so.luotk.fragments.adminrole.adminbatches;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
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
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.so.luotk.scoppedStorage.utils.PickerOptions;
import com.so.luotk.scoppedStorage.Picker;
import com.so.luotk.R;
import com.so.luotk.activities.PDFViewNew;
import com.so.luotk.activities.SmFolderDetailsActivity;
import com.so.luotk.activities.ViewAttachmentActivity;
import com.so.luotk.activities.adminrole.AddFolderLinkActivity;
import com.so.luotk.adapter.StudyMaterialAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.CustomDialogAddMaterialBinding;
import com.so.luotk.databinding.CustomDialogEnterNameBinding;
import com.so.luotk.databinding.FragmentAdminStudyMaterialBinding;
import com.so.luotk.models.newmodels.study.Datum;
import com.so.luotk.models.newmodels.study.StudyMaterialModel;
import com.so.luotk.models.output.GetBatchSubmitAssignmentTestResponse;
import com.so.luotk.utils.FileUtils;
import com.so.luotk.utils.ImageRealPath;
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
import java.util.Objects;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.core.content.PermissionChecker.checkSelfPermission;
import static com.so.luotk.scoppedStorage.Picker.PICKED_MEDIA_LIST;
import static com.so.luotk.scoppedStorage.Picker.PICKER_OPTIONS;
import static com.so.luotk.scoppedStorage.Picker.REQUEST_CODE_PICKER;

public class AdminStudyMaterialFragment extends Fragment {
    private final static int PERMISSION_ALL_NEW = 959;
    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.CAMERA
    };

    private static final int PDF_VIEW_REQUEST = 1010;
    private static final int REQUEST_CODE_PICKER_EDIT = 5458;
    private static final int DOC_FILE_VIEW = 1011;
    public static final int MATERIAL_EDIT_STATUS = 4455;
    public static final int MATERIAL_ADD_STATUS = 4456;
    private static final int DOC_EDIT = 4586;
    private static final int DOC_CREATE = 4587;
    private FragmentAdminStudyMaterialBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String[] PERMISSION_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_MEDIA_IMAGES};
    private String batchId;
    private String folderId;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private APIInterface apiInterface;
    private Handler handler;
    private final Handler searchHandler = new Handler(Looper.myLooper());
    private Runnable runnable, searchRunnable;
    private ProgressView mProgressDialog;
    private List<Datum> dataList;
    private String searchKey = "";
    private boolean isSearchOpen, isStudyFragmentLoaded, isRefreshing;
    private boolean isListLoading;
    private int folderpageNo = 1, file, folder;
    private boolean isFirstInternetToastDone;
    private final String pageLength = "10";
    private StudyMaterialAdapter mAdapter;
    private final List<String> allFilePaths = new ArrayList<>();
    private String imageIdUpdated = "";
    private Context context;
    private Activity mActivity;
    PickerOptions pickerOptions;
    ArrayList<String> testImages = new ArrayList<>();


    public AdminStudyMaterialFragment() {
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

    public static AdminStudyMaterialFragment newInstance(String batchId, String folderId) {
        AdminStudyMaterialFragment fragment = new AdminStudyMaterialFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, batchId);
        args.putString(ARG_PARAM2, folderId);
        fragment.setArguments(args);
        return fragment;
    }

    public static AdminStudyMaterialFragment newInstance(String batchId, String folderId, String isFrom, String empty) {
        AdminStudyMaterialFragment fragment = new AdminStudyMaterialFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, batchId);
        args.putString(ARG_PARAM2, folderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        handler = new Handler(Looper.myLooper());
        dataList = new ArrayList<>();
        if (getArguments() != null) {
            batchId = getArguments().getString(ARG_PARAM1);
            folderId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAdminStudyMaterialBinding.inflate(inflater, container, false);
        binding.studyMaterialRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        binding.studyMaterialRecyclerView.setNestedScrollingEnabled(false);
        apiInterface = ApiUtils.getApiInterface();
        binding.shimmerLayout.startShimmer();
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        binding.searchView.setVisibility(GONE);
        binding.searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        setClickListeners();
        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void setUpUi() {
        Utilities.hideKeyBoard(getActivity());

        pickerOptions = PickerOptions.init();
        pickerOptions.setAllowFrontCamera(true);
        pickerOptions.setMaxCount(1);
        pickerOptions.setExcludeVideos(true);
        pickerOptions.setPreSelectedMediaList(testImages);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                Utilities.hideKeyBoard(getActivity());
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
        binding.searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showInputMethod(view);
                }
            }
        });

        binding.studyMaterialRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0)
                    Utilities.hideKeyBoard(getContext());
                if (Utilities.checkInternet(getContext())) {
                    if (isLastItemDisplaying(recyclerView) && isListLoading)
                        hitGetStudyMaterialService("1");

                } else {
                    Toast.makeText(context, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                }
            }

        });

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isSearchOpen = false;
                binding.searchView.setQuery("", false);
                searchKey = "";
                binding.searchView.clearFocus();
                isRefreshing = true;
                if (Utilities.checkInternet(getContext())) {
                    setShimmerTimer();
                    folderpageNo = 1;
                    if (dataList.size() > 0) {
                        dataList.clear();
                    }
                    hitGetStudyMaterialService("2");
                } else {
                    Toast.makeText(getActivity(), getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
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
    private void checkInternet() {
        if (Utilities.checkInternet(getActivity())) {
            handler.removeCallbacks(runnable);
            /* createDummyList();*/
            if (!TextUtils.isEmpty(batchId))
                hitGetStudyMaterialService("3");
            isStudyFragmentLoaded = true;
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(getActivity(), getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    private void setShimmerTimer() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        binding.shimmerLayout.stopShimmer();
                        binding.shimmerLayout.setVisibility(View.GONE);
                        binding.layoutDataView.setVisibility(View.VISIBLE);
                        /*createDummyList();*/

                    }
                }, 1000);
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
                hitGetStudyMaterialService("4");

            } else {
                Utilities.makeToast(context, getString(R.string.internet_connection_error));
            }
        } else if (TextUtils.isEmpty(newText)) {
            if (!isRefreshing) {
                searchKey = "";
                binding.searchView.clearFocus();
                folderpageNo = 1;
                if (dataList.size() > 0) {
                    dataList.clear();
                }
                hitGetStudyMaterialService("5");
            }
        }

    }

    private void setClickListeners() {
        binding.layoutAddMaterial.setVisibility(VISIBLE);
        binding.btnAddMaterial.setOnClickListener(view -> showDialog());
    }

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
            } else {
            }
        }
        return false;
    }

    private void hitGetStudyMaterialService(String fromWhere) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(getContext(), PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(getContext(), PreferenceHandler.DEVICE_ID, ""));
        Call<StudyMaterialModel> call = apiInterface.getStudyMaterialrList(headers, searchKey, batchId, pageLength, folderId, String.valueOf(folderpageNo));
        if (binding.swipeRefreshLayout.isRefreshing()) {
            binding.searchDataProgress.setVisibility(GONE);
           /* if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }*/
        } else if (!binding.swipeRefreshLayout.isRefreshing()) {
            if (!searchKey.isEmpty()) {
                binding.searchDataProgress.setVisibility(View.VISIBLE);
            } else {
                if (folderpageNo > 1) {
                    if (binding.loadMoreLay.getVisibility() == GONE) {
                        binding.loadMoreLay.setVisibility(VISIBLE);
                    }
                  /*  mProgressDialog = new ProgressView(getActivity());
                    mProgressDialog.show();*/
                }
            }
        }
        if (folderpageNo == 1) {
            dataList.clear();
        }
        call.enqueue(new Callback<StudyMaterialModel>() {
            @Override
            public void onResponse(Call<StudyMaterialModel> call, Response<StudyMaterialModel> response) {
                isListLoading = false;
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                } else if (binding.searchDataProgress.getVisibility() == VISIBLE) {
                    binding.searchDataProgress.setVisibility(GONE);
                } /*else if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }*/ else if (binding.shimmerLayout.isShimmerStarted()) {
                    binding.shimmerLayout.stopShimmer();
                    binding.shimmerLayout.setVisibility(GONE);
                }

                if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                    binding.loadMoreLay.setVisibility(GONE);
                }
                binding.layoutDataView.setVisibility(View.VISIBLE);
               /* if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }*/
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus() == 200) {
                        if (response.body().getSuccess()) {
                            if (response.body().getResult() != null) {
                                if (response.body().getResult().getFolders().getData() != null && response.body().getResult().getFolders().getData().size() > 0
                                        || response.body().getResult().getFiles().getData() != null && response.body().getResult().getFiles().getData().size() > 0) {
                                    Utilities.hideKeyBoard(getContext());
                                    binding.studyMaterialRecyclerView.setVisibility(View.VISIBLE);
                                    binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
                                    binding.layoutEmptyList.setVisibility(GONE);
                                    binding.layoutAddMaterial.setVisibility(VISIBLE);
                                    binding.searchView.setVisibility(VISIBLE);
                                    folder = response.body().getResult().getFolders().getTotal();
                                    file = response.body().getResult().getFiles().getTotal();
                                    int total = response.body().getResult().getFolders().getTotal() + response.body().getResult().getFiles().getTotal();
                                    if (response.body().getResult().getFolders().getData() != null) {
                                        dataList.addAll(response.body().getResult().getFolders().getData());
                                    }
                                    if (response.body().getResult().getFiles().getData() != null) {
                                        dataList.addAll(response.body().getResult().getFiles().getData());
                                    }
                                    isListLoading = false;
                                    setStudyMaterialListAdapter();
                                   /* if (response.body().getResult().getFolders().getData().size() < 10 && response.body().getResult().getFiles().getData().size() < 10) {
                                        isListLoading = false;
                                    } else if (total == 10) {
                                        isListLoading = false;
                                    } else {
                                        isListLoading = true;
                                        folderpageNo++;
                                    }*/
                                } else {
                                    if (folderpageNo == 1) {
                                        if (!searchKey.isEmpty()) {
                                            // binding.layoutEmptyList.setVisibility(View.VISIBLE);
                                            //binding.layoutAddMaterial.setVisibility(GONE);
                                            binding.tvNoResults.setVisibility(View.VISIBLE);
                                            binding.swipeRefreshLayout.setVisibility(GONE);
                                            binding.studyMaterialRecyclerView.setVisibility(GONE);
                                        } else {
                                            binding.layoutEmptyList.setVisibility(View.VISIBLE);
                                          /*  tv_no_data.setVisibility(View.VISIBLE);
                                            empty_list_img.setVisibility(View.VISIBLE);
                                            empty_list_img.setImageResource(R.drawable.ic_note);
                                            tv_no_data.setText("Study material shared with you will\nappear here");*/
                                            binding.swipeRefreshLayout.setVisibility(GONE);
                                            binding.layoutAddMaterial.setVisibility(VISIBLE);
                                            binding.studyMaterialRecyclerView.setVisibility(GONE);
                                        }

                                    }
                                }
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus() == 403) {
                        Utilities.openUnauthorizedDialog(getActivity());
                    } else {
                        Snackbar.make(binding.getRoot(), "Server error", Snackbar.LENGTH_SHORT).show();
                    }
                    if (binding.swipeRefreshLayout != null && binding.swipeRefreshLayout.isRefreshing()) {
                        binding.swipeRefreshLayout.setRefreshing(false);
                    } else if (binding.searchDataProgress.getVisibility() == VISIBLE) {
                        binding.searchDataProgress.setVisibility(GONE);
                    } /*else if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    } */ else if (binding.shimmerLayout != null && binding.shimmerLayout.isShimmerStarted()) {
                        binding.shimmerLayout.stopShimmer();
                        binding.shimmerLayout.setVisibility(GONE);
                    }

                    if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                        binding.loadMoreLay.setVisibility(GONE);
                    }

                   /* try {
                        boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                        if (isAdmn) {
                            if (AdminBatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                                AdminBatchDetailActivity.shimmerFrameLayout.stopShimmer();
                                AdminBatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                                AdminBatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                }

            }

            @Override
            public void onFailure(Call<StudyMaterialModel> call, Throwable t) {
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (folderpageNo > 1) {
                        if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                            binding.loadMoreLay.setVisibility(GONE);
                        }

//                        mProgressDialog.dismiss();
                    } else {
                        binding.shimmerLayout.stopShimmer();
                        binding.shimmerLayout.setVisibility(GONE);
                    }
                }
                binding.layoutDataView.setVisibility(View.VISIBLE);
                Utilities.makeToast(getContext(), getString(R.string.server_error));
            }
        });


    }

    private void setStudyMaterialListAdapter() {
        Set<Datum> set = new LinkedHashSet<>(dataList);
        dataList.clear();
        dataList.addAll(set);
        if (mAdapter == null) {
            mAdapter = new StudyMaterialAdapter(getActivity(), dataList, "batch");
            binding.studyMaterialRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        isRefreshing = false;
        setClickListener();
    }

    private void setClickListener() {
        mAdapter.SetOnEditClickListener(new StudyMaterialAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(int position, RelativeLayout threeDotsLay) {


                Context wrapper = new ContextThemeWrapper(getActivity(), R.style.popupMenuStyle);
                PopupMenu popupMenu = new PopupMenu(wrapper, threeDotsLay);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.listing_setting, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked
                        if (menuItem.getItemId() == R.id.editTv) {
                            String folderIdEdit = String.valueOf(dataList.get(position).getId());
                            String folderNameEdit = "", folderLinkEdit = "";
                            if (dataList.get(position).getType().equals("folder")) {
                                folderNameEdit = dataList.get(position).getName();

                                Intent in = new Intent(getActivity(), AddFolderLinkActivity.class);
                                in.putExtra(PreferenceHandler.BATCH_ID, batchId);
                                in.putExtra("isLink", "false");
                                in.putExtra("editable", "true");
                                in.putExtra("fromWhere", "studyMaterial");
                                in.putExtra("folderId", folderId);
                                in.putExtra("folderName", folderNameEdit);
                                in.putExtra("id", folderIdEdit);
                                startActivityForResult(in, MATERIAL_EDIT_STATUS);


//                    showAddLinkDialogEdit(folderIdEdit, folderNameEdit, folderLinkEdit, false);
                            } else if (dataList.get(position).getType().equals("link")) {
                                folderNameEdit = dataList.get(position).getName();
                                folderLinkEdit = dataList.get(position).getContent().get(0);

                                Intent in = new Intent(getActivity(), AddFolderLinkActivity.class);
                                in.putExtra(PreferenceHandler.BATCH_ID, batchId);
                                in.putExtra("isLink", "true");
                                in.putExtra("editable", "true");
                                in.putExtra("fromWhere", "studyMaterial");
                                in.putExtra("folderId", folderId);
                                in.putExtra("folderName", folderNameEdit);
                                in.putExtra("content", folderLinkEdit);
                                in.putExtra("id", folderIdEdit);
                                startActivityForResult(in, MATERIAL_EDIT_STATUS);

                            } else if (dataList.get(position).getType().equals("file")) {
                                imageIdUpdated = folderIdEdit;
                                if (dataList.get(position).getType().equalsIgnoreCase("file")
                                        && dataList.get(position).getContent().size() > 0 && dataList.get(position).getContent().get(0).endsWith("doc") ||
                                        dataList.get(position).getContent().get(0).endsWith("docx")) {
                                    createFile("edit");
//                                    openDocPicker(DOC_EDIT);
                                } else if (dataList.get(position).getType().equalsIgnoreCase("file") && dataList.get(position).getContent().get(0).endsWith("ppt") || dataList.get(position).getContent().get(0).endsWith("pptx")) {
                                    Utilities.makeToast(getActivity(), "You can only upload pdf/doc file.");
                                } else if (dataList.get(position).getType().equalsIgnoreCase("file") && dataList.get(position).getContent().get(0).contains("pdf")) {
                                    createFile("edit");
//                                    openDocPicker(DOC_EDIT);
                                } else/* (studyMaterialList.get(position).getType().equalsIgnoreCase("file") && studyMaterialList.get(position).getContent().get(0).endsWith("jpg") || studyMaterialList.get(position).getContent().get(0).endsWith("jpeg")
                    || studyMaterialList.get(position).getContent().get(0).endsWith("png")) */ {

                                    Intent mPickerIntent = new Intent(mActivity, Picker.class);
                                    mPickerIntent.putExtra(PICKER_OPTIONS, pickerOptions);
                                    mPickerIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivityForResult(mPickerIntent, REQUEST_CODE_PICKER_EDIT);

                                }


                            }
                        } else {
                            if (dataList.get(position).getType().equals("folder")) {
                                openConfirmDelete("folder", position);
                            } else if (dataList.get(position).getType().equals("file")) {
                                openConfirmDelete("file", position);
                            } else if (dataList.get(position).getType().equals("link")) {
                                openConfirmDelete("link", position);
                            }
                        }

                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();


            }
        });
        mAdapter.SetOnItemClickListener(new StudyMaterialAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (!hasPermissions(mActivity, permissions())) {
                    ActivityCompat.requestPermissions(mActivity,
                            permissions(),
                            PERMISSION_ALL_NEW);
                    return;
                } else {
                    if (dataList.get(position).getType().equalsIgnoreCase("folder")) {
                        String folderId = dataList.get(position).getId() + "";
                        startActivity(new Intent(getActivity(), SmFolderDetailsActivity.class)
                                .putExtra(PreferenceHandler.BATCH_ID, batchId)
                                .putExtra(PreferenceHandler.SmFolderId, folderId)
                                .putExtra(PreferenceHandler.IS_FROM, "admin")
                                .putExtra(PreferenceHandler.smFolderName, dataList.get(position).getName())
                        );

                    } else if (dataList.get(position).getContent().size() > 0) {

                        File myFilesDir = null;
                        File myFile = null;

                        File file = new File(dataList.get(position).getContent().get(0));
                        String strFileName = file.getName();
                        if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                            if (dataList.get(position).getContent().get(0).contains("doc") || dataList.get(position).getContent().get(0).contains("pdf")) {
                                myFilesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), strFileName);
                            } else {
                                myFilesDir = new File(Environment
                                        .getExternalStorageDirectory().getAbsolutePath()
                                        + "/Download/"+ getString(R.string.app_name).replaceAll(" ", "") + "Files");
                                myFile = new File(myFilesDir, strFileName);
                            }
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
                            Intent intent = new Intent(getActivity(), ViewAttachmentActivity.class);
                            intent.putExtra(PreferenceHandler.PDF_NAME, link);
                            startActivity(intent);

                        } else if (dataList.get(position).getType().equalsIgnoreCase("file")
                                && dataList.get(position).getContent().size() > 0 && dataList.get(position).getContent().get(0).contains("doc")
                        ) {
                            if (myFile.exists()) {
                                try {
                                    if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {

                                        MediaScannerConnection.scanFile(mActivity,
                                                new String[]{myFile.getAbsolutePath()}, null,
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
                                && dataList.get(position).getContent().size() > 0 && dataList.get(position).getContent().get(0).contains("ppt")) {

                            if (myFile.exists()) {
                                try {
                                    if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {

                                        MediaScannerConnection.scanFile(mActivity,
                                                new String[]{myFile.getAbsolutePath()}, null,
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
                                && dataList.get(position).getContent().size() > 0 && dataList.get(position).getContent().get(0).endsWith("pdf")) {
                            String fileName = "", cahce_file_name = "";
                            if (dataList.get(position).getContent().get(0).contains("studymaterial")){
                                fileName = dataList.get(position).getName().replaceAll("studymaterial/", "");
                            } else {
                                fileName = dataList.get(position).getName();
                            }
                            if (dataList.get(position).getContent().get(0).contains("studymaterial")){
                                if (dataList.get(position).getContent().get(0).contains("studymaterial")){
                                    cahce_file_name =  dataList.get(position).getContent().get(0).replaceAll("studymaterial/", "");
                                } else {
                                    cahce_file_name = dataList.get(position).getContent().get(0);
                                }
                            } else {
                                cahce_file_name = dataList.get(position).getContent().get(0);
                            }
                            String dirPath = Utilities.getRootDirPath(mActivity);
                            File downloadedFile = new File(dirPath, cahce_file_name);
                            Intent in = new Intent(mActivity, PDFViewNew.class);
                            in.putExtra(PreferenceHandler.FILE_NAME, fileName);
                            in.putExtra(PreferenceHandler.PDF_FILE_CACHE, cahce_file_name);
                            if (downloadedFile.exists()) {
                                in.putExtra(PreferenceHandler.DOC_URL,downloadedFile.getAbsolutePath());
                                in.putExtra(PreferenceHandler.FROM_LOCAL, "true");
                            } else {
                                String docUrl = "https://web.smartowls.in/" + dataList.get(position).getContent().get(0);
                                in.putExtra(PreferenceHandler.DOC_URL,docUrl);
                                in.putExtra(PreferenceHandler.FROM_LOCAL, "false");
                            }

                            startActivity(in);
                            /*String docUrl = "https://web.smartowls.in/" + dataList.get(position).getContent().get(0);
                            Intent in = new Intent(mActivity, PDFViewNew.class);
                            in.putExtra(PreferenceHandler.DOC_URL,docUrl);
                            if (dataList.get(position).getContent().get(0).contains("studymaterial")){
                                in.putExtra(PreferenceHandler.FILE_NAME, dataList.get(position).getContent().get(0).replaceAll("studymaterial/", ""));
                            } else {
                                in.putExtra(PreferenceHandler.FILE_NAME,dataList.get(position).getContent().get(0));
                            }
                            startActivity(in);*/
                        } else if (dataList.get(position).getType().equalsIgnoreCase("file") && dataList.get(position).getContent().get(0).endsWith("jpg") || dataList.get(position).getContent().get(0).endsWith("jpeg")
                                || dataList.get(position).getContent().get(0).contains("png")) {
//                            downloadAttachments(dataList.get(position).getContent().get(0), "image");
                            Intent intent = new Intent(getActivity(), ViewAttachmentActivity.class);
                            intent.putExtra("isFrom", "studymaterial");
                            intent.putExtra(PreferenceHandler.PDF_NAME, dataList.get(position).getContent().get(0));
                            startActivity(intent);
                        }
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
            are_you_sure.setText(getString(R.string.delete_folder));
        } else if (fromWhere.equals("file")) {
            are_you_sure.setText(getString(R.string.want_to_delete_file));
        } else if (fromWhere.equals("link")) {
            are_you_sure.setText(getString(R.string.want_to_delete_link));
        }

        deleteDialogView.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.checkInternet(context)) {
                    dialog.dismiss();
                    if (fromWhere.equals("folder")) {
                        hitDeleteFolderService(position);
                    } else {
                        hitDeleteMaterialService(position);
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
        if (dataList != null && dataList.size() > 0 && dataList.get(position).getId() != null) {
            map.put("folderId", String.valueOf(dataList.get(position).getId()));
        }
        new MyClient(context).deleteFolderFromStudy(map, (content, error) -> {
            if (content != null) {
                GetBatchSubmitAssignmentTestResponse response = (GetBatchSubmitAssignmentTestResponse) content;
                if (response.getStatus() != null && response.getStatus().equals("200")) {
                    try {
                        dataList = mAdapter.removeAt(position);

                        if (dataList.size() == 0) {
                            binding.layoutEmptyList.setVisibility(View.VISIBLE);
                            binding.swipeRefreshLayout.setVisibility(GONE);
                            binding.layoutAddMaterial.setVisibility(VISIBLE);
                            binding.studyMaterialRecyclerView.setVisibility(GONE);
                        }
                    } catch (Exception e) {
                        Utilities.makeToast(context,  getString(R.string.refresh_content));
                    }

                    Utilities.makeToast(context, getString(R.string.deleted_successfully));

                } else if (response.getStatus() != null && response.getStatus().equals("403")) {
                    Utilities.openUnauthorizedDialog(context);
                } else {
                    Utilities.makeToast(context, getString(R.string.server_error));
                }
            } else {
                Utilities.makeToast(context, getString(R.string.server_error));
            }
        });
    }

    private void hitDeleteMaterialService(int position) {
        Map<String, String> map = new HashMap<>();
        /*if (batchId != null)
            map.put("batchId", batchId);*/
        if (dataList != null && dataList.size() > 0 && dataList.get(position).getId() != null) {
            map.put("materialId", String.valueOf(dataList.get(position).getId()));
        }
        new MyClient(context).deleteMaterialFromStudy(map, (content, error) -> {
            if (content != null) {
                GetBatchSubmitAssignmentTestResponse response = (GetBatchSubmitAssignmentTestResponse) content;
                if (response.getStatus() != null && response.getStatus().equals("200")) {
                    try {
                        dataList = mAdapter.removeAt(position);

                        if (dataList.size() == 0) {
                            binding.layoutEmptyList.setVisibility(View.VISIBLE);
                            binding.swipeRefreshLayout.setVisibility(GONE);
                            binding.layoutAddMaterial.setVisibility(VISIBLE);
                            binding.studyMaterialRecyclerView.setVisibility(GONE);
                        }
                    } catch (Exception e) {
                        Utilities.makeToast(context,  getString(R.string.refresh_content));
                    }

                    Utilities.makeToast(context,  getString(R.string.want_to_delete_material));

                } else if (response.getStatus() != null && response.getStatus().equals("403")) {
                    Utilities.openUnauthorizedDialog(context);
                } else {
                    Utilities.makeToast(context, getString(R.string.server_error));
                }
            } else {
                Utilities.makeToast(context, getString(R.string.server_error));
            }
        });
    }

    private void downloadAttachments(String attachmentToDownload, String type) {
        String state = Environment.getExternalStorageState();
        DownloadAttachedFiles downloadFile = new DownloadAttachedFiles(getActivity(), attachmentToDownload, type, "studymaterial");
        downloadFile.execute();
    }

    private void showDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_add_material, null, false);
        CustomDialogAddMaterialBinding dialogBinding = CustomDialogAddMaterialBinding.bind(view);
        Dialog dialog = new Dialog(getContext());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_corners_drawable);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();

        if (PreferenceHandler.readString(mActivity, PreferenceHandler.FOLDER_IN_FOLDER, "").equals("1")) {
            dialogBinding.tvAddFolder.setVisibility(VISIBLE);
            dialogBinding.folderView.setVisibility(VISIBLE);
        } else {
            if (!folderId.equals("0")) {
                dialogBinding.tvAddFolder.setVisibility(GONE);
                dialogBinding.folderView.setVisibility(GONE);
            }
        }
        dialogBinding.tvAddLink.setOnClickListener(view1 -> {
            dialog.dismiss();
            Intent in = new Intent(getActivity(), AddFolderLinkActivity.class);
            in.putExtra(PreferenceHandler.BATCH_ID, batchId);
            in.putExtra("isLink", "true");
            in.putExtra("editable", "false");
            in.putExtra("fromWhere", "studyMaterial");
            in.putExtra("folderId", folderId);
            startActivityForResult(in, MATERIAL_ADD_STATUS);
//            showAddLinkDialog(true);
        });
        dialogBinding.tvAddFolder.setOnClickListener(view1 -> {
            dialog.dismiss();
            Intent in = new Intent(getActivity(), AddFolderLinkActivity.class);
            in.putExtra(PreferenceHandler.BATCH_ID, batchId);
            in.putExtra("isLink", "false");
            in.putExtra("editable", "false");
            in.putExtra("fromWhere", "studyMaterial");
            in.putExtra("folderId", folderId);
            startActivityForResult(in, MATERIAL_ADD_STATUS);
//            showAddLinkDialog(false);
        });

        dialogBinding.tvOpenImage.setOnClickListener(view1 -> {
            if (isCameraPermitted(getActivity())) {
                if (!hasPermissions(mActivity, permissions())) {
                    ActivityCompat.requestPermissions(mActivity,
                            permissions(),
                            PERMISSION_ALL_NEW);
                    return;
                } else {
                    dialog.dismiss();
                    Intent mPickerIntent = new Intent(mActivity, Picker.class);
                    mPickerIntent.putExtra(PICKER_OPTIONS, pickerOptions);
                    mPickerIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivityForResult(mPickerIntent, REQUEST_CODE_PICKER);
//                    openCamera();
                }
            } else {
                requestCameraPermission(200);
            }


        });

        dialogBinding.tvAddDocument.setVisibility(VISIBLE);
        dialogBinding.tvAddDocument.setOnClickListener(view1 ->
        {
            if (!hasPermissions(mActivity, permissions())) {
                ActivityCompat.requestPermissions(mActivity,
                        permissions(),
                        PERMISSION_ALL_NEW);
                return;
            } else {
                dialog.dismiss();
//                openDocPicker(DOC_CREATE);
                createFile("create");

            }
        });
    }


    private void createFile(String from) {
        /*String[] mimeTypes =
                {*//*"image/*",*//*"application/pdf","application/msword"*//*,"application/vnd.ms-powerpoint","application/vnd.ms-excel","text/plain"*//*};*/

        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/pdf"
                };
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
//        intent.setType("application/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
//        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        if (from.equals("create")) {
            startActivityForResult(intent, DOC_CREATE);
        } else {
            startActivityForResult(intent, DOC_EDIT);
        }

    }

    private boolean isCameraPermitted(Activity activity) {
        return checkSelfPermission(activity, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED;
    }

    private void requestCameraPermission(int requestCode) {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
    }


    private void showEnterNameDialog(String fromWhere, boolean fromDoc) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_enter_name, null, false);
        CustomDialogEnterNameBinding dialogBinding = CustomDialogEnterNameBinding.bind(view);
        Dialog dialog = new Dialog(getContext());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_corners_drawable);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();


        if (fromWhere.equals("edit")) {
            dialogBinding.textAddName.setText(getString(R.string.edit_study_link));
        }

        dialogBinding.btnSubmit.setOnClickListener(view1 -> {
            dialog.dismiss();

            if (TextUtils.isEmpty(dialogBinding.edtFileName.getText().toString().trim())) {
//            binding.erTime.setVisibility(View.VISIBLE);
                dialogBinding.edtFileName.requestFocus();
                dialogBinding.edtFileName.getParent().requestChildFocus(dialogBinding.edtFileName, dialogBinding.edtFileName);
                dialogBinding.edtFileNameLay.setErrorEnabled(true);
                dialogBinding.edtFileNameLay.setError(getString(R.string.required_file_name));
                dialogBinding.edtFileNameLay.setErrorIconDrawable(0);
                return;
            } else {
                dialogBinding.edtFileNameLay.setErrorEnabled(false);
                dialogBinding.edtFileNameLay.setError(null);
                if (fromDoc) {
                    if (fromWhere.equals("new")) {
                        hitSubmitAddEditFile("image", allFilePaths, "new", dialogBinding.edtFileName.getText().toString());
                    } else {
                        hitSubmitAddEditFile("image", allFilePaths, "edit", dialogBinding.edtFileName.getText().toString());
                    }
                } else {
                    if (fromWhere.equals("new")) {
                        hitSubmitAddEditFile("doc", allFilePaths, "new", dialogBinding.edtFileName.getText().toString());
                    } else {
                        hitSubmitAddEditFile("doc", allFilePaths, "edit", dialogBinding.edtFileName.getText().toString());
                    }

                }
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "onActivityResult: " + requestCode);
        switch (requestCode) {
            case REQUEST_CODE_PICKER:
                if (resultCode == RESULT_OK && data != null) {
                    allFilePaths.clear();
                    if (data.getStringArrayListExtra(PICKED_MEDIA_LIST).size() > 0) {
                        for (int i = 0; i < data.getStringArrayListExtra(PICKED_MEDIA_LIST).size(); i++) {

                            if (data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i).contains("content:")) {
                                String imageGalleryPath = ImageRealPath.getUriRealPath(mActivity, Uri.parse(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i)));
                                testImages.add(imageGalleryPath);
                                allFilePaths.add(imageGalleryPath);
                                if (allFilePaths.size() < 2) {
                                    showEnterNameDialog("new", false);
                                } else {
                                    Utilities.makeToast(getActivity(), getString(R.string.cant_more_than_one));
                                }
                            } else {
                                testImages.add(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i));
                                String imageCameraPath = ImageRealPath.getUriRealPath(mActivity, Uri.parse(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i)));
                                allFilePaths.add(imageCameraPath);
                                if (allFilePaths.size() < 2) {
                                    showEnterNameDialog("new", false);
                                } else {
                                    Utilities.makeToast(getActivity(), getString(R.string.cant_more_than_one));
                                }
                            }
                        }
                    }
                }
                break;
            case REQUEST_CODE_PICKER_EDIT:
                if (resultCode == RESULT_OK && data != null) {
                    allFilePaths.clear();
                    if (data.getStringArrayListExtra(PICKED_MEDIA_LIST).size() > 0) {
                        for (int i = 0; i < data.getStringArrayListExtra(PICKED_MEDIA_LIST).size(); i++) {

                            if (data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i).contains("content:")) {
                                String imageGalleryPath = ImageRealPath.getUriRealPath(mActivity, Uri.parse(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i)));
                                testImages.add(imageGalleryPath);
                                allFilePaths.add(imageGalleryPath);
                                if (allFilePaths.size() < 2) {
                                    showEnterNameDialog("edit", false);
                                } else {
                                    Utilities.makeToast(getActivity(), getString(R.string.cant_more_than_one));
                                }
                            } else {
                                testImages.add(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i));
                                String imageCameraPath = ImageRealPath.getUriRealPath(mActivity, Uri.parse(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i)));
                                allFilePaths.add(imageCameraPath);
                                if (allFilePaths.size() < 2) {
                                    showEnterNameDialog("edir", false);
                                } else {
                                    Utilities.makeToast(getActivity(), getString(R.string.cant_more_than_one));
                                }
                            }
                        }
                    }
                }
                break;
            case DOC_CREATE:
                Log.d("DOC", "onActivityResult: " + resultCode + " : " + data);
                if (resultCode == RESULT_OK && data != null) {
                    allFilePaths.clear();
                        Log.d("29", "onActivityResult: ");
                        if (null != data.getClipData()) { // checking multiple selection or not
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                Uri uri = data.getClipData().getItemAt(i).getUri();
                                DocumentFile documentFile = DocumentFile.fromSingleUri(requireContext(), uri);
                                try {
                                    File file = FileUtils.from(mActivity, documentFile.getUri());
                                    allFilePaths.add(file.getAbsolutePath());
                                    if (allFilePaths.size() < 2) {
                                        showEnterNameDialog("new", true);
                                    } else {
                                        Utilities.makeToast(getActivity(), getString(R.string.cant_more_than_one));
                                    }

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (allFilePaths.size() < 2) {
                                showEnterNameDialog("new", true);
                            } else {
                                Utilities.makeToast(getActivity(), getString(R.string.cant_more_than_one));
                            }
                        } else {
                            Uri uri = data.getData();
                            mActivity.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            DocumentFile documentFile = DocumentFile.fromSingleUri(requireContext(), uri);
                            try {
                                File file = FileUtils.from(mActivity, documentFile.getUri());
                                allFilePaths.add(file.getAbsolutePath());
                                if (allFilePaths.size() < 2) {
                                    showEnterNameDialog("new", true);
                                } else {
                                    Utilities.makeToast(getActivity(), getString(R.string.cant_more_than_one));
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                }
                break;

            case DOC_EDIT:
                Log.d("DOC", "onActivityResult: " + resultCode + " : " + data);
                if (resultCode == RESULT_OK && data != null) {
                    allFilePaths.clear();
                        if (null != data.getClipData()) { // checking multiple selection or not
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                Uri uri = data.getClipData().getItemAt(i).getUri();
                                DocumentFile documentFile = DocumentFile.fromSingleUri(requireContext(), uri);
                                try {
                                    File file = FileUtils.from(mActivity, documentFile.getUri());
                                    allFilePaths.add(file.getAbsolutePath());
                                    if (allFilePaths.size() < 2) {
                                        showEnterNameDialog("new", true);
                                    } else {
                                        Utilities.makeToast(getActivity(), getString(R.string.cant_more_than_one));
                                    }

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (allFilePaths.size() < 2) {
                                showEnterNameDialog("new", true);
                            } else {
                                Utilities.makeToast(getActivity(), getString(R.string.cant_more_than_one));
                            }
                        } else {
                            Uri uri = data.getData();
                            mActivity.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            DocumentFile documentFile = DocumentFile.fromSingleUri(requireContext(), uri);
                            try {
                                File file = FileUtils.from(mActivity, documentFile.getUri());
                                Log.d("TAG", "onActivityResult: " + file.getAbsolutePath());

                                allFilePaths.add(file.getAbsolutePath());
                                if (allFilePaths.size() < 2) {
                                    showEnterNameDialog("edit", true);
                                } else {
                                    Utilities.makeToast(getActivity(), getString(R.string.cant_more_than_one));
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                }
                break;

            case MATERIAL_ADD_STATUS:
                Utilities.hideKeyBoard(getActivity());
                checkInternet();
                break;
            case MATERIAL_EDIT_STATUS:
                Utilities.hideKeyBoard(getActivity());
                checkInternet();
                break;
            case PDF_VIEW_REQUEST:
                if (data != null) {
                    String pdf_name = data.getStringExtra(PreferenceHandler.PDF_NAME);
                    if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                        String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        File myFile = new File(filepath, pdf_name);
                        Log.d("TAG", "onActivityResult: " + myFile.getAbsolutePath());
                        myFile.delete();
                    } else {
                        File myFilesDir = new File(Environment
                                .getExternalStorageDirectory().getAbsolutePath()
                        );
                        File myFile = new File(myFilesDir, pdf_name);
                        myFile.delete();
                    }
                }
                break;
            case DOC_FILE_VIEW:
                try {
                    String doc_name = data.getStringExtra(PreferenceHandler.PDF_NAME);
                    Log.d("TAG", "onActivityResult: " + doc_name);
                    if (Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                        String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        File myFile = new File(filepath, doc_name);
                        Log.d("TAG", "onActivityResult: " + myFile.getAbsolutePath());
                        myFile.delete();
                    } else {
                        File myFilesDir = new File(Environment
                                .getExternalStorageDirectory().getAbsolutePath()
                        );
                        File myFile = new File(myFilesDir, doc_name);
                        myFile.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!isStudyFragmentLoaded) {
            checkInternet();
            setUpUi();

        } else {
            setUpUi();
            if (mAdapter != null)
                binding.studyMaterialRecyclerView.setAdapter(mAdapter);
            setStudyMaterialListAdapter();
            binding.layoutDataView.setVisibility(VISIBLE);
            binding.shimmerLayout.setVisibility(GONE);
        }
        Utilities.hideKeyBoard(getContext());

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
            //String filename = "somefile.pdf";
            String path = "true";
            HttpURLConnection c;
            if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                Uri newFileUri = addFileToDownloadsApi29(filename);

                OutputStream outputStream;
                ResponseBody responseBody = downloadFileFromInternet("https://web.smartowls.in/studymaterial/" + filename);
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

                    URL url = new URL("https://web.smartowls.in/studymaterial/" + filename);
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
                        new String[]{result}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.e("ExternalStorage", "Scanned " + path + ":");
                                Log.e("ExternalStorage", "-> uri=" + uri);
//                                    openFile(context, file, uri);
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
                new String[]{MediaStore.Files.FileColumns.DATA},
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

    private ResponseBody downloadFileFromInternet(String url) {
        // We use OkHttp to create HTTP request
        OkHttpClient httpClient = new OkHttpClient();
        okhttp3.Response response = null;
        try {
            response = httpClient.newCall(new Request.Builder().url(url).build()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "downloadFileFromInternet: " + response + " : " + response.body());
        return response.body();
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

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void createLinkFolder(Dialog dialog, String folderLinkContent, String linkFolderName, boolean isLink, boolean isCreate, String folderIdEdit) {
        if (Utilities.checkInternet(getContext())) {
            handler.removeCallbacks(runnable);
            if (batchId != null)
                hitSubmitAddFolderLink(folderLinkContent, linkFolderName, isLink, dialog, isCreate, folderIdEdit);
            else {
                Toast.makeText(getContext(), getString(R.string.invalid_match), Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(getContext(), getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }


    private void hitSubmitAddFolderLink(final String folderLinkName, final String linkFolderName, final boolean isLink, final Dialog dialog, boolean isCreate, String folderIdEdit) {
        try {
            Map<String, String> map = new HashMap<>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(getActivity(), PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(getActivity(), PreferenceHandler.DEVICE_ID, ""));
            Call<GetBatchSubmitAssignmentTestResponse> call = null;

            if (!batchId.isEmpty())
                map.put("fk_batchId", batchId);

            if (isLink) {
                if (!folderLinkName.isEmpty())
                    map.put("content", folderLinkName);
                map.put("fk_folderId", folderId);
                if (!isCreate) {
                    map.put("id", folderIdEdit);
                }
                if (!linkFolderName.isEmpty())
                    map.put("name", linkFolderName);
                if (!isCreate) {
                    call = apiInterface.updateMaterialLink(headers, map);
                } else {
                    call = apiInterface.createNewMaterialLink(headers, map);
                }

            } else {
                if (!linkFolderName.isEmpty())
                    map.put("folderName", linkFolderName);
                if (!isCreate) {
                    map.put("id", folderIdEdit);
                }
                if (!isCreate) {
                    call = apiInterface.updateMaterialFolder(headers, map);
                } else {
                    call = apiInterface.createNewMaterialFolder(headers, map);
                }

            }

            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
            }

            mProgressDialog = new ProgressView(getActivity());
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
                                    folderpageNo = 1;
                                    hitGetStudyMaterialService("6");
                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(getActivity());
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(getActivity(), "406 error");
                            } else {
                                Utilities.makeToast(getActivity(), getString(R.string.server_error));
                            }       /*else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("402")) {
                                mProgressDialog.dismiss();
                            }*/
                        } else
                            Utilities.makeToast(getActivity(), getString(R.string.folder_not_created));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Utilities.makeToast(getActivity(), getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hitSubmitAddEditFile(final String folderAttachmentName, List<String> allFilePaths, String checkFromWhere, String fileNameNew) {
        try {
            String fileName = "";
            Map<String, RequestBody> map = new HashMap<>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(getActivity(), PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(getActivity(), PreferenceHandler.DEVICE_ID, ""));


            Call<GetBatchSubmitAssignmentTestResponse> call = null;

            MultipartBody.Part[] multipartImage = new MultipartBody.Part[allFilePaths.size()];
            if (this.allFilePaths.size() > 0) {

                for (int index = 0; index < allFilePaths.size(); index++) {
                    File file = new File(allFilePaths.get(index));
                    if (folderAttachmentName.equals("doc")) {
                        fileName = fileNameNew;
                    } else {
                        fileName = fileNameNew;
                    }
                    RequestBody assignmentImgRequestBody = RequestBody.create(MediaType.parse("*/*"), file);
                    multipartImage[index] = MultipartBody.Part.createFormData("attachment[]", file.getName(), assignmentImgRequestBody);
                }
            }
            if (!batchId.isEmpty())
                map.put("fk_batchId", toRequestBody(batchId));
            map.put("name", toRequestBody(fileName));
            map.put("fk_folderId", toRequestBody(folderId));

            if (checkFromWhere.equals("edit")) {
                map.put("id", toRequestBody(imageIdUpdated));
                call = apiInterface.updateMaterialFile(headers, map, multipartImage);
            } else {
                call = apiInterface.createNewMaterialFile(headers, map, multipartImage);
            }

            mProgressDialog = new ProgressView(getActivity());
            mProgressDialog.show();
            call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
                @Override
                public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                    mProgressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                                if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                                    Utilities.hideKeyBoard(getContext());
                                    folderpageNo = 1;
                                    hitGetStudyMaterialService("7");
                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(getActivity());
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(getActivity(), "406 error");
                            } else {
                                Utilities.makeToast(getActivity(), getString(R.string.server_error));
                            }       /*else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("402")) {
                                mProgressDialog.dismiss();
                            }*/
                        } else
                            Utilities.makeToast(getActivity(), getString(R.string.folder_not_created));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Utilities.makeToast(getActivity(), getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }
}