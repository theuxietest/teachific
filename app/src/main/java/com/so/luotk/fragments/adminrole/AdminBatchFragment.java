package com.so.luotk.fragments.adminrole;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.so.luotk.R;
import com.so.luotk.activities.adminrole.AdminMainScreen;
import com.so.luotk.activities.adminrole.CreateBatchActivity;
import com.so.luotk.adapter.BatchListAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.FragmentAdminBatchBinding;
import com.so.luotk.fragments.adminrole.adminbatches.AdminBatchDetailsFragment;
import com.so.luotk.models.output.BatchListResult;
import com.so.luotk.models.output.GetBatchListResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminBatchFragment extends Fragment {
    private FragmentAdminBatchBinding binding;
    private BatchListAdapter adapter;
    private ArrayList<BatchListResult> batchListResults;
    private boolean isDarkMode;
    private APIInterface apiInterface;
    private ProgressView mProgressDialog;
    private boolean isFirstInternetToastDone;
    private Handler handler;
    private Runnable runnable;
    private boolean isSearchOpen, isRefreshing;
    private Context context;
    private Activity mActivity;


    public AdminBatchFragment() {
        // Required empty public constructor
    }


    public static AdminBatchFragment newInstance() {
        AdminBatchFragment fragment = new AdminBatchFragment();
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
        batchListResults = new ArrayList<>();
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminBatchBinding.inflate(inflater, container, false);
        AdminMainScreen activity = (AdminMainScreen) getActivity();
        if (activity != null)
            activity.showToolbar();
        setUpUI();
        setClickListeners();
        return binding.getRoot();
    }

    private void setClickListeners() {
        binding.fab.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), CreateBatchActivity.class));
        });

    }

    private void setUpUI() {

        apiInterface = ApiUtils.getApiInterface();
        handler = new Handler(Looper.myLooper());
        runnable = () -> checkInternet();
        int currentNightMode = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                isDarkMode = false;
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                isDarkMode = true;
                break;
        }
        binding.recyclerBatchList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.shimmerLayout.startShimmer();
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            isSearchOpen = false;
            isRefreshing = true;
            binding.searchView.setQuery("", false);
            binding.searchView.clearFocus();
            Utilities.hideKeyBoard(getActivity());
            if (Utilities.checkInternet(getContext())) {
                if (!isSearchOpen)
                    hitGetBatchListService();
            } else {
                Utilities.makeToast(getContext(), getString(R.string.internet_connection_error));
            }

        });

        binding.searchView.setVisibility(View.GONE);
        checkInternet();
        binding.searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        Utilities.hideKeyBoard(getActivity());
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                isSearchOpen = true;
                if (adapter != null) {
                    adapter.getFilter().filter(query);
                    if (adapter.getItemCount() < 1) {
                        binding.tvNoResults.setVisibility(View.VISIBLE);
                        binding.recyclerBatchList.setVisibility(View.GONE);
                    } else {
                        binding.tvNoResults.setVisibility(View.GONE);
                        binding.recyclerBatchList.setVisibility(View.VISIBLE);
                    }
                    if (TextUtils.isEmpty(query)) {
                        isSearchOpen = false;
                        binding.tvNoResults.setVisibility(View.GONE);
                        binding.recyclerBatchList.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSearchOpen = true;
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                    if (adapter.getItemCount() < 1) {
                        binding.tvNoResults.setVisibility(View.VISIBLE);
                        binding.recyclerBatchList.setVisibility(View.GONE);
                    } else {
                        binding.tvNoResults.setVisibility(View.GONE);
                        binding.recyclerBatchList.setVisibility(View.VISIBLE);
                    }
                    if (TextUtils.isEmpty(newText)) {
                        isSearchOpen = false;
                        binding.tvNoResults.setVisibility(View.GONE);
                        binding.recyclerBatchList.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });

    }


    private void openNextFragment(Fragment fragment) {
        if (getActivity() != null && isAdded())
            getParentFragmentManager().beginTransaction().replace(R.id.admin_container, fragment).addToBackStack(null).commit();
    }

    private void hitGetBatchListService() {
        batchListResults.clear();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(getContext(), PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(getContext(), PreferenceHandler.DEVICE_ID, ""));
        Call<GetBatchListResponse> call = apiInterface.getAdminBatchList(headers);
        call.enqueue(new Callback<GetBatchListResponse>() {
            @Override
            public void onResponse(Call<GetBatchListResponse> call, Response<GetBatchListResponse> response) {
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                } else if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                } else {
                    binding.shimmerLayout.stopShimmer();
                    binding.shimmerLayout.setVisibility(View.GONE);
                }
                binding.layoutBatchList.setVisibility(View.VISIBLE);
                binding.searchView.setVisibility(View.VISIBLE);
                binding.layoutNoAnyBatch.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null && response.body().getResult().size() > 0) {
                                batchListResults = response.body().getResult();
                                setBatchListAdapter();
                            } else {
                                binding.layoutBatchList.setVisibility(View.GONE);
                                binding.layoutNoAnyBatch.setVisibility(View.VISIBLE);
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(getContext());
                    } else {
                        Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<GetBatchListResponse> call, Throwable t) {
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    binding.shimmerLayout.stopShimmer();
                    binding.shimmerLayout.setVisibility(View.GONE);
                }

                Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkInternet() {
        if (Utilities.checkInternet(getContext())) {
            handler.removeCallbacks(runnable);
            hitGetBatchListService();
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Utilities.makeToast(getContext(), getString(R.string.internet_connection_error));
                isFirstInternetToastDone = true;
            }
        }
    }

    private void setBatchListAdapter() {
        Set<BatchListResult> set = new LinkedHashSet<>(batchListResults);
        batchListResults.clear();
        batchListResults.addAll(set);
        if (adapter == null)
            adapter = new BatchListAdapter(getContext(), batchListResults, "adminrole", isDarkMode);
        else adapter.updateList(batchListResults);
        binding.recyclerBatchList.setAdapter(adapter);
        isRefreshing = false;
        adapter.SetOnItemClickListener(position -> {
            binding.searchView.setQuery("", false);
            binding.searchView.clearFocus();
            if (!isRefreshing) {
                if (adapter.getBatchList() != null && adapter.getBatchList().size() > 0) {
                    String batchId = adapter.getBatchList().get(position).getId();
                    openNextFragment(AdminBatchDetailsFragment.newInstance(batchId));
                }
            }
        });

    }

}