package com.so.luotk.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;
import com.so.luotk.R;
import com.so.luotk.activities.adminrole.AddFolderLinkActivity;
import com.so.luotk.adapter.VideosAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.customviews.fabRevealMenu.helper.OnFABMenuSelectedListener;
import com.so.luotk.customviews.fabRevealMenu.helper.RevealDirection;
import com.so.luotk.customviews.fabRevealMenu.model.FABMenuItem;
import com.so.luotk.customviews.fabRevealMenu.view.FABRevealMenu;
import com.so.luotk.databinding.CustomAddLinkFolderDialogBinding;
import com.so.luotk.models.output.Data;
import com.so.luotk.models.output.GetBatchFolderVideosResponse;
import com.so.luotk.models.output.GetBatchSubmitAssignmentTestResponse;
import com.so.luotk.models.video.DatumVideo;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class VideoListActivity extends AppCompatActivity {
    public static final int FOLDER_ADD_STATUS = 1234;
    public static final int FOLDER_EDIT_STATUS = 4321;
    public static final int VIDEO_ADD_STATUS = 1236;
    public static final int VIDEO_EDIT_STATUS = 1237;
    public static final int REQUEST_UPDATE_SUBMIT_STATUS = 101;
    private static String videoId = "";
    private RecyclerView mRecylerView;
    private SearchView searchView;
    private Toolbar toolbar;
    private TextView toolbarCustomTitle;
    private FloatingActionMenu materialDesignFAM;
    private FloatingActionButton btn_add_video, btn_add_folder;
    private String topicName;
    private String isFrom;
    private String batchId;
    private String folderId;
    private String courseId;
    private final String title = "";
    private String sellingPrice;
    private VideosAdapter videoAdapter;
    private TextView tvNoResultFound;
    private ArrayList<Data> resultList;
    private ProgressView mProgressDialog;
    private APIInterface apiInterface;
    private final String pageLength = "25";
    private List<DatumVideo> videoList;
    private List<DatumVideo> videoListWithType;
    private boolean isLoading;
    private int pageNo = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Handler handler;
    private final Handler searchHandler = new Handler(Looper.myLooper());
    private Runnable runnable, searchRunnable;
    private View layoutDataView, rootLayout;
    private boolean isFirstInternetToastDone;
    private View layout_empty_list, root_layout;
    private ImageView empty_list_img;
    private ProgressBar mProgressBar;
    private boolean isSearchOpen, isRefreshing/*, isFromBatch*/;
    private long mLastClickTime = 0;
    private ArrayList<FABMenuItem> items;
    private FABRevealMenu fabMenu;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
//      getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(R.layout.activity_video_list);

        setToolbar();
        setupUI();
    }
    private void initItems(boolean toShowDoubleItems) {
        items = new ArrayList<>();
        if (PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.FOLDER_IN_FOLDER, "").equals("1")) {
            items.add(new FABMenuItem(1,"Add Folder", AppCompatResources.getDrawable(VideoListActivity.this, R.drawable.ic_baseline_folder_24)));
        }
        items.add(new FABMenuItem(2,"Add Video", AppCompatResources.getDrawable(VideoListActivity.this, R.drawable.ic_baseline_ondemand_video_24)));


    }
    private void setToolbar() {
        toolbar = findViewById(R.id.sub_assign_toolbar);
        toolbarCustomTitle = findViewById(R.id.tv_toolbar_title);
        btn_add_video = findViewById(R.id.btn_add_video);
        btn_add_folder = findViewById(R.id.btn_add_folder);
        materialDesignFAM = findViewById(R.id.materialDesignFAM);
        videoList = new ArrayList<>();
        videoListWithType = new ArrayList<>();
        if (getIntent() != null) {
            topicName = getIntent().getStringExtra(PreferenceHandler.TOPIC_NAME);
            isFrom = getIntent().getStringExtra(PreferenceHandler.IS_FROM);
            folderId = getIntent().getStringExtra(PreferenceHandler.FOLDER_ID);
            sellingPrice = getIntent().getStringExtra(PreferenceHandler.SELLING_PRICE);

            Log.d("folderIdGet", "setToolbar: " + folderId);
            //  isFromBatch = getIntent().getBooleanExtra(PreferenceHandler.IS_FROM_BATCH, false);
            if (isFrom.equals("batch"))
                batchId = getIntent().getStringExtra(PreferenceHandler.BATCH_ID);
            if (isFrom.equals("home")) {
                videoList = (ArrayList<DatumVideo>) getIntent().getSerializableExtra("popularvideos");
                videoListWithType = (ArrayList<DatumVideo>) getIntent().getSerializableExtra("popularvideos");
                topicName = getIntent().getStringExtra("title");
            } else
                courseId = getIntent().getStringExtra(PreferenceHandler.COURSE_ID);

            toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(this));
            if (topicName != null) {
                toolbarCustomTitle.setText(topicName);
            }
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void setupUI() {
        Utilities.hideKeyBoard(this);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        apiInterface = ApiUtils.getApiInterface();
        resultList = new ArrayList<>();
        root_layout = findViewById(R.id.root_layout);
        mRecylerView = findViewById(R.id.assignment_recycler_view);

        mProgressBar = findViewById(R.id.search_data_progress);
        tvNoResultFound = findViewById(R.id.tv_no_result_found);
        mRecylerView.setLayoutManager(new LinearLayoutManager(this));
        mRecylerView.setNestedScrollingEnabled(false);
        layoutDataView = findViewById(R.id.layout_data_view);
        rootLayout = findViewById(R.id.root_layout);
        layout_empty_list = findViewById(R.id.layout_empty_list);
        empty_list_img = findViewById(R.id.no_assignment_img);
        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        shimmerFrameLayout.startShimmer();
        handler = new Handler(Looper.myLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        searchView = findViewById(R.id.search_view);
        Log.d("TAG", "setupUI: " + isFrom);
        if (!isFrom.equals("home")) {
            checkInternet();
        } else {
            // swipeRefreshLayout.setOnRefreshListener(null);
            shimmerFrameLayout.setVisibility(GONE);
            layoutDataView.setVisibility(View.VISIBLE);
            toolbar.setTitle(topicName);
            setAssignmentTestVideoListAdapter();
        }
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        Utilities.hideKeyBoard(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                isSearchOpen = true;
                if (videoAdapter != null) {
                    videoAdapter.getFilter().filter(newText);
                    if (videoAdapter.getItemCount() < 1) {
                        layout_empty_list.setVisibility(View.VISIBLE);
                        tvNoResultFound.setVisibility(View.VISIBLE);
                        empty_list_img.setVisibility(GONE);
                        mRecylerView.setVisibility(View.GONE);
                    } else {
                        layout_empty_list.setVisibility(GONE);
                        tvNoResultFound.setVisibility(View.GONE);
                        mRecylerView.setVisibility(View.VISIBLE);
                    }
                    if (TextUtils.isEmpty(newText)) {
                        searchView.clearFocus();
                        isSearchOpen = false;
                        layout_empty_list.setVisibility(View.GONE);
                        mRecylerView.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSearchOpen = true;
                if (videoAdapter != null) {
                    videoAdapter.getFilter().filter(newText);
                    if (videoAdapter.getItemCount() < 1) {
                        layout_empty_list.setVisibility(View.VISIBLE);
                        tvNoResultFound.setVisibility(View.VISIBLE);
                        empty_list_img.setVisibility(GONE);
                        mRecylerView.setVisibility(View.GONE);
                    } else {
                        layout_empty_list.setVisibility(GONE);
                        tvNoResultFound.setVisibility(View.GONE);
                        mRecylerView.setVisibility(View.VISIBLE);
                    }
                    if (TextUtils.isEmpty(newText)) {
                        isSearchOpen = false;
                        layout_empty_list.setVisibility(View.GONE);
                        mRecylerView.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }


        });
        if (!isFrom.equals("home"))
            mRecylerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0)
                        Utilities.hideKeyBoard(VideoListActivity.this);
                    super.onScrolled(recyclerView, dx, dy);
                    if (Utilities.checkInternet(VideoListActivity.this)) {
                        if (isLastItemDisplaying(recyclerView)) {
                            if (isLoading) {
                                if (!isSearchOpen) {
                                    if (isFrom.equals("batch"))
                                        hitGetVideoListService("load");
                                    else if (isFrom.equals("course"))
                                        hitGetCourseVideoListService("load");
                                }
                            }
                        }
                    } else {
                        Snackbar.make(rootLayout, getString(R.string.txt_check_internet), Snackbar.LENGTH_SHORT).show();
                    }
                }

            });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isSearchOpen = false;
                searchView.clearFocus();
                searchView.setQuery("", false);
                pageNo = 1;
                if (!isFrom.equals("home")) {
                    isRefreshing = true;
                    if (Utilities.checkInternet(VideoListActivity.this)) {

                        if (videoList.size() > 0) {
                            videoList.clear();
                        }
                        if (!isSearchOpen) {
                            if (isFrom.equals("batch")) {
                                hitGetVideoListService("refresh");
                            } else if (isFrom.equals("course")) {
                                hitGetCourseVideoListService("refresh");
                            }

                        }
                    } else {
                        Snackbar.make(rootLayout, getString(R.string.txt_check_internet), Snackbar.LENGTH_SHORT).show();
                    }
                } else swipeRefreshLayout.setRefreshing(false);

            }
        });

        boolean isAdmn = PreferenceHandler.readBoolean(this, PreferenceHandler.ADMIN_LOGGED_IN, false);
        Log.d("TAG", "setupUI: " + isAdmn + " : ");
        if (isAdmn) {
            btn_add_video.setVisibility(View.VISIBLE);
            if (PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.FOLDER_IN_FOLDER, "0").equals("1")) {
                btn_add_folder.setVisibility(VISIBLE);
            } else {
                btn_add_folder.setVisibility(GONE);
            }
            materialDesignFAM.setVisibility(GONE);
        } else {
            btn_add_video.setVisibility(GONE);
            btn_add_folder.setVisibility(GONE);
            materialDesignFAM.setVisibility(GONE);
        }

        btn_add_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showDialog();
                Intent in = new Intent(VideoListActivity.this, AddFolderLinkActivity.class);
                in.putExtra(PreferenceHandler.BATCH_ID, batchId);
                in.putExtra("isLink", "true");
                in.putExtra("editable", "false");
                in.putExtra("fromWhere", "video");
                in.putExtra("folderId", folderId);
                startActivityForResult(in, VIDEO_ADD_STATUS);
                if (materialDesignFAM.isOpened()) {
                    materialDesignFAM.close(true);
                }
//                showAddLinkDialog(true);
            }
        });

        btn_add_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(VideoListActivity.this, AddFolderLinkActivity.class);
                in.putExtra(PreferenceHandler.BATCH_ID, batchId);
                in.putExtra("isLink", "false");
                in.putExtra("editable", "false");
                in.putExtra("fromWhere", "video");
                in.putExtra("folderId", folderId);
                startActivityForResult(in, FOLDER_ADD_STATUS);
                if (materialDesignFAM.isOpened()) {
                    materialDesignFAM.close(true);
                }
            }
        });

        initItems(true);

        final FABRevealMenu fabMenu = initFabMenu();
        initListeners(fabMenu);
    }
    private void initListeners(FABRevealMenu fabMenu) {

        if (fabMenu != null) {
            fabMenu.setTitleVisible(true);
            fabMenu.setMenuItems(items);
//            fabMenu.setMenuTitleTypeface(ResourcesCompat.getFont(VideoListActivity.this, R.font.poppins_light));
            fabMenu.setSmallerMenu();
            fabMenu.setMenuDirection(RevealDirection.UP);
        }
    }
    private FABRevealMenu initFabMenu() {
        fab = findViewById(R.id.fab);
        final FABRevealMenu fabMenu = findViewById(R.id.fabMenu);

        try {
            if (fab != null && fabMenu != null) {
                setFabMenu(fabMenu);
                //attach menu to fab
                fabMenu.bindAnchorView(fab);
//                fabMenu.setOverlayBackground(R.color.green);
                //set menu selection listener
                fabMenu.setOnFABMenuSelectedListener(new OnFABMenuSelectedListener() {
                    @Override
                    public void onMenuItemSelected(View view, int id) {
                        Log.d("TAG", "onMenuItemSelected: " + id + " : "+ items.size() +" : "+ items.get(id).getTitle());
                        if (id >= 0 && items != null && items.size() > id) {
                            if (items.get(id).getTitle().equalsIgnoreCase("Add Folder")) {
                                Intent in = new Intent(VideoListActivity.this, AddFolderLinkActivity.class);
                                in.putExtra(PreferenceHandler.BATCH_ID, batchId);
                                in.putExtra("isLink", "false");
                                in.putExtra("editable", "false");
                                in.putExtra("fromWhere", "video");
                                in.putExtra("folderId", folderId);
                                startActivityForResult(in, FOLDER_ADD_STATUS);
                                if (materialDesignFAM.isOpened()) {
                                    materialDesignFAM.close(true);
                                }
                            } else if (items.get(id).getTitle().equalsIgnoreCase("Add Video")) {
                                Intent in = new Intent(VideoListActivity.this, AddFolderLinkActivity.class);
                                in.putExtra(PreferenceHandler.BATCH_ID, batchId);
                                in.putExtra("isLink", "true");
                                in.putExtra("editable", "false");
                                in.putExtra("fromWhere", "video");
                                in.putExtra("folderId", folderId);
                                startActivityForResult(in, VIDEO_ADD_STATUS);
                                if (materialDesignFAM.isOpened()) {
                                    materialDesignFAM.close(true);
                                }
                            }
//                            Toast.makeText(VideoListActivity.this, items.get(id).getId() + "Clicked", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            boolean isAdmn = PreferenceHandler.readBoolean(this, PreferenceHandler.ADMIN_LOGGED_IN, false);
            if (isAdmn) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fabMenu;
    }

    public void onBackPressed() {
        if (fabMenu != null) {
            if (fabMenu.isShowing()) {
                fabMenu.closeMenu();
            } else {
                super.onBackPressed();
            }
        }
    }

    public FABRevealMenu getFabMenu() {
        return fabMenu;
    }

    public void setFabMenu(FABRevealMenu fabMenu) {
        this.fabMenu = fabMenu;
    }
  /* private void showDialog() {
       View view = LayoutInflater.from(VideoListActivity.this).inflate(R.layout.custom_dialog_add_video, null, false);
       CustomDialogAddVideoBinding dialogBinding = CustomDialogAddVideoBinding.bind(view);
       Dialog dialog = new Dialog(VideoListActivity.this);
       Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_corners_drawable);
       dialog.setContentView(dialogBinding.getRoot());
       dialog.show();
       dialogBinding.tvAddLink.setOnClickListener(view1 -> {
           dialog.dismiss();
           showAddLinkDialog(true);
       });
       dialogBinding.tvAddFolder.setOnClickListener(view1 -> {
           dialog.dismiss();
           showAddLinkDialog(false);
       });
   }*/

    private void showAddLinkDialog(boolean isLink) {
        View view = LayoutInflater.from(VideoListActivity.this).inflate(R.layout.custom_add_link_folder_dialog, null, false);
        CustomAddLinkFolderDialogBinding dialogBinding = CustomAddLinkFolderDialogBinding.bind(view);
        Dialog dialog = new Dialog(VideoListActivity.this);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_corners_drawable);
        if (isLink) {
            dialogBinding.textAddLink.setText(getString(R.string.add_new_video));
            dialogBinding.edtEnterLink.setHint(getString(R.string.enter_paste_url));
            dialogBinding.edtLinkFolderName.setVisibility(GONE);
        }
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        dialog.getWindow().setLayout(800, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialogBinding.txtCancel.setOnClickListener(view1 -> {
            Utilities.hideKeyboardFrom(VideoListActivity.this, dialogBinding.edtEnterLink);
            dialog.dismiss();

        });
        dialogBinding.txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(dialogBinding.edtEnterLink.getText().toString())) {
                    createLinkFolder(dialog, dialogBinding.edtEnterLink.getText().toString(), isLink);
                } else {
                    Toast.makeText(VideoListActivity.this, getString(R.string.required_folder), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogBinding.edtEnterLink.requestFocus();
        Utilities.openKeyboard(VideoListActivity.this, dialogBinding.edtEnterLink);
    }

    private void createLinkFolder(Dialog dialog, String folderLinkName, boolean isLink) {
        if (Utilities.checkInternet(VideoListActivity.this)) {
            handler.removeCallbacks(runnable);
            if (batchId != null)
                hitSubmitAddVideoLink(folderLinkName, isLink, dialog);
            else {
                Toast.makeText(VideoListActivity.this, getString(R.string.invalid_match), Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(VideoListActivity.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }


    private void hitSubmitAddVideoLink(final String folderLinkName, final boolean isLink, final Dialog dialog) {
        try {
            Map<String, String> map = new HashMap<>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.DEVICE_ID, ""));
            Call<GetBatchSubmitAssignmentTestResponse> call = null;

            Log.d("TAG", "field: " + folderLinkName +" : "+batchId + " : "+ folderId + " : "+ isLink);
            if (!folderLinkName.isEmpty())
                map.put("videoUrl", folderLinkName);
            if (!batchId.isEmpty())
                map.put("fk_batchId", batchId);
            if (!folderId.isEmpty())
                map.put("fk_folderId", folderId);

            if (isLink) {
                call = apiInterface.createNewVideoLink(headers, map);
            }
            mProgressDialog = new ProgressView(VideoListActivity.this);
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
                                    Utilities.hideKeyBoard(VideoListActivity.this);
                                    checkInternet();
                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(VideoListActivity.this);
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(VideoListActivity.this, "406 error");
                            } else {
                                Utilities.makeToast(VideoListActivity.this, getString(R.string.server_error));
                            }       /*else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("402")) {
                               mProgressDialog.dismiss();
                           }*/
                        } else
                            Utilities.makeToast(VideoListActivity.this, getString(R.string.video_not_submitted));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Utilities.makeToast(VideoListActivity.this, getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void hitUpdateVideoLink(final String videoLink, final String videoId, final Dialog dialog) {
        try {
            Map<String, String> map = new HashMap<>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.DEVICE_ID, ""));
            Call<GetBatchSubmitAssignmentTestResponse> call = null;

            Log.d("TAG", "field: " + videoLink +" : "+batchId + " : "+ folderId);
            if (!videoId.isEmpty())
                map.put("id", videoId);
            if (!videoLink.isEmpty())
                map.put("videoUrl", videoLink);

            call = apiInterface.updateVideoLink(headers, map);
            mProgressDialog = new ProgressView(VideoListActivity.this);
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
                                    Utilities.hideKeyBoard(VideoListActivity.this);
                                    checkInternet();
                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(VideoListActivity.this);
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(VideoListActivity.this, "406 error");
                            } else {
                                Utilities.makeToast(VideoListActivity.this, getString(R.string.server_error));
                            }       /*else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("402")) {
                               mProgressDialog.dismiss();
                           }*/
                        } else
                            Utilities.makeToast(VideoListActivity.this, getString(R.string.video_not_submitted));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Utilities.makeToast(VideoListActivity.this, getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkInternet() {
        if (Utilities.checkInternet(this)) {
            handler.removeCallbacks(runnable);
            if (isFrom.equals("batch"))
                hitGetVideoListService("start");
//            else hitGetCourseVideoListService();
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Snackbar.make(rootLayout, getString(R.string.internet_connection_error), Snackbar.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        super.onResume();
        if (videoList != null && videoList.size() > 0) {
            int size = videoList.size();
            for (int i = 0; i < size; i++)
                videoList.get(i).setPlaying(false);
        }
        if (isFrom.equalsIgnoreCase("course")) {
            hitGetCourseVideoListService("start");
        }
        Utilities.hideKeyBoard(this);
        searchView.clearFocus();
    }

    private void hitGetCourseVideoListService(String from) {
        Map<String, Object> querymap = new HashMap<>();
        querymap.put("courseId", courseId);
        querymap.put("pageLength", pageLength);
        querymap.put("page", pageNo);
        querymap.put("folderId", folderId);
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            mProgressBar.setVisibility(GONE);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        } else if (!swipeRefreshLayout.isRefreshing()) {
            if (pageNo > 1) {
                mProgressDialog = new ProgressView(this);
                if (!mProgressDialog.isShowing())
                    mProgressDialog.show();
            }
        }
        if (pageNo == 1) {
            videoList.clear();
            videoListWithType.clear();
        }
        new MyClient(this).getCourseVideos(querymap, (content, error) -> {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            } else {
                if (pageNo > 1) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
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
                            if ((response.getResult().getFiles().getData() != null && response.getResult().getFiles().getData().size() > 0) ||
                                    response.getResult().getFolders().getData() != null && response.getResult().getFolders().getData().size() > 0) {
                                mRecylerView.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                                tvNoResultFound.setVisibility(GONE);
                                if (from.equals("refresh")) {
                                    videoList.clear();
                                    videoListWithType.clear();
                                }
//                                if (PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.FOLDER_IN_FOLDER, "0").equals("1")) {
                                    if (response.getResult().getFolders().getData().size() > 0) {
                                        for (int i = 0; i < response.getResult().getFolders().getData().size(); i++) {
                                            DatumVideo datumVideo = new DatumVideo();
                                            datumVideo.setType("folder");
                                            datumVideo.setId(response.getResult().getFolders().getData().get(i).getId());
                                            datumVideo.setFolderName(response.getResult().getFolders().getData().get(i).getFolderName());
                                            datumVideo.setFolderId(response.getResult().getFolders().getData().get(i).getFolderId());
                                            datumVideo.setFkBatchId(response.getResult().getFolders().getData().get(i).getFkBatchId());
                                            datumVideo.setFk_courseId(response.getResult().getFolders().getData().get(i).getFk_courseId());
                                            videoListWithType.add(datumVideo);
                                        }
                                    }
//                                }

                                if (response.getResult().getFiles().getData().size() > 0) {
                                    for (int i = 0; i < response.getResult().getFiles().getData().size(); i++) {
                                        DatumVideo datumVideo = new DatumVideo();
                                        datumVideo.setType("file");
                                        datumVideo.setId(response.getResult().getFiles().getData().get(i).getId());
                                        datumVideo.setVideoUrl(response.getResult().getFiles().getData().get(i).getVideoUrl());
                                        datumVideo.setFolderId(response.getResult().getFiles().getData().get(i).getFolderId());
                                        datumVideo.setFkBatchId(response.getResult().getFiles().getData().get(i).getFkBatchId());
                                        datumVideo.setFk_courseId(response.getResult().getFiles().getData().get(i).getFk_courseId());
                                        datumVideo.setTitle(response.getResult().getFiles().getData().get(i).getTitle());
                                        datumVideo.setThumb(response.getResult().getFiles().getData().get(i).getThumb());
                                        datumVideo.setViews_limit(response.getResult().getFiles().getData().get(i).getViews_limit());
                                        datumVideo.setIs_locked(response.getResult().getFiles().getData().get(i).getIs_locked());
                                        videoListWithType.add(datumVideo);
                                    }
//                                    videoList.addAll(response.getResult().getFolders().getData());
                                }
                                videoList.addAll(videoListWithType);
                                setAssignmentTestVideoListAdapter();
                                if (response.getResult().getFiles().getData().size() < 25) {
                                    isLoading = false;
                                } else if (response.getResult().getFiles().getTotal().equalsIgnoreCase("25")) {
                                    isLoading = false;
                                } else {
                                    isLoading = true;
                                    pageNo++;
                                }

                            } else {
                                if (pageNo == 1) {
                                    layout_empty_list.setVisibility(View.VISIBLE);
                                    tvNoResultFound.setVisibility(View.VISIBLE);
                                    empty_list_img.setImageResource(R.drawable.ic_no_video);
                                    tvNoResultFound.setText(R.string.video_shared_will_appear);
                                    swipeRefreshLayout.setVisibility(GONE);
                                    mRecylerView.setVisibility(GONE);
                                } else isLoading = false;
                            }
                        }
                    }
                } else if (response.getStatus() != null && response.getStatus().equalsIgnoreCase("403"))
                    Utilities.openUnauthorizedDialog(this);
                else
                    Utilities.makeToast(this, getString(R.string.server_error));

            } else {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (pageNo > 1) {
                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                    } else {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(GONE);
                    }
                }
                Utilities.makeToast(this, getString(R.string.server_error));
            }


        });
    }

    private void hitGetVideoListService(String from) {
        Log.d("TAG", "hitGetVideoListService: " + from);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));

        Log.d("TAG", "hitGetVideoListService: " + batchId +" : "+ folderId +" : "+ pageLength + " : "+ pageNo);
        Call<GetBatchFolderVideosResponse> call = apiInterface.getBatchVideoListUnderFolder(headers, batchId, folderId, pageLength, String.valueOf(pageNo));

        if (!swipeRefreshLayout.isRefreshing()) {
            if (pageNo > 1) {
                mProgressDialog = new ProgressView(this);
                mProgressDialog.show();
            }

        }
        Log.d("TAG", "hitGetVideoListService: " + pageNo);
        if (pageNo == 1) {
            videoList.clear();
            videoList.removeAll(videoList);
         /*  videoListWithType.clear();
           videoListWithType.removeAll(videoListWithType);*/
            Log.d("TAG", "hitGetVideoListService: " + "ClearTrue" + videoList.size());
        }
        call.enqueue(new Callback<GetBatchFolderVideosResponse>() {
            @Override
            public void onResponse(Call<GetBatchFolderVideosResponse> call, Response<GetBatchFolderVideosResponse> response) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (pageNo > 1) {
                        if (mProgressDialog != null && mProgressDialog.isShowing())
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
                                if ((response.body().getResult().getFiles().getData() != null && response.body().getResult().getFiles().getData().size() > 0) ||
                                        response.body().getResult().getFolders().getData() != null && response.body().getResult().getFolders().getData().size() > 0) {
                                    if (from.equals("refresh")) {
                                        videoList.clear();
                                    }
                                    videoListWithType.clear();
                                    Log.d("Video", "onResponse: " + videoList.size());
                                    mRecylerView.setVisibility(View.VISIBLE);
                                    searchView.setVisibility(View.VISIBLE);
                                    layout_empty_list.setVisibility(GONE);
                                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                                    tvNoResultFound.setVisibility(View.GONE);
//                                    if (PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.FOLDER_IN_FOLDER, "0").equals("1")) {
                                        if (response.body().getResult().getFolders().getData().size() > 0) {
                                            for (int i = 0; i < response.body().getResult().getFolders().getData().size(); i++) {
                                                DatumVideo datumVideo = new DatumVideo();
                                                datumVideo.setType("folder");
                                                datumVideo.setId(response.body().getResult().getFolders().getData().get(i).getId());
                                                datumVideo.setFolderName(response.body().getResult().getFolders().getData().get(i).getFolderName());
                                                datumVideo.setFolderId(response.body().getResult().getFolders().getData().get(i).getFolderId());
                                                datumVideo.setFkBatchId(response.body().getResult().getFolders().getData().get(i).getFkBatchId());
                                                datumVideo.setFk_courseId(response.body().getResult().getFolders().getData().get(i).getFk_courseId());
                                                videoListWithType.add(datumVideo);
                                            }

                                        }
//                                    }
                                    if (response.body().getResult().getFiles().getData().size() > 0) {
                                        for (int i = 0; i < response.body().getResult().getFiles().getData().size(); i++) {
                                            DatumVideo datumVideo = new DatumVideo();
                                            datumVideo.setType("file");
                                            datumVideo.setId(response.body().getResult().getFiles().getData().get(i).getId());
                                            datumVideo.setVideoUrl(response.body().getResult().getFiles().getData().get(i).getVideoUrl());
                                            datumVideo.setFolderId(response.body().getResult().getFiles().getData().get(i).getFolderId());
                                            datumVideo.setFkBatchId(response.body().getResult().getFiles().getData().get(i).getFkBatchId());
                                            datumVideo.setFk_courseId(response.body().getResult().getFiles().getData().get(i).getFk_courseId());
                                            datumVideo.setTitle(response.body().getResult().getFiles().getData().get(i).getTitle());
                                            datumVideo.setThumb(response.body().getResult().getFiles().getData().get(i).getThumb());
                                            datumVideo.setViews_limit(response.body().getResult().getFiles().getData().get(i).getViews_limit());
                                            datumVideo.setIs_locked(response.body().getResult().getFiles().getData().get(i).getIs_locked());
                                            videoListWithType.add(datumVideo);
                                        }
//                                    videoList.addAll(response.getResult().getFolders().getData());
                                    }
                                    videoList.addAll(videoListWithType);

                                    Log.d("Video1", "onResponse: " + videoList.size());
                                    setAssignmentTestVideoListAdapter();
                                    if (response.body().getResult().getFiles().getData().size() < 25) {
                                        isLoading = false;
                                    } else if (response.body().getResult().getFiles().getTotal().equalsIgnoreCase("25")) {
                                        isLoading = false;
                                    } else {
                                        isLoading = true;
                                        pageNo++;
                                    }
                                } else {
                             /*  tvNoResultFound.setVisibility(View.VISIBLE);
                               mRecylerView.setVisibility(View.GONE);*/
                                    if (pageNo == 1) {
                                        layout_empty_list.setVisibility(View.VISIBLE);
                                        tvNoResultFound.setVisibility(View.VISIBLE);
                                        empty_list_img.setImageResource(R.drawable.ic_no_video);
                                        tvNoResultFound.setText(getString(R.string.video_shared_will_appear));
                                        swipeRefreshLayout.setVisibility(GONE);
                                        searchView.setVisibility(GONE);
                                        mRecylerView.setVisibility(GONE);
                                    } else isLoading = false;
                                }

                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(VideoListActivity.this);
                    } else {
                        Utilities.makeToast(getApplicationContext(), getString(R.string.server_error));
                    }
                }

            }

            @Override
            public void onFailure(Call<GetBatchFolderVideosResponse> call, Throwable t) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (pageNo > 1) {
                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                    } else {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(GONE);
                    }

                }
                layoutDataView.setVisibility(View.VISIBLE);
                Utilities.makeToast(getApplicationContext(), getString(R.string.server_error));

            }
        });
    }


    private void setAssignmentTestVideoListAdapter() {
        Set<DatumVideo> set = new LinkedHashSet<>(videoList);
        videoList.clear();
        videoList.addAll(set);
        if (videoAdapter == null) {
            videoAdapter = new VideosAdapter(this, videoList, isFrom);
            mRecylerView.setAdapter(videoAdapter);
        } else {
            // videoAdapter.notifyDataSetChanged();
            videoAdapter.setUpdatedList(videoList);
        }
        Utilities.hideKeyBoard(VideoListActivity.this);
        isRefreshing = false;
        setVideoClickListener();
    }

    private void showUpdateLinkDialog(final String id, final String videoUrl) {
        View view = LayoutInflater.from(VideoListActivity.this).inflate(R.layout.custom_add_link_folder_dialog, null, false);
        CustomAddLinkFolderDialogBinding dialogBinding = CustomAddLinkFolderDialogBinding.bind(view);
        Dialog dialog = new Dialog(VideoListActivity.this);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_corners_drawable);
        dialogBinding.textAddLink.setText(getString(R.string.edit_video));
        dialogBinding.edtLinkFolderName.setVisibility(GONE);
        dialogBinding.edtEnterLink.setHint(getString(R.string.enter_video_url));
        dialogBinding.edtEnterLink.setText(videoUrl);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        dialog.getWindow().setLayout(800, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialogBinding.txtCancel.setOnClickListener(view1 -> {
            Utilities.hideKeyboardFrom(VideoListActivity.this, dialogBinding.edtEnterLink);
            dialog.dismiss();

        });

        dialogBinding.txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(dialogBinding.edtEnterLink.getText().toString())) {
                    updateLinkFolder(dialog, id, dialogBinding.edtEnterLink.getText().toString());
                } else {
                    Toast.makeText(VideoListActivity.this, getString(R.string.required_folder), Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogBinding.edtEnterLink.requestFocus();
        Utilities.openKeyboard(VideoListActivity.this, dialogBinding.edtEnterLink);
    }

    private void updateLinkFolder(Dialog dialog, String id, String videoLink) {
        if (Utilities.checkInternet(VideoListActivity.this)) {
            handler.removeCallbacks(runnable);
            if (id != null)
                hitUpdateVideoLink(videoLink, id, dialog);
            else {
                Toast.makeText(VideoListActivity.this, getString(R.string.invalid_match), Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(VideoListActivity.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    private void setVideoClickListener() {
        videoAdapter.SetOnEditClickListener(new VideosAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(int position, RelativeLayout threeDotsLay) {


                Context wrapper = new ContextThemeWrapper(VideoListActivity.this, R.style.popupMenuStyle);
                PopupMenu popupMenu = new PopupMenu(wrapper, threeDotsLay);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.listing_setting, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.editTv) {
                            String id = "";
                            String videoUrl = "";

                            id = videoList.get(position).getId();
                            videoUrl = videoList.get(position).getVideoUrl();

                            if (videoList.get(position).getType().equalsIgnoreCase("folder")) {
                                String folderIdEdit = String.valueOf(videoList.get(position).getId());
                                String folderNameEdit = String.valueOf(videoList.get(position).getFolderName());

                                Log.d("TAG", "onMenuItemClick: " + folderIdEdit +" : "+ folderId +" : "+ folderNameEdit);
                                Intent in = new Intent(VideoListActivity.this, AddFolderLinkActivity.class);
                                in.putExtra(PreferenceHandler.BATCH_ID, batchId);
                                in.putExtra("isLink", "false");
                                in.putExtra("editable", "true");
                                in.putExtra("fromWhere", "video");
                                in.putExtra("folderId", folderId);
                                in.putExtra("folderName", folderNameEdit);
                                in.putExtra("id", folderIdEdit);
                                startActivityForResult(in, FOLDER_EDIT_STATUS);
                            } else {
                                Intent in = new Intent(VideoListActivity.this, AddFolderLinkActivity.class);
                                in.putExtra(PreferenceHandler.BATCH_ID, batchId);
                                in.putExtra("isLink", "true");
                                in.putExtra("editable", "true");
                                in.putExtra("fromWhere", "video");
                                in.putExtra("folderId", folderId);
                                in.putExtra("videoUrl", videoUrl);
                                in.putExtra("videoId", id);
                                startActivityForResult(in, VIDEO_EDIT_STATUS);
                            }

                        } else if (menuItem.getItemId() == R.id.deleteTv){
                            if (videoList.get(position).getType().equals("folder")) {
                                String folderIdEdit = String.valueOf(videoList.get(position).getId());
                                openConfirmDeleteDialogNew("folder", position, folderIdEdit);
                            } else if (videoList.get(position).getType().equals("file")) {
                                String id = videoList.get(position).getId();
                                openConfirmDeleteDialogNew("file", position, id);
                            }
                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });
        videoAdapter.SetOnItemClickListener(new VideosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (!isRefreshing) {
                    if (videoList.get(position).getType() != null) {
                        if (videoList.get(position).getType().equalsIgnoreCase("folder")) {
                            topicName = videoList.get(position).getFolderName();
                            Intent intent = new Intent(VideoListActivity.this, VideoListActivity.class);
                            intent.putExtra(PreferenceHandler.FOLDER_ID, videoList.get(position).getId());
                            intent.putExtra(PreferenceHandler.SELLING_PRICE, sellingPrice);
                            if (isFrom.equals("batch")) {
                                intent.putExtra(PreferenceHandler.IS_FROM, "batch");
                                intent.putExtra(PreferenceHandler.BATCH_ID, batchId);
                            } else {
                                intent.putExtra(PreferenceHandler.IS_FROM, "course");
                                intent.putExtra(PreferenceHandler.COURSE_ID, courseId);
                            }
                            //remove notification dot

                            intent.putExtra(PreferenceHandler.TOPIC_NAME, topicName);
                            startActivityForResult(intent, REQUEST_UPDATE_SUBMIT_STATUS);
                        } else {
                            if (!(videoList.get(position).getIs_locked() == 1)) {
                                videoId = "";
//                                extractYTId(videoList.get(position).getVideoUrl());
                                getYoutubeId(videoList.get(position).getVideoUrl());
                                videoList.get(position).setPlaying(true);
                                if (PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.YOUTUBE_SPEED, "0").equals("4")) {
                                    Intent intent = new Intent(VideoListActivity.this, EasyVideoPlayer.class);
                                    intent.putExtra("currentposition", position);
                                    intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                    intent.putExtra("currentTime", 0L);
                                    intent.putExtra("quality", "1");
                                    intent.putExtra("videoID", videoList.get(position).getId());
                                    if (!videoId.isEmpty()) {
                                        intent.putExtra("VideoLink", videoId);
                                    } else {
                                        if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                                            intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                                        } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("shorts/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        }else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("shorts/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("live/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("live/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else {
                                            intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                                        }

                                    }
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                } else if (PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.YOUTUBE_SPEED, "0").equals("3")) {
                                    Intent intent = new Intent(VideoListActivity.this, PlayerYoutubeActivity.class);
                                    intent.putExtra("currentposition", position);
                                    intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                    intent.putExtra("currentTime", 0L);
                                    intent.putExtra("quality", "1");
                                    intent.putExtra("videoID", videoList.get(position).getId());
                                    if (!videoId.isEmpty()) {
                                        intent.putExtra("VideoLink", videoId);
                                    } else {
                                        if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                                            intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                                        } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("shorts/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        }else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("shorts/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("live/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("live/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else {
                                            intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                                        }

                                    }
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                } else if (PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.YOUTUBE_SPEED, "0").equals("2")) {
                                    Intent intent = new Intent(VideoListActivity.this, SingleVideoYoutube.class);
                                    intent.putExtra("currentposition", position);
                                    intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                    intent.putExtra("videoID", videoList.get(position).getId());
                                    if (!videoId.isEmpty()) {
                                        intent.putExtra("VideoLink", videoId);
                                    } else {
                                        if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                                            intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                                        }  else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("shorts/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        }else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("shorts/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("live/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("live/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else {
                                            intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                                        }

                                    }
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                } else if (PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.YOUTUBE_SPEED, "0").equals("1")) {
                                    Intent intent = new Intent(VideoListActivity.this, SingleIframeVideo.class);
                                    intent.putExtra("currentposition", position);
                                    intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                    intent.putExtra("videoID", videoList.get(position).getId());
                                    if (!videoId.isEmpty()) {
                                        intent.putExtra("VideoLink", videoId);
                                    } else {
                                        if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                                            intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                                        }  else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("shorts/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        }else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("shorts/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("live/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("live/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else {
                                            intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                                        }

                                    }
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                } else {
                                    Intent intent = new Intent(VideoListActivity.this, NewCustomPlayerActivity.class);
                                    //   Intent intent = new Intent(VideoListActivity.this, ExoPlayerActivity.class);
                                    intent.putExtra("videolist", (Serializable) videoList);
                                    intent.putExtra("currentposition", position);
                                    intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                    if (!videoId.isEmpty()) {
                                        intent.putExtra("VideoLink", videoId);
                                    } else {
                                        if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                                            intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                                        } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("shorts/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("shorts/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("live/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){
                                            String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                            if (splitVideo.length > 1) {
                                                String[] splitU = splitVideo[0].split("live/");
                                                intent.putExtra("VideoLink", splitU[1]);
                                            } else {
                                                String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                                intent.putExtra("VideoLink", splitUrl[1]);
                                            }
                                        } else {
                                            intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                                        }

                                    }
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                }
                            } else {
                                int limit = videoList.get(position).getViews_limit();
                                int userCount = videoList.get(position).getUser_viewed_count();
                                if (limit > 0 && userCount > 0 && (userCount >= limit))
                                    Utilities.makeToast(getApplicationContext(), getString(R.string.video_view_limit_crossed));
                                else
                                    Utilities.openContentLockedDialog(VideoListActivity.this, sellingPrice, courseId, "video");
//                            Utilities.openContentLockedDialogNew(VideoListActivity.this, sellingPrice, courseId, "video");
                            }
                        }
                    } else {
                        if (!(videoList.get(position).getIs_locked() == 1)) {
                            videoId = "";
                            //extractYTId(videoList.get(position).getVideoUrl());
                            getYoutubeId(videoList.get(position).getVideoUrl());
                            videoList.get(position).setPlaying(true);

                            if (PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.YOUTUBE_SPEED, "0").equals("4")) {
                                Intent intent = new Intent(VideoListActivity.this, EasyVideoPlayer.class);
                                intent.putExtra("currentposition", position);
                                intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                intent.putExtra("currentTime", 0L);
                                intent.putExtra("quality", "1");
                                intent.putExtra("videoID", videoList.get(position).getId());
                                if (!videoId.isEmpty()) {
                                    intent.putExtra("VideoLink", videoId);
                                } else {
                                    if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                                        intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                                    }  else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("shorts/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("shorts/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("live/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("live/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else {
                                        intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                                    }

                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            } else if (PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.YOUTUBE_SPEED, "0").equals("3")) {
                                Intent intent = new Intent(VideoListActivity.this, PlayerYoutubeActivity.class);
                                intent.putExtra("currentposition", position);
                                intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                intent.putExtra("currentTime", 0L);
                                intent.putExtra("quality", "1");
                                intent.putExtra("videoID", videoList.get(position).getId());
                                if (!videoId.isEmpty()) {
                                    intent.putExtra("VideoLink", videoId);
                                } else {
                                    if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                                        intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                                    }  else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("shorts/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("shorts/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("live/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("live/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else {
                                        intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                                    }

                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            } else if (PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.YOUTUBE_SPEED, "0").equals("2")) {
                                Intent intent = new Intent(VideoListActivity.this, SingleVideoYoutube.class);
                                intent.putExtra("currentposition", position);
                                intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                intent.putExtra("videoID", videoList.get(position).getId());
                                if (!videoId.isEmpty()) {
                                    intent.putExtra("VideoLink", videoId);
                                } else {
                                    if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                                        intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                                    } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("shorts/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("shorts/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("live/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("live/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else {
                                        intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                                    }

                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            } else if (PreferenceHandler.readString(VideoListActivity.this, PreferenceHandler.YOUTUBE_SPEED, "0").equals("1")) {
                                Intent intent = new Intent(VideoListActivity.this, SingleIframeVideo.class);
                                intent.putExtra("currentposition", position);
                                intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                intent.putExtra("videoID", videoList.get(position).getId());
                                if (!videoId.isEmpty()) {
                                    intent.putExtra("VideoLink", videoId);
                                } else {
                                    if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                                        intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                                    }  else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("shorts/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("shorts/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("live/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("live/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else {
                                        intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                                    }

                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            }
                            else {
                                Intent intent = new Intent(VideoListActivity.this, NewCustomPlayerActivity.class);
                                //   Intent intent = new Intent(VideoListActivity.this, ExoPlayerActivity.class);
                                intent.putExtra("videolist", (Serializable) videoList);
                                intent.putExtra("currentposition", position);
                                intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                if (!videoId.isEmpty()) {
                                    intent.putExtra("VideoLink", videoId);
                                } else {
                                    if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                                        intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                                    } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("shorts/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("shorts/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("live/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){
                                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                                        if (splitVideo.length > 1) {
                                            String[] splitU = splitVideo[0].split("live/");
                                            intent.putExtra("VideoLink", splitU[1]);
                                        } else {
                                            String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                                            intent.putExtra("VideoLink", splitUrl[1]);
                                        }
                                    } else {
                                        intent.putExtra("VideoLink", videoList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                                    }

                                }
                                startActivity(intent);
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            }
                        } else {
                            int limit = videoList.get(position).getViews_limit();
                            int userCount = videoList.get(position).getUser_viewed_count();
                            if (limit > 0 && userCount > 0 && (userCount >= limit))
                                Utilities.makeToast(getApplicationContext(), getString(R.string.video_view_limit_crossed));
                            else
                                Utilities.openContentLockedDialog(VideoListActivity.this, sellingPrice, courseId, "video");
//                            Utilities.openContentLockedDialogNew(VideoListActivity.this, sellingPrice, courseId, "video");
                        }
                    }
                }
            }
        });
    }


    private void openConfirmDeleteDialogNew(String fromWhere,int position, final String delid) {
        LayoutInflater factory = LayoutInflater.from(VideoListActivity.this);
        final View deleteDialogView = factory.inflate(R.layout.delete_student_popup, null);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(VideoListActivity.this);
        alertBuilder.setView(deleteDialogView);
        AlertDialog dialog = alertBuilder.create();

        TextView are_you_sure = deleteDialogView.findViewById(R.id.are_you_sure_text);
        if (fromWhere.equals("folder")) {
            are_you_sure.setText(getString(R.string.delete_folder));
        } else if (fromWhere.equals("file")) {
            are_you_sure.setText(getString(R.string.want_to_delete_video));
        }
        deleteDialogView.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.checkInternet(VideoListActivity.this)) {
                    dialog.dismiss();
                    if (fromWhere.equals("file")) {
                        hitDeleteVideoService(position, delid);
                    } else {
                        hitDeleteFolderService(position, delid);
                    }
                } else {
                    Utilities.makeToast(VideoListActivity.this, getString(R.string.internet_connection_error));
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

    private void hitDeleteFolderService(int position, final String delId) {
        Map<String, String> map = new HashMap<>();
       /*if (batchId != null)
           map.put("batchId", batchId);*/
        if (videoList != null && videoList.size() > 0 && videoList.get(position).getId() != null) {
            map.put("folderId", videoList.get(position).getId());
            Log.d("TAG", "hitDeleteFolderService: " + videoList.get(position).getId());
        }
        new MyClient(VideoListActivity.this).deleteFolderFromBatch(map, (content, error) -> {
            if (content != null) {
                GetBatchSubmitAssignmentTestResponse response = (GetBatchSubmitAssignmentTestResponse) content;
                if (response.getStatus() != null && response.getStatus().equals("200")) {
                    try {
                        videoList = videoAdapter.removeAt(position);

                        if (videoList.size() == 0) {
                            layout_empty_list.setVisibility(VISIBLE);
                            tvNoResultFound.setVisibility(VISIBLE);
                            tvNoResultFound.setText(getString(R.string.video_shared_will_appear));
                        }
                    } catch (Exception e) {
                        Utilities.makeToast(VideoListActivity.this,  getString(R.string.refresh_content));
                    }

                    Utilities.makeToast(VideoListActivity.this, getString(R.string.deleted_successfully));

                } else if (response.getStatus() != null && response.getStatus().equals("403")) {
                    Utilities.openUnauthorizedDialog(VideoListActivity.this);
                } else {
                    Utilities.makeToast(VideoListActivity.this, getString(R.string.server_error));
                }
            } else {
                Utilities.makeToast(VideoListActivity.this, getString(R.string.server_error));
            }
        });
    }
    private void hitDeleteVideoService(int position, final String delId) {
        Map<String, String> map = new HashMap<>();
       /*if (batchId != null)
           map.put("batchId", batchId);*/
        if (videoList != null && videoList.size() > 0 && videoList.get(position).getId() != null)
            map.put("videoId", delId);
        new MyClient(VideoListActivity.this).deleteVideoFromBatch(map, (content, error) -> {
            if (content != null) {
                GetBatchSubmitAssignmentTestResponse response = (GetBatchSubmitAssignmentTestResponse) content;
                if (response.getStatus() != null && response.getStatus().equals("200")) {
                    try {
                        videoList = videoAdapter.removeAt(position);
                        /*     studentDetailsDataList.remove(position);*/

                        if (videoList.size() == 0) {
                            layout_empty_list.setVisibility(VISIBLE);
                            tvNoResultFound.setVisibility(VISIBLE);
                            tvNoResultFound.setText(getString(R.string.video_shared_will_appear));
                        }
                    } catch (Exception e) {
                        Utilities.makeToast(VideoListActivity.this, getString(R.string.refresh_content));
                    }

                    Utilities.makeToast(VideoListActivity.this, getString(R.string.video_deleted_succ));

                } else if (response.getStatus() != null && response.getStatus().equals("403"))
                    Utilities.openUnauthorizedDialog(VideoListActivity.this);
                else Utilities.makeToast(VideoListActivity.this, getString(R.string.server_error));
            } else Utilities.makeToast(VideoListActivity.this, getString(R.string.server_error));
        });
    }

    public String extractYTId(String ytUrl) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(ytUrl);
        if (matcher.find()) {
            videoId = matcher.group();
        }
        return videoId;
    }

    public String extractPattern2YTId(String ytUrl) {
        String pattern = "((?<=(v|V)/)|(?<=be/)|(?<=(\\?|\\&)v=)|(?<=embed/))([\\w-]++)";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(ytUrl);
        if (matcher.find()) {
            videoId = matcher.group();
        }
        return videoId;
    }
    public static String getYoutubeId(String url) {
        String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";

        Pattern compiledPattern = Pattern.compile(pattern,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }/*from w  w  w.  j a  va  2 s .c om*/
        return null;
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int currentMode = newConfig.uiMode;
        if (AppCompatDelegate.getDefaultNightMode() != currentMode) {
            Intent intent = new Intent(VideoListActivity.this, VideoListActivity.class);
            intent.putExtra(PreferenceHandler.FOLDER_ID, folderId);
            intent.putExtra(PreferenceHandler.TOPIC_NAME, topicName);
            intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
            intent.putExtra("title", topicName);

            //  intent.putExtra(PreferenceHandler.IS_FROM_BATCH, isFromBatch);
            if (isFrom.equals("batch"))
                intent.putExtra(PreferenceHandler.BATCH_ID, batchId);
            else if (isFrom.equals("home"))
                intent.putExtra("popularvideos", (Serializable) videoList);
            else
                intent.putExtra(PreferenceHandler.COURSE_ID, courseId);
            startActivity(intent);
            finish();

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean dataaa = data != null;
        Log.e("TAG", "onActivityResultNew: in fragment $resultCode " + resultCode + " : "+ requestCode + " : "+ dataaa);
        if (dataaa) {
            if (requestCode == VIDEO_ADD_STATUS) {
                Log.d("TAG", "onActivityResult: VideoAadded");
                checkInternet();
                Utilities.hideKeyBoard(VideoListActivity.this);
            } else if (requestCode == VIDEO_EDIT_STATUS) {
                Log.d("TAG", "onActivityResult: VideoAaddedEdit");
                checkInternet();
                Utilities.hideKeyBoard(VideoListActivity.this);
            } else if (requestCode == FOLDER_ADD_STATUS) {
                checkInternet();
                Utilities.hideKeyBoard(VideoListActivity.this);
            } else if (requestCode == FOLDER_EDIT_STATUS) {
                checkInternet();
                Utilities.hideKeyBoard(VideoListActivity.this);
            }
        }
    }
}
