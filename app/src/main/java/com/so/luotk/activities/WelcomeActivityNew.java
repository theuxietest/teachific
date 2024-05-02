package com.so.luotk.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.so.luotk.R;
import com.so.luotk.activities.adminrole.AdminMainScreen;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.GenericTextWatcher;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.ActivityWelcomeNewBinding;
import com.so.luotk.models.input.CheckUserExistInput;
import com.so.luotk.models.input.LoginSignupInput;
import com.so.luotk.models.input.VerifyLoginInput;
import com.so.luotk.models.output.CheckuserexistResponse;
import com.so.luotk.models.output.LoginSignupResponse;
import com.so.luotk.models.output.VerifyLoginResponse;
import com.so.luotk.models.output.VerifyOrgResponse;
import com.so.luotk.utils.Config;
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

public class WelcomeActivityNew extends AppCompatActivity {

    public static final String TAG ="WekcomeNew";
    private String mobileNumber;
    private APIInterface apiInterface;
    private static ProgressView mProgressDialog;
    private long mLastClickTime = 0;
    private boolean tooManyRequest;
    private int clickCount = 0;
    ActivityWelcomeNewBinding binding;
    ImageView phone_number_layout;

    @Override
    protected void onStart() {
        super.onStart();
        mLastClickTime = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        binding = ActivityWelcomeNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utilities.setUpStatusBar(this);
        apiInterface = ApiUtils.getApiInterface();
        changeStatusBarColor();
        if (Utilities.checkInternet(this)) {
            hitVerifyOrgCodeService();
        } else {
            //Toast.makeText(com.so.bgjcn.activities.WelcomeActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
            Toast.makeText(WelcomeActivityNew.this, getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
            // showInternetAlert(this, "verifyOrgCode");
        }

        binding.phoneNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomDialog("");
                /*LoginBottomSheet bottomSheetDialogCommon = new LoginBottomSheet(WelcomeActivityNew.this,"BottomSheetMessage");
                bottomSheetDialogCommon.show(getSupportFragmentManager(), "exampleLoginBottomSheet");*/
            }
        });

        binding.changeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomDialogEmail("", 1);
            }
        });

        binding.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomDialog("");
            }
        });
        String locale = PreferenceHandler.readString(WelcomeActivityNew.this, PreferenceHandler.LOCALE, "en");
        if (locale.equals("en")) {
            setClickableString("Terms & Conditions", "Read our Terms & Conditions of using the product", binding.termsAndPrivacyText);
        }
        if (locale.equals("hi")) {
            setClickableString("नियम और शर्तें", "उत्पाद का उपयोग करने के हमारे नियम और शर्तें पढ़ें", binding.termsAndPrivacyText);
        }
        if (locale.equals("mr")) {
            setClickableString("वाचा & विद्युतप्रवाह", "आमच्या अटी वाचा & विद्युतप्रवाह मोजण्याच्या एककाचे संक्षिप्त रुप; उत्पादन वापरण्याच्या अटी", binding.termsAndPrivacyText);
        }
        if (locale.equals("kn")) {
            setClickableString("ನಿಯಮಗಳು ಮತ್ತು ನಿಬಂಧನೆಗಳು", "ಉತ್ಪನ್ನವನ್ನು ಬಳಸುವ ನಮ್ಮ ನಿಯಮಗಳು ಮತ್ತು ನಿಬಂಧನೆಗಳು ಓದಿ", binding.termsAndPrivacyText);
        }
        if (locale.equals("ar")) {
            setClickableString("الشروط والأحكام", "اقرأ الشروط والأحكام الخاصة باستخدام المنتج", binding.termsAndPrivacyText);
        }
        if (locale.equals("pa")) {
            setClickableString("ਨਿਯਮ ਅਤੇ ਸ਼ਰਤਾਂ", "ਉਤਪਾਦ ਦੀ ਵਰਤੋਂ ਕਰਨ ਦੇ ਸਾਡੇ ਨਿਯਮ ਅਤੇ ਸ਼ਰਤਾਂ ਪੜ੍ਹੋ", binding.termsAndPrivacyText);
        }
        if (locale.equals("or")) {
            setClickableString("ସର୍ତ୍ତାବଳୀ", "ଉତ୍ପାଦ ବ୍ୟବହାର କରିବାର ସର୍ତ୍ତାବଳୀ |", binding.termsAndPrivacyText);
        }
        String appName = getString(R.string.app_name);
        binding.teachmintTitle.setText(appName);

        binding.teachmintTitle.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = binding.teachmintTitle.getLineCount();
                if (lineCount == 1) {
                    binding.teachmintTitle.setTextSize(34);
                } else if (lineCount == 2) {
                    binding.teachmintTitle.setTextSize(23);
                }
                // Use lineCount here
            }
        });
        onTokenRefresh();
        deviceDetail();
    }

    public void onTokenRefresh() {

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                PreferenceHandler.writeString(getApplicationContext(), PreferenceHandler.FCM_TOKEN, token);
                Log.d(TAG, "onSuccess: " + token);
            }
        });
        /*FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            PreferenceHandler.writeString(getApplicationContext(), PreferenceHandler.FCM_TOKEN, instanceIdResult.getToken());
            Log.d("Firebase:", "Refreshed token: " + instanceIdResult.getToken());
        });
*/
    }

    public void BottomDialogEmail(String emailDefault, int from_page) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.DialogStyle);
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_login_email, null);
        Objects.requireNonNull(dialog.getWindow())
                .setSoftInputMode(SOFT_INPUT_STATE_VISIBLE);
        dialog.setContentView(sheetView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        TextInputLayout email_layout = sheetView.findViewById(R.id.email_layout);
        TextInputEditText email_text = sheetView.findViewById(R.id.email_text);
        MaterialButton send_otp_button = sheetView.findViewById(R.id.send_otp_button);
        TextView change_language = sheetView.findViewById(R.id.change_language);
        TextView tv_error_email = sheetView.findViewById(R.id.tv_error_email);
        ImageView globe_icon = sheetView.findViewById(R.id.globe_icon);
        TextView terms_and_privacy = sheetView.findViewById(R.id.terms_and_privacy_text2);


        String locale = PreferenceHandler.readString(WelcomeActivityNew.this, PreferenceHandler.LOCALE, "en");
        if (locale.equals("en")) {
            setClickableString("Terms & Conditions", "Read our Terms & Conditions of using the product", terms_and_privacy);
        }
        if (locale.equals("hi")) {
            setClickableString("नियम और शर्तें", "उत्पाद का उपयोग करने के हमारे नियम और शर्तें पढ़ें", terms_and_privacy);
        }
        if (locale.equals("mr")) {
            setClickableString("वाचा & विद्युतप्रवाह", "आमच्या अटी वाचा & विद्युतप्रवाह मोजण्याच्या एककाचे संक्षिप्त रुप; उत्पादन वापरण्याच्या अटी", terms_and_privacy);
        }
        if (locale.equals("kn")) {
            setClickableString("ನಿಯಮಗಳು ಮತ್ತು ನಿಬಂಧನೆಗಳು", "ಉತ್ಪನ್ನವನ್ನು ಬಳಸುವ ನಮ್ಮ ನಿಯಮಗಳು ಮತ್ತು ನಿಬಂಧನೆಗಳು ಓದಿ", terms_and_privacy);
        }
        if (locale.equals("ar")) {
            setClickableString("الشروط والأحكام", "اقرأ الشروط والأحكام الخاصة باستخدام المنتج", terms_and_privacy);
        }
        if (locale.equals("pa")) {
            setClickableString("ਨਿਯਮ ਅਤੇ ਸ਼ਰਤਾਂ", "ਉਤਪਾਦ ਦੀ ਵਰਤੋਂ ਕਰਨ ਦੇ ਸਾਡੇ ਨਿਯਮ ਅਤੇ ਸ਼ਰਤਾਂ ਪੜ੍ਹੋ", terms_and_privacy);
        }
        if (locale.equals("or")) {
            setClickableString("ସର୍ତ୍ତାବଳୀ", "ଉତ୍ପାଦ ବ୍ୟବହାର କରିବାର ସର୍ତ୍ତାବଳୀ |", terms_and_privacy);
        }

        email_layout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s.toString() +" : "+ count);
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: " + s.toString() +" : "+ count);
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s.toString());

                send_otp_button.setEnabled(email_text.getText().toString().matches(emailPattern) && s.length() > 0);

                // TODO Auto-generated method stub
            }
        });

        email_text.setText(emailDefault);
        email_text.setSelection(email_text.getText().length());
        send_otp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(email_layout.getEditText().getText().toString())) {
                    tv_error_email.setVisibility(View.GONE);
                    if (PreferenceHandler.readString(WelcomeActivityNew.this, PreferenceHandler.ISACTIVEORG, "0").equals("1")) {
                        loginWithEmail(email_layout, dialog);
                    } else {
                        Utilities.orgInActive(WelcomeActivityNew.this);
                    }
                } else {
                    tv_error_email.setText("The email is required");
                    tv_error_email.setVisibility(View.VISIBLE);
                }
            }
        });
        change_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                BottomDialog("");
               /* Intent intent = new Intent(getApplicationContext(), EmailLoginActivity.class);
                intent.putExtra("from_page", 1);
                startActivity(intent);*/
            }
        });
        globe_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                BottomDialog("");
               /* Intent intent = new Intent(getApplicationContext(), EmailLoginActivity.class);
                intent.putExtra("from_page", 1);
                startActivity(intent);*/
            }
        });
        dialog.show();

    }

    public void BottomDialog(String mobileNUmberDeafult) {

        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.DialogStyle);
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_login, null);
        Objects.requireNonNull(dialog.getWindow())
                .setSoftInputMode(SOFT_INPUT_STATE_VISIBLE);
        dialog.setContentView(sheetView);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        TextInputLayout textInputLayout = sheetView.findViewById(R.id.phone_number);
        TextInputEditText mobile_number_text = sheetView.findViewById(R.id.mobile_number_text);
        MaterialButton send_otp_button = sheetView.findViewById(R.id.send_otp_button);
        TextView change_language = sheetView.findViewById(R.id.change_language);
        ImageView globe_icon = sheetView.findViewById(R.id.globe_icon);
        TextView terms_and_privacy = sheetView.findViewById(R.id.terms_and_privacy_text2);

        /*SpannableString spannableStr = new SpannableString(getString(R.string.privacy_and_terms));
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.BLUE);
        spannableStr.setSpan(foregroundColorSpan, 9, 28, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        terms_and_privacy.setText(spannableStr);*/

        String locale = PreferenceHandler.readString(WelcomeActivityNew.this, PreferenceHandler.LOCALE, "en");
        if (locale.equals("en")) {
            setClickableString("Terms & Conditions", "Read our Terms & Conditions of using the product", terms_and_privacy);
        }
        if (locale.equals("hi")) {
            setClickableString("नियम और शर्तें", "उत्पाद का उपयोग करने के हमारे नियम और शर्तें पढ़ें", terms_and_privacy);
        }
        if (locale.equals("mr")) {
            setClickableString("वाचा & विद्युतप्रवाह", "आमच्या अटी वाचा & विद्युतप्रवाह मोजण्याच्या एककाचे संक्षिप्त रुप; उत्पादन वापरण्याच्या अटी", terms_and_privacy);
        }
        if (locale.equals("kn")) {
            setClickableString("ನಿಯಮಗಳು ಮತ್ತು ನಿಬಂಧನೆಗಳು", "ಉತ್ಪನ್ನವನ್ನು ಬಳಸುವ ನಮ್ಮ ನಿಯಮಗಳು ಮತ್ತು ನಿಬಂಧನೆಗಳು ಓದಿ", terms_and_privacy);
        }
        if (locale.equals("ar")) {
            setClickableString("الشروط والأحكام", "اقرأ الشروط والأحكام الخاصة باستخدام المنتج", terms_and_privacy);
        }
        if (locale.equals("pa")) {
            setClickableString("ਨਿਯਮ ਅਤੇ ਸ਼ਰਤਾਂ", "ਉਤਪਾਦ ਦੀ ਵਰਤੋਂ ਕਰਨ ਦੇ ਸਾਡੇ ਨਿਯਮ ਅਤੇ ਸ਼ਰਤਾਂ ਪੜ੍ਹੋ", terms_and_privacy);
        }
        if (locale.equals("or")) {
            setClickableString("ସର୍ତ୍ତାବଳୀ", "ଉତ୍ପାଦ ବ୍ୟବହାର କରିବାର ସର୍ତ୍ତାବଳୀ |", terms_and_privacy);
        }
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s.toString() +" : "+ count);
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: " + s.toString() +" : "+ count);
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s.toString());
                send_otp_button.setEnabled(s.toString().length() > 9);
                // TODO Auto-generated method stub
            }
        });

        mobile_number_text.setText(mobileNUmberDeafult);
        mobile_number_text.setSelection(mobile_number_text.getText().length());
        send_otp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.checkInternet(WelcomeActivityNew.this)) {
                    if (textInputLayout.getEditText().getText().toString().equals(SplashActivity.StaticNumner)) {
                        dialog.dismiss();
                        BottomDialogOtp(textInputLayout.getEditText().getText().toString(), SplashActivity.StaticOtp,
                                SplashActivity.StaticUserId, true);
                    } else {
                        if (PreferenceHandler.readString(WelcomeActivityNew.this, PreferenceHandler.ISACTIVEORG, "0").equals("1")) {
                            char firstChar = textInputLayout.getEditText().getText().toString().charAt(0);
                            if (Character.getNumericValue(firstChar) > 5) {
                                login(textInputLayout, dialog);
                            } else {
                                Utilities.makeToast(WelcomeActivityNew.this, "Please enter valid number");
                            }
                        } else {
                            Utilities.orgInActive(WelcomeActivityNew.this);
                        }
                    }

                } else {
                    showInternetAlert(WelcomeActivityNew.this, "emaillogin", textInputLayout, dialog);
                }
            }
        });
        change_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                BottomDialogEmail("", 1);
               /* Intent intent = new Intent(getApplicationContext(), EmailLoginActivity.class);
                intent.putExtra("from_page", 1);
                startActivity(intent);*/
            }
        });
        globe_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                BottomDialogEmail("", 1);
                /*Intent intent = new Intent(getApplicationContext(), EmailLoginActivity.class);
                intent.putExtra("from_page", 1);
                startActivity(intent);*/
            }
        });
        dialog.show();

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
                startActivity(new Intent(WelcomeActivityNew.this, TermsOfUseActivity.class));
                // do what you want with clickable value
            }
        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        yourTextView.setText(spannableString);
        yourTextView.setMovementMethod(LinkMovementMethod.getInstance()); // <-- important, onClick in ClickableSpan won't work without this
    }
    public void login(TextInputLayout textInputLayout, BottomSheetDialog dialog) {
        Log.d(TAG, "Login");

        if (Utilities.checkInternet(this)) {
            if (textInputLayout.getEditText().getText().toString().equals(SplashActivity.StaticNumner)) {
                hitLoginService(textInputLayout, dialog);
            } else {
                hitCheckUserExistService(textInputLayout, dialog);
            }

            // hitLoginService();
        } else {
            showInternetAlert(this, "CheckUserExist", textInputLayout, dialog);
        }

        // }
    }

    public void loginWithEmail(TextInputLayout textInputLayout, BottomSheetDialog dialog) {
            Log.d(TAG, "LoginWithEmail");

            if (Utilities.checkInternet(this)) {
                hitEmailLoginService(textInputLayout, dialog);
            } else {
                showInternetAlert(this, "emaillogin", textInputLayout, dialog);
            }

            // }
        }


    private void hitEmailLoginService(TextInputLayout textInputLayout, BottomSheetDialog dialog) {

        mProgressDialog = new ProgressView(WelcomeActivityNew.this);
        mProgressDialog.show();

        String deviceId = Utilities.getUniqueDeviceId(this);
        final com.so.luotk.models.newmodels.loginModel.LoginSignupInput input = new com.so.luotk.models.newmodels.loginModel.LoginSignupInput();
        input.setEmail(textInputLayout.getEditText().getText().toString().replace(" ", ""));
        input.setDeviceId(deviceId);
        input.setDeviceType("Android");
        input.setOrgCode(PreferenceHandler.readString(this, PreferenceHandler.ORG_CODE, ""));

        new MyClient(WelcomeActivityNew.this).emailLogin(input, (content, error) -> {
            mProgressDialog.dismiss();
            if (content != null) {
                com.so.luotk.models.newmodels.loginModel.LoginSignupResponse response = (com.so.luotk.models.newmodels.loginModel.LoginSignupResponse) content;
                if (response.getStatus() != null && response.getStatus() == 200) {
                    if (response.getSuccess() == true) {
                        dialog.dismiss();
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
                    Intent intent = new Intent(WelcomeActivityNew.this, SignUpWithEmail.class);
                    intent.putExtra("mobileNumber", textInputLayout.getEditText().getText().toString());
                    intent.putExtra("fromIntent", "SignUp");
                    startActivity(intent);

//                    Utilities.makeToast(this, "New Email");
                } else {
                    Utilities.makeToast(this, getString(R.string.server_error));
                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        // moveTaskToBack(true);
        finish();
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void hitCheckUserExistService(TextInputLayout textInputLayout, BottomSheetDialog dialog) {
        final CheckUserExistInput input = new CheckUserExistInput();
        input.setPhone_number(textInputLayout.getEditText().getText().toString().replace(" ", ""));
        input.setOrgCode(PreferenceHandler.readString(this, PreferenceHandler.ORG_CODE, ""));
        mProgressDialog = new ProgressView(WelcomeActivityNew.this);
        mProgressDialog.show();
        retrofit2.Call<CheckuserexistResponse> call = apiInterface.checkIfUserAlreadyExist(input);
        call.enqueue(new Callback<CheckuserexistResponse>() {
            @Override
            public void onResponse(retrofit2.Call<CheckuserexistResponse> call, Response<CheckuserexistResponse> response) {
                if (!isFinishing())
                    mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getResult() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            if ((response.body().getResult().equalsIgnoreCase("true"))) {
                                if (Utilities.checkInternet(WelcomeActivityNew.this)) {
                                    hitLoginService(textInputLayout, dialog);
                                } else {
                                    showInternetAlert(WelcomeActivityNew.this, "login", textInputLayout, dialog);
                                }
                            } else {
                                Intent intent = new Intent(WelcomeActivityNew.this, SignUpActivityNew.class);
                                intent.putExtra("mobileNumber", "91 "+textInputLayout.getEditText().getText().toString());
                                intent.putExtra("fromIntent", "Login");
                                startActivity(intent);
                                // hitLoginService();
                            }
                        }
                    } else {
                        Utilities.makeToast(getApplicationContext(), getString(R.string.server_error));
                    }
                }

            }

            @Override
            public void onFailure(retrofit2.Call<CheckuserexistResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.e("Retrofit Failure", t.getMessage());
                Utilities.makeToast(getApplicationContext(), getString(R.string.server_error));
            }
        });

    }

    private void hitLoginService(TextInputLayout textInputLayout, BottomSheetDialog dialog ) {
        String deviceId = Utilities.getUniqueDeviceId(this, textInputLayout.getEditText().getText().toString());
        Log.d(TAG, "hitLoginService: " + deviceId);
        final LoginSignupInput input = new LoginSignupInput();
        input.setPhone_number(textInputLayout.getEditText().getText().toString().replace(" ", ""));
        input.setDevice_id(deviceId);
        input.setDevice_type("Android");
        input.setOrgCode(PreferenceHandler.readString(this, PreferenceHandler.ORG_CODE, ""));
        mProgressDialog = new ProgressView(WelcomeActivityNew.this);
        //mProgressDialog.setLoaingTitle("Authenticating...");
        mProgressDialog.show();
        retrofit2.Call<LoginSignupResponse> call = apiInterface.loginSignup(input);
        call.enqueue(new Callback<LoginSignupResponse>() {
            @Override
            public void onResponse(retrofit2.Call<LoginSignupResponse> call, Response<LoginSignupResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                        if (response.body().getResult() != null) {
                            dialog.dismiss();
                            BottomDialogOtp(textInputLayout.getEditText().getText().toString(), response.body().getResult().getOtp(),
                                    response.body().getResult().getUser_id(), false);
                            /*Intent intent = new Intent(WelcomeActivityNew.this, OTPActivity.class);
                            intent.putExtra("mobileNumber", textInputLayout.getEditText().getText().toString());
                            intent.putExtra("otp", response.body().getResult().getOtp());
                            intent.putExtra("userId", response.body().getResult().getUser_id());
                            intent.putExtra("isFromEmail", false);
                            startActivity(intent);*/
                        }
                    } else {
                        Utilities.makeToast(getApplicationContext(), "Server error");
                    }
                }

            }

            @Override
            public void onFailure(retrofit2.Call<LoginSignupResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.e("Retrofit Failure", t.getMessage());
                Utilities.makeToast(getApplicationContext(), getString(R.string.server_error));
            }
        });

    }


    public void showInternetAlert(Context context, final String serviceToStart, TextInputLayout textInputLayout, BottomSheetDialog dialog) {
        Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_internet_alert);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDialog.show();
        mDialog.getWindow().setAttributes(lp);
        //mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final Button btnRetry = mDialog.findViewById(R.id.btn_retry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.checkInternet(WelcomeActivityNew.this)) {
                    if (serviceToStart.equalsIgnoreCase("CheckUserExist")) {
                        hitCheckUserExistService(textInputLayout, dialog);
                    } else if (serviceToStart.equalsIgnoreCase("emaillogin")){
                        hitEmailLoginService(textInputLayout, dialog);
                    }
                    /*else if (serviceToStart.equalsIgnoreCase("verifyOrgCode")) {
                        hitVerifyOrgCodeService();
                    }*/ else {
                        hitLoginService(textInputLayout, dialog);
                    }
                }
               /* else {
                    Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }*/

            }
        });
        mDialog.show();

    }

    private void hitVerifyOrgCodeService() {

        /*mProgressDialog = new ProgressView(WelcomeActivityNew.this);
        mProgressDialog.show();*/
//        retrofit2.Call<VerifyOrgResponse> call = apiInterface.verifyOrgCode("rmnoz");
        retrofit2.Call<VerifyOrgResponse> call = apiInterface.verifyOrgCode(getString(R.string.organization_code));
        call.enqueue(new Callback<VerifyOrgResponse>() {
            @Override
            public void onResponse(retrofit2.Call<VerifyOrgResponse> call, Response<VerifyOrgResponse> response) {
//                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess() != null && response.body().getSuccess().equalsIgnoreCase("true")) {
                            if ((response.body().getResult() != null)) {
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ADMIN_MOBILE_NO, response.body().getResult().getPhone());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ORG_CODE, response.body().getResult().getOrgCode());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ORG_NAME, response.body().getResult().getName());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ORG_EMAIL, response.body().getResult().getEmail());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ADMIN_ID, response.body().getResult().getFk_userId());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ORG_WEBSITE_LINK, response.body().getResult().getWebsite());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ORG_FB_LINK, response.body().getResult().getFacebook());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ORG_INSTAGRAM_LINK, response.body().getResult().getInstagram());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ORG_PHONE_NO, response.body().getResult().getPhone());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.SHARE_MESSAGE, response.body().getResult().getComment());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ORG_LOGO, response.body().getResult().getLogo());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.LIVE_OPTION, response.body().getResult().getLive_option());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.FEE_STRUCTURE_PHOTO, response.body().getResult().getFeeStucturephoto());
                                //Zoom dataNew
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ZOOM_USER_ID, response.body().getResult().getZoomUserId());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ZOOM_SDK_KEY, response.body().getResult().getZoomSdkKey());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ZOOM_SDK_SECRET, response.body().getResult().getZoomSdkSecret());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ZOOM_APP_KEY, response.body().getResult().getZoomAppKey());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ZOOM_APP_SECRET, response.body().getResult().getZoomAppSecret());
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ISACTIVEORG, response.body().getResult().getIsActive());

                            }
                        }
                    } else if ((response.body().getSuccess().equalsIgnoreCase("false") && response.body().getStatus().equalsIgnoreCase("402"))) {

                    }
                }

            }

            @Override
            public void onFailure(retrofit2.Call<VerifyOrgResponse> call, Throwable t) {
//                mProgressDialog.dismiss();
                Log.e("Retrofit Failure", t.getMessage());
                Toast.makeText(WelcomeActivityNew.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
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
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        final String[] otpValue = {""};
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
                if (isFromEmail) {
                    BottomDialogEmail(mobileNUmber,1);
                } else {
                    BottomDialog(mobileNUmber);
                }
            }
        });
        otp_resend_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCount < 2) {

                    hitResendOtpService(mobileNUmber, isFromEmail, otp_resend_text);
                } else {
                    if (tooManyRequest) {
                        Utilities.makeToast(WelcomeActivityNew.this, "Too many requests! Please try after 3 minutes");
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
                    if (mobileNUmber.equals(SplashActivity.StaticNumner) && otpValue.equals(SplashActivity.StaticOtp)) {
                        PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.TOKEN, Config.StaticBearerToken);
                        PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.DEVICE_ID, Config.StaticDeviceId);
                        PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.USER_TYPE, "student");
                        PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.USERID, userId);
                        PreferenceHandler.writeBoolean(WelcomeActivityNew.this, PreferenceHandler.CREATED_BATCH, false);
                        PreferenceHandler.writeBoolean(WelcomeActivityNew.this, PreferenceHandler.LOGGED_IN, true);
                        PreferenceHandler.writeBoolean(WelcomeActivityNew.this, PreferenceHandler.ISSTATICLOGIN, true);

                        try {
                            Date c = Calendar.getInstance().getTime();

                            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            String formattedDate = df.format(c);
                            Log.d(TAG, "onResponse: " + formattedDate);
                            PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.LAST_LOGIN_DATE, formattedDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                        PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.LOGGED_IN_USERNAME, "Vishal Kumar");
                        PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.USER_MOBILE, mobileNUmber);
                        Intent intent = new Intent(WelcomeActivityNew.this, MainActivity.class);
                        PreferenceHandler.writeBoolean(WelcomeActivityNew.this, PreferenceHandler.IS_EMAIL_LOGIN, isFromEmail);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        hitVerifyLoginService(mobileNUmber, otp, userId, isFromEmail, otpValue, otp_error_text, dialog);
                    }
                } else {
                   /* otp_error_text.setVisibility(View.VISIBLE);
                    otp_error_text.setText("Invalid OTP");*/
                    Toast.makeText(WelcomeActivityNew.this, "Invalid OTP entered", Toast.LENGTH_SHORT).show();
                }
            }
        });


        TextView otp_sent_to = sheetView.findViewById(R.id.otp_sent_to);
        if (isFromEmail) {
            otp_sent_to.setText(getString(R.string.otp_sent_to) + " " + mobileNUmber);
        } else {
            otp_sent_to.setText(getString(R.string.otp_sent_to) +" +91 " + mobileNUmber);
        }

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

    public String textWatcherCommon(TextInputEditText textInputEditText) {
        final String[] enteredValue = new String[0];
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + s.toString());
                enteredValue[0] = s.toString();
                // TODO Auto-generated method stub
            }
        });
        return enteredValue[0];
    }

    private void hitVerifyLoginService(final String mobileNUmber, final String otp, final String userId, final boolean isFromEmail, String otpValue, TextView otp_error_text, BottomSheetDialog dialog) {
        final VerifyLoginInput input = new VerifyLoginInput();
        input.setId(userId);
        input.setOtp(otpValue);
        input.setFcm_token(PreferenceHandler.readString(this, PreferenceHandler.FCM_TOKEN, ""));
        Log.e("fcmToken: ", PreferenceHandler.readString(this, PreferenceHandler.FCM_TOKEN, ""));
        input.setDevice_type("Android");
        mProgressDialog = new ProgressView(WelcomeActivityNew.this);
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

                            PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.TOKEN, response.body().getResult().getToken());/*"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiNzY5YjI1MDNjYTRmNjViMDliNDRlZWUxNGM5YWVkNTBmNTc2ZmQ1MDUyZGE2ZTg2NWU4OWFlMTAyODkwOTlkNGE3YmE4ZGU0OTcxNjI3NWEiLCJpYXQiOjE1OTQ2MzUxMzAsIm5iZiI6MTU5NDYzNTEzMCwiZXhwIjoxNjI2MTcxMTMwLCJzdWIiOiIzNiIsInNjb3BlcyI6W119.SgPq2hrWTNgpJ64F3_RnW6Xv3wkR59Upkpc4kxKFh2xbjAOUNsvUEVqm9noV-EiCrOjNeDiQMj_xmyLZQuW9fSeMT7AyZqqCd6hSi0okntpaVTpNI44alhmqfWwqXlR4FUNWSD_QrqFvFDOsbVWAEM1p5Q7Jco6hBzq4USQ9prT9zUFELe9HL2ZY4uRz0dwOtUZgur5QB7K4xLZEL5__4-IGMcxqL5P3-lX1h7TShzXIJlJ4JlgVfAK4Lx45EwBdFvE7fk-hgzXqbYJo0lCs5IClQy6xrDs_Dy1yZSWR8k-gUGEyZO2DzvuMz43wjnZLZJOuP9wDr9jotViRRvl68go1LfcTzhYr6hX5VzaJh_4ZelUV7eLkeqAdwaFvZWF7qgJYSCEeq90iuQdpWPa1agxx59O44CsUpSPLyAR0wNaPWvELz-6T1GDJC3JBxZTtiKtunfxdEbyRZtgBqIrMqz1or-blwY3j3Onsn42--2qml5skgqmfeVM3fSbioR3VYDw_6-oYk-QR6SyTVpvZK7BwHXmPvBKKJbXHvCnzP-00oiiiEdR--T7wCDAI-UfXsomWCuv6BKMM2Jf4gk39tnxfNVQ1trGxo4j_MSVYF_PZYjXmfGZm2Hva_q0t73dlFa7hF34fLHiXVwoNHZkKHT1q9jQD-NKWv2MKfi6mzk4"*/
                            PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.DEVICE_ID, response.body().getResult().getUserDetail().getDevice_id());
                            PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.USER_TYPE, response.body().getResult().getType());
                            PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.USERID, response.body().getResult().getUserDetail().getId());
                            PreferenceHandler.writeBoolean(WelcomeActivityNew.this, PreferenceHandler.ISSTATICLOGIN, false);
                            Log.d(TAG, "onResponse: " + response.body().getResult().getUserDetail().getId());
                            PreferenceHandler.writeBoolean(WelcomeActivityNew.this, PreferenceHandler.CREATED_BATCH, response.body().getResult().getBatch() != null && response.body().getResult().getBatch().equalsIgnoreCase("true"));

                            try {
                                Date c = Calendar.getInstance().getTime();

                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                String formattedDate = df.format(c);
                                Log.d(TAG, "onResponse: " + formattedDate);
                                PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.LAST_LOGIN_DATE, formattedDate);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                            if (response.body().getResult().getType().equalsIgnoreCase("organisation") ||
                                    response.body().getResult().getType().equalsIgnoreCase("faculty")) {
                                PreferenceHandler.writeBoolean(WelcomeActivityNew.this, PreferenceHandler.ADMIN_LOGGED_IN, true);
                                Intent intent = new Intent(WelcomeActivityNew.this, AdminMainScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                PreferenceHandler.writeBoolean(WelcomeActivityNew.this, PreferenceHandler.LOGGED_IN, true);
                                Intent intent = new Intent(WelcomeActivityNew.this, MainActivity.class);
                                PreferenceHandler.writeBoolean(WelcomeActivityNew.this, PreferenceHandler.IS_EMAIL_LOGIN, isFromEmail);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                            PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.LOGGED_IN_USERNAME, response.body().getResult().getUserDetail().getName());
                            PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.USER_MOBILE, mobileNUmber);
//                            PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ZOOM_SDK_KEY, response.body().getResult().getZoom_credentials().getZoomSdkKey());
//                            PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ZOOM_SDK_SECRET, response.body().getResult().getZoom_credentials().getZoomSdkSecret());
//                            PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ZOOM_USER_ID, response.body().getResult().getZoom_credentials().getZoomUserId());
//                            PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ZOOM_APP_KEY, response.body().getResult().getZoom_credentials().getZoomAppKey());
//                            PreferenceHandler.writeString(WelcomeActivityNew.this, PreferenceHandler.ZOOM_APP_SECRET, response.body().getResult().getZoom_credentials().getZoomAppSecret());
                            finish();
                        }
                    } else {
                        /*otp_error_text.setVisibility(View.VISIBLE);
                        otp_error_text.setText("Invalid OTP");*/
                        Toast.makeText(WelcomeActivityNew.this, getString(R.string.invalid_otp_entered), Toast.LENGTH_SHORT).show();
                        //  DrawableCompat.setTint(edtOtp.getBackground(), ContextCompat.getColor(OTPActivity.this, R.color.light_red));
                    }

                }

            }

            @Override
            public void onFailure(retrofit2.Call<VerifyLoginResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.e("Retrofit Failure", t.getMessage());
                Utilities.makeToast(WelcomeActivityNew.this, getString(R.string.server_error));
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
                            Utilities.makeToast(WelcomeActivityNew.this, getString(R.string.otp_has_sent));
//                            Snackbar.make(WelcomeActivityNew.this, "OTP has been sent!", Snackbar.LENGTH_SHORT).show();
                            otp_resend_text.setTextColor(Color.parseColor("#307CE9"));             //blue color to enable the click
                            otp_resend_text.setClickable(true);
                            clickCount++;
                            startTimer(60000, otp_resend_text);
                            otp_resend_text.setEnabled(false);
                            // PreferenceHandler.writeString(OTPActivity.this, PreferenceHandler.OTP_CLICK_COUNT, clickCount + "");
                        }
                    } else {
                        Utilities.makeToast(WelcomeActivityNew.this, getString(R.string.server_error));
//                        Snackbar.make(layoutOTP, "Server error", Snackbar.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(retrofit2.Call<LoginSignupResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.e("Retrofit Failure", t.getMessage());
                Utilities.makeToast(WelcomeActivityNew.this, getString(R.string.server_error));
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

    public void deviceDetail() {
        String  details =  "VERSION.RELEASE : "+Build.VERSION.RELEASE
                +"\nVERSION.INCREMENTAL : "+Build.VERSION.INCREMENTAL
                +"\nVERSION.SDK.NUMBER : "+Build.VERSION.SDK_INT
                +"\nBOARD : "+Build.BOARD
                +"\nBOOTLOADER : "+Build.BOOTLOADER
                +"\nBRAND : "+Build.BRAND
                +"\nCPU_ABI : "+Build.CPU_ABI
                +"\nCPU_ABI2 : "+Build.CPU_ABI2
                +"\nDISPLAY : "+Build.DISPLAY
                +"\nFINGERPRINT : "+Build.FINGERPRINT
                +"\nHARDWARE : "+Build.HARDWARE
                +"\nHOST : "+Build.HOST
                +"\nID : "+Build.ID
                +"\nMANUFACTURER : "+Build.MANUFACTURER
                +"\nMODEL : "+Build.MODEL
                +"\nPRODUCT : "+Build.PRODUCT
                +"\nSERIAL : "+Build.SERIAL
                +"\nTAGS : "+Build.TAGS
                +"\nTIME : "+Build.TIME
                +"\nTYPE : "+Build.TYPE
                +"\nUNKNOWN : "+Build.UNKNOWN
                +"\nUSER : "+Build.USER;

        Log.d("Device Details",details);
    }
}