package com.so.luotk.fragments.adminrole.adminbatches;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.so.luotk.R;
import com.so.luotk.activities.adminrole.AdminMainScreen;
import com.so.luotk.chat.fragments.ChatFragment;
import com.so.luotk.chat.models.MessageModel;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.FragmentAdminBatchDetailsBinding;
import com.so.luotk.fragments.batches.OverviewFragment;
import com.so.luotk.models.output.GetBatchSettingsResponse;
import com.so.luotk.models.output.GetBatchSettingsResult;
import com.so.luotk.utils.FontTypeFontAwseme;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.so.luotk.utils.ZoomOutPageTransformer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class AdminBatchDetailsFragment extends Fragment {
    private FragmentAdminBatchDetailsBinding binding;
    private boolean isAttendenceOn, isAssignmentOn, isTestOn, isVideoOn, isLiveOn, isStudyMaterial, isStudentOn, isAnnouncementsOn;
    private Bundle bundle;
    private GetBatchSettingsResult batchSettingsResult;
    private static final String ARG_PARAM1 = "param1";
    private String mParam2;
    private int tabCount;
    private ArrayList<String> tabList;
    private String batchId;
    private boolean isFirstInternetToastDone;
    private Runnable runnable;
    private Handler handler;
    private String user_type;
    private ProgressView mProgressDialog;
    private LinkedHashMap<String, Fragment> map;
    private Context context;
    private Activity mActivity;
    public static TabLayout commonTabLayout;
    public static ImageView img_settings_icon;
    public static ArrayList<MessageModel> messageModelsDelete = new ArrayList<>();
//    public static RelativeLayout tranparentClick;


    public AdminBatchDetailsFragment() {
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
    public static AdminBatchDetailsFragment newInstance(String param1) {
        AdminBatchDetailsFragment fragment = new AdminBatchDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            batchId = getArguments().getString(ARG_PARAM1);
        }
        Utilities.restrictScreenShot(getActivity());
//     getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = FragmentAdminBatchDetailsBinding.inflate(inflater, container, false);
//        tranparentClick = binding.transparentClick;
        user_type = PreferenceHandler.readString(getContext(), PreferenceHandler.USER_TYPE, "");
        AdminMainScreen activity = (AdminMainScreen) getActivity();
        if (activity != null)
            activity.hideToolbar();
        setUpUI();
        setUpToolbar();
        return binding.getRoot();
    }

    private void setUpToolbar() {
        binding.toolbar.setNavigationIcon(Utilities.setNavigationIconColor(getContext()));
        binding.toolbar.setNavigationOnClickListener(view -> /*((AdminRoleActivity) getActivity()).callMe()*/
                getParentFragmentManager().popBackStack());
    }

    private void setUpUI() {
        tabList = new ArrayList<>();
        map = new LinkedHashMap<>();
        PreferenceHandler.writeString(getActivity(), PreferenceHandler.VIDEO_CACHING, null);
        commonTabLayout = binding.tabLayout;
        binding.tabLayout.setSelectedTabIndicatorColor(ResourcesCompat.getColor(getResources(), R.color.blue_main, null));
        /*  binding.imgSettingsIcon.setOnClickListener(view -> startActivity(new Intent(getContext(), AdminSettingsActivity.class)));*/
        img_settings_icon = binding.imgSettingsIcon;
        binding.imgSettingsIcon.setVisibility(View.GONE);

        handler = new Handler(Looper.myLooper());
        runnable = () -> checkInternet();
        checkInternet();

    }

    private void checkInternet() {
        if (Utilities.checkInternet(getContext())) {
            handler.removeCallbacks(runnable);
            if (batchId != null)
                hitGetBatchSettingsService();
            else {
                Toast.makeText(getContext(), getString(R.string.invalid_match), Toast.LENGTH_SHORT).show();
            }
        } else {
            handler.postDelayed(runnable, 3000);
            if (!isFirstInternetToastDone) {
                Toast.makeText(getContext(), getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
                isFirstInternetToastDone = true;
            }
        }
    }

    public void onBackPressed() {
        getParentFragmentManager().popBackStack();
    }


    private void hitGetBatchSettingsService() {
        new MyClient(getContext()).getBatchSettingsService(batchId, (content, error) -> {
            GetBatchSettingsResponse response = (GetBatchSettingsResponse) content;
            if (content != null) {
                if (response.getStatus() != null && response.getStatus().equalsIgnoreCase("200")) {
                    if (response.getResult() != null) {
                        applyBatchSettings(response.getResult());
                    }
                    if (response.getExtra() != null) {
                        binding.tvToolbarTitle.setText(response.getExtra().getBatchName());
                    }
                } else if (response.getStatus() != null && response.getStatus().equalsIgnoreCase("403")) {
                    Utilities.openUnauthorizedDialog(getContext());
                } else Utilities.makeToast(getContext(), error);
            }
        });

    }

    private void applyBatchSettings(GetBatchSettingsResult batchSettings) {
        OverviewFragment fragment = OverviewFragment.newInstance(batchId,  "adminrole","batch");
        map.put(context.getString(R.string.overview), fragment);
        if (batchSettings != null) {
            if (batchSettings.getManageStudent().equals("1"))
                map.put(context.getString(R.string.student), AdminStudentFragment.newInstance(batchId, ""));
            if (batchSettings.getManageAssignment().equals("1"))
                map.put(context.getString(R.string.assignments), AdminAssignmentTestVideoFragment.newInstance("assignment", batchId, true, ""));
            if (batchSettings.getTest().equals("1"))
                map.put(context.getString(R.string.tests), AdminAssignmentTestVideoFragment.newInstance("test", batchId, true, ""));
            if (batchSettings.getManageVideo().equals("1"))
                map.put(context.getString(R.string.videos), AdminAssignmentTestVideoFragment.newInstance("video", batchId, true, ""));
            if (batchSettings.getAnnouncement().equals("1"))
                map.put(context.getString(R.string.announcement), AdminAnnouncementFragment.newInstance(batchId));

      /*      if (batchSettings.getAttendance().equals("1"))
                map.put("Attendance", AdminAttendanceFragment.newInstance(batchId));*/

            if (batchSettings.getLive().equals("1"))
                map.put(context.getString(R.string.live), AdminLiveFragment.newInstance(batchId));
            if (batchSettings.getStudy_material().equals("1"))
                map.put(context.getString(R.string.study_material), AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", ""));

            map.put(getString(R.string.chat), ChatFragment.newInstance(batchId, "0", "batch", "", true));

        }
//        map.put("Chat", ChatFragment.newInstance(batchId, "0", "batch", ""));
        tabList = new ArrayList<>(map.keySet());
        if (getActivity() != null && isAdded())
            setupSettingsUI();
    }

    private void setupSettingsUI() {
        tabCount = tabList.size();
        binding.pager.setPageTransformer(true, new ZoomOutPageTransformer());
        binding.pager.setAdapter(new ViewPagerAdapter(getActivity().getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
        binding.tabLayout.setupWithViewPager(binding.pager);
        binding.tabLayout.setScrollBarFadeDuration(10);
        binding.progressIndicator.setVisibility(View.GONE);
        binding.batchLayout.setVisibility(View.VISIBLE);

    }

    private void changeTabsFont() {
        Log.d("TAG", "changeTabsFont: "+ binding.tabLayout.getChildAt(0));
        ViewGroup vg = (ViewGroup) binding.tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(FontTypeFontAwseme.getSemiBoldFontAwsome(getActivity()));
                }
            }
        }
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {


        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
           /* try {
                for (int i = 0; i < tabList.size(); i++) {
                    if (position == 0) {
                        fragment = OverviewFragment.newInstance(batchId, "adminrole", "batch");
                    }
                    if (position == 1 && position < tabCount) {
                        if (tabList.get(1).equalsIgnoreCase(getString(R.string.attendance))) {
                            fragment = AdminStudentFragment.newInstance(batchId, "");
                        } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.assignments))) {
                            fragment = AdminAssignmentTestVideoFragment.newInstance("assignment", batchId, true, "");
                        } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.tests))) {
                            fragment = AdminAssignmentTestVideoFragment.newInstance("test", batchId, true, "");
                        } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.videos))) {
                            fragment = AdminAssignmentTestVideoFragment.newInstance("video", batchId, true, "");
                        } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.live))) {
                            fragment = AdminAnnouncementFragment.newInstance(batchId);
                        } else if (tabList.get(1).equalsIgnoreCase(getString(R.string.study_material))) {
                            fragment = AdminLiveFragment.newInstance(batchId);
                        }  else if (tabList.get(1).equalsIgnoreCase(getString(R.string.study_material))) {
                            fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                        } else if (tabList.get(1).equalsIgnoreCase("Chat")) {
                            fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                        }
                    }
                    if (position == 2 && position < tabCount) {

                        if (tabList.get(2).equalsIgnoreCase(getString(R.string.assignments))) {
                            fragment = AdminAssignmentTestVideoFragment.newInstance("assignment", batchId, true, "");
                        } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.tests))) {
                            fragment = AdminAssignmentTestVideoFragment.newInstance("test", batchId, true, "");
                        } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.videos))) {
                            fragment = AdminAssignmentTestVideoFragment.newInstance("video", batchId, true, "");
                        } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.live))) {
                            fragment = AdminAnnouncementFragment.newInstance(batchId);
                        } else if (tabList.get(2).equalsIgnoreCase(getString(R.string.study_material))) {
                            fragment = AdminLiveFragment.newInstance(batchId);
                        }  else if (tabList.get(2).equalsIgnoreCase(getString(R.string.study_material))) {
                            fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                        } else if (tabList.get(2).equalsIgnoreCase("Chat")) {
                            fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                        }
                    }
                    if (position == 3 && position < tabCount) {

                        if (tabList.get(3).equalsIgnoreCase(getString(R.string.tests))) {
                            fragment = AdminAssignmentTestVideoFragment.newInstance("test", batchId, true, "");
                        } else if (tabList.get(3).equalsIgnoreCase(getString(R.string.videos))) {
                            fragment = AdminAssignmentTestVideoFragment.newInstance("video", batchId, true, "");
                        } else if (tabList.get(3).equalsIgnoreCase(getString(R.string.live))) {
                            fragment = AdminAnnouncementFragment.newInstance(batchId);
                        } else if (tabList.get(3).equalsIgnoreCase(getString(R.string.study_material))) {
                            fragment = AdminLiveFragment.newInstance(batchId);
                        }  else if (tabList.get(3).equalsIgnoreCase(getString(R.string.study_material))) {
                            fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                        } else if (tabList.get(3).equalsIgnoreCase("Chat")) {
                            fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                        }
                    }
                    if (position == 4 && position < tabCount) {
                        if (tabList.get(4).equalsIgnoreCase(getString(R.string.videos))) {
                            fragment = AdminAssignmentTestVideoFragment.newInstance("video", batchId, true, "");
                        } else if (tabList.get(4).equalsIgnoreCase(getString(R.string.live))) {
                            fragment = AdminAnnouncementFragment.newInstance(batchId);
                        } else if (tabList.get(4).equalsIgnoreCase(getString(R.string.study_material))) {
                            fragment = AdminLiveFragment.newInstance(batchId);
                        }  else if (tabList.get(4).equalsIgnoreCase(getString(R.string.study_material))) {
                            fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                        } else if (tabList.get(4).equalsIgnoreCase("Chat")) {
                            fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                        }
                    }
                    if (position == 5 && position < tabCount) {
                        if (tabList.get(5).equalsIgnoreCase(getString(R.string.live))) {
                            fragment = AdminAnnouncementFragment.newInstance(batchId);
                        } else if (tabList.get(5).equalsIgnoreCase(getString(R.string.study_material))) {
                            fragment = AdminLiveFragment.newInstance(batchId);
                        }  else if (tabList.get(5).equalsIgnoreCase(getString(R.string.study_material))) {
                            fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                        } else if (tabList.get(5).equalsIgnoreCase("Chat")) {
                            fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                        }
                    }
                    if (position == 6 && position < tabCount) {
                        if (tabList.get(6).equalsIgnoreCase(getString(R.string.study_material))) {
                            fragment = AdminLiveFragment.newInstance(batchId);
                        }  else if (tabList.get(6).equalsIgnoreCase(getString(R.string.study_material))) {
                            fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                        } else if (tabList.get(6).equalsIgnoreCase("Chat")) {
                            fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                        }
                    }
                    if (position == 7 && position < tabCount) {
                        if (tabList.get(7).equalsIgnoreCase(getString(R.string.study_material))) {
                            fragment = AdminStudyMaterialFragment.newInstance(batchId, "0", "batch", "");
                        } else if (tabList.get(7).equalsIgnoreCase("Chat")) {
                            fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                        }
                    }
                    if (position == 8 && position < tabCount) {
                        if (tabList.get(8).equalsIgnoreCase("Chat")) {
                            fragment = ChatFragment.newInstance(batchId, "0", "batch", "", false);
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            return Objects.requireNonNull(map.get(tabList.get(position)));
//            return fragment;
        }


        @Override
        public int getCount() {
            return tabCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            for (int i = 0; i < tabList.size(); i++) {
                if (position == i && position < tabCount) {
                    title = (tabList.get(i));
                }
            }
            return title;
        }

    }

}