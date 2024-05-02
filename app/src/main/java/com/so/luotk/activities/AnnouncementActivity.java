package com.so.luotk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.so.luotk.R;
import com.so.luotk.adapter.AnnouncementListAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.firebase.NotificationDataModel;
import com.so.luotk.models.output.AnnouncementData;
import com.so.luotk.models.output.GetAnnouncementListResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AnnouncementActivity extends AppCompatActivity {

    public static final String TAG = "Annoucements";
    private static final int NOTIFICATION_PERMISSION_CODE = 2000;
    private static final String[] NOTIFICATION_PERMISSION = {Manifest.permission.POST_NOTIFICATIONS};

    private APIInterface apiInterface;
    private RecyclerView recylerViewAnnouncement;
    private AnnouncementListAdapter announcementListAdapter;
    private TextView tvNoResultFound;
    private ProgressView mProgressDialog;
    private String batchId = "", courseId;
    private Toolbar toolbar;
    private TextView toolbarCustomTitle;
    private ArrayList<AnnouncementData> resultList;
    private boolean isLoading, isPagination;
    private int pageNo = 1;
    private final String pageLength = "1000";
    private String isFrom;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Handler handler;
    private Runnable runnable;
    private RelativeLayout layoutDataView;
    private View rootlayout, empty_list_layout;
    private boolean isFirstInternetToastDone;
    private boolean isFromNotification;
    private RelativeLayout loadMoreLay;

    @Override
    protected void onStart() {
        super.onStart();
        if (!PreferenceHandler.readBoolean(this, PreferenceHandler.LOGGED_IN, false)) {
            startActivity(new Intent(this, WelcomeActivityNew.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
// getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Utilities.setUpStatusBar(this); Utilities.setLocale(this);
        setContentView(R.layout.fragment_annoucements);

        setToolbar();
        setupUI();
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbarCustomTitle = findViewById(R.id.tv_toolbar_title);
        toolbarCustomTitle.setText(R.string.announcement);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(upArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setupUI() {
        if (getIntent() != null) {
            batchId = getIntent().getStringExtra(PreferenceHandler.BATCH_ID);
            isFromNotification = getIntent().getBooleanExtra(PreferenceHandler.IS_FROM_ANNOUNCEMENT_NOTIFICATION, false);
            isFrom = getIntent().getStringExtra(PreferenceHandler.IS_FROM);
            if (isFrom != null && isFrom.equalsIgnoreCase("course"))
                courseId = getIntent().getStringExtra("courseId");

        }
        apiInterface = ApiUtils.getApiInterface();
        tvNoResultFound = findViewById(R.id.tv_no_result);
        recylerViewAnnouncement = findViewById(R.id.recycler_view_announcement);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        loadMoreLay = findViewById(R.id.load_more_lay);
        resultList = new ArrayList<>();
        recylerViewAnnouncement.setLayoutManager(new LinearLayoutManager(this));
        // recylerViewAnnouncement.setNestedScrollingEnabled(false);
        announcementListAdapter = new AnnouncementListAdapter(resultList);
        recylerViewAnnouncement.setAdapter(announcementListAdapter);
        layoutDataView = findViewById(R.id.layout_data_view);
        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        empty_list_layout = findViewById(R.id.layout_empty_list);
        rootlayout = findViewById(R.id.root_layout);
        shimmerFrameLayout.startShimmer();
        handler = new Handler(Looper.myLooper());

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
        {
            runnable = new Runnable() {
                @Override
                public void run() {
                    checkInternet();
                }
            };
            checkInternet();
        } else {
            ActivityCompat.requestPermissions(this, NOTIFICATION_PERMISSION, NOTIFICATION_PERMISSION_CODE);
        }


        recylerViewAnnouncement.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Utilities.checkInternet(AnnouncementActivity.this)) {
                    if (isLastItemDisplaying(recyclerView)) {
                        if (isLoading) {
                            if (isFrom.equalsIgnoreCase("batch"))
                                hitGetAnnouncementListService();
                            else
                                hitGetCourseAnnouncementService();
                        }
                    }
                } else {
                    Utilities.makeToast(getApplicationContext(), getString(R.string.internet_connection_error));
                    // Toast.makeText(AnnouncementActivity.this, "Check internet connection", Toast.LENGTH_SHORT).show();
                }
            }

        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.checkInternet(AnnouncementActivity.this)) {
                    pageNo = 1;
                    if (resultList.size() > 0) {
                        resultList.clear();
                    }
                    if (isFrom.equalsIgnoreCase("batch"))
                        hitGetAnnouncementListService();
                    else
                        hitGetCourseAnnouncementService();
                } else {
                    Utilities.makeToast(getApplicationContext(), getString(R.string.internet_connection_error));
                }

            }
        });

        removeAnnouncementNotification();

    }

    private void removeAnnouncementNotification() {
        try {
            List<NotificationDataModel> notificationDataList = PreferenceHandler.getNotificationDataList(this);
            if (notificationDataList != null && notificationDataList.size() > 0) {
                for (int i = notificationDataList.size() - 1; i >= 0; i--) {
                    if (notificationDataList.get(i).getNotificationType().equalsIgnoreCase("announcement")) {
                        if (notificationDataList.get(i).getBatchId().equalsIgnoreCase(batchId)) {
                            notificationDataList.remove(notificationDataList.get(i));
                            PreferenceHandler.setList(this, PreferenceHandler.NOTIFICATION_DATA, notificationDataList);
                            break;
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void hitGetCourseAnnouncementService() {
        if (!swipeRefreshLayout.isRefreshing()) {
            if (pageNo > 1) {
                mProgressDialog = new ProgressView(this);

                if (loadMoreLay.getVisibility() == GONE) {
                    loadMoreLay.setVisibility(VISIBLE);
                }

               /* if (!isFinishing())
                    mProgressDialog.show();*/
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("courseId", courseId);
        map.put("pageLength", pageLength);
        map.put("page", pageNo);
        new MyClient(this).getCourseAnnouncements(map, (content, error) -> {
            shimmerFrameLayout.setVisibility(View.GONE);
            if (content != null) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (pageNo > 1) {
                        mProgressDialog.dismiss();
                    } else {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }
                }
                layoutDataView.setVisibility(View.VISIBLE);
                GetAnnouncementListResponse response = (GetAnnouncementListResponse) content;
                if (response.getStatus() == 200 && response.isSuccess()) {
                    if (response.getResult() != null && response.getResult().getData() != null && response.getResult().getData().size() > 0) {
                        resultList.addAll(response.getResult().getData());
                        setListAdapter();
                        recylerViewAnnouncement.setVisibility(View.VISIBLE);
                        empty_list_layout.setVisibility(View.GONE);
                        int size = response.getResult().getData().size();
                        if (size < Integer.parseInt(pageLength)) {
                            isLoading = false;
                        } else if (response.getResult().getTotal().equals(50))
                            isLoading = false;
                        else {
                            isLoading = true;
                            pageNo++;
                        }
                    } else {
                        if (pageNo == 1)
                            setUpNoDataVisibilty();
                        else isLoading=false;
                    }

                } else if (response.getStatus() == 403)
                    Utilities.openUnauthorizedDialog(this);
                else Utilities.makeToast(this, getString(R.string.server_error));
            } else Utilities.makeToast(this, getString(R.string.server_error));
        });
    }

    private void setUpNoDataVisibilty() {
        swipeRefreshLayout.setVisibility(View.GONE);
        recylerViewAnnouncement.setVisibility(View.GONE);
        empty_list_layout.setVisibility(View.VISIBLE);
        tvNoResultFound.setVisibility(View.VISIBLE);

    }


    private void checkInternet() {

        if (Utilities.checkInternet(this)) {
            handler.removeCallbacks(runnable);
            if (isFrom.equalsIgnoreCase("batch") && !batchId.isEmpty())
                hitGetAnnouncementListService();
            else if (!courseId.isEmpty())
                hitGetCourseAnnouncementService();
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Utilities.makeToast(getApplicationContext(), getString(R.string.internet_connection_error));
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

    private void hitGetAnnouncementListService() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));
        Call<GetAnnouncementListResponse> call = apiInterface.getAnnouncementList(headers, batchId, pageLength, String.valueOf(pageNo));
        if (!swipeRefreshLayout.isRefreshing()) {
            if (pageNo > 1) {
                mProgressDialog = new ProgressView(this);
//                mProgressDialog.show();
                if (loadMoreLay.getVisibility() == GONE) {
                    loadMoreLay.setVisibility(VISIBLE);
                }
            }
        }
        call.enqueue(new Callback<GetAnnouncementListResponse>() {
            @Override
            public void onResponse(Call<GetAnnouncementListResponse> call, Response<GetAnnouncementListResponse> response) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (pageNo > 1) {
                        if (loadMoreLay.getVisibility() == VISIBLE) {
                            loadMoreLay.setVisibility(GONE);
                        }
//                        mProgressDialog.dismiss();
                    } else {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }
                }
                layoutDataView.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200 && response.body().isSuccess()) {
                        if (response.body().getResult() != null && response.body().getResult().getData().size() > 0) {
                            empty_list_layout.setVisibility(View.GONE);
                            tvNoResultFound.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            recylerViewAnnouncement.setVisibility(View.VISIBLE);
                            resultList.addAll(response.body().getResult().getData());
                            setListAdapter();
                            if (response.body().getResult().getData().size() < 50) {
                                isLoading = false;
                            } else if (response.body().getResult().getTotal().equalsIgnoreCase("50")) {
                                isLoading = false;
                            } else {
                                isLoading = true;
                                pageNo++;
                            }
                        } else {

                            if (pageNo == 1)
                                setUpNoDataVisibilty();
                            else isLoading = false;
                        }
                    } else if (response.body().getStatus() == 403) {
                        Utilities.openUnauthorizedDialog(AnnouncementActivity.this);
                    }
                }

            }

            @Override
            public void onFailure(Call<GetAnnouncementListResponse> call, Throwable t) {
                Log.e("Retrofit Failure", t.getMessage());
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (pageNo > 1) {
                        if (loadMoreLay.getVisibility() == VISIBLE) {
                            loadMoreLay.setVisibility(GONE);
                        }
//                        mProgressDialog.dismiss();
                    } else {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                    }
                }
                layoutDataView.setVisibility(View.VISIBLE);
                Utilities.makeToast(getApplicationContext(), getString(R.string.server_error));
            }

        });


    }

    private void setListAdapter() {
        if (resultList != null && resultList.size() > 0)
            announcementListAdapter.updateList(resultList);
        if (pageNo > 1&&mProgressDialog!=null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: If");
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        checkInternet();
                    }
                };
                checkInternet();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: Else");
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        checkInternet();
                    }
                };
                checkInternet();
            }
        }
    }
}
