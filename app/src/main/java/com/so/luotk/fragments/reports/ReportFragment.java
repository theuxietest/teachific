package com.so.luotk.fragments.reports;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.card.MaterialCardView;
import com.so.luotk.R;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.client.MyClient;
import com.so.luotk.activities.VideoListActivity;
import com.so.luotk.firebase.NotificationDataModel;
import com.so.luotk.models.output.Data;
import com.so.luotk.models.output.ReportCountResponse;
import com.so.luotk.models.output.ReportCountResult;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class ReportFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout layoutAssignmentSubmitted, layoutTestAttempted;
    private boolean isBatchCreated;
    private TextView tvNoTestAttempted, tvNoAssignSubmitted, tvAssignmentCount, tvTestCount;
    private CardView cardViewTest, cardViewAssignment;
    private APIInterface apiInterface;
    private ArrayList<Data> resultList;
    private String batchId;
    private final String pageLength = "25";
    private final String pageNo = "1";
    private String totalAssignments;
    private String totalTests;
    private ShimmerFrameLayout shimmerFrameLayout;
    private View layoutDataView;
    private Runnable runnable;
    private Handler handler;
    private boolean isFirstInternetToastDone;
    private View rootLayout;
    private ImageView new_notification;
    private Context context;
    private View test_shimmer;
    private MaterialCardView card_view_test;
    private int assignmentCount = 0, testsCount = 0;

    private List<NotificationDataModel> notificationDataList;


    public ReportFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utilities.restrictScreenShot(getActivity());
//   getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        setupUI(view);
        Log.d("ReporrtFrag", "onCreateView: ");
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }


    private void setupUI(View view) {
        apiInterface = ApiUtils.getApiInterface();
        resultList = new ArrayList<>();
        isBatchCreated = PreferenceHandler.readBoolean(context, PreferenceHandler.CREATED_BATCH, false);
        layoutDataView = view.findViewById(R.id.layout_data_view);
        layoutAssignmentSubmitted = view.findViewById(R.id.layout_assign_submitted);
        layoutTestAttempted = view.findViewById(R.id.layout_test_attempted);
        tvNoAssignSubmitted = view.findViewById(R.id.tv_no_assignment_submitted);
        tvNoTestAttempted = view.findViewById(R.id.tv_no_test_attempted);
        cardViewAssignment = view.findViewById(R.id.card_view_assignment);
        cardViewTest = view.findViewById(R.id.card_view_test);
        tvAssignmentCount = view.findViewById(R.id.tv_assignment_count);
        new_notification = view.findViewById(R.id.new_notification);
        rootLayout = view.findViewById(R.id.frame_layout_report);
        tvTestCount = view.findViewById(R.id.tv_test_count);
        tvNoTestAttempted.setVisibility(View.VISIBLE);
        layoutTestAttempted.setVisibility(View.GONE);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_layout);
        test_shimmer = view.findViewById(R.id.test_shimmer);
        card_view_test = view.findViewById(R.id.card_view_test);
        shimmerFrameLayout.startShimmer();
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.white));
        handler = new Handler(Looper.myLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        checkInternet();

        if (PreferenceHandler.readString(context, PreferenceHandler.IS_LITE_APP, "0").equalsIgnoreCase("1")) {
            test_shimmer.setVisibility(View.GONE);
            card_view_test.setVisibility(View.GONE);
        } else {
            test_shimmer.setVisibility(View.VISIBLE);
            card_view_test.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_view_test:
                ReportsTabbedFragment fragment1 = ReportsTabbedFragment.newInstance("test", false);
                openNextFragment(fragment1);
                break;

            case R.id.card_view_assignment:
                ReportAssignmentTestFragment fragment2 = ReportAssignmentTestFragment.newInstance("assignment", false);
                openNextFragment(fragment2);
                break;


            default:
                break;
        }

    }

    private void checkInternet() {
        if (context != null && Utilities.checkInternet(context)) {
            handler.removeCallbacks(runnable);
            hitGetReportCountService();
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Utilities.makeToast(context, getString(R.string.internet_connection_error));
                isFirstInternetToastDone = true;
            }
        }
    }


    private void openNextActivity(String topicName, String isFrom) {
        Intent intent = new Intent(context, VideoListActivity.class);
        intent.putExtra(PreferenceHandler.TOPIC_NAME, topicName);
        intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
        startActivity(intent);
    }

    private void openNextFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void hitGetReportCountService() {
        new MyClient(context).getReportCount((content, error) -> {
            shimmerFrameLayout.setVisibility(View.GONE);
            layoutDataView.setVisibility(View.VISIBLE);
            if (content != null) {
                ReportCountResponse response = (ReportCountResponse) content;
                if (response.getStatus() != null && response.getStatus() == 200) {
                    if (response.getSuccess() && response.getResult() != null) {
                        ReportCountResult result = response.getResult();
                        if (result.getBatchAssignmentCount() != null)
                            assignmentCount = result.getBatchAssignmentCount();
                        if (result.getBatchTestCount() != null && result.getCourseTestCount() != null)
                            testsCount = result.getBatchTestCount() + result.getCourseTestCount();
                        setAssignmentCardView();
                        setTestCardView();
                    }
                }

            } else Utilities.makeToast(context, getString(R.string.server_error));
        });
    }

    private void setTestCardView() {
        if (testsCount > 0) {
            tvNoTestAttempted.setVisibility(View.GONE);
            layoutTestAttempted.setVisibility(View.VISIBLE);
            cardViewTest.setOnClickListener(ReportFragment.this);
            tvTestCount.setText(String.valueOf(testsCount));
            setNotificationView();
        } else {
            tvNoTestAttempted.setVisibility(View.VISIBLE);
            layoutTestAttempted.setVisibility(View.GONE);
            cardViewTest.setForeground(null);
        }
    }

    private void setAssignmentCardView() {
        if (assignmentCount > 0) {
            tvNoAssignSubmitted.setVisibility(View.GONE);
            layoutAssignmentSubmitted.setVisibility(View.VISIBLE);
            cardViewAssignment.setOnClickListener(ReportFragment.this);
            tvAssignmentCount.setText(String.valueOf(assignmentCount));
        } else {
            tvNoAssignSubmitted.setVisibility(View.VISIBLE);
            layoutAssignmentSubmitted.setVisibility(View.GONE);
            cardViewAssignment.setForeground(null);
        }
    }

    private void setNotificationView() {
        notificationDataList = PreferenceHandler.getNotificationDataList(context);
        try {
            if (notificationDataList != null && notificationDataList.size() > 0) {
                for (int i = 0; i < notificationDataList.size(); i++) {
                    if (notificationDataList.get(i).getNotificationType().equals("testEvaluate")) {
                        new_notification.setVisibility(View.VISIBLE);
                        break;
                    } else {
                        new_notification.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
