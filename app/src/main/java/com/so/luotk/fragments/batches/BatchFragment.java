package com.so.luotk.fragments.batches;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.so.luotk.R;
import com.so.luotk.activities.WelcomeActivityNew;
import com.so.luotk.adapter.BatchListAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.firebase.NotificationDataModel;
import com.so.luotk.fragments.SendJoinRequestFragment;
import com.so.luotk.listeners.OnItemSelectedListener;
import com.so.luotk.activities.BatchDetailActivity;
import com.so.luotk.models.output.BatchListResult;
import com.so.luotk.models.output.GetBatchListResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.VISIBLE;

public class BatchFragment extends Fragment implements OnItemSelectedListener, View.OnClickListener {
    private RecyclerView recylerViewBatches;
    private ArrayList<BatchListResult> batchLists;
    private BatchListAdapter batchListAdapter;
    private LinearLayout layoutBatchList;
    private RelativeLayout layoutNoAnyBatch;
    private RelativeLayout layoutBatch;
    private ProgressView mProgressDialog;
    private boolean isBatchCreated, isStaticLoggedIn;
    private FloatingActionButton fab;
    private APIInterface apiInterface;
    private String dayNames = "";
    private ArrayList<String> dayList;
    private ArrayList<String> startTimeList;
    private ArrayList<String> endTimeList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Handler handler;
    private Runnable runnable;
    private View rootLayout;
    private boolean isFirstInternetToastDone, isFromNotification, isDarkMode;
    private LinearLayoutManager recylerViewLinearLayout;
    private List<NotificationDataModel> notificationDataList;
    int currentNightMode;
    private Context context;
    private Activity mActivity;
    private boolean isRefreshing;
    private long mLastClickTime=0;
//    DatabaseReference groupRef;
    private SearchView searchView;
    private boolean isSearchOpen;
    private TextView tvNoResults;

    public BatchFragment() {
    }


    public static BatchFragment newInstance() {
        BatchFragment fragment = new BatchFragment();
        return fragment;
    }

    public BatchFragment(boolean isFromNotification) {
        this.isFromNotification = isFromNotification;
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
        currentNightMode = mActivity.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                isDarkMode = false;
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                isDarkMode = true;
                break;
        }
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utilities.restrictScreenShot(getActivity());
//   getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        View view = inflater.inflate(R.layout.fragment_batch, container, false);
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.white));
//        groupRef = FirebaseDatabase.getInstance().getReference().child(PreferenceHandler.BATCH_GROUP);
        setupUI(view);
        return view;
    }

    private void setupUI(View view) {
        notificationDataList = PreferenceHandler.getNotificationDataList(context);
        apiInterface = ApiUtils.getApiInterface();
        isBatchCreated = PreferenceHandler.readBoolean(context, PreferenceHandler.CREATED_BATCH, false);
        isStaticLoggedIn = PreferenceHandler.readBoolean(context, PreferenceHandler.STATIC_LOGGED_IN, false);
        recylerViewBatches = view.findViewById(R.id.batch_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        layoutBatchList = view.findViewById(R.id.layout_batch_list);
        layoutNoAnyBatch = view.findViewById(R.id.layout_no_any_batch);
        layoutBatch = view.findViewById(R.id.layout_batch);
        rootLayout = view.findViewById(R.id.frame_layout_batch);
        batchLists = new ArrayList<>();
        recylerViewLinearLayout = new LinearLayoutManager(context);
        recylerViewBatches.setLayoutManager(recylerViewLinearLayout);
        recylerViewBatches.setNestedScrollingEnabled(true);
        fab = view.findViewById(R.id.fab);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        searchView = view.findViewById(R.id.search_view);
        tvNoResults = view.findViewById(R.id.tv_no_results);
        shimmerFrameLayout.startShimmer();
        handler = new Handler(Looper.myLooper());
        // finally change the color
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.white));
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        checkInternet();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendJoinRequestFragment sendJoinRequestFragment = SendJoinRequestFragment.newInstance(false);
                openNextFragment(sendJoinRequestFragment);
                //  sendJoinRequest();
            }
        });

        if (isStaticLoggedIn) {
            fab.hide();
        } else {
            fab.show();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                if (Utilities.checkInternet(context)) {
                    hitGetBatchListService();

                } else {
                    Utilities.makeToast(context, getString(R.string.internet_connection_error));
                }

            }
        });

        /*searchView.setOnCloseListener(new androidx.appcompat.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("TAG", "onClose: ");
                Utilities.hideKeyBoard(getActivity());
                return false;
            }
        });*/

        searchView.setVisibility(VISIBLE);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        Utilities.hideKeyBoard(getActivity());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                isSearchOpen = true;
                if (batchListAdapter != null) {
                    batchListAdapter.getFilter().filter(query);
                    if (batchListAdapter.getItemCount() < 1) {
                        tvNoResults.setVisibility(View.VISIBLE);
                        recylerViewBatches.setVisibility(View.GONE);
                    } else {
                        tvNoResults.setVisibility(View.GONE);
                        recylerViewBatches.setVisibility(View.VISIBLE);
                    }
                    if (TextUtils.isEmpty(query)) {
                        isSearchOpen = false;
                        tvNoResults.setVisibility(View.GONE);
                        recylerViewBatches.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSearchOpen = true;
                if (batchListAdapter != null) {
                    batchListAdapter.getFilter().filter(newText);
                    if (batchListAdapter.getItemCount() < 1) {
                        tvNoResults.setVisibility(View.VISIBLE);
                        recylerViewBatches.setVisibility(View.GONE);
                    } else {
                        tvNoResults.setVisibility(View.GONE);
                        recylerViewBatches.setVisibility(View.VISIBLE);
                    }
                    if (TextUtils.isEmpty(newText)) {
                        isSearchOpen = false;
                        tvNoResults.setVisibility(View.GONE);
                        recylerViewBatches.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });


    }

    private void checkInternet() {
        if (Utilities.checkInternet(context)) {
            handler.removeCallbacks(runnable);
            if (isBatchCreated) {
                ArrayList<BatchListResult> savedBatchList = (ArrayList<BatchListResult>) PreferenceHandler.getSavedBatchList(context);
                if (isFromNotification) {
                    hitGetBatchListService();
                } else {
                    if (savedBatchList != null && savedBatchList.size() > 0) {
                        batchLists.addAll(savedBatchList);
                        setBatchListAdapter();
                    } else {
                        hitGetBatchListService();
                    }
                }
            } else {
                hitGetBatchListService();
            }
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Utilities.makeToast(context, getString(R.string.internet_connection_error));
                isFirstInternetToastDone = true;
            }
        }
    }

    private void openNextFragment(Fragment fragment) {
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onItemSelected(int position, Object object) {

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setProgressBarTimer() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        mProgressDialog.dismiss();
                        PreferenceHandler.writeBoolean(context, PreferenceHandler.LOGGED_IN, false);
                        Intent intent = new Intent(context, WelcomeActivityNew.class);
                        startActivity(intent);
                        mActivity.finish();

                    }
                }, 3000);
    }

    private void hitGetBatchListService() {
        batchLists.clear();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        Call<GetBatchListResponse> call = apiInterface.getBatchList(headers);
        if (!swipeRefreshLayout.isRefreshing()) {
            //  mProgressDialog = new ProgressView(getActivity());
            //  mProgressDialog.show();
        }

        call.enqueue(new Callback<GetBatchListResponse>() {
            @Override
            public void onResponse(Call<GetBatchListResponse> call, Response<GetBatchListResponse> response) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    //mProgressDialog.dismiss();
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getStatus() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null && response.body().getResult().size() > 0) {
                                PreferenceHandler.saveBatchList(context, PreferenceHandler.BATCH_LIST, null);
                                layoutBatchList.setVisibility(View.VISIBLE);
                                layoutNoAnyBatch.setVisibility(View.GONE);
                                if (!isBatchCreated) {
                                    PreferenceHandler.writeBoolean(context, PreferenceHandler.CREATED_BATCH, true);
                                }
                                batchLists.addAll(response.body().getResult());
                                saveBatchListLocally();
                                setBatchListAdapter();
                            } else {
                                layoutBatchList.setVisibility(View.GONE);
                                layoutNoAnyBatch.setVisibility(View.VISIBLE);
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(context);
                        //openUnauthorizedDialog();
                    } else {
                        Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<GetBatchListResponse> call, Throwable t) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    //mProgressDialog.dismiss();
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveBatchListLocally() {
        if (batchLists != null && batchLists.size() > 0)
            PreferenceHandler.saveBatchList(context, PreferenceHandler.BATCH_LIST, batchLists);
    }

    private void setBatchListAdapter() {
        if (shimmerFrameLayout != null && shimmerFrameLayout.isShimmerStarted()) {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            layoutBatchList.setVisibility(View.VISIBLE);
            layoutNoAnyBatch.setVisibility(View.GONE);
        }
        Set<BatchListResult> set = new LinkedHashSet<>(batchLists);
        batchLists.clear();
        batchLists.addAll(set);
        Collections.reverse(batchLists);
        if (batchListAdapter == null) {
            batchListAdapter = new BatchListAdapter(context, batchLists, "student", isDarkMode);
        } else batchListAdapter.updateList(batchLists);
        recylerViewBatches.setAdapter(batchListAdapter);
        if (notificationDataList != null && notificationDataList.size() > 0) {
            int bigList = batchLists.size() > notificationDataList.size() ? 1 : 0;
            if (bigList == 1) {
                for (int i = 0; i < notificationDataList.size(); i++) {
                    for (int j = 0; j < batchLists.size(); j++) {
                        if (notificationDataList.get(i).getNotificationType() != null && notificationDataList.get(i).getBatchId().equalsIgnoreCase(batchLists.get(j).getId())) {
                            batchLists.get(j).setNewBatch(true);
                            batchLists.get(j).setNotificationType(notificationDataList.get(i).getNotificationType());
                            batchListAdapter.updateList(batchLists);
                            //  batchListAdapter.notifyItemRangeChanged(j, batchLists.size());

                        }
                    }
                }
            } else {
                for (int i = 0; i < batchLists.size(); i++) {
                    for (int j = 0; j < notificationDataList.size(); j++) {
                        if (notificationDataList != null && notificationDataList.get(j).getNotificationType() != null && notificationDataList.get(j).getBatchId().equalsIgnoreCase(batchLists.get(i).getId())) {
                            batchLists.get(i).setNewBatch(true);
                            batchLists.get(i).setNotificationType(notificationDataList.get(j).getNotificationType());
                            batchListAdapter.updateList(batchLists);
                            //  batchListAdapter.notifyItemRangeChanged(i, batchLists.size());


                        }
                    }
                }
            }
        }
        isRefreshing = false;
        setClickListener();

    }

    private void callScrollHandler(int position) {
        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                recylerViewBatches.smoothScrollToPosition(position);
            }
        }, 1000);
    }


    /**
     * Method to set click listener on recycler view
     */
    private void setClickListener() {
        batchListAdapter.SetOnItemClickListener(new BatchListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                searchView.setQuery("", false);
                searchView.clearFocus();
                Utilities.hideKeyBoard(getActivity());
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (!isRefreshing) {
//                    createBatchGroup(batchLists.get(position).getId());
                    convertStringToJson(batchLists.get(position).getDays_time());
                    Intent intent = new Intent(context, BatchDetailActivity.class);
                    intent.putExtra(PreferenceHandler.BATCH_ID, batchLists.get(position).getId());
                    try {
                        notificationDataList = PreferenceHandler.getNotificationDataList(context);
                        if (notificationDataList != null && notificationDataList.size() > 0)
                            for (int i = notificationDataList.size() - 1; i >= 0; i--) {
                                if (notificationDataList.get(i).getBatchId().equalsIgnoreCase(batchLists.get(position).getId())
                                        && notificationDataList.get(i).getNotificationType().equalsIgnoreCase("addStudent")) {
                                    intent.putExtra(PreferenceHandler.NOTIFICATION_TYPE, notificationDataList.get(i).getNotificationType());
                                    notificationDataList.remove(notificationDataList.get(i));
                                    batchLists.get(position).setNewBatch(false);
                                    batchListAdapter.notifyItemChanged(position);
                                    PreferenceHandler.setList(context, PreferenceHandler.NOTIFICATION_DATA, notificationDataList);
                                    break;
                                }
                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (dayList.size() > 0) {
                        intent.putExtra(PreferenceHandler.DAYS_NAME_LIST, dayList);
                    }
                    if (startTimeList.size() > 0) {
                        intent.putExtra(PreferenceHandler.START_TIME_LIST, startTimeList);
                    }
                    if (endTimeList.size() > 0) {
                        intent.putExtra(PreferenceHandler.END_TIME_LIST, endTimeList);
                    }
                    startActivity(intent);


                }
            }
        });
    }

    private void convertStringToJson(String response) {
        dayList = new ArrayList<>();
        startTimeList = new ArrayList<>();
        endTimeList = new ArrayList<>();
        try {
            dayList.clear();
            startTimeList.clear();
            endTimeList.clear();
            JSONObject jsonObject = (JSONObject) new JSONObject(response);
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONObject value = (JSONObject) jsonObject.get(key);
                    String day = (String) value.get("day");
                    String startTime = (String) value.get("startTime");
                    String endTime = (String) value.get("endTime");
                    dayList.add(day);
                    startTimeList.add(startTime);
                    endTimeList.add(endTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            StringBuilder sbString = new StringBuilder();
            for (String days : dayList) {
                sbString.append(days.charAt(0)).append(",");
            }
            dayNames = sbString.toString();
            if (dayNames.length() > 0)
                dayNames = dayNames.substring(0, dayNames.length() - 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openUnauthorizedDialog() {
        Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_unauthorized_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDialog.show();
        mDialog.getWindow().setAttributes(lp);
        //mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView btnOk = mDialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = new ProgressView(context);
                mProgressDialog.show();
                setProgressBarTimer();

            }
        });
        mDialog.show();

    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }


    @Override
    public void onClick(View v) {
    /*    if (v.getId() == R.id.img_cart) {
            FeeStructureFragment feeStructureFragment = new FeeStructureFragment();
            openNextFragment(feeStructureFragment);
        }*/
    }
    /*private void createBatchGroup(String batch_id) {

        groupRef.child("fk_"+batch_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                } else {
                    groupRef.child("fk_"+batch_id).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        *//*groupRef.child("fk_"+batch_id).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });*//*
    }*/

}
