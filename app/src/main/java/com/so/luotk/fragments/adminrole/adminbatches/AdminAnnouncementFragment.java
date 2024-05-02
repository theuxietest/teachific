package com.so.luotk.fragments.adminrole.adminbatches;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.so.luotk.R;
import com.so.luotk.activities.adminrole.MakeAnnouncementActivity;
import com.so.luotk.adapter.adminrole.AnnouncementListAdapter;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.FragmentAdminAnnouncementBinding;
import com.so.luotk.models.output.AnnouncementData;
import com.so.luotk.models.output.GetAnnouncementListResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AdminAnnouncementFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentAdminAnnouncementBinding binding;
    private String batchId;
    private final String pageLength = "50";
    private int pageNo = 1;
    private ProgressView mProgressDialog;
    private List<AnnouncementData> announcementDataList;
    private boolean isLoading, isRefreshing, isFragmentLaoded;
    private AnnouncementListAdapter adapter;
    private Context context;
    private Activity mActivity;
    private boolean isOnActivityResultCalled;
    private long mLastClickTime=0;


    public AdminAnnouncementFragment() {
        // Required empty public constructor
    }


    public static AdminAnnouncementFragment newInstance(String param1) {
        AdminAnnouncementFragment fragment = new AdminAnnouncementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
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
        announcementDataList = new ArrayList<>();
        mProgressDialog = new ProgressView(context);
        if (getArguments() != null) {
            batchId = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminAnnouncementBinding.inflate(inflater, container, false);
        binding.announcementRecyclerView.setLayoutManager(new LinearLayoutManager(context));
//        binding.announcementRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        setClickListeners();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PreferenceHandler.readBoolean(getActivity(), PreferenceHandler.ADMIN_LOGGED_IN, false)) {
//            AdminBatchDetailsFragment.img_settings_icon.setVisibility(View.GONE);
        }
        if (!isFragmentLaoded) {
            setUpUI();
            hitService();
        } else {
            setUpUI();
            if (!isOnActivityResultCalled) {
                if (announcementDataList != null && announcementDataList.size() > 0) {
                    binding.layoutNoAnnoucements.setVisibility(View.GONE);
                    binding.layoutDataView.setVisibility(View.VISIBLE);
                    binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
                    binding.announcementRecyclerView.setVisibility(View.VISIBLE);
                    adapter.updateList(announcementDataList);
                    pageNo = 1;
                } else {
                    binding.layoutNoAnnoucements.setVisibility(View.VISIBLE);
                    binding.swipeRefreshLayout.setVisibility(View.GONE);
                    binding.announcementRecyclerView.setVisibility(View.GONE);
                }
                binding.shimmerLayout.setVisibility(View.GONE);
            }
        }
    }

    private void setUpUI() {
        adapter = new AnnouncementListAdapter((isFrom, position) -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (!isRefreshing) {
                if (!announcementDataList.isEmpty()) {
                    int id = Integer.parseInt(announcementDataList.get(position).getId());
                    if (isFrom.equalsIgnoreCase("view")) {
                        startActivityForResult(new Intent(context, MakeAnnouncementActivity.class).putExtra(PreferenceHandler.BATCH_ID, batchId)
                                .putExtra("announcement", announcementDataList.get(position).getAnnouncement())
                                .putExtra("id", announcementDataList.get(position).getId())
                                .putExtra(PreferenceHandler.BATCH_ID, batchId)
                                .putExtra(PreferenceHandler.IS_FROM, "view"), 1);
                    }
                }
            }
        });
        binding.announcementRecyclerView.setAdapter(adapter);
        binding.announcementRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Utilities.hideKeyBoard(context);
                if (Utilities.checkInternet(context)) {
                    if (isLastItemDisplaying(recyclerView) && isLoading) {
                        /*if (pageNo > 1 && !mProgressDialog.isShowing()) {
                            mProgressDialog.show();
                        }*/
                        if (pageNo > 1) {
                            if (binding.loadMoreLay.getVisibility() == GONE) {
                                binding.loadMoreLay.setVisibility(VISIBLE);
                            }
                        }
                        hitGetAnnouncementListService();
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                }
            }

        });


        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                if (Utilities.checkInternet(context)) {
                    pageNo = 1;
                    if (announcementDataList.size() > 0) {
                        announcementDataList.clear();
                    }
                    hitGetAnnouncementListService();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.txt_check_internet), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void hitService() {
        if (batchId != null) {
            if (Utilities.checkInternet(context)) {
                hitGetAnnouncementListService();
                isFragmentLaoded = true;
            } else Utilities.makeToast(context, getString(R.string.internet_connection_error));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            {
                if (Utilities.checkInternet(context)) {
                    binding.shimmerLayout.setVisibility(View.VISIBLE);
                    pageNo = 1;
                    if (announcementDataList.size() > 0) {
                        announcementDataList.clear();
                    }
                    hitGetAnnouncementListService();
                    isOnActivityResultCalled = true;
                } else Utilities.makeToast(context, getString(R.string.internet_connection_error));
            }
        } else
            isOnActivityResultCalled = false;
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }


    private void hitGetAnnouncementListService() {
        Map<String, String> map = new HashMap<>();
        map.put("batchId", batchId);
        map.put("pageLength", pageLength);
        map.put("page", String.valueOf(pageNo));
        new MyClient(context).hitGetbatchAnnouncementService(map, (content, error) -> {
            if (content != null) {
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    if (pageNo > 1) {
                        /*if (mProgressDialog != null)
                            mProgressDialog.dismiss();*/
                        if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                            binding.loadMoreLay.setVisibility(GONE);
                        }
                    } else {
                        binding.shimmerLayout.stopShimmer();
                        binding.shimmerLayout.setVisibility(View.GONE);
                    }
                }
                binding.layoutDataView.setVisibility(View.VISIBLE);
                GetAnnouncementListResponse response = (GetAnnouncementListResponse) content;
                if (response.getStatus() == 200 && response.isSuccess()) {
                    if (response.getResult() != null) {
                        if (response.getResult().getData() != null && response.getResult().getData().size() > 0) {
                            announcementDataList.addAll(response.getResult().getData());
                            adapter.updateList(announcementDataList);
                            binding.layoutNoAnnoucements.setVisibility(View.GONE);
                            binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
                            binding.announcementRecyclerView.setVisibility(View.VISIBLE);
                            if (response.getResult().getData().size() < 50) {
                                isLoading = false;
                            } else if (response.getResult().getTotal().equalsIgnoreCase("50")) {
                                isLoading = false;
                            } else {
                                isLoading = true;
                                pageNo++;
                            }
                        } else {
                            if (pageNo == 1) {
                                binding.layoutNoAnnoucements.setVisibility(View.VISIBLE);
                                binding.swipeRefreshLayout.setVisibility(View.GONE);
                                binding.announcementRecyclerView.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        binding.layoutNoAnnoucements.setVisibility(View.VISIBLE);
                        binding.swipeRefreshLayout.setVisibility(View.GONE);
                        binding.announcementRecyclerView.setVisibility(View.GONE);
                    }


                } else if (response.getStatus() == 403) {
                    Utilities.openUnauthorizedDialog(context);
                } else {
                    Utilities.makeToast(context, getString(R.string.server_error));
                }

            } else {
                Utilities.makeToast(context, getString(R.string.server_error));
            }
            isRefreshing = false;
            /*if (mProgressDialog != null)
                mProgressDialog.dismiss();*/
            if (binding.loadMoreLay.getVisibility() == VISIBLE) {
                binding.loadMoreLay.setVisibility(GONE);
            }
            binding.shimmerLayout.setVisibility(View.GONE);
            isOnActivityResultCalled = false;

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
        });


    }

    private void setClickListeners() {
        binding.btnAddAnnoucement.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            startActivityForResult(new Intent(context, MakeAnnouncementActivity.class).putExtra(PreferenceHandler.BATCH_ID, batchId)
                    .putExtra(PreferenceHandler.IS_FROM, "batch"), 1);

        });
    }
}