package com.so.luotk.fragments.reports;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.so.luotk.R;
import com.so.luotk.databinding.FragmentReportsTabbedBinding;
import com.so.luotk.utils.Utilities;
import com.so.luotk.utils.ZoomOutPageTransformer;

import org.jetbrains.annotations.NotNull;

public class ReportsTabbedFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentReportsTabbedBinding binding;
    private boolean isFromNotification;
    private Toolbar toolbar;
    private TextView toolbarCustomTitle;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context context;
    private Activity mActivity;


    public ReportsTabbedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    public static ReportsTabbedFragment newInstance(String param1, boolean isFromNotify) {
        ReportsTabbedFragment fragment = new ReportsTabbedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putBoolean(ARG_PARAM2, isFromNotify);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            isFromNotification = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReportsTabbedBinding.inflate(inflater, container, false);


        binding.tvToolbarTitle.setText(R.string.test_attempted);
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(context));
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);
        binding.pager.setPageTransformer(true, new ZoomOutPageTransformer());
        if (getActivity() != null && isAdded())
            binding.pager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()/*, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)*/));
        binding.tabLayout.setupWithViewPager(binding.pager);
        final Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    if (!isFromNotification) {
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction trans = manager.beginTransaction();
                        trans.remove(ReportsTabbedFragment.this);
                        trans.commit();
                        manager.popBackStack();
                    } else {
                        openFragment(new ReportFragment());
                    }
                }
            }

        });
        Log.d("ReporrtTabbed", "onCreateView: ");
        return binding.getRoot();
    }

    public void openFragment(Fragment fragment) {

        if (fragment != null && getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            // transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    public void onBackPressed() {
        if (getActivity() != null) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.popBackStack();
        }
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0)
                fragment = ReportAssignmentTestFragment.newInstance("batchtest", false);
            if (position == 1)
                fragment = ReportAssignmentTestFragment.newInstance("coursetest", false);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0)
                title = getString(R.string.batch_tests);
            if (position == 1)
                title = getString(R.string.course_tests);
            return title;
        }

    }
}