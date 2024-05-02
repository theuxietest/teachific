package com.so.luotk.fragments.courses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.so.luotk.R;
import com.so.luotk.activities.CourseDetailAcrivity;
import com.so.luotk.adapter.StoreListAdapter;
import com.so.luotk.databinding.FragmentCourseStoreBinding;
import com.so.luotk.models.newmodels.courseModel.CourseModel;
import com.so.luotk.models.newmodels.courseModel.Result;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.so.luotk.viewmodel.CourseViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class CourseStoreFragment extends Fragment {
    private static final String ARG_PARAM1 = "ARG_PARAM1";
    private static final String FROMURL = "FROMURL";
    private static final String COURSEID = "COURSEID";
    private FragmentCourseStoreBinding binding;
    private Handler handler;
    private Runnable runnable;
    private boolean toast = true;
    private StoreListAdapter storeListAdapter;
    private Context context;
    private boolean isFromMyCourses;
    private PreferenceHandler preferenceHandler;
    private CourseViewModel viewModel;
    private boolean once = true;
    private String base;
    private int page = 1;
    private boolean isLoading = true;
    private boolean fromUrl;
    private String course_id = "";
    private final int pageLength = 50;
    private String searchKey;
    private SearchView searchView;
    private final Handler searchHandler = new Handler(Looper.myLooper());
    private Runnable searchRunnable;
    private boolean isSearchOpen;

    public CourseStoreFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static CourseStoreFragment newInstance(boolean isFromMyCourses) {
        CourseStoreFragment fragment = new CourseStoreFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isFromMyCourses);
        fragment.setArguments(args);
        return fragment;
    }

    public static CourseStoreFragment newInstance(boolean isFromMyCourses, boolean fromUrl, String courseId) {
        CourseStoreFragment fragment = new CourseStoreFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, isFromMyCourses);
        args.putBoolean(FROMURL, fromUrl);
        args.putString(COURSEID, courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isFromMyCourses = getArguments().getBoolean(ARG_PARAM1);
            fromUrl = getArguments().getBoolean(FROMURL);
            course_id = getArguments().getString(COURSEID);
        }
        handler = new Handler(Looper.myLooper());
        runnable = this::hitGetCourseListService;
        preferenceHandler = new PreferenceHandler(context);
        viewModel = new ViewModelProvider(this).get(CourseViewModel.class);

    }

    @Override
    public void onResume() {
        super.onResume();
        // storeListAdapter.removeList();
        if (once)
            hitGetCourseListService();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utilities.restrictScreenShot(getActivity());
//   getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_course_store, container, false);
        binding.storeRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        storeListAdapter = new StoreListAdapter((data, pos) -> {
            Intent intent = new Intent(context, CourseDetailAcrivity.class);
            intent.putExtra("isFromMyCourses", isFromMyCourses);
            intent.putExtra("course", data);
            intent.putExtra("base", base);
            startActivityForResult(intent, 1);
        }, isFromMyCourses, getActivity());
        searchView = binding.courseSearchView;
        //searchView.setVisibility(View.GONE);
        binding.storeRecyclerView.setAdapter(storeListAdapter);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            PreferenceHandler.saveFeaturedCoursetList(getActivity(), PreferenceHandler.FEATURED_COURSE, null);
            if (!TextUtils.isEmpty(searchKey)) {
                searchView.clearFocus();
                searchView.setQuery("", false);
            }
            page = 1;
            isLoading = true;
            hitGetCourseListService();
        });
        binding.rootLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Utilities.hideKeyBoard(context);
                }
                return true;
            }
        });

        binding.setProgress(false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.courseSearchView.clearFocus();

        String cousrseListJson = PreferenceHandler.readString(getActivity(), PreferenceHandler.FEATURED_COURSE, null);
        Log.d("TAG", "onViewCreated: " + cousrseListJson);
        try {
            CourseModel courseModel = new Gson().fromJson(cousrseListJson, CourseModel.class);
            /*if (courseModel != null) {
                setBatchListAdapter(courseModel.getResult());
                base = courseModel.getExtra().getImageBaseUrl();
                storeListAdapter.setBaseUrl(base);
                handler.removeCallbacks(runnable);
            } else {*/
                viewModel.getObservableData().observe(getViewLifecycleOwner(), response -> {
                    if (response != null)
                        switch (response.status) {
                            case EXTRA:
                                binding.setResult(true);
                                if (!TextUtils.isEmpty(searchKey))
                                    binding.setMessage(getString(R.string.no_result));
                                else {
                                    if (isFromMyCourses) {
                                        binding.setMessage(getString(R.string.no_my_course));
                                    }
                                    else {
                                        binding.setMessage(getString(R.string.no_course_available));
                                    }
                                }
                                stopShimmer();
                                break;
                            case AUTHENTICATED:
                                stopShimmer();
                                if (page == 1)
                                    storeListAdapter.removeList();
                      /*  LayoutAnimationController animationController =
                                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
                        binding.storeRecyclerView.setLayoutAnimation(animationController);*/
                                setBatchListAdapter(response.data.getResult());
                                base = response.data.getExtra().getImageBaseUrl();
                                storeListAdapter.setBaseUrl(base);
                                handler.removeCallbacks(runnable);
                                break;
                            case LOADING:
                                binding.setResult(false);
                                if (!binding.swipeRefreshLayout.isRefreshing()) {
                                    if (!isSearchOpen) {
                                        binding.shimmerLayout.startShimmer();
                                        binding.setFlag(true);
                                    } else
                                        binding.setProgress(true);
                                }
                                break;
                            case NOT_AUTHENTICATED:
                                stopShimmer();
                                Utilities.openUnauthorizedDialog(context);
                                break;
                            case ERROR:
                                if (toast)
                                    Utilities.makeToast(context, response.message);
                                handler.postDelayed(runnable, 5000);
                                if (response.message.contains("network"))
                                    toast = false;
                                else
                                    stopShimmer();
                                break;
                        }
                });
          /*  }*/
        } catch (Exception e) {
            e.printStackTrace();
        }


        binding.storeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0)
                    Utilities.hideKeyBoard(context);
                if (isLoading && isLastItemDisplaying(recyclerView)) {
                    page++;
                    hitGetCourseListService();
                }
            }

        });

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                searchKey = newText;
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSearchOpen = true;
                searchKey = newText;
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        hitApiWithSearch(newText);
                    }
                };
                searchHandler.postDelayed(searchRunnable, 400);
                return false;
            }
        });
    }

    private void hitApiWithSearch(String newText) {
        if (newText != null && !newText.isEmpty()) {
            searchKey = newText;
            if (Utilities.checkInternet(getContext())) {

                page = 1;
                hitGetCourseListService();

            } else {
                Utilities.makeToast(context, getString(R.string.internet_connection_error));
            }
        } else if (TextUtils.isEmpty(newText)) {
            searchKey = "";
            searchView.clearFocus();
            page = 1;
            hitGetCourseListService();
        }

    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }

    private void setBatchListAdapter(Result result) {

//        if (result.getOtherCourses().getData() != null && result.getOtherCourses().getData().size() > 0)
//            PreferenceHandler.writeString(context, PreferenceHandler.FEATURED_COURSE, new Gson().toJson(result.getOtherCourses().getData()));
//        Log.d("CourseFragment", "setBatchListAdapter: " + new Gson().toJson(result.getOtherCourses().getData()));

        if (!isFromMyCourses) {
            storeListAdapter.updateList(result.getOtherCourses().getData());
            if (pageLength * page >= result.getOtherCourses().getTotal())
                isLoading = false;
        } else {
            storeListAdapter.updateList(result.getPurchasedCourses().getData());
            if (pageLength * page >= result.getPurchasedCourses().getTotal())
                isLoading = false;
        }
        if (binding.swipeRefreshLayout.isRefreshing())
            binding.swipeRefreshLayout.setRefreshing(false);
    }

    private void stopShimmer() {
        if (binding.swipeRefreshLayout.isRefreshing())
            binding.swipeRefreshLayout.setRefreshing(false);
        else {
            if (binding.getProgress())
                binding.setProgress(false);
            if (binding.getFlag())
                binding.setFlag(false);
            binding.shimmerLayout.stopShimmer();
        }

    }

    private void hitGetCourseListService() {
        once = false;
        Map<String, String> map = new HashMap<>();
        map.put("pageLength", pageLength + "");
        map.put("page", page + "");
        if (!TextUtils.isEmpty(searchKey))
            map.put("search", searchKey);
        viewModel.getList(context, map, isFromMyCourses);
        if (binding.swipeRefreshLayout.isRefreshing())
            binding.swipeRefreshLayout.setRefreshing(false);
        if (isLoading) {
            isLoading = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG ", "onActivityResult: " + requestCode);
        if (requestCode == 1 && data != null) {
            boolean isLikeChanged = data.getBooleanExtra("clicked", false);
            //  boolean isFromMyCourses = data.getBooleanExtra("isFromMyCourses", false);
            if (isLikeChanged) {
                page = 1;
                hitGetCourseListService();

            }
        }
    }


}

