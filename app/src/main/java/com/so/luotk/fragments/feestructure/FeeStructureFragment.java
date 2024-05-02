package com.so.luotk.fragments.feestructure;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.so.luotk.R;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;


public class FeeStructureFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout layoutPayNow, layoutNoFeeStructure, layoutFeeStructurePhoto;
    private LinearLayout layoutFeeStructure;
    private boolean isBatchCreated;
    private String email;
    private ImageView imageViewFeeStructure;
    private Toolbar toolbar;
    private Context context;
    private Activity mActivity;

    public FeeStructureFragment() {
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
// getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        View view = inflater.inflate(R.layout.fragment_fee_structure, container, false);
        setToolbar(view);
        setupUI(view);
        return view;
    }

    private void setToolbar(View view) {
        //toolbar setup
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(context));
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
        layoutNoFeeStructure = view.findViewById(R.id.layout_no_fee_structure);
        layoutFeeStructure = view.findViewById(R.id.layout_fee_structure);
        layoutFeeStructurePhoto = view.findViewById(R.id.layout_fee_structure_image);
        layoutPayNow = view.findViewById(R.id.layout_pay_now);
        imageViewFeeStructure = view.findViewById(R.id.img_fee_structure);
        layoutPayNow.setOnClickListener(this);
        layoutPayNow.setVisibility(View.GONE);
        isBatchCreated = PreferenceHandler.readBoolean(context, PreferenceHandler.CREATED_BATCH, false);
        /*if (isBatchCreated) {
            String photoUrl = PreferenceHandler.readString(context, PreferenceHandler.FEE_STRUCTURE_PHOTO, null);
            layoutNoFeeStructure.setVisibility(View.GONE);
            layoutFeeStructurePhoto.setVisibility(View.VISIBLE);
            if (photoUrl != null) {
                Glide.with(context).load(photoUrl).into(imageViewFeeStructure);
            }
        } else {
            layoutFeeStructurePhoto.setVisibility(View.GONE);
            layoutNoFeeStructure.setVisibility(View.VISIBLE);
        }*/
        String photoUrl = PreferenceHandler.readString(context, PreferenceHandler.FEE_STRUCTURE_PHOTO, null);
        Log.d("TAG", "setupUI: " + photoUrl);
        layoutNoFeeStructure.setVisibility(View.GONE);
        layoutFeeStructurePhoto.setVisibility(View.VISIBLE);
        if (photoUrl != null) {
            Glide.with(context).load(photoUrl).into(imageViewFeeStructure);
        } else {
            layoutFeeStructurePhoto.setVisibility(View.GONE);
            layoutNoFeeStructure.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.layout_pay_now) {
         /*   Intent intent = new Intent(context, PaymentActivity.class);
            startActivity(intent);*/
            // startPayment();
        }
    }
}
