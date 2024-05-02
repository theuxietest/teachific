package com.so.luotk.fragments.adminrole.adminbatches;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.so.luotk.databinding.FragmentAddStudentBinding;
import com.so.luotk.utils.Utilities;

public class AddStudentFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private FragmentAddStudentBinding binding;

    public AddStudentFragment() {
        // Required empty public constructor
    }


    public static AddStudentFragment newInstance(String param1, String param2) {
        AddStudentFragment fragment = new AddStudentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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
        // Inflate the layout for this fragment
        binding = FragmentAddStudentBinding.inflate(inflater, container, false);
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(getContext()));
        binding.toolbar.setNavigationOnClickListener(view -> {
            getParentFragmentManager().popBackStack();
        });
        return binding.getRoot();
    }

    public void onBackPressed() {
        getParentFragmentManager().popBackStack();
    }


}