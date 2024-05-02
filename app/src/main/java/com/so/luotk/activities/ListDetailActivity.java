package com.so.luotk.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.so.luotk.scoppedStorage.utils.PickerOptions;
import com.so.luotk.scoppedStorage.Picker;
import com.so.luotk.activities.adminrole.SelectStudentsActivity;
import com.so.luotk.adapter.adminrole.SelectedAttachmentItemsAdapter;
import com.so.luotk.api.APIInterface;
import com.so.luotk.api.ApiUtils;
import com.so.luotk.customviews.ProgressView;
import com.so.luotk.databinding.FragmentListDetailBinding;
import com.so.luotk.databinding.LayoutDialogOptionChooserBinding;
import com.so.luotk.fragments.adminrole.adminbatches.AdminAssignmentTestVideoFragment;
import com.so.luotk.fragments.batches.AssignmentTestVideoListFragment;
import com.so.luotk.models.output.Data;
import com.so.luotk.models.output.GetBatchSubmitAssignmentTestResponse;
import com.so.luotk.utils.FileUtils;
import com.so.luotk.utils.ImageRealPath;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.RealPathUtil;
import com.so.luotk.utils.Utilities;
import com.so.luotk.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static com.so.luotk.scoppedStorage.Picker.PICKED_MEDIA_LIST;
import static com.so.luotk.scoppedStorage.Picker.PICKER_OPTIONS;
import static com.so.luotk.scoppedStorage.Picker.REQUEST_CODE_PICKER;

public class ListDetailActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static String[] storage_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };


    private static final int PDF_VIEW_REQUEST = 1010;
    private static final int UPDATE_ASSIGNMENT_STATUS = 1011;
    private static final int DOC_CREATE = 2222;
    private final String[] PERM = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final String[] PERM_NEW = {Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};

    private final static int PERMISSION_ALL = 4;
    private final static int PERMISSION_ALL_NEW = 959;
    private FragmentListDetailBinding binding;
    private Toolbar toolbar;
    private TextView toolbarCustomTitle;
    private String topicName, isFrom, testDuration;
    private MaterialCardView cardViewAssignment, cardViewTest;
    private LinearLayout cardViewNotes, cardViewAttachment;
    //private boolean isFrom;
    private View mainLayout/*, btnSubmitAnswer*/, addAttachmentLayout, addAnswersLayout;
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private TextView tvTestDuration, tvdate, tvTime, tvNotes, tvTopicName, tvTestTopicName, tvTestDate, tvTestMonth, tvSubmitted, tvtestSubmitted;
    private MaterialButton tvSubmitAnswer;
    private boolean isSubmitClicked = false;
    private static final int PICKFILE_RESULT_CODE = 2;
    private String notes, date, time, selectedAttachment, spinnerSelectedAttachment, batchId, assignmentId, assignment;
    private ProgressView mProgressDialog;
    private APIInterface apiInterface;
    private ArrayList<String> attachmentList;
    private int status;
    private String attachmentName;
    private String downloadResult;
    private File file;
    private boolean isDocSelected, isPdfSelected;
    private List<String> allFilePaths;
    private SelectedAttachmentItemsAdapter attachmentsAdapter, teacherAttachmentsAdapter;
    private final int MAX_ATTACHMENT_COUNT = 10;
    private final ArrayList<Uri> photoPaths = new ArrayList<>();
    private final ArrayList<Uri> docPaths = new ArrayList<>();
    private Dialog mDialog;
    private String optionClicked;
    private File cameraPhotoFile;
    private boolean isFromBatch;
    private RecyclerView attachmentsRecyclerView, teacherRecyclerView;
    private long mLastClickTime = 0;
    private String userType, assignmentData, itemPosition;
    private ArrayList<Data> allItems = new ArrayList<>();
    private boolean updateAssignment = false;
    PickerOptions pickerOptions;
    ArrayList<String> testImages = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = FragmentListDetailBinding.inflate(getLayoutInflater());
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(binding.getRoot());
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        setToolbar();
        setupUI();
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbarCustomTitle = findViewById(R.id.tv_toolbar_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.putExtra(PreferenceHandler.IS_DATA_SUBMITTED, true);
                intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                setResult(AssignmentTestVideoListFragment.REQUEST_UPDATE_SUBMIT_STATUS, intent);
                finish();*/
                onBackPressed();
            }
        });

        toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(this));

        if (getIntent() != null) {
            topicName = getIntent().getStringExtra(PreferenceHandler.TOPIC_NAME);
            isFrom = getIntent().getStringExtra(PreferenceHandler.IS_FROM);
            notes = getIntent().getStringExtra(PreferenceHandler.NOTES);
            selectedAttachment = getIntent().getStringExtra(PreferenceHandler.ATTACHMENT);
            date = getIntent().getStringExtra(PreferenceHandler.DATE);
            time = getIntent().getStringExtra(PreferenceHandler.TIME);
            batchId = getIntent().getStringExtra(PreferenceHandler.BATCH_ID);
            assignmentId = getIntent().getStringExtra(PreferenceHandler.ASSIGNMENT_ID);
            testDuration = getIntent().getStringExtra(PreferenceHandler.TEST_DURATION);
            if (isFrom.equalsIgnoreCase("test"))
                isFromBatch = getIntent().getBooleanExtra(PreferenceHandler.IS_FROM_BATCH, false);

        }
        if (topicName != null) {
            toolbarCustomTitle.setText(topicName);
        }
        /*setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.SRC_ATOP);
        this.getSupportActionBar().setHomeAsUpIndicator(upArrow);*/


    }

    @Override
    public void onBackPressed() {
        Log.d("TAG", "onBackPressed: " + updateAssignment);
        if (updateAssignment) {
            updateAssignment = false;
            Intent intent = new Intent();
            intent.putExtra(PreferenceHandler.IS_DATA_SUBMITTED, true);
            intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
            setResult(AdminAssignmentTestVideoFragment.REQUEST_UPDATE_SUBMIT_STATUS, intent);
            finish();
        } else {
            super.onBackPressed();
        }
//
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupUI() {
        userType = PreferenceHandler.readString(ListDetailActivity.this, PreferenceHandler.USER_TYPE, "");
        attachmentList = new ArrayList<>();
        apiInterface = ApiUtils.getApiInterface();
        mainLayout = binding.itemLayout;
        tvTopicName = binding.tvTopicName;
        tvTestDuration = binding.tvDuration;
        tvTestTopicName = binding.tvTestTopicName;
        //   imgSubmit = findViewById(R.id.img_submit_icon);
        tvSubmitAnswer = binding.tvSubmitAnswer;
        cardViewAssignment = binding.cardViewAssignment;
        cardViewAttachment = binding.cardViewAttachment;
        cardViewNotes = binding.cardViewNotes;
        cardViewTest = binding.cardViewTest;
        tvdate = binding.tvAssignDate;
        tvTime = binding.tvAssignTime;
        tvNotes = binding.tvNotes;
        tvTestDate = binding.tvTestDate;
        tvTestMonth = binding.tvTestMonth;
        tvSubmitted = binding.tvSubmitted;
        tvtestSubmitted = binding.tvTestSubmitted;
        addAnswersLayout = binding.layoutAddAnswers;
        // imgAttachmentIcon = findViewById(R.id.img_pdf_icon);
//        btnSubmitAnswer = binding.btnSubmitAnswer;
        attachmentsRecyclerView = binding.recylerAttachedItems;
        teacherRecyclerView = binding.recylerTeacherAttachedItems;
        addAttachmentLayout = binding.layoutSelectAttachment;
        tvSubmitAnswer.setOnClickListener(this);
        status = getIntent().getIntExtra(PreferenceHandler.STATUS, 0);
        attachmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teacherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        allFilePaths = new ArrayList<>();

        pickerOptions = PickerOptions.init();
        pickerOptions.setAllowFrontCamera(true);
        pickerOptions.setMaxCount(10);
        pickerOptions.setExcludeVideos(true);
        pickerOptions.setPreSelectedMediaList(testImages);

        PreferenceHandler.writeBoolean(this, PreferenceHandler.EXTERNAL_APP, false);
        if (isFrom.equalsIgnoreCase("test")) {
            cardViewAssignment.setVisibility(View.GONE);
            cardViewTest.setVisibility(View.VISIBLE);
            if (date != null) {
                binding.tvTestAssignDate.setText(Utilities.getFormattedDate(date, false) + ",");
                tvTestDate.setText(Utilities.getOnlyDate(date));
                tvTestMonth.setText(Utilities.getOnlyMonth(date));
            }
            if (time != null) {
                binding.tvTestAssignTime.setText(time);
            }
            if (testDuration != null) {
                tvTestDuration.setText(getString(R.string.duration) + testDuration);
            }
        } else {
            cardViewAssignment.setVisibility(View.VISIBLE);
            cardViewTest.setVisibility(View.GONE);
            if (date != null && time != null) {
                tvdate.setText(Utilities.getFormattedDate(date, false));
                tvTime.setText(time);
            }
        }

        if (topicName != null) {
            if (isFrom.equalsIgnoreCase("assignment")) {
                tvTopicName.setText(topicName);

             /*   Data changeData = new Data();
                changeData.setTopic("dddddd");*/
                try {
                    Intent intent = getIntent();
                    String assignMentGson = intent.getStringExtra("assignmentGson");
                    Type listOfItems = new TypeToken<List<Data>>() {
                    }.getType();
                    allItems = new Gson().fromJson(assignMentGson, listOfItems);
                    itemPosition = getIntent().getStringExtra("position");
                    if (getIntent().getStringExtra("assignmentData") != null) {
                        assignmentData = getIntent().getStringExtra("assignmentData");

                        binding.editIcon.setVisibility(View.GONE);
                        binding.editIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ListDetailActivity.this, SelectStudentsActivity.class);
                                intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                                intent.putExtra("title", getString(R.string.edit_assignment));
                                intent.putExtra(PreferenceHandler.BATCH_ID, batchId);
                                intent.putExtra("assignmentData", assignmentData);
                                intent.putExtra("edit", "true");
                                startActivityForResult(intent, UPDATE_ASSIGNMENT_STATUS);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                tvTestTopicName.setText(topicName);
            }

        }

        if (notes != null) {
            tvNotes.setText(notes);
        } else {
            cardViewNotes.setVisibility(View.GONE);
        }

        if (selectedAttachment != null) {
            convertStringToJson(selectedAttachment);
        } else {
            cardViewAttachment.setVisibility(View.GONE);
        }

        if (status == 0) {

            if (userType != null && !userType.equals("")) {
                if (userType.equals("organisation") || userType.equals("faculty")) {
                    addAnswersLayout.setVisibility(View.GONE);
                    tvSubmitted.setVisibility(View.GONE);
                } else {
                    addAnswersLayout.setVisibility(View.VISIBLE);
                    tvSubmitted.setVisibility(View.VISIBLE);
                }
            }
//            addAnswersLayout.setVisibility(View.VISIBLE);
            addAttachmentLayout.setVisibility(View.VISIBLE);
//            tvSubmitAnswer.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorGrayBlackGray), PorterDuff.Mode.SRC_ATOP);
            tvSubmitAnswer.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGrayBlackGray));
            tvSubmitAnswer.setTextColor(ContextCompat.getColor(this, R.color.black));
            if (isFrom.equalsIgnoreCase("assignment"))
                tvSubmitAnswer.setText(R.string.submit_assign);
            else
                tvSubmitAnswer.setText(R.string.submit_tes);
            tvtestSubmitted.setVisibility(View.VISIBLE);
            //    imgSubmit.setVisibility(View.VISIBLE);
            tvtestSubmitted.setText(R.string.pending);
            tvtestSubmitted.setTextColor(ContextCompat.getColor(this, R.color.red));

            tvSubmitted.setText(R.string.pending);
            tvSubmitted.setTextColor(ContextCompat.getColor(this, R.color.red));
//            btnSubmitAnswer.setForeground(null);
            addAttachmentLayout.setOnClickListener(this);


        } else {

            if (userType != null && !userType.equals("")) {
                if (userType.equals("organisation") || userType.equals("faculty")) {
                    addAnswersLayout.setVisibility(View.GONE);
                    tvSubmitted.setVisibility(View.GONE);
                } else {
                    addAnswersLayout.setVisibility(View.GONE);
                    tvSubmitted.setVisibility(View.VISIBLE);
                }
            }
            tvtestSubmitted.setVisibility(View.VISIBLE);
//            addAnswersLayout.setVisibility(View.GONE);
            addAttachmentLayout.setVisibility(View.GONE);
            tvSubmitAnswer.setVisibility(View.GONE);
//            btnSubmitAnswer.setForeground(null);
            tvSubmitAnswer.setOnClickListener(null);
        }

        if (userType != null && !userType.equals("")) {
            Log.d("TAG", "setupUI: " + userType);
            if (userType.equals("organisation") || userType.equals("faculty")) {
                binding.btnSubmitAnswer.setVisibility(View.GONE);
            } else {
                binding.btnSubmitAnswer.setVisibility(View.VISIBLE);
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layout_select_attachment:
                isSubmitClicked = true;
                if (!hasPermissions(ListDetailActivity.this, permissions())) {
                    ActivityCompat.requestPermissions(ListDetailActivity.this,
                            permissions(),
                            PERMISSION_ALL_NEW);
                    return;
                }

                Log.d("PErm", "onClick:7 ");
                if (allFilePaths.size() < 10) {
                    openOptionChooserDialog();
                } else
                    Toast.makeText(ListDetailActivity.this, getString(R.string.cannot_attach_more), Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_submit_answer:
                if (!hasPermissions(ListDetailActivity.this, permissions())) {
                    ActivityCompat.requestPermissions(ListDetailActivity.this,
                            permissions(),
                            PERMISSION_ALL_NEW);
                    return;
                }
                if (allFilePaths.isEmpty()) {
                    Utilities.makeToast(this, getString(R.string.attachmnet_min_item));
                } else
                    openConfirmSubmitDialog();
                break;
            default:
                break;
        }
    }
    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }

    public void openOptionChooserDialog() {
        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_option_chooser, null, false);
        LayoutDialogOptionChooserBinding dialogBinding = LayoutDialogOptionChooserBinding.bind(view);
        mDialog.setContentView(dialogBinding.getRoot());

//        mDialog.setContentView(R.layout.layout_dialog_option_chooser);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        if (!isFinishing())
            mDialog.show();
        mDialog.setCancelable(true);
        // mDialog.getWindow().setAttributes(lp);
        /*View camera = mDialog.findViewById(R.id.camera);
        View gallery = mDialog.findViewById(R.id.gallery);
        View attachment = mDialog.findViewById(R.id.layout_attachment);
        View btnCancel = mDialog.findViewById(R.id.cancel);*/

        if (PreferenceHandler.readBoolean(ListDetailActivity.this, PreferenceHandler.ISSTATICLOGIN, false)) {
            dialogBinding.layoutAttachment.setVisibility(GONE);
        } else {
            dialogBinding.layoutAttachment.setVisibility(View.VISIBLE);
        }

        dialogBinding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasPermissions(ListDetailActivity.this, permissions())) {
                    ActivityCompat.requestPermissions(ListDetailActivity.this, permissions(), PERMISSION_ALL_NEW);
                    return;
                }
                optionClicked = "gallery";

                if (Utilities.isCameraPermitted(ListDetailActivity.this)) {
                    if (allFilePaths.size() < 10) {
                        Intent mPickerIntent = new Intent(ListDetailActivity.this, Picker.class);
                        mPickerIntent.putExtra(PICKER_OPTIONS, pickerOptions);
                        mPickerIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(mPickerIntent, REQUEST_CODE_PICKER);
                    } else {
                        Toast.makeText(ListDetailActivity.this, getString(R.string.cannot_attach_more), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Utilities.requestCameraPermission(ListDetailActivity.this, 2000);
                }
            }
        });
        dialogBinding.layoutAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasPermissions(ListDetailActivity.this, permissions())) {
                    ActivityCompat.requestPermissions(ListDetailActivity.this, permissions(), PERMISSION_ALL_NEW);
                    return;
                }
                optionClicked = "viewattachment";
                if (allFilePaths.size() < 10) {
                    createFile();
//                        openDocPicker();
                } else {
                    Toast.makeText(ListDetailActivity.this, getString(R.string.cannot_attach_more), Toast.LENGTH_SHORT).show();
                }

                /*if (Utilities.isStoragePermissionRequired(ListDetailActivity.this)) {
                    Utilities.requestStoragePermission(ListDetailActivity.this, 1000);
                } else {
                    if (allFilePaths.size() < 10) {
                        createFile();
//                        openDocPicker();
                    } else {
                        Toast.makeText(ListDetailActivity.this, getString(R.string.cannot_attach_more), Toast.LENGTH_SHORT).show();
                    }
                }*/
            }
        });

        if (!isFinishing())
            mDialog.show();

    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Return", "hasPermissions: ");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ListDetailActivity.this, permission)){
                    } else {
                        ActivityCompat.requestPermissions(ListDetailActivity.this,
                                permissions(),
                                PERMISSION_ALL_NEW);
                    }
                    return false;
                }
            }
        }
        Log.d("Return", "hasPermissions: return");
        return true;
    }

    private void createFile() {
        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/pdf"
                };
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        startActivityForResult(intent, DOC_CREATE);
    }

    private void setAttachmentsAdapter() {
        if (!allFilePaths.isEmpty()) {
            Set<String> set = new HashSet<>(allFilePaths);
            allFilePaths.clear();
            allFilePaths.addAll(set);
            attachmentsAdapter = new SelectedAttachmentItemsAdapter(allFilePaths, false, position -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                setUpAttachmentView(allFilePaths.get(position));
            }, () -> {
                if (attachmentsAdapter.getList().size() == 0) {
//                    tvSubmitAnswer.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorGrayBlackGray), PorterDuff.Mode.SRC_ATOP);
                    tvSubmitAnswer.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGrayBlackGray));
                    tvSubmitAnswer.setTextColor(ContextCompat.getColor(this, R.color.black));

                } else {
//                    tvSubmitAnswer.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.blue_main), PorterDuff.Mode.SRC_ATOP);
                    tvSubmitAnswer.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_main));
                    tvSubmitAnswer.setTextColor(ContextCompat.getColor(this, R.color.textColorWhite));
                }
                if (attachmentsAdapter.getList().size() >= 10)
                    addAttachmentLayout.setVisibility(View.GONE);
                else addAttachmentLayout.setVisibility(View.VISIBLE);
            });
            if (attachmentsAdapter.getList().size() >= 10)
                addAttachmentLayout.setVisibility(View.GONE);
            else addAttachmentLayout.setVisibility(View.VISIBLE);
            attachmentsRecyclerView.setVisibility(View.VISIBLE);
            attachmentsRecyclerView.setAdapter(attachmentsAdapter);
//            tvSubmitAnswer.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.blue_main), PorterDuff.Mode.SRC_ATOP);
            tvSubmitAnswer.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_main));
            tvSubmitAnswer.setTextColor(ContextCompat.getColor(this, R.color.textColorWhite));
            tvSubmitAnswer.setClickable(true);

        } else {
            attachmentsRecyclerView.setVisibility(View.GONE);
        }
    }

    private void openConfirmSubmitDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.do_you_really_want_to_submit);
        alertBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utilities.checkInternet(ListDetailActivity.this)) {
                    if (assignmentId != null && allFilePaths.size() > 0)

                        Log.d("TAG", "onClick: " + allFilePaths.toString());
                    hitSubmitAssignmentTestService(allFilePaths);
                } else {
                    Snackbar.make(mainLayout, "Check internet connection", Snackbar.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = alertBuilder.create();
        if (!isFinishing())
            dialog.show();
    }


    private void setUpAttachmentView(String path) {
//        if (Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
            String filePath = "";
            if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                if (path.contains(".pdf") || path.contains(".doc")) {
                    filePath = path.replace("/data/user/0/" + getPackageName() + "/cache/", "");
                } else {
                    filePath = path/*.replace("/storage/emulated/0", "")*/;
                }
            } else {
                if (path.contains(".pdf") || path.contains(".doc")) {
                    if (path.contains(getPackageName())) {
                        filePath = path.replace("/data/user/0/" + getPackageName() + "/cache/", "");
                    } else {
                        filePath = path/*.replace("/storage/emulated/0", "")*/;
                    }
                } else {
                    filePath = path/*.replace("/storage/emulated/0", "")*/;
                }
            }

//            String filePath = path.replace("/storage/emulated/0/Download/", "");
            String lowerPath = path.toLowerCase();
            Log.d("TAG", "setUpAttachmentView: " + filePath +" : "+ lowerPath);
            if (lowerPath.contains(".jpg") || lowerPath.contains(".jpeg") || lowerPath.contains(".png") || lowerPath.contains(".gif")) {
                startActivity(new Intent(this, ViewAttachmentActivity.class).putExtra("isFrom", "adminAttachment")
                        .putExtra(PreferenceHandler.PDF_NAME, filePath)
                        .putExtra(PreferenceHandler.FILE_NAME, new File(filePath).getName())
                );
            } else if (lowerPath.contains(".pdf")) {
                Log.d("FileName", "setUpAttachmentView: " + filePath);
                startActivity(new Intent(this, PDFViewNew.class)
                        .putExtra(PreferenceHandler.PDF_NAME, filePath)
                        .putExtra(PreferenceHandler.DOC_URL, path)
                        .putExtra(PreferenceHandler.FILE_NAME, filePath)
                        .putExtra(PreferenceHandler.FROM_LOCAL, "true")
                );
            } else {
                try {
                    MediaScannerConnection.scanFile(this,
                            new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String pathhh, Uri uri) {
                                    Log.e("ExternalStorageA", "Scanned " + pathhh + ":");
                                    Log.e("ExternalStorageA", "-> uri=" + uri);
                                    try {
                                        Log.d("TAG", "onScanCompleted: " + new File(pathhh).exists());

                                        openDocFile(ListDetailActivity.this, new File(pathhh));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
//                                openFile(CreateAssignmentTestActivity.this, new File(path), uri);

                                }
                            });

                    Log.d("TAG", "setUpAttachmentView: " + filePath);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "onActivityResult: " + requestCode + " : " + resultCode);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        switch (requestCode) {
            case REQUEST_CODE_PICKER:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        if (data.getStringArrayListExtra(PICKED_MEDIA_LIST).size() > 0) {
                            for (int i = 0; i < data.getStringArrayListExtra(PICKED_MEDIA_LIST).size(); i++) {

                                if (data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i).contains("content:")) {
                                    String imageGalleryPath = ImageRealPath.getUriRealPath(ListDetailActivity.this, Uri.parse(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i)));
                                    testImages.add(imageGalleryPath);
                                    if (allFilePaths.size() < 11) {
                                        allFilePaths.add(imageGalleryPath);
                                    }
                                } else {
                                    testImages.add(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i));
                                    String imageCameraPath = ImageRealPath.getUriRealPath(ListDetailActivity.this, Uri.parse(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i)));
                                    Log.d("TAG", "onActivityResult: " + imageCameraPath);
                            /*String path = Uri.parse(data.getStringArrayListExtra(PICKED_MEDIA_LIST).get(i)).toString(); // "file:///mnt/sdcard/FileName.mp3"

                            File file = new File(path);*/
                                    if (allFilePaths.size() < 11) {
                                        allFilePaths.add(imageCameraPath);
                                    }
                                }

                            }
                            setAttachmentsAdapter();
                        }
                    }
                }
                break;
            case PDF_VIEW_REQUEST:
                if (data != null) {
                    String pdf_name = data.getStringExtra(PreferenceHandler.PDF_NAME);
                    if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                        String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        File myFile = new File(filepath, pdf_name);
                        Log.d("TAG", "onActivityResult: " + myFile.getAbsolutePath());
                        if (!PreferenceHandler.readBoolean(this, PreferenceHandler.FILE_DOWNLOADED, false)) {
                            myFile.delete();
                        }
                    } else {
                        File myFilesDir = new File(Environment
                                .getExternalStorageDirectory().getAbsolutePath()
                        );
                        File myFile = new File(myFilesDir, pdf_name);
                        if (!PreferenceHandler.readBoolean(this, PreferenceHandler.FILE_DOWNLOADED, false)) {
                            myFile.delete();
                        }
                    }
                }
                break;


            case UPDATE_ASSIGNMENT_STATUS:
                if (resultCode == RESULT_OK) {
                    boolean isDataSubmitted = data.getBooleanExtra(PreferenceHandler.IS_DATA_SUBMITTED, false);
                    isFrom = data.getStringExtra(PreferenceHandler.IS_FROM);
                    Log.d("TAG", "onActivityResult: " + isDataSubmitted + " : " + isFrom);

                    if (isFrom.equalsIgnoreCase("test")) {
                        cardViewAssignment.setVisibility(View.GONE);
                        cardViewTest.setVisibility(View.VISIBLE);
                        if (date != null) {
                            binding.tvTestAssignDate.setText(Utilities.getFormattedDate(date, false) + ",");
                            tvTestDate.setText(Utilities.getOnlyDate(date));
                            tvTestMonth.setText(Utilities.getOnlyMonth(date));
                        }
                        if (time != null) {
                            binding.tvTestAssignTime.setText(time);
                        }
                        if (testDuration != null) {
                            tvTestDuration.setText(getString(R.string.duration) + testDuration);
                        }
                        tvTestTopicName.setText(topicName);
                    } else {
                        cardViewAssignment.setVisibility(View.VISIBLE);
                        cardViewTest.setVisibility(View.GONE);
                        date = data.getStringExtra(PreferenceHandler.DATE);
                        time = data.getStringExtra(PreferenceHandler.TIME);
                        topicName = data.getStringExtra(PreferenceHandler.TOPIC_NAME);
                        notes = data.getStringExtra(PreferenceHandler.NOTES);
                        selectedAttachment = data.getStringExtra(PreferenceHandler.ATTACHMENT);

                        Log.d("EditData", "onActivityResultList: \nisDataSubmitted: " + isDataSubmitted
                                + "\nDate: " + date
                                + "\ntime: " + time
                                + "\ntopicName: " + topicName
                                + "\nnotes: " + notes
                                + "\nattachemnts: " + selectedAttachment
                        );


                        Log.d("TAG", "onActivityResult: " + selectedAttachment);

                        if (date != null && time != null && topicName != null) {
                            allItems.get(Integer.parseInt(itemPosition)).setTopic(topicName);

                            SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy");
                            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date oneWayTripDate = input.parse(date);                 // parse input
                                String finaldata = output.format(oneWayTripDate);  // format output
                                allItems.get(Integer.parseInt(itemPosition)).setSubmitDate(finaldata);
                                tvdate.setText(Utilities.getFormattedDate(finaldata, false));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            tvTopicName.setText(topicName);
                            toolbarCustomTitle.setText(topicName);
                            tvTime.setText(time);
                            allItems.get(Integer.parseInt(itemPosition)).setSubmitTime(time);
                            attachmentList.clear();
                            if (selectedAttachment != null) {
                                String replaceString = selectedAttachment.replaceAll("\\[", "").replaceAll("\\]", "");
                                Log.d("TAG", "replaceString: " + replaceString.trim().length());
                                if (replaceString.trim().length() > 0) {
                                    cardViewAttachment.setVisibility(View.VISIBLE);
                                    if (replaceString.contains(",")) {
                                        String[] splitArray = replaceString.split(",");
                                        for (int i = 0; i < splitArray.length; i++) {
                                            attachmentList.add(splitArray[i]);
                                        }
                                        setUpTeacherAdapter(attachmentList);
                                    } else {
                                        attachmentList.add(replaceString);
                                        setUpTeacherAdapter(attachmentList);
                                    }
                                } else {
                                    cardViewAttachment.setVisibility(View.GONE);
                                }
                            } else {
                                cardViewAttachment.setVisibility(View.GONE);
                            }
                            allItems.get(Integer.parseInt(itemPosition)).setAttachment(attachmentList.toString());
                        }

                        tvNotes.setText(notes);
                        allItems.get(Integer.parseInt(itemPosition)).setNotes(notes);
                        allItems.get(Integer.parseInt(itemPosition)).setIsSms(data.getStringExtra(PreferenceHandler.IS_SMS));

                        assignmentData = new Gson().toJson(allItems.get(Integer.parseInt(itemPosition)));
                        updateAssignment = true;
                        Log.d("TAG", "onActivityResult: " + allItems.get(Integer.parseInt(itemPosition)).getTopic());
                    }
                }
                break;

            case DOC_CREATE:
                Log.d("DOC", "onActivityResult: " + resultCode + " : " + data);
                if (resultCode == RESULT_OK && data != null) {
                        if(null != data.getClipData()) { // checking multiple selection or not
                            for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                                Uri uri = data.getClipData().getItemAt(i).getUri();
                                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                DocumentFile documentFile = DocumentFile.fromSingleUri(ListDetailActivity.this, uri);
                                File file = null;
                                try {
                                    file = FileUtils.from(ListDetailActivity.this, documentFile.getUri());
                                    Log.d("TAG", "onActivityResult: " + file.getAbsolutePath());
                                    allFilePaths.add(file.getAbsolutePath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        } else {
                            Uri uri = data.getData();
                            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            DocumentFile documentFile = DocumentFile.fromSingleUri(ListDetailActivity.this, uri);
                            try {
                                File file = FileUtils.from(ListDetailActivity.this, documentFile.getUri());
                                Log.d("TAG", "onActivityResult: " + file.getAbsolutePath() + " : " + file.exists());

                                allFilePaths.add(file.getAbsolutePath());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        setAttachmentsAdapter();
                }
                break;
        }

    }

    private String getRealPathFromURI(Uri contentURI) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(contentURI);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;

    }

    public Uri getCameraImageUri(Context inContext) {
        Bitmap rotatedBitmap = null;
        try {
            Bitmap inImage = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(cameraPhotoFile));
            rotatedBitmap = Utilities.rotateBitmap(inImage, RealPathUtil.getRealPath(ListDetailActivity.this, Uri.fromFile(cameraPhotoFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), rotatedBitmap, "Image" + Utilities.getUniqueCode(), null);
        return Uri.parse(path);
    }

    public String getExt(String filePath) {
        int strLength = filePath.lastIndexOf(".");
        if (strLength > 0)
            return filePath.substring(strLength + 1).toLowerCase();
        return null;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) throws NullPointerException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Image" + Utilities.getUniqueCode(), null);
        return Uri.parse(path);
    }


    private void setupViewAttachmentClick(String selectedAttachment) {
        Log.d("TAG", "setupViewAttachmentClick: " + selectedAttachment);
        if (!hasPermissions(ListDetailActivity.this, permissions())) {
            ActivityCompat.requestPermissions(ListDetailActivity.this, permissions(), PERMISSION_ALL_NEW);
            return;
        }
            File myFilesDir = null;
            File myFile = null;
            String lowerpath = selectedAttachment.toLowerCase();
            if (Build.VERSION.SDK_INT > PreferenceHandler.MEDIA_STORE_VERSION) {
                if (lowerpath.contains(".pdf") || lowerpath.contains(".doc")) {
                    myFilesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    myFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), selectedAttachment);
                } else {
                    myFilesDir = new File(Environment
                            .getExternalStorageDirectory().getAbsolutePath()
                            + "/Download/"+ getString(R.string.app_name).replaceAll(" ", "") + "Files");
                    myFile = new File(myFilesDir, selectedAttachment);
                }
            } else {
                myFilesDir = new File(Environment
                        .getExternalStorageDirectory().getAbsolutePath()
                        + "/Download/"+ getString(R.string.app_name).replaceAll(" ", "") + "Files");
                myFile = new File(myFilesDir, selectedAttachment);
            }


            if (lowerpath.contains(".pdf")) {

                String dirPath = Utilities.getRootDirPath(ListDetailActivity.this);
                File downloadedFile = new File(dirPath, selectedAttachment);
                Intent in = new Intent(ListDetailActivity.this, PDFViewNew.class);
                in.putExtra(PreferenceHandler.FILE_NAME, selectedAttachment);
                in.putExtra(PreferenceHandler.PDF_FILE_CACHE, selectedAttachment);
                Log.d("TAG", "newStoragePermission: " + downloadedFile.getAbsolutePath());
                if (downloadedFile.exists()) {
                    in.putExtra(PreferenceHandler.DOC_URL,downloadedFile.getAbsolutePath());
                    in.putExtra(PreferenceHandler.FROM_LOCAL, "true");
                } else {
                    String docUrl = "";
                    docUrl = "https://web.smartowls.in/attachment/" + selectedAttachment;
                    in.putExtra(PreferenceHandler.DOC_URL, docUrl);
                    in.putExtra(PreferenceHandler.FROM_LOCAL, "false");
                }
                startActivity(in);

                /*String docUrl = "https://web.smartowls.in/attachment/" + selectedAttachment;

                Intent in = new Intent(ListDetailActivity.this, PDFViewNew.class);
                in.putExtra(PreferenceHandler.DOC_URL,docUrl);
                in.putExtra(PreferenceHandler.FILE_NAME,selectedAttachment);
                startActivity(in);*/

                /*if (myFile.exists()) {
                    myFile.delete();
                    downloadAttachments(selectedAttachment, false, true);
                    *//*if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {

                        MediaScannerConnection.scanFile(ListDetailActivity.this,
                                new String[] {myFile.getAbsolutePath() }, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.e("ExternalStorageP", "Scanned " + path + ":");
                                        Log.e("ExternalStorageP", "-> uri=" + uri);
                                        startActivityForResult(new Intent(ListDetailActivity.this, PdfViewerActivity.class).putExtra(PreferenceHandler.PDF_NAME,  selectedAttachment)
                                                .putExtra(PreferenceHandler.PDF_PATH, path)
                                                .putExtra("uri", uri), PDF_VIEW_REQUEST);
                                    }
                                });
                        // only for gingerbread and newer versions
                    } else {
                        startActivityForResult(new Intent(ListDetailActivity.this, PdfViewerActivity.class).putExtra(PreferenceHandler.PDF_NAME, "/Download/"+ getString(R.string.app_name).replaceAll(" ", "") + "Files/" + selectedAttachment), PDF_VIEW_REQUEST);
                    }*//*
                } else {
                    downloadAttachments(selectedAttachment, false, true);
                }*/

                /*if (!myFile.exists()) {
                    downloadAttachments(selectedAttachment, false, true);
                } else {
                    startActivityForResult(new Intent(ListDetailActivity.this, PdfViewerActivity.class).putExtra(PreferenceHandler.PDF_NAME, "/Download/"+ getString(R.string.app_name).replaceAll(" ", "") + "Files/" + selectedAttachment), PDF_VIEW_REQUEST);
                }*/
                //startActivity(PdfViewerActivity.buildIntent(this, "https://web.smartowls.in/attachment/" + selectedAttachment, false, selectedAttachment, "", false));
            } else if (lowerpath.contains(".png")
                    || lowerpath.contains(".jpg") ||
                    selectedAttachment.contains(".jpeg") || selectedAttachment.contains(".gif")) {
              /*  if (!myFile.exists()) {
                    downloadAttachments(selectedAttachment, false, false);
                }*/
                Intent intent = new Intent(this, ViewAttachmentActivity.class);
                intent.putExtra(PreferenceHandler.PDF_NAME, selectedAttachment);
                startActivity(intent);
            } else {

                if (myFile.exists()) {
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {

                            MediaScannerConnection.scanFile(ListDetailActivity.this,
                                    new String[] {myFile.getAbsolutePath() }, null,
                                    new MediaScannerConnection.OnScanCompletedListener() {
                                        public void onScanCompleted(String path, Uri uri) {
                                            Log.e("ExternalStorageQ", "Scanned " + path + ":");
                                            Log.e("ExternalStorageQ", "-> uri=" + uri);
                                            try {
                                                openFile(ListDetailActivity.this, new File(path));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            // only for gingerbread and newer versions
                        } else {
                            try {
                                openFile(ListDetailActivity.this, myFile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    downloadAttachments(selectedAttachment, true, false);
                }

               /* if (myFile.exists()) {
                    try {
                        openFile(ListDetailActivity.this, myFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    downloadAttachments(selectedAttachment, true, false);
                }*/
            }

        //}
    }

    private void downloadAttachments(String attachmentToDownload, boolean isDoc, boolean isPdf) {
        isPdfSelected = isPdf;
        isDocSelected = isDoc;
        DownloadAttachmentFile downloadFile = new DownloadAttachmentFile(ListDetailActivity.this, attachmentToDownload, isDoc);
        downloadFile.execute();
    }


    public class DownloadAttachmentFile extends AsyncTask<Void, Void, String> {
        private final ProgressView mProgressDialog;
        private final Context context;
        private final String filename;
        private final String attachment;

        public DownloadAttachmentFile(Context context, String fileName, boolean isDoc) {
            this.context = context;
            this.filename = fileName;
            this.attachment = filename;
            mProgressDialog = new ProgressView(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            String path = "true";
            HttpURLConnection c;
            if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
                Uri newFileUri = addFileToDownloadsApi29(filename);

                OutputStream outputStream;
                ResponseBody responseBody = downloadFileFromInternet("https://web.smartowls.in/attachment/" + filename);
                try {
                    outputStream = getContentResolver().openOutputStream(newFileUri, "w");
                    outputStream.write(responseBody.bytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("TAG", "doInBackground: " + responseBody);
                path = getMediaStoreEntryPathApi29(newFileUri);

                Log.d("Path", "doInBackground: " + path);

                // only for gingerbread and newer versions
            } else {
                try {
                    URL url = new URL("https://web.smartowls.in/attachment/" + filename);
                    c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setDoOutput(true);
                    c.connect();
                } catch (IOException e1) {
                    return e1.getMessage();
                }

                File myFilesDir = new File(Environment
                        .getExternalStorageDirectory().getAbsolutePath()
                        + "/Download/"+ getString(R.string.app_name).replaceAll(" ", "") + "Files");

                file = new File(myFilesDir, filename);

                if (file.exists()) {
                    file.delete();
                }

                if ((myFilesDir.mkdirs() || myFilesDir.isDirectory())) {
                    try {
                        InputStream is = c.getInputStream();
                        FileOutputStream fos = new FileOutputStream(myFilesDir
                                + "/" + filename);

                        byte[] buffer = new byte[1024];
                        int len1 = 0;
                        while ((len1 = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len1);
                        }
                        fos.close();
                        is.close();

                    } catch (Exception e) {
                        return e.getMessage();
                    }


                } else {
                    return "Unable to create folder";
                }
            }

            return path;
        }


        @Override
        protected void onPostExecute(String result) {
            Log.d("TAG", "onPostExecute: " + result);
            downloadResult = result;
            mProgressDialog.dismiss();

            if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {

                MediaScannerConnection.scanFile(ListDetailActivity.this,
                        new String[] {result }, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.e("ExternalStorageR", "Scanned " + path + ":");
                                Log.e("ExternalStorageR", "-> uri=" + uri);
//                                    openFile(context, file, uri);
                                File fileeee = new File(path);
                                Log.d("TAG", "onScanCompleted: " + fileeee.exists());
                                if (isDocSelected && new File(path) != null) {
                                    try {
                                        openFile(ListDetailActivity.this, fileeee);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

            } else {
                if (result != null && result.equalsIgnoreCase("true")) {
                    //Toast.makeText(context, "Data has been downloaded successfully", Toast.LENGTH_LONG).show();
                    if (isDocSelected && file != null) {
                        try {
                            openFile(ListDetailActivity.this, file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            super.onPostExecute(result);
        }
    }

    private ResponseBody downloadFileFromInternet(String url){
        // We use OkHttp to create HTTP request
        OkHttpClient httpClient = new OkHttpClient();
        okhttp3.Response response = null;
        try {
            response = httpClient.newCall(new Request.Builder().url(url).build()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "downloadFileFromInternet: " + response +" : "+ response.body());
        return response.body();
    }

    private Uri addFileToDownloadsApi29(String filename) {
        Uri collection = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, filename);
        ContentResolver contentResolver =getContentResolver();

        Uri downloadedFileUri = contentResolver.insert(collection, values);
        return downloadedFileUri;
    }

    private String getMediaStoreEntryPathApi29(Uri uri) {

        Cursor cursor = getContentResolver().query(
                uri,
                new String[] {MediaStore.Files.FileColumns.DATA} ,
                null,
                null,
                null
        );
        Log.d("TAG", "getMediaStoreEntryPathApi29: " + cursor);
        while (!cursor.moveToNext()) {
            // Use an ID column from the projection to get
            // a URI representing the media item itself.
            return null;
        }
        Log.d("TAG", "getMediaStoreEntryPathApi29: " + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)));
        return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));

    }

    private void openDocFile(Context context, File url) throws IOException {
        File file = url;
        Uri uri = FileProvider.getUriForFile(context, getPackageName(), url);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getPath()));
        intent.setDataAndType(uri, mimeType);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK /*| Intent.FLAG_GRANT_READ_URI_PERMISSION */ | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        intent = Intent.createChooser(intent, "Open File");
        if (isIntentSafe) {
            // startActivityForResult(intent,101);
            try {
                startActivity(intent);
            } catch (Exception e) {

            }

        } else {
            Snackbar.make(mainLayout, getString(R.string.device_doesnot_support_file), Snackbar.LENGTH_SHORT).show();

        }
        PreferenceHandler.writeBoolean(this, PreferenceHandler.EXTERNAL_APP, false);
        PreferenceHandler.writeString(this, PreferenceHandler.EXTERNAL_DOC, url.getAbsolutePath());

    }

    private void openAttachmentFile(Context context, String path) throws IOException {
        // Create URI
        Uri uri;
        if (android.os.Build.VERSION.SDK_INT >= PreferenceHandler.MEDIA_STORE_VERSION) {
            uri = Uri.parse("uriFrom");
        } else {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + path);
            uri = Uri.fromFile(file);
        }


        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getPath()));
        intent.setDataAndType(uri, mimeType);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PackageManager packageManager = getPackageManager();

        List resolvedActivityList;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resolvedActivityList =
                    packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        } else {
            resolvedActivityList = packageManager.queryIntentActivities(intent, 0);

        }
        boolean isIntentSafe = resolvedActivityList.size() > 0;
        intent = Intent.createChooser(intent, "Open File");
        if (isIntentSafe) {
            // startActivityForResult(intent,101);
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.device_doesnot_support_file), Toast.LENGTH_SHORT).show();

        }
        PreferenceHandler.writeBoolean(this, PreferenceHandler.EXTERNAL_APP, false);
        PreferenceHandler.writeString(this, PreferenceHandler.EXTERNAL_DOC, new File(path).getAbsolutePath());

    }

    private void openFile(Context context, File url) throws IOException {
        File file = url;
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getPath()));
        intent.setDataAndType(uri, mimeType);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK /*| Intent.FLAG_GRANT_READ_URI_PERMISSION */ | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        intent = Intent.createChooser(intent, "Open File");
        if (isIntentSafe) {
            // startActivityForResult(intent,101);
            try {
                startActivity(intent);
            } catch (Exception e) {

            }

        } else {
            Snackbar.make(mainLayout, getString(R.string.device_doesnot_support_file), Snackbar.LENGTH_SHORT).show();

        }
        PreferenceHandler.writeBoolean(this, PreferenceHandler.EXTERNAL_APP, true);
        PreferenceHandler.writeString(this, PreferenceHandler.EXTERNAL_DOC, url.getAbsolutePath());

    }

    /**
     * callback method for permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (Utilities.verifyPermissions(grantResults)) {
                if (optionClicked != null)
                    if (optionClicked.equalsIgnoreCase("gallery") || optionClicked.equalsIgnoreCase("attachment")) {
                        /*  openImagePicker();*/
                    } else {
                        ArrayList<String> attachmentPermission = new ArrayList<>();
                        if (selectedAttachment != null) {
                            try {
                                JSONArray jsonArray = new JSONArray(selectedAttachment);
                                int arrLength = jsonArray.length();
                                if (arrLength > 0) {
                                    for (int i = 0; i < arrLength; i++) {
                                        String item = (String) jsonArray.get(i);
                                        Log.d("TAG", "convertStringToJson: " + item + " : " + item.replace("attachment/", ""));
                                        attachmentPermission.add(item.replace("attachment/", ""));
                                        setupViewAttachmentClick(attachmentPermission.get(0));
                                    }
                                }
                            } catch (Exception e) {
                                Log.d("TAG", "onRequestPermissionsResult: " + e.getMessage());
                            }
                        }
                    }


            }
        } else if (requestCode == PERMISSION_ALL_NEW) {
            if (Utilities.verifyPermissions(grantResults)) {
                Log.d("VerifyResult", "onRequestPermissionsResult: ");
            }
        }

    }

    private void convertStringToJson(String attachment) {
        try {
            attachmentList.clear();
//            Log.d("TAG", "convertStringToJson: " + attachment);
            if (attachment != null) {
                JSONArray jsonArray = new JSONArray(attachment);
                int arrLength = jsonArray.length();
                if (arrLength > 0) {
                    for (int i = 0; i < arrLength; i++) {
                        String item = (String) jsonArray.get(i);
                        Log.d("TAG", "convertStringToJson: " + item + " : " + item.replace("attachment/", ""));
                        attachmentList.add(item.replace("attachment/", ""));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUpTeacherAdapter(attachmentList);
        // setupSpinner(attachmentList);
    }


    private void convertStringToJsonActivity(String attachment) {
        try {
            attachmentList.clear();
            Log.d("TAG", "convertStringToJson: " + attachment);
            JSONArray jsonArray = new JSONArray(attachment);
            int arrLength = jsonArray.length();
            if (arrLength > 0) {
                for (int i = 0; i < arrLength; i++) {
                    String item = (String) jsonArray.get(i);
                    if (item.contains("storage")) {
                        attachmentList.add(item);
                    } else {
                        attachmentList.add(item.replace("attachment/", ""));
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUpTeacherAdapter(attachmentList);
        // setupSpinner(attachmentList);
    }

    private void setUpTeacherAdapter(ArrayList<String> attachmentList) {
        teacherAttachmentsAdapter = new SelectedAttachmentItemsAdapter(attachmentList, true, position -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            optionClicked = "viewattachment";
            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                setupViewAttachmentClick(attachmentList.get(position).trim());
            } else {
                try {
                    setupViewAttachmentClick(attachmentList.get(position).trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, () -> {

        });
        teacherRecyclerView.setAdapter(teacherAttachmentsAdapter);


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void hitSubmitAssignmentTestService(List<String> imageArray) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", PreferenceHandler.readString(this, PreferenceHandler.TOKEN, ""));
        headers.put("deviceId", PreferenceHandler.readString(this, PreferenceHandler.DEVICE_ID, ""));
        Call<GetBatchSubmitAssignmentTestResponse> call = null;
        MultipartBody.Part[] multipartImage = new MultipartBody.Part[imageArray.size()];
        for (int index = 0; index < imageArray.size(); index++) {
            File file = new File(imageArray.get(index));

            Log.d("TAG", "hitSubmitAssignmentTestService: " + file.getAbsolutePath());
            RequestBody assignmentImgRequestBody = RequestBody.create(MediaType.parse("*/*"), file);/*getContentResolver().getType(uri)*/
            if (isFrom.equalsIgnoreCase("assignment")) {
                multipartImage[index] = MultipartBody.Part.createFormData("assignment[]", file.getName(), assignmentImgRequestBody);
            } else {
                multipartImage[index] = MultipartBody.Part.createFormData("test[]", file.getName(), assignmentImgRequestBody);
            }

        }

        Log.d("TAG", "hitSubmitAssignmentTestService: " + multipartImage.toString());
        String currentDate = getCurrentDateTime();
        if (isFrom.equalsIgnoreCase("assignment")) {
            call = apiInterface.submitBatchAssignment(headers, assignmentId, currentDate, multipartImage);
        } else {
            if (isFromBatch)
                call = apiInterface.submitBatchTest(headers, assignmentId, currentDate, multipartImage);
            else
                call = apiInterface.submitCourseTest(headers, assignmentId, multipartImage);
        }

        mProgressDialog = new ProgressView(this);
        mProgressDialog.show();
        call.enqueue(new Callback<GetBatchSubmitAssignmentTestResponse>() {
            @Override
            public void onResponse(Call<GetBatchSubmitAssignmentTestResponse> call, Response<GetBatchSubmitAssignmentTestResponse> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("200") && response.body().getSuccess().equalsIgnoreCase("true")) {
                        if (response.body().getResult() != null && response.body().getResult().equalsIgnoreCase("success")) {
                            if (isFrom.equalsIgnoreCase("assignment")) {
                                Snackbar.make(mainLayout, getString(R.string.assihnmet_submitted_success), Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(mainLayout, getString(R.string.test_submit_success), Snackbar.LENGTH_SHORT).show();
                            }
                            tvSubmitAnswer.setVisibility(View.GONE);
                            tvtestSubmitted.setVisibility(View.VISIBLE);
                            tvtestSubmitted.setText(R.string.txt_submitted);
                            tvtestSubmitted.setTextColor(ContextCompat.getColor(ListDetailActivity.this, R.color.green));
                            tvSubmitted.setText(R.string.txt_submitted);
                            tvSubmitted.setTextColor(ContextCompat.getColor(ListDetailActivity.this, R.color.green));
                            tvSubmitted.setVisibility(View.VISIBLE);
                            tvSubmitAnswer.setOnClickListener(null);
                            attachmentsRecyclerView.setVisibility(View.GONE);
                            addAttachmentLayout.setVisibility(View.GONE);
                            addAnswersLayout.setVisibility(View.GONE);
                            Intent intent = new Intent();
                            intent.putExtra(PreferenceHandler.IS_DATA_SUBMITTED, true);
                            intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                            setResult(AssignmentTestVideoListFragment.REQUEST_UPDATE_SUBMIT_STATUS, intent);

                        }
                    } else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("403")) {
                        Utilities.openUnauthorizedDialog(ListDetailActivity.this);
                    } else {
                        Utilities.makeToast(getApplicationContext(), getString(R.string.server_error));
                    }       /*else if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("402")) {
                        mProgressDialog.dismiss();
                    }*/
                } else
                    Utilities.makeToast(getApplicationContext(), getString(R.string.assignment_not_submitted));

            }

            @Override
            public void onFailure(Call<GetBatchSubmitAssignmentTestResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Utilities.makeToast(getApplicationContext(), getString(R.string.server_error));
            }
        });
    }

    private String getCurrentDateTime() {
        Date currentTime = Calendar.getInstance().getTime();
        return Utilities.formatCurrentDate(currentTime);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAG", "onRestart: ");
        if (PreferenceHandler.readBoolean(this, PreferenceHandler.EXTERNAL_APP, false)) {
            File filll = new File(PreferenceHandler.readString(this, PreferenceHandler.EXTERNAL_DOC, ""));
            if (filll.exists()) {
                if (!PreferenceHandler.readBoolean(this, PreferenceHandler.FILE_DOWNLOADED, false)) {
                    filll.delete();
                    PreferenceHandler.writeBoolean(this, PreferenceHandler.EXTERNAL_APP, false);
                    Log.d("TAG", "openFile: Deleted" + filll.getAbsolutePath());
                }
            } else {
                Log.d("TAG", "onRestart: Not Exist");
            }
        }
    }

}

