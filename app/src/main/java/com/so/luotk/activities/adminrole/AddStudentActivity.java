package com.so.luotk.activities.adminrole;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;

import com.so.luotk.R;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.FragmentAddStudentBinding;
import com.so.luotk.models.newmodels.ServerResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.HashMap;
import java.util.Map;

public class AddStudentActivity extends AppCompatActivity {
    private FragmentAddStudentBinding binding;
    private String batchId;
    private ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = FragmentAddStudentBinding.inflate(getLayoutInflater());
        overridePendingTransition(0, 0);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(binding.getRoot());

        if (getIntent() != null)
            batchId = getIntent().getStringExtra(PreferenceHandler.BATCH_ID);
        progressView = new ProgressView(this);
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(this));
        binding.toolbar.setNavigationOnClickListener(view -> finish());
        Utilities.openKeyboard(this, binding.edtName);
        binding.btnSubmit.setOnClickListener(v -> {
            if (checkValidation()) {
                if (Utilities.checkInternet(this)) {
                    if (!progressView.isShowing())
                        progressView.show();
                    hitAddStudentService();
                } else Utilities.makeToast(this, getString(R.string.internet_connection_error));
            }
        });

        binding.edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String otp_textbox_oneStr = binding.edtName.getText().toString();
                String otp_textbox_twoStr = binding.edtPhnNumber.getText().toString();
                if (!otp_textbox_oneStr.isEmpty() && (otp_textbox_twoStr.length() == 10)) {
                    binding.btnSubmit.setEnabled(true);
                    binding.btnSubmit.setBackgroundColor(ContextCompat.getColor(AddStudentActivity.this, R.color.blue_main));
                    binding.btnSubmit.setTextColor(ContextCompat.getColor(AddStudentActivity.this, R.color.white_both));
                } else {
                    binding.btnSubmit.setEnabled(false);
                    binding.btnSubmit.setBackgroundColor(ContextCompat.getColor(AddStudentActivity.this, R.color.disabledButtonColor));
                    binding.btnSubmit.setTextColor(ContextCompat.getColor(AddStudentActivity.this, R.color.disabled_btn));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("TAG", "afterTextChanged: " + s.toString());

                // TODO Auto-generated method stub
            }
        });

        binding.edtPhnNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String otp_textbox_oneStr = binding.edtName.getText().toString();
                String otp_textbox_twoStr = binding.edtPhnNumber.getText().toString();
                if (!otp_textbox_oneStr.isEmpty() && (otp_textbox_twoStr.length() == 10)) {
                    binding.btnSubmit.setEnabled(true);
                    binding.btnSubmit.setBackgroundColor(ContextCompat.getColor(AddStudentActivity.this, R.color.blue_main));
                    binding.btnSubmit.setTextColor(ContextCompat.getColor(AddStudentActivity.this, R.color.white_both));
                } else {
                    binding.btnSubmit.setEnabled(false);
                    binding.btnSubmit.setBackgroundColor(ContextCompat.getColor(AddStudentActivity.this, R.color.disabledButtonColor));
                    binding.btnSubmit.setTextColor(ContextCompat.getColor(AddStudentActivity.this, R.color.disabled_btn));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("TAG", "afterTextChanged: " + s.toString());

                // TODO Auto-generated method stub
            }
        });

    }

    private void hitAddStudentService() {
        Map<String, Object> map = new HashMap<>();
        map.put("batchId", batchId);
        map.put("studentName", binding.edtName.getText().toString());
        map.put("studentNumber", binding.edtPhnNumber.getText().toString());
 /*       if (!TextUtils.isEmpty(binding.edtMail.getText().toString()))
            map.put("email", binding.edtMail.getText().toString());
        if (!TextUtils.isEmpty(binding.edtParentName.getText().toString()))
            map.put("pName", binding.edtParentName.getText().toString());
        if (!TextUtils.isEmpty(binding.edtParentEmail.getText().toString()))
            map.put("pEmail", binding.edtParentEmail.getText().toString());
        if (!TextUtils.isEmpty(binding.edtParentMobile.getText().toString()))
            map.put("pNumber", binding.edtParentMobile.getText().toString());*/
        new MyClient(this).addStudentToBatch(map, (content, error) -> {
            if (content != null) {
                ServerResponse response = (ServerResponse) content;
                if (response.getStatus() != null && response.getStatus() == 200) {
                    if (response.getSuccess() && response.getResult() != null) {
                        /*  Utilities.makeToast(this, response.getResult());*/
                        setResult(1);
                        openDialog(response.getResult());
                    } else if (response.getResult() != null)
                        Utilities.makeToast(this, response.getResult());

                } else if (response.getStatus() != null && response.getStatus() == 403)
                    Utilities.openUnauthorizedDialog(this);
                else Utilities.makeToast(this, getString(R.string.server_error));
            } else Utilities.makeToast(this, getString(R.string.server_error));
            if (progressView != null)
                progressView.dismiss();
        });

    }

    private void openDialog(String result) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(result + "\n" + getString(R.string.do_you_want_to_add_more_students));
        alertBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                binding.edtName.setText("");
                binding.edtPhnNumber.setText("");
                Utilities.openKeyboard(AddStudentActivity.this, binding.edtName);

            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!isFinishing()) {
                    dialog.dismiss();
                    finish();
                }
            }
        });
        AlertDialog dialog = alertBuilder.create();
        dialog.setCancelable(false);
        if (!isFinishing())
            dialog.show();
    }

    private boolean checkValidation() {
        if (TextUtils.isEmpty(binding.edtName.getText().toString())) {
            Utilities.makeToast(this, "Please enter name");
            return false;
        }
        if (TextUtils.isEmpty(binding.edtPhnNumber.getText().toString())) {
            Utilities.makeToast(this, "Please enter number");
            return false;
        }
        if (binding.edtPhnNumber.getText().toString().length() < 10) {
            Utilities.makeToast(this, "Please enter valid number");
            return false;
        }
        if (!TextUtils.isEmpty(binding.edtMail.getText().toString()) && !Patterns.EMAIL_ADDRESS.matcher(binding.edtMail.getText().toString()).matches()) {
            Utilities.makeToast(this, "Please provide valid email address");
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utilities.hideKeyBoard(this);
    }
}