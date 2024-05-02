package com.so.luotk.fragments.adminrole;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.so.luotk.R;
import com.so.luotk.adapter.adminrole.AnnouncementBatchListAdapter;
import com.so.luotk.databinding.FragmentMakeAnnouncemntBinding;
import com.so.luotk.models.newmodels.adminBatchModel.Result;
import com.so.luotk.models.output.BatchListResult;

import com.so.luotk.utils.Utilities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MakeAnnouncemntFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MakeAnnouncemntFragment extends Fragment {
    private FragmentMakeAnnouncemntBinding binding;
    private AnnouncementBatchListAdapter adapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<Result> mParam1;
    private boolean isFromMultibatch;

    public MakeAnnouncemntFragment() {
        // Required empty public constructor
    }


    public static MakeAnnouncemntFragment newInstance(List<BatchListResult> param1, boolean isFromMultibatch) {
        MakeAnnouncemntFragment fragment = new MakeAnnouncemntFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, (Serializable) param1);
        args.putBoolean(ARG_PARAM2, isFromMultibatch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (List<Result>) getArguments().getSerializable(ARG_PARAM1);
            isFromMultibatch = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMakeAnnouncemntBinding.inflate(inflater, container, false);
        setUpUi();
        setOnClicks();
        return binding.getRoot();
    }

    private void setUpUi() {
        binding.recyclerSelectedClasses.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (isFromMultibatch) {
            setUpAdapter();
            binding.toolbar.setVisibility(View.GONE);
            binding.divider.setVisibility(View.GONE);
        } else {
            binding.toolbar.setVisibility(View.VISIBLE);
            binding.toolbar.setTitle(getString(R.string.make_announcement));
            binding.divider.setVisibility(View.VISIBLE);
            binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColor(getContext()));
            binding.toolbar.setNavigationOnClickListener(view -> {
                assert getParentFragment() != null;
                getParentFragment().getParentFragmentManager().popBackStack();
            });
            binding.tvSelected.setVisibility(View.GONE);
            binding.layoutBatchAttached.setVisibility(View.GONE);
        }
    }

    private void setUpAdapter() {
        Set<Result> set = new HashSet<>();
        set.addAll(mParam1);
        mParam1.clear();
        mParam1.addAll(set);
        adapter = new AnnouncementBatchListAdapter(mParam1, "fragment");
        if (!mParam1.isEmpty()) {
            binding.recyclerSelectedClasses.setAdapter(adapter);
        }
    }

    private void setOnClicks() {
        binding.imgAddBatch.setOnClickListener(view -> {
            if (getActivity() != null)
                getActivity().getSupportFragmentManager().popBackStack();
        });
        binding.tvDone.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(binding.edtTypeMsg.getText().toString().trim())) {
                Utilities.makeToast(getContext(), getString(R.string.announcement_made));
            }
        });
    }

    public void onBackPressed() {
        getParentFragmentManager().popBackStack();
    }
}