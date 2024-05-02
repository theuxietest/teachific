package com.so.luotk.fragments.courses;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.so.luotk.R;
import com.so.luotk.activities.CourseDetailAcrivity;
import com.so.luotk.activities.MainActivity;
import com.so.luotk.activities.PaymentActivity;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.CustomTalkToTutorDialogBinding;
import com.so.luotk.databinding.FragmentCourseOverviewBinding;
import com.so.luotk.databinding.PaymentSuccesDialogBinding;
import com.so.luotk.models.newmodels.ServerResponse;
import com.so.luotk.models.newmodels.courseModel.Datum;
import com.so.luotk.models.output.PreCoursePaymentResponse;
import com.so.luotk.models.output.PreCoursePaymentResult;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.so.luotk.viewmodel.CourseOverviewViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.view.View.GONE;

public class CourseOverviewFragment extends Fragment {
    private static final int REQUEST_PHONE_CALL = 100;
    private static final int REQUEST_MESSAGE = 200;
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private FragmentCourseOverviewBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private String courseId, courseName;
    private Datum course;
    private Handler handler;
    private Runnable runnable;
    private boolean isFromMyCourses;
    private String whatsAppNo, callingNo, courseAmount;
    private Context context;
    private PreferenceHandler preferenceHandler;
    private likeChangeListener listener;
    private String url;
    private CourseOverviewViewModel viewModel;
    private ProgressView progressView;
    private boolean toast = true, likeChanged;
    private Activity activity;
    private long mLastClickTime=0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (Activity) context;
    }

    public CourseOverviewFragment() {
        // Required empty public constructor
    }

    public static CourseOverviewFragment newInstance(Datum datum, String courseId, boolean isFromMyCourses, boolean isNoti, String url) {
        CourseOverviewFragment fragment = new CourseOverviewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, datum);
        args.putBoolean(ARG_PARAM3, isFromMyCourses);
        args.putString(ARG_PARAM4, url);
        args.putBoolean(ARG_PARAM2, isNoti);
        args.putString(ARG_PARAM5, courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(getActivity());
//     getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (getArguments() != null) {
            boolean isNoti = getArguments().getBoolean(ARG_PARAM2);
            isFromMyCourses = getArguments().getBoolean(ARG_PARAM3);
            url = getArguments().getString(ARG_PARAM4);
            if (!isNoti) {
                course = (Datum) getArguments().getSerializable(ARG_PARAM1);
                courseId = course.getId() + "";
            } else courseId = getArguments().getString(ARG_PARAM5);
        }
        preferenceHandler = new PreferenceHandler(context);
        if (PreferenceHandler.readBoolean(context, PreferenceHandler.USESTATIC_NUMBER, false)){
            whatsAppNo = PreferenceHandler.readString(context, PreferenceHandler.WHATSAPP_NUMBER, getString(R.string.whatsap_no_more));
            callingNo = PreferenceHandler.readString(context, PreferenceHandler.CALLING_NUMBER, getString(R.string.call_no_more));
        } else {
            whatsAppNo = PreferenceHandler.readString(context, PreferenceHandler.ADMIN_MOBILE_NO, null);
            callingNo = PreferenceHandler.readString(context, PreferenceHandler.ADMIN_MOBILE_NO, null);
        }
        handler = new Handler(Looper.myLooper());
        runnable = this::hitOverView;

    }

    private void hitOverView() {
        viewModel.getView(context, courseId);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCourseOverviewBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(CourseOverviewViewModel.class);
        if (course == null)
            hitOverView();
        progressView = new ProgressView(context);
        if (course != null) {
            binding.setData(course);
            binding.setBase(url);
            binding.setLike(course.getIsLiked() > 0);
            setUpUI(course);
        }
        if (PreferenceHandler.readBoolean(getActivity(), PreferenceHandler.ISSTATICLOGIN, false)) {
            binding.setFlag(true);
        } else {
            binding.enrollDate.setText(getString(R.string.enrollment_date));
            binding.setFlag(isFromMyCourses);
        }

        binding.setFrag(this);
        viewModel.getObservableData().observe(getViewLifecycleOwner(), response -> {
            if (response != null)
                switch (response.status) {
                    case EXTRA:
                        //   preferenceHandler.toast(response.message);
                        break;
                    case LOADING:
                        break;
                    case AUTHENTICATED:
                        handler.removeCallbacks(runnable);
                        binding.setData(response.data.getDatum());
                        course = response.data.getDatum();
                        setUpUI(course);
                        binding.setData(course);
                        courseId = course.getId() + "";
                        binding.setBase(response.data.getExtra().getImageBaseUrl());
                        binding.setLike(course.getIsLiked() > 0);

                        break;
                    case NOT_AUTHENTICATED:
                        progressView.dismiss();
                        Utilities.openUnauthorizedDialog(context);
                        break;
                    case ERROR:
                        if (toast)
                            Utilities.makeToast(context, response.message);
                        handler.postDelayed(runnable, 5000);
                        if (response.message.contains("network"))
                            toast = false;
                        else
                            progressView.dismiss();
                        Utilities.makeToast(context, response.message);
                        break;
                }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setUpUI(Datum data) {
        courseName = data.getCourseName();
        if (data.getDurationType() != null)
            switch (data.getDurationType()) {
                case 1:
                    setDurationValidity(data);
                    break;
                case 2:
                    String expiryDate = formatExpirydate(data.getExpiryDate());
                    binding.setDuration(getString(R.string.valid_till) + " " + expiryDate);
                    break;
                case 3:
                    binding.setDuration(getString(R.string.lifetime));
                    break;
            }

        try {
            if (data.getDiscount() != null) {
                if (Float.parseFloat(data.getDiscount()) > 0) {
                    binding.tvCourseActualPrice.setVisibility(View.VISIBLE);
                } else {
                    binding.tvCourseActualPrice.setVisibility(View.GONE);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
            if (isAdmn) {
                if (CourseDetailAcrivity.shimmerFrameLayout.isShimmerStarted()) {
                    CourseDetailAcrivity.shimmerFrameLayout.stopShimmer();
                    CourseDetailAcrivity.shimmerFrameLayout.setVisibility(GONE);
                }
            } else {
                if (CourseDetailAcrivity.shimmerFrameLayout.isShimmerStarted()) {
                    CourseDetailAcrivity.shimmerFrameLayout.stopShimmer();
                    CourseDetailAcrivity.shimmerFrameLayout.setVisibility(GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void hitCourseLikeService(View v) {

        new MyClient(context).courseLikeService(courseId, (content, error) -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (content != null) {
                ServerResponse response = (ServerResponse) content;
                if (response.getStatus() != null && response.getStatus() == 200) {
                    if (response.getSuccess() && response.getResult().equals("Success")) {
                        Utilities.makeToast(context, getString(R.string.like_successfully));
                        Datum datum = binding.getData();
                        if (binding.getLike()) {
                            binding.setLike(false);
                            datum.setLikesCount(datum.getLikesCount() - 1);
                        } else {
                            binding.setLike(true);
                            datum.setLikesCount(datum.getLikesCount() + 1);
                        }
                        binding.setData(datum);
                        listener.onClick(true);
                    } else Utilities.makeToast(context, response.getResult());
                } else if (response.getStatus() != null && response.getStatus() == 403)
                    Utilities.openUnauthorizedDialog(context);
                else Utilities.makeToast(context, getString(R.string.server_error));
            } else Utilities.makeToast(context, getString(R.string.server_error));
        });
    }

    public void hitGetCoursePaymentDataService(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
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
                        startActivityForResult(new Intent(context, PaymentActivity.class).
                                putExtra("paymentData", (Serializable) result).putExtra("courseName", courseName), 1);
                    }
                } else if (response.getStatus() != null && response.getStatus() == 403)
                    Utilities.openUnauthorizedDialog(context);
                else {
                    if (progressView != null && progressView.isShowing())
                        progressView.dismiss();
                    Utilities.makeToast(context, getString(R.string.server_error));
                }
            } else {
                if (progressView != null && progressView.isShowing())
                    progressView.dismiss();
                Utilities.makeToast(context, getString(R.string.server_error));
            }
        });


    }

    public List<PreCoursePaymentResult> deserializeList(String json, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    public void showDialog(View v) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_talk_to_tutor_dialog, null, false);
        com.so.luotk.databinding.CustomTalkToTutorDialogBinding dialogBinding = CustomTalkToTutorDialogBinding.inflate(LayoutInflater.from(context
        ), null, false);
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_corners_drawable);
        dialogBinding.tvCall.setOnClickListener(view1 -> {
            dialog.dismiss();
            if (callingNo != null)
                checkPermissionGranted();
        });
        dialogBinding.tvMessage.setOnClickListener(view1 -> {
            dialog.dismiss();
            if (callingNo != null)
                checkPermissionForMessage();
        });
        dialogBinding.tvWhatsapp.setOnClickListener(view1 -> {
            dialog.dismiss();
            if (whatsAppNo != null)
                openWhatsAppIntent();
        });
    }

    private void openWhatsAppIntent() {
        String url = "https://api.whatsapp.com/send?phone=" + "91" + " " + whatsAppNo/*91 9814698149*/;
        try {
            PackageManager pm = getActivity().getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getActivity(), getString(R.string.install_whatsapp), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkPermissionForMessage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, REQUEST_MESSAGE);

            } else {
                smsIntent();
            }
        } else {
            smsIntent();

        }
    }

    public void checkPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);

            } else {
                callIntent();
            }
        } else {
            callIntent();

        }
    }

    public void smsIntent() {
        try {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:" + callingNo));
            startActivity(smsIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void callIntent() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + callingNo));
            startActivity(callIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_CALL) {
            callIntent();
        }
        if (requestCode == REQUEST_MESSAGE) {
            smsIntent();
        }
    }

    public interface likeChangeListener {
        void onClick(boolean flag);
    }

    public void setChangeListener(likeChangeListener listener) {
        this.listener = listener;
    }


    private String formatExpirydate(String expiryDate) {
        String[] values = expiryDate.split("-");
        String date = values[2] + "-" + values[1] + "-" + values[0];
        return date;

    }

    private void setDurationValidity(Datum result) {
        String duration = result.getDuration();
        switch (result.getDurationIn()) {
            case 1:
                binding.setDuration(duration + " days");
                break;
            case 2:
                binding.setDuration(duration + " months");
                break;
            case 3:
                binding.setDuration(duration + " years");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1 && data != null) {
            boolean isSuccess = data.getBooleanExtra("isSuccess", false);
            if (isSuccess)
                openSuccessDialog();
            else Utilities.makeToast(context, getString(R.string.pay_not_succ));

        }
    }

    private void openSuccessDialog() {
        PaymentSuccesDialogBinding dialogBinding = PaymentSuccesDialogBinding.inflate(LayoutInflater.from(context));
        Dialog dialog = new Dialog(context);
        dialog.setContentView(dialogBinding.getRoot());
        dialogBinding.setData(courseName);
        dialogBinding.visitCourse.setOnClickListener(v -> {
            if (!activity.isFinishing())
                dialog.dismiss();
            Intent intent = new Intent(context, CourseDetailAcrivity.class);
            intent.putExtra(PreferenceHandler.COURSE_ID, courseId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("notify", true);
            intent.putExtra("isFromMyCourses", true);
            intent.putExtra("fromLink", false);
            intent.putExtra(PreferenceHandler.NOTIFICATION_TYPE, "");
            startActivity(intent);
            if (getActivity() != null)
                getActivity().finish();

        });
        dialogBinding.visitLater.setOnClickListener(v -> {
            if (!activity.isFinishing())
                dialog.dismiss();
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(PreferenceHandler.IS_VISIT_LATER, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            if (getActivity() != null)
                getActivity().finish();
        });
        if (!activity.isFinishing())
            dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
    }
}