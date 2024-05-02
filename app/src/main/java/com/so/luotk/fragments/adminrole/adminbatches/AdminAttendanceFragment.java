package com.so.luotk.fragments.adminrole.adminbatches;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.so.luotk.R;
import com.so.luotk.adapter.adminrole.AdminAttendanceListAdapter;
import com.so.luotk.client.MyClient;
import com.so.luotk.databinding.FragmentAdminAttendanceBinding;

import com.so.luotk.models.input.GetAttendenceInput;
import com.so.luotk.models.output.GetAttendenceResponse;
import com.so.luotk.models.output.GetAttendenceResult;
import com.so.luotk.utils.Utilities;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdminAttendanceFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatePickerDialog datePickerDialog;
    private FragmentAdminAttendanceBinding binding;
    private String batchId, batchStartDate;
    private List<GetAttendenceResult> attendenceResultList;
    private AdminAttendanceListAdapter adapter;
    private boolean isFragmentLoaded, isPickerSet;
    private Context context;
    private Activity mActivity;
    private String selected_date;
    private Date minDate;
    private int mYear, mMonth, mDay;

    public AdminAttendanceFragment() {
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


    public static AdminAttendanceFragment newInstance(String batchId) {
        AdminAttendanceFragment fragment = new AdminAttendanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, batchId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            batchId = getArguments().getString(ARG_PARAM1);
        }
        adapter = new AdminAttendanceListAdapter();
        attendenceResultList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = Integer.parseInt(checkDigit(c.get(Calendar.MONTH)));
        mDay = Integer.parseInt(checkDigit(c.get(Calendar.DAY_OF_MONTH)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminAttendanceBinding.inflate(inflater, container, false);
        binding.recylerStudentsList.setLayoutManager(new LinearLayoutManager(context));
        binding.recylerStudentsList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        setClickListeners();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFragmentLoaded) {
            hitApi();
            setUpUI();
        } else {
            setUpUI();
            if (attendenceResultList != null && attendenceResultList.size() > 0) {
                adapter.updateList(attendenceResultList);
                binding.recylerStudentsList.setVisibility(View.VISIBLE);
                binding.tvNoDataLay.setVisibility(View.GONE);
            } else {
                binding.recylerStudentsList.setVisibility(View.GONE);
                binding.tvNoDataLay.setVisibility(View.VISIBLE);
            }

            binding.progressIndicator.setVisibility(View.GONE);
            binding.layoutDataView.setVisibility(View.VISIBLE);
        }

    }

    private void hitApi() {
        if (!TextUtils.isEmpty(batchId))
            if (Utilities.checkInternet(context)) {
                openDatePickerDialog(false);
                isFragmentLoaded = true;
            } else {
                Toast.makeText(context, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
            }
    }

    private void hitGetAttendanceListService() {
        GetAttendenceInput input = new GetAttendenceInput();
        input.setBatchId(batchId);
        input.setYear_month(selected_date);
        new MyClient(context).getBatchAttendance(input, (content, error) -> {
            if (content != null) {
                GetAttendenceResponse response = (GetAttendenceResponse) content;
                if (response.getStatus() != null && response.getStatus().equalsIgnoreCase("200")) {
                    if (response.getSuccess().equalsIgnoreCase("true")) {
                        if (response.getExtra() != null) {
                            batchStartDate = response.getExtra().getBatch_start_date();
                            if (!isPickerSet)
                                convertBatchStartDate(batchStartDate);
                        }
                        if (response.getResult() != null && response.getResult().size() > 0) {
                            attendenceResultList = response.getResult();
                            adapter.updateList(attendenceResultList);
                            binding.recylerStudentsList.setVisibility(View.VISIBLE);
                            binding.tvNoDataLay.setVisibility(View.GONE);

                        } else {
                            binding.recylerStudentsList.setVisibility(View.GONE);
                            binding.tvNoDataLay.setVisibility(View.VISIBLE);
                        }
                    }

                } else if (response.getStatus() != null && response.getStatus().equalsIgnoreCase("403")) {
                    Utilities.openUnauthorizedDialog(context);
                } else {
                    Utilities.makeToast(context, getString(R.string.server_error));
                }
            } else {
                Utilities.makeToast(context, getString(R.string.server_error));
            }

            binding.progressIndicator.setVisibility(View.GONE);
            binding.layoutDataView.setVisibility(View.VISIBLE);

            /*try {
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

    private void setUpUI() {
        binding.recylerStudentsList.setAdapter(adapter);
        binding.tvCurrentMonth.setText(String.format("%d-%d-%d", mDay, mMonth + 1, mYear));
    }

    private void setClickListeners() {
        binding.tvCurrentMonth.setOnClickListener(v -> openDatePickerDialog(true));
    }

    private void convertBatchStartDate(String batchStartDate) {
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            minDate = inFormat.parse(batchStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        isPickerSet = true;

    }

    private void openDatePickerDialog(boolean show) {
        // Get Current Date

        datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        binding.progressIndicator.setVisibility(View.VISIBLE);
                        binding.layoutDataView.setVisibility(View.GONE);
                        String selectedMonth = checkDigit(monthOfYear + 1);
                        String selectedYear = String.valueOf(year);
                        String monthName = new DateFormatSymbols().getShortMonths()[monthOfYear];
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        selected_date = selectedYear + "-" + selectedMonth + "-" + checkDigit(dayOfMonth);
                        binding.tvCurrentMonth.setText(String.format("%d-%s-%s", dayOfMonth, selectedMonth, selectedYear));
                        hitGetAttendanceListService();
                    }
                }, mYear, mMonth, mDay);
        if (show)
            datePickerDialog.show();
        else {
            selected_date = mYear + "-" + (mMonth + 1) + "-" + mDay;
            hitGetAttendanceListService();
        }
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        if (minDate != null)
            datePickerDialog.getDatePicker().setMinDate(minDate.getTime());
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}