package com.so.luotk.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.so.luotk.R;
import com.so.luotk.activities.adminrole.AdminMainScreen;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.GenericTextWatcher;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.ActivitySignUpWithEmailBinding;
import com.so.luotk.models.input.CheckUserExistInput;
import com.so.luotk.models.input.LoginSignupInput;
import com.so.luotk.models.input.VerifyLoginInput;
import com.so.luotk.models.output.CheckuserexistResponse;
import com.so.luotk.models.output.LoginSignupResponse;
import com.so.luotk.models.output.VerifyLoginResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Callback;
import retrofit2.Response;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;

public class SignUpWithEmail extends AppCompatActivity {

    ActivitySignUpWithEmailBinding binding;
    private APIInterface apiInterface;
    private Toolbar toolbar;
    private TextView toolbarCustomTitle;
    private static final String TAG = "SignupActivityNew";
    // @BindView(R.id.input_address)
    //EditText _addressText;
    private final int count = 0;
    private ProgressView mProgressDialog;
    private String mobileNumber, fromIntent;
    private final String orgCode = "tbhgo";
    private String orgName;
    private long mLastClickTime = 0;

    private boolean tooManyRequest;
    private int clickCount = 0;

    @Override
    protected void onStart() {
        super.onStart();
        mLastClickTime = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utilities.hideKeyBoard(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        binding = ActivitySignUpWithEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utilities.setUpStatusBar(this);
        apiInterface = ApiUtils.getApiInterface();
        orgName = PreferenceHandler.readString(this, PreferenceHandler.ORG_NAME, null);
        if (getIntent() != null) {
            mobileNumber = getIntent().getStringExtra("mobileNumber");
            if(mobileNumber!=null&&mobileNumber.contains("91 "))
            {
                String temp= mobileNumber.replace("91 ","");
                mobileNumber=temp;
            }
            fromIntent = getIntent().getStringExtra("fromIntent");
            if (mobileNumber != null) {
                binding.mobileNumberEt.setText(mobileNumber);
                binding.mobileNumberEt.requestFocus();
            } else {
                binding.emailEtLayout.setEnabled(true);
                binding.emailEtLayout.getEditText().requestFocus();
            }
        }
        openKeyboard();
        setToolbar();
        String locale = PreferenceHandler.readString(SignUpWithEmail.this, PreferenceHandler.LOCALE, "en");

        if (locale.equals("en")) {
            setClickableString("Terms & Conditions", "Read our Terms & Conditions of using the product", binding.termsAndPrivacyText2);
        }
        if (locale.equals("hi")) {
            setClickableString("नियम और शर्तें", "उत्पाद का उपयोग करने के हमारे नियम और शर्तें पढ़ें", binding.termsAndPrivacyText2);
        }
        if (locale.equals("mr")) {
            setClickableString("वाचा & विद्युतप्रवाह", "आमच्या अटी वाचा & विद्युतप्रवाह मोजण्याच्या एककाचे संक्षिप्त रुप; उत्पादन वापरण्याच्या अटी", binding.termsAndPrivacyText2);
        }
        if (locale.equals("kn")) {
            setClickableString("ನಿಯಮಗಳು ಮತ್ತು ನಿಬಂಧನೆಗಳು", "ಉತ್ಪನ್ನವನ್ನು ಬಳಸುವ ನಮ್ಮ ನಿಯಮಗಳು ಮತ್ತು ನಿಬಂಧನೆಗಳು ಓದಿ", binding.termsAndPrivacyText2);
        }
        if (locale.equals("ar")) {
            setClickableString("الشروط والأحكام", "اقرأ الشروط والأحكام الخاصة باستخدام المنتج", binding.termsAndPrivacyText2);
        }
        if (locale.equals("pa")) {
            setClickableString("ਨਿਯਮ ਅਤੇ ਸ਼ਰਤਾਂ", "ਉਤਪਾਦ ਦੀ ਵਰਤੋਂ ਕਰਨ ਦੇ ਸਾਡੇ ਨਿਯਮ ਅਤੇ ਸ਼ਰਤਾਂ ਪੜ੍ਹੋ", binding.termsAndPrivacyText2);
        }
        if (locale.equals("or")) {
            setClickableString("ସର୍ତ୍ତାବଳୀ", "ଉତ୍ପାଦ ବ୍ୟବହାର କରିବାର ସର୍ତ୍ତାବଳୀ |", binding.termsAndPrivacyText2);
        }
        binding.emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivityNew.class);
                intent.putExtra("mobileNumber", "");
                intent.putExtra("fromIntent", "Login");
                startActivity(intent);
            }
        });
        binding.loginDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                signup();
            }
        });

       /* _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), com.so.bgjcn.activities.LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });*/

        setupMobileEditText();

        binding.nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.characterLimitWarning.setVisibility(View.INVISIBLE);
                if (binding.nameEt.getText().toString().isEmpty()) {
//                    layoutName.setBackgroundResource(R.drawable.bg_flag_box);
                    //  DrawableCompat.setTint(edtName.getBackground(), ContextCompat.getColor(SignupActivity.this, R.color.light_gray));
                } else {
//                    layoutName.setBackgroundResource(R.drawable.bg_flag_box);
                    // DrawableCompat.setTint(edtName.getBackground(), ContextCompat.getColor(SignupActivity.this, R.color.dark_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
    public void setClickableString(String clickableValue, String wholeValue, TextView yourTextView){
        String value = wholeValue;
        SpannableString spannableString = new SpannableString(value);
        int startIndex = value.indexOf(clickableValue);
        int endIndex = startIndex + clickableValue.length();
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false); // <-- this will remove automatic underline in set span
            }

            @Override
            public void onClick(View widget) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(SignUpWithEmail.this, TermsOfUseActivity.class));
                // do what you want with clickable value
            }
        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        yourTextView.setText(spannableString);
        yourTextView.setMovementMethod(LinkMovementMethod.getInstance()); // <-- important, onClick in ClickableSpan won't work without this
    }
    private void setupMobileEditText() {
        binding.mobileNumberEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.characterLimitWarningNumber.setVisibility(View.GONE);
                /*if (binding.mobileNumberEt.getText().toString().isEmpty()) {
                    layoutMobile.setBackgroundResource(R.drawable.bg_flag_box);
                } else {
                    layoutMobile.setBackgroundResource(R.drawable.bg_flag_box);
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup" + fromIntent);
        if (!checkValidation()) {
            return;
        } else if (binding.mobileNumberEt.getText().toString().equalsIgnoreCase("77777 33333")) {
            mProgressDialog = new ProgressView(this);
            mProgressDialog.show();
        } else {
            if (fromIntent != null) {
                if (fromIntent.equalsIgnoreCase("SignUp")) {
                    hitSignupService();

                } else {
                    hitCheckUserExistService();
                }
            } else {
                hitCheckUserExistService();
            }
        }
    }

    private boolean checkValidation() {
        if (binding.mobileNumberEt.getText().toString().isEmpty()) {
            binding.characterLimitWarningNumber.setVisibility(View.VISIBLE);
            binding.characterLimitWarningNumber.setText(getString(R.string.enter_valid_phone_number));
            binding.mobileNumberEt.requestFocus();
//            layoutMobile.setBackgroundResource(R.drawable.bg_red_outline);
            // DrawableCompat.setTint(edtMobile.getBackground(), ContextCompat.getColor(this, R.color.light_red));
            return false;
        } else if (binding.mobileNumberEt.getText().toString().length() < 10) {
            binding.characterLimitWarningNumber.setVisibility(View.VISIBLE);
            binding.characterLimitWarningNumber.setText(getString(R.string.enter_valid_phone_number));
            binding.mobileNumberEt.requestFocus();
//            layoutMobile.setBackgroundResource(R.drawable.bg_red_outline);
            // DrawableCompat.setTint(edtMobile.getBackground(), ContextCompat.getColor(this, R.color.light_red));
            return false;

        } else if (binding.nameEt.getText().toString().isEmpty()) {
            binding.characterLimitWarning.setVisibility(View.VISIBLE);
            binding.characterLimitWarning.setText(getString(R.string.enter_your_name));
            binding.nameEt.requestFocus();
//            layoutName.setBackgroundResource(R.drawable.bg_red_outline);
            //DrawableCompat.setTint(edtName.getBackground(), ContextCompat.getColor(this, R.color.light_red));
            return false;
        } /*else if (!binding.checkTerms.isChecked()) {
            Utilities.makeToast(this, "Please accept terms and conditions");
            //DrawableCompat.setTint(edtName.getBackground(), ContextCompat.getColor(this, R.color.light_red));
            return false;
        } */else {
            binding.characterLimitWarningNumber.setVisibility(View.GONE);
            /*layoutMobile.setBackgroundResource(R.drawable.bg_flag_box);
            layoutName.setBackgroundResource(R.drawable.bg_flag_box);*/
            //DrawableCompat.setTint(edtMobile.getBackground(), ContextCompat.getColor(SignupActivity.this, R.color.light_gray));
            hideKeyboardFrom(SignUpWithEmail.this, binding.mobileNumberEt);
            hideKeyboardFrom(SignUpWithEmail.this, binding.nameEt);
            return true;
        }
    }

    private void setToolbar() {
        //toolbar setup
        /*if (orgName != null) {
            binding.profileHeading.setText("Join " + orgName);
        }*/
        toolbar = findViewById(R.id.toolbar);
        toolbarCustomTitle = findViewById(R.id.tv_toolbar_title);
        if (orgName != null) {
            toolbarCustomTitle.setText(getString(R.string.complete_profile));
        }
        setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);

        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public void openKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        binding.mobileNumberEt.requestFocus();
    }


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void hitCheckUserExistService() {
        final CheckUserExistInput input = new CheckUserExistInput();
        input.setPhone_number(binding.mobileNumberEt.getText().toString().replace(" ", ""));
        input.setOrgCode(PreferenceHandler.readString(this, PreferenceHandler.ORG_CODE, ""));
        mProgressDialog = new ProgressView(SignUpWithEmail.this);
        //mProgressDialog.setLoaingTitle("Authenticating...");
        mProgressDialog.show();
        retrofit2.Call<CheckuserexistResponse> call = apiInterface.checkIfUserAlreadyExist(input);
        call.enqueue(new Callback<CheckuserexistResponse>() {
            @Override
            public void onResponse(retrofit2.Call<CheckuserexistResponse> call, Response<CheckuserexistResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            if ((response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("true"))) {
                                Intent intent = new Intent(SignUpWithEmail.this, AlreadyExistNumberActivity.class);
                                intent.putExtra("mobileNumber", binding.mobileNumberEt.getText().toString());
                                startActivity(intent);
                                binding.mobileNumberEt.setText("");
                                binding.nameEt.setText("");
                            } else {
                                hitSignupService();
                            }
                        }
                    } else {
                        Snackbar.make(binding.main, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onFailure(retrofit2.Call<CheckuserexistResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Snackbar.make(binding.main, getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    private void hitEmailLoginService(TextInputLayout textInputLayout, BottomSheetDialog dialog) {

        mProgressDialog = new ProgressView(SignUpWithEmail.this);
        mProgressDialog.show();

        String deviceId = Utilities.getUniqueDeviceId(this);
        final com.so.luotk.models.newmodels.loginModel.LoginSignupInput input = new com.so.luotk.models.newmodels.loginModel.LoginSignupInput();
        input.setEmail(textInputLayout.getEditText().getText().toString().replace(" ", ""));
        input.setDeviceId(deviceId);
        input.setDeviceType("Android");
        input.setOrgCode(PreferenceHandler.readString(this, PreferenceHandler.ORG_CODE, ""));

        new MyClient(SignUpWithEmail.this).emailLogin(input, (content, error) -> {
            mProgressDialog.dismiss();
            if (content != null) {
                com.so.luotk.models.newmodels.loginModel.LoginSignupResponse response = (com.so.luotk.models.newmodels.loginModel.LoginSignupResponse) content;
                if (response.getStatus() != null && response.getStatus() == 200) {
                    if (response.getSuccess() == true) {
                        BottomDialogOtp(textInputLayout.getEditText().getText().toString(), String.valueOf(response.getResult().getOtp()),
                                String.valueOf(response.getResult().getUserId()), true);
                        /*Intent intent = new Intent(WelcomeActivityNew.this, OTPActivity.class);
                        intent.putExtra("mobileNumber", textInputLayout.getEditText().getText().toString());
                        intent.putExtra("otp", response.getResult().getOtp());
                        intent.putExtra("userId", response.getResult().getUserId());
                        intent.putExtra("isFromEmail", true);
                        startActivity(intent);*/
                    }

                } else if (response.getStatus() != null && response.getStatus() == 406) {
                    Intent intent = new Intent(SignUpWithEmail.this, SignUpWithEmail.class);
                    intent.putExtra("mobileNumber", textInputLayout.getEditText().getText().toString());
                    startActivity(intent);

//                    Utilities.makeToast(this, "New Email");
                } else {
                    Utilities.makeToast(this, getString(R.string.server_error));
                }
            }

        });
    }

    private void hitSignupService() {
        String deviceId = Utilities.getUniqueDeviceId(this);
        final com.so.luotk.models.newmodels.loginModel.LoginSignupInput input = new com.so.luotk.models.newmodels.loginModel.LoginSignupInput();
        input.setEmail(binding.mobileNumberEt.getText().toString().replace(" ", ""));
        input.setDeviceId(deviceId);
        input.setDeviceType("Android");
        input.setOrgCode(PreferenceHandler.readString(this, PreferenceHandler.ORG_CODE, ""));
        input.setName(binding.nameEt.getText().toString().trim());
        mProgressDialog = new ProgressView(SignUpWithEmail.this);
        mProgressDialog.show();

        new MyClient(SignUpWithEmail.this).emailLogin(input, (content, error) -> {
            mProgressDialog.dismiss();
            if (content != null) {
                com.so.luotk.models.newmodels.loginModel.LoginSignupResponse response = (com.so.luotk.models.newmodels.loginModel.LoginSignupResponse) content;
                if (response.getStatus() != null && response.getStatus() == 200) {
                    if (response.getSuccess() == true) {
                        BottomDialogOtp(binding.mobileNumberEt.getText().toString(), String.valueOf(response.getResult().getOtp()),
                                String.valueOf(response.getResult().getUserId()), true);
                        /*Intent intent = new Intent(WelcomeActivityNew.this, OTPActivity.class);
                        intent.putExtra("mobileNumber", textInputLayout.getEditText().getText().toString());
                        intent.putExtra("otp", response.getResult().getOtp());
                        intent.putExtra("userId", response.getResult().getUserId());
                        intent.putExtra("isFromEmail", true);
                        startActivity(intent);*/
                    }

                } else if (response.getStatus() != null && response.getStatus() == 406) {
                    Intent intent = new Intent(SignUpWithEmail.this, SignUpWithEmail.class);
                    intent.putExtra("mobileNumber", binding.mobileNumberEt.getText().toString());
                    startActivity(intent);

//                    Utilities.makeToast(this, "New Email");
                } else {
                    Utilities.makeToast(this, getString(R.string.server_error));
                }
            }

        });

        /*retrofit2.Call<LoginSignupResponse> call = apiInterface.loginSignup(input);
        call.enqueue(new Callback<LoginSignupResponse>() {
            @Override
            public void onResponse(retrofit2.Call<LoginSignupResponse> call, Response<LoginSignupResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getResult() != null && response.body().getSuccess().equalsIgnoreCase("true")) {

                            BottomDialogOtp(binding.mobileNumberEt.getText().toString(), response.body().getResult().getOtp(),
                                    response.body().getResult().getUser_id(), false);

                           *//* Intent intent = new Intent(SignUpActivityNew.this, OTPActivity.class);
                            intent.putExtra("mobileNumber", "91 " + binding.mobileNumberEt.getText().toString());
                            intent.putExtra("otp", response.body().getResult().getOtp());
                            intent.putExtra("userId", response.body().getResult().getUser_id());
                            intent.putExtra("isFromEmail", false);
                            startActivity(intent);*//*
                            PreferenceHandler.writeString(SignUpWithEmail.this, PreferenceHandler.USER_NAME, binding.nameEt.getText().toString());
                            // response.body().getResult().getOtp()

                        }
                    } else {
                        Snackbar.make(binding.main, "Server error", Snackbar.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(retrofit2.Call<LoginSignupResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Snackbar.make(binding.main, "Server Error", Snackbar.LENGTH_SHORT).show();
            }
        });*/

    }

    public void BottomDialogOtp(final String mobileNUmber, final String otp, final String userId, final boolean isFromEmail) {

        TextInputEditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four;
        TextView otp_error_text, edit_button, otp_resend_text;
        MaterialButton continue_button;
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.DialogStyle);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_dialog_otp, null);
        Objects.requireNonNull(dialog.getWindow())
                .setSoftInputMode(SOFT_INPUT_STATE_VISIBLE);
        dialog.setContentView(sheetView);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        otp_textbox_one = sheetView.findViewById(R.id.otp1);
        otp_textbox_two = sheetView.findViewById(R.id.otp2);
        otp_textbox_three = sheetView.findViewById(R.id.otp3);
        otp_textbox_four = sheetView.findViewById(R.id.otp4);
        otp_error_text = sheetView.findViewById(R.id.otp_error_text);
        edit_button = sheetView.findViewById(R.id.edit_button);
        otp_resend_text = sheetView.findViewById(R.id.otp_resend_text);
        continue_button = sheetView.findViewById(R.id.continue_button);

        TextInputEditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four};

        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit));
        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit));
        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit));
        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit));


        otp_textbox_one.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String otp_textbox_oneStr = otp_textbox_one.getText().toString();
                String otp_textbox_twoStr = otp_textbox_two.getText().toString();
                String otp_textbox_threeStr = otp_textbox_three.getText().toString();
                String otp_textbox_fourStr = otp_textbox_four.getText().toString();
                continue_button.setEnabled(!otp_textbox_oneStr.isEmpty() && !otp_textbox_twoStr.isEmpty() && !otp_textbox_threeStr.isEmpty() && !otp_textbox_fourStr.isEmpty());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s.toString());

                // TODO Auto-generated method stub
            }
        });

        otp_textbox_two.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String otp_textbox_oneStr = otp_textbox_one.getText().toString();
                String otp_textbox_twoStr = otp_textbox_two.getText().toString();
                String otp_textbox_threeStr = otp_textbox_three.getText().toString();
                String otp_textbox_fourStr = otp_textbox_four.getText().toString();
                continue_button.setEnabled(!otp_textbox_oneStr.isEmpty() && !otp_textbox_twoStr.isEmpty() && !otp_textbox_threeStr.isEmpty() && !otp_textbox_fourStr.isEmpty());

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s.toString());

                // TODO Auto-generated method stub
            }
        });

        otp_textbox_three.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String otp_textbox_oneStr = otp_textbox_one.getText().toString();
                String otp_textbox_twoStr = otp_textbox_two.getText().toString();
                String otp_textbox_threeStr = otp_textbox_three.getText().toString();
                String otp_textbox_fourStr = otp_textbox_four.getText().toString();
                continue_button.setEnabled(!otp_textbox_oneStr.isEmpty() && !otp_textbox_twoStr.isEmpty() && !otp_textbox_threeStr.isEmpty() && !otp_textbox_fourStr.isEmpty());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s.toString());

                // TODO Auto-generated method stub
            }
        });

        otp_textbox_four.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String otp_textbox_oneStr = otp_textbox_one.getText().toString();
                String otp_textbox_twoStr = otp_textbox_two.getText().toString();
                String otp_textbox_threeStr = otp_textbox_three.getText().toString();
                String otp_textbox_fourStr = otp_textbox_four.getText().toString();
                continue_button.setEnabled(!otp_textbox_oneStr.isEmpty() && !otp_textbox_twoStr.isEmpty() && !otp_textbox_threeStr.isEmpty() && !otp_textbox_fourStr.isEmpty());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s.toString());

                // TODO Auto-generated method stub
            }
        });


        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        otp_resend_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCount < 2) {
                    hitResendOtpService(mobileNUmber, isFromEmail, otp_resend_text);
                } else {
                    if (tooManyRequest) {
                        Utilities.makeToast(SignUpWithEmail.this, "Too many requests! Please try after 3 minutes");
//                        Snackbar.make(layoutOTP, "Too many requests! Please try after 3 minutes", Snackbar.LENGTH_SHORT).show();
                    } else {
                        setOtpClickTimer();
                    }
                }

//                hitResendOtpService(mobileNUmber, isFromEmail, otp_resend_text);
            }
        });
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp_textbox_one.getText().toString().length() != 0 && otp_textbox_two.getText().toString().length() != 0 &&
                        otp_textbox_three.getText().toString().length() != 0 && otp_textbox_four.getText().toString().length() != 0) {
                    otp_error_text.setVisibility(View.GONE);
                    String otpValue = otp_textbox_one.getText().toString() + otp_textbox_two.getText().toString() +otp_textbox_three.getText().toString() + otp_textbox_four.getText().toString();
                    hitVerifyLoginService(mobileNUmber, otp, userId, isFromEmail, otpValue, otp_error_text, dialog);
                } else {
                    otp_error_text.setVisibility(View.VISIBLE);
                    otp_error_text.setText(getString(R.string.invalid_otp));
                }
            }
        });


        TextView otp_sent_to = sheetView.findViewById(R.id.otp_sent_to);
        otp_sent_to.setText(getString(R.string.otp_sent_to) + " " + mobileNUmber);

        if (mobileNUmber.equals(SplashActivity.StaticNumner)) {
            otp_resend_text.setVisibility(View.GONE);
        } else  {
            startTimer(60000, otp_resend_text);
            otp_resend_text.setEnabled(false);
        }
        dialog.show();

    }
    private void startTimer(long milliseconds, TextView textView) {
        new CountDownTimer(milliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update the timer text during the countdown
                textView.setTextColor(Color.parseColor("#cccccc"));             //blue color to enable the click
                textView.setClickable(false);
                textView.setText("Resend OTP in " + millisUntilFinished / 1000 + " seconds");
            }

            public void onFinish() {
                // Enable the EditText and update the timer text when the countdown is finished
                textView.setTextColor(Color.parseColor("#307CE9"));             //blue color to enable the click
                textView.setClickable(true);
                textView.setText("Resend OTP");
                textView.setEnabled(true);
                //    otpEditText.setEnabled(true);
            }
        }.start();
    }
    private void hitVerifyLoginService(final String mobileNUmber, final String otp, final String userId, final boolean isFromEmail, String otpValue, TextView otp_error_text, BottomSheetDialog dialog) {
        final VerifyLoginInput input = new VerifyLoginInput();
        input.setId(userId);
        input.setOtp(otpValue);
        input.setFcm_token(PreferenceHandler.readString(this, PreferenceHandler.FCM_TOKEN, ""));
        Log.e("fcmToken: ", PreferenceHandler.readString(this, PreferenceHandler.FCM_TOKEN, ""));
        input.setDevice_type("Android");
        mProgressDialog = new ProgressView(SignUpWithEmail.this);
        //mProgressDialog.setLoaingTitle("Authenticating...");
        mProgressDialog.show();
        retrofit2.Call<VerifyLoginResponse> call = apiInterface.verifyLogin(input);
        call.enqueue(new Callback<VerifyLoginResponse>() {
            @Override
            public void onResponse(retrofit2.Call<VerifyLoginResponse> call, Response<VerifyLoginResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getResult() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            PreferenceHandler.writeString(SignUpWithEmail.this, PreferenceHandler.TOKEN, response.body().getResult().getToken());/*"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiNzY5YjI1MDNjYTRmNjViMDliNDRlZWUxNGM5YWVkNTBmNTc2ZmQ1MDUyZGE2ZTg2NWU4OWFlMTAyODkwOTlkNGE3YmE4ZGU0OTcxNjI3NWEiLCJpYXQiOjE1OTQ2MzUxMzAsIm5iZiI6MTU5NDYzNTEzMCwiZXhwIjoxNjI2MTcxMTMwLCJzdWIiOiIzNiIsInNjb3BlcyI6W119.SgPq2hrWTNgpJ64F3_RnW6Xv3wkR59Upkpc4kxKFh2xbjAOUNsvUEVqm9noV-EiCrOjNeDiQMj_xmyLZQuW9fSeMT7AyZqqCd6hSi0okntpaVTpNI44alhmqfWwqXlR4FUNWSD_QrqFvFDOsbVWAEM1p5Q7Jco6hBzq4USQ9prT9zUFELe9HL2ZY4uRz0dwOtUZgur5QB7K4xLZEL5__4-IGMcxqL5P3-lX1h7TShzXIJlJ4JlgVfAK4Lx45EwBdFvE7fk-hgzXqbYJo0lCs5IClQy6xrDs_Dy1yZSWR8k-gUGEyZO2DzvuMz43wjnZLZJOuP9wDr9jotViRRvl68go1LfcTzhYr6hX5VzaJh_4ZelUV7eLkeqAdwaFvZWF7qgJYSCEeq90iuQdpWPa1agxx59O44CsUpSPLyAR0wNaPWvELz-6T1GDJC3JBxZTtiKtunfxdEbyRZtgBqIrMqz1or-blwY3j3Onsn42--2qml5skgqmfeVM3fSbioR3VYDw_6-oYk-QR6SyTVpvZK7BwHXmPvBKKJbXHvCnzP-00oiiiEdR--T7wCDAI-UfXsomWCuv6BKMM2Jf4gk39tnxfNVQ1trGxo4j_MSVYF_PZYjXmfGZm2Hva_q0t73dlFa7hF34fLHiXVwoNHZkKHT1q9jQD-NKWv2MKfi6mzk4"*/
                            PreferenceHandler.writeString(SignUpWithEmail.this, PreferenceHandler.DEVICE_ID, response.body().getResult().getUserDetail().getDevice_id());
                            PreferenceHandler.writeString(SignUpWithEmail.this, PreferenceHandler.USER_TYPE, response.body().getResult().getType());
                            PreferenceHandler.writeString(SignUpWithEmail.this, PreferenceHandler.USERID, response.body().getResult().getUserDetail().getId());
                            PreferenceHandler.writeBoolean(SignUpWithEmail.this, PreferenceHandler.ISSTATICLOGIN, false);
                            PreferenceHandler.writeBoolean(SignUpWithEmail.this, PreferenceHandler.CREATED_BATCH, response.body().getResult().getBatch() != null && response.body().getResult().getBatch().equalsIgnoreCase("true"));

                            try {
                                Date c = Calendar.getInstance().getTime();

                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                String formattedDate = df.format(c);
                                Log.d(TAG, "onResponse: " + formattedDate);
                                PreferenceHandler.writeString(SignUpWithEmail.this, PreferenceHandler.LAST_LOGIN_DATE, formattedDate);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                            if (response.body().getResult().getType().equalsIgnoreCase("organisation") ||
                                    response.body().getResult().getType().equalsIgnoreCase("faculty")) {
                                PreferenceHandler.writeBoolean(SignUpWithEmail.this, PreferenceHandler.ADMIN_LOGGED_IN, true);
                                Intent intent = new Intent(SignUpWithEmail.this, AdminMainScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                PreferenceHandler.writeBoolean(SignUpWithEmail.this, PreferenceHandler.LOGGED_IN, true);
                                Intent intent = new Intent(SignUpWithEmail.this, MainActivity.class);
                                PreferenceHandler.writeBoolean(SignUpWithEmail.this, PreferenceHandler.IS_EMAIL_LOGIN, isFromEmail);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                            PreferenceHandler.writeString(SignUpWithEmail.this, PreferenceHandler.LOGGED_IN_USERNAME, response.body().getResult().getUserDetail().getName());
                            PreferenceHandler.writeString(SignUpWithEmail.this, PreferenceHandler.USER_MOBILE, mobileNUmber);
                            finish();
                        }
                    } else {
                        otp_error_text.setVisibility(View.VISIBLE);
                        otp_error_text.setText(getString(R.string.wrong_otp));
                        //  DrawableCompat.setTint(edtOtp.getBackground(), ContextCompat.getColor(OTPActivity.this, R.color.light_red));
                    }

                }

            }

            @Override
            public void onFailure(retrofit2.Call<VerifyLoginResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.e("Retrofit Failure", t.getMessage());
                Utilities.makeToast(SignUpWithEmail.this, getString(R.string.server_error));
            }
        });

    }

    private void hitResendOtpService(String mobileNUmber, boolean isFromEmail, TextView otp_resend_text) {
        String deviceId = Utilities.getUniqueDeviceId(this);
        final LoginSignupInput input = new LoginSignupInput();
        if (isFromEmail)
            input.setEmail(mobileNUmber);
        else
            input.setPhone_number(mobileNUmber.replace(" ", ""));
        input.setDevice_id(deviceId);
        input.setDevice_type("Android");
        input.setOrgCode(PreferenceHandler.readString(this, PreferenceHandler.ORG_CODE, ""));
        otp_resend_text.setClickable(false);
        otp_resend_text.setTextColor(Color.parseColor("#434343"));
        retrofit2.Call<LoginSignupResponse> call;
        if (isFromEmail)
            call = apiInterface.emaillogin(input);
        else
            call = apiInterface.loginSignup(input);
        mProgressDialog = new ProgressView(this);
        mProgressDialog.show();
        call.enqueue(new Callback<LoginSignupResponse>() {
            @Override
            public void onResponse(retrofit2.Call<LoginSignupResponse> call, Response<LoginSignupResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getResult() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            Utilities.makeToast(SignUpWithEmail.this, "OTP has been sent!");
//                            Snackbar.make(WelcomeActivityNew.this, "OTP has been sent!", Snackbar.LENGTH_SHORT).show();
                            otp_resend_text.setTextColor(Color.parseColor("#307CE9"));             //blue color to enable the click
                            otp_resend_text.setClickable(true);
                            clickCount++;
                            startTimer(60000, otp_resend_text);
                            otp_resend_text.setEnabled(false);
                            // PreferenceHandler.writeString(OTPActivity.this, PreferenceHandler.OTP_CLICK_COUNT, clickCount + "");
                        }
                    } else {
                        Utilities.makeToast(SignUpWithEmail.this, getString(R.string.server_error));
//                        Snackbar.make(layoutOTP, "Server error", Snackbar.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(retrofit2.Call<LoginSignupResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.e("Retrofit Failure", t.getMessage());
                Utilities.makeToast(SignUpWithEmail.this, getString(R.string.server_error));
            }
        });

    }

    private void setOtpClickTimer() {
        new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {
                tooManyRequest = true;
            }

            public void onFinish() {
                clickCount = 0;
                tooManyRequest = false;
            }

        }.start();
    }

}