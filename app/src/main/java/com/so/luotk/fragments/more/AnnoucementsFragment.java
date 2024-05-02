package com.so.luotk.fragments.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.so.luotk.R;
import com.so.luotk.adapter.AnnouncementListAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.models.output.GetAnnouncementListResult;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;

public class AnnoucementsFragment extends Fragment {
    private static final String ARGS1 = "ARGS1";
    private Toolbar toolbar;
    private String titlerName;
    private APIInterface apiInterface;
    private RecyclerView recylerViewAnnouncement;
    private AnnouncementListAdapter announcementListAdapter;
    private TextView tvNoResultFound;
    private ProgressView mProgressDialog;
    private String batchId;
    private ArrayList<GetAnnouncementListResult> resultList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View layoutDataView, empty_list_layout;

    public AnnoucementsFragment() {

    }

    public static AnnoucementsFragment newInstance(String titlerName, String batchId) {
        AnnoucementsFragment fragment = new AnnoucementsFragment();
        Bundle args = new Bundle();
        args.putString(ARGS1, titlerName);
        args.putString(PreferenceHandler.BATCH_ID, batchId);
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
//     getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (getArguments() != null) {
            batchId = getArguments().getString(PreferenceHandler.BATCH_ID);
            titlerName = getArguments().getString(ARGS1);
        }
        View view = inflater.inflate(R.layout.fragment_annoucements, container, false);
        setToolbar(view);
        setupUI(view);
        return view;
    }


    private void setToolbar(View view) {
        //toolbar setup
        toolbar = view.findViewById(R.id.toolbar);
        if (titlerName != null) {
            toolbar.setTitle(titlerName);
        }
        toolbar.setNavigationIcon(Utilities.setNavigationIconColor(getContext()));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void onBackPressed() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.popBackStack();
    }

    private void setupUI(View view) {
        layoutDataView = view.findViewById(R.id.layout_data_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        tvNoResultFound = view.findViewById(R.id.tv_no_result);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        empty_list_layout = view.findViewById(R.id.layout_empty_list);
        shimmerFrameLayout.startShimmer();
        setProgressBarTimer();
    }

    private void setProgressBarTimer() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        layoutDataView.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                        tvNoResultFound.setVisibility(View.VISIBLE);
                        empty_list_layout.setVisibility(View.VISIBLE);

                    }
                }, 1000);
    }
}
