package com.so.luotk.activities.adminrole;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.so.luotk.R;
import com.so.luotk.adapter.adminrole.CourseIdListAdapter;
import com.so.luotk.adapter.adminrole.IdListAdapter;
import com.so.luotk.adapter.adminrole.SubjectIdListAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.databinding.ActivityCatCourseSubjectSelectionBinding;
import com.so.luotk.models.newmodels.adminBatchModel.GetBatchCreateId;
import com.so.luotk.models.newmodels.adminBatchModel.GetCourseIdResponse;
import com.so.luotk.models.newmodels.adminBatchModel.GetSubjectIdResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatCourseSubjectSelectionActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityCatCourseSubjectSelectionBinding binding;
    private String fieldName;
    private String selectedCategoryId, selectedCourseId, selectedSubjectId;
    private String selectedCategoryName, selectedCourseName, selectedSubjectName;
    private Handler handler;
    private Runnable runnable;
    private boolean isFirstInternetToastDone;
    private APIInterface apiInterface;
    private ArrayList<GetBatchCreateId.IdResult> idLists;
    private ArrayList<GetCourseIdResponse.IdResult> idCourseLists;
    private ArrayList<GetSubjectIdResponse.IdResult> idSubjectLists;
    IdListAdapter idListAdapter;
    CourseIdListAdapter courseIdListAdapter;
    SubjectIdListAdapter subjectIdListAdapter;
    private long mLastClickTime = 0;
    String categoryID = "", courseID = "";
    String selectedid = "", selectedName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCatCourseSubjectSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setupToolbar();
        setupUI();

    }


    private void setupToolbar() {
        if (getIntent() != null) {
            fieldName = getIntent().getStringExtra(PreferenceHandler.FIELD_NAME);
        }
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(this));
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (fieldName != null) {
            binding.toolbar.setTitle(fieldName);
        }


    }

    private void setupUI() {
        idLists = new ArrayList<>();
        idCourseLists = new ArrayList<>();
        idSubjectLists = new ArrayList<>();
        binding.searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        apiInterface = ApiUtils.getApiInterface();
        handler = new Handler(Looper.myLooper());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(CatCourseSubjectSelectionActivity.this));
        binding.recyclerView.setNestedScrollingEnabled(true);

        if (getIntent() != null) {
            selectedCategoryId = getIntent().getStringExtra(PreferenceHandler.SELECTED_CATEGORY_ID);
            selectedCourseId = getIntent().getStringExtra(PreferenceHandler.SELECTED_COURSE_ID);
            selectedSubjectId = getIntent().getStringExtra(PreferenceHandler.SELECTED_SUBJECT_ID);
        }

        binding.btnSave.setOnClickListener(this);
        if (fieldName != null) {
            binding.layoutTitle.setText(fieldName);
        }
        if (fieldName.equalsIgnoreCase(getString(R.string.select_category))) {
        } else if (fieldName.equalsIgnoreCase(getString(R.string.select_course))) {
            categoryID = getIntent().getStringExtra("categoryID");
            Log.d("TAG", "setupUI: " + categoryID);
        } else {
            courseID = getIntent().getStringExtra("courseID");
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        checkInternet();

        String prevselectedCategory = PreferenceHandler.readString(this, PreferenceHandler.SELECTED_CATEGORY, "");

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("TAG", "onQueryTextChange: " + newText + " :  "+fieldName);
                if (fieldName.equalsIgnoreCase(getString(R.string.select_category))) {
                    idListAdapter.getFilter().filter(newText);
                } else if (fieldName.equalsIgnoreCase(getString(R.string.select_course))) {
                    courseIdListAdapter.getFilter().filter(newText);
                } else {
                    subjectIdListAdapter.getFilter().filter(newText);
                }

                return false;
            }
        });

        binding.searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showInputMethod(view);
                }
            }
        });


    }

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    private void checkInternet() {
        Log.d("Calllll", "checkInternet: ");
        if (Utilities.checkInternet(CatCourseSubjectSelectionActivity.this)) {
            handler.removeCallbacks(runnable);
            if (fieldName.equalsIgnoreCase(getString(R.string.select_category))) {
                hitCatCourseSubject(fieldName);
            } else if (fieldName.equalsIgnoreCase(getString(R.string.select_course))) {
                hitCatCourse(fieldName);
            } else {
                hitCourseSubject(fieldName);
            }
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Utilities.makeToast(CatCourseSubjectSelectionActivity.this, getString(R.string.internet_connection_error));
                isFirstInternetToastDone = true;
            }
        }
    }

    private void hitCatCourseSubject(String fromWhere) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(CatCourseSubjectSelectionActivity.this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(CatCourseSubjectSelectionActivity.this, PreferenceHandler.DEVICE_ID, ""));

        Log.d("TAG", "hitCatCourseSubject: " + headers);
        Call<GetBatchCreateId> call = apiInterface.getBatchCatIds(headers);

        call.enqueue(new Callback<GetBatchCreateId>() {
            @Override
            public void onResponse(Call<GetBatchCreateId> call, Response<GetBatchCreateId> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getStatus() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            idLists.addAll(response.body().getResult());
                            setIdAdapter(fromWhere);
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(CatCourseSubjectSelectionActivity.this);
                        //openUnauthorizedDialog();
                    } else {
                        Toast.makeText(CatCourseSubjectSelectionActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<GetBatchCreateId> call, Throwable t) {
                Toast.makeText(CatCourseSubjectSelectionActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hitCatCourse(String fromWhere) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(CatCourseSubjectSelectionActivity.this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(CatCourseSubjectSelectionActivity.this, PreferenceHandler.DEVICE_ID, ""));

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("catId", categoryID);

        Log.d("TAG", "hitCatCourse: " + headers + " : " + map);
        Call<GetCourseIdResponse> call = apiInterface.getBatchCourseIds(headers, map);

        call.enqueue(new Callback<GetCourseIdResponse>() {
            @Override
            public void onResponse(Call<GetCourseIdResponse> call, Response<GetCourseIdResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getStatus() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            idCourseLists.addAll(response.body().getResult());
                            setCourseIdAdapter(fromWhere);
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(CatCourseSubjectSelectionActivity.this);
                        //openUnauthorizedDialog();
                    } else {
                        Toast.makeText(CatCourseSubjectSelectionActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<GetCourseIdResponse> call, Throwable t) {
                Toast.makeText(CatCourseSubjectSelectionActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hitCourseSubject(String fromWhere) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(CatCourseSubjectSelectionActivity.this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(CatCourseSubjectSelectionActivity.this, PreferenceHandler.DEVICE_ID, ""));

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("courseId", courseID);

        Log.d("TAG", "hitCourseSubject: " + headers + " : " + selectedCourseId);
        Call<GetSubjectIdResponse> call = apiInterface.getBatchSubjectIds(headers, map);

        call.enqueue(new Callback<GetSubjectIdResponse>() {
            @Override
            public void onResponse(Call<GetSubjectIdResponse> call, Response<GetSubjectIdResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getStatus() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            idSubjectLists.addAll(response.body().getResult());
                            setSubjectIdAdapter(fromWhere);
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(CatCourseSubjectSelectionActivity.this);
                        //openUnauthorizedDialog();
                    } else {
                        Toast.makeText(CatCourseSubjectSelectionActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<GetSubjectIdResponse> call, Throwable t) {
                Toast.makeText(CatCourseSubjectSelectionActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setIdAdapter(String fromWhere) {
        Set<GetBatchCreateId.IdResult> set = new LinkedHashSet<>(idLists);
        idLists.clear();
        idLists.addAll(set);
        Collections.reverse(idLists);
        if (idListAdapter == null) {
            idListAdapter = new IdListAdapter(CatCourseSubjectSelectionActivity.this, idLists, fromWhere);
            binding.recyclerView.setAdapter(idListAdapter);
        }
    }

    private void setCourseIdAdapter(String fromWhere) {
        Set<GetCourseIdResponse.IdResult> set = new LinkedHashSet<>(idCourseLists);
        idCourseLists.clear();
        idCourseLists.addAll(set);
        Collections.reverse(idCourseLists);
        if (courseIdListAdapter == null) {
            courseIdListAdapter = new CourseIdListAdapter(CatCourseSubjectSelectionActivity.this, idCourseLists, fromWhere);
            binding.recyclerView.setAdapter(courseIdListAdapter);
        }
    }

    private void setSubjectIdAdapter(String fromWhere) {
        Set<GetSubjectIdResponse.IdResult> set = new LinkedHashSet<>(idSubjectLists);
        idSubjectLists.clear();
        idSubjectLists.addAll(set);
        Collections.reverse(idSubjectLists);
        if (subjectIdListAdapter == null) {
            subjectIdListAdapter = new SubjectIdListAdapter(CatCourseSubjectSelectionActivity.this, idSubjectLists, fromWhere);
            binding.recyclerView.setAdapter(subjectIdListAdapter);
        }
    }

    private void setClickListener() {
        idListAdapter.SetOnItemClickListener(new IdListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_save:
                saveData();
                break;

            default:
                break;
        }

    }

    private void saveData() {

        boolean forCheck = false;
        if (!forCheck) {
            if (fieldName.equalsIgnoreCase(getString(R.string.select_category))) {
                for (int i = 0; i < idLists.size(); i++) {

                    Log.d("TAG", "onItemClick: " + idLists.get(i).isSlelected());
                    if (idLists.get(i).isSlelected() == true) {
                        selectedid = idLists.get(i).getId();
                        selectedName = idLists.get(i).getCategoryName();
                    }
                    forCheck = true;
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra(PreferenceHandler.SELECTED_CATEGORY, selectedName);
                returnIntent.putExtra(PreferenceHandler.SELECTED_CATEGORY_ID, selectedid);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else if (fieldName.equalsIgnoreCase(getString(R.string.select_course))) {
                for (int i = 0; i < idCourseLists.size(); i++) {

                    Log.d("TAG", "onItemClick: " + idCourseLists.get(i).isSlelected());
                    if (idCourseLists.get(i).isSlelected() == true) {
                        selectedid = idCourseLists.get(i).getId();
                        selectedName = idCourseLists.get(i).getCourseName();
                    }
                    forCheck = true;
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra(PreferenceHandler.SELECTED_COURSE, selectedName);
                returnIntent.putExtra(PreferenceHandler.SELECTED_COURSE_ID, selectedid);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                for (int i = 0; i < idSubjectLists.size(); i++) {

                    Log.d("TAG", "onItemClick: " + idSubjectLists.get(i).isSlelected());
                    if (idSubjectLists.get(i).isSlelected() == true) {
                        selectedid = idSubjectLists.get(i).getId();
                        selectedName = idSubjectLists.get(i).getSubjectName();
                    }
                    forCheck = true;
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra(PreferenceHandler.SELECTED_SUBJECT, selectedName);
                returnIntent.putExtra(PreferenceHandler.SELECTED_SUBJECT_ID, selectedid);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
/*

        for (int i = 0; i < idLists.size(); i++) {
            if (fieldName.equalsIgnoreCase("Select Category")) {
                Log.d("TAG", "onItemClick: " + idLists.get(i).isSlelected());
                if (idLists.get(i).isSlelected())
                    Intent returnIntent = new Intent();
                returnIntent.putExtra(PreferenceHandler.SELECTED_CATEGORY, idLists.get(i).getCategoryName());
                returnIntent.putExtra(PreferenceHandler.SELECTED_CATEGORY_ID, String.valueOf(idLists.get(i).getId()));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else if (fieldName.equalsIgnoreCase("Select Course")) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(PreferenceHandler.SELECTED_COURSE, idCourseLists.get(i).getCourseName());
                returnIntent.putExtra(PreferenceHandler.SELECTED_COURSE_ID, String.valueOf(idCourseLists.get(i).getId()));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(PreferenceHandler.SELECTED_SUBJECT, idSubjectLists.get(i).getSubjectName());
                returnIntent.putExtra(PreferenceHandler.SELECTED_SUBJECT_ID, String.valueOf(idSubjectLists.get(i).getId()));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
*/

        /*if (fieldName.equalsIgnoreCase("Select Category")) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(PreferenceHandler.SELECTED_CATEGORY, selectedRadioButtonName(binding.radioGroupCategory));
            returnIntent.putExtra(PreferenceHandler.SELECTED_CATEGORY_ID, String.valueOf(binding.radioGroupCategory.getCheckedRadioButtonId()));
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else if (fieldName.equalsIgnoreCase("Select Course")) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(PreferenceHandler.SELECTED_COURSE, selectedRadioButtonName(binding.radioGroupCourse));
            returnIntent.putExtra(PreferenceHandler.SELECTED_COURSE_ID, String.valueOf(binding.radioGroupCourse.getCheckedRadioButtonId()));
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(PreferenceHandler.SELECTED_SUBJECT, selectedRadioButtonName(binding.radioGroupSubject));
            returnIntent.putExtra(PreferenceHandler.SELECTED_SUBJECT_ID, String.valueOf(binding.radioGroupSubject.getCheckedRadioButtonId()));
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }*/
    }

    private String selectedRadioButtonName(RadioGroup radioGroup) {
        View radioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        int idx = radioGroup.indexOfChild(radioButton);
        RadioButton rb = (RadioButton) radioGroup.getChildAt(idx);
        String selectedtext = rb.getText().toString();
        return selectedtext;
    }


    private boolean checkSaveValidation(RadioGroup radioGroup) {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            // no radio buttons are checked
            Snackbar.make(binding.getRoot(), "Please select one option", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
