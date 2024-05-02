package com.so.luotk.activities.adminrole;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.so.luotk.R;
import com.so.luotk.adapter.adminrole.SetBatchTimingsAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.ActivityCreateBatchBinding;
import com.so.luotk.models.output.BatchListResult;
import com.so.luotk.models.output.BatchTimingList;
import com.so.luotk.models.output.GetBatchSubmitAssignmentTestResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateBatchActivity extends AppCompatActivity {
    String timeFrom = "";
    private ActivityCreateBatchBinding binding;
    private boolean isOnStep2;
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom rnd = new SecureRandom();
    private String selectedCategory, selectedCourse, selectedSubject, selectedCategoryId, selectedCourseId, selectedSubjectId;
    private SetBatchTimingsAdapter adapter;
    private List<BatchTimingList> batchTimingLists;
    private final List<BatchTimingList> selectedTiming = new ArrayList<>();
    private final ArrayList<String> daysArray = new ArrayList<>();
    private final ArrayList<String> updatedDaysArray = new ArrayList<>();
    private ProgressView mProgressDialog;
    String batchName, batchCode, dateselected, batchFee, batchId;
    private APIInterface apiInterface;
    private String fromWhere, batchData;
    private final ArrayList<String> dayList = new ArrayList<>();
    private final ArrayList<String> startTimeList = new ArrayList<>();
    private final ArrayList<String> endTimeList = new ArrayList<>();
    private String dayNames = "";
    public static MaterialButton submmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBatchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiInterface = ApiUtils.getApiInterface();
        Utilities.setUpStatusBar(this); Utilities.setLocale(this);
        setUpToolbar();
        setUpUI();
        setClickListeners();
    }

    private void setUpToolbar() {
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(CreateBatchActivity.this));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnStep2) {
                    binding.layoutStep1.setVisibility(View.VISIBLE);
                    binding.layoutStep2.setVisibility(View.GONE);
                    isOnStep2 = false;
                    binding.textSteps.setText(getString(R.string.step) + " 1/2");
                } else
                    finish();
            }
        });
        binding.textSteps.setText(getString(R.string.step) + " 1/2");
    }

    private void setUpUI() {

        submmitButton = binding.btnSave;
        fromWhere = getIntent().getStringExtra("from");
        batchTimingLists = new ArrayList<>();
        /*binding.edtBatchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.edtBatchName.getText().length() > 0)
                    binding.edtBatchCode.setText(randomString(6));
                else {
                    binding.edtBatchCode.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/


        binding.edtBatchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String otp_textbox_oneStr = binding.edtBatchName.getText().toString();
                String otp_textbox_twoStr = binding.tvBatchStartDate.getText().toString();
                String otp_textbox_threeStr = binding.tvCategory.getText().toString();
                String otp_textbox_fourStr = binding.tvCourse.getText().toString();
                String otp_textbox_fiveStr = binding.tvSubject.getText().toString();
                String otp_textbox_sixStr = binding.tvBatchFee.getText().toString();
                if (!otp_textbox_oneStr.isEmpty() && !otp_textbox_twoStr.isEmpty() && !otp_textbox_threeStr.isEmpty() && !otp_textbox_fourStr.isEmpty()
                        && !otp_textbox_fiveStr.isEmpty() && !otp_textbox_sixStr.isEmpty()) {
                    binding.btnNext.setEnabled(true);
                    binding.btnNext.setBackgroundColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.blue_main));
                    binding.btnNext.setTextColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.white));
                } else {
                    binding.btnNext.setBackgroundColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.disabledButtonColor));
                    binding.btnNext.setTextColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.disabled_btn));
                    binding.btnNext.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        binding.tvBatchStartDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String otp_textbox_oneStr = binding.edtBatchName.getText().toString();
                String otp_textbox_twoStr = binding.tvBatchStartDate.getText().toString();
                String otp_textbox_threeStr = binding.tvCategory.getText().toString();
                String otp_textbox_fourStr = binding.tvCourse.getText().toString();
                String otp_textbox_fiveStr = binding.tvSubject.getText().toString();
                String otp_textbox_sixStr = binding.tvBatchFee.getText().toString();
                if (!otp_textbox_oneStr.isEmpty() && !otp_textbox_twoStr.isEmpty() && !otp_textbox_threeStr.isEmpty() && !otp_textbox_fourStr.isEmpty()
                        && !otp_textbox_fiveStr.isEmpty() && !otp_textbox_sixStr.isEmpty()) {
                    binding.btnNext.setEnabled(true);
                    binding.btnNext.setBackgroundColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.blue_main));
                    binding.btnNext.setTextColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.white));
                } else {
                    binding.btnNext.setBackgroundColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.disabledButtonColor));
                    binding.btnNext.setTextColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.disabled_btn));
                    binding.btnNext.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        binding.tvCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String otp_textbox_oneStr = binding.edtBatchName.getText().toString();
                String otp_textbox_twoStr = binding.tvBatchStartDate.getText().toString();
                String otp_textbox_threeStr = binding.tvCategory.getText().toString();
                String otp_textbox_fourStr = binding.tvCourse.getText().toString();
                String otp_textbox_fiveStr = binding.tvSubject.getText().toString();
                String otp_textbox_sixStr = binding.tvBatchFee.getText().toString();
                if (!otp_textbox_oneStr.isEmpty() && !otp_textbox_twoStr.isEmpty() && !otp_textbox_threeStr.isEmpty() && !otp_textbox_fourStr.isEmpty()
                        && !otp_textbox_fiveStr.isEmpty() && !otp_textbox_sixStr.isEmpty()) {
                    binding.btnNext.setEnabled(true);
                    binding.btnNext.setBackgroundColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.blue_main));
                    binding.btnNext.setTextColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.white));
                } else {
                    binding.btnNext.setBackgroundColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.disabledButtonColor));
                    binding.btnNext.setTextColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.disabled_btn));
                    binding.btnNext.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        binding.tvCourse.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String otp_textbox_oneStr = binding.edtBatchName.getText().toString();
                String otp_textbox_twoStr = binding.tvBatchStartDate.getText().toString();
                String otp_textbox_threeStr = binding.tvCategory.getText().toString();
                String otp_textbox_fourStr = binding.tvCourse.getText().toString();
                String otp_textbox_fiveStr = binding.tvSubject.getText().toString();
                String otp_textbox_sixStr = binding.tvBatchFee.getText().toString();
                if (!otp_textbox_oneStr.isEmpty() && !otp_textbox_twoStr.isEmpty() && !otp_textbox_threeStr.isEmpty() && !otp_textbox_fourStr.isEmpty()
                        && !otp_textbox_fiveStr.isEmpty() && !otp_textbox_sixStr.isEmpty()) {
                    binding.btnNext.setEnabled(true);
                    binding.btnNext.setBackgroundColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.blue_main));
                    binding.btnNext.setTextColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.white));
                } else {
                    binding.btnNext.setBackgroundColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.disabledButtonColor));
                    binding.btnNext.setTextColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.disabled_btn));
                    binding.btnNext.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        binding.tvSubject.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String otp_textbox_oneStr = binding.edtBatchName.getText().toString();
                String otp_textbox_twoStr = binding.tvBatchStartDate.getText().toString();
                String otp_textbox_threeStr = binding.tvCategory.getText().toString();
                String otp_textbox_fourStr = binding.tvCourse.getText().toString();
                String otp_textbox_fiveStr = binding.tvSubject.getText().toString();
                String otp_textbox_sixStr = binding.tvBatchFee.getText().toString();
                if (!otp_textbox_oneStr.isEmpty() && !otp_textbox_twoStr.isEmpty() && !otp_textbox_threeStr.isEmpty() && !otp_textbox_fourStr.isEmpty()
                        && !otp_textbox_fiveStr.isEmpty() && !otp_textbox_sixStr.isEmpty()) {
                    binding.btnNext.setEnabled(true);
                    binding.btnNext.setBackgroundColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.blue_main));
                    binding.btnNext.setTextColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.white));
                } else {
                    binding.btnNext.setBackgroundColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.disabledButtonColor));
                    binding.btnNext.setTextColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.disabled_btn));
                    binding.btnNext.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        binding.tvBatchFee.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String otp_textbox_oneStr = binding.edtBatchName.getText().toString();
                String otp_textbox_twoStr = binding.tvBatchStartDate.getText().toString();
                String otp_textbox_threeStr = binding.tvCategory.getText().toString();
                String otp_textbox_fourStr = binding.tvCourse.getText().toString();
                String otp_textbox_fiveStr = binding.tvSubject.getText().toString();
                String otp_textbox_sixStr = binding.tvBatchFee.getText().toString();
                if (!otp_textbox_oneStr.isEmpty() && !otp_textbox_twoStr.isEmpty() && !otp_textbox_threeStr.isEmpty() && !otp_textbox_fourStr.isEmpty()
                        && !otp_textbox_fiveStr.isEmpty() && !otp_textbox_sixStr.isEmpty()) {
                    binding.btnNext.setEnabled(true);
                    binding.btnNext.setBackgroundColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.blue_main));
                    binding.btnNext.setTextColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.white));
                } else {
                    binding.btnNext.setBackgroundColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.disabledButtonColor));
                    binding.btnNext.setTextColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.disabled_btn));
                    binding.btnNext.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        binding.recyclerTimingList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));



        if (fromWhere.equals("edit")) {
            binding.titleBatch.setText(getString(R.string.ed_batch));
            binding.btnSave.setEnabled(true);
            binding.btnNext.setEnabled(true);
            binding.btnNext.setBackgroundColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.blue_main));
            binding.btnNext.setTextColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.white));
            binding.btnSave.setBackgroundColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.blue_main));
            binding.btnSave.setTextColor(ContextCompat.getColor(CreateBatchActivity.this, R.color.white));
            setEditableData();
        } else {
            binding.titleBatch.setText(getString(R.string.cr_batch));
            setTimingAdapter();
        }

    }

    private void setEditableData() {
        batchData = getIntent().getStringExtra("batchData");
        BatchListResult batchListData  =  new Gson().fromJson(batchData, BatchListResult.class);
        Log.d("TAG", "setEditableData: " + batchListData.getBatchName());

        batchId = batchListData.getId();
        if (!TextUtils.isEmpty(batchListData.getBatchName())) {
            binding.edtBatchName.setText(batchListData.getBatchName());
        }

        if (!TextUtils.isEmpty(batchListData.getBatchCode())) {
            binding.edtBatchCode.setText(batchListData.getBatchCode());
        }

        if (!TextUtils.isEmpty(batchListData.getStart_date())) {
            String date=batchListData.getStart_date();
            SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd");
            Date newDate= null;
            try {
                newDate = spf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            spf= new SimpleDateFormat("dd/MM/yyyy");
            date = spf.format(newDate);

            binding.tvBatchStartDate.setText(date);
        }

        if (!TextUtils.isEmpty(batchListData.getFk_catId())) {
            binding.tvCategory.setText(batchListData.getFk_catId());
            selectedCategoryId = batchListData.getFk_catId();
            selectedCategory = batchListData.getFk_catId();
        }

        if (!TextUtils.isEmpty(batchListData.getFk_courseId())) {
            binding.tvCourse.setText(batchListData.getCourseName());
            selectedCourseId = batchListData.getFk_courseId();
            selectedCourse = batchListData.getCourseName();
        }
        if (!TextUtils.isEmpty(batchListData.getFk_subjectId())) {
            binding.tvSubject.setText(batchListData.getSubjectName());
            selectedSubjectId = batchListData.getFk_subjectId();
            selectedSubject = batchListData.getSubjectName();
        }

        if (!TextUtils.isEmpty(batchListData.getBatchFee())) {
            binding.tvBatchFee.setText(batchListData.getBatchFee());
        }

        String dayss = convertStringToJson(batchListData.getDays_time());
        Log.d("TAG", "setEditableData: " + dayss);



        List<String> days = Arrays.asList("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");

        for (int i = 0; i < 7; i++) {
            BatchTimingList timingList = new BatchTimingList();
            timingList.setDay(days.get(i));
            Log.d("TAG", "setEditableDataDaySize: " + dayList.size());
            for (int j = 0; j < dayList.size(); j++) {
                Log.d("TAG", "setEditableDataDaySize: " + days.get(i) + " : "+ dayList.get(j).substring(0, 3).toUpperCase());
                if (days.get(i).equals(dayList.get(j).substring(0, 3).toUpperCase())){
                    timingList.setOn(true);
                    timingList.setTimeFrom(startTimeList.get(j));
                    timingList.setTimeTo(endTimeList.get(j));
                }
            }
            batchTimingLists.add(timingList);
        }
        adapter = new SetBatchTimingsAdapter(this, batchTimingLists, submmitButton);
        binding.recyclerTimingList.setAdapter(adapter);

    }

    private void setTimingAdapter() {
        List<String> days = Arrays.asList("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");
        for (int i = 0; i < 7; i++) {
            BatchTimingList timingList = new BatchTimingList();
            timingList.setDay(days.get(i));
            batchTimingLists.add(timingList);
        }
        adapter = new SetBatchTimingsAdapter(this, batchTimingLists, submmitButton);
        binding.recyclerTimingList.setAdapter(adapter);
//        setAdapterTimeClickListener();
    }

    private void setAdapterTimeClickListener() {
        adapter.setTimeClickListener((timingList, toFrom, position, tv_time_to) -> {
            timingList.setOn(true);
            if (toFrom.equalsIgnoreCase("to")) {
                String timeTo = openTimePicker24(tv_time_to);
                timingList.setTimeTo(timeTo);

                adapter.notifyItemChanged(position);
            }
            if (toFrom.equalsIgnoreCase("from")) {
                String timeFrom = openTimePicker24(tv_time_to);
                timingList.setTimeFrom(timeFrom);
                adapter.notifyItemChanged(position);
            }
        });
    }

    private String openTimePicker24(TextView textView) {
        // Get Current Time
        final String[] selected_time1 = new String[1];
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateBatchActivity.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                Log.d("TAG", "onTimeSetBefore: " + hourOfDay + " : "+ minute);
                String hourSt = "";
                String minutesSt = "";
                int mHour = hourOfDay;
                int mMinutes = minute;
                if (hourOfDay < 10) {
                    hourSt = "0"+mHour;
                } else {
                    hourSt = ""+mHour;
                }

                if (mMinutes < 10) {
                    minutesSt = "0"+mMinutes;
                } else {
                    minutesSt = ""+mMinutes;
                }

                String selected_time = hourSt + ":" + minutesSt;
                selected_time1[0] = selected_time;
                Log.d("TAG", "onTimeSet: " + selected_time + " : "+ minute);
                textView.setText(selected_time);

            }
        }, mHour, mMinute, true);
        timePickerDialog.show();
        Log.d("TAG", "openTimePicker24: " + selected_time1[0]);
        return selected_time1[0];
    }

    private String openTimePicker() {
        final String[] selected_time = new String[1];
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                String AM_PM;
                int mHour = hourOfDay;
                if (hourOfDay < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                    mHour = mHour - 12;
                }
                selected_time[0] = mHour + ":" + minute + " " + AM_PM;
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
        return selected_time[0];
    }

    private String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private void setClickListeners() {
        binding.btnNext.setOnClickListener(v -> {
            if (checkValidation()) {
                binding.layoutStep1.setVisibility(View.GONE);
                binding.layoutStep2.setVisibility(View.VISIBLE);
                isOnStep2 = true;
                binding.textSteps.setText(getString(R.string.step) + " 2/2");
                Utilities.hideKeyBoard(CreateBatchActivity.this);
            } else {
                Toast.makeText(this, getString(R.string.required_all_field), Toast.LENGTH_SHORT).show();
            }
        });
        binding.tvBatchStartDate.setOnClickListener(v -> {
            binding.edtBatchName.clearFocus();
            binding.tvBatchFee.clearFocus();
            Utilities.openDatePickerSlashTextView(this, binding.tvBatchStartDate, false);
        });
        binding.tvCategory.setOnClickListener(v -> {
            binding.edtBatchName.clearFocus();
            binding.tvBatchFee.clearFocus();
            binding.tvBatchStartDate.clearFocus();
            Intent intent = new Intent(this, CatCourseSubjectSelectionActivity.class);
            intent.putExtra(PreferenceHandler.FIELD_NAME, getString(R.string.select_category));
            intent.putExtra(PreferenceHandler.SELECTED_CATEGORY_ID, selectedCategoryId);
            intent.putExtra(PreferenceHandler.SELECTED_CATEGORY, selectedCategory);
            startActivityForResult(intent, 1);
        });

        binding.tvCourse.setOnClickListener(v -> {
            binding.edtBatchName.clearFocus();
            binding.tvBatchFee.clearFocus();
            binding.tvBatchStartDate.clearFocus();
            Log.d("TAG", "setClickListeners:1 " + selectedCategoryId);
            if (!TextUtils.isEmpty(selectedCategoryId) && !selectedCategoryId.equals("")) {
                Intent intent = new Intent(this, CatCourseSubjectSelectionActivity.class);
                intent.putExtra(PreferenceHandler.FIELD_NAME, getString(R.string.select_course));
                intent.putExtra(PreferenceHandler.SELECTED_COURSE_ID, selectedCourseId);
                intent.putExtra(PreferenceHandler.SELECTED_COURSE, selectedCourse);
                intent.putExtra(PreferenceHandler.SELECTED_CATEGORY_ID, selectedCategoryId);
                intent.putExtra("categoryID", selectedCategoryId);
                startActivityForResult(intent, 2);
            } else {
                Utilities.makeToast(CreateBatchActivity.this, "Please select category first");
            }
        });


        binding.tvSubject.setOnClickListener(v -> {
            binding.edtBatchName.clearFocus();
            binding.tvBatchFee.clearFocus();
            binding.tvBatchStartDate.clearFocus();
            Log.d("TAG", "setClickListeners:2 " + selectedCourseId);
            if (!TextUtils.isEmpty(selectedCourseId) && !selectedCourseId.equals("")) {
                Intent intent = new Intent(this, CatCourseSubjectSelectionActivity.class);
                intent.putExtra(PreferenceHandler.FIELD_NAME, getString(R.string.select_subject));
                intent.putExtra(PreferenceHandler.SELECTED_SUBJECT_ID, selectedSubjectId);
                intent.putExtra(PreferenceHandler.SELECTED_SUBJECT, selectedSubject);
                intent.putExtra(PreferenceHandler.SELECTED_COURSE_ID, selectedCourseId);
                intent.putExtra("courseID", selectedCourseId);
                startActivityForResult(intent, 3);
            } else {
                Utilities.makeToast(CreateBatchActivity.this, "Please select course first");
            }
        });
        binding.btnSave.setOnClickListener(view -> {
            checkTimingValidations(fromWhere);
        });


    }

    private void checkTimingValidations(String fromWhere) {
        selectedTiming.clear();
        for (int i = 0; i < batchTimingLists.size(); i++) {
            BatchTimingList batchTimingList = batchTimingLists.get(i);
            Log.d("TAG", "setClickListeners: " + batchTimingLists.get(i).getTimeFrom() +" : "+ batchTimingLists.get(i).getTimeTo());
            selectedTiming.add(batchTimingList);
        }
        Log.d("TAG", "checkTimingValidations: " + batchName +" : "+ batchCode +" : "+ dateselected +" : "+
                selectedCategoryId +" : "+ selectedCourseId + " : "+ selectedSubjectId +" : "+ batchFee);

        hitCreateEditBatch(batchName, batchCode, dateselected, selectedCategoryId, selectedCourseId, selectedSubjectId, batchFee, selectedTiming, fromWhere);
//        Toast.makeText(this, "Batch created successfully", Toast.LENGTH_SHORT).show();
    }

    private void openNextActivity(String fieldname, int requestCode, int buttonId) {
        Intent intent = new Intent(this, CatCourseSubjectSelectionActivity.class);
        intent.putExtra(PreferenceHandler.FIELD_NAME, fieldname);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (!TextUtils.isEmpty(selectedCategory)) {
            binding.tvCategory.setText(selectedCategory);
            binding.tvCourse.setText("Select course");
            binding.tvSubject.setText("Select subject");
            selectedCourseId = "";
            selectedCourse = "";
            selectedSubjectId = "";
            selectedSubject = "";
        }
        if (!TextUtils.isEmpty(selectedCourse)) {
            binding.tvCourse.setText(selectedCourse);
            binding.tvSubject.setText("Select subject");
            selectedSubjectId = "";
            selectedSubject = "";
        }
        if (!TextUtils.isEmpty(selectedSubject)) {
            binding.tvSubject.setText(selectedSubject);
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                selectedCategory = data.getStringExtra(PreferenceHandler.SELECTED_CATEGORY);
                selectedCategoryId = data.getStringExtra(PreferenceHandler.SELECTED_CATEGORY_ID);
                Log.d("TAG", "onActivityResult1: " + selectedCategoryId +" : " + selectedCategory);

                binding.tvCategory.setText(selectedCategory);
                binding.tvCourse.setText("Select course");
                binding.tvSubject.setText("Select subject");
                selectedCourseId = "";
                selectedCourse = "";
                selectedSubjectId = "";
                selectedSubject = "";

            }
            if (requestCode == 2) {
                selectedCourse = data.getStringExtra(PreferenceHandler.SELECTED_COURSE);
                selectedCourseId = data.getStringExtra(PreferenceHandler.SELECTED_COURSE_ID);
                Log.d("TAG", "onActivityResult2: " + selectedCourseId +" : " + selectedCourse);

                binding.tvCourse.setText(selectedCourse);
                binding.tvSubject.setText("Select subject");
                selectedSubjectId = "";
                selectedSubject = "";

            }
            if (requestCode == 3) {
                selectedSubject = data.getStringExtra(PreferenceHandler.SELECTED_SUBJECT);
                selectedSubjectId = data.getStringExtra(PreferenceHandler.SELECTED_SUBJECT_ID);
                Log.d("TAG", "onActivityResult3: " + selectedSubjectId +" : " + selectedSubject);

                binding.tvSubject.setText(selectedSubject);
            }
        }
    }

    private boolean checkValidation() {
        boolean validationVal = true;

        if (!TextUtils.isEmpty(binding.tvBatchFee.getText().toString())) {
            batchFee = binding.tvBatchFee.getText().toString();
            binding.tvBatchFeeLay.setError(null);
            binding.tvBatchFeeLay.setErrorEnabled(false);
        } else {
            binding.tvBatchFeeLay.setError("The batch fees field is required");
            binding.tvBatchFeeLay.setErrorEnabled(true);
            validationVal = false;
        }

        if (!TextUtils.isEmpty(selectedSubjectId)) {
            selectedSubject = binding.tvSubject.getText().toString();
            binding.tvSubjectLay.setError(null);
            binding.tvSubjectLay.setErrorEnabled(false);
        } else {
            binding.tvSubjectLay.setError("The subject name field is required");
            binding.tvSubjectLay.setErrorEnabled(true);
            validationVal = false;
        }

        if (!TextUtils.isEmpty(selectedCourseId)) {
            selectedCourse = binding.tvCourse.getText().toString();
            binding.tvCourseLay.setError(null);
            binding.tvCourseLay.setErrorEnabled(false);
        } else {
            binding.tvCourseLay.setError("The course name field is required");
            binding.tvCourseLay.setErrorEnabled(true);
            validationVal = false;
        }
        Log.d("TAG", "checkValidation: " + selectedCategoryId +" : "+ selectedCategory);
        if (!TextUtils.isEmpty(selectedCategoryId)) {
            selectedCategory = binding.tvCategory.getText().toString();
            binding.tvCategoryLay.setError(null);
            binding.tvCategoryLay.setErrorEnabled(false);
        } else {
            binding.tvCategoryLay.setError("The category name field is required");
            binding.tvCategoryLay.setErrorEnabled(true);
            validationVal = false;
        }

        if (!TextUtils.isEmpty(binding.tvBatchStartDate.getText().toString())) {
            dateselected = binding.tvBatchStartDate.getText().toString();
            binding.tvBatchStartDateLay.setError(null);
            binding.tvBatchStartDateLay.setErrorEnabled(false);
        } else {
            binding.tvBatchStartDateLay.setError("The start date field is required");
            binding.tvBatchStartDateLay.setErrorEnabled(true);
            validationVal = false;
        }

        if (!TextUtils.isEmpty(binding.edtBatchName.getText().toString())) {
            batchName = binding.edtBatchName.getText().toString();
            binding.edtBatchNameLay.setError(null);
            binding.edtBatchNameLay.setErrorEnabled(false);
        } else {
            validationVal = false;
            binding.edtBatchNameLay.setError("The batch name field is required");
            binding.edtBatchNameLay.setErrorEnabled(true);
        }

        return validationVal;

    }

    private void hitCreateEditBatch(String batchName, String batchCode, String dateselected, String selectedCategoryId, String selectedCourseId, String selectedSubjectId, String batchFee, List<BatchTimingList> selectedTiming, String fromWhere) {
        try {
            daysArray.clear();
            Map<String, String> map = new HashMap<>();
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", PreferenceHandler.readString(CreateBatchActivity.this, PreferenceHandler.TOKEN, ""));
            headers.put("deviceId", PreferenceHandler.readString(CreateBatchActivity.this, PreferenceHandler.DEVICE_ID, ""));
            Call<GetBatchSubmitAssignmentTestResponse> call = null;

            if (fromWhere.equals("edit")) {
                if (!batchId.isEmpty())
                    map.put("batchId", batchId);
            }
            if (!batchName.isEmpty())
                map.put("batchName", batchName);
            /*if (!batchCode.isEmpty())
                map.put("batchCode", batchCode);*/
            if (!selectedCategoryId.isEmpty())
                map.put("fk_catId", selectedCategoryId);
            if (!selectedCourseId.isEmpty())
                map.put("fk_courseId", selectedCourseId);
            if (!selectedSubjectId.isEmpty())
                map.put("fk_subjectId", selectedSubjectId);
            if (!batchFee.isEmpty())
                map.put("batchFee", batchFee);
            if (!dateselected.isEmpty())
                map.put("start_date", dateselected);

            for (int i = 0; i < selectedTiming.size(); i++) {
                BatchTimingList batchTimingList = batchTimingLists.get(i);
                if (batchTimingList.isOn()) {
                    if (batchTimingList.getTimeFrom() != null) {
                        map.put(batchTimingList.getDay() + "-startTime", batchTimingList.getTimeFrom());
                    } else {
                        map.put(batchTimingList.getDay() + "-startTime", "00:00");
                    }
                    if (batchTimingList.getTimeTo() != null) {
                        map.put(batchTimingList.getDay() + "-endTime", batchTimingList.getTimeTo());
                    } else {
                        map.put(batchTimingList.getDay() + "-endTime", "00:00");
                    }
//                    daysArray.add(batchTimingList.getDay());
                    daysArray.add("\"" +batchTimingList.getDay() + "\"");
                    updatedDaysArray.add("\"" +batchTimingList.getDay() + "\"" +"TimeSplit"+ batchTimingList.getTimeFrom() +"TimeSplit"+batchTimingList.getTimeTo());
                }
            }

            for (Map.Entry<String,String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.d("TAG", "hitCreateEditBatch: " + key + " : "+ value);

            }
            Log.d("TAG", "hitCreateEditBatchDay: " + daysArray.toString());

            if (fromWhere.equals("edit")) {
                if (!batchId.isEmpty())
                    map.put("batchId", batchId);
                call = apiInterface.updateBatch(headers, daysArray.toString(), map);
            } else {
                call = apiInterface.createBatch(headers, daysArray.toString(), map);
            }

            mProgressDialog = new ProgressView(CreateBatchActivity.this);
            mProgressDialog.show();
            call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
                @Override
                public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                    mProgressDialog.dismiss();
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                                if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                                    Utilities.hideKeyBoard(CreateBatchActivity.this);
                                    if (fromWhere.equals("edit")) {
                                        Utilities.makeToast(getApplicationContext(), "Batch updated succesffully");
                                        Intent intent = new Intent();
                                        intent.putExtra(PreferenceHandler.IS_DATA_SUBMITTED, true);
                                        intent.putExtra("batchUpdatedName", batchName);
                                        intent.putExtra("batchUpdatedCourse", selectedCourse);
                                        intent.putStringArrayListExtra("batchUpdatedDays", updatedDaysArray);
                                        setResult(Activity.RESULT_OK, intent);
                                        finish();

                                    } else {
                                        Utilities.makeToast(getApplicationContext(), "Batch created succesffully");
                                        Intent intent = new Intent();
                                        intent.putExtra(PreferenceHandler.IS_DATA_SUBMITTED, true);
                                        setResult(Activity.RESULT_OK, intent);
                                        finish();

                                    }

                                }
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                                Utilities.openUnauthorizedDialog(CreateBatchActivity.this);
                            } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("406")) {
                                Utilities.makeToast(CreateBatchActivity.this, "406 error");
                            } else {
                                Utilities.makeToast(CreateBatchActivity.this, getString(R.string.server_error));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Log.d("TAG", "onFailure: " + t.getMessage());
                    Utilities.makeToast(CreateBatchActivity.this, getString(R.string.server_error));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String convertStringToJson(String response) {
        //   String response = "{\"MON\":{\"day\":\"Monday\",\"startTime\":\"03:36\",\"endTime\":\"06:36\"},\"TUE\":{\"day\":\"Tuesday\",\"startTime\":\"01:43 PM\",\"endTime\":\"05:43 PM\"},\"WED\":{\"day\":\"Wednesday\",\"startTime\":\"02:19 PM\",\"endTime\":\"04:43 PM\"}}";
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
                sbString.append(days.substring(0, 2)).append(",");
            }
            dayNames = sbString.toString();
            if (dayNames.length() > 0)
                dayNames = dayNames.substring(0, dayNames.length() - 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dayNames;
    }
}