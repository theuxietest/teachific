package com.so.luotk.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.so.luotk.R;
import com.so.luotk.client.MyClient;
import com.so.luotk.models.output.PreCoursePaymentResult;
import com.so.luotk.models.output.ServiceResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {
    private String mobileNumber, courseAmount, orgLogo, orgName, courseId, successUrl, live_key, test_key, user_email, courseName;
    private static final String TAG = PaymentActivity.class.getSimpleName();
    private Integer isLive;
    private PreCoursePaymentResult paymentResult;
    private Double totalAmount;
    private boolean isSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
//getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_payment);
        Checkout.preload(getApplicationContext());
        if (getIntent() != null) {
            paymentResult = (PreCoursePaymentResult) getIntent().getSerializableExtra("paymentData");
            courseName = getIntent().getStringExtra("courseName");
        }
        orgLogo = PreferenceHandler.readString(this, PreferenceHandler.ORG_LOGO, "");
        orgName = PreferenceHandler.readString(this, PreferenceHandler.ORG_NAME, "");
        mobileNumber = PreferenceHandler.readString(this, PreferenceHandler.USER_MOBILE, "");
        setUpData();
    }

    private void setUpData() {
        if (paymentResult != null) {
            courseId = String.valueOf(paymentResult.getCourseId());
            user_email = paymentResult.getUserEmail();
            successUrl = paymentResult.getResponseSuccessURL();
            isLive = paymentResult.getIsLive();
            test_key = paymentResult.getTestSecret();
            live_key = paymentResult.getLiveSecret();
            courseAmount = paymentResult.getAmount();
            startPayment();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Log.e(TAG, "PaymentSuccess" + razorpayPaymentID);
        try {
            if (razorpayPaymentID != null)
                if (Utilities.checkInternet(this)) {
                    if (successUrl.contains("course/payment/success"))
                        hitPaymentSuccessService(razorpayPaymentID);
                } else Utilities.makeToast(this, getString(R.string.internet_connection_error));
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            Log.e(TAG, "Exception in onPaymentError" + response);
            setResult();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }


    private void hitPaymentSuccessService(String transaction_id) {
        Map<String, Object> map = new HashMap<>();
        map.put("courseId", courseId);
        map.put("transaction_id", transaction_id);
        map.put("status", "success");
        map.put("amount", courseAmount);

        new MyClient(this).getCoursePaymentSuccess(map, (content, error) -> {
            if (content != null) {
                ServiceResponse response = (ServiceResponse) content;
                if (response.getStatus() != null && response.getStatus() == 200) {
                    if (response.getResult() != null) {
                        isSuccess = true;
                        Log.e(TAG, "isSuccess" + isSuccess);

                    }


                } else if (response.getStatus() != null && response.getStatus() == 403)
                    Utilities.openUnauthorizedDialog(this);
                else Utilities.makeToast(this, getString(R.string.server_error));
            } else Utilities.makeToast(this, getString(R.string.server_error));
            setResult();
        });
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop: " + isSuccess);
        super.onStop();
    }

    public void setResult() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("isSuccess", isSuccess);
        setResult(1, resultIntent);
        finish();
    }

    public void startPayment() {

        //   You need to pass current activity in order to let Razorpay create CheckoutActivity
        final Activity activity = this;
        final Checkout co = new Checkout();
        co.setImage(R.mipmap.ic_launcher);
        // co.setKeyID(test_key);
        if (isLive == 1)
            co.setKeyID(live_key);
        else co.setKeyID(test_key);

        try {
            JSONObject options = new JSONObject();
            if (!TextUtils.isEmpty(orgName))
                options.put("name", orgName);
            if (!TextUtils.isEmpty(orgLogo))
                options.put("image", orgLogo);
            if (!TextUtils.isEmpty(courseName))
                options.put("description", courseName);
            options.put("currency", "INR");
            if (!TextUtils.isEmpty(courseAmount)) {
                totalAmount = Double.parseDouble(courseAmount) * 100;
                options.put("amount", String.valueOf(totalAmount));
            }
            JSONObject preFill = new JSONObject();
            preFill.put("email", user_email);
            if (!TextUtils.isEmpty(mobileNumber)) {
                preFill.put("contact", mobileNumber);
            }
            JSONObject readOnly = new JSONObject();
            readOnly.put("email", false);
            readOnly.put("contact", false);
            options.put("readonly", readOnly);
            options.put("prefill", preFill);
            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
