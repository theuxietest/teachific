package com.so.luotk.fragments.adminrole;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.so.luotk.R;
import com.so.luotk.activities.adminrole.CreateEnquiryActivity;
import com.so.luotk.adapter.adminrole.EnquiryListAdapter;
import com.so.luotk.databinding.FragmentAdminEnquiryBinding;
import com.so.luotk.models.output.StudentDetailsData;

import java.util.ArrayList;
import java.util.List;


public class AdminEnquiryFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private FragmentAdminEnquiryBinding binding;
    private EnquiryListAdapter adapter;
    private List<StudentDetailsData> dataList;

    public AdminEnquiryFragment() {
        // Required empty public constructor
    }

    public static AdminEnquiryFragment newInstance() {
        AdminEnquiryFragment fragment = new AdminEnquiryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminEnquiryBinding.inflate(inflater, container, false);
        setUpUi();
        setOnClicks();
        return binding.getRoot();
    }

    private void setOnClicks() {
        binding.btnAddEnquiry.setOnClickListener(view -> startActivity(new Intent(getContext(), CreateEnquiryActivity.class)));
    }

    private void setUpUi() {
        dataList = new ArrayList<>();
        binding.enquiryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //    binding.enquiryRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        for (int i = 0; i < 6; i++) {
            StudentDetailsData student = new StudentDetailsData();
            student.setName( getString(R.string.student) + " " + (i + 1));
            dataList.add(student);
        }
        if (!dataList.isEmpty()) {
            binding.layoutEnquiryList.setVisibility(View.VISIBLE);
            binding.layoutNoEnquiries.setVisibility(View.GONE);
            adapter = new EnquiryListAdapter(dataList);
            binding.enquiryRecyclerView.setAdapter(adapter);
        } else {
            binding.layoutEnquiryList.setVisibility(View.GONE);
            binding.layoutNoEnquiries.setVisibility(View.VISIBLE);
        }


    }
}