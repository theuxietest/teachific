package com.so.luotk.fragments.more;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.so.luotk.R;
import com.so.luotk.activities.TermsOfUseActivity;
import com.so.luotk.activities.WelcomeActivityNew;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.models.output.LogoutResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private Toolbar toolbar;
    private String titlerName, whatsAppNo, fbLink, instaLink, orgName;
    private RelativeLayout layoutChatWithUs, layoutFb, layoutInstagram, layoutLogout, layoutRateUs;
    public static String FACEBOOK_URL = "https://facebook.com/";
    public static String FACEBOOK_PAGE_ID = "YourPageName";
    private ProgressView mProgressDialog;
    private AlertDialog.Builder builder;
    private Context context;
    private Activity mActivity;
    private View layoutTerms;

    public SettingFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utilities.restrictScreenShot(getActivity());
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        setToolbar(view);
        setupUI(view);
        return view;
    }

    public static SettingFragment getInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }


    private void setToolbar(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(Utilities.setNavigationIconColor(context));
        toolbar.setTitle(getString(R.string.action_settings));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void onBackPressed() {
        if (getActivity() != null) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.popBackStack();
        }
    }

    private void setupUI(View view) {
        whatsAppNo = PreferenceHandler.readString(context, PreferenceHandler.ORG_PHONE_NO, null);
        orgName = PreferenceHandler.readString(context, PreferenceHandler.ORG_NAME, null);
        fbLink = PreferenceHandler.readString(context, PreferenceHandler.ORG_FB_LINK, null);
        instaLink = PreferenceHandler.readString(context, PreferenceHandler.ORG_INSTAGRAM_LINK, null);
        layoutTerms = view.findViewById(R.id.layout_terms);
        layoutTerms.setOnClickListener(this);
        layoutChatWithUs = view.findViewById(R.id.layout_chat_with_us);
        layoutChatWithUs.setOnClickListener(this);
        layoutFb = view.findViewById(R.id.layout_facebook);
        layoutFb.setOnClickListener(this);
        layoutInstagram = view.findViewById(R.id.layout_follow_instagram);
        layoutInstagram.setOnClickListener(this);
        layoutLogout = view.findViewById(R.id.layout_logout);
        layoutLogout.setOnClickListener(this);
        layoutRateUs = view.findViewById(R.id.layout_rate_us);
        layoutRateUs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_chat_with_us:
                if (whatsAppNo != null) {
                    openWhatsAppIntent();
                }
                break;

            case R.id.layout_facebook:
                setFacebookIntent();
                break;

            case R.id.layout_follow_instagram:
                if (instaLink != null) {
                    openInstaProfile();
                }

                break;

            case R.id.layout_logout:
                showLogoutAlert(context, orgName);
                break;

            case R.id.layout_rate_us:
                openRateUsIntent();
                break;
            case R.id.layout_terms:
                startActivity(new Intent(context, TermsOfUseActivity.class));
                break;
            default:
                break;
        }
    }

    private void openRateUsIntent() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mActivity.getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + mActivity.getPackageName())));
        }
    }

    private void openWhatsAppIntent() {
        String url = "https://api.whatsapp.com/send?phone=" + "91" + " " + whatsAppNo;
        try {
            PackageManager pm = mActivity.getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, getString(R.string.install_whatsapp), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAppInstalled() {
        try {
            mActivity.getApplicationContext().getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public void setFacebookIntent() {
        if (fbLink != null && !fbLink.equalsIgnoreCase("false")) {
            Uri uri = Uri.parse("fb://page/" + fbLink);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.facebook.katana");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                try {
                    Intent intent2 = new Intent(new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL + fbLink)));
                    if (intent2.resolveActivity(mActivity.getPackageManager()) != null)
                        startActivityForResult(intent2, 0);
                    else {
                        Toast.makeText(context, " No browser found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception exp) {
                    exp.printStackTrace();
                }

            }
        }
    }

    private void openInstaProfile() {
        if (instaLink != null && !instaLink.equalsIgnoreCase("false")) {
            Uri uri = Uri.parse("instagram://user?username=" + instaLink);
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                try {
                    Intent intent = new Intent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/" + instaLink)));
                    if (intent.resolveActivity(mActivity.getPackageManager()) != null)
                        startActivityForResult(intent, 0);
                    else {
                        Toast.makeText(context, " No browser found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (Exception exp) {
                    exp.printStackTrace();
                }

            }
        }
    }

    public void showLogoutAlert(final Context context, String appName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(appName);
        builder.setMessage(R.string.sure_you_want_to_logout);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.log_out), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hitLogoutService();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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


    public void hitLogoutService() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(context, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(context, PreferenceHandler.DEVICE_ID, ""));
        APIInterface apiInterface = ApiUtils.getApiInterface();
        Call<LogoutResponse> call = apiInterface.logout(headers);
        mProgressDialog = new ProgressView(context);
        mProgressDialog.show();
        call.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess().equalsIgnoreCase("true")) {
                            PreferenceHandler.writeBoolean(context, PreferenceHandler.IS_EMAIL_LOGIN, false);
                            PreferenceHandler.writeBoolean(context, PreferenceHandler.LOGGED_IN, false);
                            PreferenceHandler.setList(context, PreferenceHandler.NOTIFICATION_DATA, null);
                            Intent intent = new Intent(context, WelcomeActivityNew.class);
                            intent.putExtra("isFromLogout", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(context);
                    }
                }

            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
