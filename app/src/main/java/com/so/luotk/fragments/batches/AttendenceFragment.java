package com.so.luotk.fragments.batches;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import com.so.luotk.R;
import com.so.luotk.adapter.MonthlyAttendenceListAdpter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.FragmentAttendenceBinding;
import com.so.luotk.firebase.NotificationDataModel;
import com.so.luotk.models.input.GetAttendenceInput;
import com.so.luotk.models.output.GetAttendenceResponse;
import com.so.luotk.models.output.GetAttendenceResult;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class AttendenceFragment extends Fragment implements View.OnClickListener {
    private FragmentAttendenceBinding binding;
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private RecyclerView recylerViewAttendence;
    private ArrayList<GetAttendenceResult> attendanceList;

    private MonthlyAttendenceListAdpter monthlyAttendenceListAdpter;
    private TextView tvCurrentMonth;
    private SimpleDateFormat month_format, year_format;
    private DatePickerDialog datePickerDialog;
    private String batchId;
    private String currentYear;
    private String currentMonth;
    private View layoutDataView, root_layout, layoutEmptyList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Handler handler;
    private Runnable runnable;
    private boolean isFirstInternetToastDone;
    private APIInterface apiInterface;
    private String batchStartDate;
    private int batchStartYear, batchStartMonth, mYear, mMonth, mDay;
    private ProgressView mProgressDialog;
    private Date minDate;
    private boolean isFragmentLoaded;
    private Context context;
    private Activity mActivity;
    private View view_below_month;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    public AttendenceFragment() {

    }

    public static AttendenceFragment newInstance(String batchId) {
        AttendenceFragment fragment = new AttendenceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, batchId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attendanceList = new ArrayList<>();
        if (getArguments() != null) {
            batchId = getArguments().getString(ARG_PARAM1);
        }
        Utilities.restrictScreenShot(getActivity());
//     getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        handler = new Handler(Looper.myLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAttendenceBinding.inflate(inflater, container, false);
        apiInterface = ApiUtils.getApiInterface();
        Utilities.hideKeyBoard(context);

        root_layout = binding.getRoot();
        recylerViewAttendence = binding.attendenceRecyclerView;
        recylerViewAttendence.setLayoutManager(new LinearLayoutManager(context));
        recylerViewAttendence.setNestedScrollingEnabled(false);
        layoutDataView = binding.layoutDataView;
        layoutEmptyList = binding.layoutEmptyList;
        shimmerFrameLayout = binding.shimmerLayout;
        tvCurrentMonth = binding.tvCurrentMonth;
        tvCurrentMonth.setOnClickListener(this);
        return binding.getRoot();
    }

    private void setupUI() {

        month_format = new SimpleDateFormat("MMM");
        currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        currentMonth = checkDigit(Calendar.getInstance().get(Calendar.MONTH) + 1);
        tvCurrentMonth.setText(new DateFormatSymbols().getShortMonths()[mMonth] + ", " + mYear);
        shimmerFrameLayout.startShimmer();
        // removeAttendenceNotification();

    }


    private void checkInternet() {
        if (Utilities.checkInternet(context)) {
            handler.removeCallbacks(runnable);
            hitMarkAttendenceService(currentYear, currentMonth);
            isFragmentLoaded = true;
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Utilities.makeToast(context, getString(R.string.internet_connection_error));
                isFirstInternetToastDone = true;
            }

        }
    }

    private void setListAdapter() {
        if (attendanceList != null && attendanceList.size() > 0) {
            if (monthlyAttendenceListAdpter == null) {
                monthlyAttendenceListAdpter = new MonthlyAttendenceListAdpter(context, attendanceList);
                recylerViewAttendence.setAdapter(monthlyAttendenceListAdpter);
            } else monthlyAttendenceListAdpter.notifyDataSetChanged();
        } else {
            recylerViewAttendence.setVisibility(GONE);
            layoutEmptyList.setVisibility(View.VISIBLE);
        }

    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }


    @Override
    public void onResume() {
        super.onResume();
        Utilities.hideKeyBoard(getContext());
        if (!isFragmentLoaded) {
            setupUI();
            checkInternet();


        } else {
            if (monthlyAttendenceListAdpter != null)
                recylerViewAttendence.setAdapter(monthlyAttendenceListAdpter);
            setupUI();
            layoutDataView.setVisibility(View.VISIBLE);
            tvCurrentMonth.setVisibility(View.VISIBLE);
            binding.viewBelowMonth.setVisibility(View.VISIBLE);
            shimmerFrameLayout.setVisibility(GONE);
            recylerViewAttendence.setVisibility(View.VISIBLE);
            layoutEmptyList.setVisibility(GONE);
            setListAdapter();
        }

    }

    @Override
    public void onClick(View v) {
        if (v == tvCurrentMonth) {
            openDatePickerDialog();

        }
    }

    private void hitMarkAttendenceService(String year, String month) {
        attendanceList.clear();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        String year_month = year + "-" + month;
        GetAttendenceInput input = new GetAttendenceInput();
        input.setBatchId(batchId);
        input.setYear_month(year_month);
        Call<GetAttendenceResponse> call = apiInterface.getStudentAttendence(headers, input);
        if (!shimmerFrameLayout.isShimmerStarted()) {
            mProgressDialog = new ProgressView(context);
            mProgressDialog.show();
        }
        call.enqueue(new Callback<GetAttendenceResponse>() {
            @Override
            public void onResponse(Call<GetAttendenceResponse> call, Response<GetAttendenceResponse> response) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }
                layoutDataView.setVisibility(View.VISIBLE);
                tvCurrentMonth.setVisibility(View.VISIBLE);
                binding.viewBelowMonth.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null && response.body().getResult().size() > 0) {
                                recylerViewAttendence.setVisibility(View.VISIBLE);
                                layoutEmptyList.setVisibility(GONE);
                                attendanceList.addAll(response.body().getResult());
                                setListAdapter();
                            } else {
                                recylerViewAttendence.setVisibility(GONE);
                                layoutEmptyList.setVisibility(View.VISIBLE);
                                // layoutNoAnyBatch.setVisibility(View.VISIBLE);
                            }
                            if (response.body().getExtra().getBatch_start_date() != null) {
                                convertBatchStartYear(response.body().getExtra().getBatch_start_date());
                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(context);
                    }
                    /*try {
                        boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                        if (!isAdmn) {
                            if (BatchDetailActivity.shimmerFrameLayout.isShimmerStarted()) {
                                BatchDetailActivity.shimmerFrameLayout.stopShimmer();
                                BatchDetailActivity.shimmerFrameLayout.setVisibility(GONE);
                                BatchDetailActivity.viewPager.setVisibility(View.VISIBLE);
                                BatchDetailActivity.tabLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }

            }

            @Override
            public void onFailure(Call<GetAttendenceResponse> call, Throwable t) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(GONE);
                }
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeAttendenceNotification() {
        List<NotificationDataModel> notificationDataList = PreferenceHandler.getNotificationDataList(context);
        if (notificationDataList != null && notificationDataList.size() > 0) {
            for (int i = notificationDataList.size() - 1; i >= 0; i--) {
                if (notificationDataList.get(i).getNotificationType().equalsIgnoreCase("attendance")) {
                    if (notificationDataList.get(i).getBatchId().equalsIgnoreCase(batchId)) {
                        notificationDataList.remove(notificationDataList.get(i));
                        PreferenceHandler.setList(context, PreferenceHandler.NOTIFICATION_DATA, notificationDataList);
                        break;
                    }
                }
            }

        }
    }


    private void convertBatchStartYear(String batchStartDate) {
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        try {
            Date inDate = inFormat.parse(batchStartDate);
            minDate = inFormat.parse(batchStartDate);
            String yr = outFormat.format(inDate);
            batchStartYear = Integer.parseInt(yr);
            String month = monthFormat.format(inDate);
            batchStartMonth = Integer.parseInt(month);
            removeLeadingZero();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void removeLeadingZero() {
        String startMonth = batchStartMonth + "";
        for (int i = 0; i < startMonth.length(); i++) {
            if (startMonth.charAt(i) == '0') {
                startMonth.replace("0", "");
                batchStartMonth = Integer.valueOf(startMonth);
                break;
            } else {
                return;
            }
        }
    }


    private void openDatePickerDialog() {
        datePickerDialog = new DatePickerDialog(context,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String selectedMonth = checkDigit(monthOfYear + 1);
                    String selectedYear = String.valueOf(year);
                    String monthName = new DateFormatSymbols().getShortMonths()[monthOfYear];
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    tvCurrentMonth.setText(monthName + ", " + mYear);
                    if (Utilities.checkInternet(context)) {
                        hitMarkAttendenceService(selectedYear, selectedMonth);
                    } else {
                        Toast.makeText(context, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                    }


                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        if (minDate != null)
            datePickerDialog.getDatePicker().setMinDate(minDate.getTime());

    }
}






