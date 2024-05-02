package com.so.luotk.fragments.more;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.so.luotk.R;
import com.so.luotk.activities.SplashActivity;
import com.so.luotk.databinding.FragmentSetLanguageBinding;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.Locale;

public class SetLanguageFragment extends Fragment {
    private FragmentSetLanguageBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1, locale = "en";
    private boolean isFromAdmin;
    private Context context;
    private Activity mActivity;
    private Toolbar toolbar;
    private TextView toolbarCustomTitle;
    public SetLanguageFragment() {
    }

    public static SetLanguageFragment newInstance(boolean isFromAdmin) {
        SetLanguageFragment fragment = new SetLanguageFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isFromAdmin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isFromAdmin = getArguments().getBoolean(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSetLanguageBinding.inflate(inflater, container, false);
        locale = PreferenceHandler.readString(context, PreferenceHandler.LOCALE, "en");
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(context));
        binding.toolbar.setNavigationOnClickListener(v -> {
                    if (isFromAdmin)
                        onBackPressed();
                    else
                        onBackPressed();
                }
        );
        if (locale.equals("en"))
            binding.setSignal(0);
        if (locale.equals("hi"))
            binding.setSignal(1);
        if (locale.equals("mr"))
            binding.setSignal(2);
        if (locale.equals("kn"))
            binding.setSignal(3);
        if (locale.equals("ar"))
            binding.setSignal(4);
        if (locale.equals("pa"))
            binding.setSignal(5);
        if (locale.equals("or"))
            binding.setSignal(6);
        binding.setFragment(this);
        setToolbar();
        return binding.getRoot();
    }

    private void setToolbar() {
        //toolbar setup
        final Drawable upArrow = ContextCompat.getDrawable(mActivity, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(mActivity, R.color.black), PorterDuff.Mode.SRC_ATOP);
        binding.toolbar.setNavigationIcon(upArrow);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    public void setChecked(Integer signal) {
        binding.setSignal(signal);
    }

    public void onBackPressed() {
        if (getActivity() != null) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.popBackStack();
        }
    }

    public void setLocale() {
        String lang = "en";
        if (binding.getSignal() == 1)
            lang = "hi";
        if (binding.getSignal() == 0)
            lang = "en";
        if (binding.getSignal() == 2)
            lang = "mr";
        if (binding.getSignal() == 3)
            lang = "kn";
        if (binding.getSignal() == 4)
            lang = "ar";
        if (binding.getSignal() == 5)
            lang = "pa";
        if (binding.getSignal() == 6)
            lang = "or";
        if (lang.equals(locale))
            Utilities.makeToast(context, getString(R.string.languge_already_set));
        else {
            Locale myLocale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                conf.setLocale(myLocale);
            } else {
                conf.locale = myLocale;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getActivity().createConfigurationContext(conf);
            } else {
                res.updateConfiguration(conf, dm);
            }
            /*conf.locale = myLocale;
            res.updateConfiguration(conf, dm);*/
            PreferenceHandler.writeString(context, PreferenceHandler.LOCALE, lang);
            Intent refresh = new Intent();
            if (isFromAdmin)
                refresh = new Intent(context, SplashActivity.class);
            else
                refresh = new Intent(context, SplashActivity.class);
            startActivity(refresh);
//            mActivity.finish();
        }

    }

    @BindingAdapter("setVisibile")
    public static void setVisibilty(View view, boolean isChecked) {
        if (isChecked)
            view.setVisibility(View.VISIBLE);
        else view.setVisibility(View.GONE);

    }

    public static void restart(Context context){
        Intent mainIntent = IntentCompat.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_LAUNCHER);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(mainIntent);
        System.exit(0);
    }
}