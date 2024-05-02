package com.so.luotk.fragments.adminrole;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.so.luotk.R;
import com.so.luotk.activities.WelcomeActivityNew;
import com.so.luotk.activities.adminrole.AdminBatchDetailActivity;
import com.so.luotk.activities.adminrole.AdminMainScreen;
import com.so.luotk.activities.adminrole.ContactUs;
import com.so.luotk.activities.adminrole.CreateBatchActivity;
import com.so.luotk.adapter.BatchListAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.FragmentAdminBatchUiBinding;
import com.so.luotk.models.output.BatchListResult;
import com.so.luotk.models.output.GetBatchListResponse;
import com.so.luotk.models.output.GetBatchSubmitAssignmentTestResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AdminBatchUiFragment extends Fragment {
    public static final int CREATE_BATCH_STATUS = 1001;
    private FragmentAdminBatchUiBinding binding;
    public static BatchListAdapter batchListAdapter;
    public static ArrayList<BatchListResult> batchListResults;
    private boolean isDarkMode;
    private APIInterface apiInterface;
    private ProgressView mProgressDialog;
    private boolean isFirstInternetToastDone;
    private Handler handler;
    private Runnable runnable;
    private boolean isSearchOpen, isRefreshing;
    private Context context;
    private Activity mActivity;
    private String orgName, userType;
//    DatabaseReference userReference, groupRef;
    private BottomSheetBehavior<View> behavior;
    private CoordinatorLayout coordinatorLayout;

    public AdminBatchUiFragment() {
        // Required empty public constructor
    }


    public static AdminBatchUiFragment newInstance() {
        AdminBatchUiFragment fragment = new AdminBatchUiFragment();
        return fragment;
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
        batchListResults = new ArrayList<>();
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminBatchUiBinding.inflate(inflater, container, false);
        AdminMainScreen activity = (AdminMainScreen) getActivity();
        if (activity != null)
//            activity.showToolbar();
            setUpUI();
        setClickListeners();
        orgName = PreferenceHandler.readString(getActivity(), PreferenceHandler.ORG_NAME, null);
        if (orgName != null)
            binding.toolbarTitle.setText(orgName);
        binding.toolbarTitle1.setText(orgName);
        /*groupRef = FirebaseDatabase.getInstance().getReference().child(PreferenceHandler.BATCH_GROUP);
        userReference = FirebaseDatabase.getInstance().getReference().child(PreferenceHandler.USER_GROUP);
*/
        behavior = BottomSheetBehavior.from(binding.bottomSheetBatch.bottomSheet);
        behavior.isGestureInsetBottomIgnored();

        if (PreferenceHandler.readString(getActivity(), PreferenceHandler.ISACTIVEORG, "0").equals("0")) {
            checkOrgInActive();
        }
//        createFirebaseUser();
        return binding.getRoot();
    }

    public void checkOrgInActive() {
        Utilities.hideKeyBoard(getActivity());
        final boolean isAdmin;
        isAdmin = PreferenceHandler.readBoolean(getActivity(), PreferenceHandler.ADMIN_LOGGED_IN, false);
        final Activity activity = (Activity) getActivity();
        Dialog mDialog = new Dialog(getActivity());
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_inactive_orgnization);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        if (!activity.isFinishing())
            mDialog.show();
        mDialog.setCancelable(false);
        mDialog.getWindow().setAttributes(lp);
        Button btnOk = mDialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin) {
                    PreferenceHandler.writeBoolean(getActivity(), PreferenceHandler.ADMIN_LOGGED_IN, false);
                } else {
                    PreferenceHandler.writeBoolean(getActivity(), PreferenceHandler.LOGGED_IN, false);
                }
                if (!activity.isFinishing())
                    mDialog.dismiss();
                Intent intent = new Intent(getActivity(), WelcomeActivityNew.class);
                intent.putExtra("isFromLogout", true);
                getActivity().startActivity(intent);
                final Activity activity = (Activity) getActivity();
                activity.finish();
            }
        });


    }

    private void setClickListeners() {
        binding.fab.setOnClickListener(v -> {
            Intent in = new Intent(getContext(), CreateBatchActivity.class);
            in.putExtra("from", "create");
            startActivityForResult(in, CREATE_BATCH_STATUS);
        });
        userType = PreferenceHandler.readString(getActivity(), PreferenceHandler.USER_TYPE, null);
        if (userType.equals("faculty")) {
            binding.bottomSheetBatch.createBatches.setVisibility(View.GONE);
            binding.menuIcon.setVisibility(View.GONE);
            binding.menuIcon1.setVisibility(View.GONE);
        } else {
            binding.bottomSheetBatch.createBatches.setVisibility(View.VISIBLE);
            binding.menuIcon.setVisibility(GONE);
            binding.menuIcon1.setVisibility(GONE);
        }
        binding.bottomSheetBatch.createBatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyBoard(getActivity());
                Intent in = new Intent(getContext(), CreateBatchActivity.class);
                in.putExtra("from", "create");
                startActivityForResult(in, CREATE_BATCH_STATUS);
            }
        });

        binding.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyBoard(getActivity());
                Context wrapper = new ContextThemeWrapper(getActivity(), R.style.popupMenuStyle);
                PopupMenu popupMenu = new PopupMenu(wrapper, binding.menuIcon);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.toolbar_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked
                        if (menuItem.getItemId() == R.id.contact_us) {
                            Intent in = new Intent(getActivity(), ContactUs.class);
                            startActivity(in);
                        } else {
                            try {
                                String emailFrom = "mailto:" + getString(R.string.contact_email);
                                Intent email = new Intent(Intent.ACTION_VIEW);
                                email.setType("message/rfc822")
                                        .setData(Uri.parse(emailFrom))
//                                        .putExtra(Intent.EXTRA_EMAIL, "your.email@gmail.com")
                                        /* .putExtra(Intent.EXTRA_SUBJECT, "Subject")
                                         .putExtra(Intent.EXTRA_TEXT, "My Email message")*/
                                        .setPackage("com.google.android.gm");
                                startActivity(email);
                            } catch (ActivityNotFoundException e) {
                                Log.d("TAG", "onMenuItemClick: " + e.getMessage());
                                //TODO smth
                            }
                        }

                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });


    }

    private void setUpUI() {

        apiInterface = ApiUtils.getApiInterface();
        handler = new Handler(Looper.myLooper());
        runnable = () -> checkInternet();
        int currentNightMode = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                isDarkMode = false;
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                isDarkMode = true;
                break;
        }
        binding.bottomSheetBatch.recyclerBatchList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.shimmerLayoutFrame.startShimmer();
        /*binding.bottomSheetBatch.swipeRefreshLayout.setOnRefreshListener(() -> {
            isSearchOpen = false;
            isRefreshing = true;
            binding.bottomSheetBatch.searchView.setQuery("", false);
            binding.bottomSheetBatch.searchView.clearFocus();
            Utilities.hideKeyBoard(getActivity());
            if (Utilities.checkInternet(getContext())) {
                if (!isSearchOpen)
                    hitGetBatchListService();
            } else {
                Utilities.makeToast(getContext(), getString(R.string.internet_connection_error));
            }

        });*/

        binding.bottomSheetBatch.searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("TAG", "onClose: ");
                Utilities.hideKeyBoard(getActivity());
                return false;
            }
        });
        binding.bottomSheetBatch.searchView.setVisibility(VISIBLE);
        checkInternet();
        binding.bottomSheetBatch.searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        Utilities.hideKeyBoard(getActivity());
        binding.bottomSheetBatch.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                isSearchOpen = true;
                if (batchListAdapter != null) {
                    batchListAdapter.getFilter().filter(query);
                    if (batchListAdapter.getItemCount() < 1) {
                        binding.bottomSheetBatch.tvNoResults.setVisibility(View.VISIBLE);
                        binding.bottomSheetBatch.recyclerBatchList.setVisibility(View.GONE);
                    } else {
                        binding.bottomSheetBatch.tvNoResults.setVisibility(View.GONE);
                        binding.bottomSheetBatch.recyclerBatchList.setVisibility(View.VISIBLE);
                    }
                    if (TextUtils.isEmpty(query)) {
                        isSearchOpen = false;
                        binding.bottomSheetBatch.tvNoResults.setVisibility(View.GONE);
                        binding.bottomSheetBatch.recyclerBatchList.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSearchOpen = true;
                if (batchListAdapter != null) {
                    batchListAdapter.getFilter().filter(newText);
                    if (batchListAdapter.getItemCount() < 1) {
                        binding.bottomSheetBatch.tvNoResults.setVisibility(View.VISIBLE);
                        binding.bottomSheetBatch.recyclerBatchList.setVisibility(View.GONE);
                    } else {
                        binding.bottomSheetBatch.tvNoResults.setVisibility(View.GONE);
                        binding.bottomSheetBatch.recyclerBatchList.setVisibility(View.VISIBLE);
                    }
                    if (TextUtils.isEmpty(newText)) {
                        isSearchOpen = false;
                        binding.bottomSheetBatch.tvNoResults.setVisibility(View.GONE);
                        binding.bottomSheetBatch.recyclerBatchList.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });

        int[] colors = {Color.parseColor("#008EF5"), Color.parseColor("#03B3FF")};

        //create a new gradient color
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, colors);

        gd.setCornerRadius(24f);

        Log.e("fcmToken: ", PreferenceHandler.readString(getActivity(), PreferenceHandler.FCM_TOKEN, ""));
        //apply the button background to newly created drawable gradient


    }


    private void openNextFragment(Fragment fragment) {
        if (getActivity() != null && isAdded())
            getParentFragmentManager().beginTransaction().replace(R.id.admin_container, fragment).addToBackStack(null).commit();
    }

    private void hitGetBatchListService() {
        batchListResults.clear();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(getContext(), PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(getContext(), PreferenceHandler.DEVICE_ID, ""));
        Call<GetBatchListResponse> call = apiInterface.getAdminBatchList(headers);
        call.enqueue(new Callback<GetBatchListResponse>() {
            @Override
            public void onResponse(Call<GetBatchListResponse> call, Response<GetBatchListResponse> response) {
                /*if (binding.bottomSheetBatch.swipeRefreshLayout.isRefreshing()) {
                    binding.bottomSheetBatch.swipeRefreshLayout.setRefreshing(false);
                } else */
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                } else {
                    binding.shimmerLayoutFrame.stopShimmer();
                    binding.shimmerLayoutFrame.setVisibility(View.GONE);
                }

//                binding.bottomSheetBatch.layoutBatchList.setVisibility(View.VISIBLE);
                binding.bottomSheetBatch.searchView.setVisibility(VISIBLE);
                binding.bottomSheetBatch.tvNoResults.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200")) {
                        if (response.body().getSuccess().equalsIgnoreCase("true")) {
                            if (response.body().getResult() != null && response.body().getResult().size() > 0) {
                                batchListResults = response.body().getResult();
                                /*if ( batchListResults.size() > 0) {
                                    binding.createBatch.setText("Hi Admin!\nCreate your batch");
                                } else {
                                    binding.createBatch.setText("Hi Admin!\nCreate your first batch");
                                }*/
                                setBatchListAdapter();

                                /*binding.shimmerLayoutFrame.stopShimmer();
                                binding.shimmerLayoutFrame.setVisibility(View.GONE);
                                binding.shimmerLay.setVisibility(View.GONE);
                                binding.bottomSheetBatch.sheet.setVisibility(VISIBLE);
                                binding.layoutDataView.setVisibility(VISIBLE);*/
                            } else {
//                                binding.bottomSheetBatch.layoutBatchList.setVisibility(View.GONE);
                                binding.shimmerLayoutFrame.stopShimmer();
                                binding.shimmerLayoutFrame.setVisibility(View.GONE);
                                binding.shimmerLay.setVisibility(View.GONE);
                                binding.bottomSheetBatch.sheet.setVisibility(VISIBLE);
                                binding.bottomSheetBatch.searchView.setVisibility(GONE);
                                binding.layoutDataView.setVisibility(VISIBLE);
                                binding.bottomSheetBatch.tvNoResults.setVisibility(View.VISIBLE);
                                binding.constarintLayo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        try {
                                            View hidden = binding.bottomSheetBatch.createBatches;
                                            behavior.setPeekHeight(hidden.getBottom() + 200);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }
                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(getContext());
                    } else {
                        Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<GetBatchListResponse> call, Throwable t) {
                /*if (binding.bottomSheetBatch.swipeRefreshLayout.isRefreshing()) {
                    binding.bottomSheetBatch.swipeRefreshLayout.setRefreshing(false);
                } else {*/
                binding.shimmerLayoutFrame.stopShimmer();
                binding.shimmerLayoutFrame.setVisibility(View.GONE);
//                }

                Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkInternet() {
        if (Utilities.checkInternet(getContext())) {
            handler.removeCallbacks(runnable);
            hitGetBatchListService();
        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Utilities.makeToast(getContext(), getString(R.string.internet_connection_error));
                isFirstInternetToastDone = true;
            }
        }
    }

    private void setBatchListAdapter() {
        /*Set<BatchListResult> set = new LinkedHashSet<>(batchListResults);
        batchListResults.clear();
        batchListResults.addAll(set);
        if (batchListAdapter == null) {
            batchListAdapter = new BatchListAdapter(getContext(), batchListResults, "adminrole", isDarkMode);
        } else {
            batchListAdapter.updateList(batchListResults);
        }*/
        batchListAdapter = new BatchListAdapter(getContext(), batchListResults, "adminrole", isDarkMode);
        binding.bottomSheetBatch.recyclerBatchList.setAdapter(batchListAdapter);

        binding.constarintLayo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    if (batchListAdapter.getItemCount() > 2) {
                        binding.bottomSheetBatch.recyclerBatchList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        View hidden = binding.bottomSheetBatch.recyclerBatchList.getChildAt(2);
                        behavior.setPeekHeight(hidden.getTop() + 200);
                    } else if (batchListAdapter.getItemCount() > 0 && batchListAdapter.getItemCount() == 2) {
                        binding.bottomSheetBatch.recyclerBatchList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        View hidden = null;
                        if (batchListAdapter.getItemCount() == 1) {
                            hidden = binding.bottomSheetBatch.recyclerBatchList.getChildAt(0);
                        } else {
                            hidden = binding.bottomSheetBatch.recyclerBatchList.getChildAt(1);
                        }
                        behavior.setPeekHeight(hidden.getTop() + 600);
                    } else if (batchListAdapter.getItemCount() > 0 && batchListAdapter.getItemCount() == 1){
                        View hidden = null;
                        hidden = binding.bottomSheetBatch.recyclerBatchList.getChildAt(0);
                        behavior.setPeekHeight(hidden.getTop() + 700);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        isRefreshing = false;

        binding.shimmerLayoutFrame.stopShimmer();
        binding.shimmerLayoutFrame.setVisibility(View.GONE);
        binding.shimmerLay.setVisibility(View.GONE);
        binding.bottomSheetBatch.sheet.setVisibility(VISIBLE);
        binding.layoutDataView.setVisibility(VISIBLE);

        batchListAdapter.SetOnItemClickListener(position -> {
            binding.bottomSheetBatch.searchView.setQuery("", false);
            binding.bottomSheetBatch.searchView.clearFocus();
            if (!isRefreshing) {

                if (batchListAdapter.getBatchList() != null && batchListAdapter.getBatchList().size() > 0) {
//                    String batchId = batchListAdapter.getBatchList().get(position).getId();
                    try {
                        /*String batchId = batchListResults.get(position).getId();

                        groupRef.child("fk_" + batchId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                } else {
                                    groupRef.child("fk_" + batchId).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });*/

                        Intent intent = new Intent(context, AdminBatchDetailActivity.class);
                        intent.putExtra("batchData", new Gson().toJson(batchListResults.get(position)));
                        intent.putExtra("batch_position", String.valueOf(position));
                        intent.putExtra(PreferenceHandler.BATCH_ID, batchListAdapter.getBatchList().get(position).getId());
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    openNextFragment(AdminBatchDetailsFragment.newInstance(batchId));
                }
            }
        });

        batchListAdapter.SetOnEditItemClickListener(new BatchListAdapter.OnEditItemClickListener() {
            @Override
            public void onEditClick(int position, RelativeLayout threeDotsLay) {
                openConfirmDeleteBatch(position);
            }
        });

    }

    private void openConfirmDeleteBatch(int position) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View deleteDialogView = factory.inflate(R.layout.delete_student_popup, null);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setView(deleteDialogView);
        AlertDialog dialog = alertBuilder.create();

        TextView are_you_sure = deleteDialogView.findViewById(R.id.are_you_sure_text);
        are_you_sure.setText(getString(R.string.are_you_sure_delete_student));
        deleteDialogView.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.checkInternet(context)) {
                    dialog.dismiss();
                    hitDeleteBatchService(position);
                } else {
                    Utilities.makeToast(context, getString(R.string.internet_connection_error));
                }
            }
        });
        deleteDialogView.findViewById(R.id.no_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        deleteDialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void hitDeleteBatchService(int position) {
        Map<String, String> map = new HashMap<>();
        if (batchListResults != null && batchListResults.size() > 0 && batchListResults.get(position).getId() != null)
            map.put("batchId", batchListResults.get(position).getId());
        new MyClient(context).deleteBatchFrom(map, (content, error) -> {
            if (content != null) {
                GetBatchSubmitAssignmentTestResponse response = (GetBatchSubmitAssignmentTestResponse) content;
                if (response.getStatus() != null && response.getStatus().equals("200")) {
                    try {
                        batchListResults = batchListAdapter.removeAt(position);
                        if (batchListResults.size() == 0) {
                            binding.bottomSheetBatch.tvNoResults.setVisibility(View.VISIBLE);
                            binding.bottomSheetBatch.recyclerBatchList.setVisibility(GONE);
                        }
                    } catch (Exception e) {
                        Utilities.makeToast(context, getString(R.string.refresh_content));
                    }

                    Utilities.makeToast(context, getString(R.string.batch_del_successfully));

                } else if (response.getStatus() != null && response.getStatus().equals("403"))
                    Utilities.openUnauthorizedDialog(context);
                else Utilities.makeToast(context, getString(R.string.server_error));
            } else Utilities.makeToast(context, getString(R.string.server_error));
        });
    }

    /*public void createFirebaseUser() {
        try {
            String userName = PreferenceHandler.readString(getActivity(), PreferenceHandler.ORG_NAME, "");
            String userId = PreferenceHandler.readString(getActivity(), PreferenceHandler.USERID, "");
            String userMobile = PreferenceHandler.readString(getActivity(), PreferenceHandler.ORG_PHONE_NO, "");
            Users users = new Users(userName, userMobile
                    , userId);
            userReference.child(userId).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
//                        Toast.makeText(context, "User Created Successfully", Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.d("TAG", "createFirebaseUser: " + e.getClass());
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_BATCH_STATUS) {
            if (data != null) {
                boolean isDataSubmitted = data.getBooleanExtra(PreferenceHandler.IS_DATA_SUBMITTED, false);
                if (isDataSubmitted) {
                    hitGetBatchListService();
                }
            }
        }
    }
}