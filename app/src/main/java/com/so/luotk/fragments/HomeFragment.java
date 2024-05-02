package com.so.luotk.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.so.luotk.BuildConfig;
import com.so.luotk.R;
import com.so.luotk.activities.CourseDetailAcrivity;
import com.so.luotk.activities.FeeStructureActivity;
import com.so.luotk.activities.MainActivity;
import com.so.luotk.activities.SmFolderDetailsActivity;
import com.so.luotk.activities.VideoListActivity;
import com.so.luotk.activities.ViewAttachmentActivity;
import com.so.luotk.activities.WelcomeActivityNew;
import com.so.luotk.adapter.HomeStoreListAdapter;
import com.so.luotk.adapter.PopularVideosAdapter;
import com.so.luotk.adapter.SliderAdapterExample;
import com.so.luotk.client.MyClient;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.FragmentHomeBinding;
import com.so.luotk.models.newmodels.courseModel.CourseModel;
import com.so.luotk.models.newmodels.courseModel.Result;
import com.so.luotk.models.output.ImportantInfoResponse;
import com.so.luotk.models.output.OrgInfoResult;
import com.so.luotk.models.video.DatumVideo;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.so.luotk.viewmodel.CourseViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private FragmentHomeBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView /*tvSendJoinRequest,*/ tvShareApp, tvToolbarTitle, tvJoinRequestTitle, tvJoinRequestTitle2, sendJoinRequest;
    private ConstraintLayout constraintLayout15;
//    private Button sendJoinRequest;
    private View layoutHome;
    private ProgressView mProgressDialog;
    private LinearLayout layoutEditTextBatchCode;
    private View layoutJoinRequest, layoutVisitWebsite;
    private boolean isBatchCreated;
    private ImageView imgCart;
    private Toolbar toolbar;
    private ImageView img_profile;
    private Context context;
    private PopularVideosAdapter adapter, testimonialsAdapter;
    private List<DatumVideo> popularVideoResultList, testimonialVideosResult;
    private Activity mActivity;
    private String websiteLink;
    private Handler handler, imagehandler;
    private Runnable runnable, imagerunnable;
    private boolean isFirstInternetToastDone;
    private TextView[] dots;
    private int[] layouts;
    private final int currentPage = 0;
    private MyViewPagerAdapter myViewPagerAdapter;
    private static final int NUM_PAGES = 5;
    private List<String> list;
    private SliderAdapterExample sliderAdapter;
    private boolean testimonialsFetched;
    private boolean isLoaded;
    private String orgName;
    private long mLastClickTime = 0;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefreshing;
//    DatabaseReference userReference;
    private String userType;
    private Handler handlerCourse;
    private Runnable runnableCourse;
    private CourseViewModel viewModel;
    private HomeStoreListAdapter storeListAdapter;
    private String base;
    WebView myGoogleWebView;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isBatchCreated = PreferenceHandler.readBoolean(context, PreferenceHandler.CREATED_BATCH, false);
        orgName = PreferenceHandler.readString(context, PreferenceHandler.ORG_NAME, null);
        websiteLink = PreferenceHandler.readString(context, PreferenceHandler.ORG_WEBSITE_LINK, null);
        userType = PreferenceHandler.readString(context, PreferenceHandler.USER_TYPE, null);
        Log.d("TAG", "onCreate: " + PreferenceHandler.readString(context, PreferenceHandler.ADMIN_MOBILE_NO, null));
        popularVideoResultList = new ArrayList<>();
        testimonialVideosResult = new ArrayList<>();
        handlerCourse = new Handler(Looper.myLooper());
        runnableCourse = this::hitGetCourseListService;
        viewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        if (getArguments() != null) {
              /*  mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utilities.restrictScreenShot(getActivity());
//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        userReference = FirebaseDatabase.getInstance().getReference().child(PreferenceHandler.USER_GROUP);
//        createFirebaseUser();
        setupUI();
        hitGetCourseListService();
        /*if (PreferenceHandler.readString(mActivity, PreferenceHandler.COURSES_ON_HOME, "0").equalsIgnoreCase("1")) {
            hitGetCourseListService();
        } else {
            binding.courseLaySection.setVisibility(View.GONE);
        }*/

        if (PreferenceHandler.readString(getActivity(), PreferenceHandler.ISACTIVEORG, "0").equals("0")) {
            checkOrgInActive();
        }

//        binding.webLay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("WebView", "onClick: ");
//                Intent intent = new Intent(context, ViewAttachmentActivity.class);
//                intent.putExtra(PreferenceHandler.PDF_NAME, "https://docs.google.com/forms/d/e/1FAIpQLSf3iFmyEG1k3ILeh0ooupfl1pYgkjR5HzHj8ZNbm8B4qMjcHg/viewform");
//                intent.putExtra("isFrom", "web");
//                startActivity(intent);
//            }
//        });

        return binding.getRoot();
    }


    private class HelloWebViewClient extends WebViewClient {
        ProgressBar progressBar;
        public HelloWebViewClient(ProgressBar load_data_progress) {
            this.progressBar = load_data_progress;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl("javascript:(function() { " +
                    "document.querySelector('[role=\"toolbar\"]').remove();})()");
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            view.loadUrl("javascript:(function() { " +
                    "document.getElementsByClassName('ndfHFb-c4YZDc-GSQQnc-LgbsSe ndfHFb-c4YZDc-to915-LgbsSe VIpgJd-TzA9Ye-eEGnhe ndfHFb-c4YZDc-LgbsSe')[0].style.display='none'; })()");
        }
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
    private void setupUI() {
        binding.shimmerLayout.startShimmer();
        layoutJoinRequest = binding.layoutJoiningRequest;
        layoutVisitWebsite = binding.cardViewVisitWebsite;
        swipeRefreshLayout = binding.swipeRefreshLayout;
//        tvSendJoinRequest = binding.tvSendJoinRequest;
        sendJoinRequest = binding.sendJoinRequest;
        constraintLayout15 = binding.constraintLayout15;
        tvShareApp = binding.tvShareApp;
        layoutHome = binding.layoutHome;
        tvJoinRequestTitle = binding.tvSendJoinRequestTitle;
        tvJoinRequestTitle2 = binding.tvSendJoinRequestTitle2;
        toolbar = binding.toolbar;
        imgCart = binding.imgCart;
        imgCart.setOnClickListener(this);
//        tvSendJoinRequest.setOnClickListener(this);
        sendJoinRequest.setOnClickListener(this);
        constraintLayout15.setOnClickListener(this);
        tvShareApp.setOnClickListener(this);
        binding.tvSeeAll.setOnClickListener(this);
        binding.tvSeeAllTestimonials.setOnClickListener(this);
        binding.tvViewFreeMaterial.setOnClickListener(this);
        tvToolbarTitle = binding.toolbarTitle;
        testimonialsFetched = false;
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(),R.color.statusBarColor));
        if (orgName != null) {
            tvToolbarTitle.setText(/*"Welcome to " + */orgName);
        }
        if (websiteLink != null && !websiteLink.contains("false")) {
            constraintLayout15.setOnClickListener(this);
            binding.cardViewVisitWebsite.setVisibility(View.VISIBLE);
        } else binding.cardViewVisitWebsite.setVisibility(View.GONE);
        binding.recyclerPopularVideos.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerTestimonialVideos.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        adapter = new PopularVideosAdapter();
        testimonialsAdapter = new PopularVideosAdapter();
        binding.recyclerPopularVideos.setAdapter(adapter);
        binding.recyclerTestimonialVideos.setAdapter(testimonialsAdapter);
        if (isBatchCreated) {
            imgCart.setVisibility(View.VISIBLE);
            tvJoinRequestTitle.setText(R.string.successfully_added_to_batch);
            tvJoinRequestTitle2.setText(R.string.add_to_another_batch);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 0);
        } else imgCart.setVisibility(View.VISIBLE);
        handler = new Handler(Looper.myLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternet();
            }
        };
        /*     if (!isLoaded)*/
        checkInternet();
       /* else {
            binding.layoutDataView.setVisibility(View.VISIBLE);
            binding.shimmerLayout.setVisibility(View.GONE);
        }*/

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PreferenceHandler.writeString(getActivity(), PreferenceHandler.HOMEFRAGMENT_CACHE, null);
                isRefreshing = true;
                checkInternet();

            }
        });
        binding.recyclerCourse.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerCourse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        storeListAdapter = new HomeStoreListAdapter((data, pos) -> {
            Intent intent = new Intent(context, CourseDetailAcrivity.class);
            intent.putExtra("isFromMyCourses", false);
            intent.putExtra("course", data);
            intent.putExtra("base", base);
            intent.putExtra("from_home_course", "home_course");
            startActivityForResult(intent, 1);
        }, context, false);

        binding.recyclerCourse.setAdapter(storeListAdapter);

        binding.tvSeeAllCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).goToCourse();
            }
        });
    }


    private void checkInternet() {
        sliderAdapter = new SliderAdapterExample(context);
        binding.imageSlider.setSliderAdapter(sliderAdapter);
        binding.imageSlider.setIndicatorRadius(3);
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.NONE);
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.imageSlider.startAutoCycle();

       /* binding.imageSlider.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeRefreshLayout.setRefreshing(false);
                return true;
            }
        });*/
        if (context != null && Utilities.checkInternet(context)) {
            handler.removeCallbacks(runnable);
            if (PreferenceHandler.readString(getActivity(), PreferenceHandler.HOMEFRAGMENT_CACHE, null) != null) {

                ImportantInfoResponse response = new Gson().fromJson(PreferenceHandler.readString(getActivity(), PreferenceHandler.HOMEFRAGMENT_CACHE, null), ImportantInfoResponse.class);
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.layoutDataView.setVisibility(View.VISIBLE);
                if (response.getStatus() != null && response.getStatus() == 200) {
                    if (response.getSuccess() && response.getResult() != null) {
                        String imageBaseUrl = "";
                        if (response.getExtra() != null)
                            imageBaseUrl = response.getExtra().getImageBaseUrl();
                        OrgInfoResult result = response.getResult();
                        try {
                            PreferenceHandler.writeString(getActivity(), PreferenceHandler.HOMEFRAGMENT_CACHE, new Gson().toJson(response));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (result.getFiles() != null && result.getFiles().size() > 0) {
                            binding.layoutImpInfo.setVisibility(View.VISIBLE);
                            int size = result.getFiles().size();
                            List<String> images = new ArrayList<>();
                            for (int i = 0; i < size; i++)
                                images.add(imageBaseUrl + "/" + result.getFiles().get(i).getPicture());
                            Log.d("TAG", "GetImage: " + images.size());
                            if (size > 1) {
                                try {
                                    sliderAdapter.renewItems(images, "web");
                                    binding.singleImage.setVisibility(View.GONE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else
                                Glide.with(this).load(imageBaseUrl + "/" + result.getFiles().get(0).getPicture()).into(binding.singleImage);
                        } else {
                            if (PreferenceHandler.readString(mActivity, PreferenceHandler.IS_LITE_APP, "0").equalsIgnoreCase("1")) {
                                binding.layoutImpInfo.setVisibility(View.VISIBLE);
                                AssetManager assetManager = mActivity.getAssets();
                                try {
                                    String[] files = assetManager.list("sliderImages");
                                    List<String> it = Arrays.asList(files);
                                    sliderAdapter.renewItems(it, "assets");
                                    binding.singleImage.setVisibility(View.GONE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                binding.layoutImpInfo.setVisibility(View.GONE);
                            }
                        }
                        Log.d("TAG", "checkInternet: " +response.getResult().getPopular().size() );
                        if (response.getResult().getPopular() != null && response.getResult().getPopular().size() > 0) {
                            popularVideoResultList = response.getResult().getPopular();
                            binding.cardViewPopularVideos.setVisibility(View.VISIBLE);
                            adapter.updateList(popularVideoResultList);
                            if (response.getResult().getPopular().size() > 8)
                                binding.tvSeeAll.setVisibility(View.VISIBLE);
                            else binding.tvSeeAll.setVisibility(View.GONE);
                        } else binding.cardViewPopularVideos.setVisibility(View.GONE);
                        if (response.getResult().getTestimonial() != null && response.getResult().getTestimonial().size() > 0) {
                            testimonialVideosResult = response.getResult().getTestimonial();
                            binding.cardViewTesstimonialVideos.setVisibility(View.VISIBLE);
//                            binding.viewHeight.setVisibility(View.GONE);
                            testimonialsAdapter.updateList(testimonialVideosResult);
                            if (response.getResult().getTestimonial().size() > 8)
                                binding.tvSeeAllTestimonials.setVisibility(View.VISIBLE);
                            else binding.tvSeeAllTestimonials.setVisibility(View.GONE);
                        } else binding.cardViewTesstimonialVideos.setVisibility(View.GONE);
                    }
                }

            } else {
                getOrgInfoService();
            }


        } else {
            handler.postDelayed(runnable, 5000);
            if (!isFirstInternetToastDone) {
                Utilities.makeToast(context, getString(R.string.internet_connection_error));
                isFirstInternetToastDone = true;
            }
        }
    }

    // Getting Oragnization Services
    private void getOrgInfoService() {

        new MyClient(context).getOrgInfo((content, error) -> {
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.layoutDataView.setVisibility(View.VISIBLE);

            if (content != null) {
                ImportantInfoResponse response = (ImportantInfoResponse) content;
                if (response.getStatus() != null && response.getStatus() == 200) {
                    if (response.getSuccess() && response.getResult() != null) {


                        String imageBaseUrl = "";
                        if (response.getExtra() != null)
                            imageBaseUrl = response.getExtra().getImageBaseUrl();
                        OrgInfoResult result = response.getResult();
                        try {
                            PreferenceHandler.writeString(getActivity(), PreferenceHandler.HOMEFRAGMENT_CACHE, new Gson().toJson(response));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (result.getFiles() != null && result.getFiles().size() > 0) {
                            binding.layoutImpInfo.setVisibility(View.VISIBLE);
                            int size = result.getFiles().size();
                            List<String> images = new ArrayList<>();
                            for (int i = 0; i < size; i++)
                                images.add(imageBaseUrl + "/" + result.getFiles().get(i).getPicture());
                            if (size > 1) {
                                sliderAdapter.renewItems(images, "web");
                                binding.singleImage.setVisibility(View.GONE);
                            } else
                                Glide.with(this).load(imageBaseUrl + "/" + result.getFiles().get(0).getPicture()).into(binding.singleImage);
                        } else {

                            if (PreferenceHandler.readString(mActivity, PreferenceHandler.IS_LITE_APP, "0").equalsIgnoreCase("1")) {
                                binding.layoutImpInfo.setVisibility(View.VISIBLE);
                                AssetManager assetManager = mActivity.getAssets();
                                try {
                                    String[] files = assetManager.list("sliderImages");
                                    List<String> it = Arrays.asList(files);
                                    sliderAdapter.renewItems(it, "assets");
                                    binding.singleImage.setVisibility(View.GONE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                binding.layoutImpInfo.setVisibility(View.GONE);
                            }

                        }
                        if (response.getResult().getPopular() != null && response.getResult().getPopular().size() > 0) {
                            popularVideoResultList = response.getResult().getPopular();
                            binding.cardViewPopularVideos.setVisibility(View.VISIBLE);
                            adapter.updateList(popularVideoResultList);
                            if (response.getResult().getPopular().size() > 8)
                                binding.tvSeeAll.setVisibility(View.VISIBLE);
                            else binding.tvSeeAll.setVisibility(View.GONE);
                        } else binding.cardViewPopularVideos.setVisibility(View.GONE);
                        if (response.getResult().getTestimonial() != null && response.getResult().getTestimonial().size() > 0) {
                            testimonialVideosResult = response.getResult().getTestimonial();
                            binding.cardViewTesstimonialVideos.setVisibility(View.VISIBLE);
//                            binding.viewHeight.setVisibility(View.GONE);
                            testimonialsAdapter.updateList(testimonialVideosResult);
                            if (response.getResult().getTestimonial().size() > 8)
                                binding.tvSeeAllTestimonials.setVisibility(View.VISIBLE);
                            else binding.tvSeeAllTestimonials.setVisibility(View.GONE);
                        } else binding.cardViewTesstimonialVideos.setVisibility(View.GONE);
                    }
                } else if (response.getStatus() != null && response.getStatus() == 403)
                    Utilities.openUnauthorizedDialog(context);
                else Utilities.makeToast(context, getString(R.string.server_error));
            } else Utilities.makeToast(context, getString(R.string.server_error));

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            isRefreshing = false;
        });
    }


    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (v.getId()) {
            /*case R.id.tv_send_join_request:
                SendJoinRequestFragment sendJoinRequestFragment = SendJoinRequestFragment.newInstance(true);
                openNextFragment(sendJoinRequestFragment);
                break;*/
            case R.id.send_join_request:
                SendJoinRequestFragment sendJoinRequestFragment1 = SendJoinRequestFragment.newInstance(true);
                openNextFragment(sendJoinRequestFragment1);
                break;
            case R.id.constraintLayout15:
                Intent intent = new Intent(context, ViewAttachmentActivity.class);
                intent.putExtra(PreferenceHandler.PDF_NAME, websiteLink);
                intent.putExtra("isFrom", "web");
                startActivity(intent);
                break;
            case R.id.tv_share_app:
                shareApp();
                break;
            case R.id.img_cart:
                showFeeStructureScreen();
               /* FeeStructureFragment feeStructureFragment = new FeeStructureFragment();
                openNextFragment(feeStructureFragment);*/
                break;
            case R.id.tv_view_free_material:
                showFreeStudyMaterial();
                break;
            case R.id.tv_see_all:
                startActivity(new Intent(context, VideoListActivity.class).putExtra(PreferenceHandler.IS_FROM, "home")
                        .putExtra("popularvideos", (Serializable) popularVideoResultList).putExtra("title", getString(R.string.recommended_videos)));
                break;
            case R.id.tv_see_all_testimonials:
                startActivity(new Intent(context, VideoListActivity.class).putExtra(PreferenceHandler.IS_FROM, "home")
                        .putExtra("popularvideos", (Serializable) testimonialVideosResult).putExtra("title", getString(R.string.testimonials)));
                break;

        }
    }
    private void showFeeStructureScreen() {
        startActivity(new Intent(mActivity, FeeStructureActivity.class)
        );

    }
    private void showFreeStudyMaterial() {
        startActivity(new Intent(mActivity, SmFolderDetailsActivity.class)
                .putExtra(PreferenceHandler.SmFolderId, "0")
                .putExtra(PreferenceHandler.IS_FROM, "freeMaterial")
                .putExtra(PreferenceHandler.smFolderName,  getString(R.string.free_study_material))
        );

    }

    private void openNextFragment(Fragment fragment) {
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private void shareApp() {
        String shareMessage = PreferenceHandler.readString(context, PreferenceHandler.SHARE_MESSAGE, null);
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Home", "onResume: ");
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private List<String> list;

        public MyViewPagerAdapter() {
        }

        public MyViewPagerAdapter(List<String> list) {
            this.list = list;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.layout_slider, container, false);
            //transformPage(view,position);
            Glide.with(context).load(list.get(position)).centerCrop().into((ImageView) view.findViewById(R.id.slider_image));
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);

        }

    }


    public void openKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        editText.requestFocus();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (imagerunnable != null) imagehandler.removeCallbacks(imagerunnable);
        if (runnable != null) handler.removeCallbacks(runnable);
    }

    private void hitGetCourseListService() {
        Map<String, String> map = new HashMap<>();
        map.put("pageLength", 5 + "");
        map.put("page", 1 + "");
        viewModel.getList(context, map, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String cousrseListJson = PreferenceHandler.readString(getActivity(), PreferenceHandler.FEATURED_COURSE, null);
        Log.d("TAG", "onViewCreated11: " + cousrseListJson);
        try {
            CourseModel courseModel = new Gson().fromJson(cousrseListJson, CourseModel.class);
            if (courseModel != null) {
                if (courseModel.getResult().getOtherCourses().getData().size() > 0) {
                    if (PreferenceHandler.readString(mActivity, PreferenceHandler.COURSES_ON_HOME, "0").equalsIgnoreCase("1")) {
                        binding.courseLaySection.setVisibility(View.VISIBLE);
                    } else {
                        binding.courseLaySection.setVisibility(View.GONE);
                    }

                }
                setBatchListAdapter(courseModel.getResult());
                base = courseModel.getExtra().getImageBaseUrl();
                storeListAdapter.setBaseUrl(base);
                handlerCourse.removeCallbacks(runnableCourse);
            } else {
                viewModel.getObservableData().observe(getViewLifecycleOwner(), response -> {
                    if (response != null)
                        switch (response.status) {
                            case EXTRA:
                                binding.courseLaySection.setVisibility(View.GONE);
                                break;
                            case AUTHENTICATED:
//                            stopShimmer();
                                if (response.data.getResult().getOtherCourses().getData().size() > 0) {
                                    if (PreferenceHandler.readString(mActivity, PreferenceHandler.IS_LITE_APP, "0").equalsIgnoreCase("1")) {
                                        binding.courseLaySection.setVisibility(View.GONE);
                                    } else {
                                        if (PreferenceHandler.readString(mActivity, PreferenceHandler.COURSES_ON_HOME, "0").equalsIgnoreCase("1")) {
                                            binding.courseLaySection.setVisibility(View.VISIBLE);
                                        } else {
                                            binding.courseLaySection.setVisibility(View.GONE);
                                        }
                                    }

                                }
                                setBatchListAdapter(response.data.getResult());
                                base = response.data.getExtra().getImageBaseUrl();
                                storeListAdapter.setBaseUrl(base);
                                handler.removeCallbacks(runnable);
                                break;
                            case LOADING:
//                            binding.setResult(false);
                                break;
                            case NOT_AUTHENTICATED:
//                            stopShimmer();
                                Utilities.openUnauthorizedDialog(context);
                                break;
                            case ERROR:

                                break;
                        }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBatchListAdapter(Result result) {

        Log.d("TAG", "setBatchListAdapter: " + new Gson().toJson(result.getOtherCourses().getData()));
        Log.d("TAG", "setBatchListAdapter: " + result.getOtherCourses().getData().size());
        storeListAdapter.updateList(result.getOtherCourses().getData());
        if (result.getOtherCourses().getData().size() > 4) {
            binding.tvSeeAllCourse.setVisibility(View.VISIBLE);
        } else {
            binding.tvSeeAllCourse.setVisibility(View.GONE);
        }
        if (binding.swipeRefreshLayout.isRefreshing())
            binding.swipeRefreshLayout.setRefreshing(false);
    }
    /*public void createFirebaseUser() {
        try {
            String userName = PreferenceHandler.readString(getActivity(), PreferenceHandler.LOGGED_IN_USERNAME, "");
            String userId = PreferenceHandler.readString(getActivity(), PreferenceHandler.USERID, "");
            String userMobile = PreferenceHandler.readString(getActivity(), PreferenceHandler.USER_MOBILE, "");
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

}

