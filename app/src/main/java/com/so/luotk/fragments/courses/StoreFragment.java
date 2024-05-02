package com.so.luotk.fragments.courses;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.so.luotk.R;
import com.so.luotk.activities.CourseDetailAcrivity;
import com.so.luotk.databinding.FragmentStoreBinding;
import com.so.luotk.models.newmodels.courseModel.Datum;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.ZoomOutPageTransformer;

import org.jetbrains.annotations.NotNull;

public class StoreFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String FROMURL = "fromUrl";
    private static final String COURSEID = "courseID";
    private static final String COURSEDATA = "courseData";
    private boolean from_url;
    private String course_id, course_data;
    private String mParam2;
    private boolean isPaymentDone;
    private FragmentStoreBinding binding;

    public StoreFragment() {
        // Required empty public constructor
    }

    public static StoreFragment newInstance(boolean isPaymentDone) {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isPaymentDone);
        // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static StoreFragment newInstance(boolean isPaymentDone, boolean fromUrl, String courseId, String courseData) {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isPaymentDone);
        args.putBoolean(FROMURL, fromUrl);
        args.putString(COURSEID, courseId);
        args.putString(COURSEDATA, courseData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isPaymentDone = getArguments().getBoolean(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            from_url = getArguments().getBoolean(FROMURL);
            course_id = getArguments().getString(COURSEID);
            course_data = getArguments().getString(COURSEDATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStoreBinding.inflate(inflater, container, false);
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.white));
        if (getActivity() != null && isAdded()) {
            binding.pager.setPageTransformer(true, new ZoomOutPageTransformer());
            binding.pager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));
            binding.tabLayout.setupWithViewPager(binding.pager);
            if (isPaymentDone)
                binding.tabLayout.getTabAt(1).select();
        }

        if (course_data != null) {
            Datum newData = new Gson().fromJson(course_data, Datum.class);
            Intent intent = new Intent(getActivity(), CourseDetailAcrivity.class);
            intent.putExtra("isFromMyCourses", false);
            intent.putExtra("course", newData);
            intent.putExtra("base", "base");
            startActivityForResult(intent, 1);
        } else {
            Log.d("TAG", "onCreateView: Null");
        }
        return binding.getRoot();
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
                fragment = CourseStoreFragment.newInstance(false, from_url, course_id);
            if (PreferenceHandler.readBoolean(getActivity(), PreferenceHandler.ISSTATICLOGIN, false)) {

            } else {
                if (position == 1)
                    fragment = CourseStoreFragment.newInstance(true, from_url, course_id);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            if (PreferenceHandler.readBoolean(getActivity(), PreferenceHandler.ISSTATICLOGIN, false)) {
                return 1;
            } else {
                return 2;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0)
                title = getString(R.string.featured_course);
            if (PreferenceHandler.readBoolean(getActivity(), PreferenceHandler.ISSTATICLOGIN, false)) {
            } else {
                if (position == 1)
                    title = getString(R.string.my_course);
            }

            return title;
        }

    }
}