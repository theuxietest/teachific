package com.so.luotk.utils;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.so.luotk.BuildConfig;
import com.so.luotk.R;
import com.so.luotk.activities.AdminMainActivity;
import com.so.luotk.activities.CourseDetailAcrivity;
import com.so.luotk.activities.PaymentActivity;
import com.so.luotk.activities.SplashActivity;
import com.so.luotk.activities.WelcomeActivityNew;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.DialogContentLockedBinding;
import com.so.luotk.fragments.more.SettingFragment;
import com.so.luotk.models.output.PreCoursePaymentResponse;
import com.so.luotk.models.output.PreCoursePaymentResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;


public class Utilities {

    private static AlertDialog.Builder builder;
    private static final String[] PERMISSION_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES};
    private static boolean isRetryButtonCliked;
    private static final ProgressView mProgressDialog = null;

    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }

    public static void restrictScreenShot(Context context) {
        try {
            ((Activity)context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void restrictKeepScreenOn(Context context) {
        try {
            ((Activity)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void setLocale(Context context) {
        String locale = PreferenceHandler.readString(context, PreferenceHandler.LOCALE, "en");
        PreferenceHandler.writeString(context, PreferenceHandler.LOCALE, locale);
        Locale myLocale = new Locale(locale);
        Locale.setDefault(myLocale);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        context.createConfigurationContext(conf);
        res.updateConfiguration(conf, dm);
    }

    public static void openContentLockedDialog(Context context, String sellingPrice, String courseId, String fromWhere) {
        String courseName = "";
        Activity mActivity = (Activity) context;
        Dialog dialog = new Dialog(context);
        DialogContentLockedBinding binding = DialogContentLockedBinding.inflate(LayoutInflater.from(context));
        String locale = PreferenceHandler.readString(context, PreferenceHandler.LOCALE, "en");
        dialog.setContentView(binding.getRoot());
        switch (locale) {
            case "en":
                binding.buyNowFor.setText(String.format(context.getString(R.string.buy_now_for) + "₹%s/-", sellingPrice));
                break;
            case "mr":
                binding.buyNowFor.setText(String.format(context.getString(R.string.buy_now_for) + "₹%s/-", sellingPrice));
                break;
            case "kn":
                binding.buyNowFor.setText(String.format(context.getString(R.string.buy_now_for) + "₹%s/-", sellingPrice));
                break;
            case "hi":
                binding.buyNowFor.setText(String.format("₹%s/-%s", sellingPrice, context.getString(R.string.buy_now_for)));
                break;
            case "ar":
                binding.buyNowFor.setText(String.format("₹%s/-%s", sellingPrice, context.getString(R.string.buy_now_for)));
                break;
            case "pa":
                binding.buyNowFor.setText(String.format("₹%s/-%s", sellingPrice, context.getString(R.string.buy_now_for)));
                break;
            case "or":
                binding.buyNowFor.setText(String.format("₹%s/-%s", sellingPrice, context.getString(R.string.buy_now_for)));
                break;
        }
        /*        binding.buyNowFor.setText(String.format("Buy now for ₹%s/-", sellingPrice));*/
        if (fromWhere.equals("test")) {
            courseName = CourseDetailAcrivity.courseNameNew;
        } else if (fromWhere.equals("video")) {
            courseName = CourseDetailAcrivity.courseNameNew;
        } else if (fromWhere.equals("study")) {
            courseName = CourseDetailAcrivity.courseNameNew;
        }
        String finalCourseName = courseName;
        binding.buyNowFor.setOnClickListener(v -> {
            hitGetCoursePaymentDataService(context, sellingPrice, courseId, finalCourseName);
            /*Intent intent = new Intent(context, CourseDetailAcrivity.class);
            intent.putExtra(PreferenceHandler.COURSE_ID, courseId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("notify", true);
            intent.putExtra("isFromMyCourses", false);
            intent.putExtra(PreferenceHandler.SELLING_PRICE, sellingPrice);
            intent.putExtra(PreferenceHandler.NOTIFICATION_TYPE, "buynow");
            context.startActivity(intent);*/
        });
        binding.goBack.setOnClickListener(v -> {
            if (!mActivity.isFinishing())
                dialog.dismiss();
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (!mActivity.isFinishing()) {
            dialog.show();
        }
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation_2);
        dialog.setCancelable(false);


    }

    public static void openContentLockedDialogNew(Context context, String sellingPrice, String courseId, String fromWhere) {
        String courseName = "";
        Activity mActivity = (Activity) context;
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.DialogStyle);
        View sheetView = mActivity.getLayoutInflater().inflate(R.layout.dialog_content_locked, null);
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
        String locale = PreferenceHandler.readString(context, PreferenceHandler.LOCALE, "en");
        TextView buyNowFor = sheetView.findViewById(R.id.buy_now_for);
        TextView go_back = sheetView.findViewById(R.id.go_back);
        switch (locale) {
            case "en":
                buyNowFor.setText(String.format(context.getString(R.string.buy_now_for) + "₹%s/-", sellingPrice));
                break;
            case "mr":
            case "kn":
            case "hi":
                buyNowFor.setText(String.format("₹%s/-%s", sellingPrice, context.getString(R.string.buy_now_for)));
                break;
            case "or":
                buyNowFor.setText(String.format("₹%s/-%s", sellingPrice, context.getString(R.string.buy_now_for)));
                break;
        }

        if (fromWhere.equals("test")) {
            courseName = CourseDetailAcrivity.courseNameNew;
        } else if (fromWhere.equals("video")) {
            courseName = CourseDetailAcrivity.courseNameNew;
        } else if (fromWhere.equals("study")) {
            courseName = CourseDetailAcrivity.courseNameNew;
        }
        String finalCourseName = courseName;
        buyNowFor.setOnClickListener(v -> {
            hitGetCoursePaymentDataService(context, sellingPrice, courseId, finalCourseName);
        });
        go_back.setOnClickListener(v -> {
            if (!mActivity.isFinishing())
                dialog.dismiss();
        });
        dialog.show();

    }

    public static void hitGetCoursePaymentDataService(Context context, String sellingPrice, String courseId, String courseName) {
        long mLastClickTime = 0;
        ProgressView progressView;
        progressView = new ProgressView(context);
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        //openSuccessDialog();
        Map<String, Object> map = new HashMap<>();
        map.put("courseId", courseId);
        new MyClient(context).getCoursePaymentData(map, (content, error) -> {
            progressView.show();
            if (content != null) {
                progressView.dismiss();
                PreCoursePaymentResponse response = (PreCoursePaymentResponse) content;
                if (response.getStatus() != null && response.getStatus() == 200) {
                    if (response.getResult() != null && response.getSuccess()) {
                        PreCoursePaymentResult result = response.getResult();
                        ((Activity) context).startActivityForResult(new Intent(context, PaymentActivity.class).
                                putExtra("paymentData", (Serializable) result).putExtra("courseName", courseName), 1);
                    }
                } else if (response.getStatus() != null && response.getStatus() == 403)
                    Utilities.openUnauthorizedDialog(context);
                else {
                    if (progressView != null && progressView.isShowing())
                        progressView.dismiss();
                    Utilities.makeToast(context, ((Activity) context).getString(R.string.server_error));
                }
            } else {
                if (progressView != null && progressView.isShowing())
                    progressView.dismiss();
                Utilities.makeToast(context, ((Activity) context).getString(R.string.server_error));
            }
        });


    }

    public synchronized static void hideKeyBoard(Context context) {
        Activity activity = (Activity) context;
        View view = activity.getCurrentFocus();
        if (view != null) {
            try {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } catch (Exception e) {
                // do nothing
                e.printStackTrace();
            }
        }
    }

    public static void formatDate(TextView view, String date, int flag) {
        String formattedDate = "";
        SimpleDateFormat input = null;
        SimpleDateFormat outputDate = null;
        switch (flag) {
            case 0:
                input = new SimpleDateFormat("yyyy-MM-dd");
                outputDate = new SimpleDateFormat("dd MMM, yyyy");
                break;
            case 1:
                input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                outputDate = new SimpleDateFormat("dd-MMM");
                break;

            case 2:
                input = new SimpleDateFormat("yyyy-MM-dd");
                outputDate = new SimpleDateFormat("MMM dd,yyyy");
                break;
            case 3:
                input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                input.setTimeZone(TimeZone.getTimeZone("UTC"));
                outputDate = new SimpleDateFormat("dd-MMM , h:mm a");
                break;
        }
        Date d = null;
        try {
            d = input.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formattedDate = outputDate.format(d);
        view.setText(formattedDate);
    }

    public static String formatCurrentTime(String date, int flag) {
        String formattedDate = "";
        SimpleDateFormat input = null;
        SimpleDateFormat outputDate = null;
        switch (flag) {
            case 0:
                input = new SimpleDateFormat("yyyy-MM-dd");
                outputDate = new SimpleDateFormat("dd MMM, yyyy");
                break;
            case 1:
                input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                outputDate = new SimpleDateFormat("dd-MMM");
                break;

            case 2:
                input = new SimpleDateFormat("yyyy-MM-dd");
                outputDate = new SimpleDateFormat("MMM dd,yyyy");
                break;
            case 3:
                input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                outputDate = new SimpleDateFormat("MMM dd,yyyy, hh:mm a");
                break;
            case 4:
                input = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                outputDate = new SimpleDateFormat("MMM dd,yyyy, hh:mm a");
                break;
        }
        Date d = null;
        try {
            d = input.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formattedDate = outputDate.format(d);


        return formattedDate;
    }

    public static String formatCurrentDate(Date date) {
        String formattedDate = "";
        SimpleDateFormat outputDate = null;
        outputDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formattedDate = outputDate.format(date);
        return formattedDate;
    }

    public static void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void openTimePicker(Context context, TextView textView) {
        String selected_time = "";
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

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
                textView.setText(String.format("%02d:%02d ", mHour, minute) + AM_PM);

            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public static void openTimePicker24(Context context, TextView textView) {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                Log.d("TAG", "onTimeSetBefore: " + hourOfDay + " : " + minute);
                String hourSt = "";
                String minutesSt = "";
                int mHour = hourOfDay;
                int mMinutes = minute;
                if (hourOfDay < 10) {
                    hourSt = "0" + mHour;
                } else {
                    hourSt = "" + mHour;
                }

                if (mMinutes < 10) {
                    minutesSt = "0" + mMinutes;
                } else {
                    minutesSt = "" + mMinutes;
                }

                String selected_time = hourSt + ":" + minutesSt;
                Log.d("TAG", "onTimeSet: " + selected_time + " : " + minute);
                textView.setText(selected_time);

            }
        }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    public static void openTimePickerAll(Context context, TextView textView) {
        String selected_time = "";
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

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
                textView.setText(String.format("%02d:%02d ", mHour, minute));

            }
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    /**
     * @return Returns true if there is network connectivity
     */
    public static Boolean checkWIFI(Context activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else if (netInfo != null && (netInfo.getState() == NetworkInfo.State.DISCONNECTED || netInfo.getState() == NetworkInfo.State.DISCONNECTING || netInfo.getState() == NetworkInfo.State.SUSPENDED || netInfo.getState() == NetworkInfo.State.UNKNOWN)) {
            return false;
        } else {
            return false;
        }
    }

    public static void shareApp(Context context) {
        String shareMessage = PreferenceHandler.readString(context, PreferenceHandler.SHARE_MESSAGE, "");
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            // String shareMessage = "Smart Owls has its own app now.\nKeep learning, keep growing!!\nDownload now:\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void checkInActiveOrgDialog(final Context context) {
        final boolean isAdmin;
        isAdmin = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
        final Activity activity = (Activity) context;
        Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_unauthorized_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        if (!activity.isFinishing())
            mDialog.show();
        mDialog.setCancelable(false);
        mDialog.getWindow().setAttributes(lp);
        TextView btnOk = mDialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin) {
                    PreferenceHandler.writeBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                } else {
                    PreferenceHandler.writeBoolean(context, PreferenceHandler.LOGGED_IN, false);
                }
                if (!activity.isFinishing())
                    mDialog.dismiss();
                Intent intent = new Intent(context, WelcomeActivityNew.class);
                intent.putExtra("isFromLogout", true);
                context.startActivity(intent);
                final Activity activity = (Activity) context;
                activity.finish();
            }
        });


    }

    public static void orgInActive(final Context context) {
        Utilities.hideKeyBoard(context);
        final Activity activity = (Activity) context;
        Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_inactive_orgnization);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        if (!activity.isFinishing())
            mDialog.show();
        mDialog.setCancelable(false);
        mDialog.getWindow().setAttributes(lp);
        Button btnOk = mDialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });


    }
    public static void setUpStatusBar(Activity activity) {
        int currentNightMode = activity.getApplicationContext().getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                activity.getWindow().getDecorView().setSystemUiVisibility(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS |
                        SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                break;
        }

    }

    /**
     * Check for Camera permission
     */
    public static boolean isCameraPermitted(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request Camera permission
     */
    public static void requestCameraPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, requestCode);
    }


    /**
     * verify state of permissions
     */
    public static boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1)
            return false;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    /**
     * is storage read/write permissions required
     */
    /*public static boolean isStoragePermissionRequired(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, PERMISSION_STORAGE[0]) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, PERMISSION_STORAGE[1]) != PackageManager.PERMISSION_GRANTED;
    }*/
    public static boolean isStoragePermissionRequired(Activity activity) {

        Log.d("TAG", "isStoragePermissionRequired: ");
        return ActivityCompat.checkSelfPermission(activity, PERMISSION_STORAGE[0]) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, PERMISSION_STORAGE[1]) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, PERMISSION_STORAGE[2]) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request storage permission
     */
    public static void requestStoragePermission(Activity activity, int requestCode) {
        Log.d("TAG", "requestStoragePermission: ");
        ActivityCompat.requestPermissions(activity,
                PERMISSION_STORAGE,
                requestCode);
//        ActivityCompat.requestPermissions(activity, PERMISSION_STORAGE, requestCode);
    }

    private static boolean hasPermissionsNotification(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * formats Date in dd-MMM-yyyy format
     */
    public static String getDateString() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy hh:mm aa", Locale.getDefault());
        return format.format(date);
    }


    public static Boolean checkInternet(Context activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else if (netInfo != null && (netInfo.getState() == NetworkInfo.State.DISCONNECTED || netInfo.getState() == NetworkInfo.State.DISCONNECTING || netInfo.getState() == NetworkInfo.State.SUSPENDED || netInfo.getState() == NetworkInfo.State.UNKNOWN)) {
            return false;
        } else {
            return false;
        }
    }


    /**
     * get array of specific number of items
     */
    public static final ArrayList<String> getFinancialYearArray() {
        ArrayList<String> years = new ArrayList<>();
        years.add(0, "Please select financial year");
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        int thisMonth = Calendar.getInstance().get(Calendar.MONTH);
        if ((thisMonth + 1) <= (Calendar.MARCH + 1)) {
            for (int i = 2016; i <= thisYear; i++) {
                years.add(Integer.toString(i));

            }
        } else {
            for (int i = 2015; i <= thisYear; i++) {
                years.add(Integer.toString(i + 1));

            }
        }
        return years;
    }


    /**
     * return if new date is after start date
     */
    public static boolean isAfterDate(String oldDate, String newDate) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date olddate = format.parse(oldDate);
            Date newdate = format.parse(newDate);
            return newdate.equals(olddate) || newdate.after(olddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkIfSelectedDateIsToday(String date) {
        boolean isTrue = false;
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        try {
            if (timeFormat.parse(date).equals(timeFormat.parse(timeStamp)))
                isTrue = false;
            else if (timeFormat.parse(date).before(timeFormat.parse(timeStamp))) {
                isTrue = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isTrue;
    }

    public static boolean checkIfSelectedDateTimeIsToday(String date) {
        boolean isTrue = false;
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
        try {
            if (timeFormat.parse(date).before(new Date()))
                isTrue = true;
            else if (timeFormat.parse(date).equals(new Date()))
                isTrue = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isTrue;
    }

    public static String getMainStorageFolderPath() {

        String dirPath;

        dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/"
                + "AuditApp" + "/";
        File directory = new File(dirPath);

        if (!directory.exists())
            directory.mkdirs();

        return dirPath;
    }

    public static String getUniqueDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getUniqueDeviceId(Context context, String number) {
        if (SplashActivity.StaticNumner.equals(number)) {
            return Config.StaticDeviceId;
        } else {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
    }
 /*   public static String getDeviceToken() {
        android.util.Log.d("Firebase", "token " + FirebaseInstanceId.getInstance().getToken());
        return FirebaseInstanceId.getInstance().getToken();
    }*/

    /*
        public static void setFragmentManager(Fragment frag, String tag, FragmentManager fm) {
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.replace(R.id.fragments_filter_dashboard, frag);
            fragmentTransaction.commitAllowingStateLoss();
        }
    */
    public static void openDatePickerForTextView(final Context context, final TextView textView, boolean setMaxDate) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        //strDate = dateFormatter.format(d);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        //  Toast.makeText(getActivity(), dayOfMonth + "-" + (monthOfYear + 1) + "-" + year, Toast.LENGTH_SHORT).show();
                        String strDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        // String formattedDate = dateFormatter.format(strDate);
                        if (setMaxDate) {
                            textView.setText("Selected Date: " + strDate);
                        } else {
                            textView.setText(strDate);
                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        //  datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        if (setMaxDate) {
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        }

    }

    public static void openDatePickerSlashTextView(final Context context, final TextView textView, boolean setMaxDate) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        //strDate = dateFormatter.format(d);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        //  Toast.makeText(getActivity(), dayOfMonth + "-" + (monthOfYear + 1) + "-" + year, Toast.LENGTH_SHORT).show();
                        String month_name = "", day_name;
                        Log.d("TAG", "onDateSet: " + monthOfYear);
                        if ((monthOfYear + 1) < 10) {
                            month_name = "0" + (monthOfYear + 1);
                        } else {
                            month_name = String.valueOf(monthOfYear + 1);
                        }

                        if ((dayOfMonth) < 10) {
                            day_name = "0" + (dayOfMonth);
                        } else {
                            day_name = String.valueOf(dayOfMonth);
                        }

                        String strDate = day_name + "/" + month_name + "/" + year;
                        // String formattedDate = dateFormatter.format(strDate);
                        if (setMaxDate) {
                            textView.setText("Selected Date: " + strDate);
                        } else {
                            textView.setText(strDate);
                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        //  datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        if (setMaxDate) {
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        }

    }


    public static Drawable setNavigationIconColor(Context context) {
        final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.gray), PorterDuff.Mode.SRC_ATOP));
        return upArrow;
    }

    public static Drawable setNavigationIconColorBlack(Context context) {
        final Drawable upArrow = ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP));
        return upArrow;
    }

    public static void openDatePickerDialog(final Context context, final EditText editText, final ImageView imageView) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        //Toast.makeText(context, dayOfMonth + "-" + (monthOfYear + 1) + "-" + year, Toast.LENGTH_SHORT).show();
                        editText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        imageView.setImageResource(R.drawable.date_icon);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

        // picker.setMaxDate(new Date().getTime());
    }

    public static void setDateDefaultIcon(final Context context, final ImageView imageView1, final ImageView imageView2) {
        imageView1.setImageResource(R.drawable.date_icon_light);
        imageView2.setImageResource(R.drawable.date_icon_light);
    }

    public static void openKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        editText.requestFocus();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /* *//*method for open the dialog to choose image*//*
    public static void openCameraChooseDialog(final ManageUplaodImagesActivity.OnImageChoosen onImageChoosen) {
        builder.setTitle("Take Picture");
        builder.setMessage("Take a new photo or select one from your existing photo library.");
        builder.setCancelable(true);

        builder.setPositiveButton("CAMERA", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onImageChoosen.cameraChoosed();
            }
        });

        builder.setNegativeButton("GALLERY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onImageChoosen.galleryChoosed();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }*/

    public static void showInternetAlert(Context context) {
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
        final TextView btnRetry = mDialog.findViewById(R.id.btn_retry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRetryButtonCliked = true;
            }
        });
        mDialog.show();

    }

    public static boolean isRetryButtonCliked() {
        return isRetryButtonCliked;

    }

    public static void galleryIntent(Activity activity) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(pickPhoto, 1);
    }


    /**
     * Method to open camera intent
     */
    public static void openCameraIntent(Activity activity) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(cameraIntent, 0);
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyCameraIFmages");
        imagesFolder.mkdirs();
        File image = new File(imagesFolder, "image.jpg");
        Uri uriSavedImage = Uri.fromFile(image);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

    }

    public static void openKeyboard(Activity activity, EditText editText) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        editText.requestFocus();
    }

    /**
     * Check for audio permission
     */
    public static boolean isAudioPermitted(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * request audio permission
     */
    public static void requestAudioPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, requestCode);
    }

    public static String getFormattedDate(String date, boolean isFromAnnoucement) {
        String formattedDate = "";
        SimpleDateFormat input = null;
        SimpleDateFormat outputDate = null;
        if (isFromAnnoucement) {
            input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            outputDate = new SimpleDateFormat("dd-MMM");
        } else {
            input = new SimpleDateFormat("yyyy-MM-dd");
            outputDate = new SimpleDateFormat("MMM dd,yyyy");
        }

        Date d = null;
        try {
            d = input.parse(date/*"2018-02-02T06:54:57.744Z"*/);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formattedDate = outputDate.format(d);
        return formattedDate;
    }

    public static String getOnlyDate(String fulldate) {
        String onlyDate = "";
        SimpleDateFormat input = null;
        SimpleDateFormat outputDate = null;
        input = new SimpleDateFormat("yyyy-MM-dd");
        outputDate = new SimpleDateFormat("dd");

        Date d = null;
        try {
            d = input.parse(fulldate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        onlyDate = outputDate.format(d);
        return onlyDate;
    }

    public static String getOnlyMonth(String fulldate) {
        String onlyMonth = "";
        SimpleDateFormat input = null;
        SimpleDateFormat outputDate = null;
        input = new SimpleDateFormat("yyyy-MM-dd");
        outputDate = new SimpleDateFormat("MMM");
        Date d = null;
        try {
            d = input.parse(fulldate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        onlyMonth = outputDate.format(d);
        return onlyMonth;
    }

    public static String getFormattedTime(String date) {
        String formattedTime = "";
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        input.setTimeZone(tz); // strip timezone
        SimpleDateFormat outputTime = new SimpleDateFormat("h:mm a", Locale.US);

        Date d = null;
        try {
            d = input.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formattedTime = outputTime.format(d);
        return formattedTime;
    }

    /**
     * Show logout alert to user
     */
    public static void showLogoutAlert(final Context context, String appName, final boolean isAdmin) {
        builder = new AlertDialog.Builder(context);
        builder.setTitle(appName);
        builder.setMessage("Are you sure you want to logout?");
        builder.setCancelable(false);
        builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isAdmin) {
                    AdminMainActivity.getInstance().hitLogoutService();
                } else {
                    SettingFragment.getInstance().hitLogoutService();
                }
               /* mProgressDialog = new ProgressView(context);
                mProgressDialog.show();
                setLogoutProgressBarTimer(context, isAdmin);*/
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        Button pButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pButton.setTextColor(ContextCompat.getColor(context, R.color.blue_main));
        Button nButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nButton.setTextColor(ContextCompat.getColor(context, R.color.blue_main));
    }

    public static void setLogoutProgressBarTimer(final Context context, final boolean isAdmin) {
        final Activity activity = (Activity) context;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        mProgressDialog.dismiss();
                        if (isAdmin) {
                            PreferenceHandler.writeBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                        } else {
                            PreferenceHandler.writeBoolean(context, PreferenceHandler.LOGGED_IN, false);
                        }
                        Intent intent = new Intent(context, WelcomeActivityNew.class);
                        intent.putExtra("isFromLogout", true);
                        context.startActivity(intent);
                        activity.finish();

                    }
                }, 2000);
    }

    public static void openUnauthorizedDialog(final Context context) {
        final boolean isAdmin;
        isAdmin = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
        final Activity activity = (Activity) context;
        Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_unauthorized_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        if (!activity.isFinishing())
            mDialog.show();
        mDialog.setCancelable(false);
        mDialog.getWindow().setAttributes(lp);
        TextView btnOk = mDialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin) {
                    PreferenceHandler.writeBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                } else {
                    PreferenceHandler.writeBoolean(context, PreferenceHandler.LOGGED_IN, false);
                }
                if (!activity.isFinishing())
                    mDialog.dismiss();
                Intent intent = new Intent(context, WelcomeActivityNew.class);
                intent.putExtra("isFromLogout", true);
                context.startActivity(intent);
                final Activity activity = (Activity) context;
                activity.finish();
            }
        });


    }

/*    public static void openUnauthorizedDialog(final Context context) {
        final boolean isAdmin;
        if (PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false)) {
            isAdmin = true;
        } else {
            isAdmin = false;
        }
        Dialog mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_unauthorized_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDialog.show();
        mDialog.setCancelable(false);
        mDialog.getWindow().setAttributes(lp);
        TextView btnOk = mDialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = new ProgressView(context);
                mProgressDialog.show();
                setLogoutProgressBarTimer(context, isAdmin);
                mDialog.dismiss();

            }
        });


    }*/

    public static int getUniqueCode() {
        Random r = new Random(System.currentTimeMillis());
        return 10000 + r.nextInt(20000);
    }


    public static File resaveBitmap(String path, String filename) {
        String extStorageDirectory = path;
        OutputStream outStream = null;
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, filename);
        }
        try {
            // make a new bitmap from your file
            Bitmap bitmap = BitmapFactory.decodeFile(path + filename);
            bitmap = rotateBitmap(bitmap, path);
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) ((float) bitmap.getWidth() * 0.3f), (int) ((float) bitmap.getHeight() * 0.3f), false);
            // bitmap = Utils.getCircleImage(bitmap);
            outStream = new FileOutputStream(path + filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    public static Bitmap rotateBitmap(Bitmap bitmap, String photoPath) throws IOException {
        ExifInterface ei = new ExifInterface(photoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatAttendanceDate(String date) {
        String formattedDate = "";
        SimpleDateFormat input = null;
        SimpleDateFormat outputDate = null;
        input = new SimpleDateFormat("yyyy-MM-dd");
        outputDate = new SimpleDateFormat("dd MMM, yyyy");
        Date d = null;
        try {
            d = input.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formattedDate = outputDate.format(d);
        return formattedDate;
    }

    public static void openAttachedFile(Context context, File url) throws IOException {
        // Create URI
        File file = url;
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getPath()));
        intent.setDataAndType(uri, mimeType);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe) {
            // startActivityForResult(intent,101);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Your device does not support this file", Toast.LENGTH_SHORT).show();
        }

    }

    public static void openFile(Context context, File url) throws IOException {
        // Create URI
        File file = url;
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getPath()));
        intent.setDataAndType(uri, mimeType);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe) {
            // startActivityForResult(intent,101);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Your device does not support this file", Toast.LENGTH_SHORT).show();
        }

    }


  /*  public static boolean isBloodGroupValid(String bloodGroup, Context context){
        if(bloodGroup != null) {
            String[] bloodGroups = context.getResources().getStringArray(R.array.blood_groups);
            int len = bloodGroups.length;
            for (int i = 0; i < len; i++) {
                if(bloodGroup.equalsIgnoreCase(bloodGroups[i]))
                    return true;
            }
        }
        return false;
    }*/

    public static String getRootDirPath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = ContextCompat.getExternalFilesDirs(context.getApplicationContext(),
                    null)[0];
            return file.getAbsolutePath();
        } else {
            return context.getApplicationContext().getFilesDir().getAbsolutePath();
        }
    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes){
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }
}
