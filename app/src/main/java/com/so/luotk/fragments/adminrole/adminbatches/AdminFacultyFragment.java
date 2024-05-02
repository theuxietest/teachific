package com.so.luotk.fragments.adminrole.adminbatches;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.so.luotk.R;
import com.so.luotk.adapter.adminrole.AttendanceStudentListAdapter;
import com.so.luotk.databinding.CustomAddFacultyDialogBinding;
import com.so.luotk.databinding.FragmentAdminFacultyBinding;
import com.so.luotk.models.output.StudentDetailsData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminFacultyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminFacultyFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentAdminFacultyBinding binding;
    private String mParam1;
    private String mParam2;
    private AttendanceStudentListAdapter adapter;
    private List<StudentDetailsData> facultyList;

    public AdminFacultyFragment() {
        // Required empty public constructor
    }

    public static AdminFacultyFragment newInstance(String param1, String param2) {
        AdminFacultyFragment fragment = new AdminFacultyFragment();
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
        binding = FragmentAdminFacultyBinding.inflate(inflater, container, false);
        binding.btnAddFaculty.setOnClickListener(view -> showAddFacultyDailog());
        setUpUi();
        return binding.getRoot();
    }

    private void setUpUi() {
        facultyList = new ArrayList<>();
        binding.facultyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.facultyRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        for (int i = 0; i < 5; i++) {
            StudentDetailsData faculty = new StudentDetailsData();
            faculty.setName("Faculty " + (i + 1));
            facultyList.add(faculty);
        }
        if (!facultyList.isEmpty()) {
            binding.layoutFacultyList.setVisibility(View.VISIBLE);
            binding.layoutNoFaculty.setVisibility(View.GONE);
            adapter = new AttendanceStudentListAdapter(facultyList, "faculty");
            binding.facultyRecyclerView.setAdapter(adapter);
        }

    }

    private void showAddFacultyDailog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_add_faculty_dialog, null, false);
        CustomAddFacultyDialogBinding dialogBinding = CustomAddFacultyDialogBinding.bind(view);
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(dialogBinding.getRoot());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.round_corners_drawable);
        dialog.show();
        dialog.getWindow().setLayout(800, ViewGroup.LayoutParams.WRAP_CONTENT);

    }
}